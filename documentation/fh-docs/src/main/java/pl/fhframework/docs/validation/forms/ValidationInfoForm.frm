<?xml version="1.0" encoding="UTF-8"?>
<Form xmlns="http://fh.asseco.com/form/1.0" container="mainForm" label="{$.fh.docs.validation_new_company_employee}" id="ValidationInfoForm">
    <AvailabilityConfiguration>
        <ReadOnly>exampleValidationXmlCode1,exampleValidationJavaCode1,exampleValidationXmlCode2,exampleValidationJavaCode2,exampleValidationJavaCode3,exampleValidationJavaCode4,exampleValidationJavaCodeJsr,exampleValidationXmlCode5,iNonEditableField</ReadOnly>
        <Invisible>iHiddenField</Invisible>
        <SetByProgrammer>
            descriptionGroup, exampleGroupSimple, exampleGroupJsr, exampleGroupCombined, exampleGroupValidationRule,exampleGroupAlternate
        </SetByProgrammer>
    </AvailabilityConfiguration>

    <TabContainer activeTabIndex="{activeTabId}" onTabChange="onTabAction" id="_Form_TabContainer">
        <Tab id="descriptionTab" label="{$.fh.docs.validation_description}">
            <PanelGroup id="descriptionGroup" label="{$.fh.docs.validation_description}" width="md-12">
                <OutputLabel width="md-12" value="{$.fh.docs.validation_validation_in_fh_is_executed_after_data}:" id="_Form_TabContainer_Tab1_PanelGroup1_OutputLabel1"/>
                <Spacer width="md-12" height="10" id="_Form_TabContainer_Tab1_PanelGroup1_Spacer1"/>
                <OutputLabel width="md-12" value="             1. {$.fh.docs.validation_first_way_of_validation_is_provided_by_fh}." id="_Form_TabContainer_Tab1_PanelGroup1_OutputLabel2"/>
                <OutputLabel width="md-12" value="             2. {$.fh.docs.validation_second_way_is_when_user_want_to_skip_fh_process_validation}." id="_Form_TabContainer_Tab1_PanelGroup1_OutputLabel3"/>
                <OutputLabel width="md-12" value="             3. {$.fh.docs.validation_developer_can_combine_fh_validation_with_custom_validation}." id="_Form_TabContainer_Tab1_PanelGroup1_OutputLabel4"/>
                <OutputLabel width="md-12" value="             4. {$.fh.docs.validation_developer_can_use_jsr_303_spec_validation_on_model}." id="_Form_TabContainer_Tab1_PanelGroup1_OutputLabel5"/>
                <OutputLabel width="md-12" value="             5. {$.fh.docs.validation_default_level}." id="_Form_TabContainer_Tab1_PanelGroup1_OutputLabel6"/>
                <OutputLabel width="md-12" value="             6. {$.fh.docs.validation_default_action}." id="_Form_TabContainer_Tab1_PanelGroup1_OutputLabel7"/>
                <OutputLabel width="md-12" value="             7. {$.fh.docs.validation_default_matrix}." id="_Form_TabContainer_Tab1_PanelGroup1_OutputLabel8"/>
                <Spacer width="md-12" height="10" id="_Form_TabContainer_Tab1_PanelGroup1_Spacer2"/>
                <OutputLabel width="md-12" value="{$.fh.docs.validation_validation_is_highly_dependent_on_events_that_are_executed}:" id="_Form_TabContainer_Tab1_PanelGroup1_OutputLabel9"/>
                <OutputLabel width="md-12" value="1. {$.fh.docs.validation_if_form_component_like_inputtext_has_default_event} 'onInput'='-' {$.fh.docs.validation_then_validation_is_not_triggered}." id="_Form_TabContainer_Tab1_PanelGroup1_OutputLabel10"/>
                <OutputLabel width="md-12" value="2. {$.fh.docs.validation_if_form_component_like_inputtext_has_default_event} 'onInput'='+' {$.fh.docs.validation_then_validation_is_triggered}." id="_Form_TabContainer_Tab1_PanelGroup1_OutputLabel11"/>
                <OutputLabel width="md-12" value="{$.fh.docs.validation_example_is_in_section_named}: '{$.fh.docs.validation_validation_with_default_events}'" id="_Form_TabContainer_Tab1_PanelGroup1_OutputLabel12"/>
                <Spacer width="md-12" height="20" id="_Form_TabContainer_Tab1_PanelGroup1_Spacer3"/>
                <OutputLabel width="md-12" value="{$.fh.docs.validation_after_validation_process_is_finished} 'UserSession.validationResults'." id="_Form_TabContainer_Tab1_PanelGroup1_OutputLabel13"/>
                <PanelGroup label="{$.fh.docs.validation_default_validation_code}" width="md-12" id="_Form_TabContainer_Tab1_PanelGroup1_PanelGroup1">
                    <InputText id="exampleValidationXmlCode1" label="{$.fh.docs.validation_xml}" width="md-12" rowsCount="1" value=" &lt;InputText id=&quot;test&quot; label=&quot;Dont validate name: &quot; width=&quot;md-3&quot; value=&quot;\{name\}&quot; required=&quot;true&quot;/&gt;"/>
                    <InputText id="exampleValidationJavaCode1" label="{$.fh.docs.validation_java}" width="md-12" rowsCount="3" value="@Action private void saveWithFHValidation() \{    \}"/>
                </PanelGroup>

                <PanelGroup label="{$.fh.docs.validation_validation_after_action_code}" width="md-12" id="_Form_TabContainer_Tab1_PanelGroup1_PanelGroup2">
                    <OutputLabel width="md-12" value="{$.fh.docs.validation_default_lifecycle_of_action_and_validation_is}" id="_Form_TabContainer_Tab1_PanelGroup1_PanelGroup2_OutputLabel1"/>
                    <OutputLabel width="md-12" value="{$.fh.docs.validation_above_example_shows_how_to_invoke_validation_after_action}" id="_Form_TabContainer_Tab1_PanelGroup1_PanelGroup2_OutputLabel2"/>
                   <InputText id="exampleValidationJavaCode4" label="{$.fh.docs.validation_java}" width="md-12" rowsCount="2">
                       <![CDATA[![ESCAPE[@Action(validateBeforeAction = false)
private void saveWithFHValidation() {    }]]]]>
                   </InputText>
                </PanelGroup>

                <PanelGroup label="{$.fh.docs.validation_skip_validation_fh_code}" width="md-12" id="_Form_TabContainer_Tab1_PanelGroup1_PanelGroup3">
                    <InputText id="exampleValidationXmlCode2" label="{$.fh.docs.validation_xml}" width="md-12" rowsCount="1" value=" &lt;InputText id=&quot;test&quot; label=&quot;Dont validate name: &quot; width=&quot;md-3&quot; value=&quot;\{name\}&quot; required=&quot;true&quot;/&gt;"/>
                    <InputText id="exampleValidationJavaCode2" label="{$.fh.docs.validation_java}" width="md-12" rowsCount="2" value="@Action(validate = false) private void saveWithoutFHValidation() \{    \}"/>
                </PanelGroup>
                <PanelGroup label="{$.validation_skip_clearcontext_fh_code}" width="md-12" id="_Form_TabContainer_Tab1_PanelGroup1_PanelGroup4">
                    <InputText id="exampleValidationJavaCode2_3" label="{$.fh.docs.validation_java}" width="md-12" rowsCount="2" value="@Action(clearContext = false) private void validateWithoutFHValidation() \{    \}"/>
                </PanelGroup>
            </PanelGroup>

            <PanelGroup label="{$.fh.docs.validation_watch_out}" width="md-12" id="_Form_TabContainer_Tab1_PanelGroup2">
                <OutputLabel width="md-12" value="{$.fh.docs.validation_developer_has_to_pay_extra_attention_when_binding}." id="_Form_TabContainer_Tab1_PanelGroup2_OutputLabel"/>
            </PanelGroup>

            <PanelGroup label="{$.fh.docs.validation_custom_message}" width="md-12" id="_Form_TabContainer_Tab1_PanelGroup3">
                <OutputLabel width="md-12" value="{$.fh.docs.validation_developer_can_create_custom_message_like_this}" id="_Form_TabContainer_Tab1_PanelGroup3_OutputLabel"/>
                <InputText id="exampleValidationJavaCode3" label="{$.fh.docs.validation_java}" width="md-12" rowsCount="4" value="@Action(validate = false) private void saveWithFHValidation() \{                                  reportValidationError(model, &quot;name&quot;, &quot;This field requires format: PL and 12 numbers &quot;, PresentationStyleEnum.ERROR);                                 \}"/>
            </PanelGroup>
        </Tab>

        <Tab id="exampleTab1" label="{$.fh.docs.validation_example} - {$.fh.docs.validation_attribute_validation}">
            <OutputLabel width="md-12" value="             1. {$.fh.docs.validation_this_example_demonstrates_simple_validation}." id="_Form_TabContainer_Tab2_OutputLabel1"/>

            <PanelGroup id="exampleGroupSimple" label="{$.fh.docs.validation_example} - {$.fh.docs.validation_validation_with_default_events}" width="md-12">
                <ValidateMessages level="error" componentIds="rowEvent1" id="_Form_TabContainer_Tab2_PanelGroup1_ValidateMessages1"/>
                <Group id="rowEvent1" width="md-12">
                    <InputText label="{$.fh.docs.validation_inputtext_with_event_oninput_without_validation}" width="md-3" onInput="-" required="true" value="{eventName}" id="_Form_TabContainer_Tab2_PanelGroup1_Group1_InputText"/>
                </Group>
                <ValidateMessages level="error" componentIds="rowEvent2" id="_Form_TabContainer_Tab2_PanelGroup1_ValidateMessages2"/>
                <Group id="rowEvent2" width="md-12">
                    <InputText label="{$.fh.docs.validation_inputtext_with_event_oninput_with_validation}" width="md-3" onInput="+" required="true" value="{eventSurname}" id="_Form_TabContainer_Tab2_PanelGroup1_Group2_InputText"/>
                </Group>
            </PanelGroup>

            <OutputLabel width="md-12" value="             2. {$.fh.docs.validation_besides_required_attribute_on_components_like_inputtext}." id="_Form_TabContainer_Tab2_OutputLabel2"/>

            <PanelGroup id="exampleGroupValidationRule" label="{$.fh.docs.validation_example} - {$.fh.docs.validation_validation_with_default_events}" width="md-12">
                <ValidateMessages level="error" componentIds="rowEvent3" id="_Form_TabContainer_Tab2_PanelGroup2_ValidateMessages"/>
                <Group id="rowEvent3" width="md-12">
                    <InputText label="{$.fh.docs.validation_inputtext_is_length_of_text_longer_than_1_char}" width="md-3" onInput="+" validationRule="-validationRuleValue != null &amp;&amp; validationRuleValue.length() &gt; 1" value="{validationRuleValue}" id="_Form_TabContainer_Tab2_PanelGroup2_Group_InputText"/>
                </Group>
            </PanelGroup>
        </Tab>

        <Tab id="exampleTab2" label="{$.fh.docs.validation_example} - {$.fh.docs.validation_jsr_303_validation}">
            <PanelGroup id="exampleGroupJsr" label="{$.fh.docs.validation_example} - {$.fh.docs.validation_validation_plus_validationmessages}" width="md-12">
                <OutputLabel width="md-12" value="1. {$.fh.docs.validation_this_example_demonstrates_validation_based_on_jsr_303}." id="_Form_TabContainer_Tab3_PanelGroup_OutputLabel"/>
                <Spacer width="md-12" height="10" id="_Form_TabContainer_Tab3_PanelGroup_Spacer"/>

                <ValidateMessages level="error" componentIds="jsrRowID" id="_Form_TabContainer_Tab3_PanelGroup_ValidateMessages"/>

                <Group id="jsrRowID" width="md-12">
                    <InputText label="{$.fh.docs.validation_name}" width="md-3" value="{name}" id="_Form_TabContainer_Tab3_PanelGroup_Group_InputText1"/>
                    <InputText label="{$.fh.docs.validation_surname}" width="md-3" value="{surname}" id="_Form_TabContainer_Tab3_PanelGroup_Group_InputText2"/>
                </Group>

                <PanelGroup id="_Form_TabContainer_Tab3_PanelGroup_PanelGroup1">
                    <Button width="md-4" label="{$.fh.docs.validation_save_with_fh_validation}" id="pSavePersonJsr" onClick="savePersonJsr"/>
                </PanelGroup>

                <PanelGroup label="{$.fh.docs.validation_code}" id="_Form_TabContainer_Tab3_PanelGroup_PanelGroup2">
                    <InputText id="exampleValidationJavaCodeJsr" label="{$.fh.docs.validation_java}" width="md-12" rowsCount="16" value="@Getter @Setter public class ValidationInfoModel  \{     @NotNull     private String name;      @Size(min = 3, max = 5, message = &quot;Length 3-5 required&quot;)     private String surname;      private String dontValidateName;     private String eventName;     private String eventSurname;     private int activeTabId;   } "/>
                </PanelGroup>

            </PanelGroup>

        </Tab>

        <Tab id="exampleTab3" label="{$.fh.docs.validation_example} - {$.fh.docs.validation_combined_validation}">
            <PanelGroup id="exampleGroupCombined" label="{$.fh.docs.validation_example} - {$.fh.docs.validation_validation_plus_validationmessages}" width="md-12">
                <ValidateMessages level="error" componentIds="rowID2,+" id="_Form_TabContainer_Tab4_PanelGroup_ValidateMessages"/>

                <Group id="rowID1" width="md-12">
                    <InputText id="test" label="{$.fh.docs.validation_don_t_validate_name} " width="md-3" value="{dontValidateName}"/>
                </Group>

                <Group id="rowID2" width="md-12">
                    <InputText label="{$.fh.docs.validation_name}" width="md-3" value="{name}" required="true" id="_Form_TabContainer_Tab4_PanelGroup_Group2_InputText1"/>
                    <InputText label="{$.fh.docs.validation_surname}" width="md-3" value="{surname}" required="true" id="_Form_TabContainer_Tab4_PanelGroup_Group2_InputText2"/>
                    <InputText id="iNonEditableField" label="{$.fh.docs.validation_nonEditableField}" width="md-3" value="{nonEditableField}" required="true"/>
                    <InputText id="iHiddenField" label="{$.fh.docs.validation_hiddenField}" width="md-3" value="{hiddenField}" required="true"/>
                </Group>

                <PanelGroup id="_Form_TabContainer_Tab4_PanelGroup_PanelGroup">
                    <Button width="md-4" label="{$.fh.docs.validation_save_with_fh_validation}" id="pSavePerson1" onClick="savePerson"/>
                    <Button width="md-4" label="{$.fh.docs.validation_save_without_fh_validation}" id="pSavePerson2" onClick="saveCustomPerson"/>
                </PanelGroup>
            </PanelGroup>
        </Tab>

        <Tab id="exampleTab5" label="{$.fh.docs.validation_example} - {$.fh.docs.validation_alternate_label}">
            <PanelGroup id="exampleGroupAlternate" label="{$.fh.docs.validation_example} - {$.fh.docs.validation_alternate_label}" width="md-12">
                <ValidateMessages level="error" componentIds="exampleTab5" id="_Form_TabContainer_Tab5_PanelGroup_ValidateMessages"/>
                <PanelGroup label="{$.fh.docs.validation_alternate_label_first_person_panel}" width="md-6" id="_Form_TabContainer_Tab5_PanelGroup_PanelGroup1">
                    <InputText label="{$.fh.docs.validation_alternate_label_name}" value="{firstPerson.name}" validationLabel="{$.fh.docs.validation_alternate_label_name_of} {$.fh.docs.validation_alternate_label_first_person}" width="md-6" required="true" id="_Form_TabContainer_Tab5_PanelGroup_PanelGroup1_InputText"/>
                    <InputNumber label="{$.fh.docs.validation_alternate_label_age}" value="{firstPerson.age}" validationLabel="{$.fh.docs.validation_alternate_label_age_of} {$.fh.docs.validation_alternate_label_first_person}" width="md-6" required="true" id="_Form_TabContainer_Tab5_PanelGroup_PanelGroup1_InputNumber"/>
                </PanelGroup>
                <PanelGroup label="{$.fh.docs.validation_alternate_label_second_person_panel}" width="md-6" id="_Form_TabContainer_Tab5_PanelGroup_PanelGroup2">
                    <InputText label="{$.fh.docs.validation_alternate_label_name}" value="{secondPerson.name}" validationLabel="{$.fh.docs.validation_alternate_label_name_of} {$.fh.docs.validation_alternate_label_second_person}" width="md-6" required="true" id="_Form_TabContainer_Tab5_PanelGroup_PanelGroup2_InputText"/>
                    <InputNumber label="{$.fh.docs.validation_alternate_label_age}" value="{secondPerson.age}" validationLabel="{$.fh.docs.validation_alternate_label_age_of} {$.fh.docs.validation_alternate_label_second_person}" width="md-6" required="true" id="_Form_TabContainer_Tab5_PanelGroup_PanelGroup2_InputNumber"/>
                </PanelGroup>
                <Group id="_Form_TabContainer_Tab5_PanelGroup_Group">
                    <Table collection="{personList}" iterator="person" width="md-6" id="_Form_TabContainer_Tab5_PanelGroup_Group_Table">
                        <Column label="{$.fh.docs.validation_alternate_label_name}" value="{person.name}" id="_Form_TabContainer_Tab5_PanelGroup_Group_Table_Column1"/>
                        <Column label="{$.fh.docs.validation_alternate_label_age}" id="_Form_TabContainer_Tab5_PanelGroup_Group_Table_Column2">
                            <InputNumber value="{person.age}" validationLabel="{$.fh.docs.validation_alternate_label_age_of} {person.name} ({$.fh.docs.validation_alternate_label_row} {person$rowNo})" required="true" id="_Form_TabContainer_Tab5_PanelGroup_Group_Table_Column2_InputNumber"/>
                        </Column>
                    </Table>
                </Group>
                <Button label="{$.fh.docs.validation_alternate_label_validate}" onClick="+" id="_Form_TabContainer_Tab5_PanelGroup_Button"/>
                <PanelGroup label="{$.fh.docs.validation_default_validation_code}" width="md-12" id="_Form_TabContainer_Tab5_PanelGroup_PanelGroup3">
                    <InputText id="exampleValidationXmlCode5" label="{$.fh.docs.validation_xml}" width="md-12" rowsCount="14">
