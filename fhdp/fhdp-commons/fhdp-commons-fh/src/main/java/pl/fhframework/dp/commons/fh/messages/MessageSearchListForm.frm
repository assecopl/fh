<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<Form xmlns="http://fh.asseco.com/form/1.0" id="parametersForm" container="mainForm" formType="STANDARD">
    <Row width="md-12" styleClasses="main-page--flex">
        <Group styleClasses="group-header shadow-none" wrapperStyle="flex: unset;">
            <Group styleClasses="header-group mb-0">
                <Row width="md-12" elementsHorizontalAlign="AROUND" styleClasses="row-panel panel-header">
                    <Group width="xs-1" wrapperStyle="
                        padding: var(--padding-bigger);
                        justify-self: start;
                        flex-grow: 1;
                        flex-basis: 0;
                        user-select: text;"
                           styleClasses="panel-header-group-unset">
                        <Group styleClasses="panel-info--container">
                            <OutputLabel availability="VIEW" width="md-12"
                                         wrapperStyle="width: min-content; flex: initial; padding-right: unset;"
                                         styleClasses="panel-info--weight output-label-no-padding output-max-width white-space--break-spaces"
                                         value=" "/>
                        </Group>
                        <OutputLabel availability="VIEW" width="md-12"
                                     styleClasses="panel-title output-label-no-padding output-max-width white-space--break-spaces"
                                     value="{$.message.search}"/>
                        <OutputLabel availability="VIEW" width="md-12"
                                     styleClasses="panel-status output-label-no-padding output-max-width white-space--break-spaces"
                                     value=" "/>
                    </Group>
                    <ButtonGroup id="buttonGroup" horizontalAlign="right"
                                 wrapperStyle="padding: var(--padding-bigger);"
                                 styleClasses="button-icon-group justify-content-end panel-options-base flex-nowrap panel-option--group-button"
                                 width="xs-1" inlineStyle="flex-wrap: nowrap;">
                        <Button id="cancelButton" style="default" styleClasses="panel-option margin__unset" horizontalAlign="right"
                                label="[b][icon='fas fa-times'][/b]"
                                hint="{$.common.close}" hintTrigger="HOVER"
                                hintPlacement="BOTTOM"
                                onClick="close"/>
                    </ButtonGroup>
                </Row>
            </Group>
        </Group>
        <Group id="table-group-toolsRow" wrapperStyle="height: 100%; flex: unset; overflow: auto;">

            <TablePaged id="modelFrmUserTable" iterator="row" collection="{list}" onRowClick="edit" selected="{selectedMessage}"
                        horizontalScrolling="true" inlineStyle="overflow-x: unset;">

                <ColumnPaged id="lp" label="Lp." value="{row$rowNo}"/>
                <ColumnPaged id="repositoryId" label="{$.message.table.repositoryId}" value="{row.repositoryId}"/>
                <ColumnPaged label="{$.message.table.declarationId}" value="{row.declarationId}"/>
                <ColumnPaged label="{$.message.table.name}" value="{row.name}"/>
                <ColumnPaged label="{$.message.table.stored}" value="{row.stored}"/>
                <ColumnPaged label="{$.message.table.delivered}" value="{row.delivered}"/>
                <ColumnPaged label="{$.message.table.direction}" value="{row.direction}"/>
                <ColumnPaged label="{$.message.table.metadata.name}" value="{row.metadata.name}"/>
                <ColumnPaged label="{$.message.table.metadata.lrn}" value="{row.metadata.localReferenceNumber}"/>
                <ColumnPaged label="{$.message.table.metadata.mrn}" value="{row.metadata.customsReferenceNumber}"/>
                <ColumnPaged label="{$.message.table.metadata.identifier}" value="{row.metadata.messageIdentification}"/>
                <ColumnPaged label="{$.message.table.metadata.date}" value="{row.metadata.date}"/>
                <ColumnPaged label="{$.message.table.metadata.sender.identifier}" value="{row.metadata.sender.identifier}"/>
                <ColumnPaged label="{$.message.table.metadata.sender.domain}" value="{row.metadata.sender.domain}"/>
            </TablePaged>
        </Group>
    </Row>
    <Model externalClass="pl.fhframework.dp.commons.fh.messages.MessageSearchListModel"/>
</Form>

