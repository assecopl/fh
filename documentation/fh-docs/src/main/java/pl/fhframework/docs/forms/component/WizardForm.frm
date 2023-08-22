<?xml version="1.0" encoding="UTF-8"?>
<Form xmlns="http://fh.asseco.com/form/1.0" container="mainForm" id="documentationHolder" label="{componentName}">
    <AvailabilityConfiguration>
        <ReadOnly>
            wizardExampleCode
        </ReadOnly>
        <SetByProgrammer>
            wizardOptionalTab
        </SetByProgrammer>
    </AvailabilityConfiguration>
    <OutputLabel width="md-12" value="{description}" id="_Form_OutputLabel"/>
    <Spacer width="md-12" height="25px" id="_Form_Spacer"/>
    <TabContainer id="_Form_TabContainer">
        <Tab label="{$.fh.docs.component.examples}" id="_Form_TabContainer_Tab1">
                <PanelGroup width="md-12" label="{$.fh.docs.component.wizard_wizard_example}" id="_Form_TabContainer_Tab1_PanelGroup">
                    <Wizard id="wizardExample" activeTabIndex="{boundActiveTabId}">
                        <Tab id="wizardTab1" label="{$.fh.docs.component.wizard_step} 1">
                            <PanelGroup label="{$.fh.docs.component.wizard_first_step}" id="_Form_TabContainer_Tab1_PanelGroup_Wizard_Tab1_PanelGroup">
                            <OutputLabel width="md-12" value="{$.fh.docs.component.wizard_this_is_first_step}." id="_Form_TabContainer_Tab1_PanelGroup_Wizard_Tab1_PanelGroup_OutputLabel"/>
                                <Button label="{$.fh.docs.component.wizard_show_optional_process_step}" onClick="showOptionalStep()" width="md-4" id="_Form_TabContainer_Tab1_PanelGroup_Wizard_Tab1_PanelGroup_Button1"/>
                                <Button label="{$.fh.docs.component.wizard_hide_optional_process_step}" onClick="hideOptionalStep()" width="md-4" id="_Form_TabContainer_Tab1_PanelGroup_Wizard_Tab1_PanelGroup_Button2"/>
                            </PanelGroup>
                        </Tab>
                        <Tab id="wizardOptionalTab" label="{$.fh.docs.component.wizard_optional_step}">
                            <PanelGroup label="{$.fh.docs.component.wizard_optional_step}" id="_Form_TabContainer_Tab1_PanelGroup_Wizard_Tab2_PanelGroup">
                            <OutputLabel width="md-12" value="{$.fh.docs.component.wizard_this_is_optional_step}." id="_Form_TabContainer_Tab1_PanelGroup_Wizard_Tab2_PanelGroup_OutputLabel"/>
                            </PanelGroup>
                        </Tab>
                        <Tab id="wizardTab2" label="{$.fh.docs.component.wizard_step} 2">
                            <PanelGroup label="{$.fh.docs.component.wizard_second_step}" id="_Form_TabContainer_Tab1_PanelGroup_Wizard_Tab3_PanelGroup">
                            <OutputLabel width="md-12" value="{$.fh.docs.component.wizard_this_is_second_step}." id="_Form_TabContainer_Tab1_PanelGroup_Wizard_Tab3_PanelGroup_OutputLabel"/>
                            </PanelGroup>
                        </Tab>
                        <Tab id="wizardTab3" label="{$.fh.docs.component.wizard_step} 3">
                            <PanelGroup label="{$.fh.docs.component.wizard_third_step}" id="_Form_TabContainer_Tab1_PanelGroup_Wizard_Tab4_PanelGroup">
                            <OutputLabel width="md-12" value="{$.fh.docs.component.wizard_this_is_third_step}." id="_Form_TabContainer_Tab1_PanelGroup_Wizard_Tab4_PanelGroup_OutputLabel"/>
                            </PanelGroup>
                        </Tab>
                        <Tab id="wizardTab4" label="{$.fh.docs.component.wizard_step} 4">
                            <PanelGroup label="{$.fh.docs.component.wizard_fourth_step}" id="_Form_TabContainer_Tab1_PanelGroup_Wizard_Tab5_PanelGroup">
                            <OutputLabel width="md-12" value="{$.fh.docs.component.wizard_this_is_fourth_step}." id="_Form_TabContainer_Tab1_PanelGroup_Wizard_Tab5_PanelGroup_OutputLabel"/>
                            </PanelGroup>
                        </Tab>
                    </Wizard>
                    <InputText id="wizardExampleCode" label="{$.fh.docs.component.code}" width="md-12" rowsCount="31">
                            <![CDATA[![ESCAPE[<Wizard id="wizardExample" activeTabIndex="{boundActiveTabId}">
<Tab id="wizardTab1" label="Step 1">
    <PanelGroup label="First step">
      <OutputLabel value="This is first step."/>
      <PanelGroup width="md-12">
        <Button label="Show optional process step" onClick="showOptionalStep()" width="md-4"/>
        <Button label="Hide optional process step" onClick="hideOptionalStep()" width="md-4"/>
      </PanelGroup>
    </PanelGroup>
</Tab>
<Tab id="wizardOptionalTab" label="Optional step">
    <PanelGroup label="Optional step">
      <OutputLabel value="This is optional step."/>
    </PanelGroup>
</Tab>
<Tab id="wizardTab2" label="Step 2">
    <PanelGroup label="Second step">
      <OutputLabel value="This is second step."/>
    </PanelGroup>
</Tab>
<Tab id="wizardTab3" label="Step 3">
    <PanelGroup label="Third step">
      <OutputLabel value="This is third step."/>
    </PanelGroup>
</Tab>
<Tab id="wizardTab4" label="Step 4">
    <PanelGroup label="Fourth step">
      <OutputLabel value="This is fourth step."/>
    </PanelGroup>
</Tab>
</Wizard>]]]]>
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