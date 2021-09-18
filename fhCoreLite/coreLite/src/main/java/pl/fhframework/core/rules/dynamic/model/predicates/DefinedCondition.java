package pl.fhframework.core.rules.dynamic.model.predicates;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Getter;
import lombok.Setter;
import pl.fhframework.core.rules.dynamic.model.Condition;
import pl.fhframework.core.rules.dynamic.model.Statement;
import pl.fhframework.core.rules.service.RuleConsts;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

/**
 * Created by pawel.ruta on 2017-08-18.
 */
@Getter
@Setter
@XmlRootElement(name = "DefinedCondition", namespace = RuleConsts.RULE_XSD)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({AndCondition.class, OrCondition.class, NotCondition.class, CompareCondition.class, ExistsInCondition.class})
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public abstract class DefinedCondition extends Statement implements Predicate {
    public Condition getWhen() {
        return null;
    }
}
