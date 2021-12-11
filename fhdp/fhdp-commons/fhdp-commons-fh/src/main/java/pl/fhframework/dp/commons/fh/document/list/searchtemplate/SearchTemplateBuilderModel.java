package pl.fhframework.dp.commons.fh.document.list.searchtemplate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.fhframework.dp.commons.fh.dataProviders.NameValueItem;
import pl.fhframework.dp.transport.searchtemplate.LogicalCondition;
import pl.fhframework.dp.transport.searchtemplate.OperatorType;
import pl.fhframework.dp.transport.searchtemplate.PozycjaSlownikaType;
import pl.fhframework.dp.transport.searchtemplate.SearchTemplateDto;
import pl.fhframework.model.forms.AccessibilityEnum;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class SearchTemplateBuilderModel {

    private List<ConditionRow> conditionRows = new ArrayList<>();
    private List<LogicalCondition> logicalConditions = Arrays.asList(LogicalCondition.values());
    private List<Column> columns = new ArrayList<>();
    //filtr wybrany przez użytkownika w combo
    private SearchTemplateDto userDefinedSelectedFilter;
    //lista filtrów dla użytkownika i obiektu
    private List<SearchTemplateDto> userDefinedFilters = new ArrayList<>();
    //filtr wczytany
    private SearchTemplateDto sourceFilter;
    private ISearchTemplateCriteriaProvider searchCriteriaProvider;
    //nazwa komponentu do zapisu w db i identyfikacji powiązania szablonu wyszukiwania z obiektem którego dotyczy
    private String componentName;
    //data ważności dla słowników
    LocalDate referenceDate = LocalDate.now();

    public SearchTemplateBuilderModel(ISearchTemplateCriteriaProvider searchCriteriaProvider) {
        this.searchCriteriaProvider = searchCriteriaProvider;
        init();
    }

    private void init() {
        setColumns(searchCriteriaProvider.getAvailableColumns());
    }

    public ConditionRow createCommandRow() {
        ConditionRow row = new ConditionRow();
        row.setRowType(RowTypeEnum.ONLY_CMDS);
        return row;
    }

    public ConditionRow createConditionRow() {
        ConditionRow row = new ConditionRow();
        row.setRowType(RowTypeEnum.CONDITION);
        row.setLogicalCondition(LogicalCondition.AND);
        return row;
    }

    public ConditionRow createBreacketStartRow() {
        ConditionRow row = new ConditionRow();
        row.setRowType(RowTypeEnum.BRACKET_START);
        row.setLogicalCondition(LogicalCondition.AND);
        row.setConditionTextValue("(");
        return row;
    }

    public ConditionRow createBreacketStopRow() {
        ConditionRow row = new ConditionRow();
        row.setRowType(RowTypeEnum.BRACKET_STOP);
        row.setConditionTextValue(")");
        return row;
    }

    public void prepareEmptyFormModel() {
        ConditionRow row = createCommandRow();
        conditionRows.add(row);
    }

    public List<ConditionRow> getConditionRowsInsideBrackets(ConditionRow bracketStartRow) {
        int startIdx = conditionRows.indexOf(bracketStartRow);
        int stopIdx = conditionRows.indexOf(bracketStartRow.connectedRow);
        return conditionRows.subList(startIdx, stopIdx + 1);
    }

    public void clearFormModel() {
        conditionRows.clear();
    }

    public boolean containsUserChanges() {
        return conditionRows.size() > 1;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConditionRow {
        private int level = 0;
        private ConditionRow connectedRow;
        private RowTypeEnum rowType;
        private LogicalCondition logicalCondition;
        private Column column;
        private List<OperatorType> operators = new ArrayList<>();
        private OperatorType operator;
        private String conditionTextValue;
        private Long conditionIntegerValue;
        private BigDecimal conditionDecimalValue;
        private NameValueItem conditionSelectValue;
        private List<NameValueItem> conditionComboValues = new ArrayList<>();
        private LocalDate conditionDateValue;
        private LocalDateTime conditionDateTimeValue;
        private PozycjaSlownikaType pozycjaSlownikaType = new PozycjaSlownikaType();
        private Map<String, Object> dictParameters;
        private LocalDate referenceDate = LocalDate.now();

        public AccessibilityEnum getAvailabilityOperator() {
            if (rowType == RowTypeEnum.ONLY_CMDS || rowType == RowTypeEnum.BRACKET_STOP) {
                return AccessibilityEnum.HIDDEN;
            }
            return AccessibilityEnum.EDIT;
        }

        public AccessibilityEnum getAvailabilityColumnName() {
            if (rowType == RowTypeEnum.CONDITION) {
                return AccessibilityEnum.EDIT;
            }
            return AccessibilityEnum.HIDDEN;
        }

        public AccessibilityEnum getAvailabilityColumnCondition() {
            if (rowType == RowTypeEnum.CONDITION) {
                return AccessibilityEnum.EDIT;
            }
            return AccessibilityEnum.HIDDEN;
        }

        public AccessibilityEnum getValuePaddingAvailability(int idx) {
            return idx < level ? AccessibilityEnum.EDIT : AccessibilityEnum.HIDDEN;
        }

        public AccessibilityEnum getAvailabilityInputText() {
            return column != null && column.getColumnType() == ColumnType.TEXT ? AccessibilityEnum.EDIT : AccessibilityEnum.HIDDEN;
        }

        public AccessibilityEnum getAvailabilityInputNumber() {
            return column != null && column.getColumnType() == ColumnType.NUMBER && !column.isDecimal() ? AccessibilityEnum.EDIT : AccessibilityEnum.HIDDEN;
        }

        public AccessibilityEnum getAvailabilityInputNumberDecimal() {
            return column != null && column.getColumnType() == ColumnType.NUMBER && column.isDecimal()? AccessibilityEnum.EDIT : AccessibilityEnum.HIDDEN;
        }

        public AccessibilityEnum getAvailabilitySelectOne() {
            return column != null && column.getColumnType() == ColumnType.ENUM && !column.isMultiple() ? AccessibilityEnum.EDIT : AccessibilityEnum.HIDDEN;
        }

        public AccessibilityEnum getAvailabilityCombo() {
            return column != null && column.getColumnType() == ColumnType.ENUM && column.isMultiple() ? AccessibilityEnum.EDIT : AccessibilityEnum.HIDDEN;
        }

        public AccessibilityEnum getAvailabilityDictionary(){
            return column != null && column.getColumnType() == ColumnType.DICTIONARY && !column.isMultiple() ? AccessibilityEnum.EDIT : AccessibilityEnum.HIDDEN;
        }


        public AccessibilityEnum getAvailabilityInputDate() {
            return column != null && column.getColumnType() == ColumnType.DATE ? AccessibilityEnum.EDIT : AccessibilityEnum.HIDDEN;
        }

        public AccessibilityEnum getAvailabilityInputTimestamp() {
            return column != null && column.getColumnType() == ColumnType.DATETIME ? AccessibilityEnum.EDIT : AccessibilityEnum.HIDDEN;
        }

        public AccessibilityEnum getAvailabilityOutputLabel() {
            if (rowType == RowTypeEnum.BRACKET_START || rowType == RowTypeEnum.BRACKET_STOP) {
                return AccessibilityEnum.VIEW;
            }
            return AccessibilityEnum.HIDDEN;
        }

        public AccessibilityEnum getActionAvailabilityDeleteCondition() {
            if (rowType == RowTypeEnum.ONLY_CMDS || rowType == RowTypeEnum.BRACKET_STOP) {
                return AccessibilityEnum.HIDDEN;
            }
            return AccessibilityEnum.EDIT;
        }

        public AccessibilityEnum getActionAvailabilityAddCondition() {
            if (rowType == RowTypeEnum.ONLY_CMDS) {
                return AccessibilityEnum.EDIT;
            }
            return AccessibilityEnum.HIDDEN;
        }

        public AccessibilityEnum getActionAvailabilityAddBracket() {
            if (rowType == RowTypeEnum.ONLY_CMDS) {
                return AccessibilityEnum.EDIT;
            }
            return AccessibilityEnum.HIDDEN;
        }

    }
}
