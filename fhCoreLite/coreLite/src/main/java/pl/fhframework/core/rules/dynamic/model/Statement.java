package pl.fhframework.core.rules.dynamic.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
public abstract class Statement implements RuleElement, ISnapshotEnabled, Cloneable, Serializable {
    @SkipSnapshot
    @XmlTransient
    @JsonIgnore
    private String id;

    @XmlAttribute
    private Double x;

    @XmlAttribute
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