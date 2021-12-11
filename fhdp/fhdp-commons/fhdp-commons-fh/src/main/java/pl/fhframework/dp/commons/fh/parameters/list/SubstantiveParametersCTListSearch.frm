<Form xmlns="http://fh.asseco.com/form/1.0" id="parametersSearch" container="searchForm" formType="STANDARD"
      modalSize="REGULAR">

    <AvailabilityConfiguration>
        <Invisible when="isSearchBoxButtons==false">searchBoxButtons</Invisible>
    </AvailabilityConfiguration>

    <Group id="searchBoxButtons" styleClasses="search-box-buttons mb-0 p-0" inlineStyle="margin-top: calc(-1 * var(--padding-default))">
        <Button id="allSearchBoxButton" width="md-4" label="[icon='fas fa-check-circle'] Wszystkie"
                styleClasses="search-box-button search-box-button--selected" style="default" wrapperStyle="margin-bottom: unset; padding: unset;"
                onClick="onClickAllSearchBoxButton"/>
        <Button id="globalSearchBoxButton" width="md-4" label="[icon='fas fa-globe'] Globalne"
                styleClasses="search-box-button" style="default" wrapperStyle="margin-bottom: unset; padding: unset;"
                onClick="onClickGlobalSearchBoxButton"/>
        <!--
        <Button id="officeSearchBoxButton" width="md-4" label="[icon='fas fa-school'] Dla urzędów"
                styleClasses="search-box-button" style="default" wrapperStyle="margin-bottom: unset; padding: unset;"
                onClick="onClickOfficeSearchBoxButton"/>
        -->
    </Group>
    <PanelGroup>
        <InputText width="md-12" value="{query.name}" label="{$.parameters.search.query.name.label}" onKeyEvent="search" keyEvent="ENTER"/>
        <InputText width="md-12" value="{query.key}" label="{$.parameters.search.query.key.label}" onKeyEvent="search" keyEvent="ENTER"/>
        <InputText width="md-12" value="{query.tag}" label="{$.parameters.search.query.tag.label}" onKeyEvent="search" keyEvent="ENTER"/>
        <!--<InputText width="md-12" value="{query.customsOffice}" label="{$.parameters.search.query.customsOffice.label}" onKeyEvent="search" keyEvent="ENTER"/>-->
    </PanelGroup>

    <Model externalClass="pl.fhframework.dp.commons.fh.parameters.list.SubstantiveParametersCTListSearch"/>
</Form>