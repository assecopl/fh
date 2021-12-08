<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<Form xmlns="http://fh.asseco.com/form/1.0" id="exampleAdvancedSideBar" container="advancedSearchForm" formType="STANDARD">
    <PanelHeaderFhDP title="{$.message.search.more}" width="md-12" onClick="closeSearchByDetails"/>
    <Spacer width="md-12" />

    <PanelGroup>
        <InputTimestampFhDP width="md-4" value="{query.dateFrom}" label="{$.message.search.dateFrom}"/>
        <InputTimestampFhDP width="md-4" value="{query.dateTo}" label="{$.message.search.dateTo}"/>
    </PanelGroup>
    <PanelGroup>
        <Group>
            <InputTextFhDP width="md-4" value="{query.sender.domain}" label="{$.message.search.sender.domain}"/>
            <InputTextFhDP width="md-4" value="{query.sender.identifier}" label="{$.message.search.sender.identifier}" />
        </Group>
        <Group>
            <InputTextFhDP width="md-4" value="{query.recipient.domain}" label="{$.message.search.recipient.domain}"/>
            <InputTextFhDP width="md-4" value="{query.recipient.identifier}" label="{$.message.search.recipient.identifier}"/>
        </Group>
    </PanelGroup>
    <PanelGroup>
        <Group>
            <InputTextFhDP width="md-4" value="{query.contentType}" label="{$.message.metadata.contentType}"/>
        </Group>
    </PanelGroup>


    <Model externalClass="pl.fhframework.dp.commons.fh.messages.MessageSearchListModel"/>
</Form>
