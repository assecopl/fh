<?xml version="1.0" encoding="UTF-8"?>
<Form xmlns="http://fh.asseco.com/form/1.0" container="mainForm" id="documentationHolder" label="{$.fh.docs.service.componentlist_standard_form_components}">

    <Repeater collection="{formElements}" iterator="formElement" id="_Form_Repeater">
        <PanelGroup label="{formElement.name}" id="_Form_Repeater_PanelGroup">
            <Table onRowClick="display(selectedFormElement)" collection="{formElement.components}" selected="{selectedFormElement}" iterator="item" id="_Form_Repeater_PanelGroup_Table">
                <Column label="{$.fh.docs.service.componentlist_identifier}" width="15" id="_Form_Repeater_PanelGroup_Table_Column1">
                    <OutputLabel width="md-12" icon="{item.icon}" iconAlignment="before" value="{item.componentName}" id="_Form_Repeater_PanelGroup_Table_Column1_OutputLabel"/>
                </Column>
                <Column label="{$.fh.docs.service.componentlist_description}" value="{item.description}" width="85" id="_Form_Repeater_PanelGroup_Table_Column2"/>
            </Table>
        </PanelGroup>
    </Repeater>
</Form>