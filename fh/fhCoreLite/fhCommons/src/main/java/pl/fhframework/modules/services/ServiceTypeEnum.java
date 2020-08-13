package pl.fhframework.modules.services;

import lombok.Getter;

import javax.xml.bind.annotation.XmlEnumValue;
import java.util.Arrays;
import java.util.Objects;

/**
 * Created by pawel.ruta on 2018-04-13.
 */
public enum ServiceTypeEnum {
    @XmlEnumValue("dynamicService")
    DynamicService("dynamicService"),
    @XmlEnumValue("restClient")
    RestClient("restClient"),
    @XmlEnumValue("restService")
    RestService("restService"),
    ;

    @Getter
    private String typeId;

    ServiceTypeEnum(String typeId) {
        this.typeId = typeId;
    }

    public String value() {
        return typeId;
    }

    public static ServiceTypeEnum fromValue(String value) {
        return Arrays.stream(values()).filter(serviceTypeEnum -> Objects.equals(serviceTypeEnum.typeId, value)).findAny().get();
    }

    public boolean isRest() {
        return this == RestClient || this == RestService;
    }
}
