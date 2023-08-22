<?xml version="1.0" encoding="UTF-8"?>
<Form xmlns="http://fh.asseco.com/form/1.0" id="FormsDocs" label="{$.fh.docs.core.forms_forms_documentation}" container="mainForm">
    <AvailabilityConfiguration/>

    <PanelGroup label="{$.fh.docs.core.forms_basic_information}" width="md-12" id="_Form_PanelGroup1">
        <OutputLabel width="md-12" id="_Form_PanelGroup1_OutputLabel1">
            {$.fh.docs.core.forms_core_functionality_of_fh}
        </OutputLabel>

        <OutputLabel width="md-12" id="_Form_PanelGroup1_OutputLabel2">{$.fh.docs.core.forms_examples_legend}:</OutputLabel>
        <OutputLabel width="md-12" id="_Form_PanelGroup1_OutputLabel3">• {$.fh.docs.core.forms_f_normal_form}</OutputLabel>
        <OutputLabel width="md-12" id="_Form_PanelGroup1_OutputLabel4">• {$.fh.docs.core.forms_mn_modal_form}</OutputLabel>
        <OutputLabel width="md-12" id="_Form_PanelGroup1_OutputLabel5">• {$.fh.docs.core.forms_mon_modal_form_as_overflow}</OutputLabel>
    </PanelGroup>
    <Spacer height="10px" width="md-12" id="_Form_Spacer"/>

    <PanelGroup width="md-12" label="{$.fh.docs.core.forms_modal_forms_with_inline_messages_from_java}" id="_Form_PanelGroup2">
        <OutputLabel width="md-12" id="_Form_PanelGroup2_OutputLabel1">
            {$.fh.docs.core.forms_by_default_messages_class}
            {$.fh.docs.core.forms_if_no_modal_is_presented}
        </OutputLabel>
        <Spacer width="md-12" height="15px" id="_Form_PanelGroup2_Spacer1"/>
        <OutputLabel width="md-12" id="_Form_PanelGroup2_OutputLabel2">
            [size='20']F -&gt; MO1 -&gt; MO2[/size]
        </OutputLabel>
        <Spacer width="md-12" height="15px" id="_Form_PanelGroup2_Spacer2"/>
        <Button label="{$.fh.docs.core.forms_messages_modal}" onClick="showMessage" id="_Form_PanelGroup2_Button"/>
    </PanelGroup>
    <PanelGroup width="md-12" label="{$.fh.docs.core.forms_modal_forms_as_normal_xml_form}" id="_Form_PanelGroup3">
        <OutputLabel width="md-12" id="_Form_PanelGroup3_OutputLabel1">
            {$.fh.docs.core.forms_creating_form_by_xml}
            {$.fh.docs.core.forms_or_if_modal_with_overflow}
        </OutputLabel>
        <Spacer width="md-12" height="15px" id="_Form_PanelGroup3_Spacer1"/>
        <OutputLabel width="md-12" id="_Form_PanelGroup3_OutputLabel2">
            [size='20']F -&gt; M1 -&gt; MO1 -&gt; MO2[/size]
        </OutputLabel>
        <Spacer width="md-12" height="15px" id="_Form_PanelGroup3_Spacer2"/>
        <Button label="{$.fh.docs.core.forms_form_modal}" onClick="showSimpleModal" id="_Form_PanelGroup3_Button"/>
    </PanelGroup>

    <PanelGroup width="md-12" label="{$.fh.docs.core.forms_more_complex_modal_forms_usage}" id="_Form_PanelGroup4">
        <OutputLabel width="md-12" id="_Form_PanelGroup4_OutputLabel1">
            {$.fh.docs.core.forms_lets_assume_that_client}:
            OtherModal -&gt; Messages1 -&gt; Messages2 -&gt; SimpleModal.
            {$.fh.docs.core.forms_when_messages2_will_be_displayed}
            {$.fh.docs.core.forms_when_simplemodal_will_be_display}
        </OutputLabel>
        <Spacer height="10px" width="md-12" id="_Form_PanelGroup4_Spacer1"/>
        <OutputLabel width="md-12" id="_Form_PanelGroup4_OutputLabel2">
            [size='20']F -&gt; M1 -&gt; MO1 -&gt; M2[/size]
        </OutputLabel>
        <Spacer height="10px" width="md-12" id="_Form_PanelGroup4_Spacer2"/>
        <Button label="{$.fh.docs.core.forms_form_modal}" onClick="showOtherModal" id="_Form_PanelGroup4_Button1"/>

        <Spacer height="10px" width="md-12" id="_Form_PanelGroup4_Spacer3"/>
        <OutputLabel width="md-12" id="_Form_PanelGroup4_OutputLabel3">
            [size='20']F -&gt; M1 -&gt; MO1 -&gt; M1[/size]
        </OutputLabel>
        <Spacer height="10px" width="md-12" id="_Form_PanelGroup4_Spacer4"/>
        <Button label="{$.fh.docs.core.forms_form_modal}" onClick="showSameModal" id="_Form_PanelGroup4_Button2"/>
    </PanelGroup>

</Form>