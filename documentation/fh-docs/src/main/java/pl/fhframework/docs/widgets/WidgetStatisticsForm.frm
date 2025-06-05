<?xml version="1.0" encoding="UTF-8"?>
<Form xmlns="http://fh.asseco.com/form/1.0" container="mainForm" id="documentationHolder" label="Widget statistics">
    <AvailabilityConfiguration>
        <Invisible when="-statistics==null">staticticTable</Invisible>
        <Invisible when="-statistics!=null">staticticNoService</Invisible>
    </AvailabilityConfiguration>
    <OutputLabel width="md-12" value="Statistics of usage FH widgets" id="_Form_OutputLabel"/>
    <PanelGroup width="md-12" label="Statistics" id="_Form_PanelGroup">
        <OutputLabel width="md-12" id="staticticNoService" value="WidgetStatisticsService implementation not found..."/>
        <Table id="staticticTable" iterator="statistic" collection="{statistics}" onRowClick="-">
            <Column label="Name" value="{statistic.name}" id="_Form_PanelGroup_Table_Column1"/>
            <Column label="Count" value="{statistic.count}" id="_Form_PanelGroup_Table_Column2"/>
        </Table>
    </PanelGroup>

</Form>