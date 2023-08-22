<?xml version="1.0" encoding="UTF-8"?>
<Form xmlns="http://fh.asseco.com/form/1.0" container="mainForm" id="documentationHolder" label="{$.fh.docs.exceptions_fh_exceptions}">

    <PanelGroup label="{$.fh.docs.exceptions_fh_exceptions}" id="_Form_PanelGroup1">
        <OutputLabel width="md-12" value="{$.fh.docs.exceptions_fh_exceptions}. {$.fh.docs.exceptions_fh_specific_exceptions}" id="_Form_PanelGroup1_OutputLabel1"/>

        <OutputLabel width="md-12" value="{$.fh.docs.exceptions_handling}" id="_Form_PanelGroup1_OutputLabel2"/>
        <Table id="fhException" label="{$.fh.docs.exceptions_fh_exceptions}" collection="{describedExceptions}" iterator="desc">
            <Column label="{$.fh.docs.exceptions_class}" width="30" id="_Form_PanelGroup1_Table_Column1">
                <OutputLabel width="md-12" value="{desc.name}" id="_Form_PanelGroup1_Table_Column1_OutputLabel"/>
            </Column>
            <Column label="{$.fh.docs.exceptions_package}" width="30" id="_Form_PanelGroup1_Table_Column2">
                <OutputLabel width="md-12" value="{desc.packageName}" id="_Form_PanelGroup1_Table_Column2_OutputLabel"/>
            </Column>
            <Column label="{$.fh.docs.exceptions_description}" width="60" id="_Form_PanelGroup1_Table_Column3">
                <OutputLabel width="md-12" value="{desc.description}" id="_Form_PanelGroup1_Table_Column3_OutputLabel"/>
            </Column>
        </Table>
    </PanelGroup>

    <PanelGroup label="{$.fh.docs.exceptions_constructor_details}" id="_Form_PanelGroup2">
        <Table id="constructorDetails" iterator="desc" collection="{describedExceptions}">
            <Iterator id="exc" collection="{desc.constructors}"/>
            <Iterator id="param" collection="{exc.parameters}"/>
            <Column label="{$.fh.docs.exceptions_class}" value="[b]{desc.name}[/b]" id="_Form_PanelGroup2_Table_Column1"/>
            <Column label="{$.fh.docs.exceptions_constructor}" width="md-4" id="_Form_PanelGroup2_Table_Column2">
                <Column label="{$.fh.docs.exceptions_declaration}" value="{exc.declaration}" iterationRef="exc" id="_Form_PanelGroup2_Table_Column2_Column1"/>
                <Column label="{$.fh.docs.exceptions_description}" value="{exc.description}" iterationRef="exc" id="_Form_PanelGroup2_Table_Column2_Column2"/>
                <Column label="{$.fh.docs.exceptions_parameters}" width="md-6" id="_Form_PanelGroup2_Table_Column2_Column3">
                    <Column label="{$.fh.docs.exceptions_class}" value="{param.className}" iterationRef="param" id="_Form_PanelGroup2_Table_Column2_Column3_Column1"/>
                    <Column label="{$.fh.docs.exceptions_name}" value="{param.parameterName}" iterationRef="param" id="_Form_PanelGroup2_Table_Column2_Column3_Column2"/>
                    <Column label="{$.fh.docs.exceptions_description}" value="{param.description}" iterationRef="param" id="_Form_PanelGroup2_Table_Column2_Column3_Column3"/>
                </Column>
            </Column>
        </Table>
    </PanelGroup>
</Form>