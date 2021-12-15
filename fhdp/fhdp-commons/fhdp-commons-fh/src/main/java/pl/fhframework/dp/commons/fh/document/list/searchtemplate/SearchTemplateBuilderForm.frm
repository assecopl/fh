<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<Form xmlns="http://fh.asseco.com/form/1.0" id="SearchCriteriaBuilderCompositeForm" container="mainForm">
    <AvailabilityConfiguration>
        <Invisible when="sourceFilter == null">sourceFilterInfo</Invisible>
        <ReadOnly when="userDefinedSelectedFilter == null">btnLoadFilter</ReadOnly>
        <ReadOnly when="sourceFilter == null">btnRemoveFilter</ReadOnly>
        <ReadOnly when="!containsUserChanges()">btnSaveFilter</ReadOnly>
        <ReadOnly when="sourceFilter == null">btnSaveAsFilter</ReadOnly>
    </AvailabilityConfiguration>
    <OutputLabel id="sourceFilterInfo" value="{$.declaration.ct.searchCriteria.tab.template.outputLabel.loadedFilter.value}: [b]{sourceFilter.getTemplateName()}[/b]"/>

    <Table id="conditionsTable" collection="{conditionRows}" iterator="row">
        <Column label="{$.declaration.ct.searchCriteria.tab.template.table.col.logicalOperator.label}" width="200">
            <SelectOneMenu availability="{row.getAvailabilityOperator()}" values="{logicalConditions}" value="{row.logicalCondition}" onChange="operatorChanged({row})" width="200"/>
        </Column>
        <Column label="{$.declaration.ct.searchCriteria.tab.template.table.col.columnName.label}" width="300">
            <SelectOneMenu availability="{row.getAvailabilityColumnName()}" values="{columns}" value="{row.column}" onChange="columnChanged({row})" displayExpression="i18N" width="300"/>
        </Column>
        <Column label="{$.declaration.ct.searchCriteria.tab.template.table.col.condition.label}" width="300">
            <!--specjalny fromatter bo nie działa poprawnie: displayExpression="displayValue"-->
            <SelectOneMenu availability="{row.getAvailabilityColumnCondition()}" values="{row.operators}" value="{row.operator}" displayExpression="displayedValue" width="300"/>
        </Column>
        <Column label="{$.declaration.ct.searchCriteria.tab.template.table.col.value.label}" width="300">
            <InputText id="inputText" availability="{row.getAvailabilityInputText()}" value="{row.conditionTextValue}" onChange="-" width="300"/>
            <InputNumber id="inputNumber" availability="{row.getAvailabilityInputNumber()}" value="{row.conditionIntegerValue}" onChange="-" width="300"/>
            <InputNumber id="inputDecimal" availability="{row.getAvailabilityInputNumberDecimal()}" value="{row.conditionDecimalValue}" onChange="-" width="300"/>

            <DictionaryCombo id="dictionaryCombo" width="300" availability="{row.getAvailabilityDictionary()}" value="{row.dictionaryItemType}"
                             hintPlacement="TOP" provider="pl.fhframework.dp.commons.fh.dataProviders.RefDataComboDataProvider" onInput="-" onChange="-"
                             displayExpression="displayedValue" icon="fas fa-search">
                <DictionaryComboParameter name="codeListId" value="{row.dictParameters.get('codeListId')}"/>
                <DictionaryComboParameter name="referenceDate" value="{row.dictParameters.get('referenceDate')}"/>
            </DictionaryCombo>

            <SelectOneMenu id="selectOne" availability="{row.getAvailabilitySelectOne()}" values="{row.column.availableValues}" value="{row.conditionSelectValue}" onChange="columnConditionChanged({row})"
                           displayExpression="displayedValue" width="300"/>
            <Combo id="combo" availability="{row.getAvailabilityCombo()}" value="{row.conditionComboValues}" values="{row.column.availableValues}" onChange="columnConditionChanged({row})"
                   multiselect="true" displayExpression="displayedValue" width="300" freeTyping="false"/>
            <InputDate id="inputDate" availability="{row.getAvailabilityInputDate()}" value="{row.conditionDateValue}" onChange="columnConditionChanged({row})" width="300"/>
            <InputTimestamp id="timestamp" availability="{row.getAvailabilityInputTimestamp()}" value="{row.conditionDateTimeValue}" onChange="columnConditionChanged({row})" width="300"/>
            <OutputLabel id="outputLabel" availability="{row.getAvailabilityOutputLabel()}" value="{row.conditionTextValue}" width="300" />
        </Column>
        <Column label="{$.common.options}" width="300">
            <Button availability="{row.getActionAvailabilityDeleteCondition()}" onClick="deleteContition({row})" label="[icon='fa fa-trash']" hint="{$.common.delete}" hintTrigger="HOVER" width="80px"/>
            <Button availability="{row.getActionAvailabilityAddCondition()}" hintTrigger="HOVER" onClick="addContition({row})" label="[icon='fa fa-plus']" hint="{$.declaration.ct.searchCriteria.tab.template.table.col.options.row.btn.addCondition.label}" horizontalAlign="right" width="80px"/>
            <Button availability="{row.getActionAvailabilityAddBracket()}" hintTrigger="HOVER" onClick="addBracket({row})" label="[icon='fa fa-chevron-left']" hint="{$.declaration.ct.searchCriteria.tab.template.table.col.options.row.btn.addBracket.label}" horizontalAlign="right" width="80px"/>
        </Column>
    </Table>
    <Group>
        <!--ValidateMessages level="error" componentIds="*"/-->
    </Group>
    <Group>
        <Button id="btnClearFilter" hintTrigger="HOVER" width="md-1" onClick="onClearFilter" label="[icon='fa fa-times']" hint="{$.common.clear}"/>
        <SelectOneMenu id="filterSelectOne" value="{userDefinedSelectedFilter}" values="{userDefinedFilters}" onChange="-" displayExpression="{getTemplateName()}" width="md-2"/>
        <Button id="btnLoadFilter" hintTrigger="HOVER" width="md-1"  onClick="onLoadFilter" label="[icon='fas fa-file-import']" hint="{$.common.load}"/>
        <Button id="btnSaveFilter" hintTrigger="HOVER" width="md-1"  onClick="onSaveFilter" label="[icon='fa fa-save']" hint="{$.common.save}"/>
        <Button id="btnSaveAsFilter" hintTrigger="HOVER" width="md-1"  onClick="onSaveAsFilter" label="[icon='fa fa-share-square']" hint="{$.common.saveAs}"/>
        <Button id="btnRemoveFilter" hintTrigger="HOVER" width="md-1" onClick="onRemoveFilter" label="[icon='fa fa-trash']" hint="{$.common.delete}"/>
    </Group>
</Form>
