<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<Form xmlns="http://fh.asseco.com/form/1.0" id="parametersForm" container="mainForm" formType="STANDARD">

    <Row width="md-12" styleClasses="main-page--flex">

        <Group styleClasses="group-header shadow-none" wrapperStyle="flex: unset; margin-left: 10px;">
            <Group styleClasses="header-group mb-0">
                <Row width="md-12" elementsHorizontalAlign="AROUND" styleClasses="row-panel panel-header">-->
                    <Group width="xs-1" wrapperStyle="
                        padding: var(--padding-bigger);
                        justify-self: start;
                        flex-grow: 1;
                        flex-basis: 0;
                        user-select: text;"
                           styleClasses="panel-header-group-unset">
                        <Group styleClasses="panel-info--container">
                            <OutputLabel availability="VIEW" width="md-12"
                                         wrapperStyle="width: auto; flex: initial; padding-right: unset;"
                                         styleClasses="panel-info--weight output-label-no-padding white-space--break-spaces"
                                         value=" "/>
                        </Group>
                        <OutputLabel availability="VIEW" width="md-12"
                                     styleClasses="panel-title output-label-no-padding white-space--break-spaces"
                                     value="{$.parameters.property.titleOfListForm}"/>
                        <OutputLabel availability="VIEW" width="md-12"
                                     styleClasses="panel-status output-label-no-padding white-space--break-spaces"
                                     value=" "/>
                    </Group>
                </Row>
            </Group>
        </Group>

        <Group id="table-group" wrapperStyle="height: 100%; flex: unset; overflow: auto; overflow-x: hidden;" styleClasses="mr-0">
            <TablePaged id="modelFrmUserTable" iterator="row" collection="{list}"
                        selected="{selectedSubstantiveParametersDto}" onRowClick="showParameterDetails()"
                        inlineStyle="overflow-x: unset;" horizontalScrolling="true">
                <ColumnPaged label="{$.parameters.property.rowNumber}" value="{row$rowNo}" width="5" availability="{checkControlVisibility()}"/>
                <ColumnPaged styleClasses="rowDisplayFlex" label="{$.parameters.property.nameAndKey}" width="35" sortBy="parameterNames.name.keyword">
                    <OutputLabel width="md-12" value="{row.parameterNames[0].name}"
                                 styleClasses="h6 text-wrap font-weight-bold"/>
                    <OutputLabel width="md-12" value="{row.key}" styleClasses="text-wrap"/>
                    <OutputLabel id="parameterSuccessScope" width="xs-1" value="{row.scope}" styleClasses="text-wrap status status--success mt-10"
                                 inlineStyle="padding: 6px 10px;" availability="{checkScopeVisibility(row, 'success')}"/>
                    <OutputLabel id="parameterWarningScope" width="xs-1" value="{row.scope}" styleClasses="text-wrap status status--warning mt-10"
                                 inlineStyle="padding: 6px 10px;" availability="{checkScopeVisibility(row, 'warning')}"/>
                </ColumnPaged>
                <ColumnPaged label="{$.parameters.property.value}" width="30" styleClasses="text-wrap" sortBy="value.keyword">
                    <OutputLabel value="{row.getFormattedCollectionValues()}" width="md-12"/>
                    <OutputLabel value="{row.value}" width="md-12"/>
                </ColumnPaged>
                <ColumnPaged label="{$.parameters.property.unit}" value="{row.unit}" width="10" sortBy="unit.keyword"/>
                <ColumnPaged label="{$.parameters.property.component}" value="{row.components}" width="7" sortBy="components.keyword"/>
                <ColumnPaged label="{$.parameters.property.tags}" styleClasses="text-wrap" value="{row.tags}" formatter="arrayStringFormatter" width="15" sortBy="tagsDisplay.keyword"/>
            </TablePaged>
        </Group>

    </Row>
    <Model externalClass="pl.fhframework.dp.commons.fh.parameters.list.SubstantiveParametersListModel"/>

</Form>

