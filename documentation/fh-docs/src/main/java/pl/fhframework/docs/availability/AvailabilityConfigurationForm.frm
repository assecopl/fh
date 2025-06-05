<?xml version="1.0" encoding="UTF-8"?>
<Form xmlns="http://fh.asseco.com/form/1.0" container="mainForm" id="documentationHolder" label="{$.fh.docs.availability_availability_configuration}">
    <AvailabilityConfiguration>
        <Preview>exampleText1,variantsCode1,variantsCode2,variantsCode3,variantsCode4,availabilityConfigurationTableExampleCode_1,availabilityConfigurationTableExampleCode_2,availabilityConfigurationTableExampleCode_3,availabilityConfigurationTableExampleCode_4,availabilityConfigurationTableExampleCode_5,availabilityConfigurationTableExampleCode_6</Preview>
        <Preview>viewPanel</Preview>
        <Edit>panelInput2</Edit>
        <SetByProgrammer>setByProgrammerExampleText1</SetByProgrammer>

        <Variant id="a">
            <Preview>exampleText2,radioOptionsGroupExample1,availabilityConfigurationTableExampleCode_1,availabilityConfigurationTableExampleCode_2,availabilityConfigurationTableExampleCode_3,availabilityConfigurationTableExampleCode_4</Preview>
            <SetByProgrammer>setByProgrammerExampleText2</SetByProgrammer>
        </Variant>
        <Variant id="b">
            <Preview>exampleText3,availabilityConfigurationTableExampleCode_1,availabilityConfigurationTableExampleCode_2,availabilityConfigurationTableExampleCode_3,availabilityConfigurationTableExampleCode_4</Preview>
            <SetByProgrammer>setByProgrammerExampleText3</SetByProgrammer>
        </Variant>
        <Variant id="c">
            <Preview>exampleText2,exampleText3,availabilityConfigurationTableExampleCode_1,availabilityConfigurationTableExampleCode_2,availabilityConfigurationTableExampleCode_3,availabilityConfigurationTableExampleCode_4</Preview>
            <SetByProgrammer>setByProgrammerExampleText2,setByProgrammerExampleText3</SetByProgrammer>
        </Variant>
       <Preview>AtributGrup-100</Preview>
    </AvailabilityConfiguration>

    <TabContainer id="_Form_TabContainer">
        <Tab label="{$.fh.docs.availability_availability_configuration}" id="_Form_TabContainer_Tab1">
            <PanelGroup width="md-12" label="{$.fh.docs.availability_variants}" id="_Form_TabContainer_Tab1_PanelGroup">
                <OutputLabel width="md-12" value="{$.fh.docs.availability_default_availabilityconfiguration_is_always}" id="_Form_TabContainer_Tab1_PanelGroup_OutputLabel"/>
                <Button label="{$.fh.docs.availability_default_variant}" width="md-6" onClick="variantDefault" id="_Form_TabContainer_Tab1_PanelGroup_Button1"/>
                <Button label="{$.fh.docs.availability_variant_a}" width="md-6" onClick="variantA" id="_Form_TabContainer_Tab1_PanelGroup_Button2"/>
                <Button label="{$.fh.docs.availability_variant_b}" width="md-6" onClick="variantB" id="_Form_TabContainer_Tab1_PanelGroup_Button3"/>
                <Button label="{$.fh.docs.availability_variant_c}" width="md-6" onClick="variantC" id="_Form_TabContainer_Tab1_PanelGroup_Button4"/>

                <PanelGroup width="md-12" label="{$.fh.docs.availability_readonly_inputtext_linked_with_variants}" id="_Form_TabContainer_Tab1_PanelGroup_PanelGroup1">
                    <InputText id="exampleText1" label="{$.fh.docs.availability_code}" width="md-12" rowsCount="2" value="Example text 1"/>
                    <InputText id="exampleText2" label="{$.fh.docs.availability_code}" width="md-12" rowsCount="2" value="Example text 2"/>
                    <InputText id="exampleText3" label="{$.fh.docs.availability_code}" width="md-12" rowsCount="2" value="Example text 3"/>
                    <RadioOptionsGroup width="md-12" id="radioOptionsGroupExample1" label="{$.fh.docs.availability_example_radiooption}" values="Poland|German|UK|US"/>
                </PanelGroup>

                <PanelGroup width="md-12" label="{$.fh.docs.availability_setbyprogrammer_inputtext_linked_with_variants}" id="_Form_TabContainer_Tab1_PanelGroup_PanelGroup2">
                    <Button label="{$.fh.docs.availability_default_variant}" width="md-6" onClick="variantDefault" id="_Form_TabContainer_Tab1_PanelGroup_PanelGroup2_Button1"/>
                    <Button label="{$.fh.docs.availability_variant_a}" width="md-6" onClick="variantA" id="_Form_TabContainer_Tab1_PanelGroup_PanelGroup2_Button2"/>
                    <Button label="{$.fh.docs.availability_variant_b}" width="md-6" onClick="variantB" id="_Form_TabContainer_Tab1_PanelGroup_PanelGroup2_Button3"/>
                    <Button label="{$.fh.docs.availability_variant_c}" width="md-6" onClick="variantC" id="_Form_TabContainer_Tab1_PanelGroup_PanelGroup2_Button4"/>
                    <OutputLabel width="md-12" value="{$.fh.docs.availability_the_same_above_example}" id="_Form_TabContainer_Tab1_PanelGroup_PanelGroup2_OutputLabel"/>
                    <InputText id="setByProgrammerExampleText1" label="{$.fh.docs.availability_code}" width="md-12" rowsCount="2" value="Example text 1"/>
                    <InputText id="setByProgrammerExampleText2" label="{$.fh.docs.availability_code}" width="md-12" rowsCount="2" value="Example text 2"/>
                    <InputText id="setByProgrammerExampleText3" label="{$.fh.docs.availability_code}" width="md-12" rowsCount="2" value="Example text 3"/>
                </PanelGroup>

                <PanelGroup width="md-12" label="{$.fh.docs.availability_availabilityconfiguration_code}" id="_Form_TabContainer_Tab1_PanelGroup_PanelGroup3">
                    <InputText id="variantsCode1" label="{$.fh.docs.availability_code}" width="md-12" rowsCount="17" value="&lt;AvailabilityConfiguration&gt;	&lt;ReadOnly&gt;exampleText1&lt;/ReadOnly&gt;	&lt;SetByProgrammer&gt;setByProgrammerExampleText1&lt;/SetByProgrammer&gt;	&lt;Variant id=&quot;a&quot;&gt;		&lt;ReadOnly&gt;exampleText2&lt;/ReadOnly&gt;		&lt;SetByProgrammer&gt;setByProgrammerExampleText2&lt;/SetByProgrammer&gt;	&lt;/Variant&gt;	&lt;Variant id=&quot;b&quot;&gt;		&lt;ReadOnly&gt;exampleText3&lt;/ReadOnly&gt;		&lt;SetByProgrammer&gt;setByProgrammerExampleText3&lt;/SetByProgrammer&gt;	&lt;/Variant&gt;	&lt;Variant id=&quot;c&quot;&gt;		&lt;ReadOnly&gt;exampleText2,exampleText3&lt;/ReadOnly&gt;		&lt;SetByProgrammer&gt;setByProgrammerExampleText2,setByProgrammerExampleText3&lt;/SetByProgrammer&gt;	&lt;/Variant&gt;&lt;/AvailabilityConfiguration&gt;"/>
                </PanelGroup>

                <PanelGroup width="md-12" label="{$.fh.docs.availability_form_components_code}" id="_Form_TabContainer_Tab1_PanelGroup_PanelGroup4">
                    <InputText id="variantsCode2" label="{$.fh.docs.availability_code}" width="md-12" rowsCount="21" value="&lt;Button label=&quot;Default Variant - Text 1 readonly&quot; width=&quot;md-3&quot; onClick=&quot;variantDefault&quot;/&gt;&lt;Button label=&quot;Variant A - Text 1 and Text 2 readonly&quot; width=&quot;md-3&quot; onClick=&quot;variantA&quot;/&gt;&lt;Button label=&quot;Variant B - Text 1 and Text Text 3 readonly&quot; width=&quot;md-3&quot; onClick=&quot;variantB&quot;/&gt;&lt;Button label=&quot;Variant C - Text 1, Text 2 and Text 3 readonly&quot; width=&quot;md-3&quot; onClick=&quot;variantC&quot;/&gt;&lt;PanelGroup width=&quot;md-12&quot; label=&quot;ReadOnly inputText linked with variants&quot;&gt;	&lt;InputText id=&quot;exampleText1&quot; label=&quot;Code&quot; width=&quot;md-12&quot; rowsCount=&quot;2&quot; value=&quot;Example text 1&quot;/&gt;	&lt;InputText id=&quot;exampleText2&quot; label=&quot;Code&quot; width=&quot;md-12&quot; rowsCount=&quot;2&quot; value=&quot;Example text 2&quot;/&gt;	&lt;InputText id=&quot;exampleText3&quot; label=&quot;Code&quot; width=&quot;md-12&quot; rowsCount=&quot;2&quot; value=&quot;Example text 3&quot;/&gt;&lt;/PanelGroup&gt;&lt;PanelGroup width=&quot;md-12&quot; label=&quot;SetByProgrammer inputText linked with variants&quot;&gt;	&lt;Button label=&quot;Default Variant - Text 1 readonly&quot; width=&quot;md-3&quot; onClick=&quot;variantDefault&quot;/&gt;	&lt;Button label=&quot;Variant A - Text 1 and Text 2 readonly&quot; width=&quot;md-3&quot; onClick=&quot;variantA&quot;/&gt;	&lt;Button label=&quot;Variant B - Text 1 and Text Text 3 readonly&quot; width=&quot;md-3&quot; onClick=&quot;variantB&quot;/&gt;	&lt;Button label=&quot;Variant C - Text 1, Text 2 and Text 3 readonly&quot; width=&quot;md-3&quot; onClick=&quot;variantC&quot;/&gt;	&lt;OutputLabel width=&quot;md-12&quot; value=&quot;The same above example (same rules) is for SetByProgrammer tags.&quot;/&gt;	&lt;InputText id=&quot;setByProgrammerExampleText1&quot; label=&quot;Code&quot; width=&quot;md-12&quot; rowsCount=&quot;2&quot; value=&quot;Example text 1&quot;/&gt;	&lt;InputText id=&quot;setByProgrammerExampleText2&quot; label=&quot;Code&quot; width=&quot;md-12&quot; rowsCount=&quot;2&quot; value=&quot;Example text 2&quot;/&gt;	&lt;InputText id=&quot;setByProgrammerExampleText3&quot; label=&quot;Code&quot; width=&quot;md-12&quot; rowsCount=&quot;2&quot; value=&quot;Example text 3&quot;/&gt;&lt;/PanelGroup&gt;"/>
                </PanelGroup>

                <PanelGroup width="md-12" label="{$.fh.docs.availability_java_code}" id="_Form_TabContainer_Tab1_PanelGroup_PanelGroup5">
                    <InputText id="variantsCode3" label="{$.fh.docs.availability_form_code}" width="md-12" rowsCount="12" value="@AvailabilityRuleMethod(&quot;setByProgrammerExampleText1&quot;)	private AccessibilityEnum setByProgrammerExampleText1(AccessibilityRule accessibilityRule) \{		return AccessibilityEnum.VIEW;	\}	@AvailabilityRuleMethod(&quot;setByProgrammerExampleText2&quot;)	private AccessibilityEnum setByProgrammerExampleText2(AccessibilityRule accessibilityRule) \{		return AccessibilityEnum.VIEW;	\}	@AvailabilityRuleMethod(&quot;setByProgrammerExampleText3&quot;)	private AccessibilityEnum setByProgrammerExampleText3(AccessibilityRule accessibilityRule) \{		return AccessibilityEnum.VIEW;	\}"/>
                    <InputText id="variantsCode4" label="{$.fh.docs.availability_usecase_code}" width="md-12" rowsCount="24" value="@Overrideprotected void start() \{showForm(AvailabilityConfigurationForm.class, model);\}@Actionpublic void variantA() \{showForm(AvailabilityConfigurationForm.class, model, &quot;a&quot;);\}@Actionpublic void variantB() \{showForm(AvailabilityConfigurationForm.class, model, &quot;b&quot;);\}@Actionpublic void variantC() \{showForm(AvailabilityConfigurationForm.class, model, &quot;c&quot;);\}@Actionpublic void variantDefault() \{start();\}"/>
                </PanelGroup>

            </PanelGroup>
        </Tab>

        <Tab label="{$.fh.docs.availability_boundable_availability}" id="_Form_TabContainer_Tab2">
            <PanelGroup label="{$.fh.docs.availability_availability_configuration_in_table}" id="_Form_TabContainer_Tab2_PanelGroup1">

            <Table collection="{propertyElements}" iterator="prop" id="_Form_TabContainer_Tab2_PanelGroup1_Table">
                <Column label="InputText" id="_Form_TabContainer_Tab2_PanelGroup1_Table_Column1">
                    <InputText width="md-12" availability="{prop.availability}" id="_Form_TabContainer_Tab2_PanelGroup1_Table_Column1_InputText"/>
                </Column>
                <Column label="InputNumber" id="_Form_TabContainer_Tab2_PanelGroup1_Table_Column2">
                    <InputNumber availability="{prop.availability}" id="_Form_TabContainer_Tab2_PanelGroup1_Table_Column2_InputNumber"/>
                </Column>
                <Column label="{$.fh.docs.availability_availability}" id="_Form_TabContainer_Tab2_PanelGroup1_Table_Column3">
                    <CheckBox width="md-12" label="{$.fh.docs.availability_editable}" value="{prop.editable}" onChange="onEditableClick(prop)" id="_Form_TabContainer_Tab2_PanelGroup1_Table_Column3_CheckBox"/>
                </Column>
            </Table>

                <InputText width="md-12" id="availabilityConfigurationTableExampleCode_1" label="{$.fh.docs.availability_xml}" rowsCount="11">
                    <![CDATA[![ESCAPE[<Table label="AvailabilityConfiguration" collection="{propertyElements}" iterator="prop">
    <Column label="InputText">
        <InputText availability="{prop.availability}"></InputText>
    </Column>
    <Column label="InputNumber">
        <InputNumber availability="{prop.availability}"></InputNumber>
    </Column>
    <Column label="Availability">
        <CheckBox width="md-12" label="Editable" value="{prop.editable}" onChange="onEditableClick(this,prop)"></CheckBox>
    </Column>
</Table>]]]]>
                </InputText>
                <InputText width="md-12" id="availabilityConfigurationTableExampleCode_2" label="{$.fh.docs.availability_java}" rowsCount="28">
                    <![CDATA[![ESCAPE[package pl.fhframework.docs.availability.model;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.model.forms.AccessibilityEnum;

@Getter
@Setter
public class PropertyElement {
    private boolean editable;
    private AccessibilityEnum availability = AccessibilityEnum.EDIT;

    public PropertyElement(boolean editable) {
        this.editable = editable;
    }
}

//Action defined in UseCase class

@Action
public void onEditableClick(PropertyElement propertyElement) {
    boolean editable = propertyElement.isEditable();
    if (editable) {
        propertyElement.setAvailability(AccessibilityEnum.EDIT);
    } else {
        propertyElement.setAvailability(AccessibilityEnum.VIEW);
    }
}]]]]>
                </InputText>
            </PanelGroup>

            <PanelGroup label="{$.fh.docs.availability_availability_value}" id="_Form_TabContainer_Tab2_PanelGroup2">
                <InputText width="md-12" label="{$.fh.docs.availability_xml}" rowsCount="3" availability="VIEW" id="_Form_TabContainer_Tab2_PanelGroup2_InputText1">
