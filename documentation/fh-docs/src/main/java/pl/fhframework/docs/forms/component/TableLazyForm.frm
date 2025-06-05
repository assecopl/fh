<?xml version="1.0" encoding="UTF-8"?>
<Form xmlns="http://fh.asseco.com/form/1.0" container="mainForm" id="documentationHolder" label="{componentName}">
    <AvailabilityConfiguration>
        <ReadOnly>
            tablePagedExampleCode1
        </ReadOnly>
    </AvailabilityConfiguration>
    <OutputLabel width="md-12" value="{description}" id="_Form_OutputLabel"/>
    <Spacer width="md-12" height="25px" id="_Form_Spacer"/>
    <TabContainer id="_Form_TabContainer">
        <Tab label="{$.fh.docs.component.examples}" id="_Form_TabContainer_Tab1">
            <TabContainer id="_Form_TabContainer_Tab1_TabContainer">
                <Tab label="{$.fh.docs.component.tablepaged_paged_data}" id="_Form_TabContainer_Tab1_TabContainer_Tab">
                    <PanelGroup width="md-12" label="{$.fh.docs.component.tablepaged_table_with_simple_usage}" id="_Form_TabContainer_Tab1_TabContainer_Tab_PanelGroup">
                        <TableLazy id="tablePage_CompanyEmployeesReadOnly" iterator="person" collection="{pagedPeopleForRead}" startSize="5">
                            <ColumnLazy label="Name" value="{person.name}" id="_Form_TabContainer_Tab1_TabContainer_Tab_PanelGroup_TableLazy_ColumnLazy1"/>
                            <ColumnLazy label="Surname" value="{person.surname}" id="_Form_TabContainer_Tab1_TabContainer_Tab_PanelGroup_TableLazy_ColumnLazy2"/>
                            <ColumnLazy label="City" value="{person.city}" id="_Form_TabContainer_Tab1_TabContainer_Tab_PanelGroup_TableLazy_ColumnLazy3"/>
                            <ColumnLazy label="Gender" value="{person.gender}" id="_Form_TabContainer_Tab1_TabContainer_Tab_PanelGroup_TableLazy_ColumnLazy4"/>
                            <ColumnLazy label="Status" value="{person.status}" id="_Form_TabContainer_Tab1_TabContainer_Tab_PanelGroup_TableLazy_ColumnLazy5"/>
                        </TableLazy>
                        <InputText id="tablePagedExampleCode1" label="{$.fh.docs.component.code}" width="md-12" height="230" rowsCount="7">
                            <![CDATA[![ESCAPE[<TableLazy id="tablePage_CompanyEmployeesReadOnly" iterator="person" collection="{pagedPeopleForRead}" onRowClick="-" pageSize="10">
    <ColumnLazy label="Name" value="{person.name}"/>
    <ColumnLazy label="Surname" value="{person.surname}"/>
    <ColumnLazy label="City" value="{person.city}"/>
    <ColumnLazy label="Gender" value="{person.gender}"/>
    <ColumnLazy label="Status" value="{person.status}"/>
</TableLazy>]]]]>
                        </InputText>
                    </PanelGroup>
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
    <Button id="pBack" label="[icon='fa fa-chevron-left'] {$.fh.docs.component.back}" onClick="backToFormComponentsList"/>

</Form>