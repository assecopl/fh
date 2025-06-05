<?xml version="1.0" encoding="UTF-8"?>
<Form xmlns="http://fh.asseco.com/form/1.0" container="mainForm" id="OptionsListSummaryForm" label="Checked elements" formType="MODAL" onManualModalClose="-">
    <Model externalClass="pl.fhframework.docs.uc.OptionsListUC$CheckedElements"/>
    <Repeater collection="{checkedElements}" iterator="elem" id="_Form_Repeater">
        <OutputLabel width="md-12" value="id: {elem.id}   value: {elem.value}" id="_Form_Repeater_OutputLabel"/>
    </Repeater>
</Form>