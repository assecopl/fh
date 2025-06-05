<?xml version="1.0" encoding="UTF-8"?>
<Form xmlns="http://fh.asseco.com/form/1.0" id="SimpleModal" label="{$.fh.docs.core.forms.example_simple_modal}" container="mainForm" formType="MODAL">
    <AvailabilityConfiguration/>

    <PanelGroup label="{$.fh.docs.core.forms.example_simple_modal}" width="md-12" id="_Form_PanelGroup">
       <OutputLabel width="md-12" id="_Form_PanelGroup_OutputLabel">
           {$.fh.docs.core.forms.example_some_basic_modal}
       </OutputLabel>
    </PanelGroup>

    <Button label="{$.fh.docs.core.forms_messages_modal}" onClick="showMessage" width="md-6" id="_Form_Button1"/>
    <Button label="{$.fh.docs.service.close}" onClick="close" id="_Form_Button2"/>

</Form>