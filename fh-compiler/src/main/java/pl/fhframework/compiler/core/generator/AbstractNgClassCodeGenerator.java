package pl.fhframework.compiler.core.generator;

import lombok.Getter;
import pl.fhframework.compiler.core.dynamic.DynamicClassArea;
import pl.fhframework.compiler.core.generator.model.ExpressionMm;
import pl.fhframework.compiler.core.generator.model.MetaModel;
import pl.fhframework.compiler.core.generator.model.ParameterMm;
import pl.fhframework.compiler.core.generator.model.usecase.WithExpression;
import pl.fhframework.compiler.core.generator.ts.TsDependency;
import pl.fhframework.compiler.core.model.generator.DynamicModelClassJavaGenerator;
import pl.fhframework.compiler.core.tools.JavaNamesUtils;
import pl.fhframework.compiler.core.uc.dynamic.model.VariableType;
import pl.fhframework.compiler.core.uc.dynamic.model.element.attribute.ParameterDefinition;
import pl.fhframework.compiler.core.uc.ts.generator.AngularCore;
import pl.fhframework.ReflectionUtils;
import pl.fhframework.core.dynamic.DynamicClassName;
import pl.fhframework.core.generator.GenerationContext;
import pl.fhframework.core.logging.FhLogger;

import java.io.File;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static pl.fhframework.compiler.forms.FormsManager.FORM_INTERNAL_MODEL_CLASS_NAME;

public abstract class AbstractNgClassCodeGenerator extends AbstractTypescriptCodeGenerator {

    @Getter
    protected GenerationContext classSignatureSection = new GenerationContext();
    @Getter
    protected GenerationContext importSection = new GenerationContext();
    @Getter
    protected GenerationContext methodSection = new GenerationContext();
    @Getter
    protected GenerationContext constructorSignatureSection = new GenerationContext();
    @Getter
    protected GenerationContext constructorSection = new GenerationContext();
    @Getter
    protected GenerationContext fieldSection = new GenerationContext();
    @Getter
    protected GenerationContext codeRanges;
    @Getter
    protected GenerationContext namespaceSection = new GenerationContext();
    @Getter
    protected GenerationContext globalSection = new GenerationContext();
    @Getter
    protected GenerationContext afterClassOpenSection = new GenerationContext();
    @Getter
    protected GenerationContext beforeClassCloseSection = new GenerationContext();
    @Getter
    private Dependency classDependency;
    @Getter
    private TsDependency classTsDependency;
    @Getter
    private MetaModel metaModel;

    private Set<TsDependency> tsImports = new HashSet<>();

    private Map<Object, String> namesMap = new HashMap<>();

    public AbstractNgClassCodeGenerator(ModuleMetaModel moduleMetaModel, Dependency classDependency, MetaModel metaModel, MetaModelService metaModelService) {
        super(moduleMetaModel, metaModelService);
        this.classDependency = classDependency;
        this.classTsDependency = classDependency == null ? null : getTsDependency(classDependency);
        this.metaModel = metaModel;
    }

    protected String getType(ParameterDefinition type) {
        if (type.isCollection() || type.isPageable()) {
            // todo: pageable
            return String.format("Array<%s>", getElementType(type));
        }
        return getElementType(type);
    }

    protected String getType(String fullQType) {
        return getTsDependency(fullQType).getName();
    }

    private String getElementType(ParameterDefinition param) {
        Optional<Type> simpleType = getSimpleType(param);
        if (simpleType.isPresent()) {
            return SIMPLETYPE_TO_TS_TYPE.get(simpleType.get());
        }
        if (isSpecialType(param)) {
            return SPECIALTYPE_TO_TS_TYPE.get(param.getType());
        }

        DynamicClassName className = DynamicClassName.forClassName(param.getType());
        Optional<String> innerClass = className.getInnerClassName();
        if (innerClass.isPresent() && Objects.equals(innerClass.get(), FORM_INTERNAL_MODEL_CLASS_NAME)) {
            if (moduleMetaModel.getMetadataType(moduleMetaModel.getMetadata(className.getOuterClassName().toFullClassName())).get() == DynamicClassArea.FORM) {
                className = DynamicClassName.forClassName(className.getOuterClassName() + "$" + "Model");
            }
        }
        return className.getBaseClassName().replace("$", ".");
    }

