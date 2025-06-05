<?xml version="1.0" encoding="UTF-8"?>
<Form xmlns="http://fh.asseco.com/form/1.0" id="OtherModal" label="{$.fh.docs.core.forms.example_other_modal}" container="mainForm" formType="MODAL">
    <AvailabilityConfiguration/>

    <PanelGroup label="{$.fh.docs.core.forms.example_other_modal}" width="md-12" id="_Form_PanelGroup">
       <OutputLabel width="md-12" id="_Form_PanelGroup_OutputLabel">
           {$.fh.docs.core.forms.example_its_not_the_same_modal_as_before}
       </OutputLabel>
    </PanelGroup>

    <Button label="{$.fh.docs.core.forms_messages_modal}" onClick="showMessageWithModal" width="md-6" id="_Form_Button1"/>
    <Button label="{$.fh.docs.service.close}" onClick="close" id="_Form_Button2"/>

</Form>