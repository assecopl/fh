<?xml version="1.0" encoding="UTF-8"?>
<Form xmlns="http://fh.asseco.com/form/1.0" container="mainForm" id="documentationHolder" label="{componentName}">
    <AvailabilityConfiguration>
        <ReadOnly>tableExampleCode,tableExampleCode2,tableExampleCode3</ReadOnly>
    </AvailabilityConfiguration>
    <OutputLabel width="md-12" value="{description}" id="_Form_OutputLabel"/>
    <Spacer width="md-12" height="25px" id="_Form_Spacer"/>
    <TabContainer id="_Form_TabContainer">
        <Tab label="{$.fh.docs.component.examples}" id="_Form_TabContainer_Tab1">
            <TabContainer id="_Form_TabContainer_Tab1_TabContainer">
                <Tab label="{$.fh.docs.component.validatemessages_navigation_for_fields}" id="_Form_TabContainer_Tab1_TabContainer_Tab1">

                        <PanelGroup width="md-12" label="{$.fh.docs.component.validatemessages_validatemessages_with_required_fields_in_multiple_groups}" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup1">
                            <ValidateMessages id="validationBoxId1" level="error" componentIds="ValidateMessages_group2,ValidateMessages_group3"/>

                            <PanelGroup id="ValidateMessages_group2" label="{$.fh.docs.component.validatemessages_country}">
                                <SelectOneMenu value="{selectOneMenuValueToValidation}" values="|Poland|German|UK|US" width="md-12" required="true" onChange="onChangeExample" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup1_PanelGroup1_SelectOneMenu"/>
                            </PanelGroup>

                            <PanelGroup id="ValidateMessages_group3" label="{$.fh.docs.component.validatemessages_your_bithday_date}">
                                <InputDate width="md-4" required="true" onChange="+" value="{inputDateRequiredToValidation}" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup1_PanelGroup2_InputDate"/>
                            </PanelGroup>

                            <InputText id="tableExampleCode" label="{$.fh.docs.component.code}" width="md-12" height="230" rowsCount="7">
<![CDATA[![ESCAPE[<ValidateMessages id="validationBoxId1" level="error" componentIds="ValidateMessages_group2,ValidateMessages_group3"/>
<PanelGroup id="ValidateMessages_group2">
    <SelectOneMenu value="{selectOneMenuValueToValidation}" values="|Poland|German|UK|US" width="md-12" required="true" label="Country" onChange="onChangeExample"/>
</PanelGroup>
<PanelGroup id="ValidateMessages_group3">
    <InputDate label="Your bithday date" width="md-4" required="true" onChange="+" value="{inputDateRequiredToValidation}"/>
</PanelGroup>]]]]>
                            </InputText>
                        </PanelGroup>


                        <PanelGroup width="md-12" label="{$.fh.docs.component.validatemessages_validatemessages_with_multi_size}" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup2">
                            <ValidateMessages id="validationBoxId3" level="error" componentIds="ValidateMessages_group7" width="xs-12,sm-10,md-6,lg-2"/>

                            <PanelGroup id="ValidateMessages_group7" label="{$.fh.docs.component.validatemessages_citizenship}">
                                <SelectOneMenu value="{selectOneMenuCitizenshipToValidation}" values="|Polish|German|UK|US" width="md-12" required="true" onChange="onChangeExample" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup2_PanelGroup_SelectOneMenu"/>
                            </PanelGroup>
                            <InputText id="tableExampleCode3" label="{$.fh.docs.component.code}" width="md-12" rowsCount="4">
<![CDATA[![ESCAPE[<ValidateMessages id="validationBoxId3" level="error" componentIds="ValidateMessages_group7" width="xs-12,sm-10,md-6,lg-2"/>
<PanelGroup id="ValidateMessages_group7">
   <SelectOneMenu value="{selectOneMenuCitizenshipToValidation}" values="|Polish|German|UK|US" width="md-12" required="true" label="Citizenship" onChange="onChangeExample"/>
</PanelGroup>]]]]>
                            </InputText>
                        </PanelGroup>
                </Tab>
                <Tab label="{$.fh.docs.component.validatemessages_missing_navigation_for_fields}" id="_Form_TabContainer_Tab1_TabContainer_Tab2">
                        <PanelGroup width="md-12" label="{$.fh.docs.component.validatemessages_validatemessages_without_navigation_for_fields}" id="_Form_TabContainer_Tab1_TabContainer_Tab2_PanelGroup">
                            <PanelGroup id="ValidateMessages_group5" label="{$.fh.docs.component.validatemessages_this_panelgroup_is_collapsible_with_ontoggle_event}" onToggle="onChangeExample" collapsed="{state}">
                                <SelectOneMenu value="{selectOneMenuValueToValidationNonNavigable}" values="|Gates|Page|Clinton" width="md-12" required="true" label="{$.fh.docs.component.validatemessages_surname}" onChange="onChangeExample" id="_Form_TabContainer_Tab1_TabContainer_Tab2_PanelGroup_PanelGroup_SelectOneMenu1"/>

                                <SelectOneMenu value="{selectOneMenuPersonToValidationNonNavigable}" values="|Adam|Bill|Jane|Lary|Timothy" width="md-12" required="true" label="{$.fh.docs.component.validatemessages_name}" onChange="onChangeExample" id="_Form_TabContainer_Tab1_TabContainer_Tab2_PanelGroup_PanelGroup_SelectOneMenu2"/>

                                <InputText width="md-12" id="inputTextExampleId" required="true" value="{requiredValue}" onChange="onChangeExample" label="{$.fh.docs.component.validatemessages_city}"/>
                            </PanelGroup>

                            <ValidateMessages id="validationBoxId2" level="error" componentIds="ValidateMessages_group5" navigation="false"/>

                            <InputText id="tableExampleCode2" label="{$.fh.docs.component.code}" width="md-12" height="200" rowsCount="6">
<![CDATA[![ESCAPE[<PanelGroup id="ValidateMessages_group5" label="This panelGroup is collapsible (with onToggle event)" onToggle="onChangeExample" collapsed="{state}">
   <SelectOneMenu value="{selectOneMenuValueToValidationNonNavigable}" values="|Gates|Page|Clinton" width="md-12" required="true" label="Surname" onChange="onChangeExample"/>
   <SelectOneMenu value="{selectOneMenuPersonToValidationNonNavigable}" values="|Adam|Bill|Jane|Lary|Timothy" width="md-12" required="true" label="Name" onChange="onChangeExample"/>
   <InputText id="inputTextExampleId"  required="true" value="{requiredValue}" onChange="onChangeExample" label="City"/>
</PanelGroup>
<ValidateMessages id="validationBoxId2" level="error" componentIds="ValidateMessages_group5" navigation="false"/>]]]]>
                            </InputText>
                        </PanelGroup>
                </Tab>
            </TabContainer>
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