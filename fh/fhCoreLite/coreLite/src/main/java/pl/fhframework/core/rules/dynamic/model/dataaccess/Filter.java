package pl.fhframework.core.rules.dynamic.model.dataaccess;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import pl.fhframework.core.rules.dynamic.blockly.BlocklyBlock;
import pl.fhframework.core.rules.dynamic.model.*;
import pl.fhframework.core.rules.dynamic.model.predicates.DefinedCondition;
import pl.fhframework.core.rules.service.RuleConsts;

import javax.xml.bind.annotation.*;
import java.util.*;
import java.util.function.Function;

@Getter
@Setter
@XmlRootElement(name = "Filter", namespace = RuleConsts.RULE_XSD)
@XmlAccessorType(XmlAccessType.FIELD)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class Filter extends StatementWithList implements DataAccess {
    @XmlTransient
    @JsonIgnore
    public static final String TAG_NAME = "FilterBlock";

    @XmlTransient
    @JsonIgnore
    public static final String CONDITION_FIELD_NAME = "condition";

    @XmlElementRef
    private List<Statement> statements = new LinkedList<>();

    @Override
    public BlocklyBlock convertToBlockly(Function<String, String> formatter) {
        BlocklyBlock block = new BlocklyBlock();
        block.setId(this.getOrGenerateId());
        block.setType(Filter.TAG_NAME);
        block.setX(this.getX());
        block.setY(this.getY());

        addComplexValues(block, formatter);

        return block;
    }

    public static RuleElement convertFromBlockly(BlocklyBlock parsedBlock) {
        Filter filter = new Filter();
        filter.setId(parsedBlock.getId());
        filter.setX(parsedBlock.getX());
        filter.setY(parsedBlock.getY());

        return filter;
    }

    @Override
    public void processValueChange(String name, String value) {
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
    }

    @Override
    public Map<String, RuleElement> getComplexValues() {
        if (getStatements().size() > 0) {
            Map<String, RuleElement> complexValues = new HashMap<>();
            complexValues.put(CONDITION_FIELD_NAME, getStatements().get(0));

            return complexValues;
        }
        return Collections.emptyMap();
    }

    @Override
    public Class getInputType(String name) {
        return Statement.class;
    }

    public static Filter of(DefinedCondition definedCondition) {
        Filter filter = new Filter();

        if (definedCondition != null) {
            filter.getStatements().add(definedCondition);
        }

        return filter;
    }

    public static Filter of() {
        return new Filter();
    }
}