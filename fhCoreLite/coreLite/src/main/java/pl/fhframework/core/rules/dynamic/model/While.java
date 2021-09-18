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
@XmlRootElement(name = "While", namespace = RuleConsts.RULE_XSD)
@XmlAccessorType(XmlAccessType.FIELD)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class While extends StatementWithList implements Loop {
    @XmlTransient
    @JsonIgnore
    public static final String TAG_NAME = "WhileBlock";

    @XmlTransient
    @JsonIgnore
    public static final String CONDITION_FIELD_NAME = "condition";

    @XmlElementRef
    private Condition condition = new Condition();

    @XmlElementRef
    private Then then = new Then();

    @Override
    @JsonIgnore
    public List<Statement> getStatements() {
        return then.getStatements();
    }

    protected String getTagName() {
        return While.TAG_NAME;
    }

    @Override
    public BlocklyBlock convertToBlockly(Function<String, String> formatter) {
        BlocklyBlock block = new BlocklyBlock();
        block.setId(this.getOrGenerateId());
        block.setType(getTagName());
        block.setX(this.getX());
        block.setY(this.getY());

        addComplexValues(block, formatter);

        addStatements(block, formatter);

        return block;
    }

    public static RuleElement convertFromBlockly(BlocklyBlock parsedBlock) {
        While whileBlock = new While();
        whileBlock.convertFromBlocklyInternal(parsedBlock);

        return whileBlock;
    }

    void convertFromBlocklyInternal(BlocklyBlock parsedBlock) {
        this.setId(parsedBlock.getId());
        this.setX(parsedBlock.getX());
        this.setY(parsedBlock.getY());
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
}