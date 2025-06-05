<?xml version="1.0" encoding="UTF-8"?>
<Form xmlns="http://fh.asseco.com/form/1.0" container="mainForm" id="documentationHolder" label="{componentName}">
    <OutputLabel width="md-12" value="Wykonany kod HTML:" id="_Form_OutputLabel1"/>
    <HtmlView text="{text}" id="_Form_HtmlView"/>
    <OutputLabel width="md-12" value="Kod HTML w postaci tekstu:" id="_Form_OutputLabel2"/>
    <InputText width="md-12" rowsCount="5" value="{text}" onInput="-" id="_Form_InputText"/>
    <Button id="pBack" label="[icon='fa fa-chevron-left'] {$.fh.docs.component.back}" onClick="backToFormComponentsList()"/>
</Form>