package pl.fhframework.dp.commons.fh.document.list.searchtemplate;


import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import pl.fhframework.dp.commons.fh.dataProviders.NameValueItem;
import pl.fhframework.dp.commons.fh.document.list.searchtemplate.name.ChooseSearchTemplateNameUC;
import pl.fhframework.dp.commons.fh.i18n.CommonsMessageHelper;
import pl.fhframework.dp.commons.fh.services.SearchTemplateService;
import pl.fhframework.dp.commons.fh.utils.FhUtils;
import pl.fhframework.dp.transport.searchtemplate.*;
import pl.fhframework.SessionManager;
import pl.fhframework.annotations.Action;
import pl.fhframework.annotations.composite.Composite;
import pl.fhframework.core.uc.IUseCaseSaveCancelCallback;
import pl.fhframework.event.EventRegistry;
import pl.fhframework.event.dto.NotificationEvent;
import pl.fhframework.model.forms.CompositeForm;
import pl.fhframework.model.forms.messages.Messages;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

@Composite(template = "SearchTemplateBuilderForm.frm")
public class SearchTemplateBuilderForm extends CompositeForm<SearchTemplateBuilderModel> {

    @Autowired
    private EventRegistry eventRegistry;
    @Autowired
    private SearchTemplateBuilderService searchTemplateBuilderService;
    @Autowired
    private SearchTemplateService searchCriteriaDtoService;
    @Autowired
    private Messages messages;
    @Autowired
    private CommonsMessageHelper commonsMessageHelper;

    private boolean validate() {
        boolean isValid = searchTemplateBuilderService.validate(getModel(), getAbstractUseCase());
        if(!isValid){
            messages.showError(SessionManager.getUserSession(), "Validation error!",() -> {
                getAbstractUseCase().getUserSession().getValidationResults().clearValidationErrors();
            });
        }
        return isValid;
    }

    @Override
    public void init() {
        super.init();
        getModel().setUserDefinedFilters(findTemplatesByNameAndUser());
    }

    @Action
    public void deleteContition(SearchTemplateBuilderModel.ConditionRow row) {
        if (row.getRowType() == RowTypeEnum.BRACKET_START) {
            List<SearchTemplateBuilderModel.ConditionRow> rowsInside = getModel().getConditionRowsInsideBrackets(row);
            if (rowsInside.size() > 3) {
                FhUtils.showConfirmDialogYesNo(commonsMessageHelper.getMessage("fhdp.common.confirm"),
                        commonsMessageHelper.getMessage("document.ct.searchCriteria.tab.template.confirmDialog.deleteParentBrackets"),
                        () -> getModel().getConditionRows().removeAll(rowsInside));
            } else {
                getModel().getConditionRows().removeAll(rowsInside);
            }
        } else if (row.getRowType() == RowTypeEnum.CONDITION) {
            getModel().getConditionRows().remove(row);
        } else {
            throw new IllegalStateException("Cannot delete:" + row.getRowType());
        }
    }

    @Action
    public void addContition(SearchTemplateBuilderModel.ConditionRow row) {
        SearchTemplateBuilderModel.ConditionRow newRow = getModel().createConditionRow();
        newRow.setLevel(row.getLevel() + 1);
        int idx = getModel().getConditionRows().indexOf(row);
        getModel().getConditionRows().add(idx, newRow);
    }

    @Action
    public void addBracket(SearchTemplateBuilderModel.ConditionRow row) {
        int level = row.getLevel() + 1;
        SearchTemplateBuilderModel.ConditionRow start = getModel().createBreacketStartRow();
        start.setLevel(level);
        SearchTemplateBuilderModel.ConditionRow stop = getModel().createBreacketStopRow();
        stop.setLevel(level);
        SearchTemplateBuilderModel.ConditionRow cmd = getModel().createCommandRow();
        cmd.setLevel(level);

        start.setConnectedRow(stop);
        stop.setConnectedRow(start);

        int idx = getModel().getConditionRows().indexOf(row);
        getModel().getConditionRows().add(idx, stop);
        getModel().getConditionRows().add(idx, cmd);
        getModel().getConditionRows().add(idx, start);
    }

