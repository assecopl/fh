package pl.fhframework.core.rules.dynamic.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import pl.fhframework.core.rules.dynamic.blockly.BlocklyBlock;
import pl.fhframework.core.rules.dynamic.model.predicates.Predicate;
import pl.fhframework.core.rules.service.RuleConsts;

import javax.xml.bind.annotation.*;
import java.util.*;
import java.util.function.Function;

@Getter
@Setter
@XmlRootElement(name = "Else", namespace = RuleConsts.RULE_XSD)
@XmlAccessorType(XmlAccessType.FIELD)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class Else extends StatementWithList implements Conditional {
    @XmlTransient
    @JsonIgnore
    public static final String TAG_IF_NAME = "ElseIfClauseBlock";

    @XmlTransient
    @JsonIgnore
    public static final String TAG_NAME = "ElseClauseBlock";

    @XmlTransient
    @JsonIgnore
    public static final String CONDITION_FIELD_NAME = "condition";

    @XmlElementRef
    Condition condition;

    @XmlElementRef
    Then then = new Then();

    @Override
    @JsonIgnore
    public List<Statement> getStatements() {
        return getThen().getStatements();
    }

    @Override
    public BlocklyBlock convertToBlockly(Function<String, String> formatter) {
        BlocklyBlock block = new BlocklyBlock();
        block.setId(this.getOrGenerateId());
        if (getCondition() != null) {
            block.setType(Else.TAG_IF_NAME);
        }
        else {
            block.setType(Else.TAG_NAME);
        }
        block.setX(this.getX());
        block.setY(this.getY());

       addComplexValues(block, formatter);

        addStatements(block, formatter);

        return block;
    }

    public static RuleElement convertFromBlockly(BlocklyBlock parsedBlock) {
        Else elseClause = new Else();
        elseClause.setId(parsedBlock.getId());
        elseClause.setX(parsedBlock.getX());
        elseClause.setY(parsedBlock.getY());
        if (TAG_IF_NAME.equals(parsedBlock.getType())) {
            elseClause.setCondition(new Condition());
        }

        return elseClause;
    }

    @Override
    public void processValueChange(String name, RuleElement value) {
        if (name.equals(CONDITION_FIELD_NAME)) {
            this.getCondition().setPredicate((Predicate) value);
        }
    }

    @Override
    public Map<String, RuleElement> getComplexValues() {
        Map<String, RuleElement> complexValues = new HashMap<>();

        if (getCondition() != null && getCondition().getPredicate() != null) {
            complexValues.put(CONDITION_FIELD_NAME, getCondition().getPredicate());
        }

        return complexValues;
    }
}