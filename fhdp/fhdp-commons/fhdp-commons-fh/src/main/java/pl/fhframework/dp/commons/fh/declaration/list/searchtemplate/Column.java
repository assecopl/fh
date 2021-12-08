package pl.fhframework.dp.commons.fh.declaration.list.searchtemplate;


import lombok.Getter;
import lombok.Setter;
import pl.fhframework.dp.commons.fh.dataProviders.NameValueItem;
import pl.fhframework.dp.transport.searchtemplate.IDescription;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Setter
public class Column {
    private String field;
    private String i18N;
    private ColumnType columnType;
    private List<NameValueItem> availableValues;
    private boolean decimal;
    private boolean multiple;
    private Class fieldClass;
    Map<String, Object> dictParameters;



    private Column(String field, String i18N, boolean decimal, boolean multiple, Class fieldClass) {
        this(field, i18N, decimal, multiple, fieldClass, null);
    }

    private Column(String field, String i18N, boolean decimal, boolean multiple, Class fieldClass, Map<String, Object> dictParameters) {
        this.field = field;
        this.i18N = i18N;
        this.decimal = decimal;
        this.multiple = multiple;
        this.fieldClass = fieldClass;
        this.dictParameters = dictParameters;

    }

    /**
     * Builds simple text columns; allow input any text as search criteria
     * @param field field name to be searched in Elastic
     * @param i18N internalization key name
     * @return configured column
     */
    public static Column buildTextColumn(String field, String i18N){
        Column col = new Column(field, i18N, false, false, null);
        col.setColumnType(ColumnType.TEXT);
        return col;
    }

    /**
     * Builds date  column; allow input of date as search criteria
     * @param field field name to be searched in Elastic
     * @param i18N internalization key name
     * @return configured column
     */
    public static Column buildDateColumn(String field, String i18N){
        Column col = new Column(field, i18N, false, false, null);
        col.setColumnType(ColumnType.DATE);
        return col;
    }

    /**
     * Builds dateTime  column; allow input of date time as search criteria
     * @param field field name to be searched in Elastic
     * @param i18N internalization key name
     * @return configured column
     */
    public static Column buildDateTimeColumn(String field, String i18N){
        Column col = new Column(field, i18N, false, false, null);
        col.setColumnType(ColumnType.DATETIME);
        return col;
    }

    /**
     * Builds dictionary  column; allow input of dictionary elements as search criteria
     * @param field field name to be searched in Elastic
     * @param i18N internalization key name
     * @param dictParameters Map<String, Object> of parameters for DictionaryCombo  (eg. dict name and date)
     * @return configured column
     */
    public static Column buildDictColumn(String field, String i18N, Map<String, Object> dictParameters){
        Column col = new Column(field, i18N, false, false, null, dictParameters);
        col.columnType = ColumnType.DICTIONARY;
        return col;
    }

    /**
     * Builds combo column; allow choose of MULTIPLE combo elements as search criteria
     * @param field field name to be searched in Elastic
     * @param i18N internalization key name
     * @param enumClass EnumClass that will be displayed in combo
     * @return configured column
     */
    public static Column buildComboColumn(String field, String i18N, Class<? extends Enum> enumClass){
       Column col = new Column(field, i18N, false, true, enumClass);
       col.columnType = ColumnType.ENUM;
       col.setAvailableValues(getNameValueItemsFromEnum(col.getFieldClass()));
       return col;
    }

    /**
     * Builds selectOne column; allow choose of SINGLE  elements as search criteria
     * @param field field name to be searched in Elastic
     * @param i18N internalization key name
     * @param enumClass EnumClass that will be displayed in combo
     * @return configured column
     */
    public static Column buildSelectOneColumn(String field, String i18N, Class<? extends Enum> enumClass){
        Column col = new Column(field, i18N, false, false, enumClass);
        col.columnType = ColumnType.ENUM;
        col.setAvailableValues(getNameValueItemsFromEnum(col.getFieldClass()));
       return  col;
    }

    /**
     * Builds build decimal Number  column; allow input only decimals as search criteria
     * @param field field name to be searched in Elastic
     * @param i18N internalization key name
     * @return configured column
     */
    public static Column buildDecimalNumberColumn(String field, String i18N){
        Column col = new Column(field, i18N, true, false, null);
        col.columnType = ColumnType.NUMBER;
        return col;
    }

    /**
     * Builds Number  column; allow input only numbers as search criteria
     * @param field field name to be searched in Elastic
     * @param i18N internalization key name
     * @return configured column
     */
    public static Column buildNumberColumn(String field, String i18N){
        Column col = new Column(field, i18N, false, false, null);
        col.columnType = ColumnType.NUMBER;
        return col;
    }

    private static List<NameValueItem> getNameValueItemsFromEnum(Class fieldClass) {
        //TODO: type safety
        return (List<NameValueItem>) EnumSet.allOf(fieldClass)
                .stream()
                .map(item -> {
                    return new NameValueItem(
                            (Enum.valueOf(fieldClass, item.toString()).toString()),
                            ((IDescription) Enum.valueOf(fieldClass, item.toString())).getDescription());
                })
                .collect(Collectors.toList());
    }
}
