<?xml version="1.0" encoding="UTF-8"?>
<Form xmlns="http://fh.asseco.com/form/1.0" container="mainForm" id="TimerForm" label="{componentName}">
    <AvailabilityConfiguration>
        <ReadOnly>timerCodeExample</ReadOnly>
    </AvailabilityConfiguration>
    <OutputLabel width="md-12" value="{description}" id="_Form_OutputLabel"/>
    <Spacer width="md-12" height="25px" id="_Form_Spacer"/>
    <TabContainer id="_Form_TabContainer">
        <Tab label="{$.fh.docs.component.examples}" id="_Form_TabContainer_Tab1">
                <PanelGroup width="md-12" label="{$.fh.docs.component.timer_label}" id="_Form_TabContainer_Tab1_PanelGroup">
                    <InputText label="{$.fh.docs.component.timer_interval}" value="{interval}" width="sm-6" onChange="-" id="_Form_TabContainer_Tab1_PanelGroup_InputText1"/>
                    <CheckBox label="{$.fh.docs.component.timer_acitve}" value="{active}" width="sm-6" onChange="-" id="_Form_TabContainer_Tab1_PanelGroup_CheckBox"/>
                    <InputText id="timerCodeExample" label="{$.fh.docs.component.code}" width="md-12" rowsCount="4">
                        <![CDATA[
<Form>
...
    <Timer active="\{active\}" interval="\{interval\}" onTimer="timeout"/>
</Form>
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

    <Timer active="{active}" interval="{interval}" onTimer="timeout" id="_Form_Timer"/>
</Form>