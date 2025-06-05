<?xml version="1.0" encoding="UTF-8"?>
<Form xmlns="http://fh.asseco.com/form/1.0" container="mainForm" id="documentationHolder" label="{componentName}">
    <AvailabilityConfiguration>
        <ReadOnly>checkBoxExampleCode1,checkBoxExampleCode2,checkBoxExampleCode3,checkBoxExampleCode4,checkBoxExampleCode5,checkBoxExampleCode6,checkBoxExampleCode4_2,checkBoxExampleCode7</ReadOnly>
    </AvailabilityConfiguration>
    <OutputLabel width="md-12" value="{description}" id="_Form_OutputLabel"/>
    <Spacer width="md-12" height="25px" id="_Form_Spacer"/>
    <TabContainer id="_Form_TabContainer">
        <Tab label="{$.fh.docs.component.examples}" id="_Form_TabContainer_Tab1">
                <PanelGroup width="md-12" label="{$.fh.docs.component.checkbox_with_label}" id="_Form_TabContainer_Tab1_PanelGroup1">
                    <CheckBox id="checbox1" label="{$.fh.docs.component.checkbox_to_be_or_not_to_be}" width="md-12"/>
                    <InputText id="checkBoxExampleCode1" label="{$.fh.docs.component.code}" width="md-12" rowsCount="2">
                        <![CDATA[
<CheckBox width="md-12" label="To be or not to be?" width="md-12"/>
                        ]]>
                    </InputText>
                </PanelGroup>

                <PanelGroup width="md-12" label="{$.fh.docs.component.checkbox_with_label_and_hint}" id="_Form_TabContainer_Tab1_PanelGroup2">
                    <CheckBox label="{$.fh.docs.component.checkbox_hover_over_me}" width="md-12" hint="This is example hint" id="_Form_TabContainer_Tab1_PanelGroup2_CheckBox"/>
                    <InputText id="checkBoxExampleCode7" label="{$.fh.docs.component.code}" width="md-12" rowsCount="2">
                        <![CDATA[
 <CheckBox label="Hover over me" width="md-12" hint="This is example hint"/>
                        ]]>
                    </InputText>
                </PanelGroup>

                <PanelGroup width="md-12" label="{$.fh.docs.component.checkbox_with_specified_height}" id="_Form_TabContainer_Tab1_PanelGroup3">
                    <CheckBox width="md-12" height="100" label="{$.fh.docs.component.checkbox_lorem_ipsum_dolor}" id="_Form_TabContainer_Tab1_PanelGroup3_CheckBox"/>

                    <InputText id="checkBoxExampleCode2" label="{$.fh.docs.component.code}" width="md-12" rowsCount="4">
                        <![CDATA[
<CheckBox height="300" label="Lorem ipsum dolor sit amet libero ac arcu nunc eget dolor massa ut quam quis nisl. Cras eu mauris.
Nullam aliquet. Morbi eget sem. Quisque lorem tortor justo nibh malesuada fames ac turpis vitae lorem sapien, non dui. Aliquam erat lacus,
suscipit ultricies, hendrerit magna lectus, eu aliquam tellus non risus. Morbi tellus felis augue nec augue. Vestibulum convallis posuere.
Quisque pellentesque quis, tincidunt in, vulputate risus dictum aliquet ipsum. Vestibulum dignissim turpis. Sed pellentesque quis, massa.
Nulla quis nisl. Curabitur fringilla ante ipsum ac ligula. Vivamus arcu elit, consequat faucibus, diam mollis vel, tortor. Nulla facilisi. Nullam sit amet."/>
                        ]]>
                    </InputText>
                </PanelGroup>

                <PanelGroup width="md-12" label="{$.fh.docs.component.checkbox_with_bootstrap_layout}" id="_Form_TabContainer_Tab1_PanelGroup4">
                    <CheckBox label="{$.fh.docs.component.checkbox_are_you_sure}" width="md-9" id="_Form_TabContainer_Tab1_PanelGroup4_CheckBox1"/>
                    <CheckBox label="{$.fh.docs.component.checkbox_are_you_really_sure}" width="md-3" id="_Form_TabContainer_Tab1_PanelGroup4_CheckBox2"/>
                    <InputText id="checkBoxExampleCode3" label="{$.fh.docs.component.code}" width="md-12" rowsCount="2">
                        <![CDATA[
<CheckBox label="Are You sure?"  width="md-9"/>
<CheckBox label="Are You really sure?" width="md-3"/>
                        ]]>
                    </InputText>
                </PanelGroup>

                <PanelGroup id="requiredGroupExample" width="md-12" label="{$.fh.docs.component.checkbox_with_required_value}">
                    <ValidateMessages componentIds="requiredGroupExample" level="error" id="_Form_TabContainer_Tab1_PanelGroup5_ValidateMessages"/>
                    <CheckBox id="checkboxExample1" label="{$.fh.docs.component.checkbox_this_text_will_be_red_when_is_not_checked}" value="{checkBoxValue}" width="md-12" required="true" onChange="+"/>

                    <InputText id="checkBoxExampleCode4" label="{$.fh.docs.component.code}" width="md-12" rowsCount="2">
                        <![CDATA[
<ValidateMessages componentIds="requiredGroupExample" level="error"/>
<CheckBox id="checkboxExample1" label="This text will be red when is not checked" value="\{checkBoxValue\}" width="md-12" required="true" onChange="+"/>
                        ]]>
                    </InputText>
                </PanelGroup>

                <PanelGroup width="md-12" label="{$.fh.docs.component.checkbox_with_onchange_event}" id="_Form_TabContainer_Tab1_PanelGroup6">
                    <CheckBox id="checkboxExample2" label="{$.fh.docs.component.checkbox_this_checkbox_is_bound_to_below_label}" value="{checkBoxOnChangeValue}" width="md-12" onChange="onChangeExample"/>
                    <OutputLabel id="checkBoxOnChangeBinding1" width="md-12" value="{$.fh.docs.component.checkbox_above_checkbox_is_selected}: {checkBoxOnChangeValue}"/>
                    <InputText id="checkBoxExampleCode5" label="{$.fh.docs.component.code}" width="md-12" rowsCount="2">
                        <![CDATA[
<CheckBox id="checkboxExample2" label="This CheckBox is bound to below label" value="\{checkBoxOnChangeValue\}" width="md-12" onChange="onChangeExample"/>
<OutputLabel id="checkBoxOnChangeBinding1" width="md-12" value="Above checkbox is selected: \{checkBoxOnChangeValue\}" />
                        ]]>
                    </InputText>
                </PanelGroup>

                <PanelGroup width="md-12" label="{$.fh.docs.component.checkbox_with_onchange_event_bound_to_another_checkbox}" id="_Form_TabContainer_Tab1_PanelGroup7">
                    <CheckBox id="checkboxExample3" label="{$.fh.docs.component.checkbox_this_checkbox_is_bound_to_below_checkbox}" value="{checkBoxToCheckBoxValue}" width="md-12" onChange="onChangeExample"/>
                    <CheckBox id="checkboxExample4" label="{$.fh.docs.component.checkbox_above_is_checked}" value="{checkBoxToCheckBoxValue}" width="md-12" onChange="onChangeExample"/>
                    <InputText id="checkBoxExampleCode6" label="{$.fh.docs.component.code}" width="md-12" rowsCount="4">
                        <![CDATA[
<CheckBox id="checkboxExample3" label="This CheckBox is bound to below CheckBox" value="\{checkBoxToCheckBoxValue\}" width="md-12" onChange="onChangeExample"/>
<CheckBox id="checkboxExample4" label="Above is checked" value="\{checkBoxToCheckBoxValue\}" width="md-12" onChange="onChangeExample"/>
                        ]]>
                    </InputText>
                </PanelGroup>

                <PanelGroup width="md-12" label="{$.fh.docs.component.checkbox_with_multiple_sizes_for_different_displays}" id="_Form_TabContainer_Tab1_PanelGroup8">
                    <CheckBox id="checkboxExample4_1" label="{$.fh.docs.component.checkbox_with_multiple_sizes}" width="xs-12,sm-8,md-4,lg-3"/>
                    <InputText id="checkBoxExampleCode4_2" label="{$.fh.docs.component.code}" width="md-12" rowsCount="2">
                        <![CDATA[
       <CheckBox id="checkboxExample4_1" label="Checkbox with multiple sizes" width="xs-12,sm-8,md-4,lg-3"/>
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