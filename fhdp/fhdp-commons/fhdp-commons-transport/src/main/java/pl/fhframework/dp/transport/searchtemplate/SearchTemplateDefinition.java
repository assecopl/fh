package pl.fhframework.dp.transport.searchtemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Klasa pośrednicząca w zapisywaniu/wczytywaniu warunków określonych w filtrze do trwałego magazynu danych
 * Struktura warunków jest płaska, warunki RHS jako stringi
 */
public class SearchTemplateDefinition implements Cloneable {
    public enum Type {CONDITION, BRACKET_START, BRACKET_STOP}
    private Type type;
    // AND/OR
    private String logicalCondition;
    //nazwa kolumny
    private String columnName;
    //EQUAL, NOT_EQUAL, LTE, GT, ....
    private String operator;
    //RHS value
    private List<String> values = new ArrayList<>();

    private DictionaryItemType dictionaryItemType;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getLogicalCondition() {
        return logicalCondition;
    }

    public void setLogicalCondition(String logicalCondition) {
        this.logicalCondition = logicalCondition;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public List<String> getValues() {
        return values;
    }

    public DictionaryItemType getPozycjaSlownikaType() {
        return dictionaryItemType;
    }

    public void setPozycjaSlownikaType(DictionaryItemType dictionaryItemType) {
        this.dictionaryItemType = dictionaryItemType;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    @Override
    public SearchTemplateDefinition clone() {
        try {
            SearchTemplateDefinition clone = (SearchTemplateDefinition) super.clone();
            clone.setValues(values == null ? null : new ArrayList<>(values));
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
