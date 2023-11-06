<?xml version="1.0" encoding="UTF-8"?>
<Form xmlns="http://fh.asseco.com/form/1.0" container="mainForm" id="documentationHolder" label="{componentName}">
    <AvailabilityConfiguration>
        <ReadOnly>inputTextExampleCode1,inputTextExampleCode2,inputTextExampleCode3_4,inputTextExampleCode5,inputTextExampleCode6,inputTextExampleCode7,inputTextExampleCode8,inputTextExampleCode9,inputTextOnChangeBinding1,inputTextOnInputBinding1,inputTextExampleCode10,inputTextExampleCode11,inputTextExampleCode12,inputDateConverterExample1,inputTextExampleCode13,inputTextExampleCode14,inputTextExampleCode15,inputTextExampleCode17,inputTextExampleCode19_1,inputTextExampleCode19_2,inputTextExampleCode19_3,inputTextExampleCode19_4,inputTextExampleCode19_5,inputTextExampleCode19_6,inputTextExampleCode19_7,inputTextExampleCode19_8,inputTextExampleCode19_9,inputTextCodeExample20,inputTextExampleCode20,inputTextCodeExample21_1,inputTextCodeExample21_2,inputTextCodeExample21_3,inputTextCodeExample21_4,inputTextCodeExample22,inputTextExampleTextAsBody,inputTextCodeExample23_1,inputTextCodeExample23_2,inputTextCodeExample23_3,inputTextFHMLAsCodeExample,inputTextCodeExample4,inputTextCodeExample41,inputTextCodeExample56,inputTextHighlightCodeExample,inputTextCodeExample23</ReadOnly>
    </AvailabilityConfiguration>
    <OutputLabel width="md-12" value="{description}" id="_Form_OutputLabel"/>
    <Spacer width="md-12" height="25px" id="_Form_Spacer"/>
    <TabContainer id="_Form_TabContainer">
        <Tab label="{$.fh.docs.component.examples}" id="_Form_TabContainer_Tab1">
                <PanelGroup width="md-12" label="{$.fh.docs.component.inputtext_with_specified_size_and_height}" id="_Form_TabContainer_Tab1_PanelGroup1">
                    <InputText id="inputTextExampleId1" width="md-6" rowsCount="4" rowsCountAuto="true" label="{$.fh.docs.component.inputtext_sample_input_value}"/>
                    <InputText id="inputTextExampleCode1" label="{$.fh.docs.component.code}" width="md-12" rowsCountAuto="true">
                        <![CDATA[
    <InputText id="inputTextExampleId1" width="md-6" height="300" label="{$.fh.docs.component.inputtext_sample_input_value}"/>
                        ]]>
                    </InputText>
                </PanelGroup>
                <PanelGroup width="md-12" label="{$.fh.docs.component.inputtext_with_predefined_value_and_label}" id="_Form_TabContainer_Tab1_PanelGroup2">
                    <InputText width="md-12" id="inputTextExampleId2" label="{$.fh.docs.component.inputtext_sample_input_value}"/>
                    <InputText id="inputTextExampleCode2" label="{$.fh.docs.component.code}" width="md-12" rowsCount="2">
                        <![CDATA[
    <InputText id="inputTextExampleId2" label="{$.fh.docs.component.inputtext_sample_input_value}"/>
                        ]]>
                    </InputText>
                </PanelGroup>
                <PanelGroup width="md-12" label="{$.fh.docs.component.inputtext_with_predefined_value_label_and_hint}" id="_Form_TabContainer_Tab1_PanelGroup3">
                    <InputText width="md-12" label="{$.fh.docs.component.inputtext_sample_input_value}" hint="{$.fh.docs.component.inputtext_this_is_hint}" id="_Form_TabContainer_Tab1_PanelGroup3_InputText1"/>
                    <InputText id="inputTextExampleCode20" label="{$.fh.docs.component.code}" width="md-12" rowsCount="2">
                        <![CDATA[
 <InputText label="Header" label="{$.fh.docs.component.inputtext_sample_input_value}" hint="This is hint"/>
                        ]]>
                    </InputText>
                </PanelGroup>
                <PanelGroup width="md-12" label="{$.fh.docs.component.inputtext_with_bootstrap_layout}" id="_Form_TabContainer_Tab1_PanelGroup4">
                    <InputText id="inputTextExampleId3" width="md-4" label="{$.fh.docs.component.inputtext_sample_input_value}"/>
                    <InputText id="inputTextExampleId4" width="md-8" label="{$.fh.docs.component.inputtext_sample_input_value}"/>
                    <InputText id="inputTextExampleCode3_4" label="{$.fh.docs.component.code}" width="md-12" rowsCount="2">
                        <![CDATA[
  <InputText id="inputTextExampleId3" width="md-4" label="{$.fh.docs.component.inputtext_sample_input_value}"/>
  <InputText id="inputTextExampleId4" width="md-8" label="{$.fh.docs.component.inputtext_sample_input_value}"/>
                        ]]>
                    </InputText>
                </PanelGroup>
                <PanelGroup width="md-12" label="{$.fh.docs.component.inputtext_with_placeholder}" id="_Form_TabContainer_Tab1_PanelGroup5">
                    <InputText width="md-12" id="inputTextExampleId5" placeholder="First name"/>
                    <InputText id="inputTextExampleCode5" label="{$.fh.docs.component.code}" width="md-12" rowsCount="2">
                        <![CDATA[
  <InputText id="inputTextExampleId5" placeholder="First name"/>
                        ]]>
                    </InputText>
                </PanelGroup>
                <PanelGroup width="md-12" label="{$.fh.docs.component.inputtext_with_required_value}" id="_Form_TabContainer_Tab1_PanelGroup6">
                    <InputText width="md-12" id="inputTextExampleId6" required="true" value="{requiredValue}" onChange="onChangeExample"/>
                    <InputText id="inputTextExampleCode6" label="{$.fh.docs.component.code}" width="md-12" rowsCount="2">
                        <![CDATA[
                   <InputText id="inputTextExampleId6" required="true" value="{requiredValue}" onChange="onChangeExample"/>
                        ]]>
                    </InputText>
                </PanelGroup>
                <PanelGroup width="md-12" label="{$.fh.docs.component.inputtext_with_multiple_lines}" id="_Form_TabContainer_Tab1_PanelGroup7">
                    <InputText width="md-12" id="inputTextExampleId7" rowsCount="6" value="Lorem ipsum dolor sit amet enim. Etiam ullamcorper. Suspendisse a pellentesque dui, non felis. Maecenas malesuada elit lectus felis, malesuada ultricies. Curabitur et ligula. Ut molestie a, ultricies porta urna. Vestibulum commodo volutpat a, convallis ac, laoreet enim. Phasellus fermentum in, dolor. Pellentesque facilisis. Nulla imperdiet sit amet magna. Vestibulum dapibus, mauris nec malesuada fames ac turpis velit, rhoncus eu, luctus et interdum adipiscing wisi. Aliquam erat ac ipsum. Integer aliquam purus. Quisque lorem tortor fringilla sed, vestibulum id, eleifend justo vel bibendum sapien massa ac turpis faucibus orci luctus non, consectetuer lobortis quis, varius in, purus. Integer ultrices posuere cubilia Curae, Nulla ipsum dolor lacus, suscipit adipiscing. Cum sociis natoque penatibus et ultrices volutpat. Nullam wisi ultricies a, gravida vitae, dapibus risus ante sodales lectus blandit eu, tempor diam pede cursus vitae, ultricies eu, faucibus quis, porttitor eros cursus lectus, pellentesque eget, bibendum a, gravida ullamcorper quam. Nullam viverra consectetuer. Quisque cursus et, porttitor risus. Aliquam sem. In hendrerit nulla quam nunc, accumsan congue. Lorem ipsum primis in nibh vel risus. Sed vel lectus. Ut sagittis, ipsum dolor quam."/>
                    <InputText id="inputTextExampleCode7" label="{$.fh.docs.component.code}" width="md-12" rowsCount="8">
                        <![CDATA[
 <InputText id="inputTextExampleId7" rowsCount="6" value="Lorem ipsum dolor sit amet enim. Etiam ullamcorper. Suspendisse a pellentesque dui, non felis.
 Maecenas malesuada elit lectus felis, malesuada ultricies. Curabitur et ligula. Ut molestie a, ultricies porta urna. Vestibulum commodo volutpat a, convallis ac, laoreet enim.
 Phasellus fermentum in, dolor. Pellentesque facilisis. Nulla imperdiet sit amet magna. Vestibulum dapibus, mauris nec malesuada fames ac turpis velit, rhoncus eu, luctus et interdum adipiscing wisi.
 Aliquam erat ac ipsum. Integer aliquam purus. Quisque lorem tortor fringilla sed, vestibulum id, eleifend justo vel bibendum sapien massa ac turpis faucibus orci luctus non, consectetuer lobortis quis,
 varius in, purus. Integer ultrices posuere cubilia Curae, Nulla ipsum dolor lacus, suscipit adipiscing. Cum sociis natoque penatibus et ultrices volutpat. Nullam wisi ultricies a, gravida vitae,
 dapibus risus ante sodales lectus blandit eu, tempor diam pede cursus vitae, ultricies eu, faucibus quis, porttitor eros cursus lectus, pellentesque eget, bibendum a, gravida ullamcorper quam.
 Nullam viverra consectetuer. Quisque cursus et, porttitor risus. Aliquam sem. In hendrerit nulla quam nunc, accumsan congue. Lorem ipsum primis in nibh vel risus. Sed vel lectus. Ut sagittis, ipsum dolor quam."/>
                        ]]>
                    </InputText>
                </PanelGroup>
                <PanelGroup width="md-12" label="{$.fh.docs.component.inputtext_with_icon_before_inputtext}" id="_Form_TabContainer_Tab1_PanelGroup8">
                    <InputText width="md-12" id="inputTextExampleId10" icon="fa fa-thumbs-o-up" rowsCount="1" label="{$.fh.docs.component.inputtext_with_icon_before_label}"/>
                    <InputText id="inputTextExampleCode10" label="{$.fh.docs.component.code}" width="md-12" rowsCount="2">
                        <![CDATA[
 <InputText id="inputTextExampleId10" icon="fa fa-thumbs-o-up" rowsCount="1" value="The icon is before text because icon-alignment is not set."/>
                        ]]>
                    </InputText>
                </PanelGroup>
                <PanelGroup width="md-12" label="{$.fh.docs.component.inputtext_with_icon_after_inputtext}" id="_Form_TabContainer_Tab1_PanelGroup9">
                    <InputText hint="Ttttteeessstttt" width="md-12" id="inputTextExampleId11" iconAlignment="after"
                               icon="fa fa-thumbs-o-up" rowsCount="1"
                               label="{$.fh.docs.component.inputtext_with_icon_after_label}"/>
                    <InputText id="inputTextExampleCode11" label="{$.fh.docs.component.code}" width="md-12" rowsCount="2">
                        <![CDATA[
      <InputText id="inputTextExampleId11" iconAlignment="after" icon="fa fa-thumbs-o-up" rowsCount="1" value="The icon is after text."/>
                        ]]>
                    </InputText>
                </PanelGroup>
                <PanelGroup width="md-12" label="{$.fh.docs.component.inputtext_with_onchange_event}" id="_Form_TabContainer_Tab1_PanelGroup10">
                    <InputText width="md-12" id="inputTextExampleId8" label="{$.fh.docs.component.inputtext_change_below_value_and_change_focus_on_any_other_inputtext}" value="{onChangeValue}" onChange="onChangeExample"/>
                    <InputText width="md-12" id="inputTextOnChangeBinding1" value="{onChangeValue}"/>
                    <InputText width="md-12" id="inputTextExampleCode8" rowsCount="2">
                        <![CDATA[
     <InputText id="inputTextExampleId8" label="Change below value and change focus on any other InputText." value="{onChangeValue}" onChange="onChangeExample"/>
     <InputText id="inputTextOnChangeBinding1" value="\{onChangeValue\}" />
                        ]]>
                    </InputText>
                </PanelGroup>
                <PanelGroup width="md-12" label="{$.fh.docs.component.inputtext_with_oninput_event}" id="_Form_TabContainer_Tab1_PanelGroup11">
                    <InputText width="md-12" id="inputTextExampleId9" label="{$.fh.docs.component.inputtext_change_below_value_and_see_what_happen_below}" value="{onInputValue}" onInput="onInputExample"/>
                    <InputText width="md-12" id="inputTextOnInputBinding1" value="{onInputValue}"/>
                    <InputText width="md-12" id="inputTextExampleCode9" rowsCount="2">
                        <![CDATA[
     <InputText id="inputTextExampleId9" label="Change below value and see what happen below." value="{onInputValue}" onInput="onInputExample"/>
     <InputText id="inputTextOnInputBinding1" value="{onInputValue}" />
                        ]]>
                    </InputText>
                </PanelGroup>
                <PanelGroup width="md-12" label="{$.fh.docs.component.inputtext_simple_binding_conversion}" id="_Form_TabContainer_Tab1_PanelGroup12">
                    <InputText width="md-12" id="inputTextExampleId12" label="{$.fh.docs.component.inputtext_below_value_will_be_converter_to_date_if_specified_in_format}" value="{inputTextConverterExample}" onChange="-"/>
                    <InputDate width="md-3" id="inputDateConverterExample1" value="{inputTextConverterExample}"/>
                    <InputText width="md-12" id="inputTextExampleCode12" rowsCount="2">
                        <![CDATA[
                    <InputText id="inputTextExampleId12" label="Below value will be converter to date if specified in format: YYYY-MM-DDTHH:mm:ss.SSSZZ in example: 2016-12-24T00:00:00.000+00:00" value="{inputTextConverterExample}" onChange="-"/>
                    <InputDate id="inputDateConverterExample1" value="{inputTextConverterExample}" />
                        ]]>
                    </InputText>
                </PanelGroup>
                <PanelGroup width="md-12" label="{$.fh.docs.component.inputtext_with_multi_size}" id="_Form_TabContainer_Tab1_PanelGroup13">
                    <InputText id="inputTextExampleId13" label="{$.fh.docs.component.inputtext_with_multi_size}" width="sm-12,md-9,lg-6"/>
                    <InputText id="inputTextExampleCode13" label="{$.fh.docs.component.code}" width="md-12" rowsCount="2">
                        <![CDATA[
                    <InputText id="inputTextExampleId13" label="InputText with multi size" width="sm-12,md-9,lg-6"/>
                        ]]>
                    </InputText>
                </PanelGroup>
                <PanelGroup width="md-12" label="{$.fh.docs.component.inputtext_with_length_limit}" id="_Form_TabContainer_Tab1_PanelGroup14">
                    <InputText width="md-12" id="inputTextExampleId14" placeholder="First name" maxLength="6"/>
                    <InputText id="inputTextExampleCode14" label="{$.fh.docs.component.code}" width="md-12" rowsCount="2">
                        <![CDATA[
                        <InputText id="inputTextExampleId14" placeholder="First name" maxLength="6"/>
                        ]]>
                    </InputText>
                </PanelGroup>
                <PanelGroup width="md-12" label="{$.fh.docs.component.inputtext_password_type_inputtext}" id="_Form_TabContainer_Tab1_PanelGroup15">
                    <InputText width="md-12" id="inputTextExampleId16" placeholder="Password" maxLength="6" inputType="password"/>
                    <InputText id="inputTextExampleCode17" label="{$.fh.docs.component.code}" width="md-12">
                        <![CDATA[
                        <InputText id="inputTextExampleId16" placeholder="Password" maxLength="6" inputType="password"/>
                        ]]>
                    </InputText>
                </PanelGroup>
                <PanelGroup width="md-12" label="{$.fh.docs.component.inputtext_simple_inputtext_with_body_as_value}" id="_Form_TabContainer_Tab1_PanelGroup16">
                    <InputText width="md-12" id="inputTextExampleTextAsBody" label="{$.fh.docs.component.inputtext_as_value_with_cdata}" placeholder="XML elements">
                        <![CDATA[
                        <InputText id="inputTextExampleTextAsBody" label="As value" placeholder="XML elements" width="md-6"/>
                        ]]>
                    </InputText>
                </PanelGroup>
                <PanelGroup width="md-12" label="{$.fh.docs.component.inputtext_xml_elements_to_xml_attribute_value_converter_very_simple}" id="_Form_TabContainer_Tab1_PanelGroup17">
                    <InputText id="inputTextExampleId18" label="{$.fh.docs.component.inputtext_paste_xml_here}" placeholder="XML elements" width="md-12" height="300" value="{xml}"/>
                    <Button label="{$.fh.docs.component.inputtext_convert}" onClick="convertXmlToXmlElement()" id="_Form_TabContainer_Tab1_PanelGroup17_Button"/>
                </PanelGroup>

                <PanelGroup width="md-12" label="{$.fh.docs.component.inputtext_example_of_using_fhml_in_label_of_inputtext_component}" id="_Form_TabContainer_Tab1_PanelGroup18">
                    <InputText id="inputTextFHMLExample" width="md-12" label="{$.fh.docs.component.inputtext_this_is_an_example_of_mixed_usage_of_tags}"/>
                    <InputText id="inputTextFHMLAsCodeExample" label="{$.fh.docs.component.code}" width="md-12">
                        <![CDATA[![ESCAPE[
 <InputText id="inputTextFHMLExample" width="md-12" label="[color='green']This is an [b]example[/b] of mixed[/color] [u][i]usage[/i] of tags[/u]"/>
                            ]]]]>
                    </InputText>
                </PanelGroup>
                <PanelGroup width="md-12" label="{$.fh.docs.component.inputtext_highlight}" id="_Form_TabContainer_Tab1_PanelGroup19">
                    <Row>
                        <Button label="OK" onClick="onOk" styleClasses="btn-success" id="_Form_TabContainer_Tab1_PanelGroup19_Row_Button1"/>
                        <Button label="INFO" onClick="onInfo" styleClasses="btn-info" id="_Form_TabContainer_Tab1_PanelGroup19_Row_Button2"/>
                        <Button label="WARNING" onClick="onWarning" styleClasses="btn-warning" id="_Form_TabContainer_Tab1_PanelGroup19_Row_Button3"/>
                        <Button label="ERROR" onClick="onError" styleClasses="btn-danger" id="_Form_TabContainer_Tab1_PanelGroup19_Row_Button4"/>
                    </Row>
                    <InputText id="inputTextHighlight" width="md-12" value="{highlight}"/>
                    <InputText id="inputTextHighlightCodeExample" label="{$.fh.docs.component.code}" width="md-12" rowsCount="3">
                        <![CDATA[![ESCAPE[
<InputText id="inputTextHighlight" width="md-12" value="{highlight}"/>

getFieldsHighlightingList().add(model, "highlight", PresentationStyleEnum.INFO);
                           ]]]]>
                    </InputText>
                </PanelGroup>
        </Tab>
        <Tab label="{$.fh.docs.component.inputtext_label_position}" id="_Form_TabContainer_Tab2">
            <PanelGroup width="md-12" label="{$.fh.docs.component.inputtext_with_changed_position_of_a_label}" id="_Form_TabContainer_Tab2_PanelGroup">
                <PanelGroup label="{$.fh.docs.component.inputtext_label_with_labelposition_up}" id="_Form_TabContainer_Tab2_PanelGroup_PanelGroup1">
                    <InputText label="{$.fh.docs.component.inputtext_label}" id="inputTextCode19_1" width="md-6" labelPosition="up" onChange="-"/>
                    <InputText id="inputTextExampleCode19_1" width="md-12" label="{$.fh.docs.component.code}">
                        <![CDATA[
      <InputText label="Label" id="inputTextCode19_1" width="sm-3" labelPosition="up" onChange="-"/>
                        ]]>
                    </InputText>
                </PanelGroup>
                <PanelGroup label="{$.fh.docs.component.inputtext_label_with_labelposition_down}" id="_Form_TabContainer_Tab2_PanelGroup_PanelGroup2">
                    <InputText label="{$.fh.docs.component.inputtext_label}" id="inputTextCode19_2" width="md-6" labelPosition="down" onChange="-"/>
                    <InputText id="inputTextExampleCode19_2" width="md-12" label="{$.fh.docs.component.code}">
                        <![CDATA[
    <InputText label="Label" id="inputTextCode19_2" width="sm-3" labelPosition="down" onChange="-"/>
                        ]]>
                    </InputText>
                </PanelGroup>
                <PanelGroup label="{$.fh.docs.component.inputtext_label_with_labelposition_left}" id="_Form_TabContainer_Tab2_PanelGroup_PanelGroup3">
                    <InputText label="{$.fh.docs.component.inputtext_label}" width="md-6" labelPosition="left" onChange="-" id="_Form_TabContainer_Tab2_PanelGroup_PanelGroup3_InputText1"/>
                    <InputText id="inputTextExampleCode19_3" width="md-12" label="{$.fh.docs.component.code}">
                        <![CDATA[
                   <InputText label="Label" width="sm-3" labelPosition="left" onChange="-"/>
                        ]]>
                    </InputText>
                </PanelGroup>

                <PanelGroup label="{$.fh.docs.component.inputtext_label_with_labelposition_right}" id="_Form_TabContainer_Tab2_PanelGroup_PanelGroup4">
                    <InputText label="{$.fh.docs.component.inputtext_label}" id="inputTextCode19_4" width="md-6" labelPosition="right" onChange="-"/>
                    <InputText id="inputTextExampleCode19_4" width="md-12" label="{$.fh.docs.component.code}">
                        <![CDATA[
  <InputText label="Label" id="inputTextCode19_4" width="sm-3" labelPosition="right" onChange="-"/>
                        ]]>
                    </InputText>
                </PanelGroup>
                <PanelGroup label="{$.fh.docs.component.inputtext_label_with_labelposition_right_and_inputsize_20}" id="_Form_TabContainer_Tab2_PanelGroup_PanelGroup5">
                    <InputText label="{$.fh.docs.component.inputtext_label}" id="inputTextCode19_5" width="md-6" inputSize="20" labelPosition="right" onChange="-"/>
                    <InputText id="inputTextExampleCode19_5" width="md-12" label="{$.fh.docs.component.code}">
                        <![CDATA[
                    <InputText label="Label" id="inputTextCode19_5" width="sm-3" inputSize="20" labelPosition="right" onChange="-"/>
                        ]]>
                    </InputText>
                </PanelGroup>
                <PanelGroup label="{$.fh.docs.component.inputtext_label_with_labelposition_right_and_inputsize_30}" id="_Form_TabContainer_Tab2_PanelGroup_PanelGroup6">
                    <InputText label="{$.fh.docs.component.inputtext_label}" id="inputTextCode19_6" width="md-6" inputSize="30" labelPosition="right" onChange="-"/>
                    <InputText id="inputTextExampleCode19_6" width="md-12" label="{$.fh.docs.component.code}">
                        <![CDATA[
  <InputText label="Label" id="inputTextCode19_6" width="sm-3" inputSize="30" labelPosition="right" onChange="-"/>
                        ]]>
                    </InputText>
                </PanelGroup>
                <PanelGroup label="{$.fh.docs.component.inputtext_label_with_labelposition_right_and_inputsize_50}" id="_Form_TabContainer_Tab2_PanelGroup_PanelGroup7">
                    <InputText label="{$.fh.docs.component.inputtext_label}" width="md-6" inputSize="50" labelPosition="right" onChange="-" id="_Form_TabContainer_Tab2_PanelGroup_PanelGroup7_InputText1"/>
                    <InputText id="inputTextExampleCode19_7" width="md-12" label="{$.fh.docs.component.code}">
                        <![CDATA[
  <InputText label="Label" width="sm-3" inputSize="50" labelPosition="right" onChange="-"/>
                        ]]>
                    </InputText>
                </PanelGroup>
                <PanelGroup label="{$.fh.docs.component.inputtext_label_with_labelposition_right_and_inputsize_90}" id="_Form_TabContainer_Tab2_PanelGroup_PanelGroup8">
                    <InputText label="{$.fh.docs.component.inputtext_label}" width="md-6" inputSize="90" labelPosition="right" onChange="-" id="_Form_TabContainer_Tab2_PanelGroup_PanelGroup8_InputText1"/>
                    <InputText id="inputTextExampleCode19_8" width="md-12" label="{$.fh.docs.component.code}">
                        <![CDATA[
                <InputText label="Label" width="sm-3" inputSize="90" labelPosition="right" onChange="-"/>
                        ]]>
                    </InputText>
                </PanelGroup>
                <PanelGroup label="{$.fh.docs.component.inputtext_label_with_labelposition_right_and_inputsize_100}" id="_Form_TabContainer_Tab2_PanelGroup_PanelGroup9">
                    <InputText label="{$.fh.docs.component.inputtext_label}" width="md-6" inputSize="100" labelPosition="right" onChange="-" id="_Form_TabContainer_Tab2_PanelGroup_PanelGroup9_InputText1"/>
                    <InputText id="inputTextExampleCode19_9" width="md-12" label="{$.fh.docs.component.code}">
                        <![CDATA[
       <InputText label="Label" width="sm-3" inputSize="100" labelPosition="right" onChange="-"/>
                        ]]>
                    </InputText>
                </PanelGroup>
            </PanelGroup>
        </Tab>
        <Tab label="{$.fh.docs.component.inputtext_masks}" id="_Form_TabContainer_Tab3">
            <PanelGroup width="md-12" label="{$.fh.docs.component.inputtext_with_mask}" id="_Form_TabContainer_Tab3_PanelGroup1">
                <InputText id="inputTextExampleId15" maskDynamic="true" width="md-6" mask="(AAA)-999/***"/>
                <InputText id="inputTextExampleCode15" label="{$.fh.docs.component.code}" width="md-12">
                    <![CDATA[
<InputText id="inputTextExampleId15" width="md-6" mask="(AAA)-999/***" />
                        ]]>
                </InputText>
                <OutputLabel width="md-12" value="{$.fh.docs.component.inputtext_default_mask_definitions}:" id="_Form_TabContainer_Tab3_PanelGroup1_OutputLabel1"/>
                <Spacer height="10px" width="md-12" id="_Form_TabContainer_Tab3_PanelGroup1_Spacer1"/>
                <OutputLabel width="md-12" value="9 : {$.fh.docs.component.inputtext_numeric}" id="_Form_TabContainer_Tab3_PanelGroup1_OutputLabel2"/>
                <OutputLabel width="md-12" value="A : {$.fh.docs.component.inputtext_alphabetical_PL_up}" id="_Form_TabContainer_Tab3_PanelGroup1_OutputLabel3"/>
                <OutputLabel width="md-12" value="a : {$.fh.docs.component.inputtext_alphabetical_PL_low}" id="_Form_TabContainer_Tab3_PanelGroup1_OutputLabel4"/>
                <OutputLabel width="md-12" value="L : {$.fh.docs.component.inputtext_alphabetical_up}" id="_Form_TabContainer_Tab3_PanelGroup1_OutputLabel5"/>
                <OutputLabel width="md-12" value="l : {$.fh.docs.component.inputtext_alphabetical_low}" id="_Form_TabContainer_Tab3_PanelGroup1_OutputLabel6"/>
                <OutputLabel width="md-12" value="* : {$.fh.docs.component.inputtext_alphanumeric}" id="_Form_TabContainer_Tab3_PanelGroup1_OutputLabel7"/>
                <OutputLabel width="md-12" value="M : {$.fh.docs.component.inputtext_alphanumeric_duh}" id="_Form_TabContainer_Tab3_PanelGroup1_OutputLabel8"/>
                <OutputLabel width="md-12" value="N : {$.fh.docs.component.inputtext_alphanumeric_duh_PL}" id="_Form_TabContainer_Tab3_PanelGroup1_OutputLabel9"/>
                <OutputLabel width="md-12" value="[A] : {$.fh.docs.component.inputtext_optional_mask_character}" id="_Form_TabContainer_Tab3_PanelGroup1_OutputLabel10"/>
                <OutputLabel width="md-12" value="\A : {$.fh.docs.component.inputtext_escaped_mask_character}" id="_Form_TabContainer_Tab3_PanelGroup1_OutputLabel11"/>----
                <OutputLabel width="md-12" value="a|9 : a {$.fh.docs.component.inputtext_or} 9" id="_Form_TabContainer_Tab3_PanelGroup1_OutputLabel12"/>
                <OutputLabel width="md-12" value="(AAA)|(999) : AAA {$.fh.docs.component.inputtext_or} 999" id="_Form_TabContainer_Tab3_PanelGroup1_OutputLabel13"/>
                <OutputLabel width="md-12" value="9\{n\} : n {$.fh.docs.component.inputtext_repeats}" id="_Form_TabContainer_Tab3_PanelGroup1_OutputLabel14"/>
                <OutputLabel width="md-12" value="9\{n,m\} : {$.fh.docs.component.inputtext_from} n {$.fh.docs.component.inputtext_to} m {$.fh.docs.component.inputtext_repeats}" id="_Form_TabContainer_Tab3_PanelGroup1_OutputLabel15"/>
                <OutputLabel width="md-12" value="9\{*\} : 0 {$.fh.docs.component.inputtext_or} {$.fh.docs.component.inputtext_more} {$.fh.docs.component.inputtext_repeats}" id="_Form_TabContainer_Tab3_PanelGroup1_OutputLabel16"/>
                <OutputLabel width="md-12" value="9\{+\} : 1 {$.fh.docs.component.inputtext_or} {$.fh.docs.component.inputtext_more} {$.fh.docs.component.inputtext_repeats}" id="_Form_TabContainer_Tab3_PanelGroup1_OutputLabel17"/>
                <Spacer height="20px" width="md-12" id="_Form_TabContainer_Tab3_PanelGroup1_Spacer2"/>
                <OutputLabel width="md-12" value="{$.fh.docs.component.inputtext_mask_more}" id="_Form_TabContainer_Tab3_PanelGroup1_OutputLabel18"/><Link width="md-12" url="https://github.com/RobinHerbots/Inputmask" value="https://github.com/RobinHerbots/Inputmask" newWindow="true" id="_Form_TabContainer_Tab3_PanelGroup1_Link"/>
                <Spacer height="20px" width="md-12" id="_Form_TabContainer_Tab3_PanelGroup1_Spacer3"/>
                <OutputLabel width="md-12" value="{$.fh.docs.component.inputtext_mask_no_validation}" id="_Form_TabContainer_Tab3_PanelGroup1_OutputLabel19"/>
            </PanelGroup>
            <PanelGroup width="md-12" label="{$.fh.docs.component.inputtext_with_email_mask}" id="_Form_TabContainer_Tab3_PanelGroup2">
                <OutputLabel width="md-12" value="{$.fh.docs.component.inputtext_with_email_mask_label}:" id="_Form_TabContainer_Tab3_PanelGroup2_OutputLabel1"/>
                <OutputLabel width="md-12" value="[size='15']*\{1\}M\{0,99\}@M\{1,26\}[/size]" id="_Form_TabContainer_Tab3_PanelGroup2_OutputLabel2"/>
                <InputText id="inputTextExample20_2" width="md-6" mask="A*@A*.SSS"/>
                <InputText id="inputTextCodeExample20" label="{$.fh.docs.component.code}" width="md-12">
                    <![CDATA[![ESCAPE[
<InputText id="inputTextExample20_2"  width="md-6" mask="*{1}[M{0,99}]@M{1,26}" />
                        ]]]]>
                </InputText>
            </PanelGroup>

            <PanelGroup width="md-12" label="{$.fh.docs.component.inputtext_mask_for_words_separated_with_space_character}" id="_Form_TabContainer_Tab3_PanelGroup3">
                <PanelGroup label="{$.fh.docs.component.inputtext_mask_for_words_separated_with_space_character_label}" id="_Form_TabContainer_Tab3_PanelGroup3_PanelGroup1">
                    <OutputLabel width="md-12" value="{$.fh.docs.component.inputtext_allowed_values}:" id="_Form_TabContainer_Tab3_PanelGroup3_PanelGroup1_OutputLabel1"/>
                    <OutputLabel width="md-12" value="John Doe" id="_Form_TabContainer_Tab3_PanelGroup3_PanelGroup1_OutputLabel2"/>
                    <OutputLabel width="md-12" value="John Doe 9" id="_Form_TabContainer_Tab3_PanelGroup3_PanelGroup1_OutputLabel3"/>
                    <OutputLabel width="md-12" value="john doe" id="_Form_TabContainer_Tab3_PanelGroup3_PanelGroup1_OutputLabel4"/>
                    <OutputLabel width="md-12" value="john doe 9" id="_Form_TabContainer_Tab3_PanelGroup3_PanelGroup1_OutputLabel5"/>
                    <OutputLabel width="md-12" value="JOHN DOE" id="_Form_TabContainer_Tab3_PanelGroup3_PanelGroup1_OutputLabel6"/>
                    <OutputLabel width="md-12" value="JOHN DOE 9" id="_Form_TabContainer_Tab3_PanelGroup3_PanelGroup1_OutputLabel7"/>
                    <OutputLabel width="md-12" value="9 JOHN DOE 9" id="_Form_TabContainer_Tab3_PanelGroup3_PanelGroup1_OutputLabel8"/>
                    <InputText id="inputTextExample21_1" width="md-6" mask="(*{0,127} {+}){+}"/>
                    <InputText id="inputTextCodeExample21_1" label="{$.fh.docs.component.code}" width="md-12">
                        <![CDATA[![ESCAPE[
<InputText id="inputTextExample21_1"  width="md-6" mask="(*{0,127} {+}){+}" />
                        ]]]]>
                    </InputText>
                </PanelGroup>

                <PanelGroup label="{$.fh.docs.component.inputtext_mask_for_words_separated_with_space_character_label2}" id="_Form_TabContainer_Tab3_PanelGroup3_PanelGroup2">
                    <OutputLabel width="md-12" value="{$.fh.docs.component.inputtext_allowed_values}:" id="_Form_TabContainer_Tab3_PanelGroup3_PanelGroup2_OutputLabel1"/>
                    <OutputLabel width="md-12" value="John Doe" id="_Form_TabContainer_Tab3_PanelGroup3_PanelGroup2_OutputLabel2"/>
                    <OutputLabel width="md-12" value="Very Long Company Name" id="_Form_TabContainer_Tab3_PanelGroup3_PanelGroup2_OutputLabel3"/>
                    <InputText id="inputTextExample21_2" width="md-6" mask="(A{1}a{0,127} {+}){+}"/>
                    <InputText id="inputTextCodeExample21_2" label="{$.fh.docs.component.code}" width="md-12">
                        <![CDATA[![ESCAPE[
<InputText id="inputTextExample21_2"  width="md-6" mask="(A{1}a{0,127} {+}){+}" />
                        ]]]]>
                    </InputText>
                </PanelGroup>

                <PanelGroup label="{$.fh.docs.component.inputtext_mask_for_words_separated_with_space_character_label3}" id="_Form_TabContainer_Tab3_PanelGroup3_PanelGroup3">
                    <OutputLabel width="md-12" value="{$.fh.docs.component.inputtext_allowed_values}:" id="_Form_TabContainer_Tab3_PanelGroup3_PanelGroup3_OutputLabel1"/>
                    <OutputLabel width="md-12" value="Sample Name" id="_Form_TabContainer_Tab3_PanelGroup3_PanelGroup3_OutputLabel2"/>
                    <OutputLabel width="md-12" value="Sample NaMe" id="_Form_TabContainer_Tab3_PanelGroup3_PanelGroup3_OutputLabel3"/>
                    <InputText id="inputTextExample21_3" width="md-2" mask="(A{1}*{0,127} {+}){+}"/>
                    <InputText id="inputTextCodeExample21_3" label="{$.fh.docs.component.code}" width="md-12">
                        <![CDATA[![ESCAPE[
<InputText id="inputTextExample21_3"  width="md-2" mask="(A{1}*{0,127} {+}){+}" />
                        ]]]]>
                    </InputText>
                </PanelGroup>

                <PanelGroup label="{$.fh.docs.component.inputtext_mask_for_words_separated_with_space_character_label4}" id="_Form_TabContainer_Tab3_PanelGroup3_PanelGroup4">
                    <OutputLabel width="md-12" value="{$.fh.docs.component.inputtext_allowed_values}:" id="_Form_TabContainer_Tab3_PanelGroup3_PanelGroup4_OutputLabel1"/>
                    <OutputLabel width="md-12" value="Sample Name" id="_Form_TabContainer_Tab3_PanelGroup3_PanelGroup4_OutputLabel2"/>
                    <OutputLabel width="md-12" value="Sample name " id="_Form_TabContainer_Tab3_PanelGroup3_PanelGroup4_OutputLabel3"/>
                    <OutputLabel width="md-12" value="Sample value 9" id="_Form_TabContainer_Tab3_PanelGroup3_PanelGroup4_OutputLabel4"/>
                    <InputText id="inputTextExample21_4" width="md-2" mask="(A{1}*{0,127} {1}){1}(*{0,127} {1}){*}"/>
                    <InputText id="inputTextCodeExample21_4" label="{$.fh.docs.component.code}" width="md-12">
                        <![CDATA[![ESCAPE[
<InputText id="inputTextExample21_4"  width="md-2" mask="(A{1}*{0,127} {1}){1}(*{0,127} {1}){*}" />
                        ]]]]>
                    </InputText>
                </PanelGroup>

            </PanelGroup>

            <PanelGroup width="md-12" label="{$.fh.docs.component.inputtext_phone_masks}" id="_Form_TabContainer_Tab3_PanelGroup4">
                <OutputLabel width="md-12" value="{$.fh.docs.component.inputtext_phone_masks_label}:" id="_Form_TabContainer_Tab3_PanelGroup4_OutputLabel1"/>
                <OutputLabel width="md-12" value="99-999-99-99" id="_Form_TabContainer_Tab3_PanelGroup4_OutputLabel2"/>
                <OutputLabel width="md-12" value="+99-999-999-999" id="_Form_TabContainer_Tab3_PanelGroup4_OutputLabel3"/>
                <OutputLabel width="md-12" value="99999-999-99-99" id="_Form_TabContainer_Tab3_PanelGroup4_OutputLabel4"/>
                <OutputLabel width="md-12" value="+999-999-999-999" id="_Form_TabContainer_Tab3_PanelGroup4_OutputLabel5"/>
                <InputText id="inputTextExample22" width="md-2" mask="(9{2}-9{3}-9{2}-9{2})|(+9{2}-9{3}-9{3}-9{3})|(9{5}-9{3}-9{2}-9{2})|(+9{3}-9{3}-9{3}-9{3})"/>
                <InputText id="inputTextCodeExample22" label="{$.fh.docs.component.code}" width="md-12">
                    <![CDATA[![ESCAPE[
<InputText id="inputTextExample22"  value="{maskedField}" width="md-2" mask="(9{2}-9{3}-9{2}-9{2})|(+9{2}-9{3}-9{3}-9{3})|(9{5}-9{3}-9{2}-9{2})|(+9{3}-9{3}-9{3}-9{3})"/>
                        ]]]]>
                </InputText>
            </PanelGroup>

            <PanelGroup width="md-12" label="{$.fh.docs.component.inputtext_phone_masks_dynamic}" id="_Form_TabContainer_Tab3_PanelGroup5">
                <OutputLabel width="md-12" value="{$.fh.docs.component.inputtext_phone_masks_label}:" id="_Form_TabContainer_Tab3_PanelGroup5_OutputLabel1"/>
                <OutputLabel width="md-12" value="99-999-99-99" id="_Form_TabContainer_Tab3_PanelGroup5_OutputLabel2"/>
                <OutputLabel width="md-12" value="+99-999-999-999" id="_Form_TabContainer_Tab3_PanelGroup5_OutputLabel3"/>
                <OutputLabel width="md-12" value="99999-999-99-99" id="_Form_TabContainer_Tab3_PanelGroup5_OutputLabel4"/>
                <OutputLabel width="md-12" value="+999-999-999-999" id="_Form_TabContainer_Tab3_PanelGroup5_OutputLabel5"/>
                <InputText id="inputTextExample23" maskDynamic="true" width="md-2" mask="(9{2}-9{3}-9{2}-9{2})|(+9{2}-9{3}-9{3}-9{3})|(9{5}-9{3}-9{2}-9{2})|(+9{3}-9{3}-9{3}-9{3})"/>
                <InputText id="inputTextCodeExample23" label="{$.fh.docs.component.code}" width="md-12">
                    <![CDATA[![ESCAPE[
<InputText id="inputTextExample22" maskDynamic="true" value="{maskedField}" width="md-2" mask="(9{2}-9{3}-9{2}-9{2})|(+9{2}-9{3}-9{3}-9{3})|(9{5}-9{3}-9{2}-9{2})|(+9{3}-9{3}-9{3}-9{3})"/>
                        ]]]]>
                </InputText>
            </PanelGroup>

            <PanelGroup width="md-12" label="{$.fh.docs.component.inputtext_mask_with_custom_definition_symbol}" id="_Form_TabContainer_Tab3_PanelGroup6">
                <OutputLabel width="md-12" value="{$.fh.docs.component.inputtext_mask_with_custom_definition_symbol_label}" id="_Form_TabContainer_Tab3_PanelGroup6_OutputLabel1"/>
                <InputText id="inputTextExample23_1" width="md-2" maskDefinition="t[4567]" mask="t{3}"/>
                <InputText id="inputTextCodeExample23_1" label="{$.fh.docs.component.code}" width="md-12">
                    <![CDATA[![ESCAPE[
<InputText id="inputTextExample23_1" width="md-2" maskDefinition="t[4567]" mask="t{3}"/>
                    ]]]]>
                </InputText>

                <OutputLabel width="md-12" value="{$.fh.docs.component.inputtext_mask_with_custom_definition_symbol_label2}" id="_Form_TabContainer_Tab3_PanelGroup6_OutputLabel2"/>
                <InputText id="inputTextExample23_2" width="md-2" maskDefinition="x[!%$^*()]||w[abcd]" mask="w{+}^w{1}x{1}w{1}x{+}"/>
                <InputText id="inputTextCodeExample23_2" label="{$.fh.docs.component.code}" width="md-12">
                    <![CDATA[![ESCAPE[
<InputText id="inputTextExample23_2" width="md-2" maskDefinition="x[!%$^*()]||w[abcd]" mask="w{+}^w{1}x{1}w{1}x{+}"/>
                        ]]]]>
                </InputText>

                <OutputLabel width="md-12" value="{$.fh.docs.component.inputtext_mask_with_custom_definition_symbol_label3}" id="_Form_TabContainer_Tab3_PanelGroup6_OutputLabel3"/>
                <InputText id="inputTextExample23_3" width="md-2" maskDefinition="w[ -]" mask="(A{1}a{*}){1}(w{1}A{1}a{*}){*}"/>
                <InputText id="inputTextCodeExample23_3" label="{$.fh.docs.component.code}" width="md-12">
                    <![CDATA[![ESCAPE[
<InputText id="inputTextExample23_3" width="md-2" maskDefinition="w[ -]" mask="(A{1}a{*}){1}(w{1}A{1}a{*}){*}"/>
                        ]]]]>
                </InputText>
            </PanelGroup>

            <PanelGroup width="md-12" label="{$.fh.docs.component.inputtext_casing_mask}" id="_Form_TabContainer_Tab3_PanelGroup7">
                <OutputLabel value="{$.fh.docs.component.inputtext_casing_mask_desc}" width="md-12" id="_Form_TabContainer_Tab3_PanelGroup7_OutputLabel1"/>
                <OutputLabel value="{$.fh.docs.component.inputtext_casing_mask_example}" width="md-12" id="_Form_TabContainer_Tab3_PanelGroup7_OutputLabel2"/>
                fh.docs.component.inputtext_casing_mask_example
                <InputText id="inputTextExample561" width="md-4" maskDefinition="x[A-Za-z0-9 ]" mask="x{*}"/>
                <InputText id="inputTextExample562" label="/upper/" width="md-4" maskDefinition="x/upper/[A-Za-z0-9 ]" mask="x{*}"/>
                <InputText id="inputTextExample563" label="/lower/" width="md-4" maskDefinition="x/lower/[A-Za-z0-9 ]" mask="x{*}"/>
                <InputText id="inputTextCodeExample56" label="{$.fh.docs.component.code}" width="md-12" rowsCount="3">
                    <![CDATA[![ESCAPE[
<InputText id="inputTextExample561" width="md-4" maskDefinition="x[A-Za-z0-9 ]" mask="x{*}"/>
<InputText id="inputTextExample562" label="/upper/" width="md-4" maskDefinition="x/upper/[A-Za-z0-9 ]" mask="x{*}"/>
<InputText id="inputTextExample563" label="/lower/" width="md-4" maskDefinition="x/lower/[A-Za-z0-9 ]" mask="x{*}"/>
                        ]]]]>
                </InputText>
            </PanelGroup>
        </Tab>
        <Tab label="{$.fh.docs.component.inputtext_regex}" id="_Form_TabContainer_Tab4">
            <PanelGroup width="md-12" label="{$.fh.docs.component.inputtext_regex_post_code}" id="_Form_TabContainer_Tab4_PanelGroup1">
                <InputText width="md-2" value="{regex}" requiredRegex="\d\d-\d\d\d" onChange="+" id="_Form_TabContainer_Tab4_PanelGroup1_InputText1"/>
                <InputText id="inputTextCodeExample4" label="{$.fh.docs.component.code}" width="md-12">
                    <![CDATA[![ESCAPE[
<InputText width="md-2" value="{regex}" requiredRegex="\d\d-\d\d\d" onChange="+"/>
                        ]]]]>
                </InputText>
            </PanelGroup>
            <PanelGroup width="md-12" label="{$.fh.docs.component.inputtext_regex_post_code_mask}" id="_Form_TabContainer_Tab4_PanelGroup2">
                <InputText width="md-2" value="{regexMasked}" requiredRegex="\d\d-\d\d\d" requiredRegexMessage="{$.fh.docs.component.inputtext_regex_post_code_msg}" mask="99-999" onChange="+" id="_Form_TabContainer_Tab4_PanelGroup2_InputText1"/>
                <InputText id="inputTextCodeExample41" label="{$.fh.docs.component.code}" width="md-12">
                    <![CDATA[
<InputText width="md-2" value="\{regexMasked\}" requiredRegex="\d\d-\d\d\d" requiredRegexMessage="{$.fh.docs.component.inputtext_regex_post_code_msg}" mask="99-999" onChange="+"/>
                        ]]>
                </InputText>
            </PanelGroup>
        </Tab>

        <Tab label="{$.fh.docs.component.attributes}" id="_Form_TabContainer_Tab5">
            <Table iterator="item" collection="{attributes}" id="_Form_TabContainer_Tab5_Table">
                <Column label="{$.fh.docs.component.attributes_identifier}" value="{item.name}" width="15" id="_Form_TabContainer_Tab5_Table_Column1"/>
                <Column label="{$.fh.docs.component.attributes_type}" value="{item.type}" width="15" id="_Form_TabContainer_Tab5_Table_Column2"/>
                <Column label="{$.fh.docs.component.attributes_boundable}" value="{item.boundable}" width="10" id="_Form_TabContainer_Tab5_Table_Column3"/>
                <Column label="{$.fh.docs.component.attributes_default_value}" value="{item.defaultValue}" width="20" id="_Form_TabContainer_Tab5_Table_Column4"/>
                <Column label="{$.fh.docs.component.attributes_description}" value="{item.description}" width="40" id="_Form_TabContainer_Tab5_Table_Column5"/>
            </Table>
        </Tab>
    </TabContainer>

    <Button id="pBack" label="[icon='fa fa-chevron-left'] {$.fh.docs.component.back}" onClick="backToFormComponentsList()"/>

</Form>
