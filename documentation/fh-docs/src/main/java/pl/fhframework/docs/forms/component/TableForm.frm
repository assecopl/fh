<?xml version="1.0" encoding="UTF-8"?>
<Form xmlns="http://fh.asseco.com/form/1.0" container="mainForm" id="documentationHolder" label="{componentName}">
    <AvailabilityConfiguration>
        <ReadOnly>tableExampleCode1,tableExampleCode2,tableExampleCode3,tableExampleCode4,tableExampleCode5,tableExampleCode7,tableExampleJavaCode,tableExampleJavaCode2,tableExampleCode6,episodeDuration,tableExampleCode7,coloredEpisodeDuration,tableExampleCode8,tableExampleCode9,tableExampleCode10,tableExampleCode11,tableExampleJavaCode3,tableExampleJavaCode4,tableExampleCode12,tableExampleCode13,tableExampleCode14,table_inlineDataCode,tableExampleCode91</ReadOnly>
    </AvailabilityConfiguration>
    <OutputLabel width="md-12" value="{description}" id="_Form_OutputLabel"/>
    <Spacer width="md-12" height="25px" id="_Form_Spacer"/>
    <TabContainer id="_Form_TabContainer">
        <Tab label="{$.fh.docs.component.examples}" id="_Form_TabContainer_Tab1">
                <TabContainer id="_Form_TabContainer_Tab1_TabContainer">
                    <Tab label="{$.fh.docs.component.table_displayed_data}" id="_Form_TabContainer_Tab1_TabContainer_Tab1">
                        <PanelGroup width="md-12" label="{$.fh.docs.component.table_table_with_simple_usage}" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup1">
                            <Table id="table_CompanyEmployeesReadOnlyDisplay" iterator="person" collection="{restrictedPeople}" onRowClick="-" minRows="3">
                                <Column label="{$.fh.docs.component.table_name}" value="{person.name}" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup1_Table_Column1"/>
                                <Column label="{$.fh.docs.component.table_surname}" value="{person.surname}" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup1_Table_Column2"/>
                                <Column label="{$.fh.docs.component.table_city}" value="{person.city}" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup1_Table_Column3"/>
                                <Column label="{$.fh.docs.component.table_gender}" value="{person.gender}" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup1_Table_Column4"/>
                                <Column label="{$.fh.docs.component.table_status}" value="{person.status}" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup1_Table_Column5"/>
                            </Table>
                            <InputText id="tableExampleCode1" label="{$.fh.docs.component.code}" width="md-12" height="240" rowsCount="7">
                                <![CDATA[![ESCAPE[<Table id="table_CompanyEmployeesReadOnlyDisplay" label="Company employees" iterator="person" collection="{restrictedPeople}" onRowClick="-" minRows="3" >
    <Column label="Name" value="{person.name}" />
    <Column label="Surname" value="{person.surname}" />
    <Column label="City" value="{person.city}" />
    <Column label="Gender" value="{person.gender}" />
    <Column label="Status" value="{person.status}" />
</Table>]]]]>
                            </InputText>
                        </PanelGroup>

                        <PanelGroup width="md-12" label="{$.fh.docs.component.table_table_with_multi_size}" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup2">
                            <Table id="table_CompanyEmployeesMultiSize" iterator="person" collection="{restrictedPeople}" onRowClick="-" width="sm-12,md-9,lg-6">
                                <Column label="{$.fh.docs.component.table_name}" value="{person.name}" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup2_Table_Column1"/>
                                <Column label="{$.fh.docs.component.table_surname}" value="{person.surname}" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup2_Table_Column2"/>
                                <Column label="{$.fh.docs.component.table_city}" value="{person.city}" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup2_Table_Column3"/>
                                <Column label="{$.fh.docs.component.table_gender}" value="{person.gender}" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup2_Table_Column4"/>
                                <Column label="{$.fh.docs.component.table_status}" value="{person.status}" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup2_Table_Column5"/>
                            </Table>
                            <InputText id="tableExampleCode6" label="{$.fh.docs.component.code}" width="md-12" rowsCount="7">
                                <![CDATA[![ESCAPE[<Table id="table_CompanyEmployeesMultiSize" label="Company employees" iterator="person" collection="{restrictedPeople}" onRowClick="-" width="xs-12,sm-10,md-8,lg-6">
    <Column label="Name" value="{person.name}" />
    <Column label="Surname" value="{person.surname}" />
    <Column label="City" value="{person.city}" />
    <Column label="Gender" value="{person.gender}" />
    <Column label="Status" value="{person.status}" />
</Table>]]]]>
                            </InputText>
                        </PanelGroup>
                        <PanelGroup width="md-12" label="{$.fh.docs.component.table_fixed_header}" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup3">
                            <Table fixedHeader="true" height="250px" id="table_CompanyEmployeesReadOnlyDisplay2" iterator="person" collection="{multiSelectPeople}" onRowClick="-">
                                <Column label="{$.fh.docs.component.table_name}" value="{person.name}" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup3_Table_Column1"/>
                                <Column label="{$.fh.docs.component.table_surname}" value="{person.surname}" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup3_Table_Column2"/>
                                <Column label="{$.fh.docs.component.table_city}" value="{person.city}" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup3_Table_Column3"/>
                                <Column label="{$.fh.docs.component.table_gender}" value="{person.gender}" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup3_Table_Column4"/>
                                <Column label="{$.fh.docs.component.table_status}" value="{person.status}" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup3_Table_Column5"/>
                            </Table>
                            <InputText id="tableExampleCode14" label="{$.fh.docs.component.code}" width="md-12" height="240" rowsCount="7">
                                <![CDATA[![ESCAPE[<Table id="table_CompanyEmployeesReadOnlyDisplay"  fixedHeader="true" height="250px" iterator="person" collection="{multiSelectPeople}" onRowClick="-">
    <Column label="Name" value="{person.name}" />
    <Column label="Surname" value="{person.surname}" />
    <Column label="City" value="{person.city}" />
    <Column label="Gender" value="{person.gender}" />
    <Column label="Status" value="{person.status}" />
</Table>]]]]>
                            </InputText>
                        </PanelGroup>
                        <PanelGroup width="md-12" label="{$.fh.docs.component.table_table_with_footer}" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup4">
                            <Table id="table_CompanyEmployeesWithFooter" iterator="person" collection="{restrictedPeople}" onRowClick="-" minRows="5">
                                <Column label="{$.fh.docs.component.table_name}" value="{person.name}" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup4_Table_Column1"/>
                                <Column label="{$.fh.docs.component.table_surname}" value="{person.surname}" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup4_Table_Column2"/>
                                <Column label="{$.fh.docs.component.table_city}" value="{person.city}" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup4_Table_Column3"/>
                                <Column label="{$.fh.docs.component.table_gender}" value="{person.gender}" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup4_Table_Column4"/>
                                <Column label="{$.fh.docs.component.table_status}" value="{person.status}" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup4_Table_Column5"/>
                                <Footer id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup4_Table_Footer">
                                    <Button label="{$.fh.docs.component.table_add} [size='16'][icon='fa fa-plus'][/size]" horizontalAlign="right" style="link" width="md-3" onClick="onAddClick" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup4_Table_Footer_Button"/>
                                </Footer>
                            </Table>
                            <InputText id="tableExampleCode8" label="{$.fh.docs.component.code}" width="md-12" rowsCount="10">
                                <![CDATA[![ESCAPE[<Table id="table_CompanyEmployeesWithFooter" label="Company employees" iterator="person" collection="{restrictedPeople}" onRowClick="-" minRows="5">
    <Column label="Name" value="{person.name}" />
    <Column label="Surname" value="{person.surname}" />
    <Column label="City" value="{person.city}" />
    <Column label="Gender" value="{person.gender}" />
    <Column label="Status" value="{person.status}" />
    <Footer>
        <Button label="add [size='16'][icon='fa fa-plus'][/size]" horizontalAlign="right" style="link" width="md-1" />
    </Footer>
</Table>]]]]>
                            </InputText>
                        </PanelGroup>
                        <PanelGroup width="md-12" label="{$.fh.docs.component.table_table_with_multiselect}" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup5">
                            <Table id="table_ActivePeople1" iterator="person" multiselect="true" selected="{selectedActivePeople}" ieFocusFixEnabled="true" collection="{multiSelectPeople}" onRowClick="+" minRows="5">
                                <Column label="{$.fh.docs.component.table_name}" value="{person.name}" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup5_Table1_Column1"/>
                                <Column label="{$.fh.docs.component.table_surname}" value="{person.surname}" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup5_Table1_Column2"/>
                                <Column label="{$.fh.docs.component.table_city}" value="{person.city}" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup5_Table1_Column3"/>
                                <Column label="{$.fh.docs.component.table_gender}" value="{person.gender}" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup5_Table1_Column4"/>
                                <Column label="{$.fh.docs.component.table_status}" value="{person.status}" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup5_Table1_Column5"/>
                            </Table>

                            <Table id="table_ActivePeople2" iterator="person" multiselect="true" selected="{selectedActivePeople}" ieFocusFixEnabled="true" collection="{multiSelectPeople}" onRowClick="+" minRows="5" selectionCheckboxes="true">
                                <Column label="{$.fh.docs.component.table_name}" value="{person.name}" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup5_Table2_Column1"/>
                                <Column label="{$.fh.docs.component.table_surname}" value="{person.surname}" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup5_Table2_Column2"/>
                                <Column label="{$.fh.docs.component.table_city}" value="{person.city}" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup5_Table2_Column3"/>
                                <Column label="{$.fh.docs.component.table_gender}" value="{person.gender}" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup5_Table2_Column4"/>
                                <Column label="{$.fh.docs.component.table_status}" value="{person.status}" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup5_Table2_Column5"/>
                            </Table>
                            <InputText id="tableExampleCode9" label="{$.fh.docs.component.code}" width="md-12" rowsCount="15">
