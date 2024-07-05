<Form container="mainForm" formType="MODAL" id="OpDataBaseForm"  hideHeader="true" modalSize="XLARGE" xmlns="http://fh.asseco.com/form/1.0">
    <PanelHeaderFhDP title="Base table" info="Add new element" onClick="close" width="md-12" />

    <Row availability="{accessibilityEnum}" width="md-12" wrapperStyle="flex: unset; overflow: auto;" styleClasses="h-100">
        <PanelGroup>
            <Row>
                <InputNumberFhDP width="md-6" label="Id" value="{selectedListElement.id}"/>
            </Row>
            <Row>
                <InputTextFhDP width="md-6" label="Name"
                                value="{selectedListElement.name}"/>
                <InputTextFhDP width="md-6" label="Description"
                                value="{selectedListElement.description}"/>
            </Row>
        </PanelGroup>
    </Row>

    <Row elementsHorizontalAlign="LEFT" styleClasses="panel-footer mb-n3">
        <Button id="btnSave"
                width="xs-13"
                onClick="save" label="Save"
                hintPlacement="BOTTOM"
                inlineStyle="line-height: unset;"
                wrapperStyle="margin-bottom: unset; margin-right: 5px;"/>
        <Button id="btnCancel"
                width="xs-13" availability="EDIT"
                onClick="close" label="Cancel"
                hintPlacement="BOTTOM" style="default"
                inlineStyle="line-height: unset;"
                wrapperStyle="margin-bottom: unset; margin-right: 5px;"/>
    </Row>

    <Model externalClass="pl.fhframework.fhdp.example.table.ExampleTableModel"/>
</Form>