<![CDATA[<AvailabilityConfiguration>
    <AvailabilityValue value="FORM.calculatedAvailability(true)">componentId</AvailabilityValue>
</AvailabilityConfiguration>]]>
                </InputText>
                <InputText label="{$.fh.docs.availability_form_code}" width="md-12" rowsCount="10" availability="VIEW" id="_Form_TabContainer_Tab2_PanelGroup2_InputText2">
                    <![CDATA[![ESCAPE[public class MyForm extends Form<MyModel> {

    public AccessibilityEnum calculatedAvailability(boolean someParameter) {
        if (someParameter) {
            return AccessibilityEnum.VIEW;
        } else {
            return AccessibilityEnum.EDIT;
        }
    }
}]]]]>
                </InputText>
            </PanelGroup>
            <PanelGroup label="{$.fh.docs.availability_availability_configuration_for_controls}" id="_Form_TabContainer_Tab2_PanelGroup3">
                <Group id="_Form_TabContainer_Tab2_PanelGroup3_Group">
                    <Group id="_Form_TabContainer_Tab2_PanelGroup3_Group_Group"><OutputLabel width="md-12" value="{$.fh.docs.availability_set_availability}:" id="_Form_TabContainer_Tab2_PanelGroup3_Group_Group_OutputLabel"/></Group>
                    <Button label="{$.fh.docs.availability_hide}" onClick="onHideButtonClick(this)" id="_Form_TabContainer_Tab2_PanelGroup3_Group_Button1"/>
                    <Spacer width="md-1" id="_Form_TabContainer_Tab2_PanelGroup3_Group_Spacer1"/>
                    <Button label="{$.fh.docs.availability_edit}" onClick="onEditButtonClick(this)" id="_Form_TabContainer_Tab2_PanelGroup3_Group_Button2"/>
                    <Spacer width="md-1" id="_Form_TabContainer_Tab2_PanelGroup3_Group_Spacer2"/>
                    <Button label="{$.fh.docs.availability_view}" onClick="onViewButtonClick(this)" id="_Form_TabContainer_Tab2_PanelGroup3_Group_Button3"/>
                </Group>
                <PanelGroup availability="{controlAvailability}" id="_Form_TabContainer_Tab2_PanelGroup3_PanelGroup">
                    <InputText width="md-12" availability="{controlAvailability}" label="InputText" id="_Form_TabContainer_Tab2_PanelGroup3_PanelGroup_InputText"/>
                    <InputNumber availability="{controlAvailability}" label="InputNumber" id="_Form_TabContainer_Tab2_PanelGroup3_PanelGroup_InputNumber"/>
                    <CheckBox width="md-12" availability="{controlAvailability}" label="CheckBox" id="_Form_TabContainer_Tab2_PanelGroup3_PanelGroup_CheckBox"/>
                    <Button availability="{controlAvailability}" label="Button" id="_Form_TabContainer_Tab2_PanelGroup3_PanelGroup_Button"/>
                    <SelectOneMenu width="md-12" availability="{controlAvailability}" values="1|2|3|4" value="1" id="_Form_TabContainer_Tab2_PanelGroup3_PanelGroup_SelectOneMenu"/>
                    <RadioOptionsGroup width="md-12" availability="{controlAvailability}" values="1|2|3|4" value="1" id="_Form_TabContainer_Tab2_PanelGroup3_PanelGroup_RadioOptionsGroup"/>
                </PanelGroup>

                <InputText width="md-12" id="availabilityConfigurationTableExampleCode_3" label="{$.fh.docs.availability_xml}" rowsCount="16">
                    <![CDATA[![ESCAPE[<Group>
    <Group><OutputLabel value="Set availability:"/></Group>
    <Button label="Hide" onClick="onHideButtonClick(this)"/>
    <Spacer width="md-1"/>
    <Button label="Edit" onClick="onEditButtonClick(this)"/>
    <Spacer width="md-1"/>
    <Button label="View" onClick="onViewButtonClick(this)"/>
</Group>
<PanelGroup availability="{controlAvailability}">
    <InputText availability="{controlAvailability}" label="InputText"/>
    <InputNumber availability="{controlAvailability}" label="InputNumber"/>
    <CheckBox width="md-12" availability="{controlAvailability}" label="CheckBox"/>
    <Button availability="{controlAvailability}" label="Button"/>
    <SelectOneMenu availability="{controlAvailability}" values="1|2|3|4" value="1"/>
    <RadioOptionsGroup availability="{controlAvailability}" values="1|2|3|4" value="1"/>
</PanelGroup>]]]]>
                </InputText>


                <InputText width="md-12" id="availabilityConfigurationTableExampleCode_4" label="{$.fh.docs.availability_java}" rowsCount="20">
                    <![CDATA[![ESCAPE[     @Action
    public void onHideButtonClick(ViewEvent<AvailabilityConfigurationModel> viewEvent) {
        AvailabilityConfigurationModel model = viewEvent.getSourceForm().getModel();
        model.setControlAvailability(AccessibilityEnum.HIDDEN);
        FhLogger.debug(logger -> logger.log("onHideButtonClick({})", viewEvent));
    }

    @Action
    public void onViewButtonClick(ViewEvent<AvailabilityConfigurationModel> viewEvent) {
        AvailabilityConfigurationModel model = viewEvent.getSourceForm().getModel();
        model.setControlAvailability(AccessibilityEnum.VIEW);
        FhLogger.debug(logger -> logger.log("onViewButtonClick({})", viewEvent));
    }

    @Action
    public void onEditButtonClick(ViewEvent<AvailabilityConfigurationModel> viewEvent) {
        AvailabilityConfigurationModel model = viewEvent.getSourceForm().getModel();
        model.setControlAvailability(AccessibilityEnum.EDIT);
        FhLogger.debug(logger -> logger.log("onEditButtonClick({})", viewEvent));
    }]]]]>
                </InputText>
            </PanelGroup>
        </Tab>

        <Tab label="{$.fh.docs.availability_elements}" id="_Form_TabContainer_Tab3">
            <PanelGroup id="AtributGrup-100" width="md-12" label="{$.fh.docs.availability_elements}">

                <InputText id="Art-1" value="&lt;Edit&gt; {$.fh.docs.availability_component_is_editable}" width="md-12"/>
                <InputText id="Art-2" value="&lt;Preview&gt; &lt;ReadOnly&gt; {$.fh.docs.availability_component_is_read_only}" width="md-12"/>
                <InputText id="Art-3" value="&lt;Invisible&gt; {$.fh.docs.availability_component_is_hidden}" width="md-12"/>
                <InputText id="Art-5" value="&lt;SetByProgramer&gt; {$.fh.docs.availability_component_will_be_the_method}" width="md-12"/>
                <InputText id="Art-6" value="&lt;Variant&gt; {$.fh.docs.availability_the_variant_contains_the_availability}" width="md-12"/>
                <InputText id="Art-8" value="&lt;AvailabilityValue&gt; {$.fh.docs.availability_value}" width="md-12"/>
                <InputText id="Art-7" value="&lt;Availability&gt; {$.fh.docs.availability_complex_availability}" width="md-12"/>

            </PanelGroup>
        </Tab>

        <Tab label="{$.fh.docs.availability_permission_based_availability}" id="_Form_TabContainer_Tab4">
            <PanelGroup width="md-12" label="{$.fh.docs.availability_permission_based_using_availability_tag}" id="_Form_TabContainer_Tab4_PanelGroup1">
                <InputText width="md-12" label="{$.fh.docs.availability_xml}" rowsCount="28" availability="VIEW" id="_Form_TabContainer_Tab4_PanelGroup1_InputText1">
