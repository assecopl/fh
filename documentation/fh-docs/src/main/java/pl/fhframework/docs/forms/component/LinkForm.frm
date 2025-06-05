<?xml version="1.0" encoding="UTF-8"?>
<Form xmlns="http://fh.asseco.com/form/1.0" container="mainForm" id="documentationHolder" label="{componentName}">
    <AvailabilityConfiguration>
        <ReadOnly>linkExampleCode1</ReadOnly>
    </AvailabilityConfiguration>
    <OutputLabel width="md-12" value="{description}" id="_Form_OutputLabel"/>
    <Spacer width="md-12" height="25px" id="_Form_Spacer"/>
    <TabContainer id="_Form_TabContainer">
        <Tab label="{$.fh.docs.component.examples}" id="_Form_TabContainer_Tab1">
            <PanelGroup label="{$.fh.docs.component.link_simple_link_example}" id="_Form_TabContainer_Tab1_PanelGroup1">
                <Link width="md-12" url="http://google.pl" value="{$.fh.docs.component.link_to_google}" id="_Form_TabContainer_Tab1_PanelGroup1_Link1"/>
                <Link width="md-12" url="http://google.pl" id="_Form_TabContainer_Tab1_PanelGroup1_Link2"/>
                <Link width="md-12" url="http://www.downloads.netgear.com/files/GPLnotice.pdf" icon="fa" id="_Form_TabContainer_Tab1_PanelGroup1_Link3"/>

                <InputText width="md-12" id="linkExampleCode1" rowsCount="3">
                    <![CDATA[
<Link url="http://google.pl" value="Link to google"/>
<Link url="http://google.pl"/>
<Link url="http://www.downloads.netgear.com/files/GPLnotice.pdf" icon="fa"/>
                        ]]>
                </InputText>
            </PanelGroup>

            <PanelGroup label="{$.fh.docs.component.link_boundable_link}" id="_Form_TabContainer_Tab1_PanelGroup2">
                <Link width="md-12" value="{$.fh.docs.component.link_boundable_link}" url="{boundedLink}" id="_Form_TabContainer_Tab1_PanelGroup2_Link"/>
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