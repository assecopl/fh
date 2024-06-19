<?xml version="1.0" encoding="UTF-8"?>
<Form xmlns="http://fh.asseco.com/form/1.0" id="SearchListSummary" container="mainForm" label="{$.fh.docs.component.searchlist_template}" formType="MODAL" onManualModalClose="-">
    <Model externalClass="pl.fhframework.docs.forms.component.searchlist.SearchListExampleModel"/>

    <PanelGroup label="{$.fh.docs.component.searchlist_items_searched}" id="_Form_PanelGroup1">
        <Repeater collection="{allElements}" iterator="it" id="_Form_PanelGroup1_Repeater">
            <OutputLabel width="md-12" value="{it.value}" id="_Form_PanelGroup1_Repeater_OutputLabel"/>
        </Repeater>
    </PanelGroup>
    <PanelGroup label="{$.fh.docs.component.searchlist_items_searched_and_checked}" id="_Form_PanelGroup2">
        <Repeater collection="{checkedElements}" iterator="it" id="_Form_PanelGroup2_Repeater">
            <OutputLabel width="md-12" value="{it.value}" id="_Form_PanelGroup2_Repeater_OutputLabel"/>
        </Repeater>
    </PanelGroup>

</Form>