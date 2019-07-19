package pl.fhframework.core.rules.dynamic.model.predicates;

import lombok.Getter;

import java.util.Arrays;

/**
 * Created by pawel.ruta on 2017-08-24.
 */
public enum  CompareOperatorEnum {
    Equal("equal"),
    NotEqual("notEqual"),
    LessThan("lessThan"),
    LessOrEqual("lessOrEqual"),
    GreaterThan("greaterThan"),
    GreaterOrEqual("greaterOrEqual"),
    In("in"),
    NotIn("notIn"),
    IsNull("isNull", true),
    IsNotNull("isNotNull", true),
    IsEmpty("isEmpty", true),
    IsNotEmpty("isNotEmpty", true),
    StartsWith("startsWith"),
    NotStartsWith("notStartsWith"),
    EndsWith("endsWith"),
    NotEndsWith("notEndsWith"),
    Contains("contains"),
    NotContains("notContains"),
    MemberOf("memberOf"),

    Intersects("intersects"),
    Touches("touches"),
    Crosses("crosses"),
    SpatialContains("spatialContains"),
    Disjoint("disjoint"),

    ;

    @Getter
    private String operator;

    @Getter
    private boolean emptinessCheck;

    CompareOperatorEnum(String operator) {
        this.operator = operator;
    }

    CompareOperatorEnum(String operator, boolean emptinessCheck) {
        this(operator);
        this.emptinessCheck = emptinessCheck;
    }

    public boolean isNegation() {
        return Arrays.asList(NotEqual, NotIn, IsNotNull, IsNotEmpty, NotStartsWith, NotEndsWith, NotContains).contains(this);
    }

    public static CompareOperatorEnum fromString(String operator) {
        return Arrays.stream(CompareOperatorEnum.values()).filter(operatorEnum -> operatorEnum.getOperator().equals(operator)).findAny().orElse(null);
    }

    public static CompareOperatorEnum[] generalOperators() {
        return new CompareOperatorEnum[]{Equal, NotEqual, LessThan, LessOrEqual, GreaterThan, GreaterOrEqual, In, NotIn};
    }

    public static CompareOperatorEnum[] stringOperators() {
        return new CompareOperatorEnum[]{Equal, NotEqual, StartsWith, NotStartsWith, EndsWith, NotEndsWith, Contains, NotContains};
    }

    public static CompareOperatorEnum[] emptinessOperators() {
        return new CompareOperatorEnum[]{IsNull, IsNotNull, IsEmpty, IsNotEmpty};
    }

    public static CompareOperatorEnum[] spatialOperators() {
        return new CompareOperatorEnum[]{Intersects, Touches, Crosses, SpatialContains, Disjoint};
    }

    public static CompareOperatorEnum[] distanceOperators() {
        return new CompareOperatorEnum[]{Equal, NotEqual, LessThan, LessOrEqual, GreaterThan, GreaterOrEqual};
    }

    public boolean isSpatial() {
        return Arrays.asList(spatialOperators()).contains(this);
    }
    @Override
    public String toString() {
        return getOperator();
    }
}
