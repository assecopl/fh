package pl.fhframework.compiler.core.services.dynamic.generator;

import pl.fhframework.compiler.core.generator.JavaCodeGeneratorFactory;
import pl.fhframework.compiler.core.dynamic.dependency.DependenciesContext;
import pl.fhframework.compiler.core.rules.dynamic.generator.BlocklyFhGenerator;
import pl.fhframework.compiler.core.rules.dynamic.generator.DynamicRuleCodeBuilder;
import pl.fhframework.compiler.core.services.dynamic.model.Operation;
import pl.fhframework.compiler.core.services.dynamic.model.Service;
import pl.fhframework.compiler.core.uc.dynamic.model.element.Permission;
import pl.fhframework.core.generator.GenerationContext;
import pl.fhframework.core.services.FhService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FhServiceCodeBuilder extends DynamicRuleCodeBuilder implements IServiceCodeGenerator {
    private Service service;

    public FhServiceCodeBuilder() {
        super();
    }

    public void initialize(Service service, String newClassName, String newClassPackage, String newBaseClassName, GenerationContext xmlTimestampMethod, DependenciesContext dependenciesContext) {
        super.initialize(null, newClassName, newClassPackage, newBaseClassName, xmlTimestampMethod, dependenciesContext);

        this.service = service;
    }

    @Override
    protected void generateClassBody() {
        generateBeanClass(service);

        generateRuleServiceAccess();

        addI18nService();

        addFhServiceAccess();

        addStoreAccessService();

        GenerationContext mainMethodSection = methodSection;
        for (Operation operation : service.getOperations()) {
            methodSection = mainMethodSection.registerDefferedSection();
            mainMethodSection.addDefferedSection(methodSection, 0);
            mainMethodSection.addLine();

            methodSection.startRange(String.format("operation '%s'", operation.getRule().getLabel()), operation.getRule().getId());
            generatePermissionLine(methodSection, operation.getRule().getPermissions());

            IServiceCodeGenerator serviceCodeGenerator = (IServiceCodeGenerator) JavaCodeGeneratorFactory.getCodeGenerator(service.getServiceType());

            serviceCodeGenerator.addServices(fieldSection, dependenciesContext, fieldsBean);

            serviceCodeGenerator.generateOperationAnnotations(operation, service, methodSection, dependenciesContext);
            serviceCodeGenerator.generateOperationSignature(operation, service, methodSection, dependenciesContext);
            serviceCodeGenerator.generateOperationBody(operation, service, methodSection, dependenciesContext);
            methodSection.addLine("}");
            methodSection.addLine();

            methodSection.endRange();
        }
        methodSection = mainMethodSection;

        // add static method returning XML timestamps
        if (xmlTimestampMethod != null) {
            methodSection.addSection(xmlTimestampMethod, 0);
        }
    }

    private void generateBeanClass(Service service) {
        classSignatureSection.addLine("@%s(modifiable = true, value = \"%s\", categories={%s})",
                toTypeLiteral(FhService.class), getBeanName(),
                service.getCategories().stream().map(cat -> "\"" + cat + "\"").collect(Collectors.joining(", ")));

        IServiceCodeGenerator serviceCodeGenerator = (IServiceCodeGenerator) JavaCodeGeneratorFactory.getCodeGenerator(service.getServiceType());
        serviceCodeGenerator.generateClassAnnotations(service, classSignatureSection, dependenciesContext);

        List<Permission> permissionList = new ArrayList<>();
        service.getOperations().forEach(operation -> permissionList.addAll(operation.getRule().getPermissions()));
        if (service.getOperations().stream().anyMatch(operation -> operation.getRule().getPermissions().isEmpty())) {
            permissionList.clear();
        }
        generatePermissionLine(classSignatureSection, permissionList);

        classSignatureSection.addLine("public class %s", this.targetClassName);
        constructorSignatureSection.addLine("public %s()", targetClassName);
    }

    @Override
    public void generateOperationSignature(Operation operation, Service service, GenerationContext methodSection, DependenciesContext dependenciesContext) {
        initialize(operation.getRule(), null, null, null, null, dependenciesContext);

        this.methodSection = methodSection;

        generateRuleMethodSignature(operation.getRule(), methodSection);
    }

    @Override
    public void generateOperationBody(Operation operation, Service service, GenerationContext methodSection, DependenciesContext dependenciesContext) {
        super.initialize(operation.getRule(), null, null, null, null, dependenciesContext);

        this.methodSection = methodSection;

        if (operation.getRule().getRuleDefinition() != null) {
            this.methodSection.addSectionWithoutLine(new BlocklyFhGenerator(operation.getRule(), dependenciesContext).generateCode(), 0);
        }
    }
}
