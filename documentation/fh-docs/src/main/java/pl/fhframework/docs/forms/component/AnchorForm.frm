<?xml version="1.0" encoding="UTF-8"?>
<Form xmlns="http://fh.asseco.com/form/1.0" container="mainForm" id="documentationHolder" label="{componentName}">
    <AvailabilityConfiguration>
        <ReadOnly>code1,code2,code3</ReadOnly>
    </AvailabilityConfiguration>
    <OutputLabel width="md-12" value="{description}" id="_Form_OutputLabel"/>
    <Spacer width="md-12" height="25px" id="_Form_Spacer"/>
    <TabContainer id="_Form_TabContainer">
        <Tab label="{$.fh.docs.component.examples}" id="_Form_TabContainer_Tab1">

            <TabContainer id="_Form_TabContainer_Tab1_TabContainer">
                <Tab label="{$.fh.docs.component.anchor.automatic}" id="_Form_TabContainer_Tab1_TabContainer_Tab1">
                    <Spacer width="md-12" height="25" id="_Form_TabContainer_Tab1_TabContainer_Tab1_Spacer1"/>
                    <OutputLabel verticalAlign="middle" width="md-12" id="_Form_TabContainer_Tab1_TabContainer_Tab1_OutputLabel1">
                        Ta strona zawsze przewinie się do kotwicy umieszczonej na dole.
                    </OutputLabel>
                    <PanelGroup horizontalAlign="center" height="1500" borderVisible="true" styleClasses="border-primary" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup">
                        <OutputLabel verticalAlign="middle" width="md-12" value="{$.fh.docs.component.anchor.fill_container}" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup_OutputLabel"/>
                    </PanelGroup>
                    <Spacer width="md-12" height="50" id="_Form_TabContainer_Tab1_TabContainer_Tab1_Spacer2"/>
                    <OutputLabel width="md-12" value="[icon='fa fa-anchor'] Here is invisiable point." id="_Form_TabContainer_Tab1_TabContainer_Tab1_OutputLabel2"/>
                    <Anchor animateDuration="{duration}" scrollOnStart="true" id="_Form_TabContainer_Tab1_TabContainer_Tab1_Anchor"/>
                    <InputText id="code1" width="md-12" label="{$.fh.docs.component.code}" rowsCount="2">
                        <![CDATA[![ESCAPE[
<Anchor animateDuration="2000" scrollOnStart="true" />
                        ]]]]>
                    </InputText>
                    <Spacer width="md-12" height="50" id="_Form_TabContainer_Tab1_TabContainer_Tab1_Spacer3"/>

                </Tab>
                <Tab label="{$.fh.docs.component.anchor.programatic}" id="_Form_TabContainer_Tab1_TabContainer_Tab2">
                    <Spacer width="md-12" height="25" id="_Form_TabContainer_Tab1_TabContainer_Tab2_Spacer1"/>
                    <OutputLabel verticalAlign="middle" width="md-12" value="{$.fh.docs.component.anchor.programatic.scrol_down}" id="_Form_TabContainer_Tab1_TabContainer_Tab2_OutputLabel1"/>
                    <Button label="[icon='fa fa-arrow-down'] Scroll" styleClasses="btn-xs btn-outline" onClick="scrollDown()" id="_Form_TabContainer_Tab1_TabContainer_Tab2_Button"/>
                    <PanelGroup horizontalAlign="center" height="1500" borderVisible="true" styleClasses="border-primary" id="_Form_TabContainer_Tab1_TabContainer_Tab2_PanelGroup">
                        <OutputLabel verticalAlign="middle" width="md-12" value="{$.fh.docs.component.anchor.fill_container}" id="_Form_TabContainer_Tab1_TabContainer_Tab2_PanelGroup_OutputLabel"/>
                    </PanelGroup>
                    <Spacer width="md-12" height="50" id="_Form_TabContainer_Tab1_TabContainer_Tab2_Spacer2"/>
                    <OutputLabel width="md-12" value="[icon='fa fa-anchor'] Here is invisible point." id="_Form_TabContainer_Tab1_TabContainer_Tab2_OutputLabel2"/>
                    <Anchor scrollOnStart="false" animateDuration="1500" scroll="{scrollRightNow}" id="_Form_TabContainer_Tab1_TabContainer_Tab2_Anchor"/>
                    <InputText id="code2" width="md-12" label="{$.fh.docs.component.code}" rowsCount="2">
                        <![CDATA[![ESCAPE[
 <Anchor scrollOnStart="false" animateDuration="1500" scroll="{scrollRightNow}" />
                        ]]]]>
                    </InputText>
                    <Spacer width="md-12" height="50" id="_Form_TabContainer_Tab1_TabContainer_Tab2_Spacer3"/>
                </Tab>
                <Tab label="{$.fh.docs.component.anchor.programatic_inside}" id="_Form_TabContainer_Tab1_TabContainer_Tab3">
                    <Spacer width="md-12" height="50" id="_Form_TabContainer_Tab1_TabContainer_Tab3_Spacer1"/>
                    <Group width="md-6" id="_Form_TabContainer_Tab1_TabContainer_Tab3_Group1">
                        <OutputLabel verticalAlign="middle" width="md-12" value="{$.fh.docs.component.anchor.programatic.scrol_down_inside}" id="_Form_TabContainer_Tab1_TabContainer_Tab3_Group1_OutputLabel"/>
                        <Button label="[icon='fa fa-arrow-down'] Scroll to Anchor 1" width="md-4" onClick="scrollDownInside()" id="_Form_TabContainer_Tab1_TabContainer_Tab3_Group1_Button1"/>
                        <Button label="[icon='fa fa-arrow-down'] Scroll to Anchor 2" width="md-4" onClick="scrollDownInside2()" id="_Form_TabContainer_Tab1_TabContainer_Tab3_Group1_Button2"/>
                        <Button label="[icon='fa fa-arrow-down'] Scroll to Anchor 3" width="md-4" onClick="scrollDownInside3()" id="_Form_TabContainer_Tab1_TabContainer_Tab3_Group1_Button3"/>
                    </Group>
                    <Group width="md-6" styleClasses="border-left" id="_Form_TabContainer_Tab1_TabContainer_Tab3_Group2">
                        <PanelGroup horizontalAlign="center" height="400" borderVisible="true" styleClasses="border-primary" id="_Form_TabContainer_Tab1_TabContainer_Tab3_Group2_PanelGroup">
                            <Anchor scrollOnStart="false" animateDuration="1500" scroll="{scrollRightNowInside}" id="_Form_TabContainer_Tab1_TabContainer_Tab3_Group2_PanelGroup_Anchor1"/>
                            <OutputLabel width="md-12" value="[icon='fa fa-anchor'] Here is invisible point 1." id="_Form_TabContainer_Tab1_TabContainer_Tab3_Group2_PanelGroup_OutputLabel1"/>
                            <Spacer width="md-12" height="500" id="_Form_TabContainer_Tab1_TabContainer_Tab3_Group2_PanelGroup_Spacer1"/>
                            <Anchor scrollOnStart="false" animateDuration="1500" scroll="{scrollRightNowInside2}" id="_Form_TabContainer_Tab1_TabContainer_Tab3_Group2_PanelGroup_Anchor2"/>
                            <OutputLabel width="md-12" value="[icon='fa fa-anchor'] Here is invisible point 2." id="_Form_TabContainer_Tab1_TabContainer_Tab3_Group2_PanelGroup_OutputLabel2"/>
                            <Spacer width="md-12" height="500" id="_Form_TabContainer_Tab1_TabContainer_Tab3_Group2_PanelGroup_Spacer2"/>
                            <Anchor scrollOnStart="false" animateDuration="1500" scroll="{scrollRightNowInside3}" id="_Form_TabContainer_Tab1_TabContainer_Tab3_Group2_PanelGroup_Anchor3"/>
                            <OutputLabel width="md-12" value="[icon='fa fa-anchor'] Here is invisible point 3." id="_Form_TabContainer_Tab1_TabContainer_Tab3_Group2_PanelGroup_OutputLabel3"/>
                        </PanelGroup>
                        <InputText id="code3" width="md-12" label="{$.fh.docs.component.code}" rowsCount="6">
                            <![CDATA[![ESCAPE[
<Anchor scrollOnStart="false" animateDuration="1500" scroll="{scrollRightNowInside}" />
<Anchor scrollOnStart="false" animateDuration="1500" scroll="{scrollRightNowInside2}" />
<Anchor scrollOnStart="false" animateDuration="1500" scroll="{scrollRightNowInside3}" />
                        ]]]]>
                        </InputText>
                    </Group>
                    <Spacer width="md-12" height="50" id="_Form_TabContainer_Tab1_TabContainer_Tab3_Spacer2"/>
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