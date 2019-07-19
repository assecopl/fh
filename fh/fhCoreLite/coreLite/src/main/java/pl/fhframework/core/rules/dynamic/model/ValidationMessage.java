package pl.fhframework.core.rules.dynamic.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import pl.fhframework.core.rules.dynamic.blockly.BlocklyBlock;
import pl.fhframework.core.rules.dynamic.blockly.BlocklyField;
import pl.fhframework.core.rules.dynamic.model.predicates.CompareCondition;
import pl.fhframework.core.rules.service.RuleConsts;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.model.PresentationStyleEnum;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Getter
@Setter
@XmlRootElement(name = "ValidationMessage", namespace = RuleConsts.RULE_XSD)
@XmlAccessorType(XmlAccessType.FIELD)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class ValidationMessage extends Statement {
    @XmlTransient
    @JsonIgnore
    public static final String TAG_NAME = "ValidationMessageBlock";

    @XmlTransient
    @JsonIgnore
    public static final String MESSAGE_FIELD_NAME = "message";

    @XmlTransient
    @JsonIgnore
    public static final String FIELD_FIELD_NAME = "field";

    @XmlTransient
    @JsonIgnore
    public static final String OBJECT_FIELD_NAME = "object";

    @XmlTransient
    @JsonIgnore
    public static final String LEVEL_FIELD_NAME = "level";

    @XmlAttribute
    private String message;

    @XmlAttribute
    private String object;

    @XmlAttribute
    private String field;

    @XmlAttribute
    private String level;

    @Override
    public BlocklyBlock convertToBlockly(Function<String, String> formatter) {

        BlocklyBlock block = new BlocklyBlock();
        block.setId(this.getOrGenerateId());
        block.setType(ValidationMessage.TAG_NAME);
        block.setX(this.getX());
        block.setY(this.getY());

        List<BlocklyField> fields = new ArrayList<>();
        fields.add(new BlocklyField(MESSAGE_FIELD_NAME, formatter.apply(this.getMessage())).setEditorType(BlocklyField.EditorType.COMBO));
        fields.add(new BlocklyField(OBJECT_FIELD_NAME, formatter.apply(this.getObject())).setEditorType(BlocklyField.EditorType.COMBO));
        fields.add(new BlocklyField(FIELD_FIELD_NAME, formatter.apply(this.getField())).setEditorType(BlocklyField.EditorType.COMBO));
        fields.add(new BlocklyField(LEVEL_FIELD_NAME, this.getLevel()).setEditorType(BlocklyField.EditorType.FIXED).setFieldType(ValidationLevelEnum.class));
        block.setFields(fields);

        return block;
    }

    public static RuleElement convertFromBlockly(BlocklyBlock block) {
        ValidationMessage validationMessage = new ValidationMessage();
        validationMessage.setId(block.getId());
        validationMessage.setX(block.getX());
        validationMessage.setY(block.getY());

        Optional<String> message = block.getFieldValue(MESSAGE_FIELD_NAME);
        message.ifPresent(validationMessage::setMessage);

        Optional<String> object = block.getFieldValue(OBJECT_FIELD_NAME);
        object.ifPresent(validationMessage::setObject);

        Optional<String> field = block.getFieldValue(FIELD_FIELD_NAME);
        field.ifPresent(validationMessage::setField);

        Optional<String> level = block.getFieldValue(LEVEL_FIELD_NAME);
        level.ifPresent(validationMessage::setLevel);

        return validationMessage;
    }

    @Override
    public void processValueChange(String name, String value) {
        switch (name) {
            case MESSAGE_FIELD_NAME:
                this.setMessage(value);
                break;
            case OBJECT_FIELD_NAME:
                this.setObject(value);
                break;
            case FIELD_FIELD_NAME:
                this.setField(value);
                break;
            case LEVEL_FIELD_NAME:
                this.setLevel(value);
                break;
        }
    }

    public enum ValidationLevelEnum {
        Error("error"),
        Warning("warning"),
        Info("info"),
        ;

        @Getter
        private String level;

        ValidationLevelEnum(String level) {
            this.level = level;
        }

        public static ValidationLevelEnum fromString(String level) {
            return Arrays.stream(ValidationLevelEnum.values()).filter(levelEnum -> levelEnum.getLevel().equals(level)).findAny().orElse(null);
        }

        @Override
        public String toString() {
            return getLevel();
        }
    }
}