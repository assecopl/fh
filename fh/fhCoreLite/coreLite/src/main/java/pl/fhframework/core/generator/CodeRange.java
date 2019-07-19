package pl.fhframework.core.generator;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by pawel.ruta on 2018-02-27.
 */
@Getter
@Setter
public class CodeRange {
    public static final String DELIMITER = " / ";

    private String name;
    private String path;

    private int start;
    private int end;

    private CodeRange(String name, String path) {
        this.name = name;
        this.path = path;
    }

    public CodeRange(String name, String path, int start, int end) {
        this(name, path);
        this.start = start;
        this.end = end;
    }

    public boolean contains(int number) {
        return (number >= start && number <= end);
    }

    public static CodeRange of(String name, String path) {
        return new CodeRange(name, path);
    }

    public static CodeRange of(String name, String path, int start, int end) {
        return new CodeRange(name, path, start, end);
    }

    public static CodeRange of(CodeRange source) {
        return new CodeRange(source.name, source.path, source.start, source.end);
    }
}
