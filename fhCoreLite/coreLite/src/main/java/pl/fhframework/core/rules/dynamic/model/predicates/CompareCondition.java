package pl.fhframework.core.rules.dynamic.model.predicates;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import pl.fhframework.aspects.snapshots.model.SkipSnapshot;
import pl.fhframework.core.rules.dynamic.blockly.BlocklyBlock;
import pl.fhframework.core.rules.dynamic.blockly.BlocklyField;
import pl.fhframework.core.rules.dynamic.model.Condition;
import pl.fhframework.core.rules.dynamic.model.Expression;
import pl.fhframework.core.rules.dynamic.model.RuleElement;
import pl.fhframework.core.rules.service.RuleConsts;
import pl.fhframework.core.util.StringUtils;

import javax.xml.bind.annotation.*;
import java.util.*;
import java.util.function.Function;

@Getter
@Setter
@XmlRootElement(name = "Compare", namespace = RuleConsts.RULE_XSD)
@XmlType(propOrder = {
        "left",
        "right",
        "when"
})
@XmlAccessorType(XmlAccessType.FIELD)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class CompareCondition extends DefinedCondition {
    @XmlTransient
    @JsonIgnore
    public static final String TAG_NAME = "CompareBlock";

    @XmlTransient
    @JsonIgnore
    public static final String TAG_STRING_NAME = "StringCompareBlock";

    @XmlTransient
    @JsonIgnore
    public static final String TAG_EMPTY_NAME = "CheckEmptinessBlock";

    @XmlTransient
    @JsonIgnore
    public static final String TAG_SPATIAL_NAME = "SpatialCompareBlock";

    @XmlTransient
    @JsonIgnore
    public static final String TAG_DISTANCE_NAME = "DistanceBlock";

    @XmlTransient
    @JsonIgnore
    public static final String OPERATOR_FIELD_NAME = "operator";

    @XmlTransient
    @JsonIgnore
    public static final String LEFT_FIELD_NAME = "left";

    @XmlTransient
    @JsonIgnore
    public static final String RIGHT_FIELD_NAME = "right";

    @XmlTransient
    @JsonIgnore
    public static final String IGNORECASE_FIELD_NAME = "ignoreCase";

    @XmlTransient
    @JsonIgnore
    public static final String WHEN_FIELD_NAME = "when";

    @XmlTransient
    @JsonIgnore
    public static final String DISTANCE_FIELD_NAME = "distance";

    @XmlAttribute
    private String operator;

    @XmlElement(name = "Left")
    private Expression left = new Expression();

    @XmlElement(name = "Right")
    private Expression right = new Expression();

    @XmlAttribute
    private String distance;

    @XmlElement(name = "When")
    private Condition when;

    @XmlAttribute
    private Boolean ignoreCase;

    @SkipSnapshot
    @XmlTransient
    @JsonIgnore
    private Object leftValue;

    @SkipSnapshot
    @XmlTransient
    @JsonIgnore
    private Object rightValue;

    @Override
    public BlocklyBlock convertToBlockly(Function<String, String> formatter) {
        BlocklyBlock block = new BlocklyBlock();
        block.setId(this.getOrGenerateId());

        CompareOperatorEnum operatorEnum = CompareOperatorEnum.fromString(this.getOperator());
        if (Arrays.asList(CompareOperatorEnum.IsNull, CompareOperatorEnum.IsNotNull, CompareOperatorEnum.IsEmpty, CompareOperatorEnum.IsNotEmpty).contains(operatorEnum)) {
            block.setType(CompareCondition.TAG_EMPTY_NAME);
        }
        else if (operatorEnum.isSpatial()) {
            block.setType(CompareCondition.TAG_SPATIAL_NAME);
        }
        else if (distance != null) {
            block.setType(CompareCondition.TAG_DISTANCE_NAME);
        }
        else if (this.getIgnoreCase() != null) {
            block.setType(CompareCondition.TAG_STRING_NAME);
        }
        else {
            block.setType(CompareCondition.TAG_NAME);
        }
        block.setX(this.getX());
        block.setY(this.getY());

        List<BlocklyField> fields = new ArrayList<>();
        fields.add(new BlocklyField(CompareCondition.LEFT_FIELD_NAME, formatter.apply(this.getLeft().getValue())).setEditorType(BlocklyField.EditorType.COMBO));
        fields.add(new BlocklyField(CompareCondition.OPERATOR_FIELD_NAME, this.getOperator()).setEditorType(BlocklyField.EditorType.FIXED).setFieldType(CompareOperatorEnum.class));
        if (this.getRight() != null && !TAG_EMPTY_NAME.equals(block.getType())) {
            fields.add(new BlocklyField(CompareCondition.RIGHT_FIELD_NAME, formatter.apply(this.getRight().getValue())).setEditorType(BlocklyField.EditorType.COMBO));
        }
        if (this.getIgnoreCase() != null) {
            fields.add(new BlocklyField(CompareCondition.IGNORECASE_FIELD_NAME, this.getIgnoreCase().toString()).setEditorType(BlocklyField.EditorType.FIXED).setFieldType(Boolean.class));
        }
        if (this.distance != null) {
            fields.add(new BlocklyField(CompareCondition.DISTANCE_FIELD_NAME, this.getDistance()).setEditorType(BlocklyField.EditorType.COMBO).setFieldType(Double.class));
        }
        block.setFields(fields);

        addComplexValues(block, formatter);

        return block;
    }

    public static RuleElement convertFromBlockly(BlocklyBlock parsedBlock) {
        CompareCondition compareCondition = new CompareCondition();
        compareCondition.setId(parsedBlock.getId());
        compareCondition.setX(parsedBlock.getX());
        compareCondition.setY(parsedBlock.getY());

        Optional<String> operator = parsedBlock.getFieldValue(CompareCondition.OPERATOR_FIELD_NAME);
        operator.ifPresent(compareCondition::setOperator);

        Optional<String> leftValue = parsedBlock.getFieldValue(CompareCondition.LEFT_FIELD_NAME);
        leftValue.ifPresent(compareCondition.getLeft()::setValue);

        Optional<String> rightValue = parsedBlock.getFieldValue(CompareCondition.RIGHT_FIELD_NAME);
        rightValue.ifPresent(compareCondition.getRight()::setValue);

        Optional<String> ignoreCase = parsedBlock.getFieldValue(CompareCondition.IGNORECASE_FIELD_NAME);
        ignoreCase.ifPresent(val -> compareCondition.setIgnoreCase(StringUtils.equalsIgnoreCase("true", val)));

        Optional<String> distance = parsedBlock.getFieldValue(CompareCondition.DISTANCE_FIELD_NAME);
        distance.ifPresent(compareCondition::setDistance);
        if (!distance.isPresent() && Objects.equals(TAG_DISTANCE_NAME, parsedBlock.getType())) {
            compareCondition.setDistance("");
        }

        return compareCondition;
    }

    @Override
    public void processValueChange(String name, String value) {
        switch (name) {
            case CompareCondition.OPERATOR_FIELD_NAME:
                this.setOperator(value);
                break;
            case CompareCondition.LEFT_FIELD_NAME:
                this.getLeft().setValue(value);
                break;
            case CompareCondition.RIGHT_FIELD_NAME:
                this.getRight().setValue(value);
                break;
            case CompareCondition.IGNORECASE_FIELD_NAME:
                this.setIgnoreCase(StringUtils.equalsIgnoreCase("true", value));
                break;
            case CompareCondition.DISTANCE_FIELD_NAME:
                this.setDistance(StringUtils.nullToEmpty(value));
                break;
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


    public static CompareCondition of(String leftPath, CompareOperatorEnum operator) {
        CompareCondition compareCondition = new CompareCondition();
        compareCondition.setLeft(Expression.of(leftPath));
        compareCondition.setOperator(operator.getOperator());

        return compareCondition;
    }

    public static CompareCondition of(String leftPath, CompareOperatorEnum operator, Object rightValue) {
        return CompareCondition.of(leftPath, operator, rightValue,null, true, null);
    }

    public static CompareCondition of(String leftPath, CompareOperatorEnum operator, Object rightValue, Boolean ignoreCase) {
        return CompareCondition.of(leftPath, operator, rightValue, ignoreCase, true, null);
    }

    public static CompareCondition of(String leftPath, CompareOperatorEnum operator, Object rightValue, Boolean ignoreCase, boolean isInputParam, Double distance) {
        CompareCondition compareCondition = new CompareCondition();
        compareCondition.setLeft(Expression.of(clearString(leftPath)));
        compareCondition.setOperator(operator.getOperator());
        if (isInputParam) {
            compareCondition.setRightValue(clearString(rightValue));
        }
        else {
            compareCondition.setRight(Expression.of(clearString((String) rightValue)));
        }
        compareCondition.setIgnoreCase(ignoreCase);
        if (distance != null) {
            compareCondition.setDistance(distance.toString());
        }

        return compareCondition;
    }

    private static <T> T clearString(T input) {
        if (String.class.isInstance(input)) {
            if (StringUtils.isNullOrEmpty((String) input)) {
                return null;
            }
        }
        return input;
    }
}