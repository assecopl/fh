<?xml version="1.0" encoding="UTF-8"?>
<Form xmlns="http://fh.asseco.com/form/1.0" container="mainForm" id="documentationHolder" label="{componentName}">
    <AvailabilityConfiguration>
        <ReadOnly>
            imageExampleCode1_1,imageExampleCode1_2,imageExampleCode2,imageExampleCode3,imageExampleCode4,imageExampleCode5,imageExampleCode6_1,imageExampleCode7_1
        </ReadOnly>
    </AvailabilityConfiguration>
    <OutputLabel width="md-12" value="{description}" id="_Form_OutputLabel"/>
    <Spacer width="md-12" height="25px" id="_Form_Spacer"/>
    <TabContainer id="_Form_TabContainer">
        <Tab label="{$.fh.docs.component.examples}" id="_Form_TabContainer_Tab1">
                <PanelGroup width="md-12" label="{$.fh.docs.component.image_with_simple_usage}" id="_Form_TabContainer_Tab1_PanelGroup1">
                    <Image id="imageCode1_1" src="{mapSrc}" width="md-4"/>
                    <InputText id="imageExampleCode1_1" label="{$.fh.docs.component.code}" width="md-12" rowsCount="1">
                        <![CDATA[
<Image id="imageCode1_1" src="{mapSrc}"/>
                        ]]>
                    </InputText>

                    <Image id="imageCode1_2" src="images/location.jpg" width="md-4"/>
                    <InputText id="imageExampleCode1_2" label="{$.fh.docs.component.code}" width="md-12" rowsCount="1">
                        <![CDATA[
<Image id="imageCode1_2" src="images/location.jpg"/>
                        ]]>
                    </InputText>

                </PanelGroup>

                <PanelGroup width="md-12" label="{$.fh.docs.component.image_with_specified_height}" id="_Form_TabContainer_Tab1_PanelGroup2">
                    <Image id="imageCode2" height="150" src="images/location.jpg" width="md-4"/>
                    <InputText id="imageExampleCode2" label="{$.fh.docs.component.code}" width="md-12" rowsCount="1">
                        <![CDATA[
<Image id="imageCode2" height="150" src="images/location.jpg"/>
                        ]]>
                    </InputText>
                </PanelGroup>

                <PanelGroup width="md-12" label="{$.fh.docs.component.image_with_bootstrap_layout}" id="_Form_TabContainer_Tab1_PanelGroup3">
                    <Image id="imageCode3_1" width="md-4" src="images/location.jpg"/>
                    <Image id="imageCode3_2" width="md-4" src="images/location.jpg"/>
                    <Image id="imageCode3_3" width="md-4" src="images/location.jpg"/>
                    <InputText id="imageExampleCode3" label="{$.fh.docs.component.code}" width="md-12" rowsCount="3">
                        <![CDATA[
<Image id="imageCode3_1" width="md-4" src="images/location.jpg"/>
<Image id="imageCode3_2" width="md-4" src="images/location.jpg"/>
<Image id="imageCode3_3" width="md-4" src="images/location.jpg"/>
                        ]]>
                    </InputText>
                </PanelGroup>

                <PanelGroup width="md-12" label="{$.fh.docs.component.image_with_onclick_event}" id="_Form_TabContainer_Tab1_PanelGroup4">
                    <Image id="imageCode4" src="images/location.jpg" onClick="imageClicked()" width="md-4"/>
                    <OutputLabel width="md-12" id="imageCode4_2" value="{$.fh.docs.component.image_clicked_label} {counter} {$.fh.docs.component.image_clicked_times}"/>
                    <InputText id="imageExampleCode4" label="{$.fh.docs.component.code}" width="md-12" rowsCount="2">
                        <![CDATA[
<Image id="imageCode4" src="images/location.jpg" onClick="imageClicked()"/>
<OutputLabel id="imageCode4_2" value="\{onClickedMessage\}"/>
                        ]]>
                    </InputText>
                </PanelGroup>

                <PanelGroup width="md-12" label="{$.fh.docs.component.image_with_onareaclick_event}" id="_Form_TabContainer_Tab1_PanelGroup5">
                    <Image id="imageCode5" src="images/location_{map}.jpg" onAreaClick="imageSelectArea(EVENT.optionalValue)" width="md-4" imageAreas="(#mock,0,0,150,150);(#mock2,150,150,300,300)"/>
                    <OutputLabel width="md-12" id="imageCode5_2" value="{$.fh.docs.component.image_clicked_label} {counterArea} {$.fh.docs.component.image_clicked_times}"/>
                    <InputText id="imageExampleCode5" label="{$.fh.docs.component.code}" width="md-12" rowsCount="4">
                        <![CDATA[
<Image id="imageCode5" src="images/location_{map}.jpg"
       onAreaClick="imageSelectArea(EVENT.optionalValue)"
       imageAreas="(#mock,0,0,150,150);(#mock2,150,150,300,300)"/>
<OutputLabel id="imageCode5_2" value="{onAreaClickedMessage}"/>
                        ]]>
                    </InputText>
                </PanelGroup>

                <PanelGroup width="md-12" label="{$.fh.docs.component.image_with_multi_size}" id="_Form_TabContainer_Tab1_PanelGroup6">
                    <Image id="imageCode6_1" src="{mapSrc}" width="xs-6,sm-8,md-12,lg-4"/>
                    <InputText id="imageExampleCode6_1" label="{$.fh.docs.component.code}" width="md-12" rowsCount="1">
                        <![CDATA[
<Image id="imageCode6_1" src="\{mapSrc\}" width="xs-6,sm-8,md-12,lg-4"/>
                        ]]>
                    </InputText>
                </PanelGroup>

                <PanelGroup width="md-12" label="{$.fh.docs.component.image_with_label}" id="_Form_TabContainer_Tab1_PanelGroup7">
                    <Image id="imageCode7_1" src="{mapSrc}" width="xs-6,sm-8,md-10,lg-4" label="Image label" labelPosition="down"/>
                    <InputText id="imageExampleCode7_1" label="{$.fh.docs.component.code}" width="md-12" rowsCount="1">
                        <![CDATA[
                    <Image id="imageCode7_1" src="{mapSrc}" width="xs-6,sm-8,md-12,lg-4" label="Image label" labelPosition="down"/>
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