<![CDATA[![ESCAPE[<Table id="table_ActivePeople1" label="Company employees" iterator="person" multiselect="true" selected="{selectedActivePeople}" collection="{multiSelectPeople}" onRowClick="+" minRows="5">
    <Column label="Name" value="{person.name}" />
    <Column label="Surname" value="{person.surname}" />
    <Column label="City" value="{person.city}" />
    <Column label="Gender" value="{person.gender}" />
    <Column label="Status" value="{person.status}" />
</Table>

<Table id="table_ActivePeople2" label="Company employees" iterator="person" multiselect="true" selected="{selectedActivePeople}" collection="{multiSelectPeople}" onRowClick="+" minRows="5" selectionCheckboxes="true">
    <Column label="Name" value="{person.name}" />
    <Column label="Surname" value="{person.surname}" />
    <Column label="City" value="{person.city}" />
    <Column label="Gender" value="{person.gender}" />
    <Column label="Status" value="{person.status}" />
</Table>]]]]>
                            </InputText>
                        </PanelGroup>
                        <PanelGroup width="md-12" label="{$.fh.docs.component.table_table_with_alignment}" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup6">
                            <Table id="table_halign" label="{$.fh.docs.component.table_horizontal_align}" iterator="person" collection="{multiSelectPeople}">
                                <Column label="left" value="left" horizontalAlign="left" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup6_Table1_Column1"/>
                                <Column label="center" value="center" horizontalAlign="center" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup6_Table1_Column2"/>
                                <Column label="right" value="right" horizontalAlign="right" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup6_Table1_Column3"/>
                            </Table>
                            <Table id="table_valign" label="{$.fh.docs.component.table_vertical_align}" iterator="person" collection="{multiSelectPeople}">
                                <Column label="top" value="top" verticalAlign="top" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup6_Table2_Column1"/>
                                <Column label="middle" value="middle" verticalAlign="middle" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup6_Table2_Column2"/>
                                <Column label="bottom" value="bottom" verticalAlign="bottom" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup6_Table2_Column3"/>
                            </Table>
                            <InputText id="tableExampleCode91" label="{$.fh.docs.component.code}" width="md-12" rowsCount="15">
                                <![CDATA[![ESCAPE[<Table id="table_halign" label="horizontalAlign" iterator="person" collection="{multiSelectPeople}">
    <Column label="left" value="left" horizontalAlign="left"/>
    <Column label="center" value="center" horizontalAlign="center"/>
    <Column label="right" value="right" horizontalAlign="right"/>
</Table>
<Table id="table_valign" label="verticalAlign" iterator="person" collection="{multiSelectPeople}">
    <Column>
        <OutputLabel value=" " width="md-12" />
        <OutputLabel value=" " width="md-12" />
        <OutputLabel value=" " width="md-12" />
    </Column>
    <Column label="top" value="top" verticalAlign="top"/>
    <Column label="middle" value="middle" verticalAlign="middle"/>
    <Column label="bottom" value="bottom" verticalAlign="bottom"/>
</Table>]]]]>
                            </InputText>
                        </PanelGroup>
                        <PanelGroup width="md-12" label="{$.fh.docs.component.table_table_with_vertical_scroll_and_resizable_columns}" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup7">
                            <Table fixedHeader="true" id="table_VerticalAndHorizontalScrollingWithResizableColumnWidth" minRows="10" height="250px">
                                <Column label="{$.fh.docs.component.table_wide_column_1}" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup7_Table_Column1"/>
                                <Column label="{$.fh.docs.component.table_wide_column_2}" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup7_Table_Column2"/>
                                <Column label="{$.fh.docs.component.table_wide_column_3}" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup7_Table_Column3"/>
                                <Column label="{$.fh.docs.component.table_wide_column_4}" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup7_Table_Column4"/>
                                <Column label="{$.fh.docs.component.table_wide_column_5}" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup7_Table_Column5"/>
                                <Column label="{$.fh.docs.component.table_wide_column_6}" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup7_Table_Column6"/>
                                <Column label="{$.fh.docs.component.table_wide_column_7}" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup7_Table_Column7"/>
                                <Column label="{$.fh.docs.component.table_wide_column_8}" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup7_Table_Column8"/>
                                <Column label="{$.fh.docs.component.table_wide_column_9}" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup7_Table_Column9"/>
                                <Column label="{$.fh.docs.component.table_wide_column_10}" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup7_Table_Column10"/>
                            </Table>

                            <InputText id="tableExampleCode16" label="{$.fh.docs.component.code}" width="md-12" height="390" rowsCount="12">
                                <![CDATA[![ESCAPE[<Table fixedHeader="true" id="table_VerticalAndHorizontalScrollingWithResizableColumnWidth" minRows="10" height="250px">
    <Column label="The_1st_column_in_wide_table" />
    <Column label="The_2nd_column_in_wide_table" />
    <Column label="The_3rd_column_in_wide_table" />
    <Column label="The_4th_column_in_wide_table" />
    <Column label="The_5th_column_in_wide_table" />
    <Column label="The_6th_column_in_wide_table" />
    <Column label="The_7th_column_in_wide_table" />
    <Column label="The_8th_column_in_wide_table" />
    <Column label="The_9th_column_in_wide_table" />
    <Column label="The_10th_column_in_wide_table" />
</Table>]]]]>
                            </InputText>
                        </PanelGroup>
                    </Tab>
                    <Tab label="{$.fh.docs.component.table_included_buttons}" id="_Form_TabContainer_Tab1_TabContainer_Tab2">
                        <PanelGroup width="md-12" label="{$.fh.docs.component.table_table_with_simple_usage}" id="_Form_TabContainer_Tab1_TabContainer_Tab2_PanelGroup">
                            <Table id="table_CompanyEmployeesReadOnlyButton" iterator="person" collection="{restrictedPeople}" onRowClick="-" selected="{selectedReadOnlyPerson}">
                                <Column label="{$.fh.docs.component.table_name}" value="{person.name}" id="_Form_TabContainer_Tab1_TabContainer_Tab2_PanelGroup_Table_Column1"/>
                                <Column label="{$.fh.docs.component.table_surname}" value="{person.surname}" id="_Form_TabContainer_Tab1_TabContainer_Tab2_PanelGroup_Table_Column2"/>
                                <Column label="{$.fh.docs.component.table_city}" value="{person.city}" id="_Form_TabContainer_Tab1_TabContainer_Tab2_PanelGroup_Table_Column3"/>
                                <Column label="{$.fh.docs.component.table_gender}" value="{person.gender}" id="_Form_TabContainer_Tab1_TabContainer_Tab2_PanelGroup_Table_Column4"/>
                                <Column label="{$.fh.docs.component.table_status}" value="{person.status}" id="_Form_TabContainer_Tab1_TabContainer_Tab2_PanelGroup_Table_Column5"/>
                                <Column label="{$.fh.docs.component.table_actions}" id="_Form_TabContainer_Tab1_TabContainer_Tab2_PanelGroup_Table_Column6">
                                    <Button label="[icon='fa fa-edit']" width="md-6" onClick="onDraw({person})" id="_Form_TabContainer_Tab1_TabContainer_Tab2_PanelGroup_Table_Column6_Button1"/>
                                    <Button label="[icon='fa fa-check']" width="md-6" onClick="onPaint({person})" id="_Form_TabContainer_Tab1_TabContainer_Tab2_PanelGroup_Table_Column6_Button2"/>
                                </Column>
                            </Table>
                            <InputText id="tableExampleCode2" label="{$.fh.docs.component.code}" width="md-12" height="360" rowsCount="11">