    private Optional<Type> getSimpleType(ParameterDefinition param) {
        Type simpleType = DynamicModelClassJavaGenerator.TYPE_MAPPER.get(param.getBaseTypeName());
        simpleType = ReflectionUtils.mapWrapperToPrimitive(ReflectionUtils.getRawClass(simpleType));
        return Optional.ofNullable(simpleType);
    }

    protected boolean isSimpleType(ParameterDefinition param) {
        return getSimpleType(param).isPresent();
    }

    protected boolean isSpecialType(ParameterDefinition param) {
        return SPECIALTYPE_TO_TS_TYPE.containsKey(param.getType());
    }

    public String generateClass() {
        return generateClass(true);
    }

    public String generateClass(boolean withConstructor) {
        return generateClassContext(withConstructor).resolveCode();
    }

    public GenerationContext generateClassContext(boolean withConstructor) {
        GenerationContext tsBody = new GenerationContext();

        importDependencies();

        generateClassBody();

        // fill import section
        generateImports();

        if (!importSection.isEmpty()) {
            tsBody.addInlineSection(importSection, 0);
        }

        if (!classSignatureSection.isEmpty()) {
            tsBody.addLineIfNeeded();
            tsBody.addInlineSection(classSignatureSection, 0);
            tsBody.addLine("{");
            tsBody.markLineNotNeeded();
        }
        if (!afterClassOpenSection.isEmpty()) {
            tsBody.addLineIfNeeded();
            tsBody.addInlineSection(afterClassOpenSection, 1);
        }
        // fields
        if (!fieldSection.isEmpty()) {
            tsBody.addLineIfNeeded();
            tsBody.addInlineSection(fieldSection, 1);
        }
        if (withConstructor) {
            if (!constructorSignatureSection.isEmpty()) {
                tsBody.addLineIfNeeded();
                // constructor
                tsBody.addInlineSection(constructorSignatureSection, 1);
                tsBody.addLineWithIndent(1, "{");
                if (!constructorSection.isEmpty()) {
                    tsBody.addSectionWithoutLine(constructorSection, 2);
                }
                tsBody.addLineWithIndent(1, "}");
            }
        }

        if (!methodSection.isEmpty()) {
            tsBody.addLineIfNeeded();
            tsBody.addSectionWithoutLine(methodSection, 1);
        }

        if (!beforeClassCloseSection.isEmpty()) {
            tsBody.addLineIfNeeded();
            tsBody.addSectionWithoutLine(beforeClassCloseSection, 1);
        }

        if (!classSignatureSection.isEmpty()) {
            tsBody.addLine("}");
        }

        // fill namespace
        if (getNamespaceName().isPresent()) {
            tsBody.addLineIfNeeded();
            tsBody.addLine("export namespace %s {", getNamespaceName().get());
            tsBody.addSectionWithoutLine(namespaceSection, 1);
            tsBody.addLine("}");
        }

        if (!globalSection.isEmpty()) {
            tsBody.addInlineSection(globalSection, 0);
        }

        tsBody.resolvePartially();

        return tsBody;
    }

    protected void importDependencies() {
        if (metaModel != null) {
            metaModel.getDependencies().forEach(this::addImport);
        }
    }

    protected void generateImports() {
        Map<String, List<TsDependency>> dependencies = tsImports.stream().collect(Collectors.groupingBy(TsDependency::getFrom));

        dependencies.forEach((from, deps) -> {
            importSection.addLine(String.format("import {%s} from \"%s\";",
                    deps.stream().map(TsDependency::getName).collect(Collectors.joining(", ")),
                    from));
        });
    }

    protected abstract void generateClassBody();

