package pl.fhframework.core.rules.dynamic.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import pl.fhframework.core.rules.dynamic.blockly.BlocklyBlock;
import pl.fhframework.core.rules.dynamic.model.predicates.DefinedCondition;
import pl.fhframework.core.rules.dynamic.model.predicates.Predicate;
import pl.fhframework.core.rules.service.RuleConsts;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@XmlRootElement(name = "BooleanExpression", namespace = RuleConsts.RULE_XSD)
@XmlAccessorType(XmlAccessType.FIELD)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class BooleanExpression extends Expression implements Predicate {
    @XmlTransient
    @JsonIgnore
    public static final String TAG_NAME = "BooleanExpressionBlock";

    @XmlTransient
    @JsonIgnore
    public static final String WHEN_FIELD_NAME = "when";

    @XmlAttribute
    private String value;

    @XmlElement(name = "When")
    private Condition when;

    @Override
    public String getTagName() {
        return BooleanExpression.TAG_NAME;
    }

    public static RuleElement convertFromBlockly(BlocklyBlock block) {
        BooleanExpression expression = new BooleanExpression();
        expression.convertFromBlocklyInternal(block);

        return expression;
    }

    @Override
    public void processValueChange(String name, RuleElement value) {
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
    @JsonIgnore
    public Map<String, RuleElement> getComplexValues() {
        Map<String, RuleElement> complexValues = new HashMap<>();

        if (getWhen() != null && getWhen().getPredicate() != null) {
            complexValues.put(WHEN_FIELD_NAME, getWhen().getPredicate());
        }

        return complexValues;
    }

    public String getValue() {
        if (super.getValue() != null) {
            return super.getValue();
        }
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
        super.setValue(null);
    }
}