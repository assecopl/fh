package pl.fhframework.core.dynamic;

import lombok.*;
import pl.fhframework.core.util.StringUtils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Dynamic class name
 */
@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
public class DynamicClassName {
    public static final String SUFFIX = "_(V(\\d+)|(Precompiled))";

    private static final Pattern LEGAL_JAVA_CLASS_NAME_PATTERN = Pattern.compile("^[_a-zA-Z].*$");

    /**
     * Package name (with dots)
     */
    private String packageName;

    /**
     * Base form class name
     */
    private String baseClassName;

    public static DynamicClassName forStaticBaseClass(Class<?> clazz) {
        return forClassName(clazz.getName());
    }

    public static DynamicClassName forClassName(String fullClassName) {
        int dotIndex = fullClassName.lastIndexOf('.');
        return forClassName(fullClassName.substring(0, Math.max(0, dotIndex)), fullClassName.substring(dotIndex + 1));
    }

    public static DynamicClassName forClassName(String packageName, String simpleClassName) {
        return new DynamicClassName(packageName, removeVersion(simpleClassName));
    }

    public static DynamicClassName forXmlFile(String relativeFilePath, String fileNameExtension) {
        // xml -> .xml
        fileNameExtension = "." + fileNameExtension;

        String className = Paths.get(relativeFilePath).getFileName().toString();
//        String className = FhResource.get(relativeFilePath).
        // strip file extension
        if (className.endsWith(fileNameExtension)) {
            className = className.substring(0, className.length() - fileNameExtension.length());
        }
        className = className.replace(".", "_");
        if (!LEGAL_JAVA_CLASS_NAME_PATTERN.matcher(className).find()) {
            className = "_" + className;
        }
        Path parentPath = Paths.get(relativeFilePath).getParent();
        String packageName;
        if (parentPath != null) {
            packageName = parentPath.toString().replace('\\', '.').replace('/', '.');
        } else {
            packageName = "";
        }
        return new DynamicClassName(packageName, className);
    }

    public static String removeVersion(String simpleClassName) {
        String[] parts = simpleClassName.split("\\$");

        if (parts.length > 2) {
            return simpleClassName;
        }

        Pattern p = Pattern.compile(SUFFIX);
        Matcher m = p.matcher(parts[0]);
        if (m.find()) {
            int pos = m.start();

            if (parts.length != 2) {
                return parts[0].substring(0, pos);
            }

            return parts[0].substring(0, pos) + "$" + parts[1];
        }

        return simpleClassName;
    }

    @Override
    public boolean equals(Object o) {
        DynamicClassName that = (DynamicClassName) o;
        return packageName.equals(that.packageName) && baseClassName.equals(that.baseClassName);
    }

    @Override
    public int hashCode() {
        return baseClassName.hashCode();
    }

    public String toFullClassName() {
        if (StringUtils.isNullOrEmpty(packageName)) {
            return baseClassName;
        } else {
            return packageName + "." + baseClassName;
        }
    }

    @Override
    public String toString() {
        return toFullClassName();
    }

    public DynamicClassName getOuterClassName() {
        String baseName = getBaseClassName();
        if (baseName.contains("$")) {
            String outerClassName = getOuterClassName(baseName);
            return DynamicClassName.forClassName(getPackageName(), outerClassName);
        } else {
            return this;
        }
    }

    public static String getOuterClassName(String className) {
        if (className.contains("$")) {
            return className.substring(0, className.indexOf('$'));
        } else {
            return className;
        }
    }

    public Optional<String> getInnerClassName() {
        String baseName = getBaseClassName();
        if (baseName.contains("$")) {
            return Optional.of(baseName.substring(baseName.indexOf('$') + 1, baseName.length()));
        } else {
            return Optional.empty();
        }
    }
}