    protected String getTypeLiterals(List<ParameterDefinition> parameterDefinitions) {
        return parameterDefinitions.stream().map(this::getType).collect(Collectors.joining(", "));
    }

    protected String getArgumentsSignature(List<ParameterDefinition> parameterDefinitions) {
        return getArgumentsSignature(parameterDefinitions, Function.identity(), Function.identity());
    }

    protected String getArgumentsSignature(List<ParameterDefinition> parameterDefinitions, Function<String, String> paramNameConverter, Function<String, String> paramTypeConverter) {
        StringBuilder parameters = new StringBuilder();
        parameterDefinitions.forEach(param -> {
            String paramName = JavaNamesUtils.getFieldName(param.getName());
            parameters.append(String.format(", %s: %s", paramNameConverter.apply(paramName), paramTypeConverter.apply(getType(param))));
        });
        if (parameters.length() >= 2) {
            parameters.delete(0, 2);
        }
        return parameters.toString();
    }

    protected String getArguments(List<ParameterDefinition> parameterDefinitions) {
        return parameterDefinitions.stream().map(ParameterDefinition::getName).collect(Collectors.joining(", "));
    }

    protected String getParameters(List<ParameterMm> parameters) {
        return parameters.stream().map(parameter -> isExpression(parameter.getValue()) ? parameter.getValue().getExpression() : parameter.getName()).collect(Collectors.joining(", "));
    }


    protected String calculateArguments(List<ParameterMm> parameters, WithExpression element) {
        return calculateArguments(parameters, element, Function.identity());
    }

    protected String calculateArguments(List<ParameterMm> parameters, WithExpression element, Function<ExpressionContext, ExpressionContext> modifier) {
        // todo: spel
        StringBuilder params = new StringBuilder();
        parameters.forEach(parameter -> {
            ExpressionMm expression = isExpression(parameter.getValue()) ? parameter.getValue() : new ExpressionMm(parameter.getName());
            params.append(", ").append(expresionConverterWith(modifier).convertExpression(expression, element, metaModel));
        });

        if (params.length() > 0) {
            return params.substring(2);
        }

        return params.toString();
    }

    protected Optional<String> getNamespaceName() {
        return Optional.empty();
    }

    protected void generateMethodSignature(GenerationContext methodSection, String name, List<ParameterDefinition> parameterDefinitions, ParameterDefinition returnType) {
        generateMethodSignature(methodSection, name, parameterDefinitions, returnType, false);
    }

    protected void generateMethodSignature(GenerationContext methodSection, String name, List<ParameterDefinition> parameterDefinitions, ParameterDefinition returnType, boolean forInterface) {
        generateMethodSignature(methodSection, name, parameterDefinitions, returnType, forInterface, false);
    }

    protected void generateMethodSignature(GenerationContext methodSection, String name, List<ParameterDefinition> parameterDefinitions, ParameterDefinition returnType, boolean forInterface, boolean async) {
        String returnTypeStr = async ? "" : ": void";
        String asyncPrefix = async ? "async " : "";

        if (returnType != null) {
            if (async) {
                returnTypeStr = ": Promise<" + getType(returnType) + ">";
            }
            else {
                returnTypeStr = ": " + getType(returnType);
            }
        }

        parameterDefinitions.forEach(this::addImport);
        String parameters = getArgumentsSignature(parameterDefinitions);
        if (forInterface) {
            methodSection.addLine("%s%s(%s)%s;", asyncPrefix, name, parameters, returnTypeStr);
        } else {
            methodSection.addLine("%s%s(%s)%s {", asyncPrefix, name, parameters, returnTypeStr);
        }
    }

