<?xml version="1.0" encoding="UTF-8"?>
<Form xmlns="http://fh.asseco.com/form/1.0" container="mainForm" id="documentationHolder" label="{componentName}">
    <AvailabilityConfiguration>
        <ReadOnly>comboExampleCode1,comboExampleCode2</ReadOnly>
        <SetByProgrammer>
            boundComboUserLabel1,boundComboUserLabel2,boundComboUserLabel3,boundComboUserLabel4,boundComboUserLabel5,boundComboUserLabel6,
            boundComboUserLabel71,boundComboUserLabel81,boundComboUserLabel91,boundComboUserLabel72,boundComboUserLabel82,boundComboUserLabel92
        </SetByProgrammer>
        <ReadOnly>
            comboExampleCode6,comboExampleCode7,comboExampleCode1,comboExampleCode2,comboExampleCode8,comboExampleCode4_2,comboExampleCode5,
            selectedUserInputText,selectedUserInputText2,comboExampleCode1withDisplayFunction1,comboExampleCode1withDisplayFunction2,comboExampleCode1withMuliselect,
            comboExampleCode1withEnum,comboExampleCode1withEnumAndMultiselect
        </ReadOnly>
    </AvailabilityConfiguration>
    <OutputLabel width="md-12" value="{description}" id="_Form_OutputLabel"/>
    <Spacer width="md-12" height="25px" id="_Form_Spacer"/>
    <TabContainer id="_Form_TabContainer">
        <Tab label="{$.fh.docs.component.examples}" id="_Form_TabContainer_Tab1">
                <PanelGroup width="md-12" label="{$.fh.docs.component.combo_with_static_values}" id="_Form_TabContainer_Tab1_PanelGroup1">
                    <Select2Combo width="md-12" label="{$.fh.docs.component.combo_select_user}" values="Hettie Minks|Karey Ditzler|Malka Garrido|Blanca Quisenberry|Jared Kent|Deborah Lerner|Jerold Mcelroy|Kenton Whitefield|Genna Parker|Madelyn Hanline|Trinity Kelton|Melaine Claassen|Lucilla Scannell|Caryn Vermillion|Erma Soja|Tyree List|Keira Heesch|Rodrigo Erdman|Kellye Chiasson|Marisha Selle" onChange="-" onInput="-" id="_Form_TabContainer_Tab1_PanelGroup1_Combo"/>
                    <InputText width="md-12" label="{$.fh.docs.component.code}" id="comboExampleCode6" rowsCount="4">
                        <![CDATA[
<Select2Combo label="Select user" values="Hettie Minks|Karey Ditzler|Malka Garrido|Blanca Quisenberry|Jared Kent|Deborah Lerner|Jerold Mcelroy|Kenton Whitefield|Genna Parker|Madelyn Hanline|Trinity Kelton|Melaine Claassen|Lucilla Scannell|Caryn Vermillion|Erma Soja|Tyree List|Keira Heesch|Rodrigo Erdman|Kellye Chiasson|Marisha Selle" onChange="-" onInput="-"/>
                        ]]>
                    </InputText>
                </PanelGroup>

                <PanelGroup width="md-12" label="{$.fh.docs.component.combo_with_static_values_and_hint}" id="_Form_TabContainer_Tab1_PanelGroup2">
                    <Select2Combo width="md-12" label="{$.fh.docs.component.combo_select_user}" hint="{$.fh.docs.component.combo_this_is_example_hint}" values="Hettie Minks|Karey Ditzler|Malka Garrido|Blanca Quisenberry|Jared Kent|Deborah Lerner|Jerold Mcelroy|Kenton Whitefield|Genna Parker|Madelyn Hanline|Trinity Kelton|Melaine Claassen|Lucilla Scannell|Caryn Vermillion|Erma Soja|Tyree List|Keira Heesch|Rodrigo Erdman|Kellye Chiasson|Marisha Selle" onChange="-" onInput="-" id="_Form_TabContainer_Tab1_PanelGroup2_Combo"/>
                    <InputText width="md-12" label="{$.fh.docs.component.code}" id="comboExampleCode7" rowsCount="4">
                        <![CDATA[
<Select2Combo label="Select user" hint="This is example hint" values="Hettie Minks|Karey Ditzler|Malka Garrido|Blanca Quisenberry|Jared Kent|Deborah Lerner|Jerold Mcelroy|Kenton Whitefield|Genna Parker|Madelyn Hanline|Trinity Kelton|Melaine Claassen|Lucilla Scannell|Caryn Vermillion|Erma Soja|Tyree List|Keira Heesch|Rodrigo Erdman|Kellye Chiasson|Marisha Selle" onChange="-" onInput="-"/>
                        ]]>
                    </InputText>
                </PanelGroup>

                <PanelGroup width="md-12" label="{$.fh.docs.component.combo_with_static_values_with_collection_as_binding}" id="_Form_TabContainer_Tab1_PanelGroup3">
                    <Select2Combo width="md-12" label="{$.fh.docs.component.combo_select_user}" value="{simpleSelectedCombo}" filterFunction="{userComboFilter}" values="{simpleComboData}" onChange="-" onInput="-" id="_Form_TabContainer_Tab1_PanelGroup3_Combo"/>
                    <OutputLabel id="boundComboUserLabel4" width="md-12" value="{$.fh.docs.component.combo_typed_user_is}: {simpleSelectedCombo.firstName}, {simpleSelectedCombo.lastName}."/>
                    <OutputLabel id="boundComboUserLabel5" width="md-12" value="{$.fh.docs.component.combo_typed_user_is_2} {simpleSelectedCombo.age} {$.fh.docs.component.combo_years_old}."/>
                    <OutputLabel id="boundComboUserLabel6" width="md-12" value="{$.fh.docs.component.combo_typed_user_was_created_on} : {simpleSelectedCombo.creationDate}."/>
                    <InputText width="md-12" label="{$.fh.docs.component.code}" id="comboExampleCode1" rowsCount="4">
                        <![CDATA[
<Select2Combo label="Select user" selectedItem="\{simpleSelectedCombo\}" filterFunction="\{userComboFilter\}" values="\{simpleComboData\}" onChange="-" onInput="-"/>
<OutputLabel id="boundComboUserLabel4" width="md-12" value="Typed user is: \{simpleSelectedCombo.firstName\}, \{simpleSelectedCombo.lastName\}." />
<OutputLabel id="boundComboUserLabel5" width="md-12" value="Typed user is \{simpleSelectedCombo.age\} years old." />
<OutputLabel id="boundComboUserLabel6" width="md-12" value="Typed user was created on : \{simpleSelectedCombo.creationDate\}." />
                        ]]>
                    </InputText>
                </PanelGroup>

                <PanelGroup width="md-12" label="{$.fh.docs.component.combo_with_advanced_biding_with_multivaluemap_as_binding}" id="_Form_TabContainer_Tab1_PanelGroup4">
                    <Select2Combo width="md-12" label="{$.fh.docs.component.combo_select_user}" value="{selectedCombo}" filterFunction="{userComboFilter}" values="{comboData}" onChange="-" onInput="-" id="_Form_TabContainer_Tab1_PanelGroup4_Combo"/>
                    <OutputLabel id="boundComboUserLabel1" width="md-12" value="{$.fh.docs.component.combo_typed_user_is}: {selectedCombo.firstName}, {selectedCombo.lastName}."/>
                    <OutputLabel id="boundComboUserLabel2" width="md-12" value="{$.fh.docs.component.combo_typed_user_is_2} {selectedCombo.age} {$.fh.docs.component.combo_years_old}."/>
                    <OutputLabel id="boundComboUserLabel3" width="md-12" value="{$.fh.docs.component.combo_typed_user_was_created_on} : {selectedCombo.creationDate}."/>
                    <Button onClick="addUserToAdmins" width="md-4" label="{$.fh.docs.component.combo_add_user_to_admins}" id="_Form_TabContainer_Tab1_PanelGroup4_Button"/>
                    <InputText width="md-12" value="{selectedCombo}" onChange="-" id="selectedUserInputText2"/>
                    <InputText width="md-12" label="{$.fh.docs.component.code}" id="comboExampleCode2" rowsCount="6">
                        <![CDATA[
<Select2Combo label="Select user" selectedItem="\{selectedCombo\}" filterFunction="\{userComboFilter\}" values="\{comboData\}" onChange="-" onInput="-"/>
<OutputLabel id="boundComboUserLabel1" width="md-12" value="Typed user is: \{selectedCombo.firstName\}, \{selectedCombo.lastName\}." />
<OutputLabel id="boundComboUserLabel2" width="md-12" value="Typed user is \{selectedCombo.age\} years old." />
<OutputLabel id="boundComboUserLabel3" width="md-12" value="Typed user was created on : \{selectedCombo.creationDate\}." />
<Button onClick="addUserToAdmins" label="Add user to admins" />
<InputText value="\{selectedCombo\}" onChange="-" id="selectedUserInputText2"/>
                        ]]>
                    </InputText>
                </PanelGroup>

                <PanelGroup width="md-12" label="{$.fh.docs.component.combo_with_advanced_biding_with_multivaluemap_as_binding_and_preload_attribute}" id="_Form_TabContainer_Tab1_PanelGroup5">
                    <OutputLabel width="md-12" value="{$.fh.docs.component.combo_this_combo_will_show_all_values_even_no_text_is_typed}." id="_Form_TabContainer_Tab1_PanelGroup5_OutputLabel"/>
                    <Select2Combo width="md-12" label="{$.fh.docs.component.combo_select_user}" preload="true" value="{selectedCombo}" filterFunction="{userComboFilter}" values="{comboData}" onChange="-" onInput="-" id="_Form_TabContainer_Tab1_PanelGroup5_Combo"/>
                    <InputText width="md-12" value="{selectedCombo}" onChange="-" id="selectedUserInputText"/>
                    <InputText width="md-12" label="{$.fh.docs.component.code}" id="comboExampleCode8" rowsCount="2">
                        <![CDATA[![ESCAPE[<Select2Combo label="Select user" preload="true" selectedItem="{selectedCombo}" filterFunction="{userComboFilter}"  values="{comboData}" onChange="-" onInput="-"/>
                    <InputText value="{selectedCombo}" onChange="-" id="selectedUserInputText" />]]]]>
                    </InputText>
                </PanelGroup>


                 <PanelGroup width="md-12" label="{$.fh.docs.component.combo_with_static_values_with_collection_as_binding_and_displayFunction}" id="_Form_TabContainer_Tab1_PanelGroup6">
                    <Select2Combo width="md-12" label="{$.fh.docs.component.combo_select_user}" value="{simpleSelectedComboWithDisplayFunction}" displayFunction="{userAsFirstLastNameFunction}" values="{simpleComboData}" onChange="-" onInput="-" id="_Form_TabContainer_Tab1_PanelGroup6_Combo"/>
                    <OutputLabel id="boundComboUserLabel71" width="md-12" value="{$.fh.docs.component.combo_typed_user_is}: {simpleSelectedComboWithDisplayFunction.firstName}, {simpleSelectedComboWithDisplayFunction.lastName}."/>
                    <OutputLabel id="boundComboUserLabel81" width="md-12" value="{$.fh.docs.component.combo_typed_user_is_2} {simpleSelectedComboWithDisplayFunction.age} {$.fh.docs.component.combo_years_old}."/>
                    <OutputLabel id="boundComboUserLabel91" width="md-12" value="{$.fh.docs.component.combo_typed_user_was_created_on} : {simpleSelectedComboWithDisplayFunction.creationDate}."/>
                    <InputText width="md-12" label="{$.fh.docs.component.code}" id="comboExampleCode1withDisplayFunction1" rowsCount="4">
                        <![CDATA[
<Select2Combo label="Select user" selectedItem="\{simpleSelectedComboWithDisplayFunction\}" displayFunction="\{userAsFirstLastNameFunction\}" values="\{simpleComboData\}" onChange="-" onInput="-"/>
<OutputLabel id="boundComboUserLabel7" width="md-12" value="Typed user is: \{simpleSelectedComboWithDisplayFunction.firstName\}, \{simpleSelectedComboWithDisplayFunction.lastName\}." />
<OutputLabel id="boundComboUserLabel8" width="md-12" value="Typed user is \{simpleSelectedComboWithDisplayFunction.age\} years old." />
<OutputLabel id="boundComboUserLabel9" width="md-12" value="Typed user was created on : \{simpleSelectedComboWithDisplayFunction.creationDate\}." />
                        ]]>
                    </InputText>
                </PanelGroup>

                 <PanelGroup width="md-12" label="{$.fh.docs.component.combo_with_static_values_with_collection_as_binding_and_displayRuleSpel}" id="_Form_TabContainer_Tab1_PanelGroup7">
                    <Select2Combo width="md-12" label="{$.fh.docs.component.combo_select_user}" value="{simpleSelectedComboWithDisplayFunction}" displayRule="firstName+' vel: '+lastName" values="{simpleComboData}" onChange="-" onInput="-" id="_Form_TabContainer_Tab1_PanelGroup7_Combo"/>
                    <OutputLabel id="boundComboUserLabel72" width="md-12" value="{$.fh.docs.component.combo_typed_user_is}: {simpleSelectedComboWithDisplayFunction.firstName}, {simpleSelectedComboWithDisplayFunction.lastName}."/>
                    <OutputLabel id="boundComboUserLabel82" width="md-12" value="{$.fh.docs.component.combo_typed_user_is_2} {simpleSelectedComboWithDisplayFunction.age} {$.fh.docs.component.combo_years_old}."/>
                    <OutputLabel id="boundComboUserLabel92" width="md-12" value="{$.fh.docs.component.combo_typed_user_was_created_on} : {simpleSelectedComboWithDisplayFunction.creationDate}."/>
                    <InputText width="md-12" label="{$.fh.docs.component.code}" id="comboExampleCode1withDisplayFunction2" rowsCount="4">
                        <![CDATA[
<Select2Combo label="Select user" selectedItem="\{simpleSelectedComboWithDisplayFunction\}" displayExpression="\{firstName+' vel: '+lastName\}" values="\{simpleComboData\}" onChange="-" onInput="-"/>
<OutputLabel id="boundComboUserLabel7" width="md-12" value="Typed user is: \{simpleSelectedComboWithDisplayFunction.firstName\}, \{simpleSelectedComboWithDisplayFunction.lastName\}." />
<OutputLabel id="boundComboUserLabel8" width="md-12" value="Typed user is \{simpleSelectedComboWithDisplayFunction.age\} years old." />
<OutputLabel id="boundComboUserLabel9" width="md-12" value="Typed user was created on : \{simpleSelectedComboWithDisplayFunction.creationDate\}." />
                        ]]>
                    </InputText>
                </PanelGroup>


                <PanelGroup width="md-12" label="{$.fh.docs.component.combo_with_multi_sizes}" id="_Form_TabContainer_Tab1_PanelGroup8">
                    <Select2Combo id="comboCode4_1" label="{$.fh.docs.component.combo_select_user}" values="Hettie Minks|Karey Ditzler|Malka Garrido|Blanca Quisenberry|Jared Kent|Deborah Lerner|Jerold Mcelroy|Kenton Whitefield|Genna Parker|Madelyn Hanline|Trinity Kelton|Melaine Claassen|Lucilla Scannell|Caryn Vermillion|Erma Soja|Tyree List|Keira Heesch|Rodrigo Erdman|Kellye Chiasson|Marisha Selle" onChange="-" onInput="-" width="sm-8,md-4,lg-10"/>
                    <InputText id="comboExampleCode4_2" label="{$.fh.docs.component.code}" width="md-12" rowsCount="3">
                        <![CDATA[
<Select2Combo id="comboCode4_1" label="Select user" values="Hettie Minks|Karey Ditzler|Malka Garrido|Blanca Quisenberry|Jared Kent|Deborah Lerner|Jerold Mcelroy|Kenton Whitefield|Genna Parker|Madelyn Hanline|Trinity Kelton|Melaine Claassen|Lucilla Scannell|Caryn Vermillion|Erma Soja|Tyree List|Keira Heesch|Rodrigo Erdman|Kellye Chiasson|Marisha Selle" onChange="-" onInput="-" width="xs-2,sm-8,md-4,lg-10"/>
                        ]]>
                    </InputText>
                </PanelGroup>

                <PanelGroup width="md-12" label="{$.fh.docs.component.combo_with_empty_value}" id="_Form_TabContainer_Tab1_PanelGroup9">
                    <Select2Combo width="md-12" label="{$.fh.docs.component.combo_select_user}" value="{comboUserWithEmptyValue}" filterFunction="{userComboFilter}" values="{comboDataWithEmptyValue}" onChange="-" onInput="-" emptyValue="true" onEmptyValue="-" id="_Form_TabContainer_Tab1_PanelGroup9_Combo"/>
                    <OutputLabel id="boundComboUserLabel5_1" width="md-12" value="{$.fh.docs.component.combo_typed_user_is}: {comboUserWithEmptyValue.firstName} {comboUserWithEmptyValue.lastName}."/>
                    <InputText width="md-12" label="{$.fh.docs.component.code}" id="comboExampleCode5" rowsCount="3">
                        <![CDATA[
<Select2Combo label="Select user" selectedItem="\{comboUserWithEmptyValue\}" filterFunction="\{userComboFilter\}" values="\{comboDataWithEmptyValue\}" onChange="-" onInput="-" emptyValue="true" onEmptyValue="-"/>
<OutputLabel id="boundComboUserLabel5_1" width="md-12" value="Selected user is: \{comboUserWithEmptyValue.firstName\} \{comboUserWithEmptyValue.lastName\}." />
                        ]]>
                    </InputText>
                </PanelGroup>

            <PanelGroup width="md-12" label="{$.fh.docs.component.combo_with_multiselect}" id="_Form_TabContainer_Tab1_PanelGroup10">
                <Select2Combo width="md-12" label="{$.fh.docs.component.combo_select_users}" value="{selectedUsers}" displayExpression="firstName+' vel: '+lastName" values="{simpleComboData}" onChange="-" onInput="-" multiselect="true" emptyValue="true" onEmptyValue="-" id="_Form_TabContainer_Tab1_PanelGroup10_Combo1"/>
                <InputText width="md-12" label="{$.fh.docs.component.code}" id="comboExampleCode1withMuliselect" rowsCount="3">
                    <![CDATA[
<Select2Combo label="\{$.fh.docs.component.combo_select_user\}" value="\{selectedUsers\}" displayExpression="firstName+' vel: '+lastName" values="\{simpleComboData\}" onChange="-" onInput="-" multiselect="true" emptyValue="true" onEmptyValue="-"/>
                        ]]>
                </InputText>
                <Select2Combo icon="" width="md-12" label="{$.fh.docs.component.combo_select_users}" value="{selectedUsers}" displayExpression="firstName+' vel: '+lastName" values="{comboData}" onChange="-" onInput="-" multiselect="true" emptyValue="false" onEmptyValue="-" id="_Form_TabContainer_Tab1_PanelGroup10_Combo2"/>
            </PanelGroup>

            <PanelGroup width="md-12" label="{$.fh.docs.component.combo_with_enum}" id="_Form_TabContainer_Tab1_PanelGroup11">
                <Select2Combo id="comboWithEnum" width="md-12" label="{$.fh.docs.component.combo_with_enum_pickColor}" values="{enumColors}" emptyValue="true" onEmptyValue="-" onChange="-"/>
                <InputText width="md-12" label="{$.fh.docs.component.code}" id="comboExampleCode1withEnum" rowsCount="2">
                    <![CDATA[
<Select2Combo label="\{$.fh.docs.component.combo_with_enum_pickColor\}" values="\{enumColors\}" onChange="-" emptyValue="true" onEmptyValue="-"/>
                        ]]>
                </InputText>
            </PanelGroup>

            <PanelGroup width="md-12" label="{$.fh.docs.component.combo_with_enum_and_multiselect}" id="_Form_TabContainer_Tab1_PanelGroup12">
                <Select2Combo id="comboWithEnumAndMultiselect" width="md-12" label="{$.fh.docs.component.combo_with_enum_pickColors}" value="{selectedColors}" values="{enumColors}" emptyValue="true" onEmptyValue="-" onChange="-" multiselect="true"/>
                <InputText width="md-12" label="{$.fh.docs.component.code}" id="comboExampleCode1withEnumAndMultiselect" rowsCount="2">
                    <![CDATA[
<Select2Combo label="\{$.fh.docs.component.combo_with_enum_pickColors\}" value="\{selectedColors\}" values="\{enumColors\}" onChange="-" multiselect="true" emptyValue="true" onEmptyValue="-"/>
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