<![CDATA[![ESCAPE[<Table id="table_CompanyEmployeesReadOnlyButton" label="Company employees" iterator="person" collection="{restrictedPeople}" onRowClick="-" selected="{selectedReadOnlyPerson}">
    <Column label="Name" value="{person.name}" />
    <Column label="Surname" value="{person.surname}" />
    <Column label="City" value="{person.city}" />
    <Column label="Gender" value="{person.gender}" />
    <Column label="Status" value="{person.status}" />
    <Column label="Actions">
        <Button label="[icon='fa fa-edit']" width="md-6" onClick="onDraw({person})"/>
        <Button label="[icon='fa fa-check']" width="md-6" onClick="onPaint({person})"/>
    </Column>
</Table>]]]]>
                            </InputText>
                            <InputText id="tableExampleJavaCode" label="{$.fh.docs.component.java}" width="md-12" rowsCount="6" value="@Action public void onDraw(ViewEvent viewEvent) \{  FormElement formElement = viewEvent.getSourceObject();  TableElement tableElement = (TableElement) formElement.getForm().getModel();  Person selectedRowOfPerson = tableElement.getPeople().get(ComponentsUtils.getIterationIndex(formElement).get()); \}"/>
                            <InputText id="tableExampleJavaCode2" label="{$.fh.docs.component.java}" width="md-12" rowsCount="6" value="@Action public void onPaint(ViewEvent viewEvent) \{  FormElement formElement = viewEvent.getSourceObject();  TableElement tableElement = (TableElement) formElement.getForm().getModel();  Person selectedRowOfPerson = tableElement.getPeople().get(ComponentsUtils.getIterationIndex(formElement).get()); \}"/>
                        </PanelGroup>
                    </Tab>
                    <Tab label="{$.fh.docs.component.table_included_form_components}" id="_Form_TabContainer_Tab1_TabContainer_Tab3">
                        <PanelGroup width="md-12" label="{$.fh.docs.component.table_table_with_simple_usage}" id="_Form_TabContainer_Tab1_TabContainer_Tab3_PanelGroup1">
                            <Table id="table_CompanyEmployeesReadOnly" label="{$.fh.docs.component.table_company_employees}" iterator="person" collection="{restrictedPeople}" onRowClick="-">
                                <Column label="{$.fh.docs.component.table_name}" value="{person.name}" id="_Form_TabContainer_Tab1_TabContainer_Tab3_PanelGroup1_Table_Column1"/>
                                <Column label="{$.fh.docs.component.table_surname}" value="{person.surname}" id="_Form_TabContainer_Tab1_TabContainer_Tab3_PanelGroup1_Table_Column2"/>
                                <Column label="{$.fh.docs.component.table_city}" value="{person.city}" id="_Form_TabContainer_Tab1_TabContainer_Tab3_PanelGroup1_Table_Column3"/>
                                <Column label="{$.fh.docs.component.table_gender}" value="{person.gender}" id="_Form_TabContainer_Tab1_TabContainer_Tab3_PanelGroup1_Table_Column4"/>
                                <Column label="{$.fh.docs.component.table_status}" value="{person.status}" id="_Form_TabContainer_Tab1_TabContainer_Tab3_PanelGroup1_Table_Column5"/>
                            </Table>
                            <InputText id="tableExampleCode3" label="{$.fh.docs.component.code}" width="md-12" height="230" rowsCount="7">