    protected TsDependency getTsDependency(Dependency dependency) {
        if (dependency.isCore()) {
            // todo:
            return TsDependency.builder().name(getBaseName(dependency.getName())).appName("fh").module("ng-core").build();
        } else {
            if (dependency.getModule() != classDependency.getModule()) {
                return TsDependency.builder().
                        name(getBaseName(dependency.getName())).
                        appName(moduleMetaModel.getAppName()).module(dependency.getModule().getName()).build();
            } else {
                //local
                String withinBase = DynamicClassName.forClassName(classDependency.getName()).getPackageName();
                String dependencyFull = DynamicClassName.forClassName(dependency.getName()).toFullClassName();
                Path relative = Paths.get(withinBase.replace(".", File.separator)).
                        relativize(Paths.get(dependencyFull.replace(".", File.separator)));
                String relativeStr = relative.toString();
                if (!Objects.equals(File.separator, "/")) {
                    relativeStr = relativeStr.replace(File.separator, "/");
                }
                if (!relativeStr.startsWith("..")) {
                    relativeStr = "./" + relativeStr;
                }
                return TsDependency.builder().
                        name(getBaseName(dependency.getName())).
                        filePath(postfixPath(relativeStr, dependency)).build();
            }

        }
    }

    protected TsDependency getTsDependency(String fullName) {
        Dependency dependency = moduleMetaModel.getDependency(fullName);
        return getTsDependency(dependency);
    }

    protected void addImport(Dependency dependency) {
        if (dependency.isCore()) {
            addImport(getTsDependency(dependency));
        } else if (dependency.isProvided()) {
            FhLogger.warn(String.format("Dependency '%s' should have stub/proxy in TypeScript", dependency.getName()));
            // todo: uncomment when proxy done
            //throw new FhException(String.format("Dependency '%s' should have stub/proxy in TypeScript", dependency.getName()));
            addImport(TsDependency.builder().appName(moduleMetaModel.getAppName()).module(dependency.getModule().getName()).name(getBaseName(dependency.getName())).build());
        } else {
            // local import
            // or from other module import ("@[app]/[module]
            addImport(getTsDependency(dependency));
        }
    }

    protected void addImport(TsDependency dependency) {
        tsImports.add(dependency);
    }

    protected void addImport(ParameterDefinition type) {
        if (!getSimpleType(type).isPresent() && !isSpecialType(type)) {
            addImport(moduleMetaModel.getDependency(type.getType()));
        }
    }

    protected void addImports(AbstractNgClassCodeGenerator other) {
        tsImports.addAll(other.tsImports);
    }

    protected void addImport(String type) {
        addImport(moduleMetaModel.getDependency(type));
    }

    protected void addImport(Type type) {
        addImport(VariableType.of(type).asParameterDefinition());
    }

    protected String getMethodName(String label) {
        return JavaNamesUtils.getMethodName(label);
    }

    protected String getFieldName(String label) {
        return JavaNamesUtils.getFieldName(label);
    }

    protected String reserveName(Object obj, String name) {
        String newName = reserveFieldName(name, "");
        namesMap.put(obj, newName);
        return newName;
    }

    protected String convertExpression(ExpressionMm fhel, WithExpression element) {
        return convertExpression(fhel, element, metaModel);
    }

    protected Type getExpressionType(String fhel, WithExpression element) {
        return getExpressionType(fhel, element, metaModel);
    }

    public String getReservedName(Object obj) {
        return namesMap.get(obj);
    }

    protected void makeInjectable() {
        /* @Injectable({
              providedIn: 'root'
            })
         */
        addImport(AngularCore.Injectable);
        classSignatureSection.addLine("@%s({", AngularCore.Injectable.getName());
        classSignatureSection.addLineWithIndent(1, "providedIn: 'root'");
        classSignatureSection.addLine("})");
    }

    protected String provideInjection(List<String> dependencies, DynamicClassArea... types) {
        List<DynamicClassArea> areas = Arrays.asList(types);
        return dependencies.stream().map(moduleMetaModel::getDependency).
                filter(dependency -> areas.contains(dependency.getType())).
                map(dependency -> {
                    String baseName = getBaseName(dependency.getName());
                    return String.format("private %s: %s", JavaNamesUtils.getFieldName(baseName), baseName);
                }).collect(Collectors.joining(", "));
    }
}
