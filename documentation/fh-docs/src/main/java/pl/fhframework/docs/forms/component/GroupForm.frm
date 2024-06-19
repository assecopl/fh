<?xml version="1.0" encoding="UTF-8"?>
<Form xmlns="http://fh.asseco.com/form/1.0" container="mainForm" id="documentationHolder" label="{componentName}">
    <AvailabilityConfiguration>
        <ReadOnly>rowExampleCode1,rowExampleCode2, rowExampleCode3</ReadOnly>
    </AvailabilityConfiguration>
    <OutputLabel width="md-12" value="{description}" id="_Form_OutputLabel"/>
    <Spacer width="md-12" height="25px" id="_Form_Spacer"/>
    <TabContainer id="_Form_TabContainer">
        <Tab label="{$.fh.docs.component.examples}" id="_Form_TabContainer_Tab1">
            <PanelGroup width="md-12" label="{$.fh.docs.component.group_row_with_simple_usage}" id="_Form_TabContainer_Tab1_PanelGroup1">
                <Group id="_Form_TabContainer_Tab1_PanelGroup1_Group1">
                    <OutputLabel width="md-12" value="{$.fh.docs.component.group_label_for_the_first_group}" id="_Form_TabContainer_Tab1_PanelGroup1_Group1_OutputLabel"/>
                    <InputText id="rowCode1_1" value="{$.fh.docs.component.group_group_index}: 1.1" width="md-4"/>
                    <InputText id="rowCode1_2" value="{$.fh.docs.component.group_group_index}: 1.2" width="md-4"/>
                    <InputText id="rowCode1_3" value="{$.fh.docs.component.group_group_index}: 1.3" width="md-4"/>
                </Group>
                <Group id="_Form_TabContainer_Tab1_PanelGroup1_Group2">
                    <OutputLabel width="md-12" value="{$.fh.docs.component.group_label_for_the_second_group}" id="_Form_TabContainer_Tab1_PanelGroup1_Group2_OutputLabel"/>
                    <InputText id="rowCode2_1" value="{$.fh.docs.component.group_group_index}: 2.1" width="md-6"/>
                    <InputText id="rowCode2_2" value="{$.fh.docs.component.group_group_index}: 2.2" width="md-6"/>
                </Group>
                <InputText id="rowExampleCode1" label="{$.fh.docs.component.code}" width="md-12" rowsCount="6">
                    <![CDATA[
<Group>
    <OutputLabel value="{$.fh.docs.component.group_label_for_the_first_group}"/>
        <InputText id="rowCode1_1" value="{$.fh.docs.component.group_group_index}: 1.1"
                   width="md-4"/>
        <InputText id="rowCode1_2" value="{$.fh.docs.component.group_group_index}: 1.2"
                   width="md-4"/>
        <InputText id="rowCode1_3" value="{$.fh.docs.component.group_group_index}: 1.3"
                   width="md-4"/>
</Group>
<Group>
    <OutputLabel value="{$.fh.docs.component.group_label_for_the_second_group}"/>
        <InputText id="rowCode2_1" value="{$.fh.docs.component.group_group_index}: 2.1" width="md-6"/>
        <InputText id="rowCode2_2" value="{$.fh.docs.component.group_group_index}: 2.2" width="md-6"/>
</Group>
                        ]]>
                </InputText>
            </PanelGroup>

            <PanelGroup width="md-12" label="{$.fh.docs.component.group_row_with_multi_size}" id="_Form_TabContainer_Tab1_PanelGroup2">
                <Group width="sm-12,md-8,lg-6" id="_Form_TabContainer_Tab1_PanelGroup2_Group">
                    <OutputLabel width="md-12" value="{$.fh.docs.component.group_label_for_the_group_with_multiple_sizes}" id="_Form_TabContainer_Tab1_PanelGroup2_Group_OutputLabel"/>
                    <InputText width="md-12" id="rowCode3_1" value="{$.fh.docs.component.group_group_index}: 3.1"/>
                    <InputText width="md-12" id="rowCode3_2" value="{$.fh.docs.component.group_group_index}: 3.2"/>
                </Group>
                <InputText id="rowExampleCode2" label="{$.fh.docs.component.code}" width="md-12" rowsCount="3">
                    <![CDATA[
<Group width="sm-12,md-8,lg-6">
    <OutputLabel value="{$.fh.docs.component.group_label_for_the_group_with_multiple_sizes}"/>
        <InputText id="rowCode3_1" value="{$.fh.docs.component.group_group_index}: 3.1"/>
        <InputText id="rowCode3_2" value="{$.fh.docs.component.group_group_index}: 3.2"/>
</Group>
                        ]]>
                </InputText>
            </PanelGroup>

            <PanelGroup width="md-12" label="{$.fh.docs.component.group_on_click}" id="_Form_TabContainer_Tab1_PanelGroup3">
                <Group width="lg-8" onClick="someActionGroup" id="_Form_TabContainer_Tab1_PanelGroup3_Group">
                    <OutputLabel width="md-12" value="{$.fh.docs.component.group_label_for_the_group_with_onClick_action}" id="_Form_TabContainer_Tab1_PanelGroup3_Group_OutputLabel1"/>
                    <OutputLabel value="{$.fh.docs.component.group_on_click_reminder}" width="md-12" id="_Form_TabContainer_Tab1_PanelGroup3_Group_OutputLabel2"/>
                    <OutputLabel value="{$.fh.docs.component.group_click_any_component}" width="md-12" id="_Form_TabContainer_Tab1_PanelGroup3_Group_OutputLabel3"/>
                    <Button width="sm-12" id="rowCode4_1" label="{$.fh.docs.component.group_button_will_execute_action}" onClick="someActionButtonInGroup"/>
                    <OutputLabel value="{$.fh.docs.component.group_click_any_component}" width="md-12" id="_Form_TabContainer_Tab1_PanelGroup3_Group_OutputLabel4"/>
                </Group>
                <InputText id="rowExampleCode3" label="{$.fh.docs.component.code}" width="md-12" rowsCount="6">
                    <![CDATA[
<Group width="lg-8" onClick="someActionGroup">
<OutputLabel value="fh.docs.component.group_label_for_the_group_with_onClick_action"/>
<OutputLabel value="Keep in mind that, if group defines some action, components with actions inside that group, will be executed." width="md-12"/>
<OutputLabel value="Click any component in this group." width="md-12"/>
<Button id="rowCode3_1" label="This button will execute action, NOT a group" onClick="someActionButtonInGroup"/>
<OutputLabel value="Click any component in this group." width="md-12"/>
</Group>
                    ]]>
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