<![CDATA[![ESCAPE[<Table id="table_CompanyEmployeesReadOnly" label="Company employees" iterator="person" collection="{restrictedPeople}" onRowClick="-">
    <Column label="Name" value="{person.name}" />
    <Column label="Surname" value="{person.surname}" />
    <Column label="City" value="{person.city}" />
    <Column label="Gender" value="{person.gender}" />
    <Column label="Status" value="{person.status}" />
</Table>]]]]>
                            </InputText>
                        </PanelGroup>
                        <PanelGroup width="md-12" label="{$.fh.docs.component.table_table_with_nested_and_binded_form_components_columns}" id="_Form_TabContainer_Tab1_TabContainer_Tab3_PanelGroup2">
                            <Table id="table_CompanyEmployeesEditable" label="{$.fh.docs.component.table_company_employees}" iterator="person" collection="{restrictedPeople}" onRowClick="-" selected="{selectedPerson}">
                                <Column label="{$.fh.docs.component.table_no}" value="{person$rowNo}" width="5" id="_Form_TabContainer_Tab1_TabContainer_Tab3_PanelGroup2_Table_Column1"/>
                                <Column label="{companyEmployeesEditableTableName}" id="_Form_TabContainer_Tab1_TabContainer_Tab3_PanelGroup2_Table_Column2">
                                    <InputText width="md-12" value="{person.name}" onInput="-" id="_Form_TabContainer_Tab1_TabContainer_Tab3_PanelGroup2_Table_Column2_InputText"/>
                                </Column>
                                <Column label="{companyEmployeesEditableTableSurname}" id="_Form_TabContainer_Tab1_TabContainer_Tab3_PanelGroup2_Table_Column3">
                                    <InputText width="md-12" value="{person.surname}" onInput="-" id="_Form_TabContainer_Tab1_TabContainer_Tab3_PanelGroup2_Table_Column3_InputText"/>
                                </Column>
                                <Column label="{companyEmployeesEditableTableCity}" id="_Form_TabContainer_Tab1_TabContainer_Tab3_PanelGroup2_Table_Column4">
                                    <OutputLabel width="md-12" value="{person.city}" id="_Form_TabContainer_Tab1_TabContainer_Tab3_PanelGroup2_Table_Column4_OutputLabel"/>
                                </Column>
                                <Column label="{companyEmployeesEditableTableGender}" value="{person.gender}" id="_Form_TabContainer_Tab1_TabContainer_Tab3_PanelGroup2_Table_Column5"/>
                                <Column label="{companyEmployeesEditableTableStatus}" value="{person.status}" id="_Form_TabContainer_Tab1_TabContainer_Tab3_PanelGroup2_Table_Column6"/>
                                <Column label="{companyEmployeesEditableTableCitizenship}" id="_Form_TabContainer_Tab1_TabContainer_Tab3_PanelGroup2_Table_Column7">
                                    <RadioOptionsGroup width="md-12" values="Poland|German|UK|USA|France" value="{person.citizenship}" id="_Form_TabContainer_Tab1_TabContainer_Tab3_PanelGroup2_Table_Column7_RadioOptionsGroup"/>
                                </Column>
                                <Column label="{companyEmployeesEditableTableDrivingLicense}" id="_Form_TabContainer_Tab1_TabContainer_Tab3_PanelGroup2_Table_Column8">
                                    <SelectOneMenu width="md-12" values="A|B|C|D|E|F" value="{person.drivingLicenseCategory}" id="_Form_TabContainer_Tab1_TabContainer_Tab3_PanelGroup2_Table_Column8_SelectOneMenu"/>
                                </Column>
                                <Column label="{companyEmployeesEditableTableName} + {companyEmployeesEditableTableSurname}" value="{person.name} {person.surname} " id="_Form_TabContainer_Tab1_TabContainer_Tab3_PanelGroup2_Table_Column9"/>
                                <Column label="{companyEmployeesEditableTableBirthDate}" width="15" id="_Form_TabContainer_Tab1_TabContainer_Tab3_PanelGroup2_Table_Column10">
                                    <InputDate width="md-12" value="{person.birthDate}" id="_Form_TabContainer_Tab1_TabContainer_Tab3_PanelGroup2_Table_Column10_InputDate"/>
                                </Column>
                            </Table>
                            <Group id="_Form_TabContainer_Tab1_TabContainer_Tab3_PanelGroup2_Group">
                                <Spacer width="md-9" id="_Form_TabContainer_Tab1_TabContainer_Tab3_PanelGroup2_Group_Spacer"/>
                                <Button id="pAdd" width="md-1" label="{$.fh.docs.component.table_add_new}" onClick="addNewPersonWindow"/>
                                <Button id="pEdit" width="md-1" label="{$.fh.docs.component.table_edit}" onClick="editPersonWindow(selectedPerson)"/>
                                <Button id="pDelete" width="md-1" label="{$.fh.docs.component.table_delete}" onClick="removePerson(selectedPerson)"/>
                            </Group>
                            <InputText id="tableExampleCode4" label="{$.fh.docs.component.code}" width="md-12" height="550" rowsCount="25">
<![CDATA[![ESCAPE[<Table id="table_CompanyEmployeesEditable" label="Company employees" iterator="person" collection="{restrictedPeople}" onRowClick="-" selected="{selectedPerson}">
<Column label="No" value="{person$rowNo}" width="5"/>
<Column label="{companyEmployeesEditableTableName}">
    <InputText value="{person.name}" onInput="-" />
</Column>
<Column label="{companyEmployeesEditableTableSurname}">
    <InputText value="{person.surname}" onInput="-" />
</Column>
<Column label="{companyEmployeesEditableTableCity}">
    <OutputLabel value="{person.city}"></OutputLabel>
</Column>
<Column label="{companyEmployeesEditableTableGender}" value="{person.gender}" />
<Column label="{companyEmployeesEditableTableStatus}" value="{person.status}" />
<Column label="{companyEmployeesEditableTableCitizenship}">
    <RadioOptionsGroup values="Poland|German|UK|USA|France" value="{person.citizenship}"/>
</Column>
<Column label="{companyEmployeesEditableTableDrivingLicense}">
    <SelectOneMenu values="A|B|C|D|E|F" value="{person.drivingLicenseCategory}"/>
</Column>
<Column label="{companyEmployeesEditableTableName} + {companyEmployeesEditableTableSurname}">
    <OutputLabel value="{person.name}"></OutputLabel>
    <OutputLabel value="{person.surname}"></OutputLabel>
</Column>
<Column label="{companyEmployeesEditableTableBirthDate}" width="15">
    <InputDate value="{person.birthDate}"></InputDate>
</Column>
</Table>
<Group>
<Spacer width="md-9"/>
<Button id="pAdd" width="md-1" label="Add new" onClick="addNewPersonWindow"/>
<Button id="pEdit" width="md-1" label="Edit" onClick="editPersonWindow(selectedPerson)"/>
<Button id="pDelete" width="md-1" label="Delete" onClick="removePerson(selectedPerson)"/>
</Group>]]]]>
                            </InputText>
                        </PanelGroup>

                        <PanelGroup width="md-12" label="{$.fh.docs.component.table_binded_form_components_with_form_components_nested_in_table}" id="_Form_TabContainer_Tab1_TabContainer_Tab3_PanelGroup3">
                            <OutputLabel width="md-12" value="{$.fh.docs.component.table_in_below_examples}" id="_Form_TabContainer_Tab1_TabContainer_Tab3_PanelGroup3_OutputLabel"/>
                            <InputText width="md-12" value="{restrictedPeople[0].name}" onInput="-" id="_Form_TabContainer_Tab1_TabContainer_Tab3_PanelGroup3_InputText1"/>
                            <InputText width="md-12" value="{restrictedPeople[0].surname}" id="_Form_TabContainer_Tab1_TabContainer_Tab3_PanelGroup3_InputText2"/>
                            <InputText width="md-12" value="{companyEmployeesEditableTableName}" onInput="-" id="_Form_TabContainer_Tab1_TabContainer_Tab3_PanelGroup3_InputText3"/>
                            <SelectOneMenu width="md-12" values="A|B|C|D|E|F" value="{restrictedPeople[1].drivingLicenseCategory}" onChange="-" id="_Form_TabContainer_Tab1_TabContainer_Tab3_PanelGroup3_SelectOneMenu"/>

                            <InputText id="tableExampleCode5" label="{$.fh.docs.component.code}" width="md-12" height="200" rowsCount="6">
