<?xml version="1.0" encoding="UTF-8"?>
<Form xmlns="http://fh.asseco.com/form/1.0" container="mainForm" id="documentationHolder" label="{componentName}">
    <AvailabilityConfiguration>
        <ReadOnly>
            inputNumberExampleCode1,inputNumberExampleCode2,inputNumberExampleCode3,inputNumberExampleCode4,inputNumberExampleCode5,
            inputNumberExampleCode5_1,inputNumberExampleCode5_2,inputNumberExampleCode5_3,inputNumberExampleCode5_4,
            inputNumberExampleCode5_5,inputNumberExampleCode5_6,inputNumberExampleCode5_7,inputNumberExampleCode5_8,inputNumberExampleCode5_9,inputNumberExampleCode6,
            inputNumberExampleCode2x
        </ReadOnly>
    </AvailabilityConfiguration>
    <OutputLabel width="md-12" value="{description}" id="_Form_OutputLabel"/>
    <Spacer width="md-12" height="25px" id="_Form_Spacer"/>
    <TabContainer id="_Form_TabContainer">
        <Tab label="{$.fh.docs.component.examples}" id="_Form_TabContainer_Tab1">
            <PanelGroup width="md-12" label="{$.fh.docs.component.inputnumber_simple}" id="_Form_TabContainer_Tab1_PanelGroup1">
                <InputNumber id="inputNumberCode1" label="{$.fh.docs.component.inputnumber_simple}" width="md-6"/>
                <InputText id="inputNumberExampleCode1" label="{$.fh.docs.component.code}" width="md-12">
                    <![CDATA[
<InputNumber id="inputNumberCode1" label="Simple InputNumber" width="md-6"/>
                        ]]>
                </InputText>
            </PanelGroup>

            <PanelGroup width="md-12" label="{$.fh.docs.component.inputnumber_simple_inputnumber_with_hint}" id="_Form_TabContainer_Tab1_PanelGroup2">
                <InputNumber label="{$.fh.docs.component.inputnumber_simple_inputnumber_with_hint}" width="md-6" hint="{$.fh.docs.component.inputnumber_this_is_hint}" id="_Form_TabContainer_Tab1_PanelGroup2_InputNumber"/>
                <InputText id="inputNumberExampleCode6" label="{$.fh.docs.component.code}" width="md-12">
                    <![CDATA[
<InputNumber label="Simple InputNumber" width="md-6" hint="This is hint"/>
                        ]]>
                </InputText>
            </PanelGroup>

            <PanelGroup width="md-12" label="{$.fh.docs.component.inputnumber_with_binding}" id="_Form_TabContainer_Tab1_PanelGroup3">
                <OutputLabel width="md-12" value="{$.fh.docs.component.inputnumber_binding_label}" id="_Form_TabContainer_Tab1_PanelGroup3_OutputLabel"/>
                <InputNumber id="inputNumberCode2_1" width="md-6" value="{onChangeValue}" onChange="-"/>
                <InputNumber id="inputNumberCode2_2" width="md-6" value="{onChangeValue}" onChange="-"/>
                <InputText id="inputNumberExampleCode2" label="{$.fh.docs.component.code}" width="md-12" rowsCount="2">
                    <![CDATA[
<InputNumber id="inputNumberCode2_1" width="md-6" value="\{onChangeValue\}" onChange="-"/>
<InputNumber id="inputNumberCode2_2" width="md-6" value="\{onChangeValue\}" onChange="-"/>
                        ]]>
                </InputText>
            </PanelGroup>
            <PanelGroup width="md-12" label="{$.fh.docs.component.inputnumber_with_binding_of_different_types}" id="_Form_TabContainer_Tab1_PanelGroup4">
                <InputNumber id="inputNumberCode2x_1_1" width="md-6" value="{bigDecAttr}" onChange="-" label="BigDecimal 1.1"/>
                <InputNumber id="inputNumberCode2x_1_2" width="md-6" value="{bigDecAttr}" onChange="-" label="BigDecimal 1.2"/>
                <InputNumber id="inputNumberCode2x_2_1" width="md-6" value="{doubleAttr}" onChange="-" label="Double 1.1"/>
                <InputNumber id="inputNumberCode2x_2_2" width="md-6" value="{doubleAttr}" onChange="-" label="Double 1.2"/>
                <InputNumber id="inputNumberCode2x_3_1" width="md-6" value="{intAttr}" onChange="-" maxFractionDigits="0" label="Integer 1.1"/>
                <InputNumber id="inputNumberCode2x_3_2" width="md-6" value="{intAttr}" onChange="-" maxFractionDigits="0" label="Integer 1.2"/>
                <InputText id="inputNumberExampleCode2x" label="{$.fh.docs.component.code}" width="md-12" rowsCount="6">
                    <![CDATA[![ESCAPE[
<InputNumber id="inputNumberCode2x_1_1" width="md-6" value="{bigDecAttr} 1.1" onChange="-" label="BigDecimal"/>
<InputNumber id="inputNumberCode2x_1_2" width="md-6" value="{bigDecAttr} 1.2" onChange="-" label="BigDecimal"/>
<InputNumber id="inputNumberCode2x_2_1" width="md-6" value="{doubleAttr} 1.1" onChange="-" label="Double"/>
<InputNumber id="inputNumberCode2x_2_2" width="md-6" value="{doubleAttr} 1.2" onChange="-" label="Double"/>
<InputNumber id="inputNumberCode2x_3_1" width="md-6" value="{intAttr}" onChange="-" maxFractionDigits="0" label="Integer 1.1"/>
<InputNumber id="inputNumberCode2x_3_2" width="md-6" value="{intAttr}" onChange="-" maxFractionDigits="0" label="Integer 1.2"/>
                            ]]]]>
                </InputText>
            </PanelGroup>

            <PanelGroup width="md-12" label="{$.fh.docs.component.inputnumber_with_required_value}" id="_Form_TabContainer_Tab1_PanelGroup5">
                <InputNumber id="inputNumberCode3" width="md-6" value="{requiredValue}" onChange="+" required="true"/>
                <InputText id="inputNumberExampleCode3" label="{$.fh.docs.component.code}" width="md-12">
                    <![CDATA[
<InputNumber id="inputNumberCode3" width="md-6" value="\{requiredValue\}" onChange="+" required="true" />
                        ]]>
                </InputText>
            </PanelGroup>

            <PanelGroup width="md-12" label="{$.fh.docs.component.inputnumber_with_multiple_sizes_for_different_displays}" id="_Form_TabContainer_Tab1_PanelGroup6">
                <InputNumber id="inputNumberCode4" width="sm-12,md-6,lg-3"/>
                <InputText id="inputNumberExampleCode4" label="{$.fh.docs.component.code}" width="md-12">
                    <![CDATA[
<InputNumber id="inputNumberCode4" width="sm-12,md-6,lg-3" />
                        ]]>
                </InputText>
            </PanelGroup>

            <PanelGroup width="md-12" label="{$.fh.docs.component.inputnumber_with_validation_rule}" id="_Form_TabContainer_Tab1_PanelGroup7">
                <ValidateMessages componentIds="inputNumberCode5" level="error" id="_Form_TabContainer_Tab1_PanelGroup7_ValidateMessages"/>
                <InputNumber id="inputNumberCode5" width="md-6" validationRule="-intAttr gt 5" value="{intAttr}" onChange="+"/>
                <InputText id="inputNumberExampleCode5" label="{$.fh.docs.component.code}" width="md-12">
                    <![CDATA[![ESCAPE[
<InputNumber id="inputNumberCode5" width="md-6" validationRule="-intAttr gt 5" value="{intAttr}"/>
                          ]]]]>
                </InputText>
            </PanelGroup>
        </Tab>
        <Tab label="{$.fh.docs.component.inputnumber_label_position}" id="_Form_TabContainer_Tab2">
            <PanelGroup width="md-12" label="{$.fh.docs.component.inputnumber_with_changed_position_of_a_label}" id="_Form_TabContainer_Tab2_PanelGroup">
                <PanelGroup label="{$.fh.docs.component.inputnumber_with_labelposition_up}" id="_Form_TabContainer_Tab2_PanelGroup_PanelGroup1">
                    <InputNumber label="{$.fh.docs.component.inputnumber_label}" id="inputNumberCode5_1" width="sm-3" labelPosition="up" onChange="-"/>
                    <InputText id="inputNumberExampleCode5_1" width="md-12" label="{$.fh.docs.component.code}">
                        <![CDATA[
<InputNumber label="Label" id="inputNumberCode5_1" width="sm-3" labelPosition="up" onChange="-"/>
                        ]]>
                    </InputText>
                </PanelGroup>
                <PanelGroup label="{$.fh.docs.component.inputnumber_label_with_labelposition_down}" id="_Form_TabContainer_Tab2_PanelGroup_PanelGroup2">
                    <InputNumber label="{$.fh.docs.component.inputnumber_label}" id="inputNumberCode5_2" width="sm-3" labelPosition="down" onChange="-"/>
                    <InputText id="inputNumberExampleCode5_2" width="md-12" label="{$.fh.docs.component.code}">
                        <![CDATA[
<InputNumber label="Label" id="inputNumberCode5_2" width="sm-3" labelPosition="down" onChange="-"/>
                        ]]>
                    </InputText>
                </PanelGroup>
                <PanelGroup label="{$.fh.docs.component.inputnumber_label_with_labelposition_left}" id="_Form_TabContainer_Tab2_PanelGroup_PanelGroup3">
                    <InputNumber label="{$.fh.docs.component.inputnumber_label}" width="sm-3" labelPosition="left" onChange="-" id="_Form_TabContainer_Tab2_PanelGroup_PanelGroup3_InputNumber"/>
                    <InputText id="inputNumberExampleCode5_3" width="md-12" label="{$.fh.docs.component.code}">
                        <![CDATA[
<InputNumber label="Label" width="sm-3" labelPosition="left" onChange="-"/>
                        ]]>
                    </InputText>
                </PanelGroup>

                <PanelGroup label="{$.fh.docs.component.inputnumber_label_with_labelposition_right}" id="_Form_TabContainer_Tab2_PanelGroup_PanelGroup4">
                    <InputNumber label="{$.fh.docs.component.inputnumber_label}" id="inputNumberCode5_4" width="sm-3" labelPosition="right" onChange="-"/>
                    <InputText id="inputNumberExampleCode5_4" width="md-12" label="{$.fh.docs.component.code}">
                        <![CDATA[
<InputNumber label="Label" id="inputNumberCode5_4" width="sm-3" labelPosition="right" onChange="-"/>
                        ]]>
                    </InputText>
                </PanelGroup>
                <PanelGroup label="{$.fh.docs.component.inputnumber_label_with_labelposition_right__and_inputsize_20}" id="_Form_TabContainer_Tab2_PanelGroup_PanelGroup5">
                    <InputNumber label="{$.fh.docs.component.inputnumber_label}" id="inputNumberCode5_5" width="sm-3" inputSize="20" labelPosition="right" onChange="-"/>
                    <InputText id="inputNumberExampleCode5_5" width="md-12" label="{$.fh.docs.component.code}">
                        <![CDATA[
<InputNumber label="Label" id="inputNumberCode5_5" width="sm-3" inputSize="20" labelPosition="right" onChange="-"/>
                        ]]>
                    </InputText>
                </PanelGroup>
                <PanelGroup label="{$.fh.docs.component.inputnumber_label_with_labelposition_right__and_inputsize_30}" id="_Form_TabContainer_Tab2_PanelGroup_PanelGroup6">
                    <InputNumber label="{$.fh.docs.component.inputnumber_label}" id="inputNumberCode5_6" width="sm-3" inputSize="30" labelPosition="right" onChange="-"/>
                    <InputText id="inputNumberExampleCode5_6" width="md-12" label="{$.fh.docs.component.code}">
                        <![CDATA[
<InputNumber label="Label" id="inputNumberCode5_6" width="sm-3" inputSize="30" labelPosition="right" onChange="-"/>
                        ]]>
                    </InputText>
                </PanelGroup>
                <PanelGroup label="{$.fh.docs.component.inputnumber_label_with_labelposition_right__and_inputsize_50}" id="_Form_TabContainer_Tab2_PanelGroup_PanelGroup7">
                    <InputNumber label="{$.fh.docs.component.inputnumber_label}" width="sm-3" inputSize="50" labelPosition="right" onChange="-" id="_Form_TabContainer_Tab2_PanelGroup_PanelGroup7_InputNumber"/>
                    <InputText id="inputNumberExampleCode5_7" width="md-12" label="{$.fh.docs.component.code}">
                        <![CDATA[
<InputNumber label="Label" width="sm-3" inputSize="50" labelPosition="right" onChange="-"/>
                        ]]>
                    </InputText>
                </PanelGroup>
                <PanelGroup label="{$.fh.docs.component.inputnumber_label_with_labelposition_right__and_inputsize_90}" id="_Form_TabContainer_Tab2_PanelGroup_PanelGroup8">
                    <InputNumber label="{$.fh.docs.component.inputnumber_label}" width="sm-3" inputSize="90" labelPosition="right" onChange="-" id="_Form_TabContainer_Tab2_PanelGroup_PanelGroup8_InputNumber"/>
                    <InputText id="inputNumberExampleCode5_8" width="md-12" label="{$.fh.docs.component.code}">
                        <![CDATA[
<InputNumber label="Label" width="sm-3" inputSize="90" labelPosition="right" onChange="-"/>
                        ]]>
                    </InputText>
                </PanelGroup>
                <PanelGroup label="{$.fh.docs.component.inputnumber_label_with_labelposition_right__and_inputsize_100}" id="_Form_TabContainer_Tab2_PanelGroup_PanelGroup9">
                    <InputNumber label="{$.fh.docs.component.inputnumber_label}" width="sm-3" inputSize="100" labelPosition="right" onChange="-" id="_Form_TabContainer_Tab2_PanelGroup_PanelGroup9_InputNumber"/>
                    <InputText id="inputNumberExampleCode5_9" width="md-12" label="{$.fh.docs.component.code}">
                        <![CDATA[
<InputNumber label="Label" width="sm-3" inputSize="100" labelPosition="right" onChange="-"/>
                        ]]>
                    </InputText>
                </PanelGroup>
            </PanelGroup>
        </Tab>

        <Tab label="{$.fh.docs.component.attributes}" id="_Form_TabContainer_Tab3">
            <Table iterator="item" collection="{attributes}" id="_Form_TabContainer_Tab3_Table">
                <Column label="{$.fh.docs.component.attributes_identifier}" value="{item.name}" width="15" id="_Form_TabContainer_Tab3_Table_Column1"/>
                <Column label="{$.fh.docs.component.attributes_type}" value="{item.type}" width="15" id="_Form_TabContainer_Tab3_Table_Column2"/>
                <Column label="{$.fh.docs.component.attributes_boundable}" value="{item.boundable}" width="10" id="_Form_TabContainer_Tab3_Table_Column3"/>
                <Column label="{$.fh.docs.component.attributes_default_value}" value="{item.defaultValue}" width="20" id="_Form_TabContainer_Tab3_Table_Column4"/>
                <Column label="{$.fh.docs.component.attributes_description}" value="{item.description}" width="40" id="_Form_TabContainer_Tab3_Table_Column5"/>
            </Table>
        </Tab>
    </TabContainer>

    <Button id="pBack" label="[icon='fa fa-chevron-left'] {$.fh.docs.component.back}" onClick="backToFormComponentsList()"/>

</Form>