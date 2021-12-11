package pl.fhframework.compiler.core.model.meta;

import lombok.*;
import org.springframework.data.util.Pair;
import pl.fhframework.compiler.core.model.RelationMuliplicityMapper;
import pl.fhframework.compiler.core.model.RelationType;
import pl.fhframework.aspects.snapshots.model.ISnapshotEnabled;

import javax.xml.bind.annotation.*;

@Getter
@Setter
@XmlRootElement(name = "Relation")
@XmlAccessorType(XmlAccessType.FIELD)
@EqualsAndHashCode(of = {"name", "source", "target", "sourceRoleName", "targetRoleName", "bidirectional", "multiplicity"}, callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RelationTag implements ISnapshotEnabled, Cloneable {

    @XmlAttribute
    @XmlID
    private String name;

    @XmlAttribute
    private String multiplicity;

    @XmlAttribute(name = "source")
    private String source;

    @XmlAttribute(name = "target")
    private String target;

    @XmlAttribute(name = "sourceRoleName")
    private String sourceRoleName;

    @XmlAttribute(name = "targetRoleName")
    private String targetRoleName;

    @XmlAttribute
    private Boolean bidirectional;

    @XmlAttribute
    private Boolean composition;

    @XmlTransient
    private String sourceMultiplicity;

    @XmlTransient
    private String targetMultiplicity;

    @XmlTransient
    private boolean editable = true;

    public void postLoad() {
        if(this.multiplicity != null) {
            Pair<String, String> pair = RelationMuliplicityMapper.calculateMultiplicity(this.multiplicity);
            this.sourceMultiplicity = pair.getFirst();
            this.targetMultiplicity = pair.getSecond();
        }
    }

    private RelationTag(RelationTag rel) {
        this.name = rel.getName();
        this.multiplicity = rel.getMultiplicity();
        this.source = rel.getSource();
        this.target = rel.getTarget();
        this.sourceRoleName = rel.getSourceRoleName();
        this.targetRoleName = rel.getTargetRoleName();
        this.bidirectional = rel.getBidirectional();
        this.composition = rel.getComposition();
        this.sourceMultiplicity = rel.getSourceMultiplicity();
        this.targetMultiplicity = rel.getTargetMultiplicity();
    }

    public RelationType relationType() {
        return RelationType.getBy(this.multiplicity);
    }

    public RelationTag copyOf() {
        return new RelationTag(this);
    }

    @Override
    public Object clone() {
        return copyOf();
    }

    public String getOppositeClassTo(String fullClassName) {
        if(this.getSource().equals(fullClassName)) {
            return this.getTarget();
        } else if(this.getTarget().equals(fullClassName)) {
            return this.getSource();
        }
        return null;
    }

    public boolean isNotEditable() {
        return !this.isEditable();
    }

}