<![CDATA[<AvailabilityConfiguration>
    <!-- {$.fh.docs.availability_perm_based} -->
    <Availability previewPermissions="my/functionality/permissionA,my/functionality/permissionB"
                  editPermissions="my/functionality/permissionC"
                  invisiblePermissions="my/functionality/permissionD"
                  defaultValue="HIDDEN">component1,component2</Availability>

    <!-- my.functionality.permissionA => my/functionality/permissionA -->
    <Availability previewPermissions="my.functionality.permissionA,my.functionality.permissionB"
                  editPermissions="my.functionality.permissionC"
                  invisiblePermissions="my.functionality.permissionD"
                  defaultValue="HIDDEN">component1,component2</Availability>

    <!-- {$.fh.docs.availability_calculated} -->
    <Availability previewPermissions="my.functionality.permA,my.functionality.permissionB"
                  editPermissions="my.functionality.permissionC"
                  invisiblePermissions="my.functionality.permissionD"
                  defaultValue="FORM.calculatedAvailability(true)">component1,component2</Availability>

    <!-- {$.fh.docs.availability_use_some_attrs} -->
    <Availability editPermissions="my.module.permC" defaultValue="VIEW">component1,component2</Availability>

    <!-- {$.fh.docs.availability_roles_based} -->
    <Availability previewRoles="admin,boss"
                  editRoles="user"
                  invisibleRoles="guestRole"
                  default="calculatedAvailability(true)">component1,component2</Availability>
</AvailabilityConfiguration>]]>
                </InputText>
                <InputText label="{$.fh.docs.availability_form_code}" width="md-12" rowsCount="10" availability="VIEW" id="_Form_TabContainer_Tab4_PanelGroup1_InputText2">
