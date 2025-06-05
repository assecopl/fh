<?xml version="1.0" encoding="UTF-8"?>
<Form xmlns="http://fh.asseco.com/form/1.0" container="mainForm" id="documentationHolder" label="{componentName}">
    <AvailabilityConfiguration>
        <ReadOnly>optionsListExampleCode1,optionsListExampleCode2,optionsListExampleCode3</ReadOnly>
    </AvailabilityConfiguration>
    <OutputLabel width="md-12" value="{description}" id="_Form_OutputLabel"/>
    <Spacer width="md-12" height="25px" id="_Form_Spacer"/>
    <TabContainer id="_Form_TabContainer">
        <Tab label="{$.fh.docs.component.examples}" id="_Form_TabContainer_Tab1">
           <PanelGroup label="{$.fh.docs.component.optionslist_elements}" id="_Form_TabContainer_Tab1_PanelGroup1">
            <OptionsList width="sm-12" values="{elements}" title="{$.fh.docs.component.optionslist_example}" displayCheckbox="true" id="_Form_TabContainer_Tab1_PanelGroup1_OptionsList"/>
            <Spacer width="md-12" height="10" id="_Form_TabContainer_Tab1_PanelGroup1_Spacer1"/>
            <Button width="md-3" label="{$.fh.docs.component.optionslist_submit}" onClick="onBtnListClicked()" id="_Form_TabContainer_Tab1_PanelGroup1_Button1"/>
            <Spacer width="md-12" height="10" id="_Form_TabContainer_Tab1_PanelGroup1_Spacer2"/>
            <Button width="md-4" label="{$.fh.docs.component.optionslist_add_element_first}" onClick="onAddElementFirst()" id="_Form_TabContainer_Tab1_PanelGroup1_Button2"/>
            <Button width="md-4" label="{$.fh.docs.component.optionslist_add_element_middle}" onClick="onAddElementMiddle()" id="_Form_TabContainer_Tab1_PanelGroup1_Button3"/>
            <Button width="md-4" label="{$.fh.docs.component.optionslist_add_element_last}" onClick="onAddElementLast()" id="_Form_TabContainer_Tab1_PanelGroup1_Button4"/>
            <Button width="md-4" label="{$.fh.docs.component.optionslist_add_empty_element}" onClick="onAddEmptyElement()" id="_Form_TabContainer_Tab1_PanelGroup1_Button5"/>
            <Button width="md-4" label="{$.fh.docs.component.optionslist_remove_first_element}" onClick="onRemoveFirst()" id="_Form_TabContainer_Tab1_PanelGroup1_Button6"/>
            <Button width="md-4" label="{$.fh.docs.component.optionslist_remove_last_element}" onClick="onRemoveLast()" id="_Form_TabContainer_Tab1_PanelGroup1_Button7"/>

            <InputText id="optionsListExampleCode1" width="md-12" rowsCount="5">
               <![CDATA[![ESCAPE[
            <OptionsList width="lg-6" values="{elements}" title="List example" displayCheckbox="true"/>
            <Spacer/>
            <Button width="md-4" label="Submit" onClick="onBtnListClicked()"/>
            <Spacer/>
            <Group>
              <Button width="md-4" label="Add element First" onClick="onAddElementFirst()"/>
              <Button width="md-4" label="Add element Middle" onClick="onAddElementMiddle()"/>
              <Button width="md-4" label="Add element Last" onClick="onAddElementLast()"/>
            </Group>
            <Group>
              <Button width="md-4" label="Add empty element" onClick="onAddEmptyElement()"/>
            </Group>
            <Group>
              <Button width="md-4" label="Remove first element" onClick="onRemoveFirst()"/>
              <Button width="md-4" label="Remove last element" onClick="onRemoveLast()"/>
            </Group>
                 ]]]]>
            </InputText>
            </PanelGroup>

            <PanelGroup label="{$.fh.docs.component.optionslist_example_of_optionslist_with_empty_element}" id="_Form_TabContainer_Tab1_PanelGroup2">
                <OptionsList width="md-6" values="{listWithEmptyElement}" title="{$.fh.docs.component.optionslist_original_list}" emptyValue="true" emptyValueLabel="{$.fh.docs.component.optionslist_empty_value}" id="_Form_TabContainer_Tab1_PanelGroup2_OptionsList1"/>
                <OptionsList width="md-6" values="{selectedList}" title="{$.fh.docs.component.optionslist_copied_list}" id="_Form_TabContainer_Tab1_PanelGroup2_OptionsList2"/>
                <Spacer width="md-12" id="_Form_TabContainer_Tab1_PanelGroup2_Spacer"/>
                <Button label="{$.fh.docs.component.optionslist_copy}" width="md-4" onClick="onSelectedListWithEmptyElement()" id="_Form_TabContainer_Tab1_PanelGroup2_Button"/>
                <OutputLabel id="boundComboUserLabel5_1" width="md-12" value="{$.fh.docs.component.optionslist_example_of_optionslist_with_empty_element_is_selected_1} {emptyListElement.id}{$.fh.docs.component.optionslist_example_of_optionslist_with_empty_element_is_selected_2} {emptyListElement.value}."/>
                <InputText id="optionsListExampleCode2" width="md-12" rowsCount="5">
                    <![CDATA[![ESCAPE[
<OptionsList width="md-6" values="{listWithEmptyElement}"  title="Original List" emptyValue="true" emptyValueLabel="Empty value"/>
<OptionsList width="md-6" values="{selectedList}"  title="Copied list." />
<Spacer />
<Button label="Copy" width="md-4" onClick="onSelectedListWithEmptyElement()"/>
                        ]]]]>
                </InputText>
            </PanelGroup>

            <PanelGroup label="{$.fh.docs.component.optionslist_example_of_optionslist_with_multiple_sizes_for_different_displays}" id="_Form_TabContainer_Tab1_PanelGroup3">
                <OptionsList width="sm-12,md-9,lg-6" values="First|Second|Third" title="{$.fh.docs.component.optionslist_sample_optionslist_with_multiple_sizes}" id="_Form_TabContainer_Tab1_PanelGroup3_OptionsList"/>
                <InputText id="optionsListExampleCode3" width="md-12">
                    <![CDATA[![ESCAPE[
<OptionsList width="sm-12,md-9,lg-6" values="First|Second|Third"  title="Sample OptionsList with multiple sizes"/>
                        ]]]]>
                </InputText>
            </PanelGroup>
        </Tab>
        <Tab label="{$.fh.docs.component.optionslist_usage_and_code}" id="_Form_TabContainer_Tab2">
            <PanelGroup label="{$.fh.docs.component.optionslist_usage_example}" id="_Form_TabContainer_Tab2_PanelGroup">
                <OutputLabel width="md-12" value="{$.fh.docs.component.optionslist_usage_of_option_list_with_collection}:" id="_Form_TabContainer_Tab2_PanelGroup_OutputLabel1"/>
                <OutputLabel width="md-12" value="&lt;OptionsList width=&quot;md-3&quot; values=&quot;\{elements\}&quot; title=&quot;List example&quot;/&gt;" id="_Form_TabContainer_Tab2_PanelGroup_OutputLabel2"/>
                <OutputLabel width="md-12" value="{$.fh.docs.component.optionslist_model_of_list_elemetnts}:" id="_Form_TabContainer_Tab2_PanelGroup_OutputLabel3"/>
                <OutputLabel width="md-12" value="{$.fh.docs.component.optionslist_model_of_list_elemetnts2}" id="_Form_TabContainer_Tab2_PanelGroup_OutputLabel4"/>
            </PanelGroup>
        </Tab>
        <Tab label="{$.fh.docs.component.attributes}" id="_Form_TabContainer_Tab3">
            <Table iterator="item" collection="{attributes}" width="15" id="_Form_TabContainer_Tab3_Table">
                <Column label="{$.fh.docs.component.attributes_identifier}" value="{item.name}" width="15" id="_Form_TabContainer_Tab3_Table_Column1"/>
                <Column label="{$.fh.docs.component.attributes_type}" value="{item.type}" width="10" id="_Form_TabContainer_Tab3_Table_Column2"/>
                <Column label="{$.fh.docs.component.attributes_boundable}" value="{item.boundable}" width="20" id="_Form_TabContainer_Tab3_Table_Column3"/>
                <Column label="{$.fh.docs.component.attributes_default_value}" value="{item.defaultValue}" width="20" id="_Form_TabContainer_Tab3_Table_Column4"/>
                <Column label="{$.fh.docs.component.attributes_description}" value="{item.description}" width="40" id="_Form_TabContainer_Tab3_Table_Column5"/>
            </Table>
        </Tab>
    </TabContainer>
    <Button id="pBack" label="[icon='fa fa-chevron-left'] {$.fh.docs.component.back}" onClick="backToFormComponentsList()"/>
</Form>