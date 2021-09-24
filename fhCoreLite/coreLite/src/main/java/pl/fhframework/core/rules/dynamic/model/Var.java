package pl.fhframework.core.rules.dynamic.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import pl.fhframework.core.rules.dynamic.blockly.BlocklyBlock;
import pl.fhframework.core.rules.dynamic.blockly.BlocklyField;
import pl.fhframework.core.rules.service.RuleConsts;
import pl.fhframework.core.uc.dynamic.model.element.attribute.TypeMultiplicityEnum;
import pl.fhframework.core.util.StringUtils;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Function;

@Getter
@Setter
@XmlRootElement(name = "Var", namespace = RuleConsts.RULE_XSD)
@XmlAccessorType(XmlAccessType.FIELD)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class Var extends SimpleStatement implements Serializable {
    @XmlTransient
    @JsonIgnore
    public static final String TAG_NAME = "VarBlock";

    @XmlTransient
    @JsonIgnore
    public static final String NAME_FIELD_NAME = "name";

    @XmlTransient
    @JsonIgnore
    public static final String TYPE_FIELD_NAME = "type";

    @XmlTransient
    @JsonIgnore
    public static final String MULTI_FIELD_NAME = "multiplicity";

    @XmlTransient
    @JsonIgnore
    public static final String INIT_FIELD_NAME = "init";

    @XmlTransient
    @JsonIgnore
    public static final String VALUE_FIELD_NAME = "value";

    @XmlAttribute
    private String type;

    @XmlAttribute
    private String name;

    @XmlAttribute
    private TypeMultiplicityEnum multiplicity = TypeMultiplicityEnum.Element;

    @XmlAttribute
    private Boolean init = Boolean.FALSE;

    protected String getTagName() {
        return Var.TAG_NAME;
    }

    @Override
    public BlocklyBlock convertToBlockly(Function<String, String> formatter) {
        BlocklyBlock block = new BlocklyBlock();
        block.setId(this.getOrGenerateId());
        block.setType(getTagName());
        block.setX(this.getX());
        block.setY(this.getY());

        List<BlocklyField> fields = new ArrayList<>();
        fields.add(new BlocklyField(Var.NAME_FIELD_NAME, this.getName()));
        fields.add(new BlocklyField(Var.TYPE_FIELD_NAME, this.getType()).setEditorType(BlocklyField.EditorType.COMBO_FIXED).setFieldType(Type.class));
        fields.add(new BlocklyField(Var.MULTI_FIELD_NAME, this.getMultiplicity().toString()).setEditorType(BlocklyField.EditorType.FIXED).setFieldType(TypeMultiplicityEnum.class));
        fields.add(new BlocklyField(Var.INIT_FIELD_NAME, this.getInit().toString()).setEditorType(BlocklyField.EditorType.FIXED).setFieldType(boolean.class));
        fields.add(new BlocklyField(Var.VALUE_FIELD_NAME, formatter.apply(this.getValue())).setEditorType(BlocklyField.EditorType.COMBO));
        block.setFields(fields);

        return block;
    }

    public static RuleElement convertFromBlockly(BlocklyBlock parsedBlock) {
        Var varBlock = new Var();
        varBlock.convertFromBlocklyInternal(parsedBlock);

        return varBlock;
    }

    void convertFromBlocklyInternal(BlocklyBlock parsedBlock) {
        setId(parsedBlock.getId());
        setX(parsedBlock.getX());
        setY(parsedBlock.getY());

        Optional<String> name = parsedBlock.getFieldValue(Var.NAME_FIELD_NAME);
        name.ifPresent(this::setName);

        Optional<String> type = parsedBlock.getFieldValue(Var.TYPE_FIELD_NAME);
        type.ifPresent(this::setType);

        Optional<String> multi = parsedBlock.getFieldValue(Var.MULTI_FIELD_NAME);
        multi.ifPresent(multiStr -> processValueChange(Var.MULTI_FIELD_NAME, multiStr));

        Optional<String> init = parsedBlock.getFieldValue(Var.INIT_FIELD_NAME);
        init.ifPresent(initStr -> processValueChange(Var.INIT_FIELD_NAME, initStr));

        Optional<String> value = parsedBlock.getFieldValue(Var.VALUE_FIELD_NAME);
        value.ifPresent(this::setValue);
    }

    @Override
    public void processValueChange(String name, String value) {
        switch (name) {
            case Var.NAME_FIELD_NAME:
                this.setName(value);
                break;
            case Var.TYPE_FIELD_NAME:
                this.setType(value);
                break;
            case Var.MULTI_FIELD_NAME:
                if (!StringUtils.isNullOrEmpty(value)) {
                    this.setMultiplicity(TypeMultiplicityEnum.valueOf(value));
                } else {
                    this.setMultiplicity(null);
                }
                break;
            case Var.INIT_FIELD_NAME:
                if (!StringUtils.isNullOrEmpty(value)) {
                    if (StringUtils.equalsIgnoreCase("true", value.toLowerCase())) {
                        this.setInit(Boolean.TRUE);
                    } else {
                        this.setInit(Boolean.FALSE);
                    }
                } else {
                    this.setInit(Boolean.FALSE);
                }
                break;
            case Var.VALUE_FIELD_NAME:
                if (!StringUtils.isNullOrEmpty(value)) {
                    this.setValue(value);
                } else {
                    this.setValue(null);
                }
                break;
        }
    }
}