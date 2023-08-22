<?xml version="1.0" encoding="UTF-8"?>
<Form xmlns="http://fh.asseco.com/form/1.0" container="mainForm" id="documentationHolder" label="{$.fh.docs.event.focus_focus_event}">
    <AvailabilityConfiguration>
        <ReadOnly>code1,code2</ReadOnly>
    </AvailabilityConfiguration>
    <OutputLabel width="md-12" value="{$.fh.docs.event.focus_focus_event_is_an_event_which}." id="_Form_OutputLabel1"/>
    <OutputLabel width="md-12" value="{$.fh.docs.event.to_use_notification_use_following} API: pl.fhframework.event.EventRegistry.fireFocusEvent." id="_Form_OutputLabel2"/>

    <PanelGroup width="md-12" label="{$.fh.docs.event.example_usage}" id="_Form_PanelGroup">
        <InputText width="md-12" id="inputTextFocus1" value="Input 1"/>
        <InputText width="md-12" id="inputTextFocus2" value="Input 2"/>
        <Button label="{$.fh.docs.event.focus_focus_input} 1" onClick="focusInputOne(this)" id="_Form_PanelGroup_Button1"/>
        <Button label="{$.fh.docs.event.focus_focus_input} 2" onClick="focusInputTwo(this)" id="_Form_PanelGroup_Button2"/>
        <PanelGroup width="md-12" label="{$.fh.docs.event.form_code}" id="_Form_PanelGroup_PanelGroup1">
            <InputText width="md-12" id="code1" rowsCount="4" label="{$.fh.docs.event.use_case_code}" value="&lt;InputText id=&quot;inputTextFocus1&quot; value=&quot;Input 1&quot;/&gt;
        </PanelGroup>

        <PanelGroup width="md-12" label="{$.fh.docs.event.java_code}" id="_Form_PanelGroup_PanelGroup2">
            <InputText width="md-12" id="code2" rowsCount="17" label="{$.fh.docs.event.use_case_code}" value="    @Autowired
        </PanelGroup>
    </PanelGroup>
</Form>