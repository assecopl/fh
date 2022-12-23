<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<Form xmlns="http://fh.asseco.com/form/1.0" id="OpDataBaseForm" hideHeader="true"
      container="mainForm" formType="MODAL"
      modalSize="XLARGE">
    <!-- <Form xmlns="http://fh.asseco.com/form/1.0" id="SubstantiveParametersDetailEditForm" hideHeader="true" -->

    <AvailabilityConfiguration>
    </AvailabilityConfiguration>

<!--    <Row width="md-12">-->
<!--        <Group styleClasses="group-header">-->
<!--            <Group styleClasses="header-group">-->
<!--                <OutputLabel width="md-12" value="{title}" styleClasses="panel-title,label_padding"/>-->
<!--                <OutputLabel width="md-12" value="{dto.parameterNames[0].name}"/>-->
<!--            </Group>-->
<!--        </Group>-->
<!--    </Row>-->

    <Group id="basicDataAndCommonCommandsParametersEditForm" styleClasses="group-header box-shadow__unset">
        <Row width="md-12" elementsHorizontalAlign="AROUND" styleClasses="row-panel panel-header">
            <Group
                    width="xs-1"
                    styleClasses="mb-0 w-100"
                    wrapperStyle="
                                padding: var(--padding-bigger);
                                justify-self: start;
                                user-select: text;
                                margin-right: 30px;
                                flex-grow: 1;
                                flex-basis: 0;
                                padding-right: unset;">
