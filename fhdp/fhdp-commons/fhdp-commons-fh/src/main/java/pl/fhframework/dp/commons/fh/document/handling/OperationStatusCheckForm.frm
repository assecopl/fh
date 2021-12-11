<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<Form xmlns="http://fh.asseco.com/form/1.0" id="OpDataBaseForm"
      label="[className='col-xs-1,w-100'][fhportal='id=basicDataAndCommonCommandsModal;wrapped=true;replaceParentId=modal-title'][/className]"
      container="mainForm" formType="MODAL"
      modalSize="XLARGE">
    <AvailabilityConfiguration>
        <Invisible when="-!operationStateResponse.finished">konec</Invisible>
        <Invisible when="-operationStateResponse.finished">konec2</Invisible>
        <Invisible when="-operationStateResponse.finished">imgWork</Invisible>
        <Invisible when="-internal">btnCancel,basicDataAndCommonCommandsModal,modalButtonsContainer</Invisible>
    </AvailabilityConfiguration>

    <Group id="basicDataAndCommonCommandsModal" styleClasses="group-header box-shadow__unset">
        <Row width="md-12" elementsHorizontalAlign="AROUND" styleClasses="row-panel panel-header">
            <Group
                    width="xs-1"
                    styleClasses="panel-header-group-base"
                    wrapperStyle="
                                padding: var(--padding-bigger);
                                justify-self: start;
                                user-select: text;
                                margin-right: 30px;
                                flex-grow: 1;
                                flex-basis: 0;
                                padding-right: unset;
                                align-self: center !important;">
                <OutputLabel availability="VIEW" width="md-12"
                             styleClasses="panel-title output-label-no-padding output-max-width white-space--break-spaces"
                             value="{$.operation.progress.check.label} "
                             hint = "Title"/>
            </Group>

            <ButtonGroup id="buttonGroupModal" horizontalAlign="right"
                         wrapperStyle="padding: var(--padding-bigger);"
                         styleClasses="button-icon-group justify-content-end panel-options-base" width="xs-1">
                <Button id="cancelButton" style="default" styleClasses="panel-option margin__unset" horizontalAlign="right"
                        label="[b][icon='fas fa-times'][/b]"
                        hint="{$.common.close}" hintTrigger="HOVER"
                        hintPlacement="BOTTOM"
                        onClick="cancel"/>
            </ButtonGroup>
        </Row>
    </Group>


    <Row width="md-12" wrapperStyle="flex: unset; overflow: auto;" styleClasses="h-100">
        <Row elementsHorizontalAlign="CENTER">
            <OutputLabel id="konec" width="md-12" styleClasses="h3 text-success" value="{$.operation.progress.check.operationFinished}"/>
            <OutputLabel id="konec2" width="md-12" styleClasses="h3 text-success" value=" "/>
        </Row>
        <Row elementsHorizontalAlign="CENTER" width="md-12">
            <Table width="md-12" collection="{operationStateResponse.steps}"
                   iterator="row" inlineStyle="padding: 0 15px;"
                   styleClasses="table-default-cursor disable-thead-auto-resize">
                <Column value=" " width="1"/>
                <Column styleClasses="text-wrap" width="20" label="{$.operation.progress.check.column.description}" value="{row.description}" formatter="stringPropertyFormatter"/>
                <Column styleClasses="text-wrap" width="10" label="{$.operation.progress.check.column.started}" value="{row.started}" formatter="defaultDatePropertyFormatter"/>
                <Column styleClasses="text-wrap" width="10" label="{$.operation.progress.check.column.finished}" value="{row.finished}" formatter="defaultDatePropertyFormatter"/>
                <Column styleClasses="text-wrap" width="10" label="{$.operation.progress.check.column.duration}" value="{FORM.formatDuration(row.duration)}"/>
            </Table>
        </Row>
    </Row>


    <Row elementsHorizontalAlign="LEFT" id="modalButtonsContainer" styleClasses="panel-footer mb-n3">
        <Button id="btnCancel"
                availability="EDIT" width="xs-1"
                onClick="cancel" label="{$.operation.progress.check.button.cancel}"
                hintPlacement="BOTTOM" style="default"
                inlineStyle="line-height: unset;"
                wrapperStyle="margin-bottom: unset; margin-right: 5px;"/>
    </Row>

</Form>
