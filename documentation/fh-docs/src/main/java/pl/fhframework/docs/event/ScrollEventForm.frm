<?xml version="1.0" encoding="UTF-8"?>
<Form xmlns="http://fh.asseco.com/form/1.0" container="mainForm" id="documentationHolder" label="{$.fh.docs.event.scroll_event_form}">
    <AvailabilityConfiguration>
        <ReadOnly>code1,code2,code3,code4,code5,code6,code7,code8,code9</ReadOnly>
    </AvailabilityConfiguration>
    <OutputLabel width="md-12" value="{$.fh.docs.event.scroll_event_form.event_description}." id="_Form_OutputLabel1"/>
    <OutputLabel width="md-12" value="{$.fh.docs.event.to_use_scroll_event_use_following} API: pl.fhframework.event.EventRegistry.fireScrollEvent." id="_Form_OutputLabel2"/>
    <Spacer width="md-12" height="30" id="_Form_Spacer"/>
    <TabContainer id="_Form_TabContainer">
        <Tab label="ScrollEvent" id="_Form_TabContainer_Tab1">
            <Spacer width="md-12" height="30" id="_Form_TabContainer_Tab1_Spacer"/>
            <Button id="Button1" label="{$.fh.docs.event.scroll_to_button} 2" onClick="fireScrollDown()"/>
            <PanelGroup width="md-12" label="{$.fh.docs.event.form_code}" id="_Form_TabContainer_Tab1_PanelGroup1">
                <InputText width="md-12" id="code1" rowsCount="1" label="{$.fh.docs.event.use_case_code}" value="">
                    <![CDATA[![ESCAPE[<Button id="Button1" label="{$.fh.docs.event.focus_focus_input} 1" onClick="fireScrollDown()"/>]]]]>
                </InputText>
            </PanelGroup>
            <PanelGroup width="md-12" label="{$.fh.docs.event.java_code}" id="_Form_TabContainer_Tab1_PanelGroup2">
                <InputText width="md-12" id="code2" height="700" rowsCount="17" label="{$.fh.docs.event.use_case_code}">
                    <![CDATA[![ESCAPE[
@Autowired
private EventRegistry eventRegistry;

@Override
public void start() {
    showForm(ScrollEventForm.class, new ScrollEventModel());
}

@Action
private void fireScrollDown(ViewEvent<ScrollEventForm> event){
    eventRegistry.fireScrollEvent("inputTextFocus1");
}

@Action
private void fireScrollUp(ViewEvent<ScrollEventForm> event){
    eventRegistry.fireScrollEvent("inputTextFocus2");
}

@Action
private void fireScrollTo(ViewEvent<ScrollEventForm> event){
    eventRegistry.fireScrollEvent("inputTextFocus3");
}
    ]]]]>
                </InputText>
            </PanelGroup>
            <Button id="Button2" label="{$.fh.docs.event.scroll_to_button} 1" onClick="fireScrollUp()"/>
            <PanelGroup width="md-12" label="{$.fh.docs.event.form_code}" id="_Form_TabContainer_Tab1_PanelGroup3">
                <InputText width="md-12" id="code3" rowsCount="1" label="{$.fh.docs.event.use_case_code}" value="">
                    <![CDATA[![ESCAPE[<Button id="Button2" label="{$.fh.docs.event.focus_focus_input} 2" onClick="fireScrollUp()"/>]]]]>
                </InputText>
            </PanelGroup>
        </Tab>
        <Tab label="{$.fh.docs.event.scroll_with_animation}" id="_Form_TabContainer_Tab2">
            <Spacer width="md-12" height="30" id="_Form_TabContainer_Tab2_Spacer"/>
            <Button id="Button3" label="{$.fh.docs.event.scroll_to_button} 4" onClick="fireScrollButton4()"/>
            <PanelGroup width="md-12" label="{$.fh.docs.event.form_code}" id="_Form_TabContainer_Tab2_PanelGroup1">
                <InputText width="md-12" id="code4" rowsCount="1" label="{$.fh.docs.event.use_case_code}" value="">
                    <![CDATA[![ESCAPE[<Button id="Button3" label="{$.fh.docs.event.scroll_to_button} 2" onClick="fireScrollButton4()"/>]]]]>
                </InputText>
            </PanelGroup>
            <PanelGroup width="md-12" label="{$.fh.docs.event.java_code}" id="_Form_TabContainer_Tab2_PanelGroup2">
                <InputText width="md-12" id="code5" height="700" rowsCount="17" label="{$.fh.docs.event.use_case_code}">
                    <![CDATA[![ESCAPE[  @Autowired
    private EventRegistry eventRegistry;

    @Override
    public void start() {
        showForm(ScrollEventForm.class, new ScrollEventModel());
    }

   @Action
    private void fireScrollButton3() {
        eventRegistry.fireScrollEvent("Button3", 3000);
    }

    @Action
    private void fireScrollButton4() {
        eventRegistry.fireScrollEvent("Button4", 3000);
    }

    ]]]]>
                </InputText>
            </PanelGroup>
            <Button id="Button4" label="{$.fh.docs.event.scroll_to_button} 3" onClick="fireScrollButton3()"/>
            <PanelGroup width="md-12" label="{$.fh.docs.event.form_code}" id="_Form_TabContainer_Tab2_PanelGroup3">
                <InputText width="md-12" id="code6" rowsCount="2" label="{$.fh.docs.event.use_case_code}" value="">
                    <![CDATA[![ESCAPE[<Button id="Button4" label="{$.fh.docs.event.scroll_to_button} 1"  onClick="fireScrollButton3()" />]]]]>
                </InputText>
            </PanelGroup>
        </Tab>
        <Tab label="ScrollEvent w kontenerze" id="_Form_TabContainer_Tab3">
            <Spacer width="md-12" height="30" id="_Form_TabContainer_Tab3_Spacer"/>
            <Group width="md-7" styleClasses="border-right pr-4" id="_Form_TabContainer_Tab3_Group1">
                <Button id="Button5" label="Scroll to Element 1" styleClasses="btn-secondary" onClick="fireScrollTo('element1', 1500)"/>
                <Button id="Button6" label="Scroll to Element 2" styleClasses="btn-warning" onClick="fireScrollTo('element2', 1500)"/>
                <Button id="Button7" label="Scroll to Element 3" onClick="fireScrollTo('element3', 0)"/>
                <PanelGroup width="md-12" label="{$.fh.docs.event.form_code}" id="_Form_TabContainer_Tab3_Group1_PanelGroup1">
                    <InputText width="md-12" id="code7" rowsCount="3" label="{$.fh.docs.event.use_case_code}" value="">
                        <![CDATA[![ESCAPE[
<Button id="Button5" label="Element 1" onClick="fireScrollTo('element1', 1500)"/>
<Button id="Button6" label="Element 2" onClick="fireScrollTo('element2', 1500)"/>
<Button id="Button7" label="Scroll to Element 3" onClick="fireScrollTo('element3', 0)"/>]]]]>
                    </InputText>
                </PanelGroup>
                <PanelGroup width="md-12" label="{$.fh.docs.event.java_code}" id="_Form_TabContainer_Tab3_Group1_PanelGroup2">
                    <InputText width="md-12" id="code8" height="350" rowsCount="15" label="{$.fh.docs.event.use_case_code}">
                        <![CDATA[![ESCAPE[
@Autowired
private EventRegistry eventRegistry;

@Override
public void start() {
    showForm(ScrollEventForm.class, new ScrollEventModel());
}

@Action
private void fireScrollTo(String componentId, int miliseconds) {
    eventRegistry.fireScrollEvent(componentId, miliseconds);
}

    }]]]]>
                    </InputText>
                </PanelGroup>
            </Group>
            <Group width="md-5" styleClasses="pl-4" id="_Form_TabContainer_Tab3_Group2">
                <PanelGroup height="400" label="{$.fh.docs.event.panel_with_height}" borderVisible="true" styleClasses="border" id="_Form_TabContainer_Tab3_Group2_PanelGroup">
                    <Button id="element1" width="md-12" label="Element 1" styleClasses="btn-secondary"/>
                    <Spacer width="md-12" height="400" id="_Form_TabContainer_Tab3_Group2_PanelGroup_Spacer1"/>
                    <Button id="element2" width="md-12" label="Element 2" styleClasses="btn-warning"/>
                    <Spacer width="md-12" height="400" id="_Form_TabContainer_Tab3_Group2_PanelGroup_Spacer2"/>
                    <Button id="element3" width="md-12" label="Element 3" styleClasses="btn-primary"/>
                </PanelGroup>

                <InputText width="md-12" id="code9" rowsCount="12" label="{$.fh.docs.event.use_case_code}" value="">
                    <![CDATA[![ESCAPE[
<PanelGroup height="400" label="{$.fh.docs.event.panel_with_height}" borderVisible="true" styleClasses="border">
<Button id="element1" width="md-12" label="Element 1" styleClasses="btn-secondary"/>
<Spacer width="md-12" height="400" />
<Button id="element2" width="md-12" label="Element 2" styleClasses="btn-warning" />
<Spacer width="md-12" height="400" />
<Button id="element3" width="md-12" label="Element 3" styleClasses="btn-primary"/>
                </PanelGroup>]]]]>
                </InputText>
            </Group>


        </Tab>
    </TabContainer>

</Form>