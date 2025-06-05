<?xml version="1.0" encoding="UTF-8"?>
<Form xmlns="http://fh.asseco.com/form/1.0" container="mainForm" id="documentationHolder" label="{componentName}">
    <AvailabilityConfiguration>
        <ReadOnly>
            inputTimestampExampleCode1,inputTimestampExampleCode2,inputTimestampExampleCode3,inputTimestampExampleCode4,inputTimestampExampleCode5,inputTimestampExampleCode6,inputTimestampExampleCode7,inputTimestampExampleCode8,inputTimestampExampleCode9
        </ReadOnly>
    </AvailabilityConfiguration>
    <OutputLabel width="md-12" value="{description}" id="_Form_OutputLabel"/>
    <Spacer width="md-12" height="25px" id="_Form_Spacer"/>
    <TabContainer id="_Form_TabContainer">
        <Tab label="{$.fh.docs.component.examples}" id="_Form_TabContainer_Tab1">
            <PanelGroup width="md-12" label="{$.fh.docs.component.inputtimestamp_time_zone_support}" id="_Form_TabContainer_Tab1_PanelGroup1">
                <OutputLabel width="md-12" id="_Form_TabContainer_Tab1_PanelGroup1_OutputLabel">{$.fh.docs.component.inputtimestamp_time_zone_support.description}</OutputLabel>
            </PanelGroup>
            <PanelGroup width="md-12" label="{$.fh.docs.component.inputtimestamp_inputtimestamp_with_specified_size_and_height}" id="_Form_TabContainer_Tab1_PanelGroup2">
                <InputTimestamp label="{$.fh.docs.component.inputtimestamp_release_date}" width="lg-6" height="40" id="_Form_TabContainer_Tab1_PanelGroup2_InputTimestamp"/>
                <InputText id="inputTimestampExampleCode1" label="{$.fh.docs.component.code}" width="md-12" rowsCount="2">
                    <![CDATA[
<InputTimestamp label="Release date:" width="lg-6" height="40"/>
                        ]]>
                </InputText>
            </PanelGroup>

            <PanelGroup width="md-12" label="{$.fh.docs.component.inputtimestamp_inputtimestamp_with_bootstrap_layout}" id="_Form_TabContainer_Tab1_PanelGroup3">
                <InputTimestamp label="{$.fh.docs.component.inputtimestamp_expiration_date_from}" width="md-4" id="_Form_TabContainer_Tab1_PanelGroup3_InputTimestamp1"/>
                <InputTimestamp label="{$.fh.docs.component.inputtimestamp_expiration_date_to}" width="md-8" id="_Form_TabContainer_Tab1_PanelGroup3_InputTimestamp2"/>
                <InputText id="inputTimestampExampleCode2" label="{$.fh.docs.component.code}" width="md-12" rowsCount="2">
                    <![CDATA[
<InputTimestamp label="Expiration date from:" width="md-4"/>
<InputTimestamp label="Expiration date to:" width="md-8"/>
                        ]]>
                </InputText>
            </PanelGroup>

            <PanelGroup width="md-12" label="{$.fh.docs.component.inputtimestamp_inputtimestamp_with_hint}" id="_Form_TabContainer_Tab1_PanelGroup4">
                <InputTimestamp label="{$.fh.docs.component.inputtimestamp_expiration_date_from}" width="lg-6" hint="{$.fh.docs.component.inputtimestamp_this_is_example_hint}" id="_Form_TabContainer_Tab1_PanelGroup4_InputTimestamp"/>
                <InputText id="inputTimestampExampleCode9" label="{$.fh.docs.component.code}" width="md-12" rowsCount="2">
                    <![CDATA[
<InputTimestamp label="Expiration date from:" width="lg-6" hint="This is example hint"/>
                        ]]>
                </InputText>
            </PanelGroup>

            <PanelGroup width="md-12" label="{$.fh.docs.component.inputtimestamp_inputtimestamp_with_predefined_value}" id="_Form_TabContainer_Tab1_PanelGroup5">
                <InputTimestamp label="{$.fh.docs.component.inputtimestamp_christmas_time}" width="lg-6" value="2016-12-24T18:00" id="_Form_TabContainer_Tab1_PanelGroup5_InputTimestamp"/>
                <InputText id="inputTimestampExampleCode3" label="{$.fh.docs.component.code}" width="md-12" rowsCount="2">
                    <![CDATA[
<InputTimestamp label="Christmas time:" width="lg-6" value="2016-12-24T18:00"/>
                        ]]>
                </InputText>
            </PanelGroup>

            <PanelGroup width="md-12" label="{$.fh.docs.component.inputtimestamp_inputtimestamp_with_required_value}" id="_Form_TabContainer_Tab1_PanelGroup6">
                <InputTimestamp label="{$.fh.docs.component.inputtimestamp_this_field_is_required}" width="lg-6" required="true" onChange="-" value="{inputTimestampRequired}" id="_Form_TabContainer_Tab1_PanelGroup6_InputTimestamp"/>
                <InputText id="inputTimestampExampleCode4" label="{$.fh.docs.component.code}" width="md-12" rowsCount="2">
                    <![CDATA[![ESCAPE[
<InputTimestamp label="This field is required:" width="lg-6" required="true"
                onChange="-" value="{inputTimestampRequired}"/>
                        ]]]]>
                </InputText>
            </PanelGroup>

            <PanelGroup width="md-12" label="{$.fh.docs.component.inputtimestamp_inputtimestamp_with_format_attribute_and_bound_to_java.util.date}" id="_Form_TabContainer_Tab1_PanelGroup7">
                <InputTimestamp id="exampleStamp" label="{$.fh.docs.component.inputtimestamp_this_field_has_dd_mm_yyyy_hh_mm_format}" format="DD/MM/YYYY HH:mm" width="lg-6" required="true" onChange="-" value="{inputTimestampFormatExample}"/>
                <OutputLabel width="md-12" id="labelTimestampExample" formatter="dateWithShortTimeFormatter" value="{inputTimestampFormatExample}"/>
                <InputText id="inputTimestampExampleCode6" label="{$.fh.docs.component.code}" width="md-12" rowsCount="2">
                    <![CDATA[![ESCAPE[
<InputTimestamp id="exampleStamp"
                label="This field has DD/MM/YYYY HH:mm format:"
                format="DD/MM/YYYY HH:mm" width="lg-6" required="true"
                onChange="-" value="{inputTimestampFormatExample}"/>
<OutputLabel id="labelTimestampExample"  formatter="localDateWithShortTimeFormatter"  value="{inputTimestampFormatExample}"/>
                        ]]]]>
                </InputText>
            </PanelGroup>

            <PanelGroup width="md-12" label="{$.fh.docs.component.inputtimestamp_inputtimestamp_with_onchange_event}" id="_Form_TabContainer_Tab1_PanelGroup8">
                <InputTimestamp label="{$.fh.docs.component.inputtimestamp_this_date_is_bound_to_below_date}" width="md-12" value="{inputTimestampOnChange}" onChange="-" id="_Form_TabContainer_Tab1_PanelGroup8_InputTimestamp1"/>
                <InputTimestamp id="onChangeBindedTimestamp1" label="{$.fh.docs.component.inputtimestamp_this_is_the_same_date_as_above}" width="md-12" value="{inputTimestampOnChange}" onChange="-"/>

                <InputText id="inputTimestampExampleCode5" label="{$.fh.docs.component.code}" width="md-12" rowsCount="2">
                    <![CDATA[![ESCAPE[
<InputTimestamp label="This date is bound to below date:" width="md-12"
                                value="{inputTimestampOnChange}" onChange="-"/>
<InputTimestamp id="onChangeBindedTimestamp1"
                label="This is the same date as above:" width="md-12"
                value="{inputTimestampOnChange}" onChange="-"/>
                        ]]]]>
                </InputText>
            </PanelGroup>

            <PanelGroup width="md-12" label="{$.fh.docs.component.inputtimestamp_inputtimestamp_with_multi_size}" id="_Form_TabContainer_Tab1_PanelGroup9">
                <InputTimestamp label="{$.fh.docs.component.inputtimestamp_inputtimestamp_with_multi_size}" width="sm-12,md-9,lg-6" id="_Form_TabContainer_Tab1_PanelGroup9_InputTimestamp"/>
                <InputText id="inputTimestampExampleCode7" label="{$.fh.docs.component.code}" width="md-12" rowsCount="2">
                    <![CDATA[
<InputTimestamp label="InputTimestamp with multi size" width="sm-12,md-9,lg-6"/>
                        ]]>
                </InputText>
            </PanelGroup>

            <PanelGroup width="md-12" label="{$.fh.docs.component.inputtimestamp_inputtimestamp_with_icon_left_aligned}" id="_Form_TabContainer_Tab1_PanelGroup10">
                <InputTimestamp label="{$.fh.docs.component.inputtimestamp_inputtimestamp_with_icon_left_aligned}" width="lg-6" icon="fa fa-calendar" iconAlignment="AFTER" id="_Form_TabContainer_Tab1_PanelGroup10_InputTimestamp"/>
                <InputText id="inputTimestampExampleCode8" label="{$.fh.docs.component.code}" width="md-12" rowsCount="2">
                    <![CDATA[![ESCAPE[
                    <InputTimestamp label="This InputTimestamp icon is right aligned:" width="lg-6" icon="fa fa-calendar" iconAlignment="AFTER"/>
                        ]]]]>
                </InputText>
            </PanelGroup>

            <PanelGroup width="md-12" label="{$.fh.docs.component.inputtimestamp_inputtimestamp_with_format_attribute_and_bound_to_localdatetime}" id="_Form_TabContainer_Tab1_PanelGroup11">
                <InputTimestamp id="exampleStamp10" label="{$.fh.docs.component.inputtimestamp_this_field_has_dd_mm_yyyy_hh_mm_format}" format="DD/MM/YYYY HH:mm" width="lg-6" required="true" onChange="-" value="{localDateTimeExample}"/>
                <OutputLabel width="md-12" id="labelTimestampExample1" value="{localDateTimeExample}" formatter="localDateWithShortTimeFormatter"/>
                <InputText id="inputTimestampExampleCode10" label="{$.fh.docs.component.code}" width="md-12" rowsCount="2">
                    <![CDATA[![ESCAPE[
     <InputTimestamp id="exampleStamp10"
                                        label="{$.fh.docs.component.inputtimestamp_this_field_has_dd_mm_yyyy_hh_mm_format}"
                                        format="DD/MM/YYYY HH:mm" width="lg-6" required="true"
                                        onChange="-" value="{localDateTimeExample}"/>
                    <OutputLabel id="labelTimestampExample1"  formatter="localDateWithShortTimeFormatter"  value="{localDateTimeExample}"/>
                            ]]]]>
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