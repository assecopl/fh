<?xml version="1.0" encoding="UTF-8"?>
<Form xmlns="http://fh.asseco.com/form/1.0" container="mainForm" id="documentationHolder" label="{componentName}">
    <AvailabilityConfiguration>
        <ReadOnly>
            selectOneMenuExampleCode1,selectOneMenuExampleCode2,selectOneMenuExampleCode3,selectOneMenuExampleCode4,selectOneMenuExampleCode5,selectOneMenuExampleCode6,selectOneMenuOnChange2,selectOneMenuExampleCode7,selectOneMenuExampleCode8,selectOneMenuExampleCode9,selectOneMenuExampleCode10,
            selectOneMenuExampleCode11,selectOneMenuExampleCode12, disabledSom,
            selectOneMenuExampleCode13,selectOneMenuExampleCode14,selectOneMenuExampleCode15
        </ReadOnly>
    </AvailabilityConfiguration>
    <OutputLabel width="md-12" value="{description}" id="_Form_OutputLabel"/>
    <Spacer width="md-12" height="25px" id="_Form_Spacer"/>
    <TabContainer id="_Form_TabContainer">
        <Tab label="{$.fh.docs.component.examples}" id="_Form_TabContainer_Tab1">
                <PanelGroup width="md-12" label="{$.fh.docs.component.selectonemenu_selectonemenu_with_label}" id="_Form_TabContainer_Tab1_PanelGroup1">
                    <SelectOneMenu id="somCountries1" label="{$.fh.docs.component.selectonemenu_countries}" values="Poland|German|UK|US" width="md-12"/>
                    <InputText id="selectOneMenuExampleCode3" label="{$.fh.docs.component.code}" width="md-12" rowsCount="1">
                        <![CDATA[![ESCAPE[<SelectOneMenu label="Countries" values="Poland|German|UK|US" width="md-12"/>]]]]>
                    </InputText>
                </PanelGroup>

                <PanelGroup width="md-12" label="{$.fh.docs.component.selectonemenu_selectonemenu_with_specified_size_and_height}" id="_Form_TabContainer_Tab1_PanelGroup2">
                    <SelectOneMenu id="somCountries2" values="Poland|German|UK|US" width="md-2" height="120"/>

                    <InputText id="selectOneMenuExampleCode1" label="{$.fh.docs.component.code}" width="md-12" rowsCount="1">
                        <![CDATA[![ESCAPE[<SelectOneMenu values="Poland|German|UK|US" width="md-2" height="120"/>]]]]>
                    </InputText>
                </PanelGroup>

                <PanelGroup width="md-12" label="{$.fh.docs.component.selectonemenu_selectonemenu_with_hint}" id="_Form_TabContainer_Tab1_PanelGroup3">
                    <SelectOneMenu values="Poland|German|UK|US" width="md-3" hint="{$.fh.docs.component.selectonemenu_this_is_hint}" id="_Form_TabContainer_Tab1_PanelGroup3_SelectOneMenu"/>

                    <InputText id="selectOneMenuExampleCode12" label="{$.fh.docs.component.code}" width="md-12" rowsCount="1">
                        <![CDATA[![ESCAPE[<SelectOneMenu values="Poland|German|UK|US" width="md-3" hint="This is hint"/>]]]]>
                    </InputText>
                </PanelGroup>

                <PanelGroup width="md-12" label="{$.fh.docs.component.selectonemenu_selectonemenu_with_bootstrap_layout}" id="_Form_TabContainer_Tab1_PanelGroup4">
                    <SelectOneMenu label="{$.fh.docs.component.selectonemenu_countries}" values="Poland|German|UK|US" width="md-9" id="_Form_TabContainer_Tab1_PanelGroup4_SelectOneMenu1"/>
                    <SelectOneMenu label="{$.fh.docs.component.selectonemenu_gender}" values="Male|Female" width="md-3" id="_Form_TabContainer_Tab1_PanelGroup4_SelectOneMenu2"/>
                    <InputText id="selectOneMenuExampleCode2" label="{$.fh.docs.component.code}" width="md-12" rowsCount="2">
                        <![CDATA[![ESCAPE[<SelectOneMenu label="Countries" values="Poland|German|UK|US" width="md-9"/>
<SelectOneMenu label="Gender" values="Male|Female" width="md-3"/>]]]]>
                    </InputText>
                </PanelGroup>

                <PanelGroup id="selectOneMenuRequiredValue" width="md-12" label="{$.fh.docs.component.selectonemenu_selectonemenu_with_required_value}">
                    <ValidateMessages id="validationBoxId1" level="error" componentIds="selectOneMenuRequiredValue"/>

                    <SelectOneMenu value="{selectOneMenuValue}" label="{$.fh.docs.component.selectonemenu_countries}" values="|Poland|German|UK|US" width="md-12" required="true" onChange="onChangeExample" id="_Form_TabContainer_Tab1_PanelGroup5_SelectOneMenu"/>

                    <InputText id="selectOneMenuExampleCode4" label="{$.fh.docs.component.code}" width="md-12" rowsCount="2">
                        <![CDATA[![ESCAPE[<ValidateMessages id="validationBoxId1" level="error" componentIds="selectOneMenuRequiredValue"/>
<SelectOneMenu value="{selectOneMenuValue}" label="Countries" values="|Poland|German|UK|US" width="md-12" required="true" onChange="onChangeExample"/>]]]]>
                    </InputText>
                </PanelGroup>

                <PanelGroup width="md-12" label="{$.fh.docs.component.selectonemenu_selectonemenu_with_onchange_event}" id="_Form_TabContainer_Tab1_PanelGroup6">
                    <SelectOneMenu label="{$.fh.docs.component.selectonemenu_below_selectonemenu_is_bound_to_label}" value="{selectOneMenuOnChangeValue}" values="Poland|German|Russia|UK|US" width="md-12" onChange="onChangeExample" id="_Form_TabContainer_Tab1_PanelGroup6_SelectOneMenu"/>

                    <OutputLabel id="selectOneMenuOnChangeBinding1" width="md-12" value="{selectOneMenuOnChangeValue}"/>
                    <InputText id="selectOneMenuExampleCode5" label="{$.fh.docs.component.code}" width="md-12" rowsCount="2">
                        <![CDATA[![ESCAPE[ <SelectOneMenu label="Below SelectOneMenu is bound to label" value="{selectOneMenuOnChangeValue}" values="Poland|German|Russia|UK|US" width="md-12" onChange="onChangeExample"/>
<OutputLabel id="selectOneMenuOnChangeBinding1" width="md-12" value="{selectOneMenuOnChangeValue}" />]]]]>
                    </InputText>
                </PanelGroup>

                <PanelGroup width="md-12" label="{$.fh.docs.component.selectonemenu_selectonemenu_with_onchange_event_bound_to_another_selectonemenu}" id="_Form_TabContainer_Tab1_PanelGroup7">
                    <SelectOneMenu id="selectOneMenuOnChange1" label="{$.fh.docs.component.selectonemenu_below_selectonemenu_is_bound_to_another_selectonemenu}" value="{selectOneMenuToSelectOneMenuValue}" values="Poland|German|Russia|UK|US" width="md-12" onChange="onChangeExample"/>
                    <SelectOneMenu id="selectOneMenuOnChange2" value="{selectOneMenuToSelectOneMenuValue}" values="Poland|German|Russia|UK|US" width="md-12" onChange="onChangeExample"/>

                    <InputText id="selectOneMenuExampleCode6" label="{$.fh.docs.component.code}" width="md-12" rowsCount="2">
                        <![CDATA[![ESCAPE[ <SelectOneMenu id="selectOneMenuOnChange1" label="Below SelectOneMenu is bound to another SelectOneMenu" value="{selectOneMenuToSelectOneMenuValue}" values="Poland|German|Russia|UK|US" width="md-12" onChange="onChangeExample"/>
                        <SelectOneMenu id="selectOneMenuOnChange2" value="{selectOneMenuToSelectOneMenuValue}" values="Poland|German|Russia|UK|US" width="md-12" onChange="onChangeExample"/>]]]]>
                    </InputText>
                </PanelGroup>

                <PanelGroup width="md-12" label="{$.fh.docs.component.selectonemenu_selectonemenu_with_conversion_with_binding_to_collection_of_any_type}" id="_Form_TabContainer_Tab1_PanelGroup8">
                    <SelectOneMenu id="hereiam" label="{$.fh.docs.component.selectonemenu_below_selectonemenu_is_bound_to_label}" value="{selectedUserBidingExample}" values="{boundUsers}" width="md-12" onChange="-"/>
                    <OutputLabel width="md-12" value="{$.fh.docs.component.selectonemenu_selected_user_is}: {selectedUserBidingExample.firstName}, {selectedUserBidingExample.lastName}." id="_Form_TabContainer_Tab1_PanelGroup8_OutputLabel1"/>
                    <OutputLabel width="md-12" value="{$.fh.docs.component.selectonemenu_selected_user_is_1} {selectedUserBidingExample.age} {$.fh.docs.component.selectonemenu_years_old}." id="_Form_TabContainer_Tab1_PanelGroup8_OutputLabel2"/>
                    <OutputLabel width="md-12" value="{$.fh.docs.component.selectonemenu_selected_user_was_created_on} : {selectedUserBidingExample.creationDate}." id="_Form_TabContainer_Tab1_PanelGroup8_OutputLabel3"/>

                    <Button onClick="addSampleUser" label="{$.fh.docs.component.selectonemenu_add_user}" id="_Form_TabContainer_Tab1_PanelGroup8_Button"/>
                    <InputText id="selectOneMenuExampleCode9" label="{$.fh.docs.component.code}" width="md-12" rowsCount="5">
                        <![CDATA[![ESCAPE[<SelectOneMenu id="hereiam" label="Below SelectOneMenu is bound to label" value="{selectedUserBidingExample}" values="{boundUsers}" width="md-12" onChange="-"/>
<OutputLabel width="md-12" value="Selected user is: {selectedUserBidingExample.firstName}, {selectedUserBidingExample.lastName}." />
<OutputLabel width="md-12" value="Selected user is {selectedUserBidingExample.age} years old." />
<OutputLabel width="md-12" value="Selected user was created on : {selectedUserBidingExample.creationDate}." />
<Button onClick="addSampleUser" label="Add user" />]]]]>
                    </InputText>
                </PanelGroup>

                <PanelGroup width="md-12" label="{$.fh.docs.component.selectonemenu_selectonemenu_with_conversion_to_enumeration}" id="_Form_TabContainer_Tab1_PanelGroup9">
                    <SelectOneMenu label="{$.fh.docs.component.selectonemenu_below_selectonemenu_is_bound_to_label}" value="{selectedSize}" values="{allSizes}" width="md-12" onChange="onChangeExample" id="_Form_TabContainer_Tab1_PanelGroup9_SelectOneMenu"/>
                    <OutputLabel id="selectOneMenuOnChangeBinding2" width="md-12" value="{selectedSize}"/>

                    <InputText id="selectOneMenuExampleCode7" label="{$.fh.docs.component.code}" width="md-12" rowsCount="2">
                        <![CDATA[![ESCAPE[<SelectOneMenu label="Below SelectOneMenu is bound to label" value="{selectedSize}" values="{allSizes}" width="md-12" onChange="onChangeExample"/>
<OutputLabel id="selectOneMenuOnChangeBinding2" width="md-12" value="{selectedSize}" />]]]]>
                    </InputText>
                </PanelGroup>

                <PanelGroup width="md-12" label="{$.fh.docs.component.selectonemenu_selectonemenu_with_conversion_with_binding_to_collection_of_any_type}" id="_Form_TabContainer_Tab1_PanelGroup10">
                    <SelectOneMenu label="{$.fh.docs.component.selectonemenu_below_selectonemenu_is_bound_to_label}" value="{selectedPersonConvExample}" values="{people}" width="md-12" onChange="onChangeExample" id="_Form_TabContainer_Tab1_PanelGroup10_SelectOneMenu"/>
                    <OutputLabel id="selectOneMenuOnChangeBinding3" width="md-12" value="{$.fh.docs.component.selectonemenu_my_name_is} {selectedPersonConvExample.name} {selectedPersonConvExample.surname}. {$.fh.docs.component.selectonemenu_i_live_in} {selectedPersonConvExample.city}."/>

                    <InputText id="selectOneMenuExampleCode8" label="{$.fh.docs.component.code}" width="md-12" rowsCount="2">
                        <![CDATA[![ESCAPE[<SelectOneMenu label="Below SelectOneMenu is bound to label" value="{selectedPersonConvExample}" values="{people}" width="md-12" onChange="onChangeExample"/>
<OutputLabel id="selectOneMenuOnChangeBinding3" width="md-12" value="My name is {selectedPersonConvExample.name} {selectedPersonConvExample.surname}. I live in {selectedPersonConvExample.city}." />
]]]]>
                    </InputText>
                </PanelGroup>

                <PanelGroup width="md-12" label="{$.fh.docs.component.selectonemenu_selectonemenu_with_multi_size}" id="_Form_TabContainer_Tab1_PanelGroup11">
                    <SelectOneMenu label="{$.fh.docs.component.selectonemenu_countries}" values="Poland|German|UK|US" width="xs-12,sm-10,md-8,lg-4" id="_Form_TabContainer_Tab1_PanelGroup11_SelectOneMenu"/>
                    <InputText id="selectOneMenuExampleCode10" label="{$.fh.docs.component.code}" width="md-12" rowsCount="1">
                        <![CDATA[![ESCAPE[<SelectOneMenu label="Countries" values="Poland|German|UK|US" width="xs-12,sm-10,md-8,lg-4"/>]]]]>
                    </InputText>
                </PanelGroup>

                <PanelGroup width="md-12" label="{$.fh.docs.component.selectonemenu_selectonemenu_with_optional_empty_value}" id="_Form_TabContainer_Tab1_PanelGroup12">
                    <SelectOneMenu width="md-3" label="SelectOneMenu {$.fh.docs.component.selectonemenu_with} emptyValue=&quot;true&quot;" value="{emptyValueSelectedPersonExample}" values="{peopleWithEmptyValue}" onChange="-" emptyValue="true" id="_Form_TabContainer_Tab1_PanelGroup12_SelectOneMenu1"/>
                    <SelectOneMenu width="md-3" label="SelectOneMenu {$.fh.docs.component.selectonemenu_with} emptyValue=&quot;true&quot; {$.fh.docs.component.selectonemenu_and} emptyLabel=&quot;true&quot;" value="{emptyValueSelectedPersonExample}" values="{peopleWithEmptyValue}" onChange="-" emptyValue="true" emptyLabel="true" id="_Form_TabContainer_Tab1_PanelGroup12_SelectOneMenu2"/>
                    <SelectOneMenu width="md-3" label="SelectOneMenu {$.fh.docs.component.selectonemenu_with} emptyLabel=&quot;true&quot;" value="{emptyValueSelectedPersonExample}" values="{peopleWithEmptyValue}" onChange="-" emptyLabel="true" id="_Form_TabContainer_Tab1_PanelGroup12_SelectOneMenu3"/>
                    <SelectOneMenu width="md-3" label="SelectOneMenu {$.fh.docs.component.selectonemenu_with_emptyvalue_and_emptylabel_attributes_not_set}" value="{emptyValueSelectedPersonExample}" values="{peopleWithEmptyValue}" onChange="-" id="_Form_TabContainer_Tab1_PanelGroup12_SelectOneMenu4"/>
                    <OutputLabel id="selectOneMenuOnChangeBinding11" width="md-12" value="{$.fh.docs.component.selectonemenu_selected_person}: {emptyValueSelectedPersonExample.name} {emptyValueSelectedPersonExample.surname}. {$.fh.docs.component.selectonemenu_this_person_lives_in} {emptyValueSelectedPersonExample.city}."/>
                    <InputText id="selectOneMenuExampleCode11" label="{$.fh.docs.component.code}" width="md-12" rowsCount="5">
                        <![CDATA[![ESCAPE[<SelectOneMenu label="SelectOneMenu with emptyValue=&quot;true&quot;" value="{emptyValueSelectedPersonExample}" values="{peopleWithEmptyValue}" onChange="-" emptyValue="true"/>
<SelectOneMenu label="SelectOneMenu with emptyValue=&quot;true&quot; and emptyLabel=&quot;true&quot;" value="{emptyValueSelectedPersonExample}" values="{peopleWithEmptyValue}" onChange="-" emptyValue="true" emptyLabel="true"/>
<SelectOneMenu label="SelectOneMenu with  emptyLabel=&quot;true&quot;" value="{emptyValueSelectedPersonExample}" values="{peopleWithEmptyValue}" onChange="-" emptyLabel="true" />
<SelectOneMenu label="SelectOneMenu with emptyValue and emptyLabel attributes not set" value="{emptyValueSelectedPersonExample}" values="{peopleWithEmptyValue}" onChange="-"/>
<OutputLabel id="selectOneMenuOnChangeBinding11" width="md-12" value="Selected person: {emptyValueSelectedPersonExample.name} {emptyValueSelectedPersonExample.surname}. This person lives in {emptyValueSelectedPersonExample.city}." />]]]]>
                    </InputText>
                </PanelGroup>

                <PanelGroup width="md-12" label="{$.fh.docs.component.selectonemenu_selectonemenu_with_label}" id="_Form_TabContainer_Tab1_PanelGroup13">
                    <SelectOneMenu id="disabledSom" label="{$.fh.docs.component.selectonemenu_countries}" values="Poland|German|UK|US" width="md-12"/>
                </PanelGroup>

                <PanelGroup width="md-12" label="{$.fh.docs.component.selectonemenu_selectonemenu_that_keeps_selected_value_removed_from_values_collection}" id="_Form_TabContainer_Tab1_PanelGroup14">
                    <SelectOneMenu label="{$.fh.docs.component.selectonemenu_do_not_keep_removed_value}" value="{selectedSizeToRemove}" values="{sizes}" width="md-12" onChange="-" id="_Form_TabContainer_Tab1_PanelGroup14_SelectOneMenu1"/>
                    <SelectOneMenu label="{$.fh.docs.component.selectonemenu_keep_removed_value}" value="{selectedSizeToRemove}" values="{sizes}" width="md-12" onChange="-" keepRemovedValue="true" id="_Form_TabContainer_Tab1_PanelGroup14_SelectOneMenu2"/>
                    <Button label="{$.fh.docs.component.selectonemenu_remove_value}" onClick="removeValue(selectedSizeToRemove)" id="_Form_TabContainer_Tab1_PanelGroup14_Button"/>


                    <InputText id="selectOneMenuExampleCode13" label="{$.fh.docs.component.code}" width="md-12" rowsCount="3">
                        <![CDATA[![ESCAPE[<SelectOneMenu label="Do not keep removed value" value="{selectedSizeToRemove}" values="{sizes}" width="md-12" onChange="-"/>
<SelectOneMenu label="Keep removed value" value="{selectedSizeToRemove}" values="{sizes}" width="md-12" onChange="-" keepRemovedValue="true" />
<Button label="Remove value" onClick="removeValue(selectedSizeToRemove)"/>]]]]>
                    </InputText>


                <PanelGroup width="md-12" label="{$.fh.docs.component.selectonemenu_attribute} &quot;value&quot; {$.fh.docs.component.selectonemenu_bounded_with_string_object}" id="_Form_TabContainer_Tab1_PanelGroup14_PanelGroup1">

                    <SelectOneMenu label="{$.fh.docs.component.selectonemenu_do_not_keep_removed_value}" value="{keepRemovedValueString}" values="{keepRemovedValueStringList}" width="md-12" onChange="-" id="_Form_TabContainer_Tab1_PanelGroup14_PanelGroup1_SelectOneMenu1"/>
                    <SelectOneMenu label="{$.fh.docs.component.selectonemenu_keep_removed_value}" value="{keepRemovedValueString}" values="{keepRemovedValueStringList}" width="md-12" onChange="-" keepRemovedValue="true" id="_Form_TabContainer_Tab1_PanelGroup14_PanelGroup1_SelectOneMenu2"/>
                    <InputText width="md-12" label="{$.fh.docs.component.selectonemenu_selected_string}" value="{keepRemovedValueString}" onChange="-" id="_Form_TabContainer_Tab1_PanelGroup14_PanelGroup1_InputText1"/>

                    <InputText id="selectOneMenuExampleCode14" label="{$.fh.docs.component.code}" width="md-12" rowsCount="3">
                        <![CDATA[![ESCAPE[<SelectOneMenu label="Do not keep removed value" value="{keepRemovedValueString}" values="{keepRemovedValueStringList}" width="md-12" onChange="-"/>
<SelectOneMenu label="Keep removed value" value="{keepRemovedValueString}" values="{keepRemovedValueStringList}" width="md-12" onChange="-" keepRemovedValue="true" />
<InputText label="Selected string" value="{keepRemovedValueString}" onChange="-"/>]]]]>
                    </InputText>

                </PanelGroup>

                <PanelGroup width="md-12" label="{$.fh.docs.component.selectonemenu_attribute_value_bounded_with_object}" id="_Form_TabContainer_Tab1_PanelGroup14_PanelGroup2">
                    <SelectOneMenu label="{$.fh.docs.component.selectonemenu_do_not_keep_removed_value}" value="{keepRemovedValuePerson}" values="{keepRemovedValuePeopleList}" width="md-12" onChange="-" id="_Form_TabContainer_Tab1_PanelGroup14_PanelGroup2_SelectOneMenu1"/>
                    <SelectOneMenu label="{$.fh.docs.component.selectonemenu_keep_removed_value}" value="{keepRemovedValuePerson}" values="{keepRemovedValuePeopleList}" width="md-12" onChange="-" keepRemovedValue="true" id="_Form_TabContainer_Tab1_PanelGroup14_PanelGroup2_SelectOneMenu2"/>

                    <InputText id="selectOneMenuExampleCode15" label="{$.fh.docs.component.code}" width="md-12" rowsCount="2">
                        <![CDATA[![ESCAPE[<SelectOneMenu label="Do not keep removed value" value="{keepRemovedValuePerson}" values="{keepRemovedValuePeopleList}" width="md-12" onChange="-"/>
<SelectOneMenu label="Keep removed value" value="{keepRemovedValuePerson}" values="{keepRemovedValuePeopleList}" width="md-12" onChange="-" keepRemovedValue="true" />]]]]>
                    </InputText>
                </PanelGroup>
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