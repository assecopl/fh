<?xml version="1.0" encoding="UTF-8"?>
<Form xmlns="http://fh.asseco.com/form/1.0" container="mainForm" id="documentationHolder" label="{componentName}">
    <AvailabilityConfiguration>
        <ReadOnly>
            SelectComboMenuExampleCode1,SelectComboMenuExampleCode2,SelectComboMenuExampleCode3,SelectComboMenuExampleCode4,SelectComboMenuExample_SelectedCountry,SelectComboMenuExampleCode5,
            SelectComboMenuExampleCode6,SelectComboMenuExampleCode7,SelectComboMenuExampleCode8
        </ReadOnly>
    </AvailabilityConfiguration>
    <OutputLabel width="md-12" value="{description}" id="_Form_OutputLabel"/>
    <Spacer width="md-12" height="25px" id="_Form_Spacer"/>
    <TabContainer id="_Form_TabContainer">
        <Tab label="{$.fh.docs.component.examples}" id="_Form_TabContainer_Tab1">
            <PanelGroup label="{$.fh.docs.component.selectcombomenu_with_static_values}" id="_Form_TabContainer_Tab1_PanelGroup1">
                <SelectComboMenu width="md-3" values="Germany|USA|Canada|Uruguay|Japan|Poland|France|Spain" onChange="-" onInput="-" id="_Form_TabContainer_Tab1_PanelGroup1_SelectComboMenu"/>
                <InputText width="md-12" label="{$.fh.docs.component.code}" id="SelectComboMenuExampleCode1" rowsCount="1">
                    <![CDATA[
<SelectComboMenu values="Germany|USA|Canada|Uruguay|Japan|Poland|France|Spain" onChange="-" onInput="-"/>
                        ]]>
                </InputText>
            </PanelGroup>
            <PanelGroup label="{$.fh.docs.component.selectcombomenu_with_label}" id="_Form_TabContainer_Tab1_PanelGroup2">
                <SelectComboMenu width="md-3" label="{$.fh.docs.component.selectcombomenu_select_user}" values="Hettie Minks|Karey Ditzler|Malka Garrido|Blanca Quisenberry|Jared Kent|Deborah Lerner|Jerold Mcelroy|Kenton Whitefield|Genna Parker" onChange="-" onInput="-" id="_Form_TabContainer_Tab1_PanelGroup2_SelectComboMenu"/>
                <InputText width="md-12" label="{$.fh.docs.component.code}" id="SelectComboMenuExampleCode2" rowsCount="2">
                    <![CDATA[
<SelectComboMenu label="Select user" values="Hettie Minks|Karey Ditzler|Malka Garrido|Blanca Quisenberry|Jared Kent|Deborah Lerner|Jerold Mcelroy|Kenton Whitefield|Genna Parker"
onChange="-" onInput="-"/>
                        ]]>
                </InputText>
            </PanelGroup>
            <PanelGroup label="{$.fh.docs.component.selectcombomenu_with_hint}" id="_Form_TabContainer_Tab1_PanelGroup3">
                <OutputLabel width="md-12" value="{$.fh.docs.component.selectcombomenu_select_user}" id="_Form_TabContainer_Tab1_PanelGroup3_OutputLabel"/>
                <SelectComboMenu width="md-3" values="Hettie Minks|Karey Ditzler|Robert Dudley|Blanca Quisenberry|Jared Kent|Deborah Lerner|Jerold Mcelroy|Kenton Whitefield|Genna Parker" onChange="-" onInput="-" hint="{$.fh.docs.component.selectcombomenu_hint}" id="_Form_TabContainer_Tab1_PanelGroup3_SelectComboMenu"/>
                <InputText width="md-12" label="{$.fh.docs.component.code}" id="SelectComboMenuExampleCode3" rowsCount="2">
                    <![CDATA[
<SelectComboMenu label="Select user" values="Hettie Minks|Karey Ditzler|Malka Garrido|Blanca Quisenberry|Jared Kent|Deborah Lerner|Jerold Mcelroy|Kenton Whitefield|Genna Parker"
onChange="-" onInput="-"/>
                        ]]>
                </InputText>
            </PanelGroup>
            <PanelGroup label="{$.fh.docs.component.selectcombomenu_with_bootstrap_layout}" id="_Form_TabContainer_Tab1_PanelGroup4">
                <SelectComboMenu label="{$.fh.docs.component.selectcombomenu_select_user}" values="Hettie Minks|Karey Ditzler|Robert Dudley|Blanca Quisenberry|Jared Kent" width="md-8" onChange="-" onInput="-" id="_Form_TabContainer_Tab1_PanelGroup4_SelectComboMenu1"/>
                <SelectComboMenu label="{$.fh.docs.component.selectcombomenu_car}" values="BMW|Audi|Mercedes|Fiat|Subaru|Toyota" width="md-4" onChange="-" onInput="-" id="_Form_TabContainer_Tab1_PanelGroup4_SelectComboMenu2"/>
                <InputText id="SelectComboMenuExampleCode4" label="{$.fh.docs.component.code}" width="md-12" rowsCount="2">
                    <![CDATA[![ESCAPE[<SelectComboMenu label="Bootstrap layout" values="Hettie Minks|Karey Ditzler|Robert Dudley|Blanca Quisenberry|Jared Kent" width="md-8"/>
<SelectComboMenu label="Gender" values="BMW|Audi|Mercedes|Fiat|Subaru|Toyota" width="md-4"/>]]]]>
                </InputText>
            </PanelGroup>
            <PanelGroup label="{$.fh.docs.component.selectcombomenu_with_onChange}" id="_Form_TabContainer_Tab1_PanelGroup5">
                <SelectComboMenu width="md-3" label="{$.fh.docs.component.selectcombomenu_countries}" values="Austria|Norway|India|China|New Zealand" onChange="onChangeExample" onInput="-" value="{selectComboMenuOnChangeValue}" id="_Form_TabContainer_Tab1_PanelGroup5_SelectComboMenu"/>
                <OutputLabel width="md-12" value="{$.fh.docs.component.selectcombomenu_selected_country}" id="_Form_TabContainer_Tab1_PanelGroup5_OutputLabel"/>
                <InputText width="md-12" id="SelectComboMenuExample_SelectedCountry" value="{selectComboMenuOnChangeValue}" availability="VIEW"/>
                <InputText id="SelectComboMenuExampleCode5" label="{$.fh.docs.component.code}" width="md-12" rowsCount="3">
                    <![CDATA[![ESCAPE[<SelectComboMenu label="onChange event" values="Austria|Norway|India|China|New Zealand" value="{selectComboMenuOnChangeValue}"/>
<OutputLabel value="Selected country"/>
<InputText id="SelectComboMenuExample_SelectedCountry" value="{selectComboMenuOnChangeValue}" availability="VIEW"/>]]]]>
                </InputText>
            </PanelGroup>
            <PanelGroup label="{$.fh.docs.component.selectcombomenu_with_static_values_with_collection_as_binding}" id="_Form_TabContainer_Tab1_PanelGroup6">
                <SelectComboMenu width="md-3" label="{$.fh.docs.component.selectcombomenu_select_user}" value="{simpleSelectedComboMenu}" filterFunction="{userSelectedComboMenuFilter}" values="{simpleSelectedComboMenuData}" onChange="-" onInput="-" id="_Form_TabContainer_Tab1_PanelGroup6_SelectComboMenu"/>
                <OutputLabel width="md-12" id="boundSelectedComboMenuUserLabel1" value="{$.fh.docs.component.selectcombomenu_selected_user}: {simpleSelectedComboMenu.firstName}, {simpleSelectedComboMenu.lastName}."/>
                <OutputLabel width="md-12" id="boundSelectedComboMenuUserLabel2" value="{$.fh.docs.component.selectcombomenu_selected_user_is} {simpleSelectedComboMenu.age} {$.fh.docs.component.selectcombomenu_selected_user_age}."/>
                <OutputLabel width="md-12" id="boundSelectedComboMenuUserLabel3" value="{$.fh.docs.component.selectcombomenu_selected_user_created_on} : {simpleSelectedComboMenu.creationDate}."/>
                <InputText width="md-12" label="{$.fh.docs.component.code}" id="SelectComboMenuExampleCode6" rowsCount="4">
                    <![CDATA[
<SelectComboMenu label="Select user" selectedItem="\{simpleSelectedCombo\}" filterFunction="\{userSelectedComboMenuFilter\}" values="\{simpleSelectedComboMenuData\}" onChange="-" onInput="-"/>
<OutputLabel id="boundSelectedComboMenuUserLabel1" value="Selected user: \{simpleSelectedComboMenu.firstName\}, \{simpleSelectedComboMenu.lastName\}." />
<OutputLabel id="boundSelectedComboMenuUserLabel2" value="Selected user is \{simpleSelectedComboMenu.age\} years old." />
<OutputLabel id="boundSelectedComboMenuUserLabel3" value="Selected user was created on : \{simpleSelectedComboMenu.creationDate\}." />
                        ]]>
                </InputText>
            </PanelGroup>
            <PanelGroup label="{$.fh.docs.component.selectcombomenu_with_empty_value}" id="_Form_TabContainer_Tab1_PanelGroup7">
                <SelectComboMenu width="md-3" label="{$.fh.docs.component.selectcombomenu_countries}" emptyLabel="true" emptyLabelText="--------" values="Germany|USA|Canada|Uruguay|Japan|Poland|France|Spain" onChange="-" onInput="-" id="_Form_TabContainer_Tab1_PanelGroup7_SelectComboMenu"/>
                <InputText width="md-12" label="{$.fh.docs.component.code}" id="SelectComboMenuExampleCode7" rowsCount="1">
                    <![CDATA[
<SelectComboMenu label="{$.fh.docs.component.selectcombomenu_countries}" emptyLabel="true" emptyLabelText="--------" values="Germany|USA|Canada|Uruguay|Japan|Poland|France|Spain" onChange="-" onInput="-"/>
                        ]]>
                </InputText>
            </PanelGroup>
            <PanelGroup label="{$.fh.docs.component.selectcombomenu_with_multi_sizes_and_empty_value}" id="_Form_TabContainer_Tab1_PanelGroup8">
            <SelectComboMenu label="{$.fh.docs.component.selectcombomenu_countries}" emptyLabel="true" emptyLabelText="--------" width="sm-12,md-6,lg-3" values="Germany|USA|Canada|Uruguay|Japan|Poland|France|Spain" onChange="-" onInput="-" hint="{$.fh.docs.component.selectcombomenu_with_multi_sizes_and_empty_value_hint}" id="_Form_TabContainer_Tab1_PanelGroup8_SelectComboMenu"/>
            <InputText width="md-12" label="{$.fh.docs.component.code}" id="SelectComboMenuExampleCode8" rowsCount="2">
                <![CDATA[
<SelectComboMenu label="{$.fh.docs.component.selectcombomenu_countries}" emptyLabel="true" emptyLabelText="--------" width="sm-12,md-6,lg-3" values="Germany|USA|Canada|Uruguay|Japan|Poland|France|Spain" onChange="-" onInput="-"/>
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