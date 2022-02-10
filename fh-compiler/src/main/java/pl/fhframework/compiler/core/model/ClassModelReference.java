package pl.fhframework.compiler.core.model;

import lombok.*;
import pl.fhframework.core.maps.features.GeometryType;
import pl.fhframework.model.forms.AccessibilityEnum;
import pl.fhframework.subsystems.Subsystem;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ClassModelReference {

    private static final String FILE_EXTENSTION = ".dmo";

    private String name;
    private String packageName;
    private Subsystem subsystem;
    private ClassType classType;
    private Boolean persisted;
    private String path;
    private String description;
    private Set<GeometryType> geometryTypes = new LinkedHashSet<>();
    private GeometryType geometryType;
    private String geoTypeDiscriminatorField;
    private String geoTypeDiscriminator;
    private boolean enumeration;
    private boolean xsdGenerated;

    public String getFullClassName() {
        return this.getPackageName() + "." + this.getName();
    }

    /**
     * This method is used in ModelsListForm.xml
     */
    public AccessibilityEnum getVisibleOrHidden() {
        if (ClassType.DYNAMIC.equals(this.classType)) {
            return AccessibilityEnum.EDIT;
        }
        return AccessibilityEnum.HIDDEN;
    }

}
