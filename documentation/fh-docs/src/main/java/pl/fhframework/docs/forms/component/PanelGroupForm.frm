<?xml version="1.0" encoding="UTF-8"?>
<Form xmlns="http://fh.asseco.com/form/1.0" container="mainForm" id="documentationHolder" label="{componentName}">
    <AvailabilityConfiguration>
        <ReadOnly>groupExampleCode1,groupExampleCode2,groupExampleCode3,groupExampleCode4,groupCollapsibleExample1,groupCollapsibleExample2,groupExampleCode7_1,groupExampleCode8,groupExampleCode9,groupExampleCode10</ReadOnly>
    </AvailabilityConfiguration>
    <OutputLabel width="md-12" value="{description}" id="_Form_OutputLabel"/>
    <Spacer width="md-12" height="25px" id="_Form_Spacer"/>
    <TabContainer id="_Form_TabContainer">
        <Tab label="{$.fh.docs.component.examples}" id="_Form_TabContainer_Tab1">
                <PanelGroup width="md-12" label="{$.fh.docs.component.panelgroup_group_with_simple_usage}" id="_Form_TabContainer_Tab1_PanelGroup1">
                    <PanelGroup label="{$.fh.docs.component.panelgroup_simple_panelgroup}" id="_Form_TabContainer_Tab1_PanelGroup1_PanelGroup1">
                        <InputText width="md-12" id="groupCode1" value="{$.fh.docs.component.panelgroup_this_is_sample_label} "/>
                    </PanelGroup>
                    <InputText id="groupExampleCode1" label="{$.fh.docs.component.code}" width="md-12" rowsCount="3">
                        <![CDATA[![ESCAPE[
<PanelGroup label="Simple panelGroup">
    <InputText id="groupCode1" value="This is sample label "/>
</PanelGroup>
                        ]]]]>
                    </InputText>

                    <PanelGroup label="{$.fh.docs.component.panelgroup_simple_panelgroupb}" borderVisible="true" id="_Form_TabContainer_Tab1_PanelGroup1_PanelGroup2">
                        <InputText width="md-12" id="groupCode3" value="{$.fh.docs.component.panelgroup_this_is_sample_label} "/>
                    </PanelGroup>
                    <InputText id="groupExampleCode3" label="{$.fh.docs.component.code}" width="md-12" rowsCount="3">
                        <![CDATA[![ESCAPE[
<PanelGroup label="Simple panelGroup" borderVisible="true">
    <InputText id="groupCode1" value="This is sample label "/>
</PanelGroup>
                        ]]]]>
                    </InputText>
                </PanelGroup>

                <PanelGroup width="md-12" label="{$.fh.docs.component.panelgroup_group_with_specified_size_and_height}" id="_Form_TabContainer_Tab1_PanelGroup2">
                    <PanelGroup label="{$.fh.docs.component.panelgroup_group_with_fixed_size}" width="md-4" height="100" id="_Form_TabContainer_Tab1_PanelGroup2_PanelGroup">
                        <InputText width="md-12" id="groupCode2" value="{$.fh.docs.component.panelgroup_group_with} width='md-4' {$.fh.docs.component.panelgroup_and} height='100'"/>
                    </PanelGroup>
                    <InputText id="groupExampleCode2" label="{$.fh.docs.component.code}" width="md-12" rowsCount="3">
                        <![CDATA[![ESCAPE[
<PanelGroup label="Group with fixed size" width="md-4" height="100">
    <InputText id="groupCode2" value="Group with width='md-4' and height='100'"/>
</PanelGroup>
                        ]]]]>
                    </InputText>
                </PanelGroup>

                <PanelGroup width="md-12" label="{$.fh.docs.component.panelgroup_group_with_bootstrap_layout_horizontal}" id="_Form_TabContainer_Tab1_PanelGroup3">

                    <PanelGroup label="{$.fh.docs.component.panelgroup_center_panelgroup}" width="md-12" id="_Form_TabContainer_Tab1_PanelGroup3_PanelGroup1">
                        <InputText width="md-12" id="groupCode4_0" value="PanelGroup width='md-12'"/>
                    </PanelGroup>

                    <PanelGroup label="{$.fh.docs.component.panelgroup_left_panelgroup}" width="md-6" id="_Form_TabContainer_Tab1_PanelGroup3_PanelGroup2">
                        <InputText width="md-12" id="groupCode4_1" value="PanelGroup width='md-6'"/>
                    </PanelGroup>
                    <PanelGroup label="{$.fh.docs.component.panelgroup_right_panelgroup}" width="md-6" id="_Form_TabContainer_Tab1_PanelGroup3_PanelGroup3">
                        <InputText width="md-12" id="groupCode4_2" value="PanelGroup width='md-6'"/>
                    </PanelGroup>
                    <InputText id="groupExampleCode4" label="{$.fh.docs.component.code}" width="md-12" rowsCount="3">
                        <![CDATA[![ESCAPE[
<PanelGroup label="Center panelGroup" width="md-12">
    <InputText id="groupCode4_0" value="PanelGroup width='md-12'"/>
</PanelGroup>

<PanelGroup label="Left panelGroup" width="md-6">
    <InputText id="groupCode4_1" value="PanelGroup width='md-6'"/>
</PanelGroup>
<PanelGroup label="Right panelGroup" width="md-6">
    <InputText id="groupCode4_2" value="PanelGroup width='md-6'"/>
</PanelGroup>
                        ]]]]>
                    </InputText>
                </PanelGroup>

                <PanelGroup width="md-12" label="{$.fh.docs.component.panelgroup_collapsible_panelgroup}" id="_Form_TabContainer_Tab1_PanelGroup4">
                    <PanelGroup label="{$.fh.docs.component.panelgroup_this_panelgroup_is_collapsible}" collapsible="true" id="_Form_TabContainer_Tab1_PanelGroup4_PanelGroup">
                        <InputText width="md-12" value="{$.fh.docs.component.panelgroup_label}" id="_Form_TabContainer_Tab1_PanelGroup4_PanelGroup_InputText"/>
                    </PanelGroup>
                    <InputText width="md-12" id="groupCollapsibleExample1" rowsCount="3">
                        <![CDATA[![ESCAPE[
<PanelGroup label="This panelGroup is collapsible" collapsible="true" >
    <InputText value="Label" />
</PanelGroup>
                        ]]]]>
                    </InputText>
                </PanelGroup>

                <PanelGroup width="md-12" label="{$.fh.docs.component.panelgroup_collapsible_panelgroup_collapsed_by_default}" id="_Form_TabContainer_Tab1_PanelGroup5">
                    <PanelGroup label="{$.fh.docs.component.panelgroup_this_panelgroup_is_collapsible_with_ontoggle_event_and_collapsed}" collapsible="true" onToggle="-" collapsed="{state}" id="_Form_TabContainer_Tab1_PanelGroup5_PanelGroup">
                        <InputText width="md-12" value="{$.fh.docs.component.panelgroup_label}" id="_Form_TabContainer_Tab1_PanelGroup5_PanelGroup_InputText"/>
                    </PanelGroup>
                    <OutputLabel width="md-12" value="{$.fh.docs.component.panelgroup_this_panelgroup_is_collapsed} {state}" id="_Form_TabContainer_Tab1_PanelGroup5_OutputLabel"/>
                    <InputText width="md-12" id="groupCollapsibleExample2" rowsCount="3">
                        <![CDATA[![ESCAPE[
<PanelGroup label="This panelGroup is collapsible (with onToggle event) and collapsed" collapsible="true" onToggle="-" collapsed="{state}">
    <InputText value="Label" />
</PanelGroup>
<OutputLabel value="This panelGroup is collapsed: {state}" />
                        ]]]]>
                    </InputText>
                </PanelGroup>

                <PanelGroup width="md-12" label="{$.fh.docs.component.panelgroup_group_with_multi_size}" id="_Form_TabContainer_Tab1_PanelGroup6">
                    <PanelGroup label="{$.fh.docs.component.panelgroup_simple_panelgroup_with_multi_sizes}" width="xs-8,sm-4,md-12,lg-10" id="_Form_TabContainer_Tab1_PanelGroup6_PanelGroup">
                        <InputText width="md-12" id="groupCode7_1" value="{$.fh.docs.component.panelgroup_this_is_sample_label}"/>
                    </PanelGroup>
                    <InputText id="groupExampleCode7_1" label="{$.fh.docs.component.code}" width="md-12" rowsCount="3">
                        <![CDATA[![ESCAPE[
<PanelGroup label="Simple panelGroup with multi sizes" width="xs-8,sm-4,md-12,lg-10">
    <InputText id="groupCode7_1" value="This is sample label"/>
</PanelGroup>
                        ]]]]>
                    </InputText>
                </PanelGroup>

                <PanelGroup width="md-12" label="{$.fh.docs.component.panelgroup_group_with_footer}" id="_Form_TabContainer_Tab1_PanelGroup7">
                    <PanelGroup label="{$.fh.docs.component.panelgroup_group_with_footer}" width="md-12" id="_Form_TabContainer_Tab1_PanelGroup7_PanelGroup">
                        <InputText width="md-12" value="{$.fh.docs.component.panelgroup_this_is_sample_content_of_panelgroup}" id="_Form_TabContainer_Tab1_PanelGroup7_PanelGroup_InputText"/>
                        <Footer id="_Form_TabContainer_Tab1_PanelGroup7_PanelGroup_Footer">
                            <Button width="md-4" horizontalAlign="left" label="{$.fh.docs.component.panelgroup_cancel}" id="_Form_TabContainer_Tab1_PanelGroup7_PanelGroup_Footer_Button1"/>
                            <Button width="md-4" horizontalAlign="right" label="{$.fh.docs.component.panelgroup_ok}" id="_Form_TabContainer_Tab1_PanelGroup7_PanelGroup_Footer_Button2"/>
                        </Footer>
                    </PanelGroup>
                    <InputText id="groupExampleCode8" label="{$.fh.docs.component.code}" width="md-12" rowsCount="7">
                        <![CDATA[![ESCAPE[
<PanelGroup label="Group with footer" width="md-12">
  <InputText value="This is sample content of panelGroup"/>
  <Footer>
    <Button width="md-3" horizontalAlign="left" label="Cancel"/>
    <Button width="md-3" horizontalAlign="right" label="Ok"/>
  </Footer>
</PanelGroup>
                        ]]]]>
                    </InputText>
                </PanelGroup>
        </Tab>

        <Tab label="{$.fh.docs.component.panelgroup_group_with_headers}" id="_Form_TabContainer_Tab2">
            <Spacer width="md-12" height="50px" id="_Form_TabContainer_Tab2_Spacer1"/>
            <OutputLabel width="md-12" value="{$.fh.docs.component.panelgroup_group_with_headers.description}" id="_Form_TabContainer_Tab2_OutputLabel"/>
            <Spacer width="md-12" height="50px" id="_Form_TabContainer_Tab2_Spacer2"/>
            <PanelGroup headingType="Auto" width="md-12" label="{$.fh.docs.component.panelgroup_group_with_auto_heading}: H2" collapsible="true" borderVisible="true" id="_Form_TabContainer_Tab2_PanelGroup1">
                <PanelGroup headingType="Auto" width="md-12" label="{$.fh.docs.component.panelgroup_group_with_auto_heading}: H3" collapsible="true" borderVisible="true" id="_Form_TabContainer_Tab2_PanelGroup1_PanelGroup">
                    <PanelGroup headingType="Auto" width="md-12" label="{$.fh.docs.component.panelgroup_group_with_auto_heading}: H4" borderVisible="true" id="_Form_TabContainer_Tab2_PanelGroup1_PanelGroup_PanelGroup">
                        <PanelGroup headingType="Auto" width="md-12" label="{$.fh.docs.component.panelgroup_group_with_auto_heading}: H5" borderVisible="true" id="_Form_TabContainer_Tab2_PanelGroup1_PanelGroup_PanelGroup_PanelGroup">
                            <PanelGroup headingType="Auto" width="md-12" label="{$.fh.docs.component.panelgroup_group_with_auto_heading}: H6" borderVisible="true" id="_Form_TabContainer_Tab2_PanelGroup1_PanelGroup_PanelGroup_PanelGroup_PanelGroup">
                                <PanelGroup headingType="Auto" width="md-12" label="{$.fh.docs.component.panelgroup_group_with_auto_heading}: H6" borderVisible="true" id="_Form_TabContainer_Tab2_PanelGroup1_PanelGroup_PanelGroup_PanelGroup_PanelGroup_PanelGroup">
                                    <PanelGroup headingType="Auto" width="md-12" label="{$.fh.docs.component.panelgroup_group_with_auto_heading}: H6" borderVisible="true" id="_Form_TabContainer_Tab2_PanelGroup1_PanelGroup_PanelGroup_PanelGroup_PanelGroup_PanelGroup_PanelGroup">

                                    </PanelGroup>
                                </PanelGroup>
                            </PanelGroup>
                        </PanelGroup>
                    </PanelGroup>
                </PanelGroup>
            </PanelGroup>
            <InputText id="groupExampleCode9" label="{$.fh.docs.component.code}" width="md-12" rowsCount="16">
                <![CDATA[![ESCAPE[
<PanelGroup headingType="Auto" width="md-12" label="{$.fh.docs.component.panelgroup_group_with_auto_heading}: H2" collapsible="true" >
    <PanelGroup headingType="Auto" width="md-12" label="{$.fh.docs.component.panelgroup_group_with_auto_heading}: H3" collapsible="true" >
        <PanelGroup headingType="Auto" width="md-12" label="{$.fh.docs.component.panelgroup_group_with_auto_heading}: H4" >
            <PanelGroup headingType="Auto" width="md-12" label="{$.fh.docs.component.panelgroup_group_with_auto_heading}: H5"  >
                <PanelGroup headingType="Auto" width="md-12" label="{$.fh.docs.component.panelgroup_group_with_auto_heading}: H6"  >
                    <PanelGroup headingType="Auto" width="md-12" label="{$.fh.docs.component.panelgroup_group_with_auto_heading}: H6" >
                        <PanelGroup headingType="Auto" width="md-12" label="{$.fh.docs.component.panelgroup_group_with_auto_heading}: H6" >

                        </PanelGroup>
                    </PanelGroup>
                </PanelGroup>
            </PanelGroup>
        </PanelGroup>
    </PanelGroup>
</PanelGroup>
                        ]]]]>
            </InputText>

            <Spacer width="md-12" height="50px" id="_Form_TabContainer_Tab2_Spacer3"/>
            <PanelGroup headingType="H4" width="md-12" label="{$.fh.docs.component.panelgroup_group_with_manual_heading}: H4" collapsible="true" borderVisible="true" id="_Form_TabContainer_Tab2_PanelGroup2">
                <PanelGroup headingType="H3" width="md-12" label="{$.fh.docs.component.panelgroup_group_with_manual_heading}: H3" collapsible="true" borderVisible="true" id="_Form_TabContainer_Tab2_PanelGroup2_PanelGroup">
                    <PanelGroup headingType="H2" width="md-12" label="{$.fh.docs.component.panelgroup_group_with_manual_heading}: H2" borderVisible="true" id="_Form_TabContainer_Tab2_PanelGroup2_PanelGroup_PanelGroup">
                        <PanelGroup headingType="H1" width="md-12" label="{$.fh.docs.component.panelgroup_group_with_manual_heading}: H1" borderVisible="true" id="_Form_TabContainer_Tab2_PanelGroup2_PanelGroup_PanelGroup_PanelGroup">
                        </PanelGroup>
                    </PanelGroup>
                </PanelGroup>
            </PanelGroup>
            <InputText id="groupExampleCode10" label="{$.fh.docs.component.code}" width="md-12" rowsCount="8">
                <![CDATA[![ESCAPE[
<PanelGroup headingType="H4" width="md-12" label="{$.fh.docs.component.panelgroup_group_with_manual_heading}: H4" collapsible="true" >
    <PanelGroup headingType="H3" width="md-12" label="{$.fh.docs.component.panelgroup_group_with_manual_heading}: H3" collapsible="true" >
        <PanelGroup headingType="H2" width="md-12" label="{$.fh.docs.component.panelgroup_group_with_manual_heading}: H2" >
            <PanelGroup headingType="H1" width="md-12" label="{$.fh.docs.component.panelgroup_group_with_manual_heading}: H1"  >
            </PanelGroup>
        </PanelGroup>
    </PanelGroup>
</PanelGroup>
                        ]]]]>
            </InputText>

        </Tab>
        <Tab label="{$.fh.docs.component.attributes}" id="_Form_TabContainer_Tab3">
            <Table iterator="item" collection="{attributes}" id="_Form_TabContainer_Tab3_Table">
                <Column label="{$.fh.docs.component.attributes_identifier}" value="{item.name}" width="15" id="_Form_TabContainer_Tab3_Table_Column1"/>
                <Column label="{$.fh.docs.component.attributes_type}" value="{item.type}" width="15" id="_Form_TabContainer_Tab3_Table_Column2"/>
                <Column label="{$.fh.docs.component.attributes_boundable}" value="{item.boundable}" width="10" id="_Form_TabContainer_Tab3_Table_Column3"/>
                <Column label="{$.fh.docs.component.attributes_default_value}" value="{item.defaultValue}" width="20" id="_Form_TabContainer_Tab3_Table_Column4"/>
                <Column label="{$.fh.docs.component.attributes_description}" value="{item.description}" width="40" id="_Form_TabContainer_Tab3_Table_Column5"/>
            </Table>
        </Tab>
    </TabContainer>
    <Button id="pBack" label="[icon='fa fa-chevron-left'] {$.fh.docs.component.back}" onClick="backToFormComponentsList()"/>

</Form>