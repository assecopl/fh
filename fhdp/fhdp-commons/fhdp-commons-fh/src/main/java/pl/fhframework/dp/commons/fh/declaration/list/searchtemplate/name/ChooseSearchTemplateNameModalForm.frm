<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<Form xmlns="http://fh.asseco.com/form/1.0" container="mainForm" label="Wprowadź nazwę filtra"
      formType="MODAL" modalSize="REGULAR" >
    <KeyboardEvent event="cancel" shortcut="ESC"/>
    <Group width="100%">
        <InputText id="templateNameIT" label="Nazwa filtra" value="{templateName}" width="100%" />
    </Group>
    <Group>
        <ValidateMessages level="error" componentIds="*"/>
    </Group>
    <Spacer height="20px"/>
    <Group>
        <Button label="Zatwierdź" onClick="confirm" width="md-4"/>
        <Button label="Anuluj" onClick="cancel" width="md-4"/>
    </Group>
</Form>