<![CDATA[![ESCAPE[<OutputLabel value="In below examples, binding of first, third InputText and SelectOneMenu to the above table form components is working fine, since these form components contain events for informing about value changes. Second InputText is not working, because has no added event, so form components in table above are not informed about changes." />
<InputText value="{restrictedPeople[0].name}" onInput="-" />
<InputText value="{restrictedPeople[0].surname}"  />
<InputText value="{companyEmployeesEditableTableName}" onInput="-" />
<SelectOneMenu values="A|B|C|D|E|F" value="{restrictedPeople[1].drivingLicenseCategory}" onChange="-" />]]]]>
                            </InputText>
                        </PanelGroup>
                    </Tab>
                   <Tab label="{$.fh.docs.component.table_grouping_rows}" id="_Form_TabContainer_Tab1_TabContainer_Tab4">
                        <PanelGroup width="md-12" label="{$.fh.docs.component.table_table_with_grouping_rows}" id="_Form_TabContainer_Tab1_TabContainer_Tab4_PanelGroup">
                            <Table id="table_TvSeriesGrouping" label="{$.fh.docs.component.table_tv_series}" iterator="serial"
                                   collection="{tvSeries}" selected="{selectedSerial}"
                                   onRowClick="-">

                                <Iterator id="season" collection="{serial.seasons}"/>
                                <Iterator id="episode" collection="{season.episodes}"/>
                                <Iterator id="actor" collection="{episode.actors}"/>
                                <Column label="{ordinalNumber}" value="{serial$rowNo}" width="8"/>
                                <Column label="{tvSeriesNames}" value="{serial.name}" width="20"/>
                                <Column label="{season}" width="60">
                                    <Column label="{seasonNumber}" value="{season$rowNo}" iterationRef="season"/>
                                    <Column label="{episode}">
                                        <Column label="{episodeTitle}" value="{episode.name}" iterationRef="episode" />
                                        <Column label="{episodeDetails}">
                                            <Column label="{episodeActors}" iterationRef="actor">
                                                <OutputLabel width="md-12" value="{actor.name} {actor.surname}"/>
                                            </Column>
                                            <Column id="episodeDuration" label="{episodeDuration}" iterationRef="episode">
                                                <InputText width="md-12" value="{episode.duration}"/>
                                            </Column>
                                        </Column>
                                        <Column label="{episodeActions}" iterationRef="episode">
                                            <Button label="{episodeRemove}" onClick="removeEpisode(season,episode)" width="md-12"/>
                                            <Button label="{episodeDescription}" onClick="showDescription(episode)" width="md-12"/>
                                        </Column>
                                    </Column>
                                    <Column label="{seasonActions}" iterationRef="season">
                                        <Button label="{removeSeason}" onClick="removeSeason(serial,season)" width="md-12"/>
                                    </Column>
                                </Column>
                                <Column label="{countryName}" value="{serial.country.name}" width="12"/>
                            </Table>

                            <InputText id="tableExampleCode7" label="{$.fh.docs.component.code}" width="md-12" height="1030" rowsCount="25">
<![CDATA[![ESCAPE[<Table id="table_TvSeriesGrouping" label="TV Series" iterator="serial"
   collection="{tvSeries}" selected="{selectedSerial}"
   onRowClick="-">

<Iterator id="season" collection="{serial.seasons}"/>
<Iterator id="episode" collection="{season.episodes}"/>
<Iterator id="actor" collection="{episode.actors}"/>
<Column label="{ordinalNumber}" value="{serial$rowNo}" width="8"/>
<Column label="{tvSeriesNames}" value="{serial.name}" width="20"/>
<Column label="{season}" width="60">
    <Column label="{seasonNumber}" value="{season$rowNo}" iterationRef="season"/>
    <Column label="{episode}">
        <Column label="{episodeTitle}" value="{episode.name}" iterationRef="episode" />
        <Column label="{episodeDetails}">
            <Column label="{episodeActors}" iterationRef="actor">
                <OutputLabel value="{actor.name} {actor.surname}"/>
            </Column>
            <Column id="episodeDuration" label="{episodeDuration}" iterationRef="episode">
                <InputText value="{episode.duration}"/>
            </Column>
        </Column>
        <Column label="{episodeActions}" iterationRef="episode">
            <Button label="{episodeRemove}" onClick="removeEpisode(season,episode)" width="md-12"/>
            <Button label="{episodeDescription}" onClick="showDescription(episode)" width="md-12"/>
        </Column>
    </Column>
    <Column label="{seasonActions}" iterationRef="season">
        <Button label="{removeSeason}" onClick="removeSeason(serial,season)" width="md-12"/>
    </Column>
</Column>
<Column label="{countryName}" value="{serial.country.name}" width="12"/>
</Table>]]]]>
                            </InputText>
                        </PanelGroup>
                    </Tab>
                    <Tab label="{$.fh.docs.component.table_colored_rows}" id="_Form_TabContainer_Tab1_TabContainer_Tab5">
                        <PanelGroup width="md-12" label="{$.fh.docs.component.table_table_with_colored_rows}" id="_Form_TabContainer_Tab1_TabContainer_Tab5_PanelGroup1">
                            <Table id="table_ColoredCompanyEmployeesReadOnlyDisplay" label="{$.fh.docs.component.table_company_employees}" iterator="person" collection="{restrictedPeople}" onRowClick="-" minRows="5" rowStylesMap="{coloredRestrictedPeople}">
                                <Column label="{$.fh.docs.component.table_name}" value="{person.name}" id="_Form_TabContainer_Tab1_TabContainer_Tab5_PanelGroup1_Table_Column1"/>
                                <Column label="{$.fh.docs.component.table_surname}" value="{person.surname}" id="_Form_TabContainer_Tab1_TabContainer_Tab5_PanelGroup1_Table_Column2"/>
                                <Column label="{$.fh.docs.component.table_city}" value="{person.city}" id="_Form_TabContainer_Tab1_TabContainer_Tab5_PanelGroup1_Table_Column3"/>
                                <Column label="{$.fh.docs.component.table_gender}" value="{person.gender}" id="_Form_TabContainer_Tab1_TabContainer_Tab5_PanelGroup1_Table_Column4"/>
                                <Column label="{$.fh.docs.component.table_status}" value="{person.status}" id="_Form_TabContainer_Tab1_TabContainer_Tab5_PanelGroup1_Table_Column5"/>
                                <Column label="{$.fh.docs.component.table_action}" id="_Form_TabContainer_Tab1_TabContainer_Tab5_PanelGroup1_Table_Column6">
                                    <Button label="{$.fh.docs.component.table_reset_color}" width="md-12" onClick="resetColoredRestrictedPerson(person)" id="_Form_TabContainer_Tab1_TabContainer_Tab5_PanelGroup1_Table_Column6_Button"/>
                                </Column>
                            </Table>
                            <Button label="{$.fh.docs.component.table_reset_all_colors}" onClick="resetColoredRestrictedPeople()" id="_Form_TabContainer_Tab1_TabContainer_Tab5_PanelGroup1_Button"/>

                            <InputText id="tableExampleCode11" label="{$.fh.docs.component.code}" width="md-12" height="230" rowsCount="7">
