<Form container="buttonsForm" xmlns="http://fh.asseco.com/form/1.0">

    <Row elementsHorizontalAlign="LEFT" styleClasses="m-0">
        <Button id="saveMessageButton"
                width="xs-1"
                onClick="downloadDeclarationMessage({message})" label="{$.common.save}"
                hintPlacement="BOTTOM" hintTrigger="HOVER"
                style="success" inlineStyle="line-height: unset;"
                wrapperStyle="margin-bottom: unset; margin-right: 5px;"/>
<!--        <Button id="sendAgainButton"-->
<!--                width="xs-1"-->
<!--                onClick="save" label="{$.message.button.sendAgain}"-->
<!--                hintPlacement="BOTTOM" hintTrigger="HOVER"-->
<!--                inlineStyle="line-height: unset;"-->
<!--                wrapperStyle="margin-bottom: unset; margin-right: 5px;"/>-->
<!--        <Button id="cancelButton"-->
<!--                width="xs-1"-->
<!--                onClick="cancel" label="{$.common.cancel}"-->
<!--                hintPlacement="BOTTOM" hintTrigger="HOVER"-->
<!--                style="default" inlineStyle="line-height: unset;"-->
<!--                wrapperStyle="margin-bottom: unset; margin-right: 5px;"/>-->
    </Row>

    <Model externalClass="pl.fhframework.dp.commons.fh.declaration.message.DeclarationMessageFormModel"/>
</Form>
