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
@XmlRootElement(name = "Limit", namespace = RuleConsts.RULE_XSD)
@XmlAccessorType(XmlAccessType.FIELD)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class Limit extends SimpleStatement implements DataAccess {
    @XmlTransient
    @JsonIgnore
    public static final String TAG_NAME = "LimitBlock";

    @XmlTransient
    @JsonIgnore
    public static final String VALUE_FIELD_NAME = "value";

    @SkipSnapshot
    @XmlTransient
    @JsonIgnore
    private int limit;

    @Override
    public BlocklyBlock convertToBlockly(Function<String, String> formatter) {
        BlocklyBlock block = new BlocklyBlock();
        block.setId(this.getOrGenerateId());
        block.setType(Limit.TAG_NAME);
        block.setX(this.getX());
        block.setY(this.getY());

        List<BlocklyField> fields = new ArrayList<>();
        fields.add(new BlocklyField(Limit.VALUE_FIELD_NAME, formatter.apply(this.getValue())).setEditorType(BlocklyField.EditorType.COMBO));
        block.setFields(fields);

        return block;
    }

    public static RuleElement convertFromBlockly(BlocklyBlock block) {
        Limit limit = new Limit();
        limit.setId(block.getId());
        limit.setX(block.getX());
        limit.setY(block.getY());

        limit.setValue(block.getFieldValue(Limit.VALUE_FIELD_NAME).orElse(""));

        return limit;
    }

    @Override
    public void processValueChange(String name, String value) {
        if (name.equals(VALUE_FIELD_NAME)) {
            this.setValue(value);
        }
    }

    public static Limit of(int limit) {
        Limit limitObj = new Limit();
        limitObj.setLimit(limit);
        limitObj.setValue(Integer.toString(limit));

        return limitObj;
    }
}