<![CDATA[![ESCAPE[<Table id="table_ColoredCompanyEmployeesReadOnlyDisplay" label="Company employees" iterator="person" collection="{restrictedPeople}" onRowClick="-" minRows="5" rowStylesMap="{coloredRestrictedPeople}">
    <Column label="Name" value="{person.name}" />
    <Column label="Surname" value="{person.surname}" />
    <Column label="City" value="{person.city}" />
    <Column label="Gender" value="{person.gender}" />
    <Column label="Status" value="{person.status}" />
    <Column label="{$.fh.docs.component.table_action}">
        <Button label="{$.fh.docs.component.table_reset_color}" width="md-12" onClick="resetColoredRestrictedPerson(person)"/>
    </Column>
</Table>]]]]>
                            </InputText>

                            <InputText id="tableExampleJavaCode3" label="{$.fh.docs.component.java}" width="md-12" height="500" rowsCount="18" value="private static final String GREEN = &quot;#B0EC78&quot;;  private static final String GRAY = &quot;#CCCCCC&quot;;   public static Map&lt;Person, String&gt; findAllColoredRestricted() \{     coloredRestrictedPeople = new HashMap&lt;&gt;();     if (restrictedPeople != null) \{         for (Person person : restrictedPeople) \{             String colorValue = null;             if (&quot;Active&quot;.equals(person.getStatus())) \{                 colorValue = GREEN;             \} else if (&quot;Inactive&quot;.equals(person.getStatus())) \{                 colorValue = GRAY;             \}              coloredRestrictedPeople.put(person, colorValue);         }     }      return coloredRestrictedPeople; }"/>
                        </PanelGroup>

                        <PanelGroup width="md-12" label="{$.fh.docs.component.table_table_with_colored_grouping_rows}" id="_Form_TabContainer_Tab1_TabContainer_Tab5_PanelGroup2">
                            <Table id="table_ColoredTvSeriesGrouping" label="{$.fh.docs.component.table_tv_series}" iterator="serial"
                                   collection="{tvSeries}" selected="{selectedSerial}"
                                   rowStylesMap="{tvSeriesColored}">
                                <Iterator id="season" collection="{serial.seasons}"/>
                                <Iterator id="episode" collection="{season.episodes}"/>
                                <Iterator id="actor" collection="{episode.actors}"/>
                                <Column label="{ordinalNumber}" value="{serial$rowNo}" width="8"/>/>
                                <Column label="{tvSeriesNames}" value="{serial.name}" width="20"/>
                                <Column label="{season}" width="60">
                                    <Column label="{seasonNumber}" value="{season$rowNo}" iterationRef="season"/>
                                    <Column label="{episode}">
                                        <Column label="{episodeTitle}" value="{episode.name}" iterationRef="episode" />
                                        <Column label="{episodeDetails}">
                                            <Column label="{episodeActors}" iterationRef="actor">
                                                <OutputLabel width="md-12" value="{actor.name} {actor.surname}"/>
                                            </Column>
                                            <Column id="coloredEpisodeDuration" label="{episodeDuration}" iterationRef="episode">
                                                <InputText width="md-12" value="{episode.duration}"/>
                                            </Column>
                                        </Column>
                                    </Column>
                                    <Column label="{seasonActions}" iterationRef="season">
                                        <Button label="{removeSeason}" onClick="removeSeason(serial,season)" width="md-12"/>
                                    </Column>
                                    <Column label="{episodeActions}" iterationRef="episode">
                                        <Button label="{episodeRemove}" onClick="removeEpisode(season,episode)" width="md-12"/>
                                        <Button label="{episodeDescription}" onClick="showDescription(episode)" width="md-12"/>
                                    </Column>
                                </Column>
                                <Column label="{countryName}" value="{serial.country.name}" width="12"/>
                            </Table>

                            <InputText id="tableExampleCode10" label="{$.fh.docs.component.code}" width="md-12" height="500" rowsCount="18">
<![CDATA[![ESCAPE[<Table id="table_ColoredTvSeriesGrouping" label="TV Series" iterator="serial"
   collection="{tvSeries}" selected="{selectedSerial}"
   rowStylesMap="{tvSeriesColored}">
    <Iterator id="season" collection="{serial.seasons}"/>
    <Iterator id="episode" collection="{season.episodes}"/>
    <Iterator id="actor" collection="{episode.actors}"/>
    <Column label="{ordinalNumber}" value="{serial$rowNo}" width="8"/>
    <Column label="{tvSeriesNames}" value="{serial.name}" width="20"/>
    <Column label="{season}" width="60">
        <Column label="{seasonNumber}" value="{season$rowNo}" iterationRef="season"/>
        <Column label="{episode}">
            <Column label="{episodeTitle}" value="{episode.name}" iterationRef="episode" />
            <Column label="{episodeDetails}">
                <Column label="{episodeActors}" iterationRef="actor">
                    <OutputLabel value="{actor.name} {actor.surname}"/>
                </Column>
                <Column id="coloredEpisodeDuration" label="{episodeDuration}" iterationRef="episode">
                    <InputText value="{episode.duration}"/>
                </Column>
            </Column>
        </Column>
        <Column label="{seasonActions}" iterationRef="season">
            <Button label="{removeSeason}" onClick="removeSeason(serial,season)" width="md-12"/>
        </Column>
        <Column label="{episodeActions}" iterationRef="episode">
            <Button label="{episodeRemove}" onClick="removeEpisode(season,episode)" width="md-12"/>
            <Button label="{episodeDescription}" onClick="showDescription(episode)" width="md-12"/>
        </Column>
    </Column>
    <Column label="{countryName}" value="{serial.country.name}" width="12"/>
</Table>]]]]>
                            </InputText>

                            <InputText id="tableExampleJavaCode4" label="{$.fh.docs.component.java}" width="md-12" height="500" rowsCount="15" value="private static final String[] COLORS = new String[]\{         &quot;#9ddee3&quot;, &quot;#93d8dd&quot;, &quot;#8ad1d6&quot;, &quot;#82cbd0&quot;, &quot;#71c0c5&quot;, &quot;#6ab6ba&quot;, &quot;#62afb4&quot;, &quot;#56a2a7&quot;, &quot;#4c989c&quot; \};    @Override public Map&lt;?, String&gt; findAllColored() \{     List&lt;TvSeries&gt; tvSeries = findAll();     Map&lt;TvSeries, String&gt; tvSeriesMap = new HashMap&lt;&gt;();      for (int i = 0; i &lt; tvSeries.size(); i++) \{         tvSeriesMap.put(tvSeries.get(i), COLORS[i]);     \}      return tvSeriesMap; }"/>
                        </PanelGroup>
                    </Tab>
                    <Tab label="{$.fh.docs.component.table_wide_table_with_scroll}" id="_Form_TabContainer_Tab1_TabContainer_Tab6">
                        <PanelGroup width="md-12" label="{$.fh.docs.component.table_table_with_scroll_on_smaller_displays}" id="_Form_TabContainer_Tab1_TabContainer_Tab6_PanelGroup1">
                            <Table id="table_ScrollingWithFewColumns" minRows="5" horizontalScrolling="true">
                                <Column label="{$.fh.docs.component.table_column1}" id="_Form_TabContainer_Tab1_TabContainer_Tab6_PanelGroup1_Table_Column1"/>
                                <Column label="{$.fh.docs.component.table_column2}" id="_Form_TabContainer_Tab1_TabContainer_Tab6_PanelGroup1_Table_Column2"/>
                                <Column label="{$.fh.docs.component.table_column3}" id="_Form_TabContainer_Tab1_TabContainer_Tab6_PanelGroup1_Table_Column3"/>
                                <Column label="{$.fh.docs.component.table_column4}" id="_Form_TabContainer_Tab1_TabContainer_Tab6_PanelGroup1_Table_Column4"/>
                            </Table>

                            <InputText id="tableExampleCode12" label="{$.fh.docs.component.code}" width="md-12" height="200" rowsCount="6">
