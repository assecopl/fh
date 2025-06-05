<?xml version="1.0" encoding="UTF-8"?>
<Form xmlns="http://fh.asseco.com/form/1.0" id="UseCaseUrlsForm" label="{$.fh.docs.url_urls_to_use_cases}" container="mainForm" formType="STANDARD">
    <PanelGroup width="md-12" label="{$.fh.docs.url_concept}" id="_Form_PanelGroup1">
        <OutputLabel width="md-12" id="_Form_PanelGroup1_OutputLabel1">
            {$.fh.docs.url_many_use_cases_are_being_started_while}
        </OutputLabel>
        <Spacer width="md-12" height="10px" id="_Form_PanelGroup1_Spacer"/>
        <OutputLabel width="md-12" id="_Form_PanelGroup1_OutputLabel2">
            {$.fh.docs.url_use_cases_creator_can_mark_it_as_use_case}
        </OutputLabel>
    </PanelGroup>
    <PanelGroup width="md-12" label="{$.fh.docs.url_url_format}" id="_Form_PanelGroup2">
        <OutputLabel width="md-12" id="_Form_PanelGroup2_OutputLabel1">{$.fh.docs.url_url_to_an_use_case_contains}:</OutputLabel>
        <PanelGroup id="_Form_PanelGroup2_PanelGroup">
            <OutputLabel width="md-12" id="_Form_PanelGroup2_PanelGroup_OutputLabel1">- {$.fh.docs.url_alias_identifies_the_use_case}</OutputLabel>
            <OutputLabel width="md-12" id="_Form_PanelGroup2_PanelGroup_OutputLabel2">- {$.fh.docs.url_positional_parameters_visible_as_path_elements}</OutputLabel>
            <OutputLabel width="md-12" id="_Form_PanelGroup2_PanelGroup_OutputLabel3">- {$.fh.docs.url_named_parameters_visible_as_query_parameters}</OutputLabel>
        </PanelGroup>
        <OutputLabel width="md-12" id="_Form_PanelGroup2_OutputLabel2">{$.fh.docs.url_example_urls}:</OutputLabel>
        <InputText width="md-12" availability="VIEW" rowsCount="6" id="_Form_PanelGroup2_InputText"><![CDATA[http://HOST/#alias
http://HOST/#alias/positional0
http://HOST/#alias/positional0/positional1
http://HOST/#alias?namedX=valueX
http://HOST/#alias?namedX=valueX&namedY=valueY
http://HOST/#alias/positional0/positional1?namedX=valueX&namedY=valueY]]></InputText>
    </PanelGroup>
    <PanelGroup width="md-12" label="{$.fh.docs.url_exposing_an_url}" id="_Form_PanelGroup3">
        <OutputLabel width="md-12" id="_Form_PanelGroup3_OutputLabel1">
            {$.fh.docs.url_to_expose_an_url_start_with_annotating}
        </OutputLabel>
        <Spacer width="md-12" height="10px" id="_Form_PanelGroup3_Spacer1"/>
        <OutputLabel width="md-12" id="_Form_PanelGroup3_OutputLabel2">
            {$.fh.docs.url_it_is_recommended_to_give_use_case}
        </OutputLabel>
        <Spacer width="md-12" height="10px" id="_Form_PanelGroup3_Spacer2"/>
        <InputText width="md-12" availability="VIEW" rowsCount="7" id="_Form_PanelGroup3_InputText"><![CDATA[![ESCAPE[http://HOST/#docs-urls

@UseCase
@UseCaseWithUrl(alias = "docs-urls")
public class UseCaseUrlsUC implements IInitialUseCase {
    ...
}]]]]></InputText>
    </PanelGroup>

    <PanelGroup width="md-12" label="{$.fh.docs.url_input_parameters}" id="_Form_PanelGroup4">
        <OutputLabel width="md-12" id="_Form_PanelGroup4_OutputLabel">
            {$.fh.docs.url_input_parameter_of_an_url_enabled}:
        </OutputLabel>
        <PanelGroup id="_Form_PanelGroup4_PanelGroup1">
            <Group width="md-12" id="_Form_PanelGroup4_PanelGroup1_Group1"><OutputLabel width="md-12" id="_Form_PanelGroup4_PanelGroup1_Group1_OutputLabel">- {$.fh.docs.url_strings_number}</OutputLabel></Group>
            <Group width="md-12" id="_Form_PanelGroup4_PanelGroup1_Group2"><OutputLabel width="md-12" id="_Form_PanelGroup4_PanelGroup1_Group2_OutputLabel">- {$.fh.docs.url_enums}</OutputLabel></Group>
            <Group width="md-12" id="_Form_PanelGroup4_PanelGroup1_Group3">
                <OutputLabel width="md-2" id="_Form_PanelGroup4_PanelGroup1_Group3_OutputLabel1">- {$.fh.docs.url_convertible_types_see} </OutputLabel>
                <Link newWindow="false" height="20px" width="md-2" labelPosition="up" value="{$.fh.docs.url_type_convertion_concept}" url="/#docs-type-convertion" id="_Form_PanelGroup4_PanelGroup1_Group3_Link"/>
                <OutputLabel width="md-8" id="_Form_PanelGroup4_PanelGroup1_Group3_OutputLabel2">)</OutputLabel>
            </Group>
            <Group width="md-12" id="_Form_PanelGroup4_PanelGroup1_Group4"><OutputLabel width="md-12" id="_Form_PanelGroup4_PanelGroup1_Group4_OutputLabel">- {$.fh.docs.url_database_entities_id_of_an_entity_is_exposed}</OutputLabel></Group>
            <Group width="md-12" id="_Form_PanelGroup4_PanelGroup1_Group5"><OutputLabel width="md-12" id="_Form_PanelGroup4_PanelGroup1_Group5_OutputLabel">- {$.fh.docs.url_complex_types_if_urlparamwrapper}</OutputLabel></Group>
        </PanelGroup>
        <PanelGroup width="md-12" label="{$.fh.docs.url_no_annotations}" id="_Form_PanelGroup4_PanelGroup2">
            <OutputLabel width="md-12" id="_Form_PanelGroup4_PanelGroup2_OutputLabel">
                {$.fh.docs.url_by_default_all_parameters_are_exposed}
            </OutputLabel>
            <Spacer width="md-12" height="10px" id="_Form_PanelGroup4_PanelGroup2_Spacer"/>
            <InputText width="md-12" availability="VIEW" rowsCount="11" id="_Form_PanelGroup4_PanelGroup2_InputText"><![CDATA[![ESCAPE[http://HOST/#alias/John/Black

@UseCase
@UseCaseWithUrl(alias = "alias")
public class TwoStringInputUseCaseNoAnnotation implements IUseCaseTwoInput<String, String, IUseCaseNoCallback> {

    @Override
    public void start(String firstName, String lastName) {
        ...
    }
}]]]]></InputText>
        </PanelGroup>
        <PanelGroup width="md-12" label="{$.fh.docs.url_urlparam_annotation}" id="_Form_PanelGroup4_PanelGroup3">
            <OutputLabel width="md-12" id="_Form_PanelGroup4_PanelGroup3_OutputLabel">
                {$.fh.docs.url_urlparam_annotation_can_be_used_to_declare}
            </OutputLabel>
            <Spacer width="md-12" height="10px" id="_Form_PanelGroup4_PanelGroup3_Spacer1"/>
            <InputText width="md-12" availability="VIEW" rowsCount="11" id="_Form_PanelGroup4_PanelGroup3_InputText1"><![CDATA[![ESCAPE[http://HOST/#alias/Black/John

@UseCase
@UseCaseWithUrl(alias = "alias")
public class TwoStringInputUseCaseRevertedPositional extends ParamStorageUseCase implements IUseCaseTwoInput<String, String, IUseCaseNoCallback> {

    @Override
    public void start(@UrlParam(position = 1) String firstName, @UrlParam(position = 0) String lastName) {
        ...
    }
}]]]]></InputText>
            <Spacer width="md-12" height="10px" id="_Form_PanelGroup4_PanelGroup3_Spacer2"/>
            <InputText width="md-12" availability="VIEW" rowsCount="11" id="_Form_PanelGroup4_PanelGroup3_InputText2"><![CDATA[![ESCAPE[http://HOST/#alias?myName=John&myAge=24

@UseCase
@UseCaseWithUrl(alias = "alias")
public class StringInputUseCaseQuery extends ParamStorageUseCase implements IUseCaseOneInput<String, IUseCaseNoCallback> {

    @Override
    public void start(@UrlParam(name = "myName") String name, @UrlParam(name = "myAge") Integer age) {
        ...
    }
}]]]]></InputText>
            <Spacer width="md-12" height="10px" id="_Form_PanelGroup4_PanelGroup3_Spacer3"/>
            <InputText width="md-12" availability="VIEW" rowsCount="12" id="_Form_PanelGroup4_PanelGroup3_InputText3"><![CDATA[![ESCAPE[http://HOST/#alias/John?myAge=24
http://HOST/#alias?myAge=24

@UseCase
@UseCaseWithUrl(alias = "alias")
public class StringInputUseCaseQuery extends ParamStorageUseCase implements IUseCaseOneInput<String, IUseCaseNoCallback> {

    @Override
    public void start(@UrlParam(optional = true) String name, @UrlParam(name = "myAge") Integer age) {
        ...
    }
}]]]]></InputText>
        </PanelGroup>
        <PanelGroup width="md-12" label="{$.fh.docs.url_urlparamignored_annotation}" id="_Form_PanelGroup4_PanelGroup4">
            <OutputLabel width="md-12" id="_Form_PanelGroup4_PanelGroup4_OutputLabel">
                {$.fh.docs.url_urlparamignored_annotation_can_be_used}
            </OutputLabel>
            <Spacer width="md-12" height="10px" id="_Form_PanelGroup4_PanelGroup4_Spacer"/>
            <InputText width="md-12" availability="VIEW" rowsCount="11" id="_Form_PanelGroup4_PanelGroup4_InputText"><![CDATA[![ESCAPE[http://HOST/#alias/Black

@UseCase
@UseCaseWithUrl(alias = "alias")
public class TwoStringInputUseCaseNoAnnotation implements IUseCaseTwoInput<String, String, IUseCaseNoCallback> {

    @Override
    public void start(@UrlParamIgnored String firstName, String lastName) {
        ...
    }
}]]]]></InputText>
        </PanelGroup>
        <PanelGroup width="md-12" label="{$.fh.docs.url_urlparamwrapper_annotation}" id="_Form_PanelGroup4_PanelGroup5">
            <OutputLabel width="md-12" id="_Form_PanelGroup4_PanelGroup5_OutputLabel">
                {$.fh.docs.url_urlparamwrapper_annotation_can_be_used}
            </OutputLabel>
            <Spacer width="md-12" height="10px" id="_Form_PanelGroup4_PanelGroup5_Spacer1"/>
            <InputText width="md-12" availability="VIEW" rowsCount="18" id="_Form_PanelGroup4_PanelGroup5_InputText1"><![CDATA[![ESCAPE[http://HOST/#alias?sister_firstName=Mary&sister_lastName=Black&brother_firstName=James&brother_lastName=Doe

public class Person {

    private String firstName;

    private String lastName;
}

@UseCase
@UseCaseWithUrl(alias = "alias")
public class TwoWrapperInputUseCaseNamedWithPrefix extends ParamStorageUseCase implements IUseCaseTwoInput<Person, Person, IUseCaseNoCallback> {

    @Override
    public void start(@UrlParamWrapper(namePrefix = "sister_") Person sister, @UrlParamWrapper(namePrefix = "brother_") Person brother) {
        ...
    }
}]]]]></InputText>
            <Spacer width="md-12" height="10px" id="_Form_PanelGroup4_PanelGroup5_Spacer2"/>
            <InputText width="md-12" availability="VIEW" rowsCount="18" id="_Form_PanelGroup4_PanelGroup5_InputText2"><![CDATA[![ESCAPE[http://HOST/#alias/John/Black?brother_firstName=James&brother_lastName=Doe

public class Person {

    private String firstName;

    private String lastName;
}

@UseCase
@UseCaseWithUrl(alias = "alias")
public class TwoWrapperInputUseCaseMixedWithPrefix extends ParamStorageUseCase implements IUseCaseTwoInput<Person, Person, IUseCaseNoCallback> {

    @Override
    public void start(@UrlParamWrapper(useNames = false) Person me, @UrlParamWrapper(namePrefix = "brother_") Person brother) {
        ...
    }
}]]]]></InputText>
            <Spacer width="md-12" height="10px" id="_Form_PanelGroup4_PanelGroup5_Spacer3"/>

            <InputText width="md-12" availability="VIEW" rowsCount="20" id="_Form_PanelGroup4_PanelGroup5_InputText3"><![CDATA[![ESCAPE[http://HOST/#alias/John?name=Black

public class Person {

    @UrlParam(position = 0)
    private String firstName;

    @UrlParam(name = "name")
    private String lastName;
}

@UseCase
@UseCaseWithUrl(alias = "alias")
public class WrapperInputUseCaseNamedWithPrefix extends ParamStorageUseCase implements IUseCaseOneInput<Person, IUseCaseNoCallback> {

    @Override
    public void start(@UrlParamWrapper Person me) Person brother) {
        ...
    }
}]]]]></InputText>
        </PanelGroup>
        <PanelGroup width="md-12" label="{$.fh.docs.url_adapters}" id="_Form_PanelGroup4_PanelGroup6">
            <OutputLabel width="md-12" id="_Form_PanelGroup4_PanelGroup6_OutputLabel">
                {$.fh.docs.url_in_more_complex_scenarios_eg_external_service}
            </OutputLabel>
            <Spacer width="md-12" height="10px" id="_Form_PanelGroup4_PanelGroup6_Spacer"/>
            <InputText width="md-12" availability="VIEW" rowsCount="38" id="_Form_PanelGroup4_PanelGroup6_InputText1"><![CDATA[![ESCAPE[public class FormEditorUrlAdapter implements IUseCaseTwoInputUrlAdapter<EditorMode, FormInfo> {

    private static final String URL_PARAM_MODE = "mode";

    @Autowired
    private FormService formService;

    @Override
    public Optional<Parameters<EditorMode, FormInfo>> extractParameters(UseCaseUrl url) {
        String className = url.getPositionalParameter(0);
        String modeStr = url.getNamedParameter(URL_PARAM_MODE);
        if (modeStr == null || className == null) {
            return Optional.empty();
        }

        EditorMode mode = EditorMode.valueOf(modeStr);

        DynamicClassFilter filter = new DynamicClassFilter();
        filter.setCustomPredicate(c -> c.getClassName().equals(DynamicClassName.forClassName(className)));
        List<FormInfo> foundForms = formService.getFormsInfo(filter);

        if (foundForms.isEmpty()) {
            FhLogger.warn("Url passed form name '{}' not found", className);
            return Optional.empty();
        } else {
            return Optional.of(new Parameters<>(mode, foundForms.get(0)));
        }
    }

    @Override
    public boolean exposeParamsInURL(UseCaseUrl url, EditorMode mode, FormInfo formInfo) {
        url.putNamedParameter(URL_PARAM_MODE, mode.name());
        url.putPositionalParameter(0, formInfo.getFullClassName());
        return true;
    }
}
}]]]]></InputText>
        <InputText width="md-12" availability="VIEW" rowsCount="9" id="_Form_PanelGroup4_PanelGroup6_InputText2"><![CDATA[![ESCAPE[@UseCase
@UseCaseWithUrl(alias = "design-form-editor", adapterClass = FormEditorUrlAdapter.class)
public class FormEditorUC implements IUseCaseTwoInput<EditorMode, FormInfo, IUseCaseCloseCallback> {

    @Override
    public void start(EditorMode mode, FormInfo formInfo) {
    ...
    }
}]]]]></InputText>
        </PanelGroup>
        <PanelGroup width="md-12" label="{$.fh.docs.url_adapters_hacking_not_recommended}" id="_Form_PanelGroup4_PanelGroup7">
            <OutputLabel width="md-12" id="_Form_PanelGroup4_PanelGroup7_OutputLabel">
                {$.fh.docs.url_although_it_is_not_considered_to_be_a_good_practice}
            </OutputLabel>
        </PanelGroup>
    </PanelGroup>
</Form>