package pl.fhframework.docs.forms.model.example;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * Example enum for conversion example.
 */
public enum SizeEnum {
    SMALL("Small"),
    BIG("Big"),
    VERY_BIG("Very big");

    private static final Map<String, SizeEnum> enums;

    static {
        enums = new HashMap<>();
        for (SizeEnum v : SizeEnum.values()) {
            enums.put(v.id, v);
        }
    }

    public static SizeEnum findById(String id) {
        return enums.get(id);
    }

    @Getter
    private String id;

    SizeEnum(String id) {
        this.id = id;
    }
}
