package pl.fhframework.dp.transport.searchtemplate;



import java.util.ArrayList;
import java.util.List;

/**
 * Klasa pośrednicząca w przekazywaniu warunków określonych w filtrze do backendu realizującego rzeczywiste wyszukiwanie
 * Warunki RHS jako obiekty
 */

public class SearchTemplateForQuery {
    public enum Type {CONDITION, BRACKETS}
    private Type type;
    //AND/OR
    private LogicalCondition logicalCondition;
    //nazwa kolumny
    private String columnName;
    //EQUAL, NOT_EQUAL, LTE, GT, ....
    private OperatorType operator;
    //RHS value
    private List<Object> values = new ArrayList<>();
    //lista wezłów wewnętrznych, na poziomie GUI odpowiada ograniczeniu wyrażenia nawiasami
    private List<SearchTemplateForQuery> inner = new ArrayList<>();

    @Override
    public String toString() {
        return "SearchTemplateForQuery{" +
                "type=" + type +
                ", logicalCondition=" + logicalCondition +
                ", columnName='" + columnName + '\'' +
                ", operator=" + operator +
                ", values=" + values +
                ", inner=" + inner +
                '}';
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public LogicalCondition getLogicalCondition() {
        return logicalCondition;
    }

    public void setLogicalCondition(LogicalCondition logicalCondition) {
        this.logicalCondition = logicalCondition;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public OperatorType getOperator() {
        return operator;
    }

    public void setOperator(OperatorType operator) {
        this.operator = operator;
    }

    public List<Object> getValues() {
        return values;
    }

    public void setValues(List<Object> values) {
        this.values = values;
    }

    public List<SearchTemplateForQuery> getInner() {
        return inner;
    }

    public void setInner(List<SearchTemplateForQuery> inner) {
        this.inner = inner;
    }
}

