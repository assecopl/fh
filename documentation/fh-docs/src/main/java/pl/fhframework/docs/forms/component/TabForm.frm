<?xml version="1.0" encoding="UTF-8"?>
<Form xmlns="http://fh.asseco.com/form/1.0" container="mainForm" id="documentationHolder" label="{componentName}">
    <AvailabilityConfiguration>
        <ReadOnly>tabExampleCode1,tabExampleCode3</ReadOnly>
    </AvailabilityConfiguration>
    <OutputLabel width="md-12" value="{description}" id="_Form_OutputLabel"/>
    <Spacer width="md-12" height="25px" id="_Form_Spacer"/>
    <TabContainer id="_Form_TabContainer">
        <Tab label="{$.fh.docs.component.examples}" id="_Form_TabContainer_Tab1">
                <PanelGroup width="md-12" label="{$.fh.docs.component.tab_tab_with_simple_usage}" id="_Form_TabContainer_Tab1_PanelGroup1">
                    <TabContainer id="_Form_TabContainer_Tab1_PanelGroup1_TabContainer">
                        <Tab id="tabCode1" label="{$.fh.docs.component.tab_sample_binding}">
                            <InputText width="md-12" value="{$.fh.docs.component.tab_sample_binding}" onChange="-" id="_Form_TabContainer_Tab1_PanelGroup1_TabContainer_Tab_InputText"/>
                            <OutputLabel width="md-12" value="{$.fh.docs.component.tab_sample_binding}" id="_Form_TabContainer_Tab1_PanelGroup1_TabContainer_Tab_OutputLabel"/>
                        </Tab>
                    </TabContainer>
                    <InputText id="tabExampleCode1" label="{$.fh.docs.component.code}" width="md-12" rowsCount="6">
                        <![CDATA[![ESCAPE[ <TabContainer>
<Tab id="tabCode1" label="{sampleBinding}">
    <InputText value="{sampleBinding}" onChange="-"/>
    <OutputLabel value="{sampleBinding}"/>
</Tab>
</TabContainer>]]]]>
                    </InputText>
                </PanelGroup>

                <PanelGroup width="md-12" label="{$.fh.docs.component.tab_tab_with_bootstrap_layout}" id="_Form_TabContainer_Tab1_PanelGroup2">
                    <TabContainer id="_Form_TabContainer_Tab1_PanelGroup2_TabContainer">
                        <Tab id="tabCode3" label="{$.fh.docs.component.tab_tab_sample_label}">
                            <OutputLabel width="md-12" id="outputLabelExmapleId5" value="Lorem ipsum dolor sit amet enim. Etiam ullamcorper. Suspendisse a pellentesque dui, non felis. Maecenas malesuada elit lectus felis,                             malesuada ultricies. Curabitur et ligula. Ut molestie a, ultricies porta urna. Vestibulum commodo volutpat a, convallis ac, laoreet enim. Phasellus fermentum in, dolor.                             Pellentesque facilisis."/>
                        </Tab>
                    </TabContainer>
                    <InputText id="tabExampleCode3" label="{$.fh.docs.component.code}" width="md-12" rowsCount="7">
                        <![CDATA[![ESCAPE[<TabContainer>
<Tab id="tabCode3" label="Tab sample label">
    <OutputLabel id="outputLabelExmapleId5"
    value="Lorem ipsum dolor sit amet enim. Etiam ullamcorper. Suspendisse a pellentesque dui, non felis. Maecenas malesuada elit lectus felis, malesuada ultricies.
    Curabitur et ligula. Ut molestie a, ultricies porta urna. Vestibulum commodo volutpat a, convallis ac, laoreet enim. Phasellus fermentum in, dolor. Pellentesque facilisis."/>
</Tab>
</TabContainer>]]]]>
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