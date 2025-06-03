package pl.fhframework.compiler.core.generator.model.data;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.fhframework.compiler.core.model.RelationType;
import pl.fhframework.compiler.core.model.meta.RelationTag;
import pl.fhframework.core.uc.dynamic.model.element.attribute.TypeMultiplicityEnum;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class RelationMm {
    @JsonIgnore
    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PROTECTED)
    private RelationTag relation;
    @JsonIgnore
    private ClassMm parent;

    public RelationMm(RelationTag relation, ClassMm parent) {
        this.relation = relation;
        this.parent = parent;
    }

    @JsonGetter
    public String getName() {
        if (isOwner()) {
            return relation.getTargetRoleName();
        }
        return relation.getSourceRoleName();
    }

    @JsonGetter
    public String getType() {
        if (isOwner()) {
            return relation.getTarget();
        }
        return relation.getSource();
    }

    @JsonGetter
    public TypeMultiplicityEnum getMultiplicity() {
        RelationType relationType = relation.relationType();
        if (relationType == RelationType.MANY_TO_MANY) {
            return TypeMultiplicityEnum.Collection;
        }
        if (relationType == RelationType.ONE_TO_ONE) {
            return TypeMultiplicityEnum.Element;
        }
        if (isOwner() && relationType == RelationType.ONE_TO_MANY) {
            return TypeMultiplicityEnum.Collection;
        }
        if (!isOwner() && relationType == RelationType.MANY_TO_ONE) {
            return TypeMultiplicityEnum.Collection;
        }
        return TypeMultiplicityEnum.Element;
    }

    @JsonGetter
    public String getSource() {
        if (isOwner()) {
            return relation.getSource();
        }
        return relation.getTarget();
    }

    @JsonGetter
    public String getTarget() {
        if (isOwner()) {
            return relation.getTarget();
        }
        return relation.getSource();
    }

    @JsonGetter
    public String getSourceRoleName() {
        if (isOwner()) {
            return relation.getSourceRoleName();
        }
        return relation.getTargetRoleName();
    }

    @JsonGetter
    public String getTargetRoleName() {
        if (isOwner()) {
            return relation.getTargetRoleName();
        }
        return relation.getSourceRoleName();
    }

    @JsonGetter
    public boolean isBidirectional() {
        return Boolean.TRUE.equals(relation.getBidirectional());
    }

    @JsonGetter
    public boolean isComposition() {
        return Boolean.TRUE.equals(relation.getComposition());
    }

    @JsonGetter
    public RelationType getRelationType() {
        if (isOwner()) {
            return relation.relationType();
        }
        switch (relation.relationType()) {
            case ONE_TO_MANY:
                return RelationType.MANY_TO_ONE;
            case MANY_TO_ONE:
                return RelationType.ONE_TO_MANY;
            default:
                return relation.relationType();
        }
    }

    @JsonGetter
    public boolean isOwner() {
        return Objects.equals(relation.getSource(), parent.getId());
    }
}
