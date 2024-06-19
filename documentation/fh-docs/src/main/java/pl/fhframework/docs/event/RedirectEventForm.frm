<?xml version="1.0" encoding="UTF-8"?>
<Form xmlns="http://fh.asseco.com/form/1.0" container="mainForm" id="documentationHolder" label="{$.fh.docs.event.redirect_redirect_event}">
    <AvailabilityConfiguration>
        <ReadOnly>code1,code2</ReadOnly>
    </AvailabilityConfiguration>
    <OutputLabel width="md-12" value="{$.fh.docs.event.redirect_redirect_event_is_an_event_which}." id="_Form_OutputLabel1"/>
    <OutputLabel width="md-12" value="{$.fh.docs.event.to_use_notification_use_following} API: pl.fhframework.event.EventRegistry.fireRedirectEvent." id="_Form_OutputLabel2"/>

    <PanelGroup width="md-12" label="{$.fh.docs.event.example_usage}" id="_Form_PanelGroup">
        <OutputLabel width="md-12" value="{$.fh.docs.event.redirect_url_can_be_relative}." id="_Form_PanelGroup_OutputLabel1"/>
        <OutputLabel width="md-12" value="{$.fh.docs.event.redirect_warning}" id="_Form_PanelGroup_OutputLabel2"/>
        <Button label="{$.fh.docs.event.redirect_logout_user}" onClick="logout()" id="_Form_PanelGroup_Button1"/>
        <Button label="{$.fh.docs.event.redirect_open_google.com_in_new_window}" width="md-4" onClick="openGoogleInNewWindow()" id="_Form_PanelGroup_Button2"/>
        <PanelGroup width="md-12" label="{$.fh.docs.event.form_code}" id="_Form_PanelGroup_PanelGroup1">
            <InputText width="md-12" id="code1" rowsCount="2" label="{$.fh.docs.event.use_case_code}" value="&lt;Button label=&quot;Logout user&quot; onClick=&quot;logout()&quot;/&gt;
&lt;Button label=&quot;Open google.com&quot; onClick=&quot;openGoogleInNewWindow()&quot;/&gt;"/>
        </PanelGroup>

        <PanelGroup width="md-12" label="{$.fh.docs.event.java_code}" id="_Form_PanelGroup_PanelGroup2">
            <InputText width="md-12" id="code2" rowsCount="12" label="{$.fh.docs.event.use_case_code}" >
                <![CDATA[![ESCAPE[
@Autowired
private EventRegistry eventRegistry;

@Action
private void logout() {
    eventRegistry.fireRedirectEvent("/logout", false);
}

@Action
private void openGoogleInNewWindow() {
    eventRegistry.fireRedirectEvent("http://www.google.com/", true);
}

            ]]]]>
            </InputText>
        </PanelGroup>
    </PanelGroup>
</Form>