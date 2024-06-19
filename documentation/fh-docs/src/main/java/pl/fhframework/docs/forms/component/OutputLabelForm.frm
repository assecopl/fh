<?xml version="1.0" encoding="UTF-8"?>
<Form xmlns="http://fh.asseco.com/form/1.0" container="mainForm" id="documentationHolder" label="{componentName}">
    <AvailabilityConfiguration>
        <ReadOnly>outputLabelExampleCode1_2,outputLabelExampleCode5,outputLabelExampleCode6,outputLabelExampleCode7,outputLabelExampleCode8,outputLabelExampleCode9,outputLabelExampleCode11,outputLabelExampleCode12</ReadOnly>
    </AvailabilityConfiguration>
    <OutputLabel width="md-12" value="{description}" id="_Form_OutputLabel"/>
    <Spacer width="md-12" height="25px" id="_Form_Spacer"/>
    <TabContainer id="_Form_TabContainer">
        <Tab label="{$.fh.docs.component.examples}" id="_Form_TabContainer_Tab1">
                <PanelGroup width="md-12" label="{$.fh.docs.component.outputlabel_outputlabel_with_specified_height}" id="_Form_TabContainer_Tab1_PanelGroup1">
                    <OutputLabel width="md-12" id="outputLabelExmapleId5" height="75" value="Lorem ipsum dolor sit amet enim. Etiam ullamcorper. Suspendisse a pellentesque dui, non felis. Maecenas malesuada elit lectus felis, malesuada ultricies. Curabitur et ligula. Ut molestie a, ultricies porta urna. Vestibulum commodo volutpat a, convallis ac, laoreet enim. Phasellus fermentum in, dolor. Pellentesque facilisis. Nulla imperdiet sit amet magna. Vestibulum dapibus, mauris nec malesuada fames ac turpis velit, rhoncus eu, luctus et interdum adipiscing wisi. Aliquam erat ac ipsum. Integer aliquam purus. Quisque lorem tortor fringilla sed, vestibulum id, eleifend justo vel bibendum sapien massa ac turpis faucibus orci luctus non, consectetuer lobortis quis, varius in, purus. Integer ultrices posuere cubilia Curae, Nulla ipsum dolor lacus, suscipit adipiscing. Cum sociis natoque penatibus et ultrices volutpat. Nullam wisi ultricies a, gravida vitae, dapibus risus ante sodales lectus blandit eu, tempor diam pede cursus vitae, ultricies eu, faucibus quis, porttitor eros cursus lectus, pellentesque eget, bibendum a, gravida ullamcorper quam. Nullam viverra consectetuer. Quisque cursus et, porttitor risus. Aliquam sem. In hendrerit nulla quam nunc, accumsan congue. Lorem ipsum primis in nibh vel risus. Sed vel lectus. Ut sagittis, ipsum dolor quam."/>

                    <InputText id="outputLabelExampleCode5" label="{$.fh.docs.component.code}" width="md-12" rowsCount="7">
                        <![CDATA[
                            <OutputLabel id="outputLabelExmapleId5" height="75"
                            value="Lorem ipsum dolor sit amet enim. Etiam ullamcorper. Suspendisse a pellentesque dui, non felis.
                            Maecenas malesuada elit lectus felis, malesuada ultricies. Curabitur et ligula. Ut molestie a, ultricies porta urna.
                            Vestibulum commodo volutpat a, convallis ac, laoreet enim. Phasellus fermentum in, dolor. Pellentesque facilisis.
                            Nulla imperdiet sit amet magna. Vestibulum dapibus, mauris nec malesuada fames ac turpis velit, rhoncus eu, luctus et interdum adipiscing wisi.
                            Aliquam erat ac ipsum. Integer aliquam purus. Quisque lorem tortor fringilla sed, vestibulum id, eleifend justo vel bibendum sapien massa
                            ac turpis faucibus orci luctus non, consectetuer lobortis quis, varius in, purus. Integer ultrices posuere cubilia Curae,
                            Nulla ipsum dolor lacus, suscipit adipiscing. Cum sociis natoque penatibus et ultrices volutpat. Nullam wisi ultricies a, gravida vitae,
                            dapibus risus ante sodales lectus blandit eu, tempor diam pede cursus vitae, ultricies eu, faucibus quis, porttitor eros cursus lectus, pellentesque eget,
                            bibendum a, gravida ullamcorper quam. Nullam viverra consectetuer. Quisque cursus et, porttitor risus. Aliquam sem. In hendrerit nulla quam nunc, accumsan congue.
                            Lorem ipsum primis in nibh vel risus. Sed vel lectus. Ut sagittis, ipsum dolor quam."/>
                        ]]>
                    </InputText>
                </PanelGroup>

                <PanelGroup width="md-12" label="{$.fh.docs.component.outputlabel_outputlabel_with_bootstrap_layout}" id="_Form_TabContainer_Tab1_PanelGroup2">
                    <OutputLabel id="outputLabelExmapleId1" width="sm-5" value="{$.fh.docs.component.outputlabel_outputlabel_with_bootstrap_layout_sample_label_width_sm_5} width=&quot;md-5&quot;"/>
                    <OutputLabel id="outputLabelExmapleId2" width="sm-7" value="{$.fh.docs.component.outputlabel_outputlabel_with_bootstrap_layout_sample_label_width_sm_7} width=&quot;md-7&quot;"/>

                    <InputText id="outputLabelExampleCode1_2" label="{$.fh.docs.component.code}" width="md-12" rowsCount="2">
                        <![CDATA[
                            <OutputLabel id="outputLabelExmapleId1" width="md-5" value="This is sample label with width=&quot;md-5&quot;"/>
                            <OutputLabel id="outputLabelExmapleId2" width="md-7" value="This is sample label with width=&quot;md-7&quot;"/>
                        ]]>
                    </InputText>
                </PanelGroup>

                <PanelGroup width="md-12" label="{$.fh.docs.component.outputlabel_outputlabel_with_hint}" id="_Form_TabContainer_Tab1_PanelGroup3">
                    <OutputLabel width="md-12" hint="{$.fh.docs.component.outputlabel_this_is_hint}" value="{$.fh.docs.component.outputlabel_this_is_sample_label} " id="_Form_TabContainer_Tab1_PanelGroup3_OutputLabel"/>

                    <InputText id="outputLabelExampleCode11" label="{$.fh.docs.component.code}" width="md-12">
                        <![CDATA[
                            <OutputLabel width="md-12" hint="This is hint" value="This is sample label"/>
                        ]]>
                    </InputText>
                </PanelGroup>

                <PanelGroup width="md-12" label="{$.fh.docs.component.outputlabel_outputlabel_with_icon_before_text}" id="_Form_TabContainer_Tab1_PanelGroup4">
                    <OutputLabel id="outputLabelExmapleId7" icon="fa fa-taxi" width="md-12" value="{$.fh.docs.component.outputlabel_the_icon_is_before_text_because_icon_alignment_is_not_set}"/>

                    <InputText id="outputLabelExampleCode7" label="{$.fh.docs.component.code}" width="md-12" rowsCount="2">
                        <![CDATA[
                            <OutputLabel id="outputLabelExmapleId7" icon="fa fa-taxi" width="md-12" value="The icon is before text because icon-alignment is not set." />
                        ]]>
                    </InputText>
                </PanelGroup>

                <PanelGroup width="md-12" label="{$.fh.docs.component.outputlabel_outputlabel_with_icon_after_text}" id="_Form_TabContainer_Tab1_PanelGroup5">
                    <OutputLabel id="outputLabelExmapleId8" icon="fa fa-taxi" iconAlignment="after" width="md-12" value="{$.fh.docs.component.outputlabel_the_icon_is_after_text}"/>

                    <InputText id="outputLabelExampleCode8" label="{$.fh.docs.component.code}" width="md-12" rowsCount="2">
                        <![CDATA[
                            <OutputLabel id="outputLabelExmapleId8" icon="fa fa-taxi" iconAlignment="after" width="md-12" value="The icon is after text." />
                        ]]>
                    </InputText>
                </PanelGroup>

                <PanelGroup width="md-12" label="{$.fh.docs.component.outputlabel_outputlabel_with_binding}" id="_Form_TabContainer_Tab1_PanelGroup6">
                    <OutputLabel id="outputLabelExmapleId6" width="md-12" value="{$.fh.docs.component.outputlabel_outputlabel_with_binding_sample_value_1} {samplePerson.name} {samplePerson.surname}{$.fh.docs.component.outputlabel_outputlabel_with_binding_sample_value_2} {samplePerson.city}."/>

                    <InputText id="outputLabelExampleCode6" label="{$.fh.docs.component.code}" width="md-12" rowsCount="2">
                        <![CDATA[![ESCAPE[<OutputLabel id="outputLabelExmapleId6" width="md-12" value="My name is: {samplePerson.name} {samplePerson.surname}. I live in {samplePerson.city}."/>]]]]>
                    </InputText>
                </PanelGroup>

                <PanelGroup width="md-12" label="{$.fh.docs.component.outputlabel_outputlabel_with_multi_size}" id="_Form_TabContainer_Tab1_PanelGroup7">
                    <OutputLabel id="outputLabelExmapleId9" width="xs-12,sm-10,md-6" value="Lorem ipsum dolor sit amet enim. Etiam ullamcorper. Suspendisse a pellentesque dui, non felis. Maecenas malesuada elit lectus felis, malesuada ultricies. Curabitur et ligula. Ut molestie a, ultricies porta urna. Vestibulum commodo volutpat a, convallis ac, laoreet enim. Phasellus fermentum in, dolor. Pellentesque facilisis. Nulla imperdiet sit amet magna. Vestibulum dapibus, mauris nec malesuada fames ac turpis velit, rhoncus eu, luctus et interdum adipiscing wisi. Aliquam erat ac ipsum. Integer aliquam purus. Quisque lorem tortor fringilla sed, vestibulum id, eleifend justo vel bibendum sapien massa ac turpis faucibus orci luctus non, consectetuer lobortis quis, varius in, purus. Integer ultrices posuere cubilia Curae, Nulla ipsum dolor lacus, suscipit adipiscing. Cum sociis natoque penatibus et ultrices volutpat. Nullam wisi ultricies a, gravida vitae, dapibus risus ante sodales lectus blandit eu, tempor diam pede cursus vitae, ultricies eu, faucibus quis, porttitor eros cursus lectus, pellentesque eget, bibendum a, gravida ullamcorper quam. Nullam viverra consectetuer. Quisque cursus et, porttitor risus. Aliquam sem. In hendrerit nulla quam nunc, accumsan congue. Lorem ipsum primis in nibh vel risus. Sed vel lectus. Ut sagittis, ipsum dolor quam."/>
                    <InputText id="outputLabelExampleCode9" label="{$.fh.docs.component.code}" width="md-12" rowsCount="8">
                        <![CDATA[
                            <OutputLabel id="outputLabelExmapleId9" width="xs-12,sm-10,md-6" value="Lorem ipsum dolor sit amet enim. Etiam ullamcorper. Suspendisse a pellentesque dui, non felis.
                            Maecenas malesuada elit lectus felis, malesuada ultricies. Curabitur et ligula. Ut molestie a, ultricies porta urna. Vestibulum commodo volutpat a, convallis ac, laoreet enim.
                            Phasellus fermentum in, dolor. Pellentesque facilisis. Nulla imperdiet sit amet magna. Vestibulum dapibus, mauris nec malesuada fames ac turpis velit, rhoncus eu, luctus et interdum adipiscing wisi.
                            Aliquam erat ac ipsum. Integer aliquam purus. Quisque lorem tortor fringilla sed, vestibulum id, eleifend justo vel bibendum sapien massa ac turpis faucibus orci luctus non,
                            consectetuer lobortis quis, varius in, purus. Integer ultrices posuere cubilia Curae, Nulla ipsum dolor lacus, suscipit adipiscing. Cum sociis natoque penatibus et ultrices volutpat.
                            Nullam wisi ultricies a, gravida vitae, dapibus risus ante sodales lectus blandit eu, tempor diam pede cursus vitae, ultricies eu, faucibus quis, porttitor eros cursus lectus,
                            pellentesque eget, bibendum a, gravida ullamcorper quam. Nullam viverra consectetuer. Quisque cursus et, porttitor risus. Aliquam sem. In hendrerit nulla quam nunc, accumsan
                            congue. Lorem ipsum primis in nibh vel risus. Sed vel lectus. Ut sagittis, ipsum dolor quam."/>
                        ]]>
                    </InputText>
                </PanelGroup>

                <PanelGroup width="md-12" label="{$.fh.docs.component.outputlabel_outputlabel_with_body_as_value}" id="_Form_TabContainer_Tab1_PanelGroup8">
                    <OutputLabel id="outputLabelExmapleId12" width="xs-12,sm-10,md-6">
                        {$.fh.docs.component.outputlabel_printed_document}
                    </OutputLabel>
                    <InputText id="outputLabelExampleCode12" label="{$.fh.docs.component.code}" width="md-12" rowsCount="3">
                        <![CDATA[
                         <OutputLabel id="outputLabelExmapleId12" width="xs-12,sm-10,md-6">
                            Printed document
                         </OutputLabel>
                        ]]>
                    </InputText>
                </PanelGroup>
        </Tab>

        <Tab label="{$.fh.docs.component.attributes}" id="_Form_TabContainer_Tab2">
            <Table iterator="item" collection="{attributes}" id="_Form_TabContainer_Tab2_Table">
                <Column label="{$.fh.docs.component.attributes_identifier}" value="{item.name}" width="15" id="_Form_TabContainer_Tab2_Table_Column1"/>
                <Column label="{$.fh.docs.component.attributes_type}" value="{item.type}" width="15" id="_Form_TabContainer_Tab2_Table_Column2"/>
                <Column label="{$.fh.docs.component.attributes_boundable}" value="{item.boundable}" width="10" id="_Form_TabContainer_Tab2_Table_Column3"/>
                <Column label="{$.fh.docs.component.attributes_default_value}" value="{item.defaultValue}" width="20" id="_Form_TabContainer_Tab2_Table_Column4"/>
                <Column label="{$.fh.docs.component.attributes_description}" value="{item.description}" width="40" id="_Form_TabContainer_Tab2_Table_Column5"/>
            </Table>
        </Tab>
    </TabContainer>
    <Button id="pBack" label="[icon='fa fa-chevron-left'] {$.fh.docs.component.back}" onClick="backToFormComponentsList()"/>

</Form>