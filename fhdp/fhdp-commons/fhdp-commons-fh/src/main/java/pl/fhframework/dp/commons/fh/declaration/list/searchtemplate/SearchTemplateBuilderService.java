package pl.fhframework.dp.commons.fh.declaration.list.searchtemplate;

import org.springframework.stereotype.Service;
import pl.fhframework.dp.commons.fh.dataProviders.NameValueItem;
import pl.fhframework.dp.transport.searchtemplate.SearchTemplateForQuery;
import pl.fhframework.core.uc.IFormUseCaseContext;
import pl.fhframework.model.PresentationStyleEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SearchTemplateBuilderService {

    /**
     * Buduje obiekt opisujący szablon wyszukiwania na bazie wierszy wyklikanych w GUI
     * @param rows - wiersz
     * @return
     */
    public List<SearchTemplateForQuery> prepareFilterDefinitionForSearch(List<SearchTemplateBuilderModel.ConditionRow> rows) {
        List<SearchTemplateForQuery> result = new ArrayList<>();
        for (int idx = 0; idx < rows.size(); idx++) {
            SearchTemplateBuilderModel.ConditionRow cr = rows.get(idx);
            if (cr.getRowType() == null || cr.getRowType() == RowTypeEnum.ONLY_CMDS) {
                continue;
            }
            if (cr.getRowType() == RowTypeEnum.BRACKET_START) {
                SearchTemplateForQuery fd = new SearchTemplateForQuery();
                fd.setType(SearchTemplateForQuery.Type.BRACKETS);
                fd.setLogicalCondition(cr.getLogicalCondition());
                int stopIdx = rows.indexOf(cr.getConnectedRow());
                fd.setInner(prepareFilterDefinitionForSearch(rows.subList(idx + 1, stopIdx - 1)));
                idx = stopIdx + 1;
                result.add(fd);
            } else if (cr.getRowType() == RowTypeEnum.CONDITION) {
                SearchTemplateForQuery fd = new SearchTemplateForQuery();
                fd.setType(SearchTemplateForQuery.Type.CONDITION);
                fd.setLogicalCondition(cr.getLogicalCondition());
                fd.setColumnName(cr.getColumn().getField());
                fd.setOperator(cr.getOperator());
                switch (cr.getColumn().getColumnType()) {
                    case DATE:
                        fd.getValues().add(cr.getConditionDateValue());
                        break;
                    case DATETIME:
                        fd.getValues().add(cr.getConditionDateTimeValue());
                        break;
                    case NUMBER:
                        if (cr.getColumn().isDecimal()) {
                            fd.getValues().add(cr.getConditionDecimalValue());
                        } else {
                            fd.getValues().add(cr.getConditionIntegerValue());
                        }
                        break;
                    case TEXT:
                        fd.getValues().add(cr.getConditionTextValue());
                        break;
                    case ENUM:
                        if (!cr.getColumn().isMultiple()) {
                            fd.getValues().add(cr.getConditionSelectValue() == null ? null : cr.getConditionSelectValue().getTargetValue());
                        } else {
                            if (cr.getConditionComboValues() != null) {
                                fd.setValues(cr.getConditionComboValues().stream().map(NameValueItem::getTargetValue).collect(Collectors.toList()));
                            }
                        }
                        break;
                    case DICTIONARY:
                            fd.getValues().add(cr.getPozycjaSlownikaType()== null ? null : cr.getPozycjaSlownikaType().getKod());

                }
                result.add(fd);
            }
        }
        return result;
    }

    public boolean validate(SearchTemplateBuilderModel model, IFormUseCaseContext useCaseContext) {
        for (SearchTemplateBuilderModel.ConditionRow row : model.getConditionRows()) {
            if (!validateRow(row, useCaseContext)) {
                return false;
            }
        }

        return !useCaseContext.getUserSession().getValidationResults().areAnyValidationMessages();
    }

    public boolean validateRow(SearchTemplateBuilderModel.ConditionRow row, IFormUseCaseContext useCaseContext) {
        if (row.getRowType() == RowTypeEnum.BRACKET_START) {
            if (row.getLogicalCondition() == null) {
                reportValidationError(row, "logicalCondition", " - pole nie może być puste",useCaseContext);
                return false;
            }
            return true;
        } else if (row.getRowType() == RowTypeEnum.BRACKET_STOP || row.getRowType() == RowTypeEnum.ONLY_CMDS) {
            return true;
        }
        //RowTypeEnum.CONDITION
        if (row.getLogicalCondition() == null) {
            reportValidationError(row, "logicalCondition", " - pole nie może być puste", useCaseContext);
        }
        if (row.getColumn() == null) {
            reportValidationError(row, "column", " - pole nie może być puste", useCaseContext);
        }
        if (row.getOperator() == null) {
            reportValidationError(row, "operator", " - pole nie może być puste", useCaseContext);
        }
        if (row.getColumn() != null && row.getColumn().getColumnType() != null) {
            switch (row.getColumn().getColumnType()) {
                case TEXT:
                    if (row.getConditionTextValue() == null || row.getConditionTextValue().isEmpty()) {
                        reportValidationError(row, "conditionTextValue", " - pole nie może być puste", useCaseContext);
                    }
                    break;
                case DATE:
                    if (row.getConditionDateValue() == null) {
                        reportValidationError(row, "conditionDateValue", " - pole nie może być puste", useCaseContext);
                    }
                    break;
                case DATETIME:
                    if (row.getConditionDateTimeValue() == null) {
                        reportValidationError(row, "conditionDateTimeValue", " - pole nie może być puste", useCaseContext);
                    }
                    break;
                case NUMBER:
                    if (row.getColumn().isDecimal() && row.getConditionDecimalValue() == null) {
                        reportValidationError(row, "conditionDecimalValue", " - pole nie może być puste", useCaseContext);
                    } else if (!row.getColumn().isDecimal() && row.getConditionIntegerValue() == null) {
                        reportValidationError(row, "conditionIntegerValue", " - pole nie może być puste", useCaseContext);
                    }
                    break;
                case ENUM:
                    if (!row.getColumn().isMultiple() && row.getConditionSelectValue() == null) {
                        reportValidationError(row, "conditionSelectValue", " - pole nie może być puste", useCaseContext);
                    } else if (row.getColumn().isMultiple() && (row.getConditionComboValues() == null || row.getConditionComboValues().isEmpty())) {
                        reportValidationError(row, "conditionComboValues", " - pole nie może być puste", useCaseContext);
                    }
                    break;
                case DICTIONARY:
                    if(row.getPozycjaSlownikaType() == null) {
                        reportValidationError(row, "pozycjaSlownikaType", " - pole nie może być puste", useCaseContext);
                    }

            }
        }

        return !useCaseContext.getUserSession().getValidationResults().areAnyValidationMessages();
    }

    private void reportValidationError(Object parent, String attributeName, String message, IFormUseCaseContext useCaseContext ) {
        useCaseContext.getUserSession().getValidationResults().addCustomMessage(parent, attributeName, message, PresentationStyleEnum.ERROR);
    }
}