<![CDATA[
<PanelGroup label="{$.fh.docs.validation_alternate_label_first_person_panel}" width="md-6">
    <InputText label="{$.fh.docs.validation_alternate_label_name}" value="\{firstPerson.name\}" validationLabel="{$.fh.docs.validation_alternate_label_name_of} {$.fh.docs.validation_alternate_label_first_person}" width="md-6" required="true"/>
    <InputNumber label="{$.fh.docs.validation_alternate_label_age}" value="\{firstPerson.age\}" validationLabel="{$.fh.docs.validation_alternate_label_age_of} {$.fh.docs.validation_alternate_label_first_person}" width="md-6" required="true"/>
</PanelGroup>
<PanelGroup label="{$.fh.docs.validation_alternate_label_second_person_panel}" width="md-6">
    <InputText label="{$.fh.docs.validation_alternate_label_name}" value="\{secondPerson.name\}" validationLabel="{$.fh.docs.validation_alternate_label_name_of} {$.fh.docs.validation_alternate_label_second_person}" width="md-6" required="true"/>
    <InputNumber label="{$.fh.docs.validation_alternate_label_age}" value="\{secondPerson.age\}" validationLabel="{$.fh.docs.validation_alternate_label_age_of} {$.fh.docs.validation_alternate_label_second_person}" width="md-6" required="true"/>
</PanelGroup>
<Group>
    <Table collection="\{personList\}" iterator="person" width="md-6">
        <Column label="{$.fh.docs.validation_alternate_label_name}" value="\{person.name\}"/>
        <Column label="{$.fh.docs.validation_alternate_label_age}">
            <InputNumber value="\{person.age\}" validationLabel="{$.fh.docs.validation_alternate_label_age_of} \{person.name\} ({$.fh.docs.validation_alternate_label_row} \{person$rowNo\})" required="true"/>
        </Column>
    </Table>
</Group>
]]>
                    </InputText>
                </PanelGroup>
            </PanelGroup>
        </Tab>
    </TabContainer>
</Form>