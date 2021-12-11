package pl.fhframework.compiler.core.model;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum ClassType {

    STATIC("Static"), DYNAMIC("Dynamic"), VIEW("View");

    private final String type;

    ClassType(String type) {
        this.type = type;
    }

    public static List<String> getAllTypes() {
        return Stream.of(ClassType.values()).map(ClassType::getType).collect(Collectors.toList());
    }

    public String getType() {
        return type;
    }
}
