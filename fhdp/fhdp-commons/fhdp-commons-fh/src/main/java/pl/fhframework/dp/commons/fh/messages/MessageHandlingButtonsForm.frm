<Form container="buttonsForm" xmlns="http://fh.asseco.com/form/1.0">

    <Row elementsHorizontalAlign="LEFT" styleClasses="m-0">
        <Button id="saveMessageButton"
                width="xs-1"
                onClick="downloadDeclarationMessage({selectedMessageTreeElement.obj})" label="{$.common.save}"
                hintPlacement="BOTTOM" hintTrigger="HOVER"
                style="success" inlineStyle="line-height: unset;"
                wrapperStyle="margin-bottom: unset; margin-right: 5px;"/>
    </Row>

    <Model externalClass="pl.fhframework.dp.commons.fh.messages.MessageHandlingFormModel"/>
</Form>
