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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Getter
@Setter
@XmlRootElement(name = "Init", namespace = RuleConsts.RULE_XSD)
@XmlAccessorType(XmlAccessType.FIELD)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class Init extends SimpleStatement implements Serializable {
    @XmlTransient
    @JsonIgnore
    public static final String TAG_NAME = "InitBlock";

    @XmlTransient
    @JsonIgnore
    public static final String VALUE_FIELD_NAME = "value";

    @Override
    public BlocklyBlock convertToBlockly(Function<String, String> formatter) {
        BlocklyBlock block = new BlocklyBlock();
        block.setId(this.getOrGenerateId());
        block.setType(Init.TAG_NAME);
        block.setX(this.getX());
        block.setY(this.getY());

        List<BlocklyField> fields = new ArrayList<>();
        fields.add(new BlocklyField(Expression.VALUE_FIELD_NAME, formatter.apply(this.getValue())).setEditorType(BlocklyField.EditorType.COMBO));
        block.setFields(fields);

        return block;
    }

    public static RuleElement convertFromBlockly(BlocklyBlock block) {
        Init init = new Init();
        init.setId(block.getId());
        init.setX(block.getX());
        init.setY(block.getY());

        init.setValue(block.getFieldValue(Expression.VALUE_FIELD_NAME).orElse(""));

        return init;
    }

    @Override
    public void processValueChange(String name, String value) {
        if (name.equals(VALUE_FIELD_NAME)) {
            this.setValue(value);
        }
    }
}