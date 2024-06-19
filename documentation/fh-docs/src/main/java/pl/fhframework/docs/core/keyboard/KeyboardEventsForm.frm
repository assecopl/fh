<?xml version="1.0" encoding="UTF-8"?>
<Form xmlns="http://fh.asseco.com/form/1.0" id="KeyboardEventsForm" label="{$.fh.docs.core.keyboard_keyboard_events}" container="mainForm">
    <AvailabilityConfiguration>
        <ReadOnly>code1,code2,code3,code4</ReadOnly>
    </AvailabilityConfiguration>

    <PanelGroup label="{$.fh.docs.core.keyboard_description}" width="md-12" id="_Form_PanelGroup1">
        <OutputLabel width="md-12" id="_Form_PanelGroup1_OutputLabel1">
            {$.fh.docs.core.keyboard_theese_form_components_can_handle}
        </OutputLabel>
        <OutputLabel width="md-12" id="_Form_PanelGroup1_OutputLabel2">
            {$.fh.docs.core.keyboard_key_combination_is_defined}
        </OutputLabel>
        <OutputLabel width="md-12" id="_Form_PanelGroup1_OutputLabel3">
            {$.fh.docs.core.keyboard_predefined_keys_are}
        </OutputLabel>
        <OutputLabel width="md-12" id="_Form_PanelGroup1_OutputLabel4">
            {$.fh.docs.core.keyboard_in_order_to_handle_more_than}
        </OutputLabel>
        <OutputLabel width="md-12" id="_Form_PanelGroup1_OutputLabel5">
            {$.fh.docs.core.keyboard_in_order_to_handle_more_than_one}
        </OutputLabel>
    </PanelGroup>
    <PanelGroup label="{$.fh.docs.core.keyboard_a_single_action_with_a_single_keyboard_event}" width="md-12" id="_Form_PanelGroup2">
        <InputText width="md-12" label="{$.fh.docs.core.keyboard_press_enter}" onKeyEvent="keyPressed" keyEvent="ENTER" id="_Form_PanelGroup2_InputText1"/>
        <InputNumber label="{$.fh.docs.core.keyboard_press_del}" onKeyEvent="keyPressed" keyEvent="DEL" id="_Form_PanelGroup2_InputNumber"/>
        <InputDate width="md-3" label="{$.fh.docs.core.keyboard_press_ctrl_x}" onKeyEvent="keyPressed" keyEvent="CTRL+X" id="_Form_PanelGroup2_InputDate"/>
        <InputTimestamp width="md-3" label="{$.fh.docs.core.keyboard_press_f2}" onKeyEvent="keyPressed" keyEvent="F2" id="_Form_PanelGroup2_InputTimestamp"/>
        <Combo width="md-12" label="{$.fh.docs.core.keyboard_press_ctrl_shift_space}" onKeyEvent="keyPressed" keyEvent="CTRL+SHIFT+SPACE" id="_Form_PanelGroup2_Combo"/>
        <InputText id="code1" label="{$.fh.docs.core.keyboard_xml}" width="md-12" rowsCount="5">
            <![CDATA[![ESCAPE[<InputText label="Press ENTER" onKeyEvent="keyPressed" keyEvent="ENTER"/>
<InputNumber label="Press DEL" onKeyEvent="keyPressed" keyEvent="DEL"/>
<InputDate label="Press CTRL+X" onKeyEvent="keyPressed" keyEvent="CTRL+X"/>
<InputTimestamp label="Press F2" onKeyEvent="keyPressed" keyEvent="F2"/>
<Combo label="Press CTRL+SHIFT+SPACE" onKeyEvent="keyPressed" keyEvent="CTRL+SHIFT+SPACE"/>
]]]]>
        </InputText>
    </PanelGroup>

    <Spacer height="10px" width="md-12" id="_Form_Spacer1"/>

    <PanelGroup label="{$.fh.docs.core.keyboard_a_single_action_with_multiple_keyboard_events}" width="md-12" id="_Form_PanelGroup3">
        <InputText width="md-12" label="{$.fh.docs.core.keyboard_press_enter_del_or_space}" onKeyEvent="keyPressed" keyEvent="ENTER|DEL|SPACE" id="_Form_PanelGroup3_InputText1"/>
        <InputText id="code2" label="{$.fh.docs.core.keyboard_xml}" width="md-12" rowsCount="1">
            <![CDATA[![ESCAPE[<InputText label="Press ENTER, DEL or SPACE" onKeyEvent="keyPressed" keyEvent="ENTER|DEL|SPACE"/>]]]]>
        </InputText>
    </PanelGroup>

    <Spacer height="10px" width="md-12" id="_Form_Spacer2"/>

    <PanelGroup width="md-12" label="{$.fh.docs.core.keyboard_multiple_actions_each_with_a_single_keyboard_event}" id="_Form_PanelGroup4">
        <InputText width="md-12" label="{$.fh.docs.core.keyboard_press_enter_ctrl_x_or_esc}" id="_Form_PanelGroup4_InputText1">
            <OnKeyEvent onKeyEvent="keyPressedCtrlX" keyEvent="CTRL+X"/>
            <OnKeyEvent onKeyEvent="keyPressedEnter" keyEvent="ENTER"/>
            <OnKeyEvent onKeyEvent="keyPressedESC" keyEvent="ESC"/>
        </InputText>
        <InputText id="code3" label="{$.fh.docs.core.keyboard_xml}" width="md-12" rowsCount="5">
    <![CDATA[![ESCAPE[<InputText label="Press one of defined keys">
    <OnKeyEvent onKeyEvent="keyPressedCtrlX" keyEvent="CTRL+X"/>
    <OnKeyEvent onKeyEvent="keyPressedEnter" keyEvent="ENTER"/>
    <OnKeyEvent onKeyEvent="keyPressedESC" keyEvent="ESC"/>
</InputText>
]]]]>
        </InputText>
    </PanelGroup>

    <Spacer height="10px" width="md-12" id="_Form_Spacer3"/>

    <PanelGroup width="md-12" label="{$.fh.docs.core.keyboard_mixed_example}" id="_Form_PanelGroup5">
        <InputText width="md-12" label="{$.fh.docs.core.keyboard_press_enter_ctrl_x_or_esc}" onKeyEvent="keyPressedCtrlX" keyEvent="CTRL+X" id="_Form_PanelGroup5_InputText1">
            <OnKeyEvent onKeyEvent="keyPressed" keyEvent="ENTER|ESC"/>
        </InputText>
        <InputText id="code4" label="{$.fh.docs.core.keyboard_xml}" width="md-12" rowsCount="3">
            <![CDATA[![ESCAPE[<InputText label="Press one of defined keys" onKeyEvent="keyPressedCtrlX" keyEvent="CTRL+X">
    <OnKeyEvent onKeyEvent="keyPressed" keyEvent="ENTER|ESC"/>
</InputText>
]]]]>
        </InputText>
    </PanelGroup>
</Form>