<!--                <OutputLabel availability="VIEW" width="md-12"-->
<!--                             styleClasses="panel-title output-label-no-padding output-max-width white-space&#45;&#45;break-spaces"-->
<!--                             value="{title} "-->
<!--                             hint = "Type of operation"/>-->
<!--                <OutputLabel availability="VIEW" width="md-12"-->
<!--                             styleClasses="panel-status output-label-no-padding output-max-width white-space&#45;&#45;break-spaces"-->
<!--                             value="{dto.parameterNames[0].name}"-->
<!--                             hint="Performer"/>-->

                <OutputLabel availability="VIEW" width="md-12"
                             styleClasses="panel-title output-label-no-padding white-space--break-spaces"
                             value="{$.fhdp.parameters.property.titleOfEditForm}"
                             hint = "Type of operation"/>
                <OutputLabel availability="VIEW" width="md-12"
                             styleClasses="panel-status output-label-no-padding white-space--break-spaces"
                             value="{dto.parameterNames[0].name} "
                             hint="Performer"/>
            </Group>

            <ButtonGroup id="buttonGroupEditParameters" horizontalAlign="right"
                         wrapperStyle="padding: var(--padding-bigger);"
                         styleClasses="button-icon-group justify-content-end panel-options-base" width="xs-1">
                <Button id="cancelButtonEditParameters" style="default" styleClasses="panel-option margin__unset" horizontalAlign="right"
                        label="[b][icon='fas fa-times'][/b]"
                        hint="{$.fhdp.common.close}" hintTrigger="HOVER"
                        hintPlacement="BOTTOM"
                        onClick="cancel"/>
            </ButtonGroup>
        </Row>
    </Group>
    <Row width="md-12" wrapperStyle="flex: unset; overflow: auto;" styleClasses="h-100">
        <PanelGroup>
            <Row width="md-12">
                <Button id="btnAddRow" width="md-1" label="[icon='fas fa-plus']" style="default" onClick="addRepeater"
                        availability="{checkControlVisibility(dto.valueTypes, 'Repeater')}"/>
            </Row>
            <Row width="md-12">
                <Group width="md-12">
                    <OutputLabel width="md-12" value="{$.fhdp.parameters.property.name}"/>
                    <InputText width="md-12" value="{detailNameAndDescription.nameForSelectedLanguage}"/>
                </Group>
                <Group width="md-12">
                    <OutputLabel width="md-12" value="{$.fhdp.parameters.property.description}"/>
                    <InputText width="md-12" value="{detailNameAndDescription.description}"/>
                </Group>
                <Group width="md-12">
                    <OutputLabel width="md-12" value="{$.fhdp.parameters.property.value}"/>
                    <Group width="md-12">
                        <CheckBox label=" " width="md-1" value="{dto.value}"
                                  availability="{checkControlVisibility(dto.valueTypes, 'CheckBox')}"/>
                        <!--<InputNumber width="md-12" value="{dto.value}" requiredRegexBinding="{dto.pattern}" requiredRegexMessage="{$.fhdp.parameters.validation.message}"
                                     availability="{checkControlVisibility(dto.valueTypes, 'InputNumber')}"/>-->
                        <!-- maskDefinition="x[0-9]" mask="x{*}" -->
                        <InputText width="md-12" value="{dto.value}"  requiredRegexBinding="{dto.pattern}" requiredRegexMessage="{$.fhdp.parameters.validation.message}"
                                   availability="{checkControlVisibility(dto.valueTypes, 'InputText')}"/>
                        <InputDate width="md-12" value="{dto.value}" requiredRegexBinding="{dto.pattern}" requiredRegexMessage="{$.fhdp.parameters.validation.message}"
                                   availability="{checkControlVisibility(dto.valueTypes, 'InputDate')}"/>
                        <Repeater collection="{dto.values}" iterator="row">
                            <InputText width="md-10" value="{row.value}" requiredRegexBinding="{dto.pattern}" requiredRegexMessage="{$.fhdp.parameters.validation.message}"/>
                            <Button width="md-2" label="[icon='far fa-trash-alt']"
                                    styleClasses="pl-0 ml-0" style="primary" onClick="removeRepeater({row$rowNo})"/>
                        </Repeater>
                    </Group>
                </Group>
                <Group width="md-12">
                    <OutputLabel width="md-12" value="{$.fhdp.parameters.property.tags}"/>
                    <Combo id="tag"
                           width="md-4" emptyValue="true"
                           multiselect="true"
                           value="{dto.tags}"
                           values="{substantiveParametersTagList}"
                           onChange="-"
                           availability="EDIT"/>
                    <Button id="btnAddTag" width="md-1" label="[icon='fas fa-plus']" style="default" onClick="addTag"/>
                    <InputText id="addTag" width="md-4" value="{name}" rowsCountAuto="true" styleClasses="font-weight-bold"
                               availability="{checkControlVisibility()}"/>
                    <Button id="btnSaveTag"
                            width="xs-1"
                            onClick="saveTag" label="{$.fhdp.parameters.property.save}"
                            hintPlacement="BOTTOM"
                            inlineStyle="line-height: unset;"
                            wrapperStyle="margin-bottom: unset; margin-right: 5px;"
                            availability="{checkControlVisibility()}"/>
                    <Button id="btnCancelAddTag"
                            width="xs-1"
                            onClick="cancelAddTag" label="{$.fhdp.parameters.property.cancel}"
                            hintPlacement="BOTTOM" style="default"
                            inlineStyle="line-height: unset;"
                            wrapperStyle="margin-bottom: unset; margin-right: 5px;"
                            availability="{checkControlVisibility()}"/>
                    <!-- <InputText width="md-12" value="{dto.tags}" rowsCountAuto="true" styleClasses="font-weight-bold"/>-->
                </Group>
            </Row>
        </PanelGroup>
    </Row>

    <Row elementsHorizontalAlign="LEFT" styleClasses="panel-footer mb-n3">
        <Button id="btnSave"
                width="xs-1"
                onClick="save" label="{$.fhdp.parameters.property.save}"
                hintPlacement="BOTTOM"
                inlineStyle="line-height: unset;"
                wrapperStyle="margin-bottom: unset; margin-right: 5px;"/>
        <Button id="btnCancel"
                width="xs-1" availability="EDIT"
                onClick="cancel" label="{$.fhdp.parameters.property.cancel}"
                hintPlacement="BOTTOM" style="default"
                inlineStyle="line-height: unset;"
                wrapperStyle="margin-bottom: unset; margin-right: 5px;"/>
    </Row>
</Form>
