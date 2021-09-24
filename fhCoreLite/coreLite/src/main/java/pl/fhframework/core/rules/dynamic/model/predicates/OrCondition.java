package pl.fhframework.core.rules.dynamic.model.predicates;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import pl.fhframework.core.rules.dynamic.blockly.BlocklyBlock;
import pl.fhframework.core.rules.dynamic.model.EmptyStatement;
import pl.fhframework.core.rules.dynamic.model.RuleElement;
import pl.fhframework.core.rules.dynamic.model.Statement;
import pl.fhframework.core.rules.service.RuleConsts;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by pawel.ruta on 2017-08-18.
 */
@Getter
@Setter
@XmlRootElement(name = "Or", namespace = RuleConsts.RULE_XSD)
@XmlAccessorType(XmlAccessType.FIELD)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class OrCondition extends ComplexCondition {
    @XmlTransient
    @JsonIgnore
    public static final String TAG_NAME = "OrBlock";

    @XmlTransient
    @JsonIgnore
    public static final String L_CONDITION_FIELD_NAME = "leftCondition";

    @XmlTransient
    @JsonIgnore
    public static final String R_CONDITION_FIELD_NAME = "rightCondition";

    @Override
    protected String getTagName() {
        return OrCondition.TAG_NAME;
    }

    public static RuleElement convertFromBlockly(BlocklyBlock parsedBlock) {
        OrCondition orCondition = new OrCondition();
        orCondition.convertFromBlocklyInternal(parsedBlock);

        return orCondition;
    }

    public static OrCondition of(DefinedCondition... definedConditions) {
        OrCondition orCondition = new OrCondition();

        if (definedConditions != null) {
            Arrays.stream(definedConditions).forEach(definedCondition -> orCondition.getStatements().add(definedCondition));
        }

        return orCondition;
    }

    @Override
    public void processValueChange(String name, RuleElement value) {
        if (L_CONDITION_FIELD_NAME.equals(name)) {
            getStatements().remove(0);
            if (value != null) {
                getStatements().add(0, (Statement) value);
            }
            else {
                getStatements().add(0, new EmptyStatement());
            }
        }
        if (R_CONDITION_FIELD_NAME.equals(name)) {
            getStatements().remove(1);
            if (value != null) {
                getStatements().add(1, (Statement) value);
            }
            else {
                getStatements().add(1, new EmptyStatement());
            }
        }
    }

    @Override
    public Map<String, RuleElement> getComplexValues() {
        while (getStatements().size() < 2) {
            getStatements().add(new EmptyStatement());
        }
        Map<String, RuleElement> complexValues = new HashMap<>();
        if (!EmptyStatement.class.isInstance(getStatements().get(0))) {
            complexValues.put(L_CONDITION_FIELD_NAME, getStatements().get(0));
        }
        Statement rightPredicate = getStatements().get(1);
        if (getStatements().size() > 2) {
            AndCondition nestedAnd = new AndCondition();
            nestedAnd.getStatements().addAll(getStatements().subList(1, getStatements().size()));
            rightPredicate = nestedAnd;
            getStatements().removeAll(nestedAnd.getStatements());
            getStatements().add(nestedAnd);
        }

        if (!EmptyStatement.class.isInstance(getStatements().get(1))) {
            complexValues.put(R_CONDITION_FIELD_NAME, rightPredicate);
        }

        return complexValues;
    }

    @Override
    public Class getInputType(String name) {
        return Statement.class;
    }
}
