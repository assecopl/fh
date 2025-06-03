package pl.fhframework.compiler.core.tools;

import pl.fhframework.compiler.core.uc.service.UseCaseService;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.model.PresentationStyleEnum;
import pl.fhframework.validation.IValidationResults;

import java.util.Arrays;

/**
 * Created by pawel.ruta on 2017-04-26.
 */
public class JavaNamesUtils {
    private static final String javaKeywords[] = { "abstract", "assert", "boolean",
            "break", "byte", "case", "catch", "char", "class", "const",
            "continue", "default", "do", "double", "else", "enum", "extends", "false",
            "final", "finally", "float", "for", "goto", "if", "implements",
            "import", "instanceof", "int", "interface", "long", "native",
            "new", "null", "package", "private", "protected", "public",
            "return", "short", "static", "strictfp", "super", "switch",
            "synchronized", "this", "throw", "throws", "transient", "true",
            "try", "void", "volatile", "while" };

    public static String getFullName(String packageName, String className) {
        if (StringUtils.isNullOrEmpty(packageName)) {
            return className;
        }
        return packageName.concat(".").concat(className);
    }

    public static String normalizeClassName(String name) {
        return StringUtils.firstLetterToUpper(name);
    }

    public static String normalizeFieldName(String name) {
        return StringUtils.firstLetterToLower(name);
    }

    public static String normalizeMethodName(String name) {
        return StringUtils.firstLetterToLower(name);
    }

    public static String getClassName(String label) {
        return normalizeClassName(camelName(normalizeClassName(label)));
    }

    public static String getFieldName(String label) {
        return normalizeFieldName(camelName(normalizeFieldName(label)));
    }

    public static String getMethodName(String label) {
        return normalizeMethodName(camelName(normalizeMethodName(label)));
    }

    public static String camelName(String nazwa) {
        if (nazwa == null) {
            return null;
        }
        if (nazwa.matches(UseCaseService.variableNameMask)){
            return nazwa;
        }

        StringBuilder sb = new StringBuilder();
        boolean capNext = true;

        for (char c : nazwa.toCharArray()) {
            c = (capNext)
                    ? Character.toUpperCase(c)
                    : c;
            sb.append(c);
            capNext = String.valueOf(c).matches("[^(a-zA-Z0-9)]"); // explicit cast not needed
        }
        return sb.toString().replaceAll("[^(a-zA-Z0-9)]", "");
    }

    public static boolean isJavaKeyword(String keyword) {
        if (!StringUtils.isNullOrEmpty(keyword)) {
            return (Arrays.binarySearch(javaKeywords, keyword.trim().toLowerCase()) >= 0);
        }
        return false;
    }

    public static boolean validateName(String name, Object parent, String attributeName, IValidationResults validationResults) {
        return validateName(name, parent, attributeName, validationResults, String.format("'%s' is reserved keyword", name));
    }

    public static boolean validateName(String name, Object parent, String attributeName, IValidationResults validationResults, String message) {
        if (isJavaKeyword(name)) {
            validationResults.addCustomMessage(parent, attributeName, message,  PresentationStyleEnum.ERROR);
            return false;
        }
        return true;
    }
}
