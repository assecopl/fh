<?xml version="1.0" encoding="UTF-8"?>
<Form xmlns="http://fh.asseco.com/form/1.0" container="mainForm" id="documentationHolder" label="{$.fh.docs.event.custom_custom_action_event}">
    <AvailabilityConfiguration>
        <ReadOnly>code1,code2</ReadOnly>
    </AvailabilityConfiguration>
    <OutputLabel width="md-12" value="{$.fh.docs.event.custom_custom_action_event_helps}" id="_Form_OutputLabel1"/>
    <OutputLabel width="md-12" value="{$.fh.docs.event.to_use_notification_use_following} API: pl.fhframework.event.EventRegistry.fireCustomActionEvent." id="_Form_OutputLabel2"/>

    <PanelGroup width="md-12" label="{$.fh.docs.event.example_usage}" id="_Form_PanelGroup">
        <OutputLabel width="md-12" value="{$.fh.docs.event.custom_action_name_is_a_function_name}." id="_Form_PanelGroup_OutputLabel"/>
        <PanelGroup width="md-12" label="{$.fh.docs.event.custom_javascript_example}" id="_Form_PanelGroup_PanelGroup1">
            <InputText width="md-12" id="code1" rowsCount="7">
                <![CDATA[![ESCAPE[CustomActions.callbacks['toggleMenu'] = function() {
        $('.menu-toggle').on('click', function(event) {
            FormsManager.toggleMenu();
        });
};]]]]>
            </InputText>
        </PanelGroup>
        <PanelGroup width="md-12" label="{$.fh.docs.event.custom_usecase_java_example}" id="_Form_PanelGroup_PanelGroup2">
            <InputText width="md-12" id="code2" rowsCount="8">
                <![CDATA[![ESCAPE[@Autowired
    private EventRegistry eventRegistry;

    @Override
    public void start() {
        eventRegistry.fireCustomActionEvent("toggleMenu");
    }
]]]]>
            </InputText>
        </PanelGroup>

    </PanelGroup>
</Form>