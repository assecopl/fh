<?xml version="1.0" encoding="UTF-8"?>
<Form xmlns="http://fh.asseco.com/form/1.0" container="mainForm" label="i18n" id="InternationalizationForm">

    <AvailabilityConfiguration>
        <ReadOnly>basicCodeExample, staticCodeExample, tableCodeExample, modelBindingCodeExample,
            validationErrorCodeExample,enumCodeExample1,enumCodeExample2
        </ReadOnly>
    </AvailabilityConfiguration>

    <LocaleBundle basename="docsMessageSource" var="msg"/>

    <PanelGroup id="_Form_PanelGroup1">
        <OutputLabel width="md-12" value="{$.fh.docs.i18n_project_can_use_spring_internationalization}" id="_Form_PanelGroup1_OutputLabel1"/>
        <OutputLabel width="md-12" value="1. {$.fh.docs.i18n_if_developer_wants_to_use_global_message_binding} - '$.fh.binding.message'. {$.fh.docs.i18n_fh_will_search_every_messagesource_to_find}" id="_Form_PanelGroup1_OutputLabel2"/>
        <OutputLabel width="md-12" value="2. {$.fh.docs.i18n_if_developer_wants_to_use_specific_message_binding} - '$[bundlePrefix].fh.binding.message'. {$.fh.docs.i18n_fh_will_search_for_messagesource_under}     [bundlePrefix] {$.fh.docs.i18n_named_to_find_first_occurrence_of_binded_key}" id="_Form_PanelGroup1_OutputLabel3"/>

        <InputText width="md-12" id="basicCodeExample" rowsCount="2" label="{$.fh.docs.i18n_code_example_looks_like_this}">
            <![CDATA[![ESCAPE[
<LocaleBundle basename="docsMessageSource" var="msg"/>
<OutputLabel width="md-12" value="{$msg.fh.docs.welcome.info}"/>]]]]>
        </InputText>

        <OutputLabel width="md-12" value="{$.fh.docs.i18n_change_language}: " id="_Form_PanelGroup1_OutputLabel4"/>
        <Button label="Polski" onClick="setPL" width="lg-2,md-3" id="_Form_PanelGroup1_Button1"/>
        <Button label="English" onClick="setEng" width="lg-2,md-3" id="_Form_PanelGroup1_Button2"/>
    </PanelGroup>

    <!--<Spacer height="20"/>-->

    <PanelGroup label="{$.fh.docs.table.example.binding}" id="_Form_PanelGroup2">
        <Group width="md-6" id="_Form_PanelGroup2_Group1">
            <OutputLabel width="md-12" value="Static value - {$.fh.docs.welcome.info}" id="_Form_PanelGroup2_Group1_OutputLabel1"/>
            <OutputLabel width="md-12" value="{$msg.fh.docs.welcome.info}" id="_Form_PanelGroup2_Group1_OutputLabel2"/>
        </Group>
        <Group width="md-6" id="_Form_PanelGroup2_Group2">
            <InputText width="md-12" id="staticCodeExample" label="{$.fh.docs.i18n_code_example_looks_like_this}" rowsCount="3">
                <![CDATA[![ESCAPE[<LocaleBundle basename="docsMessageSource" var="msg"/>
<OutputLabel width="md-12" value="Static value - {$.fh.docs.welcome.info}"/>
<OutputLabel width="md-12" value="{$msg.fh.docs.welcome.info}"/>]]]]>
            </InputText>
        </Group>
    </PanelGroup>

    <PanelGroup label="{$.fh.docs.table.example}" id="_Form_PanelGroup3">
        <Group width="md-6" id="_Form_PanelGroup3_Group1">
            <Table id="i18nTable" collection="{tableMsg}" iterator="iter">
                <Column id="noCol" label="{$.fh.docs.table.example.no}" value="{iter$rowNo}"/>
                <Column id="textCol" label="{$.fh.docs.table.example.dynamic_text_i18n_message}">
                    <OutputLabel width="md-12" value="{$.fh.docs.table.cell} {iter}" id="_Form_PanelGroup3_Group1_Table_Column2_OutputLabel"/>
                </Column>
            </Table>
        </Group>
        <Group width="md-6" id="_Form_PanelGroup3_Group2">
            <InputText width="md-12" id="tableCodeExample" label="{$.fh.docs.i18n_code_example_looks_like_this}" rowsCount="6">
                <![CDATA[![ESCAPE[<Table id="i18nTable" collection="{tableMsg}" iterator="iter">
       <Column id="noCol" label="No." value="{iter$rowNo}"/>
       <Column id="textCol" label="Dynamic text + i18n message">
            <OutputLabel width="md-12" value="{$.fh.docs.table.cell} {iter}"/>
       </Column>
</Table>]]]]>
            </InputText>
        </Group>
    </PanelGroup>

    <PanelGroup label="{$.fh.docs.model.example.binding}" id="_Form_PanelGroup4">
        <Group width="md-12" id="_Form_PanelGroup4_Group1">
            <OutputLabel width="md-12" id="_Form_PanelGroup4_Group1_OutputLabel">
                {$.fh.docs.i18n_there_is_java_way_for_developer_to_use_model_binding}
            </OutputLabel>
        </Group>

        <Spacer width="md-12" height="15" id="_Form_PanelGroup4_Spacer"/>

        <Group width="md-6" id="_Form_PanelGroup4_Group2">
            <OutputLabel width="md-12" value="Static value - {messageFromModel}" id="_Form_PanelGroup4_Group2_OutputLabel1"/>
            <OutputLabel width="md-12" value="{$.getMessage('fh.docs.welcome.info')}" id="_Form_PanelGroup4_Group2_OutputLabel2"/>
        </Group>
        <Group width="md-6" id="_Form_PanelGroup4_Group3">
            <InputText width="md-12" id="modelBindingCodeExample" label="{$.fh.docs.i18n_code_example_looks_like_this}" rowsCount="2">
                <![CDATA[![ESCAPE[<OutputLabel width="md-12" value="Static value - {messageFromModel}"/>
<OutputLabel width="md-12" value="{$.getMessage('fh.docs.welcome.info')}"/>]]]]>
            </InputText>
        </Group>
    </PanelGroup>

    <PanelGroup label="{$.fh.docs.i18n.example.enums}" id="_Form_PanelGroup5">
        <Group width="md-12" id="_Form_PanelGroup5_Group">
            <OutputLabel width="md-12" value="{$.fh.docs.i18n.example.enums.desc}" id="_Form_PanelGroup5_Group_OutputLabel"/>
        </Group>
        <SelectOneMenu label="{$.fh.docs.i18n.example.enums.selectOneMenu}" values="{exampleI18nEnums}" width="md-6" id="_Form_PanelGroup5_SelectOneMenu"/>
        <InputText width="md-12" id="enumCodeExample1" rowsCount="3">
            <![CDATA[![ESCAPE[public enum ExampleI18nEnum {
    ONE, TWO, THREE;
}]]]]>
        </InputText>
        <InputText width="md-12" id="enumCodeExample2" rowsCount="3">
            <![CDATA[enum.ExampleI18nEnum.ONE={$.enum.ExampleI18nEnum.ONE}
enum.ExampleI18nEnum.TWO={$.enum.ExampleI18nEnum.TWO}
enum.ExampleI18nEnum.THREE={$.enum.ExampleI18nEnum.THREE}]]>
        </InputText>
    </PanelGroup>

    <PanelGroup label="{$.fh.docs.validation.example.panel}" id="_Form_PanelGroup6">
        <Group width="md-12" id="_Form_PanelGroup6_Group1">
            <OutputLabel width="md-12" id="_Form_PanelGroup6_Group1_OutputLabel">
                {$.fh.docs.i18n_fh_also_supports_custom_validation_messages_using_message}
            </OutputLabel>
        </Group>

        <ValidateMessages componentIds="reportErrorBtn,+" level="error" id="_Form_PanelGroup6_ValidateMessages"/>
        <InputText id="argInputText" value="{argA}" width="lg-6,md-8"/>
        <Group id="_Form_PanelGroup6_Group2">
            <Button id="reportErrorBtn" label="{$.fh.docs.validation.example.btn}" onClick="reportError" width="lg-2,md-4"/>
        </Group>

        <InputText width="md-12" id="validationErrorCodeExample" label="{$.fh.docs.i18n_code_example_on_java_side}:" rowsCount="4">
            <![CDATA[![ESCAPE[reportValidationError(model, "1", messageService.getAllBundles().getMessage("fh.docs.validation.example.msg), PresentationStyleEnum.ERROR);
reportValidationTemplateError(model, "2", "fh.docs.validation.example.msg", PresentationStyleEnum.ERROR);
String[] args = {model.getArgA()};
reportValidationTemplateError(model, "3", "fh.docs.validation.example.msgWithParam", args, PresentationStyleEnum.ERROR);]]]]>
        </InputText>
    </PanelGroup>

    <PanelGroup label="{$.fh.docs.i18n_fh_messages_with_simple_dialog}" id="_Form_PanelGroup7">
        <OutputLabel width="md-12" value="{$.fh.docs.i18n_fh_dialogs_works_with_messages_based}:" id="_Form_PanelGroup7_OutputLabel"/>
        <Group width="md-6" id="_Form_PanelGroup7_Group">
            <OutputLabel width="md-12" value="fh.core.dialog.info.title" id="_Form_PanelGroup7_Group_OutputLabel1"/>
            <OutputLabel width="md-12" value="fh.core.dialog.error.title" id="_Form_PanelGroup7_Group_OutputLabel2"/>
            <OutputLabel width="md-12" value="fh.core.dialog.button.close" id="_Form_PanelGroup7_Group_OutputLabel3"/>
            <OutputLabel width="md-12" value="fh.core.toast.current_user_with_session" id="_Form_PanelGroup7_Group_OutputLabel4"/>
            <OutputLabel width="md-12" value="fh.core.toast.error_singular" id="_Form_PanelGroup7_Group_OutputLabel5"/>
            <OutputLabel width="md-12" value="fh.core.toast.error_plural" id="_Form_PanelGroup7_Group_OutputLabel6"/>
        </Group>

        <Spacer width="md-12" height="15" id="_Form_PanelGroup7_Spacer"/>
        <Button label="{$.fh.docs.i18n_display_dialog}" onClick="displayDialog" width="lg-2,md-3" id="_Form_PanelGroup7_Button"/>
    </PanelGroup>

</Form>