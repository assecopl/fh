<?xml version="1.0" encoding="UTF-8"?>
<Form xmlns="http://fh.asseco.com/form/1.0" container="mainForm" hideHeader="true" formType="MODAL" modalSize="SMALL">
    <PanelHeaderFhDP title="{$.fhdp.adm.roles.create.header.title}" onClick="cancel" width="md-12" />

    <Group id="roleInfoGroup">
        <InputText width="md-12" id="roleNameIT" label="{$.fhdp.adm.roles.create.role.name}" value="{roleName}" required="true"/>
        <InputText width="md-12" id="roleDescription" label="{$.fhdp.adm.roles.create.role.description}" value="{description}"/>
        <ValidateMessages level="error" componentIds="roleInfoGroup" id="roleValidateMessages"/>
    </Group>

    <Group id="buttonsGroup">
        <Button label="{$.fhdp.adm.roles.create.button.confirm}" onClick="confirm" width="md-3" id="confirmButton"/>
        <Button label="{$.fhdp.adm.roles.create.button.cancel}" onClick="cancel" width="md-3" id="cancelButton"/>
    </Group>
</Form>
