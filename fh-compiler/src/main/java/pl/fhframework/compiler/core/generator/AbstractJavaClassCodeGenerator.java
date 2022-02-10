package pl.fhframework.compiler.core.generator;

import lombok.Getter;
import pl.fhframework.compiler.core.dynamic.dependency.CodeRangeJavaGenerator;
import pl.fhframework.core.generator.GeneratedDynamicClass;
import pl.fhframework.core.generator.GenerationContext;
import pl.fhframework.core.util.StringUtils;

import java.util.*;

/**
 * Statefull abstract java class code generator.
 */
public abstract class AbstractJavaClassCodeGenerator extends AbstractJavaCodeGenerator { // non-spring class - it is also used in non-spring context

    protected String targetClassPackage;

    protected String targetClassName;

    protected String baseClassName;

    private Set<String> imports = new TreeSet<>();

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

    public AbstractJavaClassCodeGenerator(String targetClassPackage, String targetClassName, String baseClassName) {
        this.targetClassPackage = targetClassPackage;
        this.targetClassName = targetClassName;
        this.baseClassName = baseClassName;
    }

    public GenerationContext generateClassContext() {
        return generateClassContext(true, false);
    }

    public GenerationContext generateClassContext(boolean withConstructor, boolean withCodeRanges) {
        generateClassBody();

        GenerationContext classBody = new GenerationContext();

        // package + imports
        if (targetClassPackage != null) {
            classBody.addLine("package %s;", targetClassPackage);
            classBody.addLine();
        }
        classBody.addSection(importSection, 0);
        classBody.addLine();

        // class declaration
        classBody.addLine();
        if (targetClassPackage != null && baseClassName != null && addGeneratedDynamicClass()) {
            classBody.addLine("@%s(%s)", toTypeLiteral(GeneratedDynamicClass.class), toStringLiteral(targetClassPackage + "." + baseClassName));
        }
        classBody.addInlineSection(classSignatureSection, 0);
        classBody.addLine("{");

        // fields
        classBody.addSection(fieldSection, 1);

        if (withConstructor && !constructorSignatureSection.isEmpty()) {
            // constructor
            classBody.addLine();
            classBody.addInlineSection(constructorSignatureSection, 1);
            classBody.addLineWithIndent(1, "{");
            classBody.addSection(constructorSection, 2);
            classBody.addLineWithIndent(1, "}");
        }

        if (!methodSection.isEmpty()) {
            classBody.addLine();
            classBody.addSection(methodSection, 1);
        }

        withCodeRanges = withCodeRanges && !DynamicClassCompiler.isCoreLiteTarget();
        if (withCodeRanges) {
            classBody.addLine();
            codeRanges = classBody.registerDefferedSection();
        }

        classBody.addLine("}");

        // fill import section
        imports.forEach(imp -> importSection.addLine(imp));

        classBody.resolvePartially();

        if (withCodeRanges) {
            generateCodeRange(classBody);
        }

        return classBody;
    }

    protected boolean addGeneratedDynamicClass() {
        return !DynamicClassCompiler.isCoreLiteTarget();
    }

    public String generateClass() {
        return generateClass(true, false);
    }

    public String generateClass(boolean withConstructor, boolean withCodeRanges) {
        return generateClassContext(withConstructor, withCodeRanges).resolveCode();
    }

    private void generateCodeRange(GenerationContext classBody) {
        classBody.addDefferedSection(CodeRangeJavaGenerator.generateStaticJavaMethod(codeRanges, classBody.getResolvedRangesList()), 1);
    }

    public void importClass(String... classes) {
        Arrays.stream(classes).forEach(clazz -> {
            if (!clazz.startsWith("java.lang.")) {
                imports.add("import " + clazz + ";");
            }
        });
    }

    public void importClass(Class... classes) {
        Arrays.stream(classes).forEach(clazz -> {
            imports.add("import " + clazz.getName() + ";");
        });
    }

    public void importPackage(Class clazz) {
        imports.add("import " + clazz.getPackage().getName() + ".*;");
    }

    protected abstract void generateClassBody();

    protected String getBeanName() {
        return StringUtils.firstLetterToLower(baseClassName);
    }
}
