<?xml version="1.0" encoding="UTF-8"?>
<Form xmlns="http://fh.asseco.com/form/1.0" container="mainForm" id="documentationHolder" label="{componentName}">
    <AvailabilityConfiguration>
        <ReadOnly>code</ReadOnly>
    </AvailabilityConfiguration>
    <EmbedPage width="md-6" label="Embedded video" height="440px" src="{url}" id="_Form_EmbedPage"/>

    <InputText id="code" label="{$.fh.docs.component.code}" width="md-12" rowsCount="1">
        <![CDATA[<EmbedPage width="md-6" height="440px"  label="Embedded video" src="{url}" />]]>
    </InputText>
    <Button id="pBack" label="[icon='fa fa-chevron-left'] {$.fh.docs.component.back}" onClick="backToFormComponentsList()"/>
</Form>