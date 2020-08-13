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
@XmlRootElement(name = "If", namespace = RuleConsts.RULE_XSD)
@XmlAccessorType(XmlAccessType.FIELD)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class If extends StatementWithList implements Conditional {
    @XmlTransient
    @JsonIgnore
    public static final String TAG_NAME = "IfClauseBlock";

    @XmlTransient
    @JsonIgnore
    public static final String CONDITION_FIELD_NAME = "condition";

    @XmlElementRef
    Condition condition = new Condition();

    @XmlElementRef
    Then then = new Then();

    @Override
    public BlocklyBlock convertToBlockly(Function<String, String> formatter) {
        BlocklyBlock block = new BlocklyBlock();
        block.setId(this.getOrGenerateId());
        block.setType(If.TAG_NAME);
        block.setX(this.getX());
        block.setY(this.getY());

        addComplexValues(block, formatter);

        addStatements(block, formatter);

        return block;
    }

    public static RuleElement convertFromBlockly(BlocklyBlock parsedBlock) {
        If ifClause = new If();
        ifClause.setId(parsedBlock.getId());
        ifClause.setX(parsedBlock.getX());
        ifClause.setY(parsedBlock.getY());

        return ifClause;
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

        if (getCondition().getPredicate() != null) {
            complexValues.put(CONDITION_FIELD_NAME, getCondition().getPredicate());
        }

        return complexValues;
    }

    @Override
    public List<Statement> getStatements() {
        return getThen().getStatements();
    }
}