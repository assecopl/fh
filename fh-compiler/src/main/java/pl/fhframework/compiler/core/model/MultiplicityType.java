package pl.fhframework.compiler.core.model;

import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum MultiplicityType {
    ONE("1"), MULTIPLE("n");

    @Getter
    private String type;

    MultiplicityType(String type) {
        this.type = type;
    }

    public static List<String> getAllTypes() {
        return Stream.of(MultiplicityType.values()).map(MultiplicityType::getType).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return getType();
    }
}
