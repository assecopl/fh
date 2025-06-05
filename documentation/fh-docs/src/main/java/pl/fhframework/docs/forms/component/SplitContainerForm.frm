<?xml version="1.0" encoding="UTF-8"?>
<Form xmlns="http://fh.asseco.com/form/1.0" container="mainForm" id="documentationHolder" label="{componentName}">
    <AvailabilityConfiguration>
        <ReadOnly>rowExampleCode1</ReadOnly>
    </AvailabilityConfiguration>
    <OutputLabel width="md-12" value="{description}" id="_Form_OutputLabel"/>
    <Spacer width="md-12" height="25px" id="_Form_Spacer"/>
    <TabContainer id="_Form_TabContainer">
        <Tab label="{$.fh.docs.component.examples}" id="_Form_TabContainer_Tab1">
            <PanelGroup label="{$.fh.docs.component.splitcontainer_split_container_example}" id="_Form_TabContainer_Tab1_PanelGroup">
                <SplitContainer width="md-12" sizeLeft="500px" height="170px" id="_Form_TabContainer_Tab1_PanelGroup_SplitContainer">
                    <PanelGroup id="_Form_TabContainer_Tab1_PanelGroup_SplitContainer_PanelGroup1">
                        <InputText width="md-12" id="rowCode1_1" value="{$.fh.docs.component.splitcontainer_this_is_label_for_the_first_row}"/>
                    </PanelGroup>
                    <PanelGroup id="_Form_TabContainer_Tab1_PanelGroup_SplitContainer_PanelGroup2">
                        <InputText width="md-12" id="rowCode1_2" value="{$.fh.docs.component.splitcontainer_this_is_label_for_the_second_row}"/>
                    </PanelGroup>
                </SplitContainer>
                <InputText id="rowExampleCode1" label="{$.fh.docs.component.code}" width="md-12" height="260" rowsCount="8">
                    <![CDATA[![ESCAPE[<SplitContainer width="md-12" sizeLeft="500px" height="170px">
                    <PanelGroup>
                        <InputText id="rowCode1_1" value="This is label for the first row"/>
                    </PanelGroup>
                    <PanelGroup>
                        <InputText id="rowCode1_2" value="This is label for the second row"/>
                    </PanelGroup>
                </SplitContainer>]]]]>
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