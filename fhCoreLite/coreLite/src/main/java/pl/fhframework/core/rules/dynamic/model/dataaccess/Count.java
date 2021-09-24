package pl.fhframework.core.rules.dynamic.model.dataaccess;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import pl.fhframework.aspects.snapshots.model.SkipSnapshot;
import pl.fhframework.core.rules.dynamic.blockly.BlocklyBlock;
import pl.fhframework.core.rules.dynamic.blockly.BlocklyField;
import pl.fhframework.core.rules.dynamic.model.RuleElement;
import pl.fhframework.core.rules.dynamic.model.SimpleStatement;
import pl.fhframework.core.rules.dynamic.model.Statement;
import pl.fhframework.core.rules.service.RuleConsts;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Getter
@Setter
@XmlRootElement(name = "Count", namespace = RuleConsts.RULE_XSD)
@XmlAccessorType(XmlAccessType.FIELD)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class Count extends Statement implements DataAccess {
    @XmlTransient
    @JsonIgnore
    public static final String TAG_NAME = "CountBlock";

    @Override
    public BlocklyBlock convertToBlockly(Function<String, String> formatter) {
        BlocklyBlock block = new BlocklyBlock();
        block.setId(this.getOrGenerateId());
        block.setType(Count.TAG_NAME);
        block.setX(this.getX());
        block.setY(this.getY());

        return block;
    }

    public static RuleElement convertFromBlockly(BlocklyBlock block) {
        Count limit = new Count();
        limit.setId(block.getId());
        limit.setX(block.getX());
        limit.setY(block.getY());

        return limit;
    }

    public static Count of() {
        Count count = new Count();

        return count;
    }
}