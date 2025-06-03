package pl.fhframework.compiler.core.model;

import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum RelationType {
    ONE_TO_ONE("OneToOne"), ONE_TO_MANY("OneToMany"),
    MANY_TO_ONE("ManyToOne"), MANY_TO_MANY("ManyToMany");

    private static final String NOT_SUPPORTED_MESSAGE = "Not supported type";

    @Getter
    private String type;

    RelationType(String type) {
        this.type = type;
    }

    public static List<String> getAllTypes() {
        return Stream.of(RelationType.values()).map(RelationType::getType).collect(Collectors.toList());
    }

    public static RelationType getBy(String possibleType) {
        switch(possibleType) {
            case "OneToOne": return ONE_TO_ONE;
            case "ManyToMany": return MANY_TO_MANY;
            case "OneToMany": return ONE_TO_MANY;
            case "ManyToOne": return MANY_TO_ONE;
            default: return null;
        }
    }

    @Override
    public String toString() {
        return getType();
    }
}
