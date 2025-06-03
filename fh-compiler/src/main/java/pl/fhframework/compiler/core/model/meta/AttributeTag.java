package pl.fhframework.compiler.core.model.meta;

import lombok.*;
import pl.fhframework.aspects.snapshots.model.ISnapshotEnabled;
import pl.fhframework.aspects.snapshots.model.SkipSnapshot;
import pl.fhframework.core.uc.dynamic.model.element.attribute.TypeMultiplicityEnum;

import javax.xml.bind.annotation.*;

@Getter
@Setter
@XmlRootElement(name = "Attribute")
@XmlAccessorType(XmlAccessType.FIELD)
@EqualsAndHashCode(callSuper = false, of = {"name", "editable", "type"})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttributeTag implements ISnapshotEnabled, Cloneable {

    @XmlAttribute
    @XmlID
    private String name;

    @XmlAttribute
    private String type;

    @XmlAttribute
    private String description;

    @XmlAttribute
    private boolean primary; // maybe to remove

    @XmlAttribute
    private boolean mandatory;

    @XmlAttribute
    @Builder.Default
    private TypeMultiplicityEnum multiplicity = TypeMultiplicityEnum.Element;

    @XmlAttribute
    private String jsonProperty;

    @XmlTransient
    private boolean editable = true;

    public AttributeTag(AttributeTag attributeTag) {
        this.name = attributeTag.getName();
        this.type = attributeTag.getType();
        this.description = attributeTag.getDescription();
        this.primary = attributeTag.isPrimary();
        this.mandatory = attributeTag.isMandatory();
        this.editable = attributeTag.isEditable();
        this.multiplicity = attributeTag.getMultiplicity();
        this.jsonProperty = attributeTag.getJsonProperty();
    }

    @Override
    public Object clone() {
        return copyOf();
    }

    @SkipSnapshot
    public AttributeTag copyOf() {
        return new AttributeTag(this);
    }

    public boolean isNotEditable() {
        return !this.isEditable();
    }
}