    //Akcja na zmianie przeszukiwanego pola
    @Action
    public void columnChanged(SearchTemplateBuilderModel.ConditionRow row) {
        row.setOperators(getOperatorsForColumn(row.getColumn()));
        if (!row.getOperators().contains(row.getOperator())) {
            row.setOperator(null);
        }
        if (ColumnType.DICTIONARY.equals(row.getColumn().getColumnType())) {
            row.setDictParameters(row.getColumn().getDictParameters());
            row.setDictionaryItemType(null);
        }
    }

    private List<OperatorType> getOperatorsForColumn(Column column) {
        List<OperatorType> operators = new ArrayList<>();
        if (column != null && column.getColumnType() != null) {
            switch (column.getColumnType()) {
                case DATE:
                case DATETIME:
                case NUMBER:
                    operators.addAll(Arrays.asList(OperatorType.EQUAL, OperatorType.NOT_EQUAL, OperatorType.LT, OperatorType.LTE, OperatorType.GT, OperatorType.GTE));
                    break;
                case TEXT:
                    operators.addAll(Arrays.asList(OperatorType.EQUAL, OperatorType.NOT_EQUAL, OperatorType.LIKE, OperatorType.DONT_LIKE));
                    break;
                case ENUM:
                    if (column.isMultiple()) {
                        operators.addAll(Arrays.asList(OperatorType.IN, OperatorType.NOT_IN));
                    } else {
                        operators.addAll(Arrays.asList(OperatorType.EQUAL, OperatorType.NOT_EQUAL));
                    }
                    break;
                case DICTIONARY:
                    operators.addAll(Arrays.asList(OperatorType.EQUAL, OperatorType.NOT_EQUAL));
                    break;
                default:
                    break;
            }
        }
        return operators;
    }

    @Action
    public void onClearFilter() {
        getModel().clearFormModel();
        getModel().prepareEmptyFormModel();
        getModel().setSourceFilter(null);
        getModel().setUserDefinedSelectedFilter(null);
    }

    @Action
    public void onLoadFilter() {
        SearchTemplateDto selectedFilter = getModel().getUserDefinedSelectedFilter();
        if (selectedFilter != null) {
            if (getModel().containsUserChanges()) {
                FhUtils.showConfirmDialogYesNo(commonsMessageHelper.getMessage("fhdp.common.confirm"),
                        commonsMessageHelper.getMessage("document.ct.searchCriteria.tab.template.confirmDialog.loadFilter"),
                        () -> doLoadFilterWithNotification(selectedFilter));
            } else {
                doLoadFilterWithNotification(selectedFilter);
            }
        }
    }

    private void doLoadFilterWithNotification(SearchTemplateDto filter) {
        getModel().clearFormModel();
        getModel().setSourceFilter(filter);
        getModel().setConditionRows(prepareRowsForFilterDefinition(filter.getSearchTemplateDefinitions()));
        eventRegistry.fireNotificationEvent(NotificationEvent.Level.INFO,
                commonsMessageHelper.getMessage("fhdp.document.ct.searchCriteria.tab.template.notification.filterLoaded", filter.getTemplateName()));
    }
    @Action
    public void onSaveFilter() {
        if(!validate()) {
            return;
        }

        SearchTemplateDto sourceFilter = getModel().getSourceFilter();
        if (sourceFilter == null) {
            SearchTemplateDto filter = new SearchTemplateDto();
            saveFilterWithDialog(filter);
        } else {
            persistAndRefresh(sourceFilter);
            eventRegistry.fireNotificationEvent(NotificationEvent.Level.INFO,
                    commonsMessageHelper.getMessage("document.ct.searchCriteria.tab.template.notification.filterSaved", sourceFilter.getTemplateName()));
        }
    }

    @Action
    public void onSaveAsFilter() {
        if(!validate())
            return;
        if (getModel().getSourceFilter() != null) {
            SearchTemplateDto filter = new SearchTemplateDto();
            filter.setTemplateName(getModel().getSourceFilter().getTemplateName() + "-" + commonsMessageHelper.getMessage("fhdp.common.copy"));
            saveFilterWithDialog(filter);
        }
    }

