<?xml version="1.0" encoding="UTF-8"?>
<Form xmlns="http://fh.asseco.com/form/1.0" container="mainForm" id="documentationHolder" label="{componentName}">
    <AvailabilityConfiguration>
        <ReadOnly>buttonExampleCode1,buttonExampleCode2,buttonExampleCode3_3,buttonExampleCode4,buttonExampleCode3_11,buttonExampleCode3_12,buttonExampleCode5,buttonExampleCode4_2,buttonExampleCode6,buttonExampleCode7</ReadOnly>
    </AvailabilityConfiguration>
    <OutputLabel width="md-12" value="{description}" id="_Form_OutputLabel"/>
    <Spacer width="md-12" height="25px" id="_Form_Spacer"/>
    <TabContainer id="_Form_TabContainer">
        <Tab label="{$.fh.docs.component.examples}" id="_Form_TabContainer_Tab1">
                <PanelGroup width="md-12" label="{$.fh.docs.component.button_with_simple_usage}" id="_Form_TabContainer_Tab1_PanelGroup1">
                    <Button id="buttonCode1" label="{$.fh.docs.component.button_simple_button}"/>
                    <InputText id="buttonExampleCode1" label="{$.fh.docs.component.code}" width="md-12" rowsCount="1">
                        <![CDATA[
                            <Button id="buttonCode1" label="Simple Button"/>
                        ]]>
                    </InputText>
                </PanelGroup>

                <PanelGroup width="md-12" label="{$.fh.docs.component.button_with_specified_height}" id="_Form_TabContainer_Tab1_PanelGroup2">
                    <Button id="buttonCode2_1" label="{$.fh.docs.component.button_small_button}" height="40"/>
                    <Button id="buttonCode2_2" label="{$.fh.docs.component.button_large_button}" height="100"/>
                    <InputText id="buttonExampleCode2" label="{$.fh.docs.component.code}" width="md-12" rowsCount="2">
                        <![CDATA[
                            <Button id="buttonCode2_1" label="Small button" height="40" />
                            <Button id="buttonCode2_2" label="Large button" height="100" />
                        ]]>
                    </InputText>
                </PanelGroup>

                <PanelGroup width="md-12" label="{$.fh.docs.component.button_with_bound_label}" id="_Form_TabContainer_Tab1_PanelGroup3">
                    <Button label="{buttonLabel}" onClick="fizzBuzz" id="_Form_TabContainer_Tab1_PanelGroup3_Button"/>
                    <InputText id="buttonExampleCode5" label="{$.fh.docs.component.code}" width="md-12" rowsCount="1">
                        <![CDATA[
                            <Button label="\{buttonLabel\}" onClick="fizzBuzz"/>
                        ]]>
                    </InputText>
                </PanelGroup>

                <PanelGroup width="md-12" label="{$.fh.docs.component.button_with_bootstrap_align_parameter}" id="_Form_TabContainer_Tab1_PanelGroup4">
                    <Button id="buttonCode3_11" label="{$.fh.docs.component.button_right_button}" horizontalAlign="right"/>
                    <InputText id="buttonExampleCode3_11" label="{$.fh.docs.component.code}" width="md-12" rowsCount="1">
                        <![CDATA[
                             <Button id="buttonCode3_11" label="Right Button" horizontalAlign="right" />
                        ]]>
                    </InputText>

                    <Button id="buttonCode3_12" label="{$.fh.docs.component.button_left_button}" horizontalAlign="left"/>
                    <InputText id="buttonExampleCode3_12" label="{$.fh.docs.component.code}" width="md-12" rowsCount="1">
                        <![CDATA[
                            <Button id="buttonCode3_12" label="Left Button" horizontalAlign="left"/>
                        ]]>
                    </InputText>
                </PanelGroup>

                <PanelGroup width="md-12" label="{$.fh.docs.component.button_with_size_parameter}" id="_Form_TabContainer_Tab1_PanelGroup5">
                    <Button id="buttonCode3_1" label="{$.fh.docs.component.button_left_button}" width="md-4"/>
                    <Button id="buttonCode3_2" label="{$.fh.docs.component.button_center_button}" width="md-4"/>
                    <Button id="buttonCode3_3" label="{$.fh.docs.component.button_right_button}" width="md-4"/>
                    <Button id="buttonCode3_4" label="{$.fh.docs.component.button_left_button}" width="md-6"/>
                    <Button id="buttonCode3_5" label="{$.fh.docs.component.button_right_button}" width="md-6"/>
                    <Button id="buttonCode3_6" label="{$.fh.docs.component.button_center_button}" width="md-12"/>
                    <InputText id="buttonExampleCode3_3" label="{$.fh.docs.component.code}" width="md-12" rowsCount="6">
                        <![CDATA[
<Button id="buttonCode3_1" label="Left Button" width="md-4"/>
<Button id="buttonCode3_2" label="Center Button" width="md-4"/>
<Button id="buttonCode3_3" label="Right Button" width="md-4"/>
<Button id="buttonCode3_4" label="Left Button" width="md-6"/>
<Button id="buttonCode3_5" label="Right Button" width="md-6"/>
<Button id="buttonCode3_6" label="Center Button" width="md-12"/>
                        ]]>
                    </InputText>
                </PanelGroup>

                <PanelGroup width="md-12" label="{$.fh.docs.component.button_with_onclick_event}" id="_Form_TabContainer_Tab1_PanelGroup6">
                    <Button id="buttonCode4" label="{$.fh.docs.component.button_clicked}" onClick="buttonClicked()"/>
                    <OutputLabel width="md-12" id="buttonCode4_2" value="{$.fh.docs.component.button_clicked_label} {counter} {$.fh.docs.component.button_clicked_times}"/>
                    <InputText id="buttonExampleCode4" label="{$.fh.docs.component.code}" width="md-12" rowsCount="2">
                        <![CDATA[
<Button id="buttonCode4" label="Clicked Button" onClick="buttonClicked()"/>
<OutputLabel id="buttonCode4_2" value="\{onClickedMessage\}"/>
                        ]]>
                    </InputText>
                </PanelGroup>

                <PanelGroup width="md-12" label="{$.fh.docs.component.button_with_multiple_size}" id="_Form_TabContainer_Tab1_PanelGroup7">
                    <Button id="buttonCode4_1" label="{$.fh.docs.component.button_multi_size}" width="md-4,xs-12,sm-8,lg-10"/>
                    <InputText id="buttonExampleCode4_2" label="{$.fh.docs.component.code}" width="md-12" rowsCount="1">
                        <![CDATA[
                            <Button id="buttonCode4_1" label="Button multi size" width="md-4,xs-12,sm-8,lg-10"/>
                        ]]>
                    </InputText>
                </PanelGroup>

                <PanelGroup width="md-12" label="{$.fh.docs.component.button_with_bootstrap_styles}" id="_Form_TabContainer_Tab1_PanelGroup8">
                    <OutputLabel width="md-12" value="{$.fh.docs.component.button_can_have_6_bootstrap_styles}" id="_Form_TabContainer_Tab1_PanelGroup8_OutputLabel"/>

                    <Button id="buttonCode6_1" label="{$.fh.docs.component.button_without_defined_style}"/>
                    <Spacer width="md-12" id="_Form_TabContainer_Tab1_PanelGroup8_Spacer1"/>
                    <Button id="buttonCode6_2" label="{$.fh.docs.component.button_default}" style="default"/>
                    <Spacer width="md-12" id="_Form_TabContainer_Tab1_PanelGroup8_Spacer2"/>
                    <Button id="buttonCode6_3" label="{$.fh.docs.component.button_primary}" style="primary"/>
                    <Spacer width="md-12" id="_Form_TabContainer_Tab1_PanelGroup8_Spacer3"/>
                    <Button id="buttonCode6_4" label="{$.fh.docs.component.button_success}" style="success"/>
                    <Spacer width="md-12" id="_Form_TabContainer_Tab1_PanelGroup8_Spacer4"/>
                    <Button id="buttonCode6_5" label="{$.fh.docs.component.button_info}" style="info"/>
                    <Spacer width="md-12" id="_Form_TabContainer_Tab1_PanelGroup8_Spacer5"/>
                    <Button id="buttonCode6_6" label="{$.fh.docs.component.button_warning}" style="warning"/>
                    <Spacer width="md-12" id="_Form_TabContainer_Tab1_PanelGroup8_Spacer6"/>
                    <Button id="buttonCode6_7" label="{$.fh.docs.component.button_danger}" style="danger"/>
                    <Spacer width="md-12" id="_Form_TabContainer_Tab1_PanelGroup8_Spacer7"/>
                    <InputText id="buttonExampleCode6" width="md-12" rowsCount="16">
                        <![CDATA[
<OutputLabel
    value="Button can have 6 bootstrap styles. If no style is defined, &quot;primary&quot; style will be used."/>
<Button id="buttonCode6_1" label="Button without defined style"/>
<Spacer/>
<Button id="buttonCode6_2" label="Default" style="default"/>
<Spacer/>
<Button id="buttonCode6_3" label="Primary" style="primary"/>
<Spacer/>
<Button id="buttonCode6_4" label="Success" style="success"/>
<Spacer/>
<Button id="buttonCode6_5" label="Info" style="info"/>
<Spacer/>
<Button id="buttonCode6_6" label="Warning" style="warning"/>
<Spacer/>
<Button id="buttonCode6_7" label="Danger" style="danger"/>
<Spacer/>
                        ]]>
                    </InputText>
                </PanelGroup>

                <PanelGroup width="md-12" label="{$.fh.docs.component.button_with_bound_styles}" id="_Form_TabContainer_Tab1_PanelGroup9">
                    <Button id="buttonCode7_1" label="{$.fh.docs.component.button_button_1}" style="{selectedEnumStyle}" onClick="changeButtonStyle()"/>
                    <Button id="buttonCode7_2" label="{$.fh.docs.component.button_button_2}" style="{selectedEnumStyle}" onClick="changeButtonStyle()"/>
                    <OutputLabel width="md-12" value="{$.fh.docs.component.button_selected_style} {selectedEnumStyle}" id="_Form_TabContainer_Tab1_PanelGroup9_OutputLabel"/>
                    <Button id="buttonCode7_3" label="{$.fh.docs.component.button_reset_style}" onClick="resetButtonStyle()"/>
                    <InputText id="buttonExampleCode7" width="md-12" rowsCount="7">
                        <![CDATA[
<Button id="buttonCode7_1" label="Button 1" style="\{selectedEnumStyle\}"
    onClick="changeButtonStyle()"/>
<Button id="buttonCode7_2" label="Button 2" style="\{selectedEnumStyle\}"
    onClick="changeButtonStyle()"/>
<OutputLabel value="Selected style is: \{selectedStyle\}."/>
<Button id="buttonCode7_3" label="Reset style"
    onClick="resetButtonStyle()"/>
                        ]]>
                    </InputText>
                </PanelGroup>
                <PanelGroup width="md-12" label="{$.fh.docs.component.button_with_confirm_msg}" id="_Form_TabContainer_Tab1_PanelGroup10">
                    <Button id="buttonCode8_1" label="{$.fh.docs.component.button_confirmLabel}" onClick="buttonClickedNotification" confirmationMsg="{$.fh.docs.component.button_confirmMsg}" confirmOnEvent="onClick"/>
                    <InputText id="buttonExampleCode8" width="md-12" rowsCount="3">
                        <![CDATA[
                    <Button id="buttonCode8_1" label="{$.fh.docs.component.button_confirmLabel}" onClick="buttonClicked"
                        confirmationMsg="{$.fh.docs.component.button_confirmMsg}" confirmOnEvent="onClick"/>
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