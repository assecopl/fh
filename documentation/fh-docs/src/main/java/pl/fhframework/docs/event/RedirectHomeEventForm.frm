<?xml version="1.0" encoding="UTF-8"?>
<Form xmlns="http://fh.asseco.com/form/1.0" container="mainForm" id="documentationHolder" label="{$.fh.docs.event.redirect_home}">
    <AvailabilityConfiguration>
        <ReadOnly>code1,code2</ReadOnly>
    </AvailabilityConfiguration>
    <OutputLabel width="md-12" value="{$.fh.docs.event.redirect_redirect_event_is_an_event_which}." id="_Form_OutputLabel1"/>
    <OutputLabel width="md-12" value="{$.fh.docs.event.to_use_notification_use_following} API: pl.fhframework.event.EventRegistry.fireRedirectHomeEvent." id="_Form_OutputLabel2"/>

    <PanelGroup width="md-12" label="{$.fh.docs.event.example_usage}" id="_Form_PanelGroup">
        <Button label="{$.fh.docs.event.redirect_home}" onClick="home()" id="_Form_PanelGroup_Button"/>

        <PanelGroup width="md-12" label="{$.fh.docs.event.form_code}" id="_Form_PanelGroup_PanelGroup1">
            <InputText width="md-12" id="code1" rowsCount="2" label="{$.fh.docs.event.use_case_code}" value="&lt;Button label=&quot;{$.fh.docs.event.redirect_home}&quot; onClick=&quot;home()&quot;/&gt;"/>
        </PanelGroup>

        <PanelGroup width="md-12" label="{$.fh.docs.event.java_code}" id="_Form_PanelGroup_PanelGroup2">
            <InputText width="md-12" id="code2" rowsCount="7" label="{$.fh.docs.event.use_case_code}" >
                <![CDATA[![ESCAPE[
@Autowired
private EventRegistry eventRegistry;

@Action
private void home() {
    eventRegistry.fireRedirectHomeEvent();
}
                ]]]]>
            </InputText>
        </PanelGroup>
    </PanelGroup>
</Form>