<Form container="buttonsForm" xmlns="http://fh.asseco.com/form/1.0">

    <Row elementsHorizontalAlign="LEFT" styleClasses="m-0">
        <Button id="saveButton"
                width="xs-1"
                onClick="save" label="{$.fhdp.common.save}"
                hintPlacement="BOTTOM" hintTrigger="HOVER"
                style="success" inlineStyle="line-height: unset;"
                wrapperStyle="margin-bottom: unset; margin-right: 5px;"/>
        <Button id="cancelButton"
                width="xs-1"
                onClick="cancel" label="{$.fhdp.common.cancel}"
                hintPlacement="BOTTOM" hintTrigger="HOVER"
                style="default" inlineStyle="line-height: unset;"
                wrapperStyle="margin-bottom: unset; margin-right: 5px;"/>
    </Row>
</Form>
