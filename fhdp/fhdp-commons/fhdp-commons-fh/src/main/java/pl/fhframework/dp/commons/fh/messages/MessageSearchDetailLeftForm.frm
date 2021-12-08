<Form xmlns="http://fh.asseco.com/form/1.0" id="messageDetailLeft" formType="STANDARD" container="searchForm" modalSize="REGULAR">

    <PanelGroup id="treeListId">-->
        <Row width="md-12">
            <Group>
                <TreeFhDP collection="{selectMessagesLeftMenu}" iterator="element" relation="{children}" expanded="true"
                           leafIcon=""
                           nodeIcon="fas fa-caret-down"
                           collapsedNodeIcon="fas fa-caret-right" id="messageSearchTreeLeftMenu">
                    <TreeElementFhDP id="te1OutlineLeftMenuMessage"
                                      onLabelClick="onClickTreeElement(element)"
                                      label="{element.label}"
                                      expandedException="test"
                                      icon="{element.icon}"/>
                </TreeFhDP>
            </Group>
        </Row>
    </PanelGroup>

    <Model externalClass="pl.fhframework.dp.commons.fh.messages.MessageHandlingFormModel"/>
</Form>