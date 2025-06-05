<?xml version="1.0" encoding="UTF-8"?>
<Form xmlns="http://fh.asseco.com/form/1.0" container="mainForm" id="documentationHolder" label="{componentName}">
    <AvailabilityConfiguration>
        <ReadOnly>
            dropdownItemExampleCode1,dropdownItemExampleCode2,dropdownItemExampleCode3,dropdownItemExampleCode4,dropdownItemExampleCode5,dropdownItemExampleCode6,dropdownItemExampleCode7,dropdownItemExampleCode8,dropdownItemExampleCode9,dropdownItemExampleCode10,dropdownItemExampleCode11
        </ReadOnly>
    </AvailabilityConfiguration>
    <OutputLabel width="md-12" value="{description}" id="_Form_OutputLabel"/>
    <Spacer width="md-12" height="25px" id="_Form_Spacer"/>
    <TabContainer id="_Form_TabContainer">
        <Tab label="{$.fh.docs.component.examples}" id="_Form_TabContainer_Tab1">
            <PanelGroup width="md-12" label="{$.fh.docs.component.dropdown_simple}" id="_Form_TabContainer_Tab1_PanelGroup1">
                <Dropdown width="md-3" label="{$.fh.docs.component.dropdown_simple}" id="_Form_TabContainer_Tab1_PanelGroup1_Dropdown">
                    <DropdownItem value="{$.fh.docs.component.dropdown_dropdownitem_without_icon}" id="_Form_TabContainer_Tab1_PanelGroup1_Dropdown_DropdownItem1"/>
                    <DropdownItem value="{$.fh.docs.component.dropdown_dropdownitem_with_icon_before}" icon="fa fa-asterisk" id="_Form_TabContainer_Tab1_PanelGroup1_Dropdown_DropdownItem2"/>
                    <DropdownItem value="{$.fh.docs.component.dropdown_dropdownitem_with_icon_after}" icon="fa fa-asterisk" iconAlignment="after" id="_Form_TabContainer_Tab1_PanelGroup1_Dropdown_DropdownItem3"/>
                </Dropdown>
                <InputText id="dropdownItemExampleCode1" label="{$.fh.docs.component.code}" width="md-12" rowsCount="5">
                    <![CDATA[
<Dropdown label="Simple Dropdown">
    <DropdownItem value="DropdownItem without icon"/>
    <DropdownItem value="DropdownItem with icon before" icon="fa fa-asterisk"/>
    <DropdownItem value="DropdownItem with icon after" icon="fa fa-asterisk" iconAlignment="after"/>
</Dropdown>
                        ]]>
                </InputText>
            </PanelGroup>

            <PanelGroup width="md-12" label="{$.fh.docs.component.dropdown_simple_with_hint}" id="_Form_TabContainer_Tab1_PanelGroup2">
                <Dropdown width="md-3" label="{$.fh.docs.component.dropdown_simple_with_hint}" hint="{$.fh.docs.component.dropdown_this_is_example_hint}" id="_Form_TabContainer_Tab1_PanelGroup2_Dropdown">
                    <DropdownItem value="{$.fh.docs.component.dropdown_dropdownitem_without_icon}" id="_Form_TabContainer_Tab1_PanelGroup2_Dropdown_DropdownItem1"/>
                    <DropdownItem value="{$.fh.docs.component.dropdown_dropdownitem_with_icon_before}" icon="fa fa-asterisk" id="_Form_TabContainer_Tab1_PanelGroup2_Dropdown_DropdownItem2"/>
                    <DropdownItem value="{$.fh.docs.component.dropdown_dropdownitem_with_icon_after}" icon="fa fa-asterisk" iconAlignment="after" id="_Form_TabContainer_Tab1_PanelGroup2_Dropdown_DropdownItem3"/>
                </Dropdown>
                <InputText id="dropdownItemExampleCode11" label="{$.fh.docs.component.code}" width="md-12" rowsCount="5">
                    <![CDATA[
<Dropdown label="Simple Dropdown with hint" hint="This is example hint">
    <DropdownItem value="DropdownItem without icon"/>
    <DropdownItem value="DropdownItem with icon before" icon="fa fa-asterisk"/>
    <DropdownItem value="DropdownItem with icon after" icon="fa fa-asterisk" iconAlignment="after"/>
</Dropdown>]]>
                </InputText>
            </PanelGroup>

            <PanelGroup width="md-12" label="{$.fh.docs.component.dropdown_with_changed_height}" id="_Form_TabContainer_Tab1_PanelGroup3">
                <Dropdown width="md-3" label="{$.fh.docs.component.dropdown_dropdown}" height="80" id="_Form_TabContainer_Tab1_PanelGroup3_Dropdown">
                    <DropdownItem value="DropdownItem 1" id="_Form_TabContainer_Tab1_PanelGroup3_Dropdown_DropdownItem1"/>
                    <DropdownItem value="DropdownItem 2" id="_Form_TabContainer_Tab1_PanelGroup3_Dropdown_DropdownItem2"/>
                    <DropdownItem value="DropdownItem 3" id="_Form_TabContainer_Tab1_PanelGroup3_Dropdown_DropdownItem3"/>
                </Dropdown>
                <InputText id="dropdownItemExampleCode2" label="{$.fh.docs.component.code}" width="md-12" rowsCount="5">
                    <![CDATA[
<Dropdown label="Dropdown" height="80">
    <DropdownItem value="DropdownItem 1"/>
    <DropdownItem value="DropdownItem 2"/>
    <DropdownItem value="DropdownItem 3"/>
</Dropdown>
                        ]]>
                </InputText>
            </PanelGroup>

            <PanelGroup width="md-12" label="{$.fh.docs.component.dropdown_with_changed_size}" id="_Form_TabContainer_Tab1_PanelGroup4">
                <Dropdown label="{$.fh.docs.component.dropdown_dropdown}" width="md-6" id="_Form_TabContainer_Tab1_PanelGroup4_Dropdown">
                    <DropdownItem value="DropdownItem 1" id="_Form_TabContainer_Tab1_PanelGroup4_Dropdown_DropdownItem1"/>
                    <DropdownItem value="DropdownItem 2" id="_Form_TabContainer_Tab1_PanelGroup4_Dropdown_DropdownItem2"/>
                    <DropdownItem value="DropdownItem 3" id="_Form_TabContainer_Tab1_PanelGroup4_Dropdown_DropdownItem3"/>
                </Dropdown>
                <InputText id="dropdownItemExampleCode3" label="{$.fh.docs.component.code}" width="md-12" rowsCount="5">
                    <![CDATA[
<Dropdown label="Dropdown" width="md-6">
    <DropdownItem value="DropdownItem 1"/>
    <DropdownItem value="DropdownItem 2"/>
    <DropdownItem value="DropdownItem 3"/>
</Dropdown>
                        ]]>
                </InputText>
            </PanelGroup>

            <PanelGroup width="md-12" label="{$.fh.docs.component.dropdown_alignment}" id="_Form_TabContainer_Tab1_PanelGroup5">
                    <OutputLabel width="md-12" value="{$.fh.docs.component.dropdown_dropdown_aligned_to_left}" id="_Form_TabContainer_Tab1_PanelGroup5_OutputLabel1"/>
                    <Spacer width="md-12" id="_Form_TabContainer_Tab1_PanelGroup5_Spacer1"/>
                    <Dropdown width="md-3" label="{$.fh.docs.component.dropdown_left_dropdown}" horizontalAlign="left" id="_Form_TabContainer_Tab1_PanelGroup5_Dropdown1">
                        <DropdownItem value="DropdownItem 1" id="_Form_TabContainer_Tab1_PanelGroup5_Dropdown1_DropdownItem1"/>
                        <DropdownItem value="DropdownItem 2" id="_Form_TabContainer_Tab1_PanelGroup5_Dropdown1_DropdownItem2"/>
                        <DropdownItem value="DropdownItem 3" id="_Form_TabContainer_Tab1_PanelGroup5_Dropdown1_DropdownItem3"/>
                    </Dropdown>
                    <InputText id="dropdownItemExampleCode4" label="{$.fh.docs.component.code}" width="md-12" rowsCount="7">
                        <![CDATA[
<OutputLabel value="Dropdown aligned to left"/>
<Spacer/>
<Dropdown label="Left Dropdown" horizontalAlign="left">
    <DropdownItem value="DropdownItem 1"/>
    <DropdownItem value="DropdownItem 2"/>
    <DropdownItem value="DropdownItem 3"/>
</Dropdown>
                        ]]>
                    </InputText>
                    <OutputLabel width="md-12" horizontalAlign="right" value="{$.fh.docs.component.dropdown_dropdown_aligned_to_right}" id="_Form_TabContainer_Tab1_PanelGroup5_OutputLabel2"/>
                    <Spacer width="md-12" id="_Form_TabContainer_Tab1_PanelGroup5_Spacer2"/>
                    <Dropdown width="md-3" label="{$.fh.docs.component.dropdown_right_dropdown}" horizontalAlign="right" id="_Form_TabContainer_Tab1_PanelGroup5_Dropdown2">
                        <DropdownItem value="DropdownItem 1" id="_Form_TabContainer_Tab1_PanelGroup5_Dropdown2_DropdownItem1"/>
                        <DropdownItem value="DropdownItem 2" id="_Form_TabContainer_Tab1_PanelGroup5_Dropdown2_DropdownItem2"/>
                        <DropdownItem value="DropdownItem 3" id="_Form_TabContainer_Tab1_PanelGroup5_Dropdown2_DropdownItem3"/>
                    </Dropdown>
                    <InputText id="dropdownItemExampleCode5" label="{$.fh.docs.component.code}" width="md-12" rowsCount="7">
                        <![CDATA[
<OutputLabel horizontalAlign="right" value="Dropdown aligned to right"/>
<Spacer/>
<Dropdown label="Right dropdown" horizontalAlign="right">
    <DropdownItem value="DropdownItem 1"/>
    <DropdownItem value="DropdownItem 2"/>
    <DropdownItem value="DropdownItem 3"/>
</Dropdown>
                        ]]>
                    </InputText>
                </PanelGroup>
            <PanelGroup width="md-12" label="{$.fh.docs.component.dropdown_inside_buttongroup}" id="_Form_TabContainer_Tab1_PanelGroup6">
                <ButtonGroup width="md-6" id="_Form_TabContainer_Tab1_PanelGroup6_ButtonGroup">
                    <Button label="{$.fh.docs.component.dropdown_button_1}" id="_Form_TabContainer_Tab1_PanelGroup6_ButtonGroup_Button1"/>
                    <Button label="{$.fh.docs.component.dropdown_button_2}" id="_Form_TabContainer_Tab1_PanelGroup6_ButtonGroup_Button2"/>
                    <Dropdown width="md-3" label="{$.fh.docs.component.dropdown_dropdown}" id="_Form_TabContainer_Tab1_PanelGroup6_ButtonGroup_Dropdown">
                        <DropdownItem value="DropdownItem 1" id="_Form_TabContainer_Tab1_PanelGroup6_ButtonGroup_Dropdown_DropdownItem1"/>
                        <DropdownItem value="DropdownItem 2" id="_Form_TabContainer_Tab1_PanelGroup6_ButtonGroup_Dropdown_DropdownItem2"/>
                        <DropdownItem value="DropdownItem 3" id="_Form_TabContainer_Tab1_PanelGroup6_ButtonGroup_Dropdown_DropdownItem3"/>
                    </Dropdown>
                </ButtonGroup>
                <InputText id="dropdownItemExampleCode6" label="{$.fh.docs.component.code}" width="md-12" rowsCount="9">
                    <![CDATA[
<ButtonGroup width="md-6">
    <Button label="Button 1"/>
    <Button label="Button 2"/>
    <Dropdown label="Dropdown">
        <DropdownItem value="DropdownItem 1"/>
        <DropdownItem value="DropdownItem 2"/>
        <DropdownItem value="DropdownItem 3"/>
    </Dropdown>
</ButtonGroup>
                        ]]>
                </InputText>
            </PanelGroup>

            <PanelGroup width="md-12" label="{$.fh.docs.component.dropdown_with_bound_label}" id="_Form_TabContainer_Tab1_PanelGroup7">
                <Dropdown width="md-3" label="{dropdownLabel}" id="_Form_TabContainer_Tab1_PanelGroup7_Dropdown">
                    <DropdownItem value="Change Dropdown label" onClick="onDropdownItemClick()" id="_Form_TabContainer_Tab1_PanelGroup7_Dropdown_DropdownItem1"/>
                    <DropdownItem value="DropdownItem 2" id="_Form_TabContainer_Tab1_PanelGroup7_Dropdown_DropdownItem2"/>
                </Dropdown>
                <InputText id="dropdownItemExampleCode7" label="{$.fh.docs.component.code}" width="md-12" rowsCount="5">
                    <![CDATA[
<Dropdown label="\{dropdownLabel\}">
    <DropdownItem value="Change Dropdown label"
                  onClick="onDropdownItemClick()"/>
    <DropdownItem value="DropdownItem 2"/>
</Dropdown>
                        ]]>
                </InputText>
            </PanelGroup>
            <PanelGroup width="md-12" label="{$.fh.docs.component.dropdown_with_sizes_for_different_displays}" id="_Form_TabContainer_Tab1_PanelGroup8">
                <OutputLabel width="md-12" value="{$.fh.docs.component.dropdown_change_width_of_browser_window}" id="_Form_TabContainer_Tab1_PanelGroup8_OutputLabel"/>
                <Dropdown label="{$.fh.docs.component.dropdown_dropdown}" width="sm-12,md-9,lg-6" id="_Form_TabContainer_Tab1_PanelGroup8_Dropdown">
                    <DropdownItem value="DropdownItem 1" id="_Form_TabContainer_Tab1_PanelGroup8_Dropdown_DropdownItem1"/>
                    <DropdownItem value="DropdownItem 2" id="_Form_TabContainer_Tab1_PanelGroup8_Dropdown_DropdownItem2"/>
                </Dropdown>
                <InputText id="dropdownItemExampleCode8" label="{$.fh.docs.component.code}" width="md-12" rowsCount="4">
                    <![CDATA[
<Dropdown label="Dropdown" width="xs-12,sm-10,md-6,lg-2">
    <DropdownItem value="DropdownItem 1"/>
    <DropdownItem value="DropdownItem 2"/>
</Dropdown>
                        ]]>
                </InputText>
            </PanelGroup>

            <PanelGroup width="md-12" label="{$.fh.docs.component.dropdown_with_link}" id="_Form_TabContainer_Tab1_PanelGroup9">
                <Dropdown width="md-3" label="{$.fh.docs.component.dropdown_dropdown}" id="_Form_TabContainer_Tab1_PanelGroup9_Dropdown">
                    <DropdownItem value="google.pl" url="http://google.pl" id="_Form_TabContainer_Tab1_PanelGroup9_Dropdown_DropdownItem1"/>
                    <DropdownItem value="Logout" url="logout" id="_Form_TabContainer_Tab1_PanelGroup9_Dropdown_DropdownItem2"/>
                </Dropdown>
                <InputText id="dropdownItemExampleCode9" label="{$.fh.docs.component.code}" width="md-12" rowsCount="4">
                    <![CDATA[
<Dropdown label="Dropdown" >
    <DropdownItem  value="google.pl" url="http://google.pl"/>
    <DropdownItem  value="Logout" url="/logout"/>
</Dropdown>
                        ]]>
                </InputText>
            </PanelGroup>

            <PanelGroup width="md-12" label="{$.fh.docs.component.dropdown_with_bootstrap_style}" id="_Form_TabContainer_Tab1_PanelGroup10">
                <Dropdown width="md-3" label="{$.fh.docs.component.dropdown_dropdown}" style="warning" id="_Form_TabContainer_Tab1_PanelGroup10_Dropdown">
                    <DropdownItem value="DropdownItem 1" id="_Form_TabContainer_Tab1_PanelGroup10_Dropdown_DropdownItem1"/>
                    <DropdownItem value="DropdownItem 2" id="_Form_TabContainer_Tab1_PanelGroup10_Dropdown_DropdownItem2"/>
                </Dropdown>
                <InputText id="dropdownItemExampleCode10" label="{$.fh.docs.component.code}" width="md-12" rowsCount="4">
                    <![CDATA[
<Dropdown label="Dropdown" style="warning">
    <DropdownItem  value="DropdownItem 1" />
    <DropdownItem  value="DropdownItem 2" />
</Dropdown>
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