package pl.fhframework.core.rules.dynamic.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import pl.fhframework.core.rules.dynamic.HasConvertableField;
import pl.fhframework.core.rules.dynamic.blockly.BlocklyBlock;
import pl.fhframework.core.rules.dynamic.blockly.BlocklyField;
import pl.fhframework.core.rules.service.RuleConsts;
import pl.fhframework.core.util.StringUtils;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Getter
@Setter
@XmlRootElement(name = "Expression", namespace = RuleConsts.RULE_XSD)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({BooleanExpression.class})
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class Expression extends SimpleStatement implements Serializable, HasConvertableField {
    @XmlTransient
    @JsonIgnore
    public static final String TAG_NAME = "ExpressionBlock";

    @XmlTransient
    @JsonIgnore
    public static final String VALUE_FIELD_NAME = "value";

    @XmlTransient
    @JsonIgnore
    public static final String PRINT_NAME = "PrintBlock";

    @XmlTransient
    @JsonIgnore
    public static final String DATAWRITE_NAME = "DataWriteBlock";

    @XmlTransient
    @JsonIgnore
    public static final String DATAREFRESH_NAME = "DataRefreshBlock";

    @XmlTransient
    @JsonIgnore
    public static final String DATADELETE_NAME = "DataDeleteBlock";

    @XmlTransient
    @JsonIgnore
    public static final String DATAWRITE_REGEX = "RULE.pl.fhframework.core.rules.builtin.DataAccessUtils.storeWrite\\(.*\\)";

    @XmlTransient
    @JsonIgnore
    public static final String DATAREFRESH_REGEX = "RULE.pl.fhframework.core.rules.builtin.DataAccessUtils.storeRefresh\\(.*\\)";

    @XmlTransient
    @JsonIgnore
    public static final String DATADELETE_REGEX = "RULE.pl.fhframework.core.rules.builtin.DataAccessUtils.storeDelete\\(.*\\)";

    @XmlTransient
    @JsonIgnore
    public static final String PRINT_REGEX = "RULE.pl.fhframework.core.rules.builtin.Print.print\\(\'.*\'\\)";

    @XmlTransient
    @JsonIgnore
    public static final String TEXT_FIELD_NAME = "text";

    @Override
    public BlocklyBlock convertToBlockly(Function<String, String> formatter) {
        BlocklyBlock block = new BlocklyBlock();
        block.setId(this.getOrGenerateId());
        block.setX(this.getX());
        block.setY(this.getY());

        List<BlocklyField> fields = new ArrayList<>();
        String tagName = getTagName();
        block.setType(tagName);
        if (Expression.PRINT_NAME.equals(tagName)) {
            fields.add(new BlocklyField(Expression.TEXT_FIELD_NAME, getValueInner()));
        } else if (Expression.DATAWRITE_NAME.equals(tagName)) {
            fields.add(new BlocklyField(Expression.VALUE_FIELD_NAME, formatter.apply(getValueInner())).setEditorType(BlocklyField.EditorType.COMBO));
        } else if (Expression.DATAREFRESH_NAME.equals(tagName)) {
            fields.add(new BlocklyField(Expression.VALUE_FIELD_NAME, formatter.apply(getValueInner())).setEditorType(BlocklyField.EditorType.COMBO));
        } else if (Expression.DATADELETE_NAME.equals(tagName)) {
            fields.add(new BlocklyField(Expression.VALUE_FIELD_NAME, formatter.apply(getValueInner())).setEditorType(BlocklyField.EditorType.COMBO));
        } else {
            fields.add(new BlocklyField(Expression.VALUE_FIELD_NAME, formatter.apply(getValueInner())).setEditorType(BlocklyField.EditorType.COMBO));
        }
        block.setFields(fields);

        addComplexValues(block, formatter);

        return block;
    }

    @JsonIgnore
    public String getTagName() {
        String value = this.getValue();
        if (!StringUtils.isNullOrEmpty(value) && value.matches(PRINT_REGEX)) {
            return Expression.PRINT_NAME;
        } else if (!StringUtils.isNullOrEmpty(value) && value.matches(DATAWRITE_REGEX)) {
            return Expression.DATAWRITE_NAME;
        } else if (!StringUtils.isNullOrEmpty(value) && value.matches(DATAREFRESH_REGEX)) {
            return Expression.DATAREFRESH_NAME;
        } else if (!StringUtils.isNullOrEmpty(value) && value.matches(DATADELETE_REGEX)) {
            return Expression.DATADELETE_NAME;
        }
        return Expression.TAG_NAME;
    }

    @JsonIgnore
    public String getValueInner() {
        String tagName = getTagName();
        String value = getValue();
        if (Expression.PRINT_NAME.equals(tagName)) {
            return value.substring(Expression.PRINT_REGEX.length() - 6, value.length() - 2);
        } else if (Expression.DATAWRITE_NAME.equals(tagName)) {
            return value.substring(Expression.DATAWRITE_REGEX.length() - 5, value.length() - 1);
        } else if (Expression.DATAREFRESH_NAME.equals(tagName)) {
            return value.substring(Expression.DATAREFRESH_REGEX.length() - 5, value.length() - 1);
        } else if (Expression.DATADELETE_NAME.equals(tagName)) {
            return value.substring(Expression.DATADELETE_REGEX.length() - 5, value.length() - 1);
        } else {
            return value;
        }
    }

    public static RuleElement convertFromBlockly(BlocklyBlock block) {
        Expression expression = new Expression();
        expression.convertFromBlocklyInternal(block);

        return expression;
    }

    protected void convertFromBlocklyInternal(BlocklyBlock block) {
        setId(block.getId());
        setX(block.getX());
        setY(block.getY());

        if (PRINT_NAME.equals(block.getType())) {
            setPrintText(block.getFieldValue(Expression.TEXT_FIELD_NAME).orElse(""));
        } else if (DATAWRITE_NAME.equals(block.getType())) {
            setDataWriteExp(block.getFieldValue(Expression.TEXT_FIELD_NAME).orElse(""));
        } else if (DATAREFRESH_NAME.equals(block.getType())) {
            setDataRefreshExp(block.getFieldValue(Expression.TEXT_FIELD_NAME).orElse(""));
        } else if (DATADELETE_NAME.equals(block.getType())) {
            setDataDeleteExp(block.getFieldValue(Expression.TEXT_FIELD_NAME).orElse(""));
        } else {
            setValue(block.getFieldValue(Expression.VALUE_FIELD_NAME).orElse(""));
        }
    }

    private void setPrintText(String text) {
        setValue(String.format("RULE.pl.fhframework.core.rules.builtin.Print.print(\'%s\')", text));
    }

    private void setDataWriteExp(String text) {
        setValue(String.format("RULE.pl.fhframework.core.rules.builtin.DataAccessUtils.storeWrite(%s)", text));
    }

    private void setDataRefreshExp(String text) {
        setValue(String.format("RULE.pl.fhframework.core.rules.builtin.DataAccessUtils.storeRefresh(%s)", text));
    }

    private void setDataDeleteExp(String text) {
        setValue(String.format("RULE.pl.fhframework.core.rules.builtin.DataAccessUtils.storeDelete(%s)", text));
    }

    @Override
    public void processValueChange(String name, String value) {
        if (name.equals(VALUE_FIELD_NAME)) {
            String tagName = getTagName();
            if (tagName.equals(DATAWRITE_NAME)) {
                this.setDataWriteExp(value);
            } else if (tagName.equals(DATAREFRESH_NAME)) {
                this.setDataRefreshExp(value);
            } else if (tagName.equals(DATADELETE_NAME)) {
                this.setDataDeleteExp(value);
            } else {
                this.setValue(value);
            }
        } else if (name.equals(TEXT_FIELD_NAME)) {
            this.setPrintText(value);
        }
    }

    @Override
    public Map<String, String> getFieldsConverted() {
        Map<String, String> result = new HashMap<>();
        result.put(VALUE_FIELD_NAME, getValue() == null ? "" : getValue());
        return result;
    }

    public static Expression of(String value) {
        Expression expression = new Expression();
        expression.setValue(value);

        return expression;
    }
}