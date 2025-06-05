<?xml version="1.0" encoding="UTF-8"?>
<Form xmlns="http://fh.asseco.com/form/1.0" container="mainForm" id="documentationHolder" label="{componentName}">
    <AvailabilityConfiguration>
        <ReadOnly>accordionExampleCode1,accordionExampleCode2,accordionExampleCode3</ReadOnly>
    </AvailabilityConfiguration>
    <OutputLabel width="md-12" value="{description}" id="_Form_OutputLabel"/>
    <Spacer width="md-12" height="25px" id="_Form_Spacer"/>
    <TabContainer id="_Form_TabContainer">
        <Tab label="{$.fh.docs.component.examples}" id="_Form_TabContainer_Tab1">
                <PanelGroup width="md-12" label="{$.fh.docs.component.panelgroupwrapper.simple}" id="_Form_TabContainer_Tab1_PanelGroup1">
                    <Row width="md-12">
                        <Button label="Zamknij/Otwórz wszystkie" onClick="toggle"/>
                    </Row>

                    <PanelGroupWrapper toggleAll="{toggleAll}" width="md-12" iconOpened="fa-chevron-down" iconClosed="fa-chevron-up" id="_Form_TabContainer_Tab1_PanelGroup1_Wrapper">
                        <PanelGroup label="{$.fh.docs.component.panelgroupwrapper_group_one}" width="md-12" id="_Form_TabContainer_Tab1_PanelGroup1_Accordion_PanelGroup1">
                            <InputText width="md-12" value="{$.fh.docs.component.panelgroupwrapper_group_one}" id="_Form_TabContainer_Tab1_PanelGroup1_Accordion_PanelGroup1_InputText"/>
                        </PanelGroup>

                        <PanelGroup label="{$.fh.docs.component.panelgroupwrapper_group_two}" width="md-12" id="_Form_TabContainer_Tab1_PanelGroup1_Accordion_PanelGroup2">
                            <InputText width="md-12" value="{$.fh.docs.component.panelgroupwrapper_group_two}" id="_Form_TabContainer_Tab1_PanelGroup1_Accordion_PanelGroup2_InputText"/>
                        </PanelGroup>

                        <PanelGroup label="{$.fh.docs.component.panelgroupwrapper_group_three}" width="md-12" id="_Form_TabContainer_Tab1_PanelGroup1_Accordion_PanelGroup3">
                            <InputText width="md-12" value="{$.fh.docs.component.panelgroupwrapper_group_three}" id="_Form_TabContainer_Tab1_PanelGroup1_Accordion_PanelGroup3_InputText"/>
                        </PanelGroup>

                    </PanelGroupWrapper>
                    <InputText id="accordionExampleCode1" label="{$.fh.docs.component.code}" width="md-12" rowsCount="11">
                        <![CDATA[
<PanelGroupWrapper toggleAll="{toggleAll}" width="md-12" iconOpened="fa-chevron-down" iconClosed="fa-chevron-up">
    <PanelGroup label="Group one" width="md-12">
        <InputText value="Group one"/>
    </PanelGroup>

    <PanelGroup label="Group two" width="md-12">
        <InputText value="Group two"/>
    </PanelGroup>

    <PanelGroup label="Group three" width="md-12">
        <InputText value="Group three"/>
    </PanelGroup>
</PanelGroupWrapper>
                        ]]>
                    </InputText>
                </PanelGroup>

<!--                <PanelGroup width="md-12" label="{$.fh.docs.component.panelgroupwrapper_with_activegroup_attribute}" id="_Form_TabContainer_Tab1_PanelGroup2">-->
<!--                    <Accordion width="md-12" activeGroup="2" id="_Form_TabContainer_Tab1_PanelGroup2_Accordion">-->
<!--                        <PanelGroup label="{$.fh.docs.component.panelgroupwrapper_group_one}" width="md-12" id="_Form_TabContainer_Tab1_PanelGroup2_Accordion_PanelGroup1">-->
<!--                            <InputText width="md-12" value="{$.fh.docs.component.panelgroupwrapper_group_one}" id="_Form_TabContainer_Tab1_PanelGroup2_Accordion_PanelGroup1_InputText"/>-->
<!--                        </PanelGroup>-->

<!--                        <PanelGroup label="{$.fh.docs.component.panelgroupwrapper_group_two}" width="md-12" id="_Form_TabContainer_Tab1_PanelGroup2_Accordion_PanelGroup2">-->
<!--                            <InputText width="md-12" value="{$.fh.docs.component.panelgroupwrapper_group_two}" id="_Form_TabContainer_Tab1_PanelGroup2_Accordion_PanelGroup2_InputText"/>-->
<!--                        </PanelGroup>-->

<!--                        <PanelGroup label="{$.fh.docs.component.panelgroupwrapper_group_three}" width="md-12" id="_Form_TabContainer_Tab1_PanelGroup2_Accordion_PanelGroup3">-->
<!--                            <InputText width="md-12" value="{$.fh.docs.component.panelgroupwrapper_group_three}" id="_Form_TabContainer_Tab1_PanelGroup2_Accordion_PanelGroup3_InputText"/>-->
<!--                        </PanelGroup>-->
<!--                    </Accordion>-->
<!--                    <InputText id="accordionExampleCode2" label="{$.fh.docs.component.code}" width="md-12" rowsCount="13">-->
<!--                         <![CDATA[-->
<!--    <Accordion width="md-12" activeGroup="2">-->
<!--        <PanelGroup label="Group one" width="md-12">-->
<!--            <InputText value="Group one"/>-->
<!--        </PanelGroup>-->

<!--        <PanelGroup label="Group two" width="md-12">-->
<!--            <InputText value="Group two"/>-->
<!--        </PanelGroup>-->

<!--        <PanelGroup label="Group three" width="md-12">-->
<!--            <InputText value="Group three"/>-->
<!--        </PanelGroup>-->
<!--    </Accordion>-->
<!--                        ]]>-->
<!--                    </InputText>-->
<!--                </PanelGroup>-->

