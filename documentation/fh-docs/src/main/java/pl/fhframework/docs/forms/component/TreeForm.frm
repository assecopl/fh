<?xml version="1.0" encoding="UTF-8"?>
<Form xmlns="http://fh.asseco.com/form/1.0" container="mainForm" id="documentationHolder" label="{componentName}">
    <AvailabilityConfiguration>
        <ReadOnly>treeCode1,treeCode2,treeCodeDynamicTreeJava1,treeCodeDynamicTreeJava2</ReadOnly>
        <ReadOnly when="-selectedNode==null">removeNode,addSubnode</ReadOnly>
    </AvailabilityConfiguration>
    <OutputLabel width="md-12" value="{description}" id="_Form_OutputLabel"/>
    <Spacer width="md-12" height="25px" id="_Form_Spacer"/>
    <TabContainer id="_Form_TabContainer">
        <Tab label="{$.fh.docs.component.examples}" id="_Form_TabContainer_Tab1">
                <PanelGroup label="{$.fh.docs.component.tree_simple_tree}" width="md-12" id="_Form_TabContainer_Tab1_PanelGroup1">
                    <Tree collection="{nodes}" iterator="node" relation="{children}" id="_Form_TabContainer_Tab1_PanelGroup1_Tree">
                        <!--<OutputLabel value="{node.name}"/>-->
                        <TreeElement onLabelClick="-" label="{node.name}" id="_Form_TabContainer_Tab1_PanelGroup1_Tree_TreeElement"/>
                    </Tree>
                    <InputText id="treeCode1" label="{$.fh.docs.component.code}" width="md-12" rowsCount="3">
                        <![CDATA[![ESCAPE[<Tree collection="{nodes}" iterator="node" relation="{children}">
    <TreeElement onLabelClick="-" label="{node.name}"/>
</Tree>]]]]>
                    </InputText>
                </PanelGroup>
                <PanelGroup label="{$.fh.docs.component.tree_simple_tree_without_lines}" width="md-12" id="_Form_TabContainer_Tab1_PanelGroup2">
                    <Tree collection="{nodes}" iterator="node" relation="{children}" lines="false" id="_Form_TabContainer_Tab1_PanelGroup2_Tree">
                        <!--<OutputLabel value="{node.name}"/>-->
                        <TreeElement onLabelClick="-" label="{node.name}" id="_Form_TabContainer_Tab1_PanelGroup2_Tree_TreeElement"/>
                    </Tree>
                    <InputText id="treeCode2" label="{$.fh.docs.component.code}" width="md-12" rowsCount="3">
                        <![CDATA[![ESCAPE[<Tree collection="{nodes}" iterator="node" relation="{children}" lines="false">
    <TreeElement onLabelClick="-" label="{node.name}"/>
</Tree>]]]]>
                    </InputText>
                </PanelGroup>

                <PanelGroup label="{$.fh.docs.component.tree_dynamic_tree_change_tree_from_java}" width="md-12" id="_Form_TabContainer_Tab1_PanelGroup3">
                    <Tree collection="{nodes}" iterator="node" relation="{children}" selected="{selectedNode}" id="_Form_TabContainer_Tab1_PanelGroup3_Tree">
                        <TreeElement label="{node.name}" onLabelClick="-" icon="{node.icon}" id="_Form_TabContainer_Tab1_PanelGroup3_Tree_TreeElement"/>
                    </Tree>
                    <Button id="addNode" label="{$.fh.docs.component.tree_add_node_dynamically}" onClick="addNodeDynamically(nodes)"/>
                    <Button id="addSubnode" label="{$.fh.docs.component.tree_add_subnode_to_selected_node}" onClick="addNodeDynamically(selectedNode.children)"/>
                    <Button id="removeNode" label="{$.fh.docs.component.tree_remove_selected_node}" onClick="removeNodeDynamically(selectedNode)"/>
                    <OutputLabel width="md-12" value="{$.fh.docs.component.tree_selected_node_name}: {selectedNode.name}" id="_Form_TabContainer_Tab1_PanelGroup3_OutputLabel"/>
                    <InputText id="treeCodeDynamicTreeJava1" label="{$.fh.docs.component.code}" width="md-12" rowsCount="8">
                        <![CDATA[![ESCAPE[<Tree collection="{nodes}" iterator="node" relation="{children}">
    <TreeElement label="{node.name}" onLabelClick="-" icon="{node.icon}"/>
    <Button label="Activate: {node.name}" onClick="updateTreeMessage()"/>
</Tree>
<Button label="Add node dynamically" onClick="addNodeDynamically(nodes)"/>
<Button label="Add subnode to selected node" onClick="addNodeDynamically(selectedNode.children)"/>
<Button label="Remove selected node" onClick="removeNodeDynamically(selectedNode)"/>
<OutputLabel value="Selected node name: {selectedNode.name}" />]]]]>
                    </InputText>
                </PanelGroup>

                <PanelGroup label="{$.fh.docs.component.tree_lazy_tree_calculates_children_on_demand}" width="md-12" id="_Form_TabContainer_Tab1_PanelGroup4">
                    <Tree collection="{fileSystem}" iterator="file" relation="{files}" selected="{selectedFile}" lazy="true" nextLevelExpandableExpression="{folder}" id="_Form_TabContainer_Tab1_PanelGroup4_Tree">
                        <TreeElement label="{file.name}" onLabelClick="-" icon="{file.icon}" id="_Form_TabContainer_Tab1_PanelGroup4_Tree_TreeElement"/>
                    </Tree>
                    <InputText id="treeCodeDynamicTreeJava2" label="{$.fh.docs.component.code}" width="md-12" rowsCount="3">
                        <![CDATA[![ESCAPE[<Tree collection="{fileSystem}" iterator="file" relation="{files}" selected="{selectedFile}" lazy="true" nextLevelExpandableExpression="{folder}">
    <TreeElement label="{file.name}" onLabelClick="-" icon="{file.icon}"/>
</Tree>]]]]>
                    </InputText>
                </PanelGroup>
                <PanelGroup label="{$.fh.docs.component.tree_lazy_tree_calculates_children_on_demand_repeater}" width="md-12" id="_Form_TabContainer_Tab1_PanelGroup5">
                    <Repeater collection="{multiFileSystemDirect}" iterator="el" id="_Form_TabContainer_Tab1_PanelGroup5_Repeater1">
                        <Tree collection="{el}" iterator="file" relation="{files}" selected="{selectedFile}" lazy="true" nextLevelExpandableExpression="{folder}" id="_Form_TabContainer_Tab1_PanelGroup5_Repeater1_Tree">
                            <TreeElement label="{file.name}" onLabelClick="-" icon="{file.icon}" id="_Form_TabContainer_Tab1_PanelGroup5_Repeater1_Tree_TreeElement"/>
                        </Tree>
                    </Repeater>
                    <Repeater collection="{multiFileSystem}" iterator="el" id="_Form_TabContainer_Tab1_PanelGroup5_Repeater2">
                        <Tree collection="{el.files}" iterator="file" relation="{files}" selected="{selectedFile}" lazy="true" nextLevelExpandableExpression="{folder}" id="_Form_TabContainer_Tab1_PanelGroup5_Repeater2_Tree">
                            <TreeElement label="{file.name}" onLabelClick="-" icon="{file.icon}" id="_Form_TabContainer_Tab1_PanelGroup5_Repeater2_Tree_TreeElement"/>
                        </Tree>
                    </Repeater>
                </PanelGroup>
                <!--PanelGroup label="Prototyping with inline data" width="md-12">
                    <Tree collection="{RULE.pl.fhframework.core.rules.builtin.CsvTree.csvTree('Car(Wheels(Wheel|Wheel|Wheel|Wheel)|Engine|Cabin)|Driver(Head(Brain)|Arms(Left|Right)|Legs(Left|Right)')}" iterator="elem" relation="{subelements}">
                        <TreeElement label="{elem.label}"/>
                    </Tree>
                    <InputText id="treeCodeInline" label="Code" width="md-12" rowsCount="3">
                        <![CDATA[![ESCAPE[<Tree collection="{RULE.csvTree('Car(Wheels(Wheel|Wheel|Wheel|Wheel)|Engine|Cabin)|Driver(Head(Brain)|Arms(Left|Right)|Legs(Left|Right)')}" iterator="elem" relation="{subelements}">
    <TreeElement label="{elem.label}"/>
</Tree>]]]]>
                    </InputText>
                </PanelGroup-->
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