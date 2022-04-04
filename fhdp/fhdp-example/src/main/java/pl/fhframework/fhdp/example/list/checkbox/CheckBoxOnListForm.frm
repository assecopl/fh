<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<Form xmlns="http://fh.asseco.com/form/1.0"  container="mainForm" formType="STANDARD" modalSize="REGULAR">
    <PanelHeaderFhDP title="Checkbox in list" info="Example" onClick="close" width="md-12" />
    <Row elementsHorizontalAlign="CENTER">
        <Table id="exampleTable" width="md-10"
               iterator="row"
               collection="{elements}"
               selected="{selectedElement}"
               minRows="1" label="Example table with checkBox">
            <Column width="30" label="Text" value="{row.wrapDisabled('text')}" formatter="stringPropertyFormatter"/>
            <Column width="10" label="Some static text" value="{row.wrapDisabled('some static text')}" />
            <Column width="10" label="Crossed column [s&#8205;]" value="[s]{row.element.importantText}[/s]" />
            <Column width="10" label="Crossed column func" value="{row.wrapDisabled('importantText', true)}" />
            <Column width="20" id="test" label="Crossed and not text" value="{row.formatOldNew('importantText', 'flex-basis-100')}" />
            <Column width="10" label="Modified" value="{row.element.modified}"
                    formatter="booleanColumnFormatter"
                    horizontalAlign="center"/>
            <Column  width="10" label="Checked">
                <CheckBox marginTop="20" verticalAlign="top" horizontalAlign="center" availability="{row.element.availability}" id="exampleCheckbox" width="md-12" value="{row.element.checked}" label=" " onChange="modifiedAction" />
            </Column>
        </Table>
    </Row>
    <Row>
        <Spacer width="md-1"/>
        <Button label="Check model" width="md-2" style="success" onClick="checkModel"/>
    </Row>
    <Model externalClass="eu.cdm.example.checkboxInList.CheckBoxOnTheListModel"/>
</Form>

