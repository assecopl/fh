package pl.fhframework.core.rules.dynamic.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import pl.fhframework.core.rules.dynamic.blockly.BlocklyBlock;
import pl.fhframework.core.rules.dynamic.blockly.BlocklyField;
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
@XmlRootElement(name = "Return", namespace = RuleConsts.RULE_XSD)
@XmlAccessorType(XmlAccessType.FIELD)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class Return extends SimpleStatement implements Branching {
    @XmlTransient
    @JsonIgnore
    public static final String TAG_NAME = "ReturnBlock";

    @XmlTransient
    @JsonIgnore
    public static final String VALUE_FIELD_NAME = "value";

    @Override
    public BlocklyBlock convertToBlockly(Function<String, String> formatter) {
        BlocklyBlock block = new BlocklyBlock();
        block.setId(this.getOrGenerateId());
        block.setType(Return.TAG_NAME);
        block.setX(this.getX());
        block.setY(this.getY());

        List<BlocklyField> fields = new ArrayList<>();
        fields.add(new BlocklyField(Expression.VALUE_FIELD_NAME, formatter.apply(this.getValue())).setEditorType(BlocklyField.EditorType.COMBO));
        block.setFields(fields);

        return block;
    }

    public static RuleElement convertFromBlockly(BlocklyBlock block) {
        Return returnStatement = new Return();
        returnStatement.setId(block.getId());
        returnStatement.setX(block.getX());
        returnStatement.setY(block.getY());

        returnStatement.setValue(block.getFieldValue(Expression.VALUE_FIELD_NAME).orElse(""));

        return returnStatement;
    }

    @Override
    public void processValueChange(String name, String value) {
        if (name.equals(VALUE_FIELD_NAME)) {
            this.setValue(value);
        }
    }
}