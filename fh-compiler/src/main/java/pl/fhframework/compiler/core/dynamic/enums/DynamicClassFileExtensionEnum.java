/**
 * *********************************************************
 * Author: krzysztof.kozlowski2
 * Created: 2017-10-13
 * **********************************************************
 */
package pl.fhframework.compiler.core.dynamic.enums;

import lombok.Getter;
import pl.fhframework.core.FhException;
import pl.fhframework.compiler.core.dynamic.DynamicClassArea;

public enum DynamicClassFileExtensionEnum {

    FORM_EXTENSION("frm", DynamicClassArea.FORM),
    USE_CASE_EXTENSION("duc", DynamicClassArea.USE_CASE),
    RULE_EXTENSION("dru", DynamicClassArea.RULE),
    SERVICE_EXTENSION("srv", DynamicClassArea.SERVICE),
    MODEL_EXTENSION("dmo", DynamicClassArea.MODEL),
    JR_REPORT_EXTENSION("jrxml", DynamicClassArea.JR_REPORT);

    @Getter
    private String fileExtension;
    @Getter
    private DynamicClassArea dynamicClassArea;

    DynamicClassFileExtensionEnum(String fileExtension, DynamicClassArea dynamicClassArea) {
        this.fileExtension = fileExtension;
        this.dynamicClassArea = dynamicClassArea;
    }

    public static DynamicClassFileExtensionEnum getByFileExtension(String fileExtension) {
        for (DynamicClassFileExtensionEnum extensionEnum : DynamicClassFileExtensionEnum.values()) {
            if (extensionEnum.getFileExtension().equals(fileExtension)) {
                return extensionEnum;
            }
        }
        throw new FhException("Unknown file extension: " + fileExtension);
    }

    public static DynamicClassFileExtensionEnum getByDynamicClassArea(DynamicClassArea classArea) {
        for (DynamicClassFileExtensionEnum extensionEnum : DynamicClassFileExtensionEnum.values()) {
            if (extensionEnum.getDynamicClassArea().equals(classArea)) {
                return extensionEnum;
            }
        }
        throw new FhException("Unknown class area: " + classArea.name());
    }
}
