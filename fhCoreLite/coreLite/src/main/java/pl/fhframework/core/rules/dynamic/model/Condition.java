package pl.fhframework.core.rules.dynamic.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Getter;
import lombok.Setter;
import pl.fhframework.aspects.snapshots.model.ISnapshotEnabled;
import pl.fhframework.core.rules.dynamic.model.predicates.Predicate;
import pl.fhframework.core.rules.service.RuleConsts;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
@XmlRootElement(name = "Condition", namespace = RuleConsts.RULE_XSD)
@XmlAccessorType(XmlAccessType.FIELD)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class Condition implements StatementsList, ISnapshotEnabled {
    @XmlElementRef
    private List<Statement> statements = new LinkedList<>();

    public Predicate getPredicate() {
        if (statements.size() > 0){
            return (Predicate) statements.get(0);
        }

        return null;
    }

    public void setPredicate(Predicate predicate) {
        if (predicate == null) {
            statements.remove(0);
        }
        else {
            if (statements.size() == 0) {
                statements.add((Statement) predicate);
            } else {
                statements.set(0, (Statement) predicate);
            }
        }
    }
}