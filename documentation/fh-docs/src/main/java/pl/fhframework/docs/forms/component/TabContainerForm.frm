<?xml version="1.0" encoding="UTF-8"?>
<Form xmlns="http://fh.asseco.com/form/1.0" container="mainForm" id="documentationHolder" label="{componentName}">
    <AvailabilityConfiguration>
        <ReadOnly>tabContainerExampleCode1,tabContainerExampleCode3,tabContainerExampleCode4,tabContainerExampleCode5,tabContainerExampleCode6,tabContainerExampleCode7</ReadOnly>
        <Invisible>boundTabTwoId</Invisible>
    </AvailabilityConfiguration>
    <OutputLabel width="md-12" value="{description}" id="_Form_OutputLabel"/>
    <Spacer width="md-12" height="25px" id="_Form_Spacer"/>
    <TabContainer id="_Form_TabContainer">
        <Tab label="{$.fh.docs.component.examples}" id="_Form_TabContainer_Tab1">
            <PanelGroup width="md-12" label="{$.fh.docs.component.tabcontainer_tabcontainer_with_simple_usage}" id="_Form_TabContainer_Tab1_PanelGroup1">
                <TabContainer id="_Form_TabContainer_Tab1_PanelGroup1_TabContainer">
                    <Tab id="tabContainerCode1_1" label="Tab 1">
                        <OutputLabel width="md-12" value="{$.fh.docs.component.tabcontainer_this_is_static_content_of} Tab 1" id="_Form_TabContainer_Tab1_PanelGroup1_TabContainer_Tab1_OutputLabel"/>
                    </Tab>
                    <Tab id="tabContainerCode1_2" label="Tab 2">
                        <OutputLabel width="md-12" value="{$.fh.docs.component.tabcontainer_this_is_static_content_of} Tab 2" id="_Form_TabContainer_Tab1_PanelGroup1_TabContainer_Tab2_OutputLabel"/>
                    </Tab>
                </TabContainer>
                <InputText id="tabContainerExampleCode1" label="{$.fh.docs.component.code}" width="md-12" rowsCount="6">
                    <![CDATA[![ESCAPE[<TabContainer>
 <Tab id="tabContainerCode1_1" label="Tab 1">
 <OutputLabel value="{$.fh.docs.component.tabcontainer_this_is_static_content_of} Tab 1"/>
 </Tab>
 <Tab id="tabContainerCode1_2" label="Tab 2">
 <OutputLabel value="{$.fh.docs.component.tabcontainer_this_is_static_content_of} Tab 2"/>
 </Tab>
</TabContainer>]]]]>
                </InputText>
            </PanelGroup>

            <PanelGroup width="md-12" label="{$.fh.docs.component.tabcontainer_tabcontainer_with_selected_active_tab}" id="_Form_TabContainer_Tab1_PanelGroup2">
                <TabContainer activeTabIndex="1" id="_Form_TabContainer_Tab1_PanelGroup2_TabContainer">
                    <Tab id="tabContainerCode4_1" label="Tab 1">
                        <OutputLabel width="md-12" value="{$.fh.docs.component.tabcontainer_this_is_static_content_of} Tab 1" id="_Form_TabContainer_Tab1_PanelGroup2_TabContainer_Tab1_OutputLabel"/>
                    </Tab>
                    <Tab id="tabContainerCode4_2" label="Tab 2">
                        <OutputLabel width="md-12" value="{$.fh.docs.component.tabcontainer_this_is_static_content_of} Tab 2" id="_Form_TabContainer_Tab1_PanelGroup2_TabContainer_Tab2_OutputLabel"/>
                    </Tab>
                </TabContainer>
                <InputText id="tabContainerExampleCode4" label="{$.fh.docs.component.code}" width="md-12" rowsCount="6">
                    <![CDATA[![ESCAPE[<TabContainer activeTabIndex="1">
 <Tab id="tabContainerCode4_1" label="Tab 1">
 <OutputLabel value="{$.fh.docs.component.tabcontainer_this_is_static_content_of} Tab 1"/>
 </Tab>
 <Tab id="tabContainerCode4_2" label="Tab 2">
 <OutputLabel value="{$.fh.docs.component.tabcontainer_this_is_static_content_of} Tab 2"/>
 </Tab>
</TabContainer>]]]]>
                </InputText>
            </PanelGroup>

            <PanelGroup width="md-12" label="{$.fh.docs.component.tabcontainer_tabcontainer_with_bootstrap_layout}" id="_Form_TabContainer_Tab1_PanelGroup3">
                <Spacer width="md-6" id="_Form_TabContainer_Tab1_PanelGroup3_Spacer"/>
                <TabContainer width="md-6" id="_Form_TabContainer_Tab1_PanelGroup3_TabContainer">
                    <Tab id="tabContainerCode3_1" label="Tab 1">
                        <OutputLabel width="md-12" value="{$.fh.docs.component.tabcontainer_this_is_static_content_of} Tab 1" id="_Form_TabContainer_Tab1_PanelGroup3_TabContainer_Tab1_OutputLabel"/>
                    </Tab>
                    <Tab id="tabContainerCode3_2" label="Tab 2">
                        <OutputLabel width="md-12" value="{$.fh.docs.component.tabcontainer_this_is_static_content_of} Tab 2" id="_Form_TabContainer_Tab1_PanelGroup3_TabContainer_Tab2_OutputLabel"/>
                    </Tab>
                </TabContainer>
                <InputText id="tabContainerExampleCode3" label="{$.fh.docs.component.code}" width="md-12" rowsCount="7">
                    <![CDATA[![ESCAPE[<Spacer width="md-6"/>
<TabContainer width="md-6">
    <Tab id="tabContainerCode3_1" label="Tab 1">
    <OutputLabel value="{$.fh.docs.component.tabcontainer_this_is_static_content_of} Tab 1"/>
    </Tab>
    <Tab id="tabContainerCode3_2" label="Tab 2">
    <OutputLabel value="{$.fh.docs.component.tabcontainer_this_is_static_content_of} Tab 2"/>
    </Tab>
</TabContainer>]]]]>
                </InputText>
            </PanelGroup>

            <PanelGroup width="md-12" label="{$.fh.docs.component.tabcontainer_tabcontainer_with_active_tab_binding}" id="_Form_TabContainer_Tab1_PanelGroup4">
                <TabContainer id="boundTabContainer" activeTabIndex="{boundActiveTabIndex}" onTabChange="-">
                    <Tab id="boundTabOne" label="Tab 1">
                        <OutputLabel width="md-12" value="{$.fh.docs.component.tabcontainer_this_is_static_content_of} Tab 1" id="_Form_TabContainer_Tab1_PanelGroup4_TabContainer_Tab1_OutputLabel"/>
                    </Tab>
                    <Tab id="boundTabTwo" label="Tab 2">
                        <OutputLabel width="md-12" value="{$.fh.docs.component.tabcontainer_this_is_static_content_of} Tab 2" id="_Form_TabContainer_Tab1_PanelGroup4_TabContainer_Tab2_OutputLabel"/>
                    </Tab>
                </TabContainer>
                <OutputLabel width="md-12" value="{$.fh.docs.component.tabcontainer_selected_tab_index_is}: {boundActiveTabIndex}" id="_Form_TabContainer_Tab1_PanelGroup4_OutputLabel"/>
                <InputText label="{$.fh.docs.component.tabcontainer_type_0_or_1}" width="md-12" rowsCount="1" value="{boundActiveTabIndex}" onChange="-" id="_Form_TabContainer_Tab1_PanelGroup4_InputText1"/>
                <RadioOptionsGroup width="md-4" label="{$.fh.docs.component.tabcontainer_active_tab_id_automatic_conversion}:" value="{boundActiveTabIndex}" values="{radioToActiveTabBinding}" onChange="-" id="_Form_TabContainer_Tab1_PanelGroup4_RadioOptionsGroup"/>
                <InputText id="tabContainerExampleCode5" label="{$.fh.docs.component.code}" width="md-12" rowsCount="15">
                    <![CDATA[![ESCAPE[<TabContainer id="boundTabContainer" activeTabIndex="{boundActiveTabIndex}" onTabChange="-">
<Tab id="boundTabOne" label="Tab 1">
        <OutputLabel value="This is static content of Tab 1" />
</Tab>
<Tab id="boundTabTwo" label="Tab 2">
        <OutputLabel value="This is static content of Tab 2" />
</Tab>
</TabContainer>
<OutputLabel value="Selected tab index is: {boundActiveTabIndex}" />
<InputText label="Type 0 or 1" width="md-12" rowsCount="1" value="{boundActiveTabIndex}" onChange="-"/>
<RadioOptionsGroup width="md-4" label="Active tab Id (automatic conversion):" value="{boundActiveTabIndex}" values="{radioToActiveTabBinding}" onChange="-" />]]]]>
                </InputText>
            </PanelGroup>

            <PanelGroup width="md-12" label="{$.fh.docs.component.tabcontainer_tabcontainer_with_active_tab_binding}" id="_Form_TabContainer_Tab1_PanelGroup5">
                <TabContainer id="boundTabContainerId" activeTabId="{boundActiveTabId}" onTabChange="-">
                    <Tab id="boundTabOneId" label="Tab 1">
                        <OutputLabel width="md-12" value="{$.fh.docs.component.tabcontainer_this_is_static_content_of} Tab 1" id="_Form_TabContainer_Tab1_PanelGroup5_TabContainer_Tab1_OutputLabel"/>
                    </Tab>
                    <Tab id="boundTabTwoId" label="Tab 2">
                        <OutputLabel width="md-12" value="{$.fh.docs.component.tabcontainer_this_is_static_content_of} Tab 2" id="_Form_TabContainer_Tab1_PanelGroup5_TabContainer_Tab2_OutputLabel"/>
                    </Tab>
                    <Tab id="boundTabThreeId" label="Tab 3">
                        <OutputLabel width="md-12" value="{$.fh.docs.component.tabcontainer_this_is_static_content_of} Tab 3" id="_Form_TabContainer_Tab1_PanelGroup5_TabContainer_Tab3_OutputLabel"/>
                    </Tab>
                </TabContainer>
                <OutputLabel width="md-12" value="{$.fh.docs.component.tabcontainer_selected_tab_index_is}: {boundActiveTabId}" id="_Form_TabContainer_Tab1_PanelGroup5_OutputLabel"/>
                <InputText id="tabContainerExampleCode7" label="{$.fh.docs.component.code}" width="md-12" rowsCount="15">
                    <![CDATA[![ESCAPE[<TabContainer id="boundTabContainerId" activeTabId="{boundActiveTabId}" onTabChange="-">
<Tab id="boundTabOneId" label="Tab 1">
        <OutputLabel value="This is static content of Tab 1" />
</Tab>
<Tab id="boundTabTwoId" label="Tab 2">
        <OutputLabel value="This is static content of Tab 2" />
</Tab>
<Tab id="boundTabThreeId" label="Tab 3">
        <OutputLabel value="This is static content of Tab 3" />
</Tab>
</TabContainer>
<OutputLabel value="Selected tab index is: {boundActiveTabId}" />]]]]>
                </InputText>
            </PanelGroup>

            <PanelGroup width="md-12" label="{$.fh.docs.component.tabcontainer_tabcontainer_with_multi_size}" id="_Form_TabContainer_Tab1_PanelGroup6">
                <TabContainer width="xs-12,sm-10,md-6,lg-4" id="_Form_TabContainer_Tab1_PanelGroup6_TabContainer">
                    <Tab id="tabContainerCode6_1" label="Tab 1">
                        <OutputLabel width="md-12" value="{$.fh.docs.component.tabcontainer_this_is_static_content_of} Tab 1" id="_Form_TabContainer_Tab1_PanelGroup6_TabContainer_Tab1_OutputLabel"/>
                    </Tab>
                    <Tab id="tabContainerCode6_2" label="Tab 2">
                        <OutputLabel width="md-12" value="{$.fh.docs.component.tabcontainer_this_is_static_content_of} Tab 2" id="_Form_TabContainer_Tab1_PanelGroup6_TabContainer_Tab2_OutputLabel"/>
                    </Tab>
                </TabContainer>
                <InputText id="tabContainerExampleCode6" label="{$.fh.docs.component.code}" width="md-12" rowsCount="11">
                    <![CDATA[![ESCAPE[ <TabContainer width="xs-12,sm-10,md-6,lg-4">
<Tab id="tabContainerCode6_1" label="Tab 1">
        <OutputLabel value="This is static content of Tab 1" />
</Tab>
<Tab id="tabContainerCode6_2" label="Tab 2">
        <OutputLabel value="This is static content of Tab 2" />
</Tab>]]]]>
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