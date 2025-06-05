<?xml version="1.0" encoding="UTF-8"?>
<Form xmlns="http://fh.asseco.com/form/1.0" container="mainForm" id="documentationHolder">
    <Model externalClass="pl.fhframework.docs.forms.component.composits.AttributesTableModel"/>
    <Table iterator="item" collection="{attributes}" id="_Form_Table">
        <Column label="{$.fh.docs.component.attributes_identifier}" value="{item.name}" width="15" id="_Form_Table_Column1"/>
        <Column label="{$.fh.docs.component.attributes_type}" value="{item.type}" width="15" id="_Form_Table_Column2"/>
        <Column label="{$.fh.docs.component.attributes_boundable}" value="{item.boundable}" width="10" id="_Form_Table_Column3"/>
        <Column label="{$.fh.docs.component.attributes_default_value}" value="{item.defaultValue}" width="20" id="_Form_Table_Column4"/>
        <Column label="{$.fh.docs.component.attributes_description}" value="{item.description}" width="40" id="_Form_Table_Column5"/>
    </Table>

</Form>