    private void saveFilterWithDialog(SearchTemplateDto filter) {
        getAbstractUseCase().getUseCase().runUseCase(ChooseSearchTemplateNameUC.class, filter, this::findExistingTemplate, new IUseCaseSaveCancelCallback<SearchTemplateDto>() {
            @Override
            public void save(SearchTemplateDto one) {
                persistAndRefresh(one);
            }

            @Override
            public void cancel() {
            }
        });
    }

    private void persistAndRefresh(SearchTemplateDto searchTemplateDto) {
        Long id = persistTemplate(searchTemplateDto);
        SearchTemplateDto updatedDto = searchCriteriaDtoService.getDto(id);

        SearchTemplateDto userDefinedFilterForUpdate = getModel().getUserDefinedFilters().stream()
                .filter(item -> item.getId().equals(id))
                .findFirst()
                .orElse(null);

        if (userDefinedFilterForUpdate == null) {
            userDefinedFilterForUpdate = new SearchTemplateDto();
            BeanUtils.copyProperties(updatedDto, userDefinedFilterForUpdate);
            getModel().getUserDefinedFilters().add(userDefinedFilterForUpdate);

        } else {
            BeanUtils.copyProperties(updatedDto, userDefinedFilterForUpdate);
        }
        getModel().setUserDefinedSelectedFilter(userDefinedFilterForUpdate);
        getModel().setSourceFilter(userDefinedFilterForUpdate);
    }

    private Long  persistTemplate(SearchTemplateDto template) {
        List<SearchTemplateDefinition> definition = prepareTemplateDefinition();
        template.setSearchTemplateDefinitions(definition);
        template.setComponentName(getModel().getComponentName());
        template.setUserName(SessionManager.getSystemUser().getLogin());
         return searchCriteriaDtoService.persistDto(template);
    }

    private List<SearchTemplateDto> findExistingTemplate(String templateName) {
        SearchTemplateDtoQuery query = new SearchTemplateDtoQuery();
        query.setTemplateName(templateName);
        query.setUserName(SessionManager.getSystemUser().getLogin());
        query.setComponentName(getModel().getComponentName());
        return searchCriteriaDtoService.listDto(query);
    }

    private List<SearchTemplateDto> findTemplatesByNameAndUser() {
        SearchTemplateDtoQuery query = new SearchTemplateDtoQuery();
        query.setUserName(SessionManager.getSystemUser().getLogin());
        query.setComponentName(getModel().getComponentName());
        return searchCriteriaDtoService.listDto(query);
    }

    private List<SearchTemplateDefinition> prepareTemplateDefinition() {
        List<SearchTemplateDefinition> defs = new ArrayList<>();
        for (SearchTemplateBuilderModel.ConditionRow cr : getModel().getConditionRows()) {
            if (cr.getRowType() == null || cr.getRowType() == RowTypeEnum.ONLY_CMDS) {
                continue;
            }
            SearchTemplateDefinition fd = new SearchTemplateDefinition();
            fd.setLogicalCondition(cr.getLogicalCondition() == null ? null : cr.getLogicalCondition().name());
            if (cr.getRowType() == RowTypeEnum.BRACKET_START) {
                fd.setType(SearchTemplateDefinition.Type.BRACKET_START);
            } else if (cr.getRowType() == RowTypeEnum.BRACKET_STOP) {
                fd.setType(SearchTemplateDefinition.Type.BRACKET_STOP);
            } else if (cr.getRowType() == RowTypeEnum.CONDITION) {
                updateFilterDefinitionForCondition(fd, cr);
            } else {
                throw new IllegalStateException();
            }
            defs.add(fd);
        }
        return defs;
    }


