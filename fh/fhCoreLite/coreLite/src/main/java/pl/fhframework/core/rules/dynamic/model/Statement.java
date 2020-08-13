package pl.fhframework.core.rules.dynamic.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.Setter;
import pl.fhframework.aspects.snapshots.model.ISnapshotEnabled;
import pl.fhframework.aspects.snapshots.model.SkipSnapshot;
import pl.fhframework.core.rules.dynamic.blockly.BlocklyBlock;
import pl.fhframework.core.rules.dynamic.blockly.BlocklyValue;
import pl.fhframework.core.rules.dynamic.model.dataaccess.*;
import pl.fhframework.core.rules.dynamic.model.predicates.AndCondition;
import pl.fhframework.core.rules.dynamic.model.predicates.CompareCondition;
import pl.fhframework.core.rules.dynamic.model.predicates.NotCondition;
import pl.fhframework.core.rules.dynamic.model.predicates.OrCondition;
import pl.fhframework.core.rules.service.RuleConsts;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Getter
@Setter
@XmlRootElement(name = "Statement", namespace = RuleConsts.RULE_XSD)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({Expression.class, BooleanExpression.class, If.class, Else.class, Then.class,
        For.class, ForEach.class, While.class, DoWhile.class,
        Var.class, Const.class, Init.class, EmptyStatement.class, ValidationMessage.class,
        Break.class, Return.class, Continue.class,
        From.class, Join.class, Filter.class, SortBy.class, SortField.class,  Offset.class, Limit.class, Count.class,
        AndCondition.class, OrCondition.class, NotCondition.class, CompareCondition.class})
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "statementType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Expression.class, name = "expression"),
        @JsonSubTypes.Type(value = BooleanExpression.class, name = "booleanExpression"),
        @JsonSubTypes.Type(value = If.class, name = "if"),
        @JsonSubTypes.Type(value = Else.class, name = "else"),
        @JsonSubTypes.Type(value = Then.class, name = "then"),
        @JsonSubTypes.Type(value = For.class, name = "for"),
        @JsonSubTypes.Type(value = ForEach.class, name = "forEach"),
        @JsonSubTypes.Type(value = While.class, name = "while"),
        @JsonSubTypes.Type(value = DoWhile.class, name = "doWhile"),
        @JsonSubTypes.Type(value = Var.class, name = "var"),
        @JsonSubTypes.Type(value = Const.class, name = "const"),
        @JsonSubTypes.Type(value = Init.class, name = "init"),
        @JsonSubTypes.Type(value = ValidationMessage.class, name = "validationMessage"),
        @JsonSubTypes.Type(value = EmptyStatement.class, name = "emptyStatement"),
        @JsonSubTypes.Type(value = Break.class, name = "break"),
        @JsonSubTypes.Type(value = Return.class, name = "return"),
        @JsonSubTypes.Type(value = From.class, name = "from"),
        @JsonSubTypes.Type(value = Join.class, name = "join"),
        @JsonSubTypes.Type(value = SortBy.class, name = "sortBy"),
        @JsonSubTypes.Type(value = SortField.class, name = "sortField"),
        @JsonSubTypes.Type(value = Offset.class, name = "offset"),
        @JsonSubTypes.Type(value = Limit.class, name = "limit"),
        @JsonSubTypes.Type(value = Count.class, name = "count"),
        @JsonSubTypes.Type(value = AndCondition.class, name = "andCondition"),
        @JsonSubTypes.Type(value = OrCondition.class, name = "orCondition"),
        @JsonSubTypes.Type(value = NotCondition.class, name = "notCondition"),
        @JsonSubTypes.Type(value = CompareCondition.class, name = "compareCondition"),
})
public abstract class Statement implements RuleElement, ISnapshotEnabled, Cloneable, Serializable {
    @SkipSnapshot
    @XmlTransient
    @JsonIgnore
    private String id;

    @XmlAttribute
    @JsonIgnore
    private Double x;

    @XmlAttribute
    @JsonIgnore
    private Double y;

    @SkipSnapshot
    @XmlTransient
    @JsonIgnore
    private RuleElement parent;

    @SkipSnapshot
    @XmlTransient
    @JsonIgnore
    private StatementsList surroundParent;

    @XmlTransient
    @SkipSnapshot
    @JsonIgnore
    private boolean invalid;

    public void processValueChange(String name, String value) {
    }

    public void processValueChange(String name, RuleElement value) {
    }

    @Override
    @JsonIgnore
    public Map<String, RuleElement> getComplexValues() {
        return Collections.emptyMap();
    }

    @Override
    @JsonIgnore
    public Class getInputType(String name) {
        return Statement.class;
    }

    protected void addComplexValues(BlocklyBlock block, Function<String, String> formatter) {
        List<BlocklyValue> values = new ArrayList<>();

        getComplexValues().forEach((inputName, ruleElement) -> {
                values.add(new BlocklyValue(inputName, ruleElement.convertToBlockly(formatter)));
        });

        block.setValues(values);

    }

}
