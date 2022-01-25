package pl.fhframework.compiler.core.generator.model;

public interface Wrapper {
    <T> T provideImpl();
}
