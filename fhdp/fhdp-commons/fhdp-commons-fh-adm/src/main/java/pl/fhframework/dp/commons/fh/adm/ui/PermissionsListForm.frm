<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<Form xmlns="http://fh.asseco.com/form/1.0" container="mainForm" formType="STANDARD" modalSize="REGULAR">
    <PanelHeaderFhDP title="{$.fhdp.adm.permissions.list.header.title}" onClick="close" width="md-12" />
    <AvailabilityConfiguration>
        <Invisible when="-selectedRole == null">permissionPanelGroup</Invisible>
<!--        <ReadOnly when="-selectedRole == null">addPermissionButton</ReadOnly>-->
<!--        <ReadOnly when="-selectedPermissions.isEmpty()">deletePermissionButton</ReadOnly>-->
    </AvailabilityConfiguration>
    <Button id="addPermissionButton" inlineStyle="margin-left: auto;" horizontalAlign="right" style="default"
            styleClasses="button-in-operations" verticalAlign="middle" onClick="addPermission"
            label="[icon='fas fa-plus']" paddingRight="10"/>
    <Button id="addRoleButton" inlineStyle="margin-left: auto;" horizontalAlign="right" style="default"
            styleClasses="button-in-operations" verticalAlign="middle" onClick="addRole"
            label="[icon='fas fa-plus']" paddingRight="10"/>
    <PanelGroup>
        <PanelGroup id="rolesPanelGroup" width="lg-3,md-12" label="{$.fhdp.adm.permissions.list.roles.label}">
            <Table collection="{roles}" iterator="role" selected="{selectedRole}" onRowClick="roleSelected" minRows="3" id="rolesTable">
                <Column label="{$.fhdp.adm.permissions.list.roles.name}" width="70" value="{role.roleName}" id="roleNameColumn"/>
                <Column id="roleButtonsColumn" width="30" label="[className='d-flex,align-items-center'] [portal='addRoleButton'][/className]">
                    <Button horizontalAlign="left" style="default" width="xs-13" verticalAlign="middle"
                            onClick="deleteRole({role})" label="[icon='fa fa-trash']" availability="{FORM.isAdminRole(role)}"
                            confirmationMsg="{$.fhdp.adm.roles.list.role.delete.message}" confirmOnEvent="onClick"
                            styleClasses="button-in-operations"/>
                </Column>
            </Table>
        </PanelGroup>
        <PanelGroup id="permissionPanelGroup"
                    width="lg-9,md-12" label="{$.fhdp.adm.permissions.list.permission.label}"
                    borderVisible="true">
            <TablePaged width="md-12" iterator="permission" collection="{permissions}" multiselect="true"
                        selected="{selectedPermissions}" tableStripes="show" rowHeight="small"
                        minRows="3" id="permissionsTablePaged">
<!--                <ColumnPaged id="noColumn" label="{$.fhdp.adm.permissions.list.permission.column.no}" value="{permission$rowNo}" width="4"/>-->
                <ColumnPaged id="functionNameColumn" label="{$.fhdp.adm.permissions.list.permission.column.function_name}" value="{permission.functionName}" width="29"/>
                <ColumnPaged id="moduleNameColumn" label="{$.fhdp.adm.permissions.list.permission.column.module_name}" value="{getModuleName(permission.moduleUUID)}" width="26"/>
                <ColumnPaged id="allowDisallowColumn" label="{$.fhdp.adm.permissions.list.permission.column.allow_disallow}" width="30" horizontalAlign="center" verticalAlign="middle">
                    <OutputLabel width="md-12" value="{permission.denied ? '[color=''#C80000''][icon=''fa fa-times''][/color]' : '[color=''green''][icon=''fa fa-check''][/color]'}" id="allowDisallowOutputLabel"/>
                </ColumnPaged>
                <ColumnPaged id="buttonsColumn" width="4" label="[className='d-flex,align-items-center'] [portal='addPermissionButton'][/className]">
                    <Button horizontalAlign="left" style="default" width="xs-13" verticalAlign="middle"
                            onClick="deletePermission({permission})" label="[icon='fa fa-trash']"
                            confirmationMsg="{$.fhdp.adm.permissions.list.permission.delete.message}" confirmOnEvent="onClick"
                            styleClasses="button-in-operations"/>
                </ColumnPaged>
            </TablePaged>

        </PanelGroup>
    </PanelGroup>
    <Row>
        <Spacer width="md-3"/>
<!--        <Button id="addPermissionButton" width="md-1" onClick="addPermission" label="{$.fhdp.adm.permissions.list.permission.button.add}"/>-->
<!--        <Button id="deletePermissionButton" width="md-2" onClick="deletePermission" label="{$.fhdp.adm.permissions.list.permission.button.delete}"-->
<!--            confirmationMsg="{$.fhdp.adm.permissions.list.permission.delete.message}" confirmOnEvent="onClick"/>-->
    </Row>
</Form>
