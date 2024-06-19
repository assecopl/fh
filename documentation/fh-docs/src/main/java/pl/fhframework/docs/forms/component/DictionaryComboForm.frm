<?xml version="1.0" encoding="UTF-8"?>
<Form xmlns="http://fh.asseco.com/form/1.0" container="mainForm" id="documentationHolder" label="{componentName}">
    <AvailabilityConfiguration>
        <ReadOnly when="codeListId == '008'">codelist008</ReadOnly>
        <ReadOnly when="codeListId == '007'">codelist007</ReadOnly>
    </AvailabilityConfiguration>
    <OutputLabel width="md-12" value="{description}" id="_Form_OutputLabel"/>


    <Spacer width="md-12" height="25px" id="_Form_Spacer"/>
    <PanelGroup width="md-12" label="{$.fh.docs.component.combo_with_static_values}" id="_Form_PanelGroup1">
        <Row>
            <Button label="Clear startValue" onClick="clearStartValue()" id="_Form_PanelGroup1_Row_Button"/>
        </Row>
        <DictionaryCombo icon="fas fa-search" width="md-12" value="{startValue}" emptyValue="true" label="{$.fh.docs.component.combo_select_user}" provider="pl.fhframework.docs.forms.service.DictionaryComboDataProvider" onChange="-" onInput="-" id="_Form_PanelGroup1_DictionaryCombo">
            <DictionaryComboParameter name="codeListId" value="007" id="_Form_PanelGroup1_DictionaryCombo_DictionaryComboParameter1"/>
            <DictionaryComboParameter name="referenceDate" value="{dataReferencyjna}" id="_Form_PanelGroup1_DictionaryCombo_DictionaryComboParameter2"/>
        </DictionaryCombo>
        <InputText width="md-12" label="{$.fh.docs.component.code}" id="comboExampleCode6" rowsCount="4">
            <![CDATA[
<DictionaryCombo icon="fas fa-search" value="{startValue}" label="Wybierz uzytkownika" provider="pl.fhframework.docs.forms.service.DictionaryComboDataProvider" onChange="-" onInput="-">
            <DictionaryComboParameter name="codeListId" value="007"/>
            <DictionaryComboParameter name="referenceDate" value="{dataReferencyjna}"/>
        </DictionaryCombo>                        ]]>
        </InputText>

        <OutputLabel width="md-12" value="Aktualna wartość" id="_Form_PanelGroup1_OutputLabel1"/>
        <OutputLabel width="md-12" value="{startValue}" id="_Form_PanelGroup1_OutputLabel2"/>
    </PanelGroup>

    <PanelGroup width="md-12" label="{$.fh.docs.component.combo_with_static_values}" id="_Form_PanelGroup2">

        <Row>
            <OutputLabel width="md-2" value="codeListId:" id="_Form_PanelGroup2_Row_OutputLabel"/>
            <Button id="codelist007" label="007" onClick="setCodeListId('007')"/>
            <Button id="codelist008" label="008" onClick="setCodeListId('008')"/>
        </Row>
        <DictionaryCombo icon="fas fa-search" width="md-12" label="{$.fh.docs.component.combo_select_user}" value="{secondValue}" provider="pl.fhframework.docs.forms.service.DictionaryComboDataProvider" onChange="-" onInput="-" id="_Form_PanelGroup2_DictionaryCombo">

            <DictionaryComboParameter name="codeListId" value="{codeListId}" id="_Form_PanelGroup2_DictionaryCombo_DictionaryComboParameter1"/>
            <DictionaryComboParameter name="referenceDate" value="{dataReferencyjna}" id="_Form_PanelGroup2_DictionaryCombo_DictionaryComboParameter2"/>
        </DictionaryCombo>

        <OutputLabel width="md-12" value="Aktualna wartość" id="_Form_PanelGroup2_OutputLabel1"/>
        <OutputLabel width="md-12" value="{secondValue}" id="_Form_PanelGroup2_OutputLabel2"/>

        <InputText width="md-12" label="{$.fh.docs.component.code}" id="comboExampleCode5" rowsCount="4">
            <![CDATA[
 <DictionaryCombo icon="fas fa-search" width="md-12" label="{$.fh.docs.component.combo_select_user}" provider="pl.fhframework.docs.forms.service.DictionaryComboDataProvider" onChange="-" onInput="-">
            <DictionaryComboParameter name="codeListId" value="{codeListId}"/>
            <DictionaryComboParameter name="referenceDate" value="{dataReferencyjna}"/>
        </DictionaryCombo>                          ]]>
        </InputText>
    </PanelGroup>


    <Button id="pBack" label="[icon='fa fa-chevron-left'] {$.fh.docs.component.back}" onClick="backToFormComponentsList()"/>

</Form>