    private void updateFilterDefinitionForCondition(SearchTemplateDefinition fd, SearchTemplateBuilderModel.ConditionRow cr) {
        fd.setType(SearchTemplateDefinition.Type.CONDITION);
        fd.setColumnName(cr.getColumn() == null ? null : cr.getColumn().getField());
        fd.setOperator(cr.getOperator() == null ? null : cr.getOperator().name());
        String value = null;
        if (cr.getColumn() != null && cr.getColumn().getColumnType() != null) {
            switch (cr.getColumn().getColumnType()) {
                case TEXT:
                    value = cr.getConditionTextValue();
                    break;
                case NUMBER:
                    if (!cr.getColumn().isDecimal()) {
                        value = cr.getConditionIntegerValue() == null ? null : cr.getConditionIntegerValue().toString();
                    } else {
                        value = cr.getConditionDecimalValue() == null ? null : cr.getConditionDecimalValue().toString();
                    }
                    break;
                case DATE:
                    value = cr.getConditionDateValue() == null ? null : cr.getConditionDateValue().toString();
                    break;
                case DATETIME:
                    value = cr.getConditionDateTimeValue() == null ? null : cr.getConditionDateTimeValue().toString();
                    break;
                case ENUM:
                    if (cr.getColumn().isMultiple()) {
                        if (cr.getConditionComboValues() != null) {
                            cr.getConditionComboValues().forEach(v -> {
                                fd.getValues().add(v.getTargetValue());
                            });
                        }
                    } else {
                        value = cr.getConditionSelectValue() == null ? null : cr.getConditionSelectValue().getTargetValue();
                    }
                    break;
                case DICTIONARY:
                    if (cr.getDictionaryItemType() == null || cr.getDictionaryItemType().getCode() == null)
                        value = null;
                    else{
                        value = cr.getDictionaryItemType().getCode();
                        fd.setPozycjaSlownikaType(cr.getDictionaryItemType());
                    }
                    break;
                default:
                    throw new IllegalStateException();

            }
        }
        if (value != null) {
            fd.getValues().add(value);
        }
    }

    private List<SearchTemplateBuilderModel.ConditionRow> prepareRowsForFilterDefinition(List<SearchTemplateDefinition> definitions) {
        List<SearchTemplateBuilderModel.ConditionRow> rows = new ArrayList<>();
        int level = 0;
        Stack<SearchTemplateBuilderModel.ConditionRow> openingBrackets = new Stack<>();
        int position = -1;
        for (SearchTemplateDefinition fd : definitions) {
            position++;
            if (fd.getType() == null) {
                continue;
            }
            SearchTemplateBuilderModel.ConditionRow cr;
            if (fd.getType() == SearchTemplateDefinition.Type.BRACKET_START) {
                cr = getModel().createBreacketStartRow();
                cr.setLevel(++level);
                if (fd.getLogicalCondition() != null) {
                    cr.setLogicalCondition(LogicalCondition.valueOf(fd.getLogicalCondition()));
                }
                openingBrackets.push(cr);
            } else if (fd.getType() == SearchTemplateDefinition.Type.BRACKET_STOP) {
                cr = getModel().createBreacketStopRow();
                cr.setLevel(level);
                if (openingBrackets.isEmpty()) {
                    throw new IllegalStateException("Closing bracket at position:" + position + " not connected with starting one");
                }
                SearchTemplateBuilderModel.ConditionRow openingBracket = openingBrackets.pop();
                openingBracket.setConnectedRow(cr);
                cr.setConnectedRow(openingBracket);
                rows.add(cr);

                cr = getModel().createCommandRow();
                cr.setLevel(level--);
            } else if (fd.getType() == SearchTemplateDefinition.Type.CONDITION) {
                cr = getModel().createConditionRow();
                if (fd.getLogicalCondition() != null) {
                    cr.setLogicalCondition(LogicalCondition.valueOf(fd.getLogicalCondition()));
                }
                if (fd.getColumnName() != null) {
                    cr.setColumn(getModel().getColumns().stream()
                            .filter(c -> fd.getColumnName().equalsIgnoreCase(c.getField()))
                            .findFirst()
                            .orElseThrow(() -> new IllegalStateException("Column:" + fd.getColumnName() + " not found"))
                    );
                }
                if (fd.getOperator() != null) {
                    cr.setOperator(OperatorType.valueOf(fd.getOperator()));
                    cr.setOperators(getOperatorsForColumn(cr.getColumn()));
                }
                if (cr.getColumn() != null) {
                    updateConditionRowValueForFilterDefinition(cr, fd);
                }
            } else {
                throw new IllegalStateException("Unknown FilterDefinitionType:" + fd.getType());
            }
            //noinspection ConstantConditions
            if (cr != null) {
                rows.add(cr);
            }
        }
        if (!openingBrackets.isEmpty()) {
            throw new IllegalStateException("Opening and closing bracket doesn't match");
        }
        if (level != 0) {
            throw new IllegalStateException("Sth wrong with level:" + level);
        }
        rows.add(getModel().createCommandRow());
        return rows;
    }


