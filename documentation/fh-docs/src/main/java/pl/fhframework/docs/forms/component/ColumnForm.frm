<?xml version="1.0" encoding="UTF-8"?>
<Form xmlns="http://fh.asseco.com/form/1.0" container="mainForm" id="documentationHolder" label="{componentName}">
    <AvailabilityConfiguration>
        <ReadOnly>
            columnExampleCode1,columnExampleCode2,columnExampleCode3,columnExampleCode4,column_CompanyEmployeesEditable_Name,column_CompanyEmployeesNonEditable_Name,column_CompanyEmployeesEditable2
        </ReadOnly>
    </AvailabilityConfiguration>
    <OutputLabel width="md-12" value="{description}" id="_Form_OutputLabel"/>
    <Spacer width="md-12" height="25px" id="_Form_Spacer"/>
    <TabContainer id="_Form_TabContainer">
        <Tab label="{$.fh.docs.component.examples}" id="_Form_TabContainer_Tab1">
                <TabContainer id="_Form_TabContainer_Tab1_TabContainer">
                    <Tab label="{$.fh.docs.component.column_displayed_data}" id="_Form_TabContainer_Tab1_TabContainer_Tab1">
                        <PanelGroup id="column_CompanyEmployeesReadOnly" width="md-12" label="{$.fh.docs.component.column_table_with_simple_usage}">
                            <Table label="{$.fh.docs.component.column_company_employees}" iterator="person" collection="{people}" onRowClick="-" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup_Table">
                                <Column label="{$.fh.docs.component.column_name}" value="{person.name}" width="12" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup_Table_Column1"/>
                                <Column label="{$.fh.docs.component.column_surname}" value="{person.surname}" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup_Table_Column2"/>
                                <Column label="{$.fh.docs.component.column_city}" value="{person.city}" width="30" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup_Table_Column3"/>
                                <Column label="{$.fh.docs.component.column_gender}" value="{person.gender}" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup_Table_Column4"/>
                                <Column label="{$.fh.docs.component.column_status}" value="{person.status}" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup_Table_Column5"/>
                            </Table>
                            <InputText id="columnExampleCode1" label="{$.fh.docs.component.code}" width="md-12" height="150" rowsCount="6">
                                <![CDATA[
<Table label="Company employees" iterator="person" collection="\{people\}" onRowClick="-">
    <Column label="Name" value="\{person.name\}" width="12"/>
    <Column label="Surname" value="\{person.surname\}" />
    <Column label="City" value="\{person.city\}" width="30"/>
    <Column label="Gender" value="\{person.gender\}" />
    <Column label="Status" value="\{person.status\}" />
</Table>
                                ]]>
                            </InputText>
                        </PanelGroup>
                    </Tab>
                    <Tab label="{$.fh.docs.component.column_non_editable_column}" id="_Form_TabContainer_Tab1_TabContainer_Tab2">
                        <PanelGroup width="md-12" label="{$.fh.docs.component.column_merged_columns}" id="_Form_TabContainer_Tab1_TabContainer_Tab2_PanelGroup">
                            <Table id="column_CompanyEmployeesNonEditable" label="{$.fh.docs.component.column_company_employees}" iterator="person" collection="{people}" onRowClick="-">
                                <Column id="column_CompanyEmployeesNonEditable_Name" label="Name">
                                    <InputText width="md-12" value="{person.name}" id="_Form_TabContainer_Tab1_TabContainer_Tab2_PanelGroup_Table_Column1_InputText"/>
                                </Column>
                                <Column label="{$.fh.docs.component.column_surname}" value="{person.surname}" id="_Form_TabContainer_Tab1_TabContainer_Tab2_PanelGroup_Table_Column2"/>
                                <Column label="{$.fh.docs.component.column_city}" id="_Form_TabContainer_Tab1_TabContainer_Tab2_PanelGroup_Table_Column3">
                                    <OutputLabel width="md-12" value="{person.city}" id="_Form_TabContainer_Tab1_TabContainer_Tab2_PanelGroup_Table_Column3_OutputLabel"/>
                                </Column>
                                <Column label="{$.fh.docs.component.column_gender}" value="{person.gender}" id="_Form_TabContainer_Tab1_TabContainer_Tab2_PanelGroup_Table_Column4"/>
                                <Column label="{$.fh.docs.component.column_status}" value="{person.status}" id="_Form_TabContainer_Tab1_TabContainer_Tab2_PanelGroup_Table_Column5"/>
                            </Table>
                            <InputText id="columnExampleCode2" label="{$.fh.docs.component.code}" width="md-12" height="250" rowsCount="11">
                                <![CDATA[
<Table id="column_CompanyEmployeesNonEditable" label="Company employees" iterator="person" collection="\{people\}" onRowClick="-">
    <Column id="column_CompanyEmployeesNonEditable_Name" label="Name">
        <InputText value="\{person.name\}" />
    </Column>
    <Column label="Surname" value="\{person.surname\}" />
    <Column label="City">
        <OutputLabel value="\{person.city\}"></OutputLabel>
    </Column>
    <Column label="Gender" value="\{person.gender\}" />
    <Column label="Status" value="\{person.status\}" />
</Table>
                                ]]>
                            </InputText>
                        </PanelGroup>
                    </Tab>
                    <Tab label="{$.fh.docs.component.column_merged_columns_with_binding}" id="_Form_TabContainer_Tab1_TabContainer_Tab3">
                        <PanelGroup width="md-12" label="{$.fh.docs.component.column_merged_columns}" id="_Form_TabContainer_Tab1_TabContainer_Tab3_PanelGroup1">
                            <Table id="column_CompanyEmployeesEditable" label="{$.fh.docs.component.column_company_employees}" iterator="person" collection="{people}" onRowClick="-">
                                <Column label="{companyEmployeesEditableTableNameAndSurname}" id="_Form_TabContainer_Tab1_TabContainer_Tab3_PanelGroup1_Table_Column1">
                                    <Column id="column_CompanyEmployeesEditable_Name" label="{companyEmployeesEditableTableName}">
                                        <InputText width="md-12" value="{person.name}" id="_Form_TabContainer_Tab1_TabContainer_Tab3_PanelGroup1_Table_Column1_Column1_InputText"/>
                                    </Column>
                                    <Column label="{companyEmployeesEditableTableSurname}" value="{person.surname}" id="_Form_TabContainer_Tab1_TabContainer_Tab3_PanelGroup1_Table_Column1_Column2"/>
                                </Column>
                                <Column label="{companyEmployeesEditableTableCity}" id="_Form_TabContainer_Tab1_TabContainer_Tab3_PanelGroup1_Table_Column2">
                                    <OutputLabel width="md-12" value="{person.city}" id="_Form_TabContainer_Tab1_TabContainer_Tab3_PanelGroup1_Table_Column2_OutputLabel"/>
                                </Column>
                                <Column label="{companyEmployeesEditableTableGender}" value="{person.gender}" id="_Form_TabContainer_Tab1_TabContainer_Tab3_PanelGroup1_Table_Column3"/>
                                <Column label="{companyEmployeesEditableTableStatus}" value="{person.status}" id="_Form_TabContainer_Tab1_TabContainer_Tab3_PanelGroup1_Table_Column4"/>
                            </Table>
                            <InputText id="columnExampleCode3" label="{$.fh.docs.component.code}" width="md-12" height="290" rowsCount="5">
                                <![CDATA[
<Table id="column_CompanyEmployeesEditable" label="Company employees" iterator="person" collection="\{people\}" onRowClick="-">
    <Column label="\{companyEmployeesEditableTableNameAndSurname\}">
        <Column id="column_CompanyEmployeesEditable_Name" label="\{companyEmployeesEditableTableName\}">
            <InputText value="\{person.name\}" />
        </Column>
        <Column label="\{companyEmployeesEditableTableSurname\}" value="\{person.surname\}" />
    </Column>
    <Column label="\{companyEmployeesEditableTableCity\}">
        <OutputLabel value="\{person.city\}"></OutputLabel>
    </Column>
    <Column label="\{companyEmployeesEditableTableGender\}" value="\{person.gender\}"/>
    <Column label="\{companyEmployeesEditableTableStatus\}" value="\{person.status\}"/>
</Table>
                                ]]>
                            </InputText>
                        </PanelGroup>

                        <PanelGroup width="md-12" label="{$.fh.docs.component.column_merged_columns_advanced}" id="_Form_TabContainer_Tab1_TabContainer_Tab3_PanelGroup2">
                            <Table id="column_CompanyEmployeesEditable2" label="{$.fh.docs.component.column_company_employees}" iterator="person" collection="{people}" onRowClick="-">
                                <Column label="{companyEmployeesEditableTableFullInfo}" id="_Form_TabContainer_Tab1_TabContainer_Tab3_PanelGroup2_Table_Column">
                                    <Column label="{companyEmployeesEditableTablePersonalDate}" id="_Form_TabContainer_Tab1_TabContainer_Tab3_PanelGroup2_Table_Column_Column1">
                                        <Column label="{companyEmployeesEditableTableNameAndSurnameAndCity}" id="_Form_TabContainer_Tab1_TabContainer_Tab3_PanelGroup2_Table_Column_Column1_Column">
                                            <Column label="{companyEmployeesEditableTableNameAndSurname}" id="_Form_TabContainer_Tab1_TabContainer_Tab3_PanelGroup2_Table_Column_Column1_Column_Column1">
                                                <Column label="{companyEmployeesEditableTableName}" id="_Form_TabContainer_Tab1_TabContainer_Tab3_PanelGroup2_Table_Column_Column1_Column_Column1_Column1">
                                                    <InputText width="md-12" value="{person.name}" id="_Form_TabContainer_Tab1_TabContainer_Tab3_PanelGroup2_Table_Column_Column1_Column_Column1_Column1_InputText"/>
                                                </Column>
                                                <Column label="{companyEmployeesEditableTableSurname}" value="{person.surname}" id="_Form_TabContainer_Tab1_TabContainer_Tab3_PanelGroup2_Table_Column_Column1_Column_Column1_Column2"/>
                                            </Column>
                                            <Column label="{companyEmployeesEditableTableCity}" id="_Form_TabContainer_Tab1_TabContainer_Tab3_PanelGroup2_Table_Column_Column1_Column_Column2">
                                                <OutputLabel width="md-12" value="{person.city}" id="_Form_TabContainer_Tab1_TabContainer_Tab3_PanelGroup2_Table_Column_Column1_Column_Column2_OutputLabel"/>
                                            </Column>
                                        </Column>
                                    </Column>
                                    <Column label="{companyEmployeesEditableTableGenderAndStatus}" id="_Form_TabContainer_Tab1_TabContainer_Tab3_PanelGroup2_Table_Column_Column2">
                                        <Column label="{companyEmployeesEditableTableGender}" value="{person.gender}" id="_Form_TabContainer_Tab1_TabContainer_Tab3_PanelGroup2_Table_Column_Column2_Column1"/>
                                        <Column label="{companyEmployeesEditableTableStatus}" value="{person.status}" id="_Form_TabContainer_Tab1_TabContainer_Tab3_PanelGroup2_Table_Column_Column2_Column2"/>
                                    </Column>
                                </Column>
                            </Table>
                            <InputText id="columnExampleCode4" label="{$.fh.docs.component.code}" width="md-12" height="430" rowsCount="9">
                                <![CDATA[
 <Table id="column_CompanyEmployeesEditable2" label="Company employees" iterator="person" collection="\{people\}" onRowClick="-">
    <Column label="\{companyEmployeesEditableTableFullInfo\}">
        <Column label="\{companyEmployeesEditableTablePersonalDate\}">
            <Column label="\{companyEmployeesEditableTableNameAndSurnameAndCity\}">
                <Column label="\{companyEmployeesEditableTableNameAndSurname\}">
                    <Column label="\{companyEmployeesEditableTableName\}">
                        <InputText value="\{person.name\}" />
                    </Column>
                    <Column label="\{companyEmployeesEditableTableSurname\}" value="\{person.surname\}" />
                </Column>
                <Column label="\{companyEmployeesEditableTableCity\}">
                    <OutputLabel value="\{person.city\}"></OutputLabel>
                </Column>
            </Column>
        </Column>
        <Column label="\{companyEmployeesEditableTableGenderAndStatus\}">
            <Column label="\{companyEmployeesEditableTableGender\}" value="\{person.gender\}"/>
            <Column label="\{companyEmployeesEditableTableStatus\}" value="\{person.status\}"/>
        </Column>
    </Column>
</Table>
                                ]]>
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

    <Button id="pBack" label="[icon='fa fa-chevron-left'] {$.fh.docs.component.back}" onClick="backToFormComponentsList()"/>

</Form>