<![CDATA[![ESCAPE[public class MyForm extends Form<MyModel> {

    public AccessibilityEnum calculatedAvailability(boolean someParameter) {
        if (someParameter) {
            return AccessibilityEnum.VIEW;
        } else {
            return AccessibilityEnum.EDIT;
        }
    }
}]]]]>
                </InputText>
            </PanelGroup>
            <PanelGroup width="md-12" label="{$.fh.docs.availability_permission_based_using_permission_checks}" id="_Form_TabContainer_Tab4_PanelGroup2">
                <InputText width="md-12" label="{$.fh.docs.availability_xml}" rowsCount="5" availability="VIEW" id="_Form_TabContainer_Tab4_PanelGroup2_InputText">
<![CDATA[<AvailabilityConfiguration>
    <!-- my.functionality.permissionA => my/functionality/permissionA -->
    <Preview when="-PERM.my.functionality.permissionA">componentId</Preview>
    <Preview when="-ROLE.administrator">componentId</Preview>
</AvailabilityConfiguration>]]>
                </InputText>
            </PanelGroup>
        </Tab>

        <Tab label="{$.fh.docs.availability_inheritance}" id="_Form_TabContainer_Tab5">
            <PanelGroup label="{$.fh.docs.availability_limited_inheritance_example}" id="_Form_TabContainer_Tab5_PanelGroup1">
                <PanelGroup id="viewPanel" label="{$.fh.docs.availability_panel_with_view_availability}">
                    <InputText width="md-12" id="panelInput1" label="{$.fh.docs.availability_input_without_any_defined_availability_inherits_view_from_panel}"/>
                    <InputText width="md-12" id="panelInput2" label="{$.fh.docs.availability_input_with_a_availability_rule_in_availabilityconfiguration}"/>
                    <InputText width="md-12" id="panelInput3" label="{$.fh.docs.availability_input_with_a_availability_binding}" availability="EDIT"/>
                </PanelGroup>
                <InputText width="md-12" id="availabilityConfigurationTableExampleCode_5" label="{$.fh.docs.availability_xml}" rowsCount="10">
                    <![CDATA[![ESCAPE[<AvailabilityConfiguration>
    <Preview>viewPanel</Preview>
    <Edit>panelInput2</Edit>
</AvailabilityConfiguration>
...
<PanelGroup id="viewPanel" label="Panel with VIEW availability">
    <InputText id="panelInput1" label="Input without any defined availability - inherits VIEW from panel" />
    <InputText id="panelInput2" label="Input with a availability rule in AvailabilityConfiguration" />
    <InputText id="panelInput3" label="Input with a availability binding" availability="EDIT"/>
</PanelGroup>]]]]>
                </InputText>
            </PanelGroup>

            <PanelGroup label="{$.fh.docs.availability_nested_panels_example}" id="_Form_TabContainer_Tab5_PanelGroup2">
                <PanelGroup label="{$.fh.docs.availability_panel_with_view_availability}" availability="VIEW" id="_Form_TabContainer_Tab5_PanelGroup2_PanelGroup">
                    <InputText width="md-12" label="{$.fh.docs.availability_input_in_panel}" id="_Form_TabContainer_Tab5_PanelGroup2_PanelGroup_InputText"/>
                    <PanelGroup label="{$.fh.docs.availability_panel_with_edit_availability}" availability="EDIT" id="_Form_TabContainer_Tab5_PanelGroup2_PanelGroup_PanelGroup">
                        <InputText width="md-12" label="{$.fh.docs.availability_input_in_panel}" id="_Form_TabContainer_Tab5_PanelGroup2_PanelGroup_PanelGroup_InputText"/>
                        <PanelGroup label="{$.fh.docs.availability_panel_with_view_availability}" availability="VIEW" id="_Form_TabContainer_Tab5_PanelGroup2_PanelGroup_PanelGroup_PanelGroup">
                            <InputText width="md-12" label="{$.fh.docs.availability_input_in_panel}" id="_Form_TabContainer_Tab5_PanelGroup2_PanelGroup_PanelGroup_PanelGroup_InputText"/>
                            <PanelGroup label="{$.fh.docs.availability_panel_with_edit_availability}" availability="EDIT" id="_Form_TabContainer_Tab5_PanelGroup2_PanelGroup_PanelGroup_PanelGroup_PanelGroup">
                                <InputText width="md-12" label="{$.fh.docs.availability_input_in_panel}" id="_Form_TabContainer_Tab5_PanelGroup2_PanelGroup_PanelGroup_PanelGroup_PanelGroup_InputText"/>
                            </PanelGroup>
                        </PanelGroup>
                    </PanelGroup>
                </PanelGroup>
                <InputText width="md-12" id="availabilityConfigurationTableExampleCode_6" label="{$.fh.docs.availability_xml}" rowsCount="12">
                    <![CDATA[![ESCAPE[<PanelGroup label="Panel with VIEW" availability="VIEW">
    <InputText label="Input in panel" />
    <PanelGroup label="Panel with EDIT" availability="EDIT">
        <InputText label="Input in panel" />
        <PanelGroup label="Panel with VIEW" availability="VIEW">
            <InputText label="Input in panel" />
            <PanelGroup label="Panel with EDIT" availability="EDIT">
                <InputText label="Input in panel" />
            </PanelGroup>
        </PanelGroup>
    </PanelGroup>
</PanelGroup>]]]]>
                </InputText>
            </PanelGroup>
        </Tab>
    </TabContainer>


</Form>