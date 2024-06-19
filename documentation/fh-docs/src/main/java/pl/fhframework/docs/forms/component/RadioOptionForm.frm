<?xml version="1.0" encoding="UTF-8"?>
<Form xmlns="http://fh.asseco.com/form/1.0" container="mainForm" id="documentationHolder" label="{componentName}">
    <AvailabilityConfiguration>
        <ReadOnly>radioOptionsGroupsExampleCode1,radioOptionsGroupsExampleCode2</ReadOnly>
    </AvailabilityConfiguration>
    <OutputLabel width="md-12" value="{description}" id="_Form_OutputLabel"/>
    <Spacer width="md-12" height="25px" id="_Form_Spacer"/>
    <TabContainer id="_Form_TabContainer">
        <Tab label="{$.fh.docs.component.examples}" id="_Form_TabContainer_Tab1">
            <PanelGroup width="md-12" label="{$.fh.docs.component.radiooption_radiooption_with_simple_usage}" id="_Form_TabContainer_Tab1_PanelGroup1">
                <ValidateMessages componentIds="*" level="error" id="_Form_TabContainer_Tab1_PanelGroup1_ValidateMessages"/>
                <Row>
                    <Button label="{$.fh.docs.component.radiooption_random}" onClick="onRandom(THIS)" id="_Form_TabContainer_Tab1_PanelGroup1_Row1_Button"/>
                </Row>
                <Row elementsHorizontalAlign="AROUND">
                    <RadioOption value="{radioNigeria}" label="{radioNigeria}" onChange="-" width="md-2" targetValue="{selectCountry}" required="true" id="_Form_TabContainer_Tab1_PanelGroup1_Row2_RadioOption1"/>
                    <RadioOption value="{radioSenegal}" label="{radioSenegal}" onChange="-" width="md-2" targetValue="{selectCountry}" id="_Form_TabContainer_Tab1_PanelGroup1_Row2_RadioOption2"/>
                </Row>
                <Row elementsHorizontalAlign="AROUND">
                    <RadioOption value="{radioGhana}" label="{radioGhana}" onChange="-" width="md-2" targetValue="{selectCountry}" id="_Form_TabContainer_Tab1_PanelGroup1_Row3_RadioOption1"/>
                    <RadioOption value="{radioZimbabwe}" label="{radioZimbabwe}" onChange="-" width="md-2" targetValue="{selectCountry}" id="_Form_TabContainer_Tab1_PanelGroup1_Row3_RadioOption2"/>
                </Row>
                <OutputLabel width="md-12" value="{$.fh.docs.component.radiooption_radiooption_selected_country} {selectCountry}" id="_Form_TabContainer_Tab1_PanelGroup1_OutputLabel"/>
                <Row elementsHorizontalAlign="AROUND">
                    <RadioOption value="{T(pl.fhframework.docs.forms.component.model.RadioOptionElement.RadioEnum).Nigeria}" label="{T(pl.fhframework.docs.forms.component.model.RadioOptionElement.RadioEnum).Nigeria}" onChange="-" width="md-2" targetValue="{selectedEnum}" id="_Form_TabContainer_Tab1_PanelGroup1_Row4_RadioOption1"/>
                    <RadioOption value="{T(pl.fhframework.docs.forms.component.model.RadioOptionElement.RadioEnum).Senegal}" label="{T(pl.fhframework.docs.forms.component.model.RadioOptionElement.RadioEnum).Senegal}" onChange="-" width="md-2" targetValue="{selectedEnum}" id="_Form_TabContainer_Tab1_PanelGroup1_Row4_RadioOption2"/>
                </Row>
                <InputText id="radioOptionsGroupsExampleCode1" label="{$.fh.docs.component.code}" width="md-12" rowsCount="4">
                    <![CDATA[![ESCAPE[<Row elementsHorizontalAlign="AROUND">
    <RadioOption value="{radioNigeria}" label="{radioNigeria}" onChange="-" width="md-2" targetValue="{selectCountry}" />
    <RadioOption value="{radioSenegal}" label="{radioSenegal}" onChange="-" width="md-2" targetValue="{selectCountry}" />
</Row>
<Row elementsHorizontalAlign="AROUND">
    <RadioOption value="{radioGhana}" label="{radioGhana}" onChange="-" width="md-2" targetValue="{selectCountry}" />
    <RadioOption value="{radioZimbabwe}" label="{radioZimbabwe}" onChange="-" width="md-2" targetValue="{selectCountry}" />
</Row>]]]]>
                </InputText>
            </PanelGroup>
            <PanelGroup width="md-12" label="{$.fh.docs.component.radiooption_radiooption_with_blocked_usage}" id="_Form_TabContainer_Tab1_PanelGroup2">
                <Row elementsHorizontalAlign="AROUND">
                    <RadioOption availability="VIEW" value="{radioNigeria}" label="{radioNigeria}" onChange="-" width="md-2" targetValue="{selectCountry2}" id="_Form_TabContainer_Tab1_PanelGroup2_Row1_RadioOption1"/>
                    <RadioOption id="radioSenegal2" value="{radioSenegal}" label="{radioSenegal}" onChange="-" width="md-2" targetValue="{selectCountry2}"/>
                </Row>
                <Row elementsHorizontalAlign="AROUND">
                    <RadioOption id="radioGhana2" value="{radioGhana}" label="{radioGhana}" onChange="-" width="md-2" targetValue="{selectCountry2}"/>
                    <RadioOption availability="VIEW" value="{radioZimbabwe}" label="{radioZimbabwe}" onChange="-" width="md-2" targetValue="{selectCountry2}" id="_Form_TabContainer_Tab1_PanelGroup2_Row2_RadioOption2"/>
                </Row>
                <OutputLabel width="md-12" value="{$.fh.docs.component.radiooption_radiooption_selected_country} {selectCountry2}" id="_Form_TabContainer_Tab1_PanelGroup2_OutputLabel"/>
                <InputText id="radioOptionsGroupsExampleCode2" label="{$.fh.docs.component.code}" width="md-12" rowsCount="4">
                    <![CDATA[![ESCAPE[<Row elementsHorizontalAlign="AROUND">
    <RadioOption availability="VIEW" value="{radioNigeria}" label="{radioNigeria}" onChange="-" width="md-2" targetValue="{selectCountry2}" />
    <RadioOption value="{radioSenegal}" label="{radioSenegal}" onChange="-" width="md-2" targetValue="{selectCountry2}" />
</Row>
<Row elementsHorizontalAlign="AROUND">
    <RadioOption id="radioGhana2" value="{radioGhana}" label="{radioGhana}" onChange="-" width="md-2" targetValue="{selectCountry2}" />
    <RadioOption availability="VIEW" id="radioZimbabwe2" value="{radioZimbabwe}" label="{radioZimbabwe}" onChange="-" width="md-2" targetValue="{selectCountry2}" />
</Row>]]]]>
                </InputText>
            </PanelGroup>
        </Tab>
        <Tab label="{$.fh.docs.component.attributes}" id="_Form_TabContainer_Tab2">
            <Table iterator="item" collection="{attributes}" id="_Form_TabContainer_Tab2_Table">
                <Column label="{$.fh.docs.component.attributes_identifier}" value="{item.name}" width="15" id="_Form_TabContainer_Tab2_Table_Column1"/>
                <Column label="{$.fh.docs.component.attributes_type}" value="{item.type}" width="15" id="_Form_TabContainer_Tab2_Table_Column2"/>
                <Column label="{$.fh.docs.component.attributes_boundable}" value="{item.boundable}" width="10" id="_Form_TabContainer_Tab2_Table_Column3"/>
                <Column label="{$.fh.docs.component.attributes_default_value}" value="{item.defaultValue}" width="20" id="_Form_TabContainer_Tab2_Table_Column4"/>
                <Column label="{$.fh.docs.component.attributes_description}" value="{item.description}" width="40" id="_Form_TabContainer_Tab2_Table_Column5"/>
            </Table>
        </Tab>
    </TabContainer>
    <Button id="pBack" label="[icon='fa fa-chevron-left'] {$.fh.docs.component.back}" onClick="backToFormComponentsList()"/>
</Form>