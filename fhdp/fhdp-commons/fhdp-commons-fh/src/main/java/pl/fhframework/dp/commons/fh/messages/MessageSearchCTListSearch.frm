<Form xmlns="http://fh.asseco.com/form/1.0" id="messageSearch" formType="STANDARD" container="searchForm" modalSize="REGULAR">
    <PanelGroup>
        <InputText width="md-12" value="{query.localReferenceNumber}" label="{$.message.search.lrn}" onKeyEvent="search" keyEvent="ENTER"/>
        <InputText width="md-12" value="{query.customsReferenceNumber}" label="{$.message.search.mrn}" onKeyEvent="search" keyEvent="ENTER"/>
        <InputText width="md-12" value="{query.name}" label="{$.message.search.name}" onKeyEvent="search" keyEvent="ENTER"/>
        <InputText width="md-12" value="{query.repositoryId}" label="{$.message.repositoryId}" onKeyEvent="search" keyEvent="ENTER"/>
        <InputText width="md-12" value="{query.messageIdentification}" label="{$.message.search.messageIdentifier}" onKeyEvent="search" keyEvent="ENTER"/>
        <InputText width="md-12" value="{query.textSearch}" label="{$.message.search.text.fragment}" onKeyEvent="search" keyEvent="ENTER"/>
        <CheckBox width="md-12" value="{query.wholeWordsOnly}" label="{$.message.search.full.text}"/>
    </PanelGroup>

    <Button label="{$.message.search.more}" width="md-12" onClick="openSearchByDetails" />

    <Model externalClass="pl.fhframework.dp.commons.fh.messages.MessageSearchCTListSearch"/>
</Form>