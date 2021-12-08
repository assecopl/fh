<Form xmlns="http://fh.asseco.com/form/1.0" id="declarationHandlingOutline" container="searchForm" formType="STANDARD"
      modalSize="REGULAR">

    <AvailabilityConfiguration>
    </AvailabilityConfiguration>

    <TabContainer styleClasses="tab-container-auto-scroll tab-content-z-index-4" onTabChange="onTabChangeMessages({messagesTabIndex})"
                  activeTabIndex="{messagesTabIndex}">
<!--        <Tab label="{message.name}">-->
<!--            <PanelGroup label="{$.message.metadata.title}">-->
<!--                <Row styleClasses="mb-4">-->
<!--                    <Group width="md-4">-->
<!--                        <OutputLabel width="md-12" value="{$.message.metadata.customsReferenceNumber}"/>-->
<!--                        <OutputLabel width="md-12" value="{message.metadata.customsReferenceNumber}" styleClasses="font-weight-bold"/>-->
<!--                    </Group>-->
<!--                    <Group width="md-4">-->
<!--                        <OutputLabel width="md-12" value="{$.message.metadata.localReferenceNumber}"/>-->
<!--                        <OutputLabel width="md-12" value="{message.metadata.localReferenceNumber}" styleClasses="font-weight-bold"/>-->
<!--                    </Group>-->
<!--                    <Group width="md-4">-->
<!--                        <OutputLabel width="md-12" value="{$.message.metadata.name}"/>-->
<!--                        <OutputLabel width="md-12" value="{message.metadata.name}" styleClasses="font-weight-bold"/>-->
<!--                    </Group>-->
<!--                </Row>-->
<!--                <Row styleClasses="mb-4">-->
<!--                    <Group width="md-4">-->
<!--                        <OutputLabel width="md-12" value="{$.message.metadata.sender.identifier}"/>-->
<!--                        <OutputLabel width="md-12" value="{message.metadata.sender.identifier}" styleClasses="font-weight-bold"/>-->
<!--                    </Group>-->
<!--                    <Group width="md-4">-->
<!--                        <OutputLabel width="md-12" value="{$.message.metadata.recipients.identifier}"/>-->
<!--                        <OutputLabel width="md-12" value=" {message.metadata.recipients[0].identifier} " styleClasses="font-weight-bold"/>-->
<!--                    </Group>-->
<!--                    <Group width="md-4">-->
<!--                        <OutputLabel width="md-12" value="{$.message.metadata.messageIdentification}"/>-->
<!--                        <OutputLabel width="md-12" value="{message.metadata.messageIdentification}" styleClasses="font-weight-bold"/>-->
<!--                    </Group>-->
<!--                </Row>-->
<!--                <Row styleClasses="mb-4">-->
<!--                    <Group width="md-4">-->
<!--                        <OutputLabel width="md-12" value="{$.message.metadata.date}"/>-->
<!--                        <OutputLabel width="md-12" value="{message.metadata.date}" styleClasses="font-weight-bold"/>-->
<!--                    </Group>-->
<!--                    <Group width="md-4">-->
<!--                        <OutputLabel width="md-12" value="{$.message.direction}"/>-->
<!--                        <OutputLabel width="md-12" value="{declarationMessageHelper.getDirectionFromDto(message)}" styleClasses="font-weight-bold"/>-->
<!--                    </Group>-->
<!--                    <Group width="md-4">-->
<!--                        <OutputLabel width="md-12" value="{$.message.metadata.contentType}"/>-->
<!--                        <OutputLabel width="md-12" value="{message.metadata.contentType}" styleClasses="font-weight-bold"/>-->
<!--                    </Group>-->
<!--                </Row>-->
<!--            </PanelGroup>-->
<!--        </Tab>-->
        <Tab label="{$.message.content.title}">
            <PanelGroup styleClasses="muipaper-word-break">
<!--                <InputText width="md-12" rowsCount="16" value="{message.messageContent}" availability="VIEW"/>-->
                <XMLViewerFhDP content="{message.messageContent}" styleClasses="MuiPaper-root" />
            </PanelGroup>
        </Tab>
    </TabContainer>

<!--    <PanelGroup>-->
<!--        <Button label="{$.message.button.sendAgain}"/>-->
<!--        <Button label="{$.common.save}" style="success" onClick="downloadDeclarationMessage({message})"/>-->
<!--    </PanelGroup>-->

    <Model externalClass="pl.fhframework.dp.commons.fh.declaration.message.DeclarationMessageFormModel"/>
</Form>
