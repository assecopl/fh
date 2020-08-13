package pl.fhframework.core.rules.dynamic.model.dataaccess;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import pl.fhframework.core.rules.dynamic.blockly.BlocklyBlock;
import pl.fhframework.core.rules.dynamic.blockly.BlocklyField;
import pl.fhframework.core.rules.dynamic.model.Condition;
import pl.fhframework.core.rules.dynamic.model.RuleElement;
import pl.fhframework.core.rules.dynamic.model.SimpleStatement;
import pl.fhframework.core.rules.dynamic.model.predicates.Predicate;
import pl.fhframework.core.rules.service.RuleConsts;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Getter
@Setter
@XmlRootElement(name = "SortField", namespace = RuleConsts.RULE_XSD)
@XmlAccessorType(XmlAccessType.FIELD)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class SortField extends SimpleStatement implements DataAccess {
    @XmlTransient
    @JsonIgnore
    public static final String TAG_NAME = "SortFieldBlock";

    @XmlTransient
    @JsonIgnore
    public static final String VALUE_FIELD_NAME = "value";

    @XmlTransient
    @JsonIgnore
    public static final String DIRECTION_FIELD_NAME = "direction";

    @XmlTransient
    @JsonIgnore
    public static final String WHEN_FIELD_NAME = "when";

    @XmlAttribute
    private String value;

    @XmlAttribute
    private String direction;

    @XmlElement(name = "When")
    private Condition when;

    @Override
    public BlocklyBlock convertToBlockly(Function<String, String> formatter) {
        BlocklyBlock block = new BlocklyBlock();
        block.setId(this.getOrGenerateId());
        block.setType(SortField.TAG_NAME);
        block.setX(this.getX());
        block.setY(this.getY());

        List<BlocklyField> fields = new ArrayList<>();
        fields.add(new BlocklyField(SortField.VALUE_FIELD_NAME, formatter.apply(this.getValue())).setEditorType(BlocklyField.EditorType.COMBO));
        fields.add(new BlocklyField(SortField.DIRECTION_FIELD_NAME, this.getDirection()).setEditorType(BlocklyField.EditorType.FIXED).setFieldType(SortDirectionEnum.class));
        block.setFields(fields);

        addComplexValues(block, formatter);

        return block;
    }

    public static RuleElement convertFromBlockly(BlocklyBlock block) {
        SortField sortField = new SortField();
        sortField.setId(block.getId());
        sortField.setX(block.getX());
        sortField.setY(block.getY());

        sortField.setValue(block.getFieldValue(SortField.VALUE_FIELD_NAME).orElse(""));
        sortField.setDirection(block.getFieldValue(SortField.DIRECTION_FIELD_NAME).orElse(""));

        return sortField;
    }

    @Override
    public void processValueChange(String name, String value) {
        if (name.equals(VALUE_FIELD_NAME)) {
            this.setValue(value);
        }
        if (name.equals(DIRECTION_FIELD_NAME)) {
            this.setDirection(value);
        }
    }

    @Override
    public void processValueChange(String name, RuleElement value) {
        if (WHEN_FIELD_NAME.equals(name)) {
            if (value != null) {
                if (getWhen() == null) {
                    setWhen(new Condition());
                }
                getWhen().setPredicate((Predicate) value);
            }
            else {
                setWhen(null);
            }
        }
    }

    @Override
    public Map<String, RuleElement> getComplexValues() {
        Map<String, RuleElement> complexValues = new HashMap<>();

        if (getWhen() != null && getWhen().getPredicate() != null) {
            complexValues.put(WHEN_FIELD_NAME, getWhen().getPredicate());
        }

        return complexValues;
    }

    public String getValue() {
        if (super.getValue() != null) {
            setValue(super.getValue());
        }
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
        super.setValue(null);
    }

    public static SortField of(String name, String direction) {
        SortField sortField = new SortField();
        sortField.setValue(name);
        sortField.setDirection(direction);

        return sortField;
    }
}