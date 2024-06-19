<?xml version="1.0" encoding="UTF-8"?>
<Form xmlns="http://fh.asseco.com/form/1.0" container="mainForm" id="documentationHolder" label="{componentName}">
    <AvailabilityConfiguration>
        <ReadOnly>spacerExampleCode1,spacerExampleCode2,spacerExampleCode3,spacerExampleCode4</ReadOnly>
    </AvailabilityConfiguration>
    <OutputLabel width="md-12" value="{description}" id="_Form_OutputLabel"/>
    <Spacer width="md-12" height="25px" id="_Form_Spacer"/>
    <TabContainer id="_Form_TabContainer">
        <Tab label="{$.fh.docs.component.examples}" id="_Form_TabContainer_Tab1">
                <PanelGroup width="md-12" label="{$.fh.docs.component.spacer_spacer_with_double_bootstrap_space}" id="_Form_TabContainer_Tab1_PanelGroup1">
                    <Spacer width="md-2" id="_Form_TabContainer_Tab1_PanelGroup1_Spacer"/>
                    <Button id="buttonCodeSpacer1" label="{$.fh.docs.component.spacer_animals}" width="md-2"/>

                    <InputText id="spacerExampleCode1" label="{$.fh.docs.component.code}" width="md-12" height="70" rowsCount="2">
                        <![CDATA[![ESCAPE[<Spacer width="md-2"/>
<Button id="buttonCodeSpacer1" label="Animals" width="md-2"/>]]]]>
                    </InputText>
                </PanelGroup>

                <PanelGroup width="md-12" label="{$.fh.docs.component.spacer_spacer_with_single_bootstrap_spaces}" id="_Form_TabContainer_Tab1_PanelGroup2">
                    <Spacer width="md-1" id="_Form_TabContainer_Tab1_PanelGroup2_Spacer1"/>
                    <Button id="buttonCodeSpacer2" label="{$.fh.docs.component.spacer_dog}" width="md-2"/>
                    <Spacer width="md-1" id="_Form_TabContainer_Tab1_PanelGroup2_Spacer2"/>
                    <Button id="buttonCodeSpacer3" label="{$.fh.docs.component.spacer_cat}" width="md-2"/>
                    <Spacer width="md-1" id="_Form_TabContainer_Tab1_PanelGroup2_Spacer3"/>
                    <Button id="buttonCodeSpacer4" label="{$.fh.docs.component.spacer_horse}" width="md-2"/>

                    <InputText id="spacerExampleCode2" label="{$.fh.docs.component.code}" width="md-12" height="200" rowsCount="6">
                        <![CDATA[![ESCAPE[<Spacer width="md-1"/>
<Button id="buttonCodeSpacer2" label="Dog" width="md-2"/>
<Spacer width="md-1"/>
<Button id="buttonCodeSpacer3" label="Cat" width="md-2"/>
<Spacer width="md-1"/>
<Button id="buttonCodeSpacer4" label="Horse" width="md-2"/>]]]]>
                    </InputText>
                </PanelGroup>

                <PanelGroup width="md-12" label="{$.fh.docs.component.spacer_spacer_with_ten_bootstrap_spaces}" id="_Form_TabContainer_Tab1_PanelGroup3">
                    <Spacer width="md-10" id="_Form_TabContainer_Tab1_PanelGroup3_Spacer"/>
                    <InputText id="inputTextSpacerExample1" label="{$.fh.docs.component.spacer_please_insert_your_text_here}" width="md-2"/>

                    <InputText id="spacerExampleCode3" label="{$.fh.docs.component.code}" width="md-12" height="70" rowsCount="2">
                        <![CDATA[![ESCAPE[<Spacer width="md-10"/>
<InputText id="inputTextSpacerExample1" label="Please insert your text here" width="md-2"/>]]]]>
                    </InputText>
                </PanelGroup>

                <PanelGroup width="md-12" label="{$.fh.docs.component.spacer_spacer_with_multi_size}" id="_Form_TabContainer_Tab1_PanelGroup4">
                    <Spacer width="xs-10,sm-8,md-6,lg-4" id="_Form_TabContainer_Tab1_PanelGroup4_Spacer"/>
                    <InputText id="inputTextSpacerExample4" label="{$.fh.docs.component.spacer_please_insert_your_text_here}" width="xs-2,sm-4,md-6,lg-8"/>

                    <InputText id="spacerExampleCode4" label="{$.fh.docs.component.code}" width="md-12" rowsCount="2">
                        <![CDATA[![ESCAPE[<Spacer width="xs-10,sm-8,md-6,lg-4"/>
<InputText id="inputTextSpacerExample4" label="Please insert your text here" width="xs-2,sm-4,md-6,lg-8"/>]]]]>
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