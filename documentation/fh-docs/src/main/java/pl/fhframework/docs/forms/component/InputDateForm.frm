<?xml version="1.0" encoding="UTF-8"?>
<Form xmlns="http://fh.asseco.com/form/1.0" container="mainForm" id="documentationHolder" label="{componentName}">
    <AvailabilityConfiguration>
        <ReadOnly>
            inputDateExampleCode1,inputDateExampleCode2,inputDateExampleCode3,inputDateExampleCode4,inputDateExampleCode5,onChangeBindedDate1,inputDateExampleCode6,
            inputDateExampleCode7,inputDateExampleCode8,inputDateExampleCode9,inputDateExampleCode9_1,
            inputDateExampleCode9_2,inputDateExampleCode9_3,inputDateExampleCode9_4,inputDateExampleCode9_5,inputDateExampleCode9_6,inputDateExampleCode9_7,
            inputDateExampleCode9_8,inputDateExampleCode9_9,inputDateExampleCode9_10,inputDateExampleCode10,inputDateExampleCode10_1,inputDateExampleCode10_2,
            disabledId
        </ReadOnly>
    </AvailabilityConfiguration>
    <OutputLabel width="md-12" value="{description}" id="_Form_OutputLabel"/>
    <Spacer width="md-12" height="25px" id="_Form_Spacer"/>
    <TabContainer id="_Form_TabContainer">
        <Tab label="{$.fh.docs.component.examples}" id="_Form_TabContainer_Tab1">
                <PanelGroup width="md-12" label="{$.fh.docs.component.inputdate_with_specified_height}" id="_Form_TabContainer_Tab1_PanelGroup1">
                    <InputDate width="md-3" label="{$.fh.docs.component.inputdate_release_date}" height="40" id="_Form_TabContainer_Tab1_PanelGroup1_InputDate"/>
                    <InputText id="inputDateExampleCode1" label="{$.fh.docs.component.code}" width="md-12" rowsCount="2">
                        <![CDATA[
<InputDate label="Release date" height="40"/>
                        ]]>
                    </InputText>
                </PanelGroup>

                <PanelGroup width="md-12" label="{$.fh.docs.component.inputdate_with_bootstrap_layout}" id="_Form_TabContainer_Tab1_PanelGroup2">
                    <InputDate label="{$.fh.docs.component.inputdate_expiration_date_from}" width="md-4" id="_Form_TabContainer_Tab1_PanelGroup2_InputDate1"/>
                    <InputDate label="{$.fh.docs.component.inputdate_expiration_date_to}" width="md-8" id="_Form_TabContainer_Tab1_PanelGroup2_InputDate2"/>
                    <InputText id="inputDateExampleCode2" label="{$.fh.docs.component.code}" width="md-12" rowsCount="2">
                        <![CDATA[
<InputDate label="Expiration date from:" width="md-4"/>
<InputDate label="Expiration date to:" width="md-8"/>
                        ]]>
                    </InputText>
                </PanelGroup>

                <PanelGroup width="md-12" label="{$.fh.docs.component.inputdate_with_predefined_value}" id="_Form_TabContainer_Tab1_PanelGroup3">
                    <InputDate label="{$.fh.docs.component.inputdate_christmas_time}" width="lg-6" value="2016-12-24" id="_Form_TabContainer_Tab1_PanelGroup3_InputDate"/>
                    <InputText id="inputDateExampleCode3" label="{$.fh.docs.component.code}" width="md-12" rowsCount="2">
                        <![CDATA[
<InputDate label="Christmas time:" width="lg-6" value="2016-12-24"/>
                        ]]>
                    </InputText>
                </PanelGroup>

                <PanelGroup width="md-12" label="{$.fh.docs.component.inputdate_with_predefined_value_and_hint}" id="_Form_TabContainer_Tab1_PanelGroup4">
                    <InputDate label="{$.fh.docs.component.inputdate_christmas_time}" width="lg-6" value="2016-12-24" hint="{$.fh.docs.component.inputdate_this_is_example_hint}" id="_Form_TabContainer_Tab1_PanelGroup4_InputDate"/>
                    <InputText id="inputDateExampleCode10" label="{$.fh.docs.component.code}" width="md-12" rowsCount="2">
                        <![CDATA[
<InputDate label="Christmas time:" width="lg-6" value="2016-12-24" hint="This is example hint"/>
                        ]]>
                    </InputText>
                </PanelGroup>

                <PanelGroup width="md-12" label="{$.fh.docs.component.inputdate_with_format_attribute_and_bound_to_java_util_date}" id="_Form_TabContainer_Tab1_PanelGroup5">
                    <InputDate label="{$.fh.docs.component.inputdate_this_field_has_yyyy_mm_dd_format}" format="YYYY/MM/DD" width="lg-6" onChange="-" value="{inputDateRequiredFormatExample}" id="_Form_TabContainer_Tab1_PanelGroup5_InputDate"/>
                    <OutputLabel width="md-12" value="{inputDateRequiredFormatExample}" id="_Form_TabContainer_Tab1_PanelGroup5_OutputLabel"/>
                    <InputText id="inputDateExampleCode6" label="{$.fh.docs.component.code}" width="md-12" rowsCount="2">
                        <![CDATA[
<InputDate label="This field has YYYY/MM/DD format:" format="YYYY/MM/DD" width="lg-6" onChange="-" value="\{inputDateRequiredFormatExample\}"/>
<OutputLabel value="\{inputDateRequiredFormatExample\}"/>
                        ]]>
                    </InputText>
                </PanelGroup>

                <PanelGroup width="md-12" label="{$.fh.docs.component.inputdate_with_required_value}" id="_Form_TabContainer_Tab1_PanelGroup6">
                    <InputDate label="{$.fh.docs.component.inputdate_this_field_is_required}" width="lg-6" required="true" onChange="-" value="{inputDateRequired}" id="_Form_TabContainer_Tab1_PanelGroup6_InputDate"/>
                    <InputText id="inputDateExampleCode4" label="{$.fh.docs.component.code}" width="md-12" rowsCount="2">
                        <![CDATA[
 <InputDate label="This field is required:" width="lg-6" required="true" onChange="-" value="\{inputDateRequired\}"/>
                        ]]>
                    </InputText>
                </PanelGroup>

                <PanelGroup width="md-12" label="{$.fh.docs.component.inputdate_with_onchange_event}" id="_Form_TabContainer_Tab1_PanelGroup7">
                    <InputDate label="{$.fh.docs.component.inputdate_this_date_is_bound_to_below_date}" width="md-12" value="{inputDateOnChange}" onChange="-" id="_Form_TabContainer_Tab1_PanelGroup7_InputDate1"/>
                    <InputDate id="onChangeBindedDate1" label="{$.fh.docs.component.inputdate_this_is_the_same_date_as_above}" width="lg-6" value="{inputDateOnChange}" onChange="-"/>

                    <InputText id="inputDateExampleCode5" label="{$.fh.docs.component.code}" width="lg-6" rowsCount="2">
                        <![CDATA[
<InputDate label="This date is bound to below date:" width="lg-6" value="\{inputDateOnChange\}" onChange="-"/>
<InputDate id="onChangeBindedDate1" label="This is the same date as above:" width="lg-6" value="\{inputDateOnChange\}" onChange="-"/>
                        ]]>
                    </InputText>
                </PanelGroup>

                <PanelGroup width="md-12" label="{$.fh.docs.component.inputdate_with_multi_size}" id="_Form_TabContainer_Tab1_PanelGroup8">
                    <InputDate label="{$.fh.docs.component.inputdate_with_multi_size}" width="sm-12,md-9,lg-6" id="_Form_TabContainer_Tab1_PanelGroup8_InputDate"/>
                    <InputText id="inputDateExampleCode7" label="{$.fh.docs.component.code}" width="md-12" rowsCount="2">
                        <![CDATA[
<InputDate label="InputDate with multi size" width="sm-12,md-9,lg-6"/>
                        ]]>
                    </InputText>
                </PanelGroup>

                <PanelGroup width="md-12" label="{$.fh.docs.component.inputdate_with_calendar_icon_aligned}" id="_Form_TabContainer_Tab1_PanelGroup9">
                    <InputDate label="{$.fh.docs.component.inputdate_this_calendar_icon_is_left_aligned}" width="md-4" icon="fas fa-tachometer-alt" iconAlignment="before" id="_Form_TabContainer_Tab1_PanelGroup9_InputDate"/>
                    <InputText id="inputDateExampleCode8" label="{$.fh.docs.component.code}" width="md-12" rowsCount="2">
                        <![CDATA[
<InputDate label="This calendar icon is left aligned:" width="md-4" icon="fas fa-tachometer-alt" iconAlignment="before"/>
                        ]]>
                    </InputText>
                </PanelGroup>

                <PanelGroup width="md-12" label="{$.fh.docs.component.inputdate_with_changed_position_of_a_label}" id="_Form_TabContainer_Tab1_PanelGroup10">
                    <Group id="_Form_TabContainer_Tab1_PanelGroup10_Group1">
                        <OutputLabel width="md-12" value="{$.fh.docs.component.inputdate_up_label}" id="_Form_TabContainer_Tab1_PanelGroup10_Group1_OutputLabel"/>
                        <InputDate id="inputDateCode8_1" width="lg-6" inputSize="60" labelPosition="up"/>
                    </Group>
                    <Group id="_Form_TabContainer_Tab1_PanelGroup10_Group2">
                        <InputDate label="{$.fh.docs.component.inputdate_down_label}" id="inputDateCode8_2" width="lg-6" inputSize="60" labelPosition="down"/>
                    </Group>
                    <Group id="_Form_TabContainer_Tab1_PanelGroup10_Group3">
                        <InputDate label="{$.fh.docs.component.inputdate_left_label}" id="inputDateCode8_3" width="lg-6" inputSize="60" labelPosition="left"/>
                    </Group>
                    <Group id="_Form_TabContainer_Tab1_PanelGroup10_Group4">
                        <InputDate label="{$.fh.docs.component.inputdate_right_label}" id="inputDateCode8_4" width="lg-6" inputSize="60" labelPosition="right"/>
                    </Group>

                    <InputText id="inputDateExampleCode10_2" label="{$.fh.docs.component.code}" width="md-12" rowsCount="4">
                        <![CDATA[![ESCAPE[
                                     <InputDate label="This calendar present position label" width="lg-6"/>
                                                                                          ]]]]>
                    </InputText>



                </PanelGroup>


                <PanelGroup width="md-12" label="{$.fh.docs.component.inputdate_disabled_inputdate}" id="_Form_TabContainer_Tab1_PanelGroup11">
                    <InputDate width="md-3" id="disabledId" label="{$.fh.docs.component.inputdate_release_date}" height="40"/>
                  <InputText id="inputDateExampleCode10_1" label="{$.fh.docs.component.code}" width="md-12" rowsCount="4">
                                         <![CDATA[
<Form ...>
    <AvailabilityConfiguration>
        <ReadOnly>
            disabledId
        </ReadOnly>
    </AvailabilityConfiguration>

    <InputDate id="disabledId" label="{$.fh.docs.component.inputdate_release_date}" height="40"/>
</Form>
]]>
                                     </InputText>
                </PanelGroup>

                <PanelGroup width="md-12" label="{$.fh.docs.component.inputdate_with_validation_rule}" id="_Form_TabContainer_Tab1_PanelGroup12">
                    <InputDate label="{$.fh.docs.component.inputdate_this_calendar_using_validation_if_given_date} " width="lg-6" onChange="+" validationRule="-inputDateBeforeValidationRule.isBefore(inputDateValidationRule)" value="{inputDateValidationRule}" id="_Form_TabContainer_Tab1_PanelGroup12_InputDate"/>
                    <InputText id="inputDateExampleCode9" label="{$.fh.docs.component.code}" width="md-12" rowsCount="4">
                        <![CDATA[![ESCAPE[
                    <InputDate label="This calendar using validation, if given date is before 26.04.2017: "
                               width="md-4" onChange="+"
                               validationRule="-inputDateBeforeValidationRule.isBefore(inputDateValidationRule)"
                               value="{inputDateValidationRule}"/>
                          ]]]]>
                    </InputText>
                </PanelGroup>
                <PanelGroup width="md-12" label="{$.fh.docs.component.inputdate_common_model}" id="_Form_TabContainer_Tab1_PanelGroup13">
                    <InputDate value="{inputDateValidationRule}" onChange="-" width="lg-6" id="_Form_TabContainer_Tab1_PanelGroup13_InputDate1"/>
                    <InputDate value="{inputDateValidationRule}" onChange="-" format="YYYY/MM/DD" width="lg-6" id="_Form_TabContainer_Tab1_PanelGroup13_InputDate2"/>
                    <InputText id="inputDateExampleCode9_10" label="{$.fh.docs.component.code}" width="md-12" rowsCount="2">
                        <![CDATA[![ESCAPE[
                    <InputDate value="{inputDateValidationRule}" onChange="-"/>
                    <InputDate value="{inputDateValidationRule}" onChange="-" format="YYYY/MM/DD"/>
                          ]]]]>
                    </InputText>
                </PanelGroup>
        </Tab>
        <Tab label="{$.fh.docs.component.inputdate_label_position}" id="_Form_TabContainer_Tab2">
            <PanelGroup width="md-12" label="{$.fh.docs.component.inputdate_with_changed_position_of_a_label}" id="_Form_TabContainer_Tab2_PanelGroup">
                <PanelGroup label="{$.fh.docs.component.inputdate_label_with_labelposition_up}" id="_Form_TabContainer_Tab2_PanelGroup_PanelGroup1">
                    <InputDate label="{$.fh.docs.component.inputdate_label}" id="inputDateCode9_1" width="lg-6" labelPosition="up" onChange="-"/>
                    <InputText id="inputDateExampleCode9_1" width="md-12" label="{$.fh.docs.component.code}">
                        <![CDATA[
<InputDate label="Label" id="inputDateCode9_1" width="lg-6" labelPosition="up" onChange="-"/>
                        ]]>
                    </InputText>
                </PanelGroup>
                <PanelGroup label="{$.fh.docs.component.inputdate_label_with_labelposition_down}" id="_Form_TabContainer_Tab2_PanelGroup_PanelGroup2">
                    <InputDate label="{$.fh.docs.component.inputdate_label}" id="inputDateCode9_2" width="lg-6" labelPosition="down" onChange="-"/>
                    <InputText id="inputDateExampleCode9_2" width="md-12" label="{$.fh.docs.component.code}">
                        <![CDATA[
<InputDate label="Label" id="inputDateCode9_2" width="lg-6" labelPosition="down" onChange="-"/>
                        ]]>
                    </InputText>
                </PanelGroup>
                <PanelGroup label="{$.fh.docs.component.inputdate_label_with_labelposition_left}" id="_Form_TabContainer_Tab2_PanelGroup_PanelGroup3">
                    <InputDate label="{$.fh.docs.component.inputdate_label}" width="lg-6" labelPosition="left" onChange="-" id="_Form_TabContainer_Tab2_PanelGroup_PanelGroup3_InputDate"/>
                    <InputText id="inputDateExampleCode9_3" width="md-12" label="{$.fh.docs.component.code}">
                        <![CDATA[
<InputDate label="Label" width="lg-6" labelPosition="left" onChange="-"/>
                        ]]>
                    </InputText>
                </PanelGroup>

                <PanelGroup label="{$.fh.docs.component.inputdate_label_with_labelposition_right}" id="_Form_TabContainer_Tab2_PanelGroup_PanelGroup4">
                    <InputDate label="{$.fh.docs.component.inputdate_label}" id="inputDateCode9_4" width="lg-6" labelPosition="right" onChange="-"/>
                    <InputText id="inputDateExampleCode9_4" width="md-12" label="{$.fh.docs.component.code}">
                        <![CDATA[
<InputDate label="Label" id="inputDateCode9_4" width="lg-6" labelPosition="right" onChange="-"/>
                        ]]>
                    </InputText>
                </PanelGroup>
                <PanelGroup label="{$.fh.docs.component.inputdate_label_with_labelposition_right_and_inputsize_20}" id="_Form_TabContainer_Tab2_PanelGroup_PanelGroup5">
                    <InputDate label="{$.fh.docs.component.inputdate_label}" id="inputDateCode9_5" width="lg-6" inputSize="20" labelPosition="right" onChange="-"/>
                    <InputText id="inputDateExampleCode9_5" width="md-12" label="{$.fh.docs.component.code}">
                        <![CDATA[
<InputDate label="Label" id="inputDateCode9_5" width="lg-6" inputSize="20" labelPosition="right" onChange="-"/>
                        ]]>
                    </InputText>
                </PanelGroup>
                <PanelGroup label="{$.fh.docs.component.inputdate_label_with_labelposition_right_and_inputsize_30}" id="_Form_TabContainer_Tab2_PanelGroup_PanelGroup6">
                    <InputDate label="{$.fh.docs.component.inputdate_label}" id="inputDateCode9_6" width="lg-6" inputSize="30" labelPosition="right" onChange="-"/>
                    <InputText id="inputDateExampleCode9_6" width="md-12" label="{$.fh.docs.component.code}">
                        <![CDATA[
<InputDate label="Label" id="inputDateCode9_6" width="lg-6" inputSize="30" labelPosition="right" onChange="-"/>
                        ]]>
                    </InputText>
                </PanelGroup>
                <PanelGroup label="{$.fh.docs.component.inputdate_label_with_labelposition_right_and_inputsize_50}" id="_Form_TabContainer_Tab2_PanelGroup_PanelGroup7">
                    <InputDate label="{$.fh.docs.component.inputdate_label}" width="lg-6" inputSize="50" labelPosition="right" onChange="-" id="_Form_TabContainer_Tab2_PanelGroup_PanelGroup7_InputDate"/>
                    <InputText id="inputDateExampleCode9_7" width="md-12" label="{$.fh.docs.component.code}">
                        <![CDATA[
<InputDate label="Label" width="lg-6" inputSize="50" labelPosition="right" onChange="-"/>
                        ]]>
                    </InputText>
                </PanelGroup>
                <PanelGroup label="{$.fh.docs.component.inputdate_label_with_labelposition_right_and_inputsize_90}" id="_Form_TabContainer_Tab2_PanelGroup_PanelGroup8">
                    <InputDate label="{$.fh.docs.component.inputdate_label}" width="lg-6" inputSize="90" labelPosition="right" onChange="-" id="_Form_TabContainer_Tab2_PanelGroup_PanelGroup8_InputDate"/>
                    <InputText id="inputDateExampleCode9_8" width="md-12" label="{$.fh.docs.component.code}">
                        <![CDATA[
<InputDate label="Label" width="lg-6" inputSize="90" labelPosition="right" onChange="-"/>
                        ]]>
                    </InputText>
                </PanelGroup>
                <PanelGroup label="{$.fh.docs.component.inputdate_label_with_labelposition_right_and_inputsize_100}" id="_Form_TabContainer_Tab2_PanelGroup_PanelGroup9">
                    <InputDate label="{$.fh.docs.component.inputdate_label}" width="lg-6" inputSize="100" labelPosition="right" onChange="-" id="_Form_TabContainer_Tab2_PanelGroup_PanelGroup9_InputDate"/>
                    <InputText id="inputDateExampleCode9_9" width="md-12" label="{$.fh.docs.component.code}">
                        <![CDATA[
<InputDate label="Label" width="lg-6" inputSize="100" labelPosition="right" onChange="-"/>
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