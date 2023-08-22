<?xml version="1.0" encoding="UTF-8"?>
<Form xmlns="http://fh.asseco.com/form/1.0" container="mainForm" id="documentationHolder" label="{$.fh.docs.event.notification_notification_event}">
    <AvailabilityConfiguration>
        <ReadOnly>code1,code2</ReadOnly>
    </AvailabilityConfiguration>
    <OutputLabel width="md-12" value="{$.fh.docs.event.notification_notification_event_is_an_event}." id="_Form_OutputLabel1"/>
    <OutputLabel width="md-12" value="{$.fh.docs.event.to_use_notification_use_following} API: pl.fhframework.event.EventRegistry.fireNotificationEvent." id="_Form_OutputLabel2"/>
    <PanelGroup width="md-12" label="{$.fh.docs.event.example_usage}" id="_Form_PanelGroup">
        <OutputLabel width="md-12" value="{$.fh.docs.event.notification_four_levels_of_notification_can_be_used}: info, success, warning, error." id="_Form_PanelGroup_OutputLabel"/>
        <Button label="{$.fh.docs.event.notification_push_notification_of} info {$.fh.docs.event.notification_level}" onClick="notificationInfo()" id="_Form_PanelGroup_Button1"/>
        <Button label="{$.fh.docs.event.notification_push_notification_of} success {$.fh.docs.event.notification_level}" onClick="notificationSuccess()" id="_Form_PanelGroup_Button2"/>
        <Button label="{$.fh.docs.event.notification_push_notification_of} warning {$.fh.docs.event.notification_level}" onClick="notificationWarning()" id="_Form_PanelGroup_Button3"/>
        <Button label="{$.fh.docs.event.notification_push_notification_of} error {$.fh.docs.event.notification_level}" onClick="notificationError()" id="_Form_PanelGroup_Button4"/>
        <PanelGroup width="md-12" label="{$.fh.docs.event.form_code}" id="_Form_PanelGroup_PanelGroup1">
            <InputText width="md-12" id="code1" rowsCount="4" label="{$.fh.docs.event.use_case_code}" value="&lt;Button label=&quot;Push notification of info level&quot; onClick=&quot;notificationInfo()&quot;/&gt;
        </PanelGroup>

        <PanelGroup width="md-12" label="{$.fh.docs.event.java_code}" id="_Form_PanelGroup_PanelGroup2">
            <InputText width="md-12" id="code2" rowsCount="22" label="{$.fh.docs.event.use_case_code}" value="@Autowired
        </PanelGroup>
    </PanelGroup>
</Form>