    private void updateConditionRowValueForFilterDefinition(SearchTemplateBuilderModel.ConditionRow cr, SearchTemplateDefinition fd) {
        String firstValue = fd.getValues() != null && fd.getValues().size() == 1 ? fd.getValues().get(0) : null;
        switch (cr.getColumn().getColumnType()) {
            //TEXT, NUMBER, DATE, DATETIME, ENUM
            case TEXT:
                cr.setConditionTextValue(firstValue);
                break;
            case NUMBER:
                if (!cr.getColumn().isDecimal()) {
                    if (firstValue != null) {
                        cr.setConditionIntegerValue(Long.valueOf(firstValue));
                    }
                } else {
                    if (firstValue != null) {
                        cr.setConditionDecimalValue(new BigDecimal(firstValue));
                    }
                }
                break;
            case DATE:
                if (firstValue != null) {
                    cr.setConditionDateValue(LocalDate.parse(firstValue));
                }
                break;
            case DATETIME:
                if (firstValue != null) {
                    cr.setConditionDateTimeValue(LocalDateTime.parse(firstValue));
                }
                break;
            case ENUM:
                if (!cr.getColumn().isMultiple()) {
                    if (firstValue != null) {
                        NameValueItem item = findNameValueItem(cr.getColumn().getAvailableValues(), firstValue);
                        if (item == null) {
                            throw new IllegalStateException("Value: " + firstValue + " not in available list for column: " + cr.getColumn().getField());
                        }
                        cr.setConditionSelectValue(item);
                    }
                } else {
                    //multi select
                    List<NameValueItem> values = new ArrayList<>();
                    for (String value : fd.getValues()) {
                        NameValueItem item = findNameValueItem(cr.getColumn().getAvailableValues(), value);
                        if (item == null) {
                            throw new IllegalStateException("Value: " + value + " not in available list for column: " + cr.getColumn().getField());
                        }
                        values.add(item);
                    }
                    cr.setConditionComboValues(values);
                }
                break;
            case DICTIONARY:
                if (firstValue != null) {
                    cr.setDictParameters(cr.getColumn().getDictParameters());
                    cr.setDictionaryItemType(fd.getPozycjaSlownikaType());
                }
        }
    }

    private NameValueItem findNameValueItem(List<NameValueItem> all, String targetValue) {
        if (all != null) {
            for (NameValueItem i : all) {
                if (targetValue.equals(i.getTargetValue())) {
                    return i;
                }
            }
        }
        return null;
    }

    @Action
    public void onRemoveFilter() {
        SearchTemplateDto filter = getModel().getSourceFilter();
        if (filter != null) {
            FhUtils.showConfirmDialogYesNo(commonsMessageHelper.getMessage("fhdp.common.confirm"), commonsMessageHelper.getMessage("document.ct.searchCriteria.tab.template.confirmDialog.deleteFilter", filter.getTemplateName()), () -> {
                searchCriteriaDtoService.deleteDto(filter.getId());
                eventRegistry.fireNotificationEvent(NotificationEvent.Level.INFO, commonsMessageHelper.getMessage("document.ct.searchCriteria.tab.template.notification.filterDeleted", filter.getTemplateName()));
                getModel().setUserDefinedFilters(findTemplatesByNameAndUser());
                onClearFilter();
            });
        }
    }
}
