<Form container="mainForm" id="DeclarationHandlingFormMessages" xmlns="http://fh.asseco.com/form/1.0">

    <Row width="md-12" styleClasses="main-page--flex h-100" wrapperStyle="height: 100%;">

        <Group id="basicDataAndCommonCommands" styleClasses="group-header box-shadow__unset">
            <Row id="basePanelHeader" width="md-12" elementsHorizontalAlign="AROUND"
                 styleClasses="row-panel panel-header flex-nowrap"
                 inlineStyle="flex-wrap: nowrap; justify-content: space-between !important;">
                <Group
                        width="xs-1"
                        styleClasses="panel-header-group-base"
                        wrapperStyle="
                            padding: var(--padding-bigger);
                            justify-self: start;
                            user-select: text;
                            margin-right: 30px;
                            flex-grow: 1;
                            flex-basis: 0;
                            padding-right: unset;
                            min-width: 53%;">
                    <Group styleClasses="panel-info--container">

                        <OutputLabel availability="VIEW" width="md-12"
                                     wrapperStyle="flex: initial; padding-right: unset; padding-left: unset;"
                                     styleClasses="output-label-no-padding panel-info--weight white-space--break-spaces"
                                     value="{$.message.content.title}"
                                     hint="Formal Date"/>
                    </Group>
                    <OutputLabel availability="VIEW" width="md-12"
                                 styleClasses="panel-title output-label-no-padding white-space--break-spaces"
                                 inlineStyle="word-break: break-word;"
                                 value="{selectedMessageTreeElement.obj.metadata.customsReferenceNumber}"
                                 hint="MRN"/>
                    <OutputLabel availability="VIEW" width="md-12"
                                 styleClasses="panel-status output-label-no-padding white-space--break-spaces"
                                 inlineStyle="margin-top: 3px !important;"
                                 value="{selectedMessageTreeElement.obj.metadata.localReferenceNumber}"
                                 hint="LRN"/>
                </Group>
                <ButtonGroup id="buttonGroup" horizontalAlign="right"
                             wrapperStyle="padding: var(--padding-bigger);"
                             styleClasses="button-icon-group justify-content-end panel-options-base flex-nowrap panel-option--group-button" width="xs-1"
                             inlineStyle="flex-wrap: nowrap;">
                    <Button id="cancelButton" style="default" styleClasses="panel-option margin__unset"
                            horizontalAlign="right"
                            label="[b][icon='fas fa-times'][/b]"
                            hint="{$.common.close}" hintTrigger="HOVER"
                            hintPlacement="BOTTOM"
                            onClick="close"/>
                </ButtonGroup>
            </Row>
        </Group>

        <Group width="md-12" id="tabContainerGroup" wrapperStyle="height: 100%; overflow: auto;"
               styleClasses="h-100 overflow-auto mb-0">

            <TabContainer wrapperStyle="overflow: auto; height: 100%;" styleClasses="tab-container-auto-scroll"
                onTabChange="onTabChange()" activeTabIndex="{activeTabIndex}">
                <Tab label="{selectedMessageTreeElement.obj.metadata.name}">
                    <PanelGroup styleClasses="p-3">
                        <Row styleClasses="mb-4">
                            <Group width="md-4">
                                <OutputLabel width="md-12" value="{$.message.search.details.lrn}"/>
                                <OutputLabel width="md-12" value="{selectedMessageTreeElement.obj.metadata.localReferenceNumber}" styleClasses="font-weight-bold"/>
                            </Group>
                            <Group width="md-4">
                                <OutputLabel width="md-12" value="{$.message.search.details.mrn}"/>
                                <OutputLabel width="md-12" value="{selectedMessageTreeElement.obj.metadata.customsReferenceNumber}" styleClasses="font-weight-bold"/>
                            </Group>
                            <Group width="md-4">
                                <OutputLabel width="md-12" value="{$.message.search.name}"/>
                                <OutputLabel width="md-12" value="{selectedMessageTreeElement.obj.metadata.name}" styleClasses="font-weight-bold"/>
                            </Group>
                        </Row>
                        <Row styleClasses="mb-4">
                            <Group width="md-4">
                                <OutputLabel width="md-12" value="{$.message.repositoryId}"/>
                                <OutputLabel width="md-12" value="{selectedMessageTreeElement.obj.repositoryId}" styleClasses="font-weight-bold"/>
                            </Group>
                            <Group width="md-4">
                                <OutputLabel width="md-12" value="{$.message.search.details.messageIdentifier}"/>
                                <OutputLabel width="md-12" value="{selectedMessageTreeElement.obj.metadata.messageIdentification}" styleClasses="font-weight-bold"/>
                            </Group>
                            <Group width="md-4">
                                <OutputLabel width="md-12" value="{$.message.metadata.contentType}"/>
                                <OutputLabel width="md-12" value="{selectedMessageTreeElement.obj.metadata.contentType}" styleClasses="font-weight-bold"/>
                            </Group>
                        </Row>
                        <Row styleClasses="mb-4">
                            <Group width="md-4">
                                <OutputLabel width="md-12" value="{$.message.search.details.time}"/>
                                <OutputLabel width="md-12" value="{selectedMessageTreeElement.obj.stored}" styleClasses="font-weight-bold"/>
                            </Group>
                        </Row>
                        <Row styleClasses="mb-4">
                            <Group width="md-4" styleClasses="pr-10">
                                <OutputLabel width="md-12" value="{$.message.metadata.sender.identifier}"/>
                                <Table collection="{selectedMessageTreeElement.obj.metadata.recipients}" iterator="row">
                                    <Column label="{$.message.domain}" value="{selectedMessageTreeElement.obj.metadata.sender.domain}"/>
                                    <Column label="{$.message.identifier}" value="{selectedMessageTreeElement.obj.metadata.sender.identifier}"/>
                                </Table>
                            </Group>
                            <Group width="md-4" styleClasses="pr-10">
                                <OutputLabel width="md-12" value="{$.message.metadata.recipients.identifier}"/>
                                <Table collection="{selectedMessageTreeElement.obj.metadata.recipients}" iterator="row">
                                    <Column label="{$.message.domain}" value="{row.domain}"/>
                                    <Column label="{$.message.identifier}" value="{row.identifier}"/>
                                </Table>
                            </Group>
                        </Row>
                    </PanelGroup>
                </Tab>
                <Tab label="{$.message.otherMetadata}">
                    <PanelGroup>
                        <Table collection="{selectedMessageTreeElement.obj.metadata.otherMetadata}" iterator="row">
                            <Column label="{$.message.search.name}" value="{row.name}"/>
                            <Column label="{$.message.value}" value="{row.value}"/>
                        </Table>
                    </PanelGroup>
                </Tab>
                <Tab label="{$.message.content}">
                    <PanelGroup>
                        <XMLViewerFhDP content="{selectedMessageTreeElement.obj.content}" styleClass="MuiPaper-root" />
                    </PanelGroup>
                </Tab>
            </TabContainer>
        </Group>

    </Row>
    <Model externalClass="pl.fhframework.dp.commons.fh.messages.MessageHandlingFormModel"/>

</Form>