<Form container="appSiderDetailsContent" id="DetailsFormParameters" xmlns="http://fh.asseco.com/form/1.0">
    <AvailabilityConfiguration>
    </AvailabilityConfiguration>

    <Row width="md-12" styleClasses="main-page--flex">
        <Group id="basicDataAndCommonCommandsParameters" styleClasses="group-header box-shadow__unset" wrapperStyle="flex: unset;">
            <Row width="md-12" elementsHorizontalAlign="AROUND" styleClasses="row-panel panel-header">
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
                                    padding-right: unset;">
                    <Group styleClasses="panel-info--container">
                        <OutputLabel availability="VIEW" width="md-12"
                                     wrapperStyle="width: min-content; flex: initial; padding-right: unset; padding-left: unset;"
                                     styleClasses="output-label-no-padding output-max-width panel-info--weight white-space--break-spaces"
                                     value="{selectedSubstantiveParametersDto.key}"/>
                    </Group>
                    <OutputLabel availability="VIEW" width="md-12"
                                 styleClasses="panel-title output-label-no-padding output-max-width white-space--break-spaces"
                                 value="{$.fhdp.parameters.property.titleOfDetailsForm}"
                                 hint = "Title"/>
                    <OutputLabel availability="VIEW" width="md-12"
                                 styleClasses="panel-status output-label-no-padding output-max-width white-space--break-spaces"
                                 value=" "/>
                </Group>

                <ButtonGroup id="buttonGroupParameters" horizontalAlign="right"
                             wrapperStyle="padding: var(--padding-bigger);"
                             styleClasses="button-icon-group justify-content-end panel-options-base" width="xs-1">
                    <Button id="cancelButtonParameters" style="default" styleClasses="panel-option margin__unset" horizontalAlign="right"
                            label="[b][icon='fas fa-times'][/b]"
                            hint="{$.fhdp.common.close}" hintTrigger="HOVER"
                            hintPlacement="BOTTOM"
                            onClick="cancel"/>
                </ButtonGroup>
            </Row>
        </Group>
        <Group width="md-12" wrapperStyle="flex: unset; overflow: auto; height: 100%;" styleClasses="d-block">
            <PanelGroup>
                <Row width="md-12">
                    <Group width="md-6">
                        <OutputLabel width="md-12" value="{$.fhdp.parameters.property.name}"/>
                        <OutputLabel width="md-12" value="{selectedSubstantiveParametersDto.parameterNames[0].name}"
                                     styleClasses="font-weight-bold"/>
                    </Group>
                    <Group width="md-6">
                        <OutputLabel width="md-12" value="{$.fhdp.parameters.property.key}" availability="{checkControlVisibility()}"/>
                        <OutputLabel width="md-12" value="{selectedSubstantiveParametersDto.key}"
                                     styleClasses="font-weight-bold" availability="{checkControlVisibility()}"/>
                    </Group>
                </Row>
                <Row width="md-12">
                    <Group width="md-6">
                        <OutputLabel width="md-12" value="{$.fhdp.parameters.property.component}"/>
                        <OutputLabel width="md-12" value="{selectedSubstantiveParametersDto.components}"
                                     styleClasses="font-weight-bold"/>
                    </Group>
                    <Group width="md-6">
                        <OutputLabel width="md-12" value="{$.fhdp.parameters.property.type}"/>
                        <OutputLabel width="md-12" value="{selectedSubstantiveParametersDto.valueTypes}"
                                     styleClasses="font-weight-bold"/>
                    </Group>
                </Row>
                <!--
                <Row width="md-12">
                    <Group width="md-6">
                        <OutputLabel width="md-12" value="Dla urzędu"/>
                        <OutputLabel width="md-12" value="-" styleClasses="font-weight-bold"/>
                    </Group>
                </Row>
                -->
                <Row width="md-12">
                    <Group width="md-6">
                        <OutputLabel width="md-12" value="{$.fhdp.parameters.property.value}"/>
                        <CheckBox label=" " width="" value="{selectedSubstantiveParametersDto.value}"
                                  availability="{checkControlVisibility(selectedSubstantiveParametersDto.valueTypes, 'CheckBox')}"/>
                        <InputNumber width="md-12" value="{selectedSubstantiveParametersDto.value}"
                                     availability="{checkControlVisibility(selectedSubstantiveParametersDto.valueTypes, 'InputNumber')}"/>
                        <InputText width="md-12" value="{selectedSubstantiveParametersDto.value}"
                                   availability="{checkControlVisibility(selectedSubstantiveParametersDto.valueTypes, 'InputText')}"/>
                        <InputDate width="md-12" value="{selectedSubstantiveParametersDto.value}"
                                   availability="{checkControlVisibility(selectedSubstantiveParametersDto.valueTypes, 'InputDate')}"/>
                        <Repeater collection="{selectedSubstantiveParametersDto.values}" iterator="row">
                            <InputText width="md-10" value="{row.value}" availability="VIEW"/>
                        </Repeater>
                    </Group>
                    <Group width="md-6">
                        <OutputLabel width="md-12" value="{$.fhdp.parameters.property.unit}"
                                     availability="{checkControlVisibility(selectedSubstantiveParametersDto.unit)}"/>
                        <OutputLabel width="md-12" value="{selectedSubstantiveParametersDto.unit}"
                                     availability="{checkControlVisibility(selectedSubstantiveParametersDto.unit)}"
                                     styleClasses="font-weight-bold"/>
                    </Group>
                </Row>
            </PanelGroup>
            <PanelGroup width="md-12" label="{$.fhdp.parameters.property.description}">
                <Row width="md-12">
                    <OutputLabel inlineStyle="line-height: 1.5;" width="md-12" value="{selectedSubstantiveParametersDto.parameterDescriptions[0].description}"/>
                </Row>
            </PanelGroup>

            <PanelGroup width="md-12" label="{$.fhdp.parameters.property.tags}">
                <Row width="md-12">
                    <Combo
                           width="md-4" emptyValue="true"
                           multiselect="true"
                           value="{selectedSubstantiveParametersDto.tags}"
                           availability="VIEW"/>
                    <!-- <InputText width="md-12" value="{selectedSubstantiveParametersDto.tags}" formatter="arrayStringFormatter" rowsCountAuto="true" availability="VIEW"/> -->
                </Row>
            </PanelGroup>
        </Group>

        <Group wrapperStyle="flex: unset;" styleClasses="mb-0">
            <Row elementsHorizontalAlign="LEFT" styleClasses="panel-footer">
                <Button id="btnEdit"
                        width="xs-1" availability="EDIT"
                        onClick="edit" label="{$.fhdp.parameters.property.edit}"
                        hintPlacement="BOTTOM"
                        inlineStyle="line-height: unset;"
                        wrapperStyle="margin-bottom: unset; margin-right: 5px;"/>
            </Row>
        </Group>
    </Row>
    <Model externalClass="pl.fhframework.dp.commons.fh.parameters.list.SubstantiveParametersListModel"/>
</Form>
