package pl.fhframework.compiler.core.model.meta;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.fhframework.compiler.core.model.ClassModelReference;
import pl.fhframework.compiler.core.model.ClassType;
import pl.fhframework.aspects.snapshots.model.ISnapshotEnabled;
import pl.fhframework.aspects.snapshots.model.SkipSnapshot;
import pl.fhframework.core.maps.features.GeometryType;
import pl.fhframework.core.uc.dynamic.model.element.behaviour.Identifiable;
import pl.fhframework.core.util.StringUtils;

import javax.xml.bind.annotation.*;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@XmlRootElement(name = "Class")
@XmlAccessorType(XmlAccessType.FIELD)
@EqualsAndHashCode(of = "id", callSuper = false)
public class ClassTag implements ISnapshotEnabled, Identifiable {

    @XmlAttribute
    @XmlID
    private String id;

    @XmlAttribute
    private String name;

    @XmlAttribute
    private String description;

    @XmlAttribute
    private Boolean modifiedStatic;

    @XmlAttribute(name = "extends")
    private String parent;

    @XmlAttribute
    private Boolean persisted = Boolean.FALSE;

    @XmlAttribute
    private Boolean enumeration = Boolean.FALSE;

    @XmlTransient
    @SkipSnapshot
    private ClassModelReference reference;

    @XmlElement(name = "FeatureClass")
    private Set<String> featuresClass = new LinkedHashSet<>();

    @XmlElements({
            @XmlElement(name = "Attribute", type = AttributeTag.class)
    })
    @XmlElementWrapper(name = "Attributes")
    private List<AttributeTag> attributeTags = new ArrayList<>();

    @XmlElements({
            @XmlElement(name = "Relation", type = RelationTag.class)
    })
    @XmlElementWrapper(name = "Relations")
    private List<RelationTag> relationTags = new ArrayList<>();

    @XmlElement(name = "GeometryType")
    private Set<GeometryType> geometryTypes = new LinkedHashSet<>();

    @XmlAttribute
    private String geoTypeDiscriminatorField;

    @XmlAttribute
    private String geoTypeDiscriminator;

    @XmlElement(name = "XsdSchema")
    private String xsdSchema;

    @XmlElement(name = "XsdDefinition")
    private String xsdDefinition;

    @XmlElement(name = "XsdSubPackage")
    private String xsdSubPackage;

    @XmlElements({
            @XmlElement(name = "XsdSchema", type = String.class)
    })
    @XmlElementWrapper(name = "XsdSchemaPackage")
    private List<String> xsdSchemaPackage;

    @XmlElements({
            @XmlElement(name = "Value", type = String.class)
    })
    @XmlElementWrapper(name = "Constants")
    private List<String> constants;

    public ClassTag(ClassTag classTag) {
        this.id = classTag.getId();
        this.name = classTag.getName();
        this.description = classTag.getDescription();
        this.modifiedStatic = classTag.getModifiedStatic();
        this.parent = classTag.getParent();
        this.persisted = classTag.getPersisted();
        this.reference = classTag.getReference();

        classTag.getAttributeTags().forEach(x -> {
            attributeTags.add(x.copyOf());
        });
        classTag.getRelationTags().forEach(x -> {
            relationTags.add(x.copyOf());
        });

        this.geometryTypes.addAll(classTag.getGeometryTypes());
        this.geoTypeDiscriminator = classTag.getGeoTypeDiscriminator();
        this.geoTypeDiscriminatorField = classTag.getGeoTypeDiscriminatorField();
        featuresClass.addAll(classTag.getFeaturesClass());
    }

    public ClassTag copyOf() {
        return new ClassTag(this);
    }

    public void postLoad() {
        relationTags.stream().forEach(RelationTag::postLoad);
    }

    public boolean isStatic() {
        return reference == null || ClassType.STATIC.equals(reference.getClassType());
    }

    public boolean isDynamic() {
        return ClassType.DYNAMIC.equals(reference.getClassType()) && !isModifiedStatic();
    }

    public boolean isModifiedStatic() {
        return Boolean.TRUE.equals(getModifiedStatic());
    }

    /**
     * Get declared fields names of this class tag definition. Does not contains parent fields if
     * any.
     */
    public Set<String> getDefinedFields(boolean shouldGetFromTarget) {
        final Set<String> fieldNames = getAttributeTags().stream().map(AttributeTag::getName).collect(Collectors.toSet());
        if(shouldGetFromTarget) {
            fieldNames.addAll(getRelationTags().stream().map(RelationTag::getTargetRoleName).collect(Collectors.toSet()));
        } else {
            fieldNames.addAll(getRelationTags().stream().map(RelationTag::getSourceRoleName).collect(Collectors.toSet()));
        }
        return fieldNames;
    }

    public boolean isGeometryType(GeometryType geometryType) {
        return geometryTypes.size() == 1 && geometryTypes.contains(geometryType);
    }

    public boolean isGeometryType() {
        return geometryTypes.size() > 0;
    }

    public boolean isXsdSchema() {
        return !StringUtils.isNullOrEmpty(xsdSchema);
    }

    public boolean isEnumeration() {
        return Objects.equals(enumeration, Boolean.TRUE);
    }

    public boolean isModifiedStaticCode() {
        return isModifiedStatic() && (attributeTags.size() > 0 ||
                relationTags.stream().anyMatch(relationTag -> Objects.equals(Boolean.TRUE,relationTag.getBidirectional()) ||
                        !Objects.equals("OneToOne", relationTag.getMultiplicity()) || Objects.equals(getId(), relationTag.getSource())));
    }
}
