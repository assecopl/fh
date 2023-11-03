<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<Form xmlns="http://fh.asseco.com/form/1.0" container="mainForm" formType="STANDARD" modalSize="REGULAR">
    <PanelHeaderFhDP title="Tables" info="Example" onClick="close" width="md-12"/>

    <PanelGroup label="SimpleTable - one click">
        <Button id="btnAddBase2" inlineStyle="margin-left: auto;" horizontalAlign="right" style="default"
                styleClasses="button-in-operations" verticalAlign="middle" onClick="addBaseList()"
                label="[icon='fas fa-plus']" paddingRight="10"/>
        <Table collection="{listElements}" selected="{selectedListElement}" iterator="row" onRowClick="editBaseList()">
            <Column width="1" label="Id" value="{row.id}"/>

            <Column width="4" label="Name" value="{row.name}"/>
            <Column width="4" label="Description" value="{row.description}"/>
            <Column width="1" verticalAlign="top"
                    label="[className='d-flex,align-items-center'][portal='btnAddBase2'][/className]">

            <Button horizontalAlign="right" style="default" width="xs-1" verticalAlign="middle"
                        onClick="removeBaseList({row})" label="[icon='fa fa-trash']"
                        styleClasses="button-in-operations"/>
            </Column>
        </Table>
    </PanelGroup>

    <PanelGroup label="SimpleTable - loupe icon">
        <Button id="btnAddBase3" inlineStyle="margin-left: auto;" horizontalAlign="right" style="default"
                styleClasses="button-in-operations" verticalAlign="middle" onClick="addBaseList()"
                label="[icon='fas fa-plus']" paddingRight="10"/>
        <Table collection="{listElements}" selected="{selectedListElement}" iterator="row" styleClasses="table-default-cursor">
            <Column width="1" label="Id" value="{row.id}"/>
            <Column width="4" label="Name" value="{row.name}"/>
            <Column width="4" label="Description" value="{row.description}"/>
            <Column width="1" verticalAlign="top"
                    label="[className='d-flex,align-items-center'][portal='btnAddBase3'][/className]">
                <Button horizontalAlign="right" style="default" width="xs-1" verticalAlign="middle"
                        onClick="viewBaseListByRow({row})" label="[icon='fas fa-search']"
                        styleClasses="button-in-operations"/>
            </Column>
        </Table>
    </PanelGroup>

    <PanelGroup label="Action on buttons - EDIT">
        <PanelGroup label="SimpleTable">
            <Button id="btnAddBase4" inlineStyle="margin-left: auto;" horizontalAlign="right" style="default"
                    styleClasses="button-in-operations" verticalAlign="middle" onClick="addBaseList()"
                    label="[icon='fas fa-plus']" paddingRight="10"/>
            <Table collection="{listElements}" selected="{selectedListElement}" iterator="row" styleClasses="table-default-cursor">
                <Column width="1" label="Id" value="{row.id}"/>
                <Column width="4" label="Name" value="{row.name}"/>
                <Column width="4" label="Description" value="{row.description}"/>
                <Column width="1" verticalAlign="top"
                        label="[className='d-flex,align-items-center'][portal='btnAddBase4'][/className]">
                    <Button horizontalAlign="right" style="default" width="xs-1" verticalAlign="middle"
                            onClick="viewBaseListByRow({row})" label="[icon='fas fa-pencil-alt']"
                            styleClasses="button-in-operations"/>
                    <Button horizontalAlign="right" style="default" width="xs-1" verticalAlign="middle"
                            onClick="removeBaseList({row})" label="[icon='fa fa-trash']"
                            styleClasses="button-in-operations" wrapperStyle="margin-left: unset;"/>
                </Column>
            </Table>
        </PanelGroup>
        <Button id="btnAddBase5" inlineStyle="margin-left: auto;" horizontalAlign="right" style="default"
                styleClasses="button-in-operations" verticalAlign="middle" onClick="addBaseList()"
                label="[icon='fas fa-plus']" paddingRight="10"/>
        <PanelGroup label="NestedTable">
            <Table collection="{listElements}" selected="{selectedListElement}" iterator="row" styleClasses="table-default-cursor">
                <Column width="5" label="Id" value="{row.id}"/>
                <Column width="15" label="Name" value="{row.name}"/>
                <Column width="15" label="Description" value="{row.description}"/>
                <Column width="5">
                    <Button horizontalAlign="left" style="default" width="xs-1" verticalAlign="middle"
                            onClick="viewBaseListByRow({row})" label="[icon='fas fa-pencil-alt']"
                            styleClasses="button-in-operations"/>
                    <Button horizontalAlign="left" style="default" width="xs-1" verticalAlign="middle"
                            onClick="removeBaseList({row})" label="[icon='fa fa-trash']"
                            styleClasses="button-in-operations" wrapperStyle="margin-left: unset;"/>
                </Column>
                <Column width="60" verticalAlign="top"
                        label="[className='d-flex,align-items-center']Attributes [portal='btnAddBase5'][/className]">
                    <Button id="btnAddNested2" inlineStyle="margin-left: auto;" horizontalAlign="right" style="default"
                            styleClasses="button-in-operations" verticalAlign="middle" onClick="addAttributes({row})"
                            label="[icon='fas fa-plus']"/>
                    <Group>
                        <Table width="99%" collection="{row.attributeElementList}" iterator="row2"
                               selected="{selectedAttributeElement}" styleClasses="table-default-cursor">
                            <Column width="1" label="Id" value="{row2.id}"/>
                            <Column width="7" label="name" value="{row2.name}"/>
                            <Column id="colPortalNestedBtn2" width="2"
                                    label="[className='col-xs-1'][fhportal='id=btnAddNested2_row_{row$index};wrapped=false;removeWrapper=true'][/className]">
                                <Button horizontalAlign="right" style="default" width="xs-1" verticalAlign="middle"
                                        onClick="viewAttributesListByRow({row2}, {row})" label="[icon='fas fa-pencil-alt']"
                                        styleClasses="button-in-operations"/>
                                <Button horizontalAlign="right" style="default" styleClasses="button-in-operations"
                                        width="xs-1" verticalAlign="middle"
                                        onClick="removeAttributes({row2}, {row})"
                                        label="[icon='fa fa-trash']" wrapperStyle="margin-left: unset;"/>
                            </Column>
                        </Table>
                    </Group>
                </Column>
            </Table>
        </PanelGroup>
    </PanelGroup>

    <PanelGroup label="Action on buttons - VIEW">
        <PanelGroup label="SimpleTable">
            <Table collection="{listElements}" selected="{selectedListElement}" iterator="row" styleClasses="table-default-cursor">
                <Column width="1" label="Id" value="{row.id}"/>
                <Column width="4" label="Name" value="{row.name}"/>
                <Column width="4" label="Description" value="{row.description}"/>
                <Column width="1" verticalAlign="top"
                        label=" ">
                    <Button horizontalAlign="right" style="default" width="xs-1" verticalAlign="middle"
                            onClick="viewBaseListByRowAccessibility({row}, 'VIEW')" label="[icon='fas fa-eye']"
                            styleClasses="button-in-operations"/>
                </Column>
            </Table>
        </PanelGroup>
        <PanelGroup label="NestedTable">
            <Table collection="{listElements}" selected="{selectedListElement}" iterator="row" styleClasses="table-default-cursor">
                <Column width="5" label="Id" value="{row.id}"/>
                <Column width="15" label="Name" value="{row.name}"/>
                <Column width="15" label="Description" value="{row.description}"/>
                <Column width="5">
                    <Button horizontalAlign="left" style="default" width="xs-1" verticalAlign="middle"
                            onClick="viewBaseListByRowAccessibility({row}, 'VIEW')" label="[icon='fas fa-eye']"
                            styleClasses="button-in-operations"/>
                </Column>
                <Column width="60" verticalAlign="top"
                        label=" ">
                    <Group>
                        <Table width="99%" collection="{row.attributeElementList}" iterator="row2"
                               selected="{selectedAttributeElement}" styleClasses="table-default-cursor">
                            <Column width="1" label="Id" value="{row2.id}"/>
                            <Column width="7" label="name" value="{row2.name}"/>
                            <Column id="colPortalNestedBtn3" width="2"
                                    label="[className='col-xs-1'][/className]">
                                <Button horizontalAlign="right" style="default" width="xs-1" verticalAlign="middle"
                                        onClick="viewAttributesListByRowAccessibility({row2}, {row}, 'VIEW')" label="[icon='fas fa-eye']"
                                        styleClasses="button-in-operations"/>
                            </Column>
                        </Table>
                    </Group>
                </Column>
            </Table>
        </PanelGroup>
    </PanelGroup>
</Form>
