<?xml version="1.0" encoding="UTF-8"?>
<Form xmlns="http://fh.asseco.com/form/1.0" id="PickListExampleForm" container="mainForm" label="{$.fh.docs.component.picklist_picklist_example}">

    <TabContainer id="_Form_TabContainer">
        <Tab label="{$.fh.docs.component.picklist_example}" id="_Form_TabContainer_Tab1">
            <OutputLabel width="md-12" value="{$.fh.docs.component.picklist_order_cars}" id="_Form_TabContainer_Tab1_OutputLabel1"/>

            <Include model="carsListModel" ref="PickList">
                <OnEvent name="onList1Changed"/>
                <OnEvent name="onList2Changed"/>
            </Include>

            <OutputLabel width="md-12" value=" " id="_Form_TabContainer_Tab1_OutputLabel2"/>
            <OutputLabel width="md-12" value=" " id="_Form_TabContainer_Tab1_OutputLabel3"/>

            <Button label="{$.fh.docs.service.save}" onClick="saveCarsList(this)" id="_Form_TabContainer_Tab1_Button"/>
        </Tab>
        <Tab label="{$.fh.docs.component.picklist_documentation}" id="_Form_TabContainer_Tab2">
        </Tab>
    </TabContainer>
</Form>