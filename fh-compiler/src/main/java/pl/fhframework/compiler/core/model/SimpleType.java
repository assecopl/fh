package pl.fhframework.compiler.core.model;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public enum SimpleType {

    SHORT("Short"), INTEGER("Integer"), LONG("Long"), FLOAT("Float"), DOUBLE("Double"), BIG_DECIMAL("BigDecimal"),
    BOOLEAN("Boolean"), DATE("Date"), TIMESTAMP("Timestamp"), STRING("String"), RESOURCE("Resource"), DATETIME("LocalDateTime");

    private String type;

    SimpleType(String type) {
        this.type = type;
    }

    public static List<String> getAllTypes() {
        return Arrays.asList(values()).stream()
                .map(SimpleType::getType)
                .collect(Collectors.toList());
    }

    public static boolean isSimpleType(String typeName) {
        for (SimpleType simpleType : values()) {
            if (simpleType.getType().equals(typeName)) {
                return true;
            }
        }
        return false;
    }

}
