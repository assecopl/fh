<?xml version="1.0" encoding="UTF-8"?>
<Form xmlns="http://fh.asseco.com/form/1.0" container="mainForm" id="documentationHolder" label="{componentName}">
    <AvailabilityConfiguration>
        <ReadOnly>exampleCode1</ReadOnly>
    </AvailabilityConfiguration>
    <TabContainer id="_Form_TabContainer">
        <Tab label="{$.fh.docs.component.examples}" id="_Form_TabContainer_Tab1">
            <OutputLabel width="md-12" value="Za pomocą kontrolki KeyboardEvent możliwe jest wywołanie akcji przypadku użycia za pomocą wciśnięcia klawisza lub kombinacji klawiszy. Dozwolone jest użycie klawiszy F1 - F12 oraz kombinacji klawiszy CTRL_A - CTRL_Z." id="_Form_TabContainer_Tab1_OutputLabel"/>

            <PanelGroup label="Przykład" borderVisible="true" id="_Form_TabContainer_Tab1_PanelGroup">
                <Button onClick="akcjaUC" label="Wciśnij przycisk" id="_Form_TabContainer_Tab1_PanelGroup_Button"/>

                <OutputLabel value="Wciśnięcie kombinacji klawiszy CTRL i A lub klawisza F4 wywoła akcję która jest podpięta do naciśnięcia powyższego przyciku." width="md-12" id="_Form_TabContainer_Tab1_PanelGroup_OutputLabel"/>
                <InputText id="exampleCode1" label="{$.fh.docs.component.code}" width="md-12" rowsCount="2">
                    <![CDATA[![ESCAPE[<KeyboardEvent event="akcjaUC" shortcut="CTRL_A" />]]]]>
                    <![CDATA[![ESCAPE[<KeyboardEvent event="akcjaUC" shortcut="F4" />]]]]>
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
    <KeyboardEvent event="akcjaUC" shortcut="CTRL_A" id="_Form_KeyboardEvent1"/>
    <KeyboardEvent event="akcjaUC" shortcut="F4" id="_Form_KeyboardEvent2"/>

    <Button id="pBack" label="[icon='fa fa-chevron-left'] {$.fh.docs.component.back}" onClick="backToFormComponentsList()"/>
</Form>