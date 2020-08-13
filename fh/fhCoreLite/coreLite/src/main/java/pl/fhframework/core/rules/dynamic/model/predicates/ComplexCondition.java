package pl.fhframework.core.rules.dynamic.model.predicates;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Getter;
import lombok.Setter;
import pl.fhframework.core.rules.dynamic.blockly.BlocklyBlock;
import pl.fhframework.core.rules.dynamic.model.*;
import pl.fhframework.core.rules.service.RuleConsts;

import javax.xml.bind.annotation.*;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

/**
 * Created by pawel.ruta on 2017-08-18.
 */
@Getter
@Setter
@XmlRootElement(name = "ComplexCondition", namespace = RuleConsts.RULE_XSD)
@XmlAccessorType(XmlAccessType.FIELD)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public abstract class ComplexCondition extends DefinedCondition implements StatementsList {
    @XmlElementRef
    private List<Statement> statements = new LinkedList<>();

    protected abstract String getTagName();

    @Override
    public BlocklyBlock convertToBlockly(Function<String, String> formatter) {
        BlocklyBlock block = new BlocklyBlock();
        block.setId(this.getOrGenerateId());
        block.setType(getTagName());
        block.setX(this.getX());
        block.setY(this.getY());

        addComplexValues(block, formatter);

        return block;
    }

    protected void convertFromBlocklyInternal(BlocklyBlock block) {
        setId(block.getId());
        setX(block.getX());
        setY(block.getY());
    }
}