<![CDATA[![ESCAPE[<Table id="table_ScrollingWithFewColumns" minRows="5" horizontalScrolling="true">
    <Column label="This is the 1st column" />
    <Column label="This is the 2nd column" />
    <Column label="This is the 3rd column" />
    <Column label="This is the 4th column" />
</Table>
]]]]>
                            </InputText>
                        </PanelGroup>

                        <PanelGroup width="md-12" label="{$.fh.docs.component.table_table_with_scroll}" id="_Form_TabContainer_Tab1_TabContainer_Tab6_PanelGroup2">
                            <Table id="table_ScrollingWithManyColumns" minRows="5" horizontalScrolling="true">
                                <Column label="{$.fh.docs.component.table_wide_column_1}" id="_Form_TabContainer_Tab1_TabContainer_Tab6_PanelGroup2_Table_Column1"/>
                                <Column label="{$.fh.docs.component.table_wide_column_2}" id="_Form_TabContainer_Tab1_TabContainer_Tab6_PanelGroup2_Table_Column2"/>
                                <Column label="{$.fh.docs.component.table_wide_column_3}" id="_Form_TabContainer_Tab1_TabContainer_Tab6_PanelGroup2_Table_Column3"/>
                                <Column label="{$.fh.docs.component.table_wide_column_4}" id="_Form_TabContainer_Tab1_TabContainer_Tab6_PanelGroup2_Table_Column4"/>
                                <Column label="{$.fh.docs.component.table_wide_column_5}" id="_Form_TabContainer_Tab1_TabContainer_Tab6_PanelGroup2_Table_Column5"/>
                                <Column label="{$.fh.docs.component.table_wide_column_6}" id="_Form_TabContainer_Tab1_TabContainer_Tab6_PanelGroup2_Table_Column6"/>
                                <Column label="{$.fh.docs.component.table_wide_column_7}" id="_Form_TabContainer_Tab1_TabContainer_Tab6_PanelGroup2_Table_Column7"/>
                                <Column label="{$.fh.docs.component.table_wide_column_8}" id="_Form_TabContainer_Tab1_TabContainer_Tab6_PanelGroup2_Table_Column8"/>
                                <Column label="{$.fh.docs.component.table_wide_column_9}" id="_Form_TabContainer_Tab1_TabContainer_Tab6_PanelGroup2_Table_Column9"/>
                                <Column label="{$.fh.docs.component.table_wide_column_10}" id="_Form_TabContainer_Tab1_TabContainer_Tab6_PanelGroup2_Table_Column10"/>
                            </Table>

                            <InputText id="tableExampleCode13" label="{$.fh.docs.component.code}" width="md-12" height="390" rowsCount="12">
