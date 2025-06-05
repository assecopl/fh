<?xml version="1.0" encoding="UTF-8"?>
<Form xmlns="http://fh.asseco.com/form/1.0" container="mainForm" label="{$.fh.docs.service.newperson_new_company_employee}" formType="MODAL" id="NewPersonForm">
    <AvailabilityConfiguration>
        <SetByProgrammer>pSavePerson</SetByProgrammer>
    </AvailabilityConfiguration>

    <PanelGroup label=" " id="_Form_PanelGroup">
        <Group id="_Form_PanelGroup_Group">
            <InputText label="{$.fh.docs.service.newperson_name}: " width="md-3" value="{name}" id="_Form_PanelGroup_Group_InputText1"/>
            <InputText label="{$.fh.docs.service.newperson_surname}: " width="md-3" value="{surname}" id="_Form_PanelGroup_Group_InputText2"/>
            <InputText label="{$.fh.docs.service.newperson_city}: " width="md-3" value="{city}" id="_Form_PanelGroup_Group_InputText3"/>
            <InputText label="{$.fh.docs.service.newperson_gender}: " width="md-3" value="{gender}" id="_Form_PanelGroup_Group_InputText4"/>
            <InputText label="{$.fh.docs.service.newperson_status}: " width="md-3" value="{status}" id="_Form_PanelGroup_Group_InputText5"/>
        </Group>

        <PanelGroup label=" " id="_Form_PanelGroup_PanelGroup">
            <Button label="{$.fh.docs.service.save}" id="pSavePerson" onClick="savePerson"/>
            <Button label="{$.fh.docs.service.close}" onClick="closeForm" id="_Form_PanelGroup_PanelGroup_Button2"/>
        </PanelGroup>
    </PanelGroup>
</Form>