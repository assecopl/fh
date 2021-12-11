package pl.fhframework.compiler.core.generator.model.data;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.fhframework.compiler.core.generator.model.MetaModel;
import pl.fhframework.compiler.core.model.meta.ClassTag;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class ClassMm extends MetaModel {
    @JsonIgnore
    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PROTECTED)
    private ClassTag classTag;

    public ClassMm(ClassTag classTag, List<String> dependencies) {
        setDependencies(dependencies);
        this.classTag = classTag;
    }

    @JsonGetter
    public String getId() {
        return classTag.getId();
    }

    @JsonGetter
    public boolean isEnumeration() {
        return classTag.isEnumeration();
    }

    @JsonGetter
    public List<String> getConstants() {
        return classTag.getConstants();
    }

    @JsonGetter
    public String getParent() {
        return classTag.getParent();
    }

    @JsonGetter
    public boolean isPersistable() {
        return Boolean.TRUE.equals(classTag.getPersisted());
    }

    @JsonGetter
    public boolean isSpatial() {
        return classTag.isGeometryType();
    }

    @JsonGetter
    public boolean isServerClassExtension() {
        return classTag.isModifiedStatic();
    }

    @JsonGetter
    public List<AttributeMm> getAttributes() {
        return classTag.getAttributeTags().stream().map(AttributeMm::new).collect(Collectors.toList());
    }

    @JsonGetter
    public List<RelationMm> getRelations() {
        return classTag.getRelationTags().stream()
                .filter(rel -> Objects.equals(rel.getSource(), getId()) || Boolean.TRUE.equals(rel.getBidirectional()))
                .map(tag -> new RelationMm(tag, this)).collect(Collectors.toList());
    }

    @Override
    public <T> T provideImpl() {
        return (T) classTag;
    }
}
