package pl.fhframework.core.rules.dynamic.model.predicates;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import pl.fhframework.core.rules.dynamic.blockly.BlocklyBlock;
import pl.fhframework.core.rules.dynamic.model.Condition;
import pl.fhframework.core.rules.dynamic.model.RuleElement;
import pl.fhframework.core.rules.dynamic.model.Statement;
import pl.fhframework.core.rules.service.RuleConsts;

import javax.xml.bind.annotation.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by pawel.ruta on 2017-08-18.
 */
@Getter
@Setter
@XmlRootElement(name = "Not", namespace = RuleConsts.RULE_XSD)
@XmlAccessorType(XmlAccessType.FIELD)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class NotCondition extends ComplexCondition {
    @XmlTransient
    @JsonIgnore
    public static final String TAG_NAME = "NotBlock";

    @XmlTransient
    @JsonIgnore
    public static final String CONDITION_FIELD_NAME = "condition";

    @XmlTransient
    @JsonIgnore
    public static final String WHEN_FIELD_NAME = "when";

    @XmlElement(name = "When")
    private Condition when;

    @Override
    protected String getTagName() {
        return NotCondition.TAG_NAME;
    }

    public static RuleElement convertFromBlockly(BlocklyBlock parsedBlock) {
        NotCondition notCondition = new NotCondition();
        notCondition.convertFromBlocklyInternal(parsedBlock);

        return notCondition;
    }

    public static NotCondition of(DefinedCondition... definedConditions) {
        NotCondition notCondition = new NotCondition();

        if (definedConditions != null) {
            Arrays.stream(definedConditions).forEach(definedCondition -> notCondition.getStatements().add(definedCondition));
        }

        return notCondition;
    }

    @Override
    public void processValueChange(String name, RuleElement value) {
        if (CONDITION_FIELD_NAME.equals(name)) {
            if (getStatements().size() > 0) {
                getStatements().remove(0);
            }
            if (value != null) {
                getStatements().add(0, (Statement) value);
            }
        }
        if (WHEN_FIELD_NAME.equals(name)) {
            if (value != null) {
                if (getWhen() == null) {
                    setWhen(new Condition());
                }
                getWhen().setPredicate((Predicate) value);
            }
            else {
                setWhen(null);
            }
        }
    }

    @Override
    public Map<String, RuleElement> getComplexValues() {
        Map<String, RuleElement> complexValues = new HashMap<>();
        if (getStatements().size() > 0) {
            complexValues.put(CONDITION_FIELD_NAME, getStatements().get(0));
        }
        if (getWhen() != null && getWhen().getPredicate() != null) {
            complexValues.put(WHEN_FIELD_NAME, getWhen().getPredicate());
        }
        return complexValues;
    }

    @Override
    public Class getInputType(String name) {
        return Statement.class;
    }
}
