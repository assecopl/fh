<?xml version="1.0" encoding="UTF-8"?>
<Form xmlns="http://fh.asseco.com/form/1.0" container="mainForm" id="documentationHolder" label="{componentName}">
    <OutputLabel width="md-12" value="Edytor z podglądem:" id="_Form_OutputLabel1"/>
    <HtmlEditor text="{editorText}" onChange="-" id="_Form_HtmlEditor"/>
    <OutputLabel width="md-12" value="Wygenerowany kod HTML:" id="_Form_OutputLabel2"/>
    <InputText width="md-12" availability="VIEW" rowsCount="5" value="{editorText}" onInput="-" id="_Form_InputText"/>
    <Button id="pBack" label="[icon='fa fa-chevron-left'] {$.fh.docs.component.back}" onClick="backToFormComponentsList()"/>
</Form>