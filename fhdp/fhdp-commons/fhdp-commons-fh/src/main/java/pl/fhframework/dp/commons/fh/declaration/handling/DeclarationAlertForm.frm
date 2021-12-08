<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<Form xmlns="http://fh.asseco.com/form/1.0" id="alertResultForm" label="{$.declaration.ct.tabs.declarationAlert}" container="mainForm">
    <Row width="md-12">
        <TablePaged id="modelFrmUserTable" pageSize="5" iterator="row" rowHeight="small" tableStripes="show"
                    collection="{declarationAlertResult}">
            <ColumnPaged width="18" label="{$.declaration.ct.declarationAlerts.codeAndName}">
                <OutputLabel width="md-12" value="{row.name}" styleClasses="font-weight-bold" formatter="stringPropertyFormatter"/>
                <OutputLabel width="md-12" value="{row.code}"/>
            </ColumnPaged>
            <ColumnPaged width="5" label="{$.declaration.ct.declarationAlerts.time}" value="{row.time}" />
            <ColumnPaged width="5" label="{$.declaration.ct.declarationAlerts.office}" value="{row.office}" />
            <ColumnPaged width="18" label="{$.declaration.ct.declarationAlerts.guidelines}" value="{row.guidelines}" styleClasses="text-wrap" formatter="stringPropertyFormatter" />
            <ColumnPaged width="18" label="{$.declaration.ct.declarationAlerts.description}" value="{row.description}" styleClasses="text-wrap" formatter="stringPropertyFormatter" />
            <ColumnPaged width="10" label="{$.declaration.ct.declarationAlerts.roles}" value="{getRoleList(row.roles)}" styleClasses="text-wrap" />
        </TablePaged>
    </Row>
</Form>