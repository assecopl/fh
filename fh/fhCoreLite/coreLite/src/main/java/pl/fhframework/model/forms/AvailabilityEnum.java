package pl.fhframework.model.forms;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by krzysztof.kobylarek on 2016-11-19.
 */
public enum AvailabilityEnum {

    EDIT ("Edit"),
    PREVIEW  ("Preview"),
    READONLY ("ReadOnly"),
    INVISIBLE ("Invisible"),
    SET_BY_PROGRAMMER ("SetByProgrammer"),
    VALUE ("AvailabilityValue"),
    AVAILABILITY ("Availability"),
    VARIANT ("Variant");

    private String tagName;

    AvailabilityEnum(String tagName) {
        this.tagName = tagName;
    }

    public static AvailabilityEnum getTag(String tagName){
        return Arrays.stream(AvailabilityEnum.values()).filter(
                tag-> Objects.equals(tag.getTagName(),tagName)).findFirst()
                .orElseThrow(()->new RuntimeException("Tag: "+tagName+" does not exist"));
    }

    public String getTagName(){
        return tagName;
    }
}
