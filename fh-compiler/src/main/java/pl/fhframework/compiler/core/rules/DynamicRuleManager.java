package pl.fhframework.compiler.core.rules;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import pl.fhframework.compiler.core.dynamic.AbstractDynamicClassAreaHandler;
import pl.fhframework.compiler.core.dynamic.DynamicClassArea;
import pl.fhframework.compiler.core.dynamic.DynamicClassFileDescriptor;
import pl.fhframework.compiler.core.dynamic.DynamicClassMetadata;
import pl.fhframework.compiler.core.generator.DynamicClassCompiler;
import pl.fhframework.compiler.core.generator.ICollapsePropertiesToMethodName;
import pl.fhframework.compiler.core.generator.RulesTypeProvider;
import pl.fhframework.compiler.core.rules.dynamic.generator.DynamicRuleCodeBuilder;
import pl.fhframework.compiler.core.rules.dynamic.generator.DynamicRulePlPgSqlBuilder;
import pl.fhframework.compiler.core.rules.dynamic.model.Rule;
import pl.fhframework.compiler.core.rules.meta.DruExtension;
import pl.fhframework.compiler.core.rules.service.RulesServiceExtImpl;
import pl.fhframework.compiler.core.dynamic.dependency.DependenciesContext;
import pl.fhframework.core.dynamic.DynamicClassName;
import pl.fhframework.core.generator.GenerationContext;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.core.rules.meta.RuleMetadataRegistry;
import pl.fhframework.core.uc.UseCaseBeanFactoryPostProcessor;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.subsystems.Subsystem;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by pawel.ruta on 2017-05-29.
 */
@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON) // must be a singleton
public class DynamicRuleManager extends AbstractDynamicClassAreaHandler<DynamicRuleMetadata> {
    public static final Class<?> RULE_HINT_TYPE = IRuleHintType.class;
    public static final String RULE_HINT_TYPE_NAME = "Rules...";

    @Autowired
    private RulesServiceExtImpl rulesService;

    @Autowired
    private UseCaseBeanFactoryPostProcessor postProcessor;

    @Autowired
    private DynamicClassCompiler dynamicClassCompiler;

    @Autowired
    private RulesTypeProvider rulesTypeProvider;

    private List<String> functionsToCreate = new LinkedList<>();

    private static interface IRuleHintType extends ICollapsePropertiesToMethodName {}

    public DynamicRuleManager() {
        super(DruExtension.RULE_FILENAME_EXTENSION, DynamicClassArea.RULE, false);
    }

    @Override
    public List<Class<?>> listAreaStaticClasses(Subsystem subsystem) {
        List<Class<?>> staticClasses = new LinkedList<>();

        staticClasses.addAll(RuleMetadataRegistry.INSTANCE.getValidationRules(subsystem));
        staticClasses.addAll(RuleMetadataRegistry.INSTANCE.getAccessibilityRules(subsystem));
        staticClasses.addAll(RuleMetadataRegistry.INSTANCE.getBusinessRules(subsystem));
        staticClasses.addAll(RuleMetadataRegistry.INSTANCE.getFilteringRules(subsystem));

        return staticClasses;
    }

    @Override
    public DynamicRuleMetadata readMetadata(DynamicClassFileDescriptor file) {
        DynamicRuleMetadata metadata = new DynamicRuleMetadata();
        DynamicClassName className = DynamicClassName.forXmlFile(file.getRelativePath(), DruExtension.RULE_FILENAME_EXTENSION);
        try {
            metadata.setRule(rulesService.readRule(file.getResource().getURL()));
            metadata.setDisplayName(metadata.getRule().getLabel());
            metadata.setDependencies(provideDependencies(metadata.getRule()));
        } catch (Exception e) {
            FhLogger.error(String.format("Error reading metada of '%s'", className.toFullClassName()), e);
            metadata.setRule(null);
        }
        metadata.setDynamicClassName(className);


        return metadata;
    }

    @Override
    public String generateClass(DynamicRuleMetadata metadata, String newClassPackage, String newClassName, GenerationContext xmlTimestampMethod, DependenciesContext dependenciesContext) {
        DynamicRuleCodeBuilder generator = new DynamicRuleCodeBuilder();
        generator.initialize(metadata.getRule(), newClassName, newClassPackage, metadata.getDynamicClassName().getBaseClassName(),
                xmlTimestampMethod, dependenciesContext);

        if (metadata.getRule().isPlpgsql()) {
            DynamicRulePlPgSqlBuilder generatorPlPgSql = new DynamicRulePlPgSqlBuilder();
            generatorPlPgSql.initialize(metadata.getRule(), newClassName, newClassPackage, metadata.getDynamicClassName().getBaseClassName(), dependenciesContext);
            String plpgSqlCode = generatorPlPgSql.generateClass();
            dynamicClassCompiler.createDynamicFile(plpgSqlCode, newClassPackage, newClassName, ".sql");
        }

        return generator.generateClass();
    }

    @Override
    public void postLoad(DynamicClassFileDescriptor xmlFile, Class<?> clazz, DynamicClassMetadata metadata) {
        super.postLoad( xmlFile, clazz, metadata);

        if (((DynamicRuleMetadata)metadata).getRule().isPlpgsql()) {
            String plpgSqlCode = dynamicClassCompiler.readDynamicFile(clazz.getPackage().getName(), clazz.getSimpleName(), ".sql");
            functionsToCreate.add(plpgSqlCode);
        }

        postProcessor.registerBean(clazz, StringUtils.decapitalize(metadata.getDynamicClassName().getBaseClassName()), true, true);

        RuleMetadataRegistry.INSTANCE.register(clazz, xmlFile.getSubsystem());
        RuleMetadataRegistry.INSTANCE.addCategories(((DynamicRuleMetadata) metadata).getRule().getCategories());
    }

    @Override
    public void postLoad() {
        super.postLoad();

        if (functionsToCreate.size() > 0 ) {
            rulesService.executeDdlFunctions(functionsToCreate.stream().collect(Collectors.joining("\n")));
            functionsToCreate.clear();
        }
    }

    @Override
    public void postRegisterDynamicClass(DynamicRuleMetadata metadata) {
        RuleMetadataRegistry.INSTANCE.addCategories(metadata.getRule().getCategories());
        rulesTypeProvider.refresh();
    }

    @Override
    public void postUnregisterDynamicClass(DynamicRuleMetadata metadata) {
        rulesTypeProvider.refresh();
    }

    @Override
    public void postUpdateDynamicClass(DynamicRuleMetadata metadata) {
        RuleMetadataRegistry.INSTANCE.addCategories(metadata.getRule().getCategories());
        rulesTypeProvider.refresh();
    }

    private Set<DynamicClassName> provideDependencies(Rule rule) {
        return  rulesService.provideDependencies(rule);
    }
}
