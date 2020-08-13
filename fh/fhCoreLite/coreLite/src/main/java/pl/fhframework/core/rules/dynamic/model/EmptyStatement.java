package pl.fhframework.core.rules.dynamic.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import pl.fhframework.core.rules.dynamic.blockly.BlocklyBlock;
import pl.fhframework.core.rules.service.RuleConsts;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.function.Function;

@Getter
@Setter
@XmlRootElement(name = "Empty", namespace = RuleConsts.RULE_XSD)
@XmlAccessorType(XmlAccessType.FIELD)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class EmptyStatement extends Statement {
    @XmlTransient
    @JsonIgnore
    public static final String TAG_NAME = "Empty";

    @Override
    public BlocklyBlock convertToBlockly(Function<String, String> formatter) {

        BlocklyBlock block = new BlocklyBlock();
        block.setId(this.getOrGenerateId());
        block.setType(EmptyStatement.TAG_NAME);
        block.setX(this.getX());
        block.setY(this.getY());

        return block;
    }

    public static RuleElement convertFromBlockly(BlocklyBlock block) {
        EmptyStatement breakStatement = new EmptyStatement();
        breakStatement.setId(block.getId());
        breakStatement.setX(block.getX());
        breakStatement.setY(block.getY());

        return breakStatement;
    }
}