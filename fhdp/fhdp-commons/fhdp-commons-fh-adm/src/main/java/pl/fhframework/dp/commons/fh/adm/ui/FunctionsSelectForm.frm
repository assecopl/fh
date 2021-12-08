<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<Form xmlns="http://fh.asseco.com/form/1.0" container="mainForm" hideHeader="true" formType="MODAL" modalSize="XLARGE">
    <PanelHeaderFhDP title="{$.adm.functions.select.header.title}" onClick="cancel" width="md-12" />
    <AvailabilityConfiguration>
        <ReadOnly when="-selectedFunctionLeft == null OR selectedFunctionLeft.isRoot()">addSelectedFunctionsButton,addDisallowedFunctionsButton</ReadOnly>
        <ReadOnly when="-selectedFunctionsRight.isEmpty()">removeSelectedFunctionsButton</ReadOnly>
        <ReadOnly when="-addedFunctions.isEmpty()">confirmButton</ReadOnly>
    </AvailabilityConfiguration>
    <PanelGroup height="500px" width="md-4" borderVisible="true" id="allFunctionsPanelGroup">
        <Tree verticalAlign="top" collection="{allSystemFunctions}" iterator="node" relation="{children}" selected="{selectedFunctionLeft}" leafIcon="fa fa-angle-right" id="allFunctionsTree">
            <TreeElement label="{node.name}" onLabelClick="-" id="functionTreeElement"/>
        </Tree>
    </PanelGroup>

    <PanelGroup width="md-1" id="buttonsPanelGroup" wrapperStyle="padding-left: 0px">
        <Spacer width="md-2" height="120px" id="buttonSpacer1"/>
        <Button id="addSelectedFunctionsButton" label="[icon='fa fa-check']" width="100px" onClick="addSelectedFunctionsAsAllowed" hint="{$.adm.functions.select.button.allow.hint}"/>
        <Spacer width="md-2" height="20px" id="buttonSpacer2"/>
        <Button id="addDisallowedFunctionsButton" label="[icon='fa fa-times']" width="100px" onClick="addSelectedFunctionsAsDisallowed" hint="{$.adm.functions.select.button.disallow.hint}"/>
        <Spacer width="md-2" height="20px" id="buttonSpacer3"/>
        <Button id="removeSelectedFunctionsButton" label="[icon='fa fa-trash']" width="100px" onClick="removeSelectedFunctions" hint="{$.adm.functions.select.button.remove.hint}"/>
    </PanelGroup>

    <Group height="500px" width="md-7" id="selectedFunctionsPanelGroup" wrapperStyle="padding-left: 26px">
        <Table iterator="function" collection="{addedFunctions}" multiselect="true" minRows="1" selected="{selectedFunctionsRight}" id="selectedFunctionsTable">
            <Column label="{$.adm.functions.select.function.column.function_name}" value="{function.name}" id="functionNameColumn" width="45"/>
            <Column label="{$.adm.functions.select.function.column.module_name}" value="{function.moduleLabel}" id="moduleNameColumn"/>
            <Column label="{$.adm.functions.select.function.column.allow_disallow}" horizontalAlign="center" verticalAlign="middle" id="allowDisallowColumn" width="25">
                <OutputLabel width="md-12" value="{function.denial ? '[color=''#C80000''][icon=''fa fa-times''][/color]' : '[color=''green''][icon=''fa fa-check''][/color]'}" id="allowDisallowOutputLabel"/>
            </Column>
        </Table>
    </Group>

    <Spacer width="md-12" height="10px" id="spacerConfirmButtons"/>
    <Group>
        <Button id="confirmButton" width="md-3" onClick="confirm" label="{$.adm.functions.select.button.confirm}"/>
        <Button id="cancelButton" width="md-3" onClick="cancel" label="{$.adm.functions.select.button.cancel}" />
    </Group>
</Form>
