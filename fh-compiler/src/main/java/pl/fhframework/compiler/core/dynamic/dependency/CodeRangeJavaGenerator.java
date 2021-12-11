package pl.fhframework.compiler.core.dynamic.dependency;

import pl.fhframework.core.generator.CodeRange;
import pl.fhframework.core.generator.GenerationContext;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static pl.fhframework.compiler.core.generator.AbstractJavaCodeGenerator.toTypeLiteral;

/**
 * Timestamp map method generator
 */
public class CodeRangeJavaGenerator {

    public static final String XML_TIMESTAMPS_STATIC_METHOD = "geCodeRanges";

    /**
     * Gets XML file's modification timestamp
     */
    public static List<CodeRange> getCodeRanges(Class<?> aClass) {
        try {
            return ( List<CodeRange>) aClass.getMethod(XML_TIMESTAMPS_STATIC_METHOD).invoke(null);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public static GenerationContext generateStaticJavaMethod(GenerationContext methodSection, List<CodeRange> codeRanges) {
        GenerationContext methodBodySection = new GenerationContext();
        methodSection.addLine("public static %s<%s> %s() {", toTypeLiteral(List.class), toTypeLiteral(CodeRange.class), XML_TIMESTAMPS_STATIC_METHOD);
        methodSection.addSection(methodBodySection, 1);
        methodSection.addLine("}");

        methodBodySection.addLine("return %s.asList(", toTypeLiteral(Arrays.class));
        methodBodySection.addLine(codeRanges.stream().map(
                codeRange -> String.format("%s.of(\"%s\", \"%s\", %s, %s)", toTypeLiteral(CodeRange.class), codeRange.getName(), codeRange.getPath(), codeRange.getStart(), codeRange.getEnd())
        ).collect(Collectors.joining(",\n")));
        methodBodySection.addLine(");");

        return methodSection;
    }
}
