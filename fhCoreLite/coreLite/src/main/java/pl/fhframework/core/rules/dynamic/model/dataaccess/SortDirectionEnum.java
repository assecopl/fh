package pl.fhframework.core.rules.dynamic.model.dataaccess;

import lombok.Getter;

import java.util.Arrays;

/**
 * Created by pawel.ruta on 2017-08-24.
 */
public enum SortDirectionEnum {
    Asc("asc"),
    Desc("desc"),
    ;

    @Getter
    private String direction;

    SortDirectionEnum(String operator) {
        this.direction = operator;
    }

    public static SortDirectionEnum fromString(String operator) {
        return Arrays.stream(SortDirectionEnum.values()).filter(operatorEnum -> operatorEnum.getDirection().equals(operator)).findAny().orElse(null);
    }

    @Override
    public String toString() {
        return getDirection();
    }
}
