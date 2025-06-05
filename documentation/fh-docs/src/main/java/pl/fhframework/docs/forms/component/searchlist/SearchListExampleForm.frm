<?xml version="1.0" encoding="UTF-8"?>
<Form xmlns="http://fh.asseco.com/form/1.0" id="SearchListExampleForm" container="mainForm" label="{$.fh.docs.component.searchlist_search_list_example}">
    <AvailabilityConfiguration>
        
    </AvailabilityConfiguration>

    <TabContainer id="_Form_TabContainer">
        <Tab label="{$.fh.docs.component.examples}" id="_Form_TabContainer_Tab1">
            <Include model="searchListModel" ref="SearchList">
                <OnEvent name="onSearch"/>
            </Include>
            <Spacer width="md-12" id="_Form_TabContainer_Tab1_Spacer"/>
            <Button width="md-3" label="{$.fh.docs.component.searchlist_send}" onClick="onSend(this)" id="_Form_TabContainer_Tab1_Button"/>
        </Tab>
        <Tab label="{$.fh.docs.component.documentation}" id="_Form_TabContainer_Tab2">
        </Tab>
    </TabContainer>
</Form>