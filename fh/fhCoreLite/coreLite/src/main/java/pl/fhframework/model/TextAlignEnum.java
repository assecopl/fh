package pl.fhframework.model;

import lombok.Getter;

public enum TextAlignEnum {
    LEFT("LEFT"),
    RIGHT("RIGHT");

    TextAlignEnum(String align){
        this.align = align;
    }

    @Getter
    private String align;
}
