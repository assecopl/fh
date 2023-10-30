<?xml version="1.0" encoding="UTF-8"?>
<Form xmlns="http://fh.asseco.com/form/1.0" container="mainForm" id="documentationHolder" label="{$.fh.docs.converter_type_conversion}">
    <AvailabilityConfiguration>
        <ReadOnly>exampleCode,exampleCode2,exampleModelFormatterCode,exampleFormatterCode,exampleFormatterCode2,exampleCode3,exampleModelFormatterCode2</ReadOnly>
        <SetByProgrammer>boundUserLabel1,boundUserLabel2,boundUserLabel3</SetByProgrammer>
    </AvailabilityConfiguration>
    <PanelGroup width="md-12" label="{$.fh.docs.converter_selectonemenu_bound_to_collection}" id="_Form_PanelGroup1">
        <OutputLabel width="md-12" value="{$.fh.docs.converter_conversion_is_mechanism_which_will}" id="_Form_PanelGroup1_OutputLabel"/>
        <PanelGroup width="md-12" label="{$.fh.docs.converter_to_string_conversion}" id="_Form_PanelGroup1_PanelGroup1">
            <OutputLabel width="md-12" value="{$.fh.docs.converter_in_this_example_first_selectonemenu}" id="_Form_PanelGroup1_PanelGroup1_OutputLabel1"/>
            <SelectOneMenu label="{$.fh.docs.converter_please_select_user_by_last_name}" value="{selectedUser}" values="{users}" width="md-12" onChange="-" id="_Form_PanelGroup1_PanelGroup1_SelectOneMenu1"/>
            <SelectOneMenu label="{$.fh.docs.converter_please_select_user_by_first_name}" value="{selectedUser}" formatter="userSecondFormatter" values="{users}" width="md-12" onChange="-" id="_Form_PanelGroup1_PanelGroup1_SelectOneMenu2"/>
            <OutputLabel width="md-12" value="{$.fh.docs.converter_selected_user_is}: {selectedUser.lastName}, {selectedUser.firstName}." id="_Form_PanelGroup1_PanelGroup1_OutputLabel2"/>
            <OutputLabel width="md-12" value="{$.fh.docs.converter_selected_user_is_2} {selectedUser.age} {$.fh.docs.converter_years_old}." id="_Form_PanelGroup1_PanelGroup1_OutputLabel3"/>
            <OutputLabel width="md-12" value="{$.fh.docs.converter_selected_user_was_created_on}: {selectedUser.creationDate}." id="_Form_PanelGroup1_PanelGroup1_OutputLabel4"/>
            <InputText id="exampleCode" label="{$.fh.docs.converter_code}" width="md-12" rowsCount="5" value="&lt;SelectOneMenu label=&quot;Please select user (by last name)&quot; value=&quot;\{selectedUser\}&quot; values=&quot;\{users\}&quot; width=&quot;md-12&quot; onChange=&quot;-&quot;/&gt;
        </PanelGroup>
        <PanelGroup width="md-12" label="{$.fh.docs.converter_to_object_conversion}" id="_Form_PanelGroup1_PanelGroup2">
            <OutputLabel width="md-12" value="{$.fh.docs.converter_in_this_example_inputtext_is_bound}" id="_Form_PanelGroup1_PanelGroup2_OutputLabel1"/>
            <InputText width="md-12" label="{$.fh.docs.converter_please_type_last_name_one_of_users}:" value="{typedUser}" onChange="-" id="_Form_PanelGroup1_PanelGroup2_InputText1"/>
            <OutputLabel id="boundUserLabel1" width="md-12" value="1 {$.fh.docs.converter_typed_user_is}: {typedUser.firstName}, {typedUser.lastName}."/>
            <OutputLabel id="boundUserLabel2" width="md-12" value="2 {$.fh.docs.converter_typed_user_is_2} {typedUser.age} {$.fh.docs.converter_years_old}."/>
            <OutputLabel id="boundUserLabel3" width="md-12" value="3 {$.fh.docs.converter_typed_user_was_created_on}: {typedUser.creationDate}."/>
            <InputText id="exampleCode2" label="{$.fh.docs.converter_code}" width="md-12" rowsCount="4" value="&lt;InputText label=&quot;Please type last name one of users:&quot; value=&quot;\{typedUser\}&quot; onChange=&quot;-&quot; /&gt;
        </PanelGroup>
        <PanelGroup width="md-12" label="{$.fh.docs.converter_model}" id="_Form_PanelGroup1_PanelGroup3">
            <InputText id="exampleModelFormatterCode" label="{$.fh.docs.converter_code}" width="md-12" rowsCount="11" value="@Getter
        </PanelGroup>
        <PanelGroup width="md-12" label="{$.fh.docs.converter_formatter}" id="_Form_PanelGroup1_PanelGroup4">
            <OutputLabel width="md-12" value="{$.fh.docs.converter_in_fh_framework_we_have_two_ways}" id="_Form_PanelGroup1_PanelGroup4_OutputLabel1"/>
            <InputText id="exampleFormatterCode" label="{$.fh.docs.converter_default_formatter_code}" width="md-12" rowsCount="16" value="@FhFormatter
            <OutputLabel width="md-12" value="{$.fh.docs.converter_if_we_want_to_have_other_than_default}" id="_Form_PanelGroup1_PanelGroup4_OutputLabel2"/>
            <InputText id="exampleFormatterCode2" label="{$.fh.docs.converter_custom_formatter_code}" width="md-12" rowsCount="13" value="@FhFormatter(&quot;userSecondFormatter&quot;)

        </PanelGroup>

    </PanelGroup>
    <PanelGroup width="md-12" label="{$.fh.docs.converter_inherited_conversion}" id="_Form_PanelGroup2">

        <PanelGroup width="md-12" label="{$.fh.docs.converter_inherited_to_string_conversion}" id="_Form_PanelGroup2_PanelGroup">
            <OutputLabel width="md-12" value="{$.fh.docs.converter_in_this_example_first_selectonemenu_is_bound}" id="_Form_PanelGroup2_PanelGroup_OutputLabel1"/>

            <SelectOneMenu label="{$.fh.docs.converter_please_select_user_by_last_name}" value="{selectedForeignUser}" values="{foreignUsers}" width="md-12" onChange="-" id="_Form_PanelGroup2_PanelGroup_SelectOneMenu"/>
            <OutputLabel width="md-12" value="{$.fh.docs.converter_selected_user_is}: {selectedForeignUser.lastName}, {selectedForeignUser.firstName}." id="_Form_PanelGroup2_PanelGroup_OutputLabel2"/>
            <OutputLabel width="md-12" value="{$.fh.docs.converter_selected_user_is_2} {selectedForeignUser.age} {$.fh.docs.converter_years_old}." id="_Form_PanelGroup2_PanelGroup_OutputLabel3"/>
            <OutputLabel width="md-12" value="{$.fh.docs.converter_selected_user_was_created_on}: {selectedForeignUser.creationDate}." id="_Form_PanelGroup2_PanelGroup_OutputLabel4"/>
            <OutputLabel width="md-12" value="{$.fh.docs.converter_selected_user_comes_from_system_id}: {selectedForeignUser.systemInfo}." id="_Form_PanelGroup2_PanelGroup_OutputLabel5"/>
            <OutputLabel width="md-12" value="{$.fh.docs.converter_selected_user_comes_from_company}: {selectedForeignUser.companyName}." id="_Form_PanelGroup2_PanelGroup_OutputLabel6"/>
            <InputText id="exampleCode3" label="{$.fh.docs.converter_code}" width="md-12" rowsCount="7" value="&lt;SelectOneMenu label=&quot;Please select user (by last name)&quot; value=&quot;\{selectedForeignUser\}&quot; values=&quot;\{foreignUsers\}&quot; width=&quot;md-12&quot; onChange=&quot;-&quot;/&gt;
            <PanelGroup width="md-12" label="{$.fh.docs.converter_model}" id="_Form_PanelGroup2_PanelGroup_PanelGroup">
                <InputText id="exampleModelFormatterCode2" label="{$.fh.docs.converter_code}" width="md-12" rowsCount="12" value="@Getter
            </PanelGroup>
        </PanelGroup>

    </PanelGroup>

</Form>