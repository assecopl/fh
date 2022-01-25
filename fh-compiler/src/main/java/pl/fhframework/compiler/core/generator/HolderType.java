package pl.fhframework.compiler.core.generator;

import lombok.AllArgsConstructor;

import java.lang.reflect.Type;

@AllArgsConstructor
public class HolderType implements Type {
    private String name;

    @Override
    public String getTypeName() {
        return name;
    }
}

