<?xml version="1.0" encoding="UTF-8"?>
<Form xmlns="http://fh.asseco.com/form/1.0" container="mainForm" id="documentationHolder" label="{componentName}">
    <AvailabilityConfiguration>
        <ReadOnly>groupCollapsibleExample1,groupCollapsibleExample2,groupCollapsibleExample3,groupCollapsibleExample4,groupCollapsibleExample5,groupCollapsibleExample6,groupCollapsibleExample7,groupCollapsibleExample8,groupCollapsibleExample9,groupCollapsibleExample10,groupCollapsibleExample11</ReadOnly>
    </AvailabilityConfiguration>
    <OutputLabel width="md-12" value="{description}" id="_Form_OutputLabel"/>
    <Spacer width="md-12" height="25px" id="_Form_Spacer"/>
    <TabContainer id="_Form_TabContainer">
        <Tab label="{$.fh.docs.component.examples}" id="_Form_TabContainer_Tab1">
            <PanelGroup id="Group_group">
               <PanelGroup width="md-12" label="{$.fh.docs.component.floatinggroup_with_simple_usage}" id="_Form_TabContainer_Tab1_PanelGroup_PanelGroup1">
                    <FloatingGroup label="{$.fh.docs.component.floatinggroup_simple}" id="_Form_TabContainer_Tab1_PanelGroup_PanelGroup1_FloatingGroup">
                        <InputText width="md-12" id="groupCode1" value="{$.fh.docs.component.floatinggroup_sample_content}"/>
                    </FloatingGroup>
                    <InputText id="groupCollapsibleExample1" label="{$.fh.docs.component.code}" width="md-12" rowsCount="3">
                        <![CDATA[
<FloatingGroup label="Simple FloatingGroup">
    <InputText id="groupCode1" value="This is sample label "/>
</FloatingGroup>
                        ]]>
                    </InputText>
                </PanelGroup>


                <PanelGroup width="md-12" label="{$.fh.docs.component.floatinggroup_with_specified_size_and_height}" id="_Form_TabContainer_Tab1_PanelGroup_PanelGroup2">
                    <FloatingGroup label="{$.fh.docs.component.floatinggroup_with_fixed_size}" width="md-4" height="120" id="_Form_TabContainer_Tab1_PanelGroup_PanelGroup2_FloatingGroup">
                        <InputText width="md-12" value="Group with width='md-4' and height='120'" id="_Form_TabContainer_Tab1_PanelGroup_PanelGroup2_FloatingGroup_InputText"/>
                    </FloatingGroup>
                    <InputText id="groupCollapsibleExample2" label="{$.fh.docs.component.code}" width="md-12" rowsCount="3">
                        <![CDATA[
<FloatingGroup label="Group with fixed size" width="md-4" height="120">
    <InputText value="Group with width='md-4' and height='120'"/>
</FloatingGroup>
                        ]]>
                    </InputText>
                </PanelGroup>

                <PanelGroup width="md-12" label="{$.fh.docs.component.floatinggroup_with_bootstrap_layout_horizontal}" id="_Form_TabContainer_Tab1_PanelGroup_PanelGroup3">

                    <FloatingGroup label="{$.fh.docs.component.floatinggroup_center_panelgroup}" width="md-12" id="_Form_TabContainer_Tab1_PanelGroup_PanelGroup3_FloatingGroup1">
                        <InputText width="md-12" id="groupCollapsibleExample3" value="Group width='md-12'"/>
                    </FloatingGroup>
                    <FloatingGroup label="{$.fh.docs.component.floatinggroup_left_panelgroup}" width="md-6" id="_Form_TabContainer_Tab1_PanelGroup_PanelGroup3_FloatingGroup2">
                        <InputText width="md-12" id="groupCollapsibleExample4" value="Group width='md-6'"/>
                    </FloatingGroup>
                    <FloatingGroup label="{$.fh.docs.component.floatinggroup_right_panelgroup}" width="md-6" id="_Form_TabContainer_Tab1_PanelGroup_PanelGroup3_FloatingGroup3">
                        <InputText width="md-12" id="groupCollapsibleExample5" value="Group width='md-6'"/>
                    </FloatingGroup>

                    <InputText id="groupCollapsibleExample6" label="{$.fh.docs.component.code}" width="md-12" rowsCount="9">
                        <![CDATA[
<FloatingGroup label="Center panelGroup" width="md-12">
    <InputText id="groupCollapsibleExample3" value="Group width='md-12'"/>
</FloatingGroup>
<FloatingGroup label="Left panelGroup" width="md-6">
    <InputText id="groupCollapsibleExample4" value="Group width='md-6'"/>
</FloatingGroup>
<FloatingGroup label="Right panelGroup" width="md-6">
    <InputText id="groupCollapsibleExample5" value="Group width='md-6'"/>
</FloatingGroup>
                        ]]>
                    </InputText>
                </PanelGroup>


               <PanelGroup width="md-12" label="{$.fh.docs.component.floatinggroup_with_only_floating_mode}" id="_Form_TabContainer_Tab1_PanelGroup_PanelGroup4">
                    <FloatingGroup label="{$.fh.docs.component.floatinggroup_with_only_floating_mode}" width="md-12" floatingOnly="true" floatingState="UNPINNED_MINIMIZED" id="_Form_TabContainer_Tab1_PanelGroup_PanelGroup4_FloatingGroup">
                        <InputText width="md-12" value="{$.fh.docs.component.floatinggroup_sample_content}" id="_Form_TabContainer_Tab1_PanelGroup_PanelGroup4_FloatingGroup_InputText"/>
                    </FloatingGroup>
                    <InputText id="groupCollapsibleExample9" label="{$.fh.docs.component.code}" width="md-12" rowsCount="3">
                        <![CDATA[
<FloatingGroup label="FloatingGroup with only floating mode" width="md-12" floatingOnly="true" floatingState="UNPINNED_MINIMIZED">
    <InputText value="This is sample content of panelGroup"/>
</FloatingGroup>
                        ]]>
                    </InputText>
                </PanelGroup>

                <PanelGroup width="md-12" label="{$.fh.docs.component.floatinggroup_with_footer}" id="_Form_TabContainer_Tab1_PanelGroup_PanelGroup5">
                    <FloatingGroup label="{$.fh.docs.component.floatinggroup_with_footer}" width="xs-8,sm-4,md-12,lg-10" id="_Form_TabContainer_Tab1_PanelGroup_PanelGroup5_FloatingGroup">
                        <InputText width="md-12" value="{$.fh.docs.component.floatinggroup_sample_content}" id="_Form_TabContainer_Tab1_PanelGroup_PanelGroup5_FloatingGroup_InputText"/>
                        <Footer id="_Form_TabContainer_Tab1_PanelGroup_PanelGroup5_FloatingGroup_Footer">
                            <Button width="md-3" horizontalAlign="left" label="{$.fh.docs.component.floatinggroup_cancel}" id="_Form_TabContainer_Tab1_PanelGroup_PanelGroup5_FloatingGroup_Footer_Button1"/>
                            <Button width="md-3" horizontalAlign="right" label="{$.fh.docs.component.floatinggroup_confirm}" id="_Form_TabContainer_Tab1_PanelGroup_PanelGroup5_FloatingGroup_Footer_Button2"/>
                        </Footer>
                    </FloatingGroup>
                    <InputText id="groupCollapsibleExample7" label="{$.fh.docs.component.code}" width="md-12" rowsCount="7">
                        <![CDATA[
<FloatingGroup label="Group with footer" width="xs-8,sm-4,md-12,lg-10">
    <InputText value="This is sample content of panelGroup"/>
    <Footer>
        <Button width="md-3" horizontalAlign="left" label="Cancel"/>
        <Button width="md-3" horizontalAlign="right" label="Ok"/>
    </Footer>
</FloatingGroup>
                        ]]>
                    </InputText>
                </PanelGroup>

                <PanelGroup width="md-12" label="{$.fh.docs.component.floatinggroup_group_with_binding}" id="_Form_TabContainer_Tab1_PanelGroup_PanelGroup6">
                    <FloatingGroup label="{$.fh.docs.component.floatinggroup_this_group_is_collapsible}" collapsible="true" width="md-4" height="500px" top="200px" left="800px" floatingState="{floatingState}" onTogglePin="-" onToggleFullScreen="-" id="_Form_TabContainer_Tab1_PanelGroup_PanelGroup6_FloatingGroup">
                        <OutputLabel value="{floatingState}" width="md-12" verticalAlign="top" id="_Form_TabContainer_Tab1_PanelGroup_PanelGroup6_FloatingGroup_OutputLabel"/>
                    </FloatingGroup>
                    <InputText width="md-12" id="groupCollapsibleExample8" rowsCount="4">
                       <![CDATA[
<FloatingGroup label="This group is collapsible" collapsible="true" width="md-4" height="500px" top="200px" left="800px" floatingState="\{floatingState\}" onTogglePin="-" onToggleFullScreen="-">
    <OutputLabel value="\{floatingState\}" width="md-12 verticalAlign="top" />
</FloatingGroup>
                        ]]>
                   </InputText>
                </PanelGroup>

                <PanelGroup width="md-12" label="{$.fh.docs.component.floatinggroup_group_with_binding}" id="_Form_TabContainer_Tab1_PanelGroup_PanelGroup7">
                    <FloatingGroup label="{$.fh.docs.component.floatinggroup_this_group_is_collapsible}" collapsible="true" width="md-4" height="500px" top="200px" left="800px" floatingState="{boundFloatingState}" onTogglePin="-" onToggleFullScreen="-" id="_Form_TabContainer_Tab1_PanelGroup_PanelGroup7_FloatingGroup">
                        <OutputLabel value="{$.fh.docs.component.floatinggroup_sample_content}" width="md-12" verticalAlign="top" id="_Form_TabContainer_Tab1_PanelGroup_PanelGroup7_FloatingGroup_OutputLabel"/>
                    </FloatingGroup>
                    <Button label="PINNED_MINIMIZED" onClick="pinnedMinimized()" verticalAlign="top" id="_Form_TabContainer_Tab1_PanelGroup_PanelGroup7_Button1"/>
                    <Button label="PINNED_MAXIMIZED" onClick="pinnedMaximized()" verticalAlign="top" id="_Form_TabContainer_Tab1_PanelGroup_PanelGroup7_Button2"/>
                    <Button label="UNPINNED_MINIMIZED" onClick="unpinnedMinimized()" verticalAlign="top" id="_Form_TabContainer_Tab1_PanelGroup_PanelGroup7_Button3"/>
                    <Button label="UNPINNED_MAXIMIZED" onClick="unpinnedMaximized()" verticalAlign="top" id="_Form_TabContainer_Tab1_PanelGroup_PanelGroup7_Button4"/>
                    <InputText width="md-12" id="groupCollapsibleExample10" rowsCount="8">
                        <![CDATA[
<FloatingGroup label="This group is collapsible" collapsible="true" width="md-4" height="500px" top="200px" left="800px" floatingState="\{boundFloatingState\}" onTogglePin="-" onToggleFullScreen="-">
    <OutputLabel value="Example content" width="md-12" verticalAlign="top"/>
</FloatingGroup>
<Button label="PINNED_MINIMIZED" onClick="pinnedMinimized()" verticalAlign="top"/>
<Button label="PINNED_MAXIMIZED" onClick="pinnedMaximized()" verticalAlign="top"/>
<Button label="UNPINNED_MINIMIZED" onClick="unpinnedMinimized()" verticalAlign="top"/>
<Button label="UNPINNED_MAXIMIZED" onClick="unpinnedMaximized()" verticalAlign="top"/>
                        ]]>
                    </InputText>
                </PanelGroup>

                <PanelGroup width="md-12" label="{$.fh.docs.component.floatinggroup_group_without_drag}" id="_Form_TabContainer_Tab1_PanelGroup_PanelGroup8">
                    <Row height="200px" width="md-12">
                    <FloatingGroup label="{$.fh.docs.component.floatinggroup_this_group_is_not_draggable}" isDraggable="false" width="md-5" height="200px" top="100px" left="100px" floatingState="{notDraggableFloatingState}" onTogglePin="-" onToggleFullScreen="-" id="_Form_TabContainer_Tab1_PanelGroup_PanelGroup8_Row1_FloatingGroup">
                        <OutputLabel value="{$.fh.docs.component.floatinggroup_sample_content}" width="md-12" verticalAlign="top" id="_Form_TabContainer_Tab1_PanelGroup_PanelGroup8_Row1_FloatingGroup_OutputLabel"/>
                    </FloatingGroup>
                    </Row>
                    <Row width="md-12">
                    <Button label="PINNED_MINIMIZED" onClick="notDraggablePinnedMinimized()" verticalAlign="top" id="_Form_TabContainer_Tab1_PanelGroup_PanelGroup8_Row2_Button1"/>
                    <Button label="UNPINNED_MINIMIZED" onClick="notDraggableUnpinnedMinimized()" verticalAlign="top" id="_Form_TabContainer_Tab1_PanelGroup_PanelGroup8_Row2_Button2"/>
                    </Row>
                    <Row width="md-12">
                    <InputText width="md-12" id="groupCollapsibleExample11" rowsCount="8">
                        <![CDATA[
<FloatingGroup label="This group has draggable option disabled." isDraggable="false"  width="md-4" height="500px" top="200px" left="800px" floatingState="PINNED_MAXIMIZED" onTogglePin="-" onToggleFullScreen="-">
    <OutputLabel value=" This is sample label" width="md-12" verticalAlign="top"/>
</FloatingGroup>
<Button label="PINNED_MINIMIZED" onClick="notDraggablePinnedMinimized()" verticalAlign="top"/>
<Button label="UNPINNED_MINIMIZED" onClick="notDraggableUnpinnedMinimized()" verticalAlign="top"/>
                        ]]>
                    </InputText>
                    </Row>
                </PanelGroup>

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