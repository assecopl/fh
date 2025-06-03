package pl.fhframework.compiler.core.dynamic.dependency;

import pl.fhframework.core.FhFormException;
import pl.fhframework.core.dynamic.DynamicClassName;
import pl.fhframework.core.generator.GenerationContext;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static pl.fhframework.compiler.core.generator.AbstractJavaCodeGenerator.toStringLiteral;
import static pl.fhframework.compiler.core.generator.AbstractJavaCodeGenerator.toTypeLiteral;

/**
 * Timestamp map method generator
 */
public class DependencyTimestampJavaGenerator {

    public static final String XML_TIMESTAMPS_STATIC_METHOD = "getXmlTimestamps";

    /**
     * Gets XML file's modification timestamp
     */
    public static Map<DynamicClassName, Instant> getXmlTimestamps(Class<?> formClass) {
        try {
            return (Map<DynamicClassName, Instant>) formClass.getMethod(XML_TIMESTAMPS_STATIC_METHOD).invoke(null);
        } catch (Exception e) {
            throw new FhFormException(e); // should never happen
        }
    }

    public static GenerationContext generateStaticJavaMethod(Map<DynamicClassName, Instant> timestamps) {
        GenerationContext methodSection = new GenerationContext();
        GenerationContext methodBodySection = new GenerationContext();
        methodSection.addLine("public static %s<%s, %s> %s() {", toTypeLiteral(Map.class), toTypeLiteral(DynamicClassName.class),
                toTypeLiteral(Instant.class), XML_TIMESTAMPS_STATIC_METHOD);
        methodSection.addSection(methodBodySection, 1);
        methodSection.addLine("}");

        methodBodySection.addLine("%s<%s, %s> timestamps = new %s<>();", toTypeLiteral(Map.class),
                toTypeLiteral(DynamicClassName.class), toTypeLiteral(Instant.class), toTypeLiteral(HashMap.class));
        timestamps.forEach((className, timestamp) -> {
            if (timestamp != null) {
                methodBodySection.addLine("timestamps.put(%s.forClassName(%s), %s.ofEpochMilli(%sL));",
                        toTypeLiteral(DynamicClassName.class), toStringLiteral(className.toFullClassName()),
                        toTypeLiteral(Instant.class), Long.toString(timestamp.toEpochMilli()));
            } else {
                methodBodySection.addLine("timestamps.put(%s.forClassName(%s), null);",
                        toTypeLiteral(DynamicClassName.class), toStringLiteral(className.toFullClassName()));
            }
        });
        methodBodySection.addLine("return timestamps;");

        return methodSection;
    }
}