<!--                <PanelGroup width="md-12" label="{$.fh.docs.component.panelgroupwrapper_with_bound_activegroup_attribute}" id="_Form_TabContainer_Tab1_PanelGroup3">-->
<!--                    <Accordion width="md-12" activeGroup="{activeGroup}" onGroupChange="-" id="_Form_TabContainer_Tab1_PanelGroup3_Accordion1">-->
<!--                        <PanelGroup label="{$.fh.docs.component.panelgroupwrapper_group_one}" width="md-12" id="_Form_TabContainer_Tab1_PanelGroup3_Accordion1_PanelGroup1">-->
<!--                            <InputText width="md-12" value="{$.fh.docs.component.panelgroupwrapper_group_one}" id="_Form_TabContainer_Tab1_PanelGroup3_Accordion1_PanelGroup1_InputText"/>-->
<!--                        </PanelGroup>-->

<!--                        <PanelGroup label="{$.fh.docs.component.panelgroupwrapper_group_two}" width="md-12" id="_Form_TabContainer_Tab1_PanelGroup3_Accordion1_PanelGroup2">-->
<!--                            <InputText width="md-12" value="{$.fh.docs.component.panelgroupwrapper_group_two}" id="_Form_TabContainer_Tab1_PanelGroup3_Accordion1_PanelGroup2_InputText"/>-->
<!--                        </PanelGroup>-->

<!--                        <PanelGroup label="{$.fh.docs.component.panelgroupwrapper_group_three}" width="md-12" id="_Form_TabContainer_Tab1_PanelGroup3_Accordion1_PanelGroup3">-->
<!--                            <InputText width="md-12" value="{$.fh.docs.component.panelgroupwrapper_group_three}" id="_Form_TabContainer_Tab1_PanelGroup3_Accordion1_PanelGroup3_InputText"/>-->
<!--                        </PanelGroup>-->
<!--                    </Accordion>-->

<!--                    <Accordion width="md-12" activeGroup="{activeGroup}" onGroupChange="-" id="_Form_TabContainer_Tab1_PanelGroup3_Accordion2">-->
<!--                        <PanelGroup label="{$.fh.docs.component.panelgroupwrapper_group_one}" width="md-12" id="_Form_TabContainer_Tab1_PanelGroup3_Accordion2_PanelGroup1">-->
<!--                            <InputText width="md-12" value="{$.fh.docs.component.panelgroupwrapper_group_one}" id="_Form_TabContainer_Tab1_PanelGroup3_Accordion2_PanelGroup1_InputText"/>-->
<!--                        </PanelGroup>-->

<!--                        <PanelGroup label="{$.fh.docs.component.panelgroupwrapper_group_two}" width="md-12" id="_Form_TabContainer_Tab1_PanelGroup3_Accordion2_PanelGroup2">-->
<!--                            <InputText width="md-12" value="{$.fh.docs.component.panelgroupwrapper_group_two}" id="_Form_TabContainer_Tab1_PanelGroup3_Accordion2_PanelGroup2_InputText"/>-->
<!--                        </PanelGroup>-->

<!--                        <PanelGroup label="{$.fh.docs.component.panelgroupwrapper_group_three}" width="md-12" id="_Form_TabContainer_Tab1_PanelGroup3_Accordion2_PanelGroup3">-->
<!--                            <InputText width="md-12" value="{$.fh.docs.component.panelgroupwrapper_group_three}" id="_Form_TabContainer_Tab1_PanelGroup3_Accordion2_PanelGroup3_InputText"/>-->
<!--                        </PanelGroup>-->
<!--                    </Accordion>-->
<!--                    <OutputLabel width="md-12" value="{$.fh.docs.component.panelgroupwrapper_active_group_index}: {activeGroup}" id="_Form_TabContainer_Tab1_PanelGroup3_OutputLabel"/>-->
<!--                    <InputText id="accordionExampleCode3" label="{$.fh.docs.component.code}" width="md-12" rowsCount="25">-->
<!--                        <![CDATA[-->
<!--                <Accordion width="md-12" activeGroup="{activeGroup}" onGroupChange="-">-->
<!--                    <PanelGroup label="Group one" width="md-12">-->
<!--                        <InputText value="Group one"/>-->
<!--                    </PanelGroup>-->

<!--                    <PanelGroup label="Group two" width="md-12">-->
<!--                        <InputText value="Group two"/>-->
<!--                    </PanelGroup>-->

<!--                    <PanelGroup label="Group three" width="md-12">-->
<!--                        <InputText value="Group three"/>-->
<!--                    </PanelGroup>-->
<!--                </Accordion>-->

<!--                <Accordion width="md-12" activeGroup="{activeGroup}" onGroupChange="-">-->
<!--                    <PanelGroup label="Group one" width="md-12">-->
<!--                        <InputText value="Group one"/>-->
<!--                    </PanelGroup>-->

<!--                    <PanelGroup label="Group two" width="md-12">-->
<!--                        <InputText value="Group two"/>-->
<!--                    </PanelGroup>-->

<!--                    <PanelGroup label="Group three" width="md-12">-->
<!--                        <InputText value="Group three"/>-->
<!--                    </PanelGroup>-->
<!--                </Accordion>-->
<!--                <OutputLabel value="Active group index is: \{activeGroup\}" />-->
<!--                        ]]>-->
<!--                    </InputText>-->
<!--                </PanelGroup>-->
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
