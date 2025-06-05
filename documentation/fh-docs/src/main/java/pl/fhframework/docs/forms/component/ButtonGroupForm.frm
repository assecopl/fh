<?xml version="1.0" encoding="UTF-8"?>
<Form xmlns="http://fh.asseco.com/form/1.0" container="mainForm" id="documentationHolder" label="{componentName}">
    <AvailabilityConfiguration>
        <ReadOnly>
            buttonGroupExampleCode1,buttonGroupExampleCode2,buttonGroupExampleCode3,buttonGroupExampleCode4,buttonGroupExampleCode5,buttonGroupExampleCode6
        </ReadOnly>
    </AvailabilityConfiguration>
    <OutputLabel width="md-12" value="{description}" id="_Form_OutputLabel"/>
    <Spacer width="md-12" height="25px" id="_Form_Spacer"/>
    <TabContainer id="_Form_TabContainer">
        <Tab label="{$.fh.docs.component.examples}" id="_Form_TabContainer_Tab1">
            <PanelGroup width="md-12" label="{$.fh.docs.component.buttongroup_simple_buttongroup} " id="_Form_TabContainer_Tab1_PanelGroup1">
                <ButtonGroup id="buttonGroupCode1" width="md-12">
                    <Button id="buttonCode1_1" label="{$.fh.docs.component.buttongroup_btn_1}"/>
                    <Button id="buttonCode1_2" label="{$.fh.docs.component.buttongroup_btn_2}"/>
                    <Button id="buttonCode1_3" label="{$.fh.docs.component.buttongroup_btn_3}"/>
                </ButtonGroup>
                <InputText id="buttonGroupExampleCode1" label="{$.fh.docs.component.code}" width="md-12" rowsCount="5">
                    <![CDATA[
 <ButtonGroup id="buttonGroupCode1" width="md-12">
    <Button id="buttonCode1_1" label="BTN 1" />
    <Button id="buttonCode1_2" label="BTN 2"/>
    <Button id="buttonCode1_3" label="BTN 3"/>
</ButtonGroup>
                        ]]>
                </InputText>
            </PanelGroup>

            <PanelGroup width="md-12" label="{$.fh.docs.component.buttongroup_with_onchange_event_bound_to_another_buttongroup}" id="_Form_TabContainer_Tab1_PanelGroup2">
                <ButtonGroup id="buttonGroupOnChangeCode2" width="md-12" activeButton="{activeButton}" onButtonChange="-">
                    <Button id="buttonCode2_1" label="{$.fh.docs.component.buttongroup_btn_2_1}"/>
                    <Button id="buttonCode2_2" label="{$.fh.docs.component.buttongroup_btn_2_2}"/>
                    <Button id="buttonCode2_3" label="{$.fh.docs.component.buttongroup_btn_2_3}"/>
                </ButtonGroup>
                <Group id="_Form_TabContainer_Tab1_PanelGroup2_Group">
                    <Spacer width="md-12" id="_Form_TabContainer_Tab1_PanelGroup2_Group_Spacer"/>
                </Group>
                <ButtonGroup id="buttonGroupOnChangeCode3" width="md-12" activeButton="{activeButton}" onButtonChange="-">
                    <Button id="buttonCode3_1" label="{$.fh.docs.component.buttongroup_btn_3_1}"/>
                    <Button id="buttonCode3_2" label="{$.fh.docs.component.buttongroup_btn_3_2}"/>
                    <Button id="buttonCode3_3" label="{$.fh.docs.component.buttongroup_btn_3_3}"/>
                </ButtonGroup>
                <OutputLabel width="md-12" value="{$.fh.docs.component.buttongroup_active_group_index}: {activeButton}" id="_Form_TabContainer_Tab1_PanelGroup2_OutputLabel"/>
                <InputText id="buttonGroupExampleCode2" label="{$.fh.docs.component.code}" width="md-12" rowsCount="12">
                    <![CDATA[
<ButtonGroup id="buttonGroupOnChangeCode2"  width="md-12" activeButton="\{activeButton\}"  onButtonChange="-">
    <Button id="buttonCode2_1" label="BTN 2_1"/>
    <Button id="buttonCode2_2" label="BTN 2_2"/>
    <Button id="buttonCode2_3" label="BTN 2_3"/>
</ButtonGroup>
<Group><Spacer/></Group>
<ButtonGroup id="buttonGroupOnChangeCode3" width="md-12" activeButton="\{activeButton\}" onButtonChange="-">
    <Button id="buttonCode3_1" label="BTN 3_1"/>
    <Button id="buttonCode3_2" label="BTN 3_2"/>
    <Button id="buttonCode3_3" label="BTN 3_3"/>
</ButtonGroup>
<OutputLabel value="Active group index is: \{activeButton\}" />
                        ]]>
                </InputText>
            </PanelGroup>

            <PanelGroup width="md-12" label="{$.fh.docs.component.buttongroup_focus}" id="_Form_TabContainer_Tab1_PanelGroup3">
                <ButtonGroup id="buttonGroupCode4" width="md-4">
                    <Button id="buttoncode4_1" label="{$.fh.docs.component.buttongroup_1}"/>
                    <Button id="buttonCode4_2" label="{$.fh.docs.component.buttongroup_2}"/>
                    <Button id="buttonCode4_3" label="{$.fh.docs.component.buttongroup_3}"/>
                </ButtonGroup>
                <ButtonGroup id="buttonGroupCode5" width="md-4">
                    <Button id="buttonCode5_1" label="{$.fh.docs.component.buttongroup_4}"/>
                    <Button id="buttonCode5_2" label="{$.fh.docs.component.buttongroup_5}"/>
                    <Button id="buttonCode5_3" label="{$.fh.docs.component.buttongroup_6}"/>
                    <Button id="buttonCode5_4" label="{$.fh.docs.component.buttongroup_7}"/>
                </ButtonGroup>
                <ButtonGroup id="buttonGroupCode6" width="md-4">
                    <Button id="buttonCode6_1" label="{$.fh.docs.component.buttongroup_8}"/>
                </ButtonGroup>
                <InputText id="buttonGroupExampleCode3" label="{$.fh.docs.component.code}" width="md-12" rowsCount="14">
                    <![CDATA[
<ButtonGroup id="buttonGroupCode4" width="md-4">
    <Button id="buttoncode4_1" label="1"/>
    <Button id="buttonCode4_2" label="2"/>
    <Button id="buttonCode4_3" label="3"/>
</ButtonGroup>
<ButtonGroup id="buttonGroupCode5" width="md-4">
    <Button id="buttonCode5_1" label="4"/>
    <Button id="buttonCode5_2" label="5"/>
    <Button id="buttonCode5_3" label="6"/>
    <Button id="buttonCode5_4" label="7"/>
</ButtonGroup>
<ButtonGroup id="buttonGroupCode6" width="md-4">
    <Button id="buttonCode6_1" label="8"/>
</ButtonGroup>
                        ]]>
                </InputText>
            </PanelGroup>

            <PanelGroup width="md-12" label="{$.fh.docs.component.buttongroup_group_with_predefined_activebutton}" id="_Form_TabContainer_Tab1_PanelGroup4">
                <ButtonGroup id="buttonGroup7" activeButton="1">
                    <Button id="buttonCode7_1" label="1"/>
                    <Button id="buttonCode7_2" label="2"/>
                </ButtonGroup>
                <InputText id="buttonGroupExampleCode4" label="{$.fh.docs.component.code}" width="md-12" rowsCount="4">
                    <![CDATA[
<ButtonGroup id="buttonGroup7" activeButton="1">
    <Button id="buttonCode7_1" label="1"/>
    <Button id="buttonCode7_2" label="2"/>
</ButtonGroup>
                        ]]>
                </InputText>
            </PanelGroup>

            <PanelGroup width="md-12" label="{$.fh.docs.component.buttongroup_group_with_multi_size_defined_for_different_displays}" id="_Form_TabContainer_Tab1_PanelGroup5">
                <ButtonGroup id="buttonGroup8" width="xs-12,sm-8,md-6,lg-2">
                    <Button id="buttonCode8_1" label="1"/>
                    <Button id="buttonCode8_2" label="2"/>
                </ButtonGroup>
                <InputText id="buttonGroupExampleCode5" label="{$.fh.docs.component.code}" width="md-12" rowsCount="4">
                    <![CDATA[
<ButtonGroup id="buttonGroup8" width="xs-12,sm-8,md-6,lg-2">
    <Button id="buttonCode8_1" label="1"/>
    <Button id="buttonCode8_2" label="2"/>
</ButtonGroup>
                        ]]>
                </InputText>
            </PanelGroup>

            <PanelGroup width="md-12" label="{$.fh.docs.component.buttongroup_group_with_margin}" id="_Form_TabContainer_Tab1_PanelGroup6">
                <ButtonGroup id="buttonGroup9" width="md-12" margin="true">
                    <Button id="buttonCode9_1" label="{$.fh.docs.component.buttongroup_1}"/>
                    <Button id="buttonCode9_2" label="{$.fh.docs.component.buttongroup_2}"/>
                    <Button id="buttonCode9_3" label="{$.fh.docs.component.buttongroup_3}"/>
                    <Button id="buttonCode9_4" label="{$.fh.docs.component.buttongroup_4}"/>
                    <Button id="buttonCode9_5" label="{$.fh.docs.component.buttongroup_5}"/>
                    <Button id="buttonCode9_6" label="{$.fh.docs.component.buttongroup_6}"/>
                </ButtonGroup>
                <InputText id="buttonGroupExampleCode6" label="{$.fh.docs.component.code}" width="md-12" rowsCount="8">
                    <![CDATA[
<ButtonGroup id="buttonGroup9" width="md-12" margin="true">
    <Button id="buttonCode9_1" label="1"/>
    <Button id="buttonCode9_2" label="2"/>
    <Button id="buttonCode9_3" label="3"/>
    <Button id="buttonCode9_4" label="4"/>
    <Button id="buttonCode9_5" label="5"/>
    <Button id="buttonCode9_6" label="6"/>
</ButtonGroup>
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