<![CDATA[![ESCAPE[<Table id="table_ScrollingWithManyColumns" minRows="5" horizontalScrolling="true">
    <Column label="The_1st_column_in_wide_table" />
    <Column label="The_2nd_column_in_wide_table" />
    <Column label="The_3rd_column_in_wide_table" />
    <Column label="The_4th_column_in_wide_table" />
    <Column label="The_5th_column_in_wide_table" />
    <Column label="The_6th_column_in_wide_table" />
    <Column label="The_7th_column_in_wide_table" />
    <Column label="The_8th_column_in_wide_table" />
    <Column label="The_9th_column_in_wide_table" />
    <Column label="The_10th_column_in_wide_table" />
</Table>]]]]>
                            </InputText>
                        </PanelGroup>
                        <PanelGroup width="md-12" label="{$.fh.docs.component.table_table_with_horizontal_and_vertical_scroll}" id="_Form_TabContainer_Tab1_TabContainer_Tab6_PanelGroup3">
                            <Table fixedHeader="true" id="table_VerticalAndHorizontalScrolling" minRows="10" height="250px" horizontalScrolling="true">
                                <Column label="{$.fh.docs.component.table_wide_column_1}" id="_Form_TabContainer_Tab1_TabContainer_Tab6_PanelGroup3_Table_Column1"/>
                                <Column label="{$.fh.docs.component.table_wide_column_2}" id="_Form_TabContainer_Tab1_TabContainer_Tab6_PanelGroup3_Table_Column2"/>
                                <Column label="{$.fh.docs.component.table_wide_column_3}" id="_Form_TabContainer_Tab1_TabContainer_Tab6_PanelGroup3_Table_Column3"/>
                                <Column label="{$.fh.docs.component.table_wide_column_4}" id="_Form_TabContainer_Tab1_TabContainer_Tab6_PanelGroup3_Table_Column4"/>
                                <Column label="{$.fh.docs.component.table_wide_column_5}" id="_Form_TabContainer_Tab1_TabContainer_Tab6_PanelGroup3_Table_Column5"/>
                                <Column label="{$.fh.docs.component.table_wide_column_6}" id="_Form_TabContainer_Tab1_TabContainer_Tab6_PanelGroup3_Table_Column6"/>
                                <Column label="{$.fh.docs.component.table_wide_column_7}" id="_Form_TabContainer_Tab1_TabContainer_Tab6_PanelGroup3_Table_Column7"/>
                                <Column label="{$.fh.docs.component.table_wide_column_8}" id="_Form_TabContainer_Tab1_TabContainer_Tab6_PanelGroup3_Table_Column8"/>
                                <Column label="{$.fh.docs.component.table_wide_column_9}" id="_Form_TabContainer_Tab1_TabContainer_Tab6_PanelGroup3_Table_Column9"/>
                                <Column label="{$.fh.docs.component.table_wide_column_10}" id="_Form_TabContainer_Tab1_TabContainer_Tab6_PanelGroup3_Table_Column10"/>
                            </Table>

                            <InputText id="tableExampleCode15" label="{$.fh.docs.component.code}" width="md-12" height="390" rowsCount="12">
                                <![CDATA[![ESCAPE[<Table fixedHeader="true" id="table_VerticalAndHorizontalScrolling" minRows="10" height="250px" horizontalScrolling="true">
    <Column label="The_1st_column_in_wide_table" />
    <Column label="The_2nd_column_in_wide_table" />
    <Column label="The_3rd_column_in_wide_table" />
    <Column label="The_4th_column_in_wide_table" />
    <Column label="The_5th_column_in_wide_table" />
    <Column label="The_6th_column_in_wide_table" />
    <Column label="The_7th_column_in_wide_table" />
    <Column label="The_8th_column_in_wide_table" />
    <Column label="The_9th_column_in_wide_table" />
    <Column label="The_10th_column_in_wide_table" />
</Table>]]]]>
                            </InputText>
                        </PanelGroup>
                        <PanelGroup width="md-12" label="{$.fh.docs.component.table_table_with_synchronized_scroll}" id="_Form_TabContainer_Tab1_TabContainer_Tab6_PanelGroup4">
                            <Table id="table_ScrollingWithManyColumns3" synchronizeScrolling="table_ScrollingWithManyColumns2" minRows="5" horizontalScrolling="true">
                                <Column label="The_1st_column_in_wide_table" id="_Form_TabContainer_Tab1_TabContainer_Tab6_PanelGroup4_Table1_Column1"/>
                                <Column label="The_2nd_column_in_wide_table" id="_Form_TabContainer_Tab1_TabContainer_Tab6_PanelGroup4_Table1_Column2"/>
                                <Column label="The_3rd_column_in_wide_table" id="_Form_TabContainer_Tab1_TabContainer_Tab6_PanelGroup4_Table1_Column3"/>
                                <Column label="The_4th_column_in_wide_table" id="_Form_TabContainer_Tab1_TabContainer_Tab6_PanelGroup4_Table1_Column4"/>
                                <Column label="The_5th_column_in_wide_table" id="_Form_TabContainer_Tab1_TabContainer_Tab6_PanelGroup4_Table1_Column5"/>
                                <Column label="The_6th_column_in_wide_table" id="_Form_TabContainer_Tab1_TabContainer_Tab6_PanelGroup4_Table1_Column6"/>
                                <Column label="The_7th_column_in_wide_table" id="_Form_TabContainer_Tab1_TabContainer_Tab6_PanelGroup4_Table1_Column7"/>
                                <Column label="The_8th_column_in_wide_table" id="_Form_TabContainer_Tab1_TabContainer_Tab6_PanelGroup4_Table1_Column8"/>
                                <Column label="The_9th_column_in_wide_table" id="_Form_TabContainer_Tab1_TabContainer_Tab6_PanelGroup4_Table1_Column9"/>
                                <Column label="The_10th_column_in_wide_table" id="_Form_TabContainer_Tab1_TabContainer_Tab6_PanelGroup4_Table1_Column10"/>
                            </Table>
                            <Table id="table_ScrollingWithManyColumns2" synchronizeScrolling="table_ScrollingWithManyColumns3" minRows="5" horizontalScrolling="true">
                                <Column label="The_1st_column_in_wide_table" id="_Form_TabContainer_Tab1_TabContainer_Tab6_PanelGroup4_Table2_Column1"/>
                                <Column label="The_2nd_column_in_wide_table" id="_Form_TabContainer_Tab1_TabContainer_Tab6_PanelGroup4_Table2_Column2"/>
                                <Column label="The_3rd_column_in_wide_table" id="_Form_TabContainer_Tab1_TabContainer_Tab6_PanelGroup4_Table2_Column3"/>
                                <Column label="The_4th_column_in_wide_table" id="_Form_TabContainer_Tab1_TabContainer_Tab6_PanelGroup4_Table2_Column4"/>
                                <Column label="The_5th_column_in_wide_table" id="_Form_TabContainer_Tab1_TabContainer_Tab6_PanelGroup4_Table2_Column5"/>
                                <Column label="The_6th_column_in_wide_table" id="_Form_TabContainer_Tab1_TabContainer_Tab6_PanelGroup4_Table2_Column6"/>
                                <Column label="The_7th_column_in_wide_table" id="_Form_TabContainer_Tab1_TabContainer_Tab6_PanelGroup4_Table2_Column7"/>
                                <Column label="The_8th_column_in_wide_table" id="_Form_TabContainer_Tab1_TabContainer_Tab6_PanelGroup4_Table2_Column8"/>
                                <Column label="The_9th_column_in_wide_table" id="_Form_TabContainer_Tab1_TabContainer_Tab6_PanelGroup4_Table2_Column9"/>
                                <Column label="The_10th_column_in_wide_table" id="_Form_TabContainer_Tab1_TabContainer_Tab6_PanelGroup4_Table2_Column10"/>
                            </Table>
                            <InputText id="tableExampleCode151" label="{$.fh.docs.component.code}" width="md-12" height="390" rowsCount="12">
                                <![CDATA[![ESCAPE[<Table id="table_ScrollingWithManyColumns3" synchronizeScrolling="table_ScrollingWithManyColumns2" minRows="5" horizontalScrolling="true">
                                <Column label="The_1st_column_in_wide_table" />
                                <Column label="The_2nd_column_in_wide_table" />
                                <Column label="The_3rd_column_in_wide_table" />
                                <Column label="The_4th_column_in_wide_table" />
                                <Column label="The_5th_column_in_wide_table" />
                                <Column label="The_6th_column_in_wide_table" />
                                <Column label="The_7th_column_in_wide_table" />
                                <Column label="The_8th_column_in_wide_table" />
                                <Column label="The_9th_column_in_wide_table" />
                                <Column label="The_10th_column_in_wide_table" />
                            </Table>
                            <Table id="table_ScrollingWithManyColumns2" synchronizeScrolling="table_ScrollingWithManyColumns3" minRows="5" horizontalScrolling="true">
                                <Column label="The_1st_column_in_wide_table" />
                                <Column label="The_2nd_column_in_wide_table" />
                                <Column label="The_3rd_column_in_wide_table" />
                                <Column label="The_4th_column_in_wide_table" />
                                <Column label="The_5th_column_in_wide_table" />
                                <Column label="The_6th_column_in_wide_table" />
                                <Column label="The_7th_column_in_wide_table" />
                                <Column label="The_8th_column_in_wide_table" />
                                <Column label="The_9th_column_in_wide_table" />
                                <Column label="The_10th_column_in_wide_table" />
                            </Table>]]]]>
                            </InputText>
                        </PanelGroup>

                    </Tab>
                    <Tab label="{$.fh.docs.component.table_prototyping_with_inline_data}" id="_Form_TabContainer_Tab1_TabContainer_Tab7">
                        <PanelGroup width="md-12" label="{$.fh.docs.component.table_table_with_inline_data_binding}" id="_Form_TabContainer_Tab1_TabContainer_Tab7_PanelGroup">
                            <OutputLabel width="md-12" value="{$.fh.docs.component.table_for_better_prototyping}" id="_Form_TabContainer_Tab1_TabContainer_Tab7_PanelGroup_OutputLabel"/>
                            <Table id="table_inlineData" iterator="row" collection="{RULE.pl.fhframework.core.rules.builtin.CsvRows.csvRows('John;Black;34|Tom;White;67|Florence;Dalton;25')}">
                                <Column label="{$.fh.docs.component.table_first_name}" value="{row.column1}" id="_Form_TabContainer_Tab1_TabContainer_Tab7_PanelGroup_Table_Column1"/>
                                <Column label="{$.fh.docs.component.table_last_name}" value="{row.column2}" id="_Form_TabContainer_Tab1_TabContainer_Tab7_PanelGroup_Table_Column2"/>
                                <Column label="{$.fh.docs.component.table_age}" value="{row.column3}" id="_Form_TabContainer_Tab1_TabContainer_Tab7_PanelGroup_Table_Column3"/>
                            </Table>
                            <InputText id="table_inlineDataCode" label="{$.fh.docs.component.code}" width="md-12" height="170" rowsCount="5">
                                <![CDATA[![ESCAPE[<Table id="table_inlineData" iterator="row" collection="{RULE.pl.fhframework.core.rules.builtin.CsvRows.csvRows('John;Black;34|Tom;White;67|Florence;Dalton;25')}">
    <Column label="First name" value="{row.column1}"/>
    <Column label="Last name" value="{row.column2}"/>
    <Column label="Age" value="{row.column3}"/>
</Table>]]]]>
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
