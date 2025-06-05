<?xml version="1.0" encoding="UTF-8"?>
<Form xmlns="http://fh.asseco.com/form/1.0" container="mainForm" id="documentationHolder" label="{componentName}">
    <AvailabilityConfiguration>
        <ReadOnly>
            tablePagedExampleCode1
        </ReadOnly>
    </AvailabilityConfiguration>
    <OutputLabel width="md-12" value="{description}" id="_Form_OutputLabel"/>
    <Spacer width="md-12" height="25px" id="_Form_Spacer"/>
    <PanelGroup label="Table " id="_Form_PanelGroup">
        <Row elementsHorizontalAlign="LEFT">
            <TabContainer id="_Form_PanelGroup_Row_TabContainer">
                <Tab label="Test dla tabContainer inactive" id="el_okresyUbezpieczenioweTab">
                    <Row elementsHorizontalAlign="LEFT">
                        <PanelGroup label="Test zagnieżdżenia" height="1000px" id="_Form_PanelGroup_Row_TabContainer_Tab_Row_PanelGroup">
                            <Row elementsHorizontalAlign="LEFT">
                                <TableOptimized id="tablePage_CompanyEmployeesReadOnly" width="md-12" tableStripes="show" onRowClick="+" startSize="3" fixedHeader="true" horizontalScrolling="false" ieFocusFixEnabled="true" height="300px" iterator="c" collection="{people}" selectable="true" selected="{selectedElement}" multiselect="true">
                                    <ColumnOptimized width="120px" label="Name" id="_Form_PanelGroup_Row_TabContainer_Tab_Row_PanelGroup_Row_TableOptimized_ColumnOptimized1">
                                        <OutputLabel width="md-12" value="{c.name}" id="_Form_PanelGroup_Row_TabContainer_Tab_Row_PanelGroup_Row_TableOptimized_ColumnOptimized1_OutputLabel"/>
                                    </ColumnOptimized>
                                    <ColumnOptimized label="Okres do 1" id="_Form_PanelGroup_Row_TabContainer_Tab_Row_PanelGroup_Row_TableOptimized_ColumnOptimized2">
                                        <InputDateOptimized width="md-12" iconAlignment="after" value="{c.birthDate}" id="_Form_PanelGroup_Row_TabContainer_Tab_Row_PanelGroup_Row_TableOptimized_ColumnOptimized2_InputDateOptimized"/>
                                    </ColumnOptimized>
                                    <ColumnOptimized label="Okres do 14" id="_Form_PanelGroup_Row_TabContainer_Tab_Row_PanelGroup_Row_TableOptimized_ColumnOptimized3">
                                        <SelectComboMenuOptimized width="md-12" value="{c.country}" displayExpression="{name}" values="{modelCountries}" id="_Form_PanelGroup_Row_TabContainer_Tab_Row_PanelGroup_Row_TableOptimized_ColumnOptimized3_SelectComboMenuOptimized"/>
                                    </ColumnOptimized>
                                    <ColumnOptimized label="Okres do 14" id="_Form_PanelGroup_Row_TabContainer_Tab_Row_PanelGroup_Row_TableOptimized_ColumnOptimized4">
                                        <SelectComboMenuOptimized width="md-12" value="{c.country}" displayExpression="{name}" values="{modelCountries}" id="_Form_PanelGroup_Row_TabContainer_Tab_Row_PanelGroup_Row_TableOptimized_ColumnOptimized4_SelectComboMenuOptimized"/>
                                    </ColumnOptimized>
                                </TableOptimized>

                                <InputText id="tablePagedExampleCode1" label="{$.fh.docs.component.code}" width="md-12" height="230" rowsCount="7">
                                    <![CDATA[![ESCAPE[ <TableOptimized id="tablePage_CompanyEmployeesReadOnly" width="md-12" tableStripes="show" onRowClick="+" startSize="3"
                                                fixedHeader="true" horizontalScrolling="false" ieFocusFixEnabled="true"
                                                height="300px"
                                                iterator="c" collection="{people}" selectable="true" selected="{selectedElement}" multiselect="true"
                                >
                                    <ColumnOptimized width="120px" label="Name" >
                                        <OutputLabel width="md-12" value="{c.name}" />
                                    </ColumnOptimized>
                                    <ColumnOptimized label="Okres do 1">
                                        <InputDateOptimized width="md-12" iconAlignment="after" value="{c.birthDate}" />
                                    </ColumnOptimized>
                                    <ColumnOptimized label="Okres do 14" >
                                        <SelectComboMenuOptimized width="md-12" value="{c.country}" displayExpression="{name}"
                                                         values="{modelCountries}"/>
                                    </ColumnOptimized>
                                    <ColumnOptimized  label="Okres do 14" >
                                        <SelectComboMenuOptimized width="md-12" value="{c.country}" displayExpression="{name}"
                                                values="{modelCountries}" />
                                    </ColumnOptimized>
                                </TableOptimized>]]]]>
                                </InputText>
                            </Row>
                        </PanelGroup>
                    </Row>
                </Tab>
            </TabContainer>
        </Row>
    </PanelGroup>
    <Button id="pBack" label="[icon='fa fa-chevron-left'] {$.fh.docs.component.back}" onClick="backToFormComponentsList"/>

</Form>