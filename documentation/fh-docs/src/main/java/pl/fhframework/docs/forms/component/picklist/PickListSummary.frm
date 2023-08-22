<?xml version="1.0" encoding="UTF-8"?>
<Form xmlns="http://fh.asseco.com/form/1.0" id="PickListSummary" container="mainForm" label="{$.fh.docs.component.picklist_template}" formType="MODAL" onManualModalClose="-">
      <Model externalClass="pl.fhframework.docs.forms.component.picklist.PickListExampleUC$PickListModelWrapper"/>

      <PanelGroup label="{$.fh.docs.component.picklist_cars_available_checked}" id="_Form_PanelGroup1">
            <Repeater collection="{elementsAvailableChecked}" iterator="it" id="_Form_PanelGroup1_Repeater">
                  <OutputLabel width="md-12" value="{it.value}" id="_Form_PanelGroup1_Repeater_OutputLabel"/>
            </Repeater>
      </PanelGroup>
      <PanelGroup label="{$.fh.docs.component.picklist_cars_ordered_checked}" id="_Form_PanelGroup2">
            <Repeater collection="{elementsOrderedChecked}" iterator="it" id="_Form_PanelGroup2_Repeater">
                  <OutputLabel width="md-12" value="{it.value}" id="_Form_PanelGroup2_Repeater_OutputLabel"/>
            </Repeater>
      </PanelGroup>
      <PanelGroup label="{$.fh.docs.component.picklist_cars_available_all}" id="_Form_PanelGroup3">
            <Repeater collection="{elementsAvailable}" iterator="it" id="_Form_PanelGroup3_Repeater">
                  <OutputLabel width="md-12" value="{it.value}" id="_Form_PanelGroup3_Repeater_OutputLabel"/>
            </Repeater>
      </PanelGroup>
      <PanelGroup label="{$.fh.docs.component.picklist_cars_ordered_all}" id="_Form_PanelGroup4">
            <Repeater collection="{elementsOrdered}" iterator="it" id="_Form_PanelGroup4_Repeater">
                  <OutputLabel width="md-12" value="{it.value}" id="_Form_PanelGroup4_Repeater_OutputLabel"/>
            </Repeater>
      </PanelGroup>
</Form>