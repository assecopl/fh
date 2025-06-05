<?xml version="1.0" encoding="UTF-8"?>
<Form xmlns="http://fh.asseco.com/form/1.0" container="mainForm" id="documentationHolder" label="{componentName}">
    <AvailabilityConfiguration>
        <ReadOnly>radioOptionsGroupsExampleCode1, radioOptionsGroupsExampleCode2, radioOptionsGroupsExampleCode3,
            radioOptionsGroupsExampleCode4,radioOptionsGroupsExampleCode5,radioOptionsGroupsExampleCode6,radioOptionsGroupOnChange2,radioOptionsGroupsExampleCode4_2,radioOptionsGroupsExampleCode7
        </ReadOnly>
    </AvailabilityConfiguration>
    <OutputLabel width="md-12" value="{description}" id="_Form_OutputLabel"/>
    <Spacer width="md-12" height="25px" id="_Form_Spacer"/>
    <TabContainer id="_Form_TabContainer">
        <Tab label="{$.fh.docs.component.examples}" id="_Form_TabContainer_Tab1">
            <PanelGroup width="md-12" label="{$.fh.docs.component.radiooptionsgroup_radiooptionsgroup_with_simple_usage}" id="_Form_TabContainer_Tab1_PanelGroup1">
                <RadioOptionsGroup width="md-12" label="{$.fh.docs.component.radiooptionsgroup_countries}" values="{RULE.pl.fhframework.core.rules.builtin.StringRuleUtils.stringSplit($.fh.docs.component.radiooptionsgroup_poland_germany_uk_us,'|')}" id="_Form_TabContainer_Tab1_PanelGroup1_RadioOptionsGroup"/>
                <InputText id="radioOptionsGroupsExampleCode1" label="{$.fh.docs.component.code}" width="md-12" rowsCount="1">
                    <![CDATA[![ESCAPE[<RadioOptionsGroup label="Countries" values="Poland|German|UK|US"/>]]]]>
                </InputText>
            </PanelGroup>

            <PanelGroup width="md-12" label="{$.fh.docs.component.radiooptionsgroup_radiooptionsgroup_with_align_parameter}" id="_Form_TabContainer_Tab1_PanelGroup2">
                <RadioOptionsGroup label="{$.fh.docs.component.radiooptionsgroup_countries}" width="md-4" values="{RULE.pl.fhframework.core.rules.builtin.StringRuleUtils.stringSplit($.fh.docs.component.radiooptionsgroup_poland_germany_uk_us,'|')}" horizontalAlign="right" id="_Form_TabContainer_Tab1_PanelGroup2_RadioOptionsGroup1"/>
                <RadioOptionsGroup label="{$.fh.docs.component.radiooptionsgroup_gender}" width="md-4" values="{RULE.pl.fhframework.core.rules.builtin.StringRuleUtils.stringSplit($.fh.docs.component.radiooptionsgroup_male_female,'|')}" horizontalAlign="left" id="_Form_TabContainer_Tab1_PanelGroup2_RadioOptionsGroup2"/>
                <InputText id="radioOptionsGroupsExampleCode2" label="{$.fh.docs.component.code}" width="md-12" rowsCount="2">
                    <![CDATA[![ESCAPE[<RadioOptionsGroup label="Countries" width="md-4" values="Poland|German|UK|US" horizontalAlign="right"/>
<RadioOptionsGroup label="Gender" width="md-4" values="Male|Female" horizontalAlign="left"/>]]]]>
                </InputText>
            </PanelGroup>

            <PanelGroup width="md-12" label="{$.fh.docs.component.radiooptionsgroup_radiooptionsgroup_with_bootstrap_size_parameter}" id="_Form_TabContainer_Tab1_PanelGroup3">
                <RadioOptionsGroup label="{$.fh.docs.component.radiooptionsgroup_countries}" values="{RULE.pl.fhframework.core.rules.builtin.StringRuleUtils.stringSplit($.fh.docs.component.radiooptionsgroup_poland_germany_uk_us,'|')}" width="md-9" id="_Form_TabContainer_Tab1_PanelGroup3_RadioOptionsGroup1"/>
                <RadioOptionsGroup label="{$.fh.docs.component.radiooptionsgroup_gender}" values="{RULE.pl.fhframework.core.rules.builtin.StringRuleUtils.stringSplit($.fh.docs.component.radiooptionsgroup_male_female,'|')}" width="md-3" id="_Form_TabContainer_Tab1_PanelGroup3_RadioOptionsGroup2"/>
                <InputText id="radioOptionsGroupsExampleCode3" label="{$.fh.docs.component.code}" width="md-12" rowsCount="2">
                    <![CDATA[![ESCAPE[<RadioOptionsGroup label="Countries" values="Poland|German|UK|US" width="md-9"/>
<RadioOptionsGroup label="Gender" values="Male|Female" width="md-3"/>]]]]>
                </InputText>
            </PanelGroup>

            <PanelGroup width="md-12" label="{$.fh.docs.component.radiooptionsgroup_radiooptionsgroup_with_required_value}" id="_Form_TabContainer_Tab1_PanelGroup4">
                <RadioOptionsGroup width="md-12" label="{$.fh.docs.component.radiooptionsgroup_countries}" value="{selectRadioGroupValue}" values="{RULE.pl.fhframework.core.rules.builtin.StringRuleUtils.stringSplit($.fh.docs.component.radiooptionsgroup_poland_germany_uk_us,'|')}" required="true" onChange="onChangeExample" id="_Form_TabContainer_Tab1_PanelGroup4_RadioOptionsGroup"/>
                <InputText id="radioOptionsGroupsExampleCode4" label="{$.fh.docs.component.code}" width="md-12" rowsCount="1">
                    <![CDATA[![ESCAPE[<RadioOptionsGroup label="Countries" value="{selectRadioGroupValue}" values="Poland|German|UK|US" required="true" onChange="onChangeExample"/>]]]]>
                </InputText>
            </PanelGroup>

            <PanelGroup width="md-12" label="{$.fh.docs.component.radiooptionsgroup_radiooptionsgroup_with_model_value_changed}" id="_Form_TabContainer_Tab1_PanelGroup5">
                <Button id="radioButton1" label="{$.fh.docs.component.radiooptionsgroup_poland}" onClick="polandClicked()" width="md-3"/>
                <Button id="radioButton2" label="{$.fh.docs.component.radiooptionsgroup_germany}" onClick="germanClicked()" width="md-3"/>
                <Button id="radioButton3" label="{$.fh.docs.component.radiooptionsgroup_uk}" onClick="ukClicked()" width="md-3"/>
                <Button id="radioButton4" label="{$.fh.docs.component.radiooptionsgroup_us}" onClick="usClicked()" width="md-3"/>
                <RadioOptionsGroup width="md-12" label="{$.fh.docs.component.radiooptionsgroup_countries}" value="{selectRadioGroupValue2}" values="Poland|Germany|UK|USA" onChange="onChangeExample" id="_Form_TabContainer_Tab1_PanelGroup5_RadioOptionsGroup"/>
                <InputText id="radioOptionsGroupsExampleCode4_2" label="{$.fh.docs.component.code}" width="md-12" rowsCount="2">
                    <![CDATA[![ESCAPE[<Button id="radioButton1" label="Poland" onClick="polandClicked()" width="md-3"/>
<Button id="radioButton2" label="German" onClick="germanClicked()" width="md-3"/>
<Button id="radioButton3" label="UK" onClick="ukClicked()" width="md-3"/>
<Button id="radioButton4" label="US" onClick="usClicked()" width="md-3"/>
<RadioOptionsGroup label="Countries" value="{selectRadioGroupValue2}" values="Poland|German|UK|US" onChange="onChangeExample"/>]]]]>
                </InputText>
            </PanelGroup>

            <PanelGroup width="md-12" label="{$.fh.docs.component.radiooptionsgroup_radiooptionsgroup_with_onchange_event}" id="_Form_TabContainer_Tab1_PanelGroup6">
                <RadioOptionsGroup width="md-12" label="{$.fh.docs.component.radiooptionsgroup_countries}" value="{selectRadioGroupOnChangeValue}" values="{RULE.pl.fhframework.core.rules.builtin.StringRuleUtils.stringSplit($.fh.docs.component.radiooptionsgroup_poland_germany_uk_us,'|')}" onChange="onChangeExample" id="_Form_TabContainer_Tab1_PanelGroup6_RadioOptionsGroup"/>
                <OutputLabel width="md-12" id="radioOptionsGroups4_2" value="{selectRadioGroupOnChangeValue}"/>
                <InputText id="radioOptionsGroupsExampleCode5" label="{$.fh.docs.component.code}" width="md-12" rowsCount="2">
                    <![CDATA[![ESCAPE[<RadioOptionsGroup label="Countries" value="{selectRadioGroupOnChangeValue}" values="Poland|German|UK|US" onChange="onChangeExample"/>
<OutputLabel id="radioOptionsGroups4_2" value="{selectRadioGroupOnChangeValue}" />]]]]>
                </InputText>
            </PanelGroup>

            <PanelGroup width="md-12" label="{$.fh.docs.component.radiooptionsgroup_radiooptionsgroup_with_onchange_event_bound_to_another_radiooptionsgroup}" id="_Form_TabContainer_Tab1_PanelGroup7">
                <RadioOptionsGroup width="md-12" id="radioOptionsGroupOnChange1" label="{$.fh.docs.component.radiooptionsgroup_countries}" value="{radioGroupToRadioGroupValue}" values="{RULE.pl.fhframework.core.rules.builtin.StringRuleUtils.stringSplit($.fh.docs.component.radiooptionsgroup_poland_germany_uk_us,'|')}" onChange="onChangeExample"/>
                <RadioOptionsGroup width="md-12" id="radioOptionsGroupOnChange2" label="{$.fh.docs.component.radiooptionsgroup_countries}1" value="{radioGroupToRadioGroupValue}" values="{RULE.pl.fhframework.core.rules.builtin.StringRuleUtils.stringSplit($.fh.docs.component.radiooptionsgroup_poland_germany_uk_us,'|')}" onChange="onChangeExample"/>
                <InputText id="radioOptionsGroupsExampleCode6" label="{$.fh.docs.component.code}" width="md-12" rowsCount="2">
                    <![CDATA[![ESCAPE[<RadioOptionsGroup id="radioOptionsGroupOnChange1" label="Countries" value="{radioGroupToRadioGroupValue}" values="Poland|German|UK|US" onChange="onChangeExample"/>
<RadioOptionsGroup id="radioOptionsGroupOnChange2" label="Countries1" value="{radioGroupToRadioGroupValue}" values="Poland|German|UK|US" onChange="onChangeExample"/>]]]]>
                </InputText>
            </PanelGroup>

            <PanelGroup width="md-12" label="{$.fh.docs.component.radiooptionsgroup_radiooptionsgroup_with_multi_size}" id="_Form_TabContainer_Tab1_PanelGroup8">
                <RadioOptionsGroup id="radioOptionsGroupOnChange7_1" label="{$.fh.docs.component.radiooptionsgroup_countries}" values="{RULE.pl.fhframework.core.rules.builtin.StringRuleUtils.stringSplit($.fh.docs.component.radiooptionsgroup_poland_germany_uk_us,'|')}" width="xs-6,sm-6,md-4"/>
                <RadioOptionsGroup id="radioOptionsGroupOnChange7_2" label="{$.fh.docs.component.radiooptionsgroup_gender}" values="{RULE.pl.fhframework.core.rules.builtin.StringRuleUtils.stringSplit($.fh.docs.component.radiooptionsgroup_male_female,'|')}" width="xs-6,sm-6,md-4"/>
                <InputText id="radioOptionsGroupsExampleCode7" label="{$.fh.docs.component.code}" width="md-12" rowsCount="2">
                    <![CDATA[![ESCAPE[<RadioOptionsGroup id="radioOptionsGroupOnChange7_1" label="Countries" values="Poland|German|UK|US" width="xs-6,sm-6,md-4"/>
<RadioOptionsGroup id="radioOptionsGroupOnChange7_2" label="Gender" values="Male|Female" width="xs-6,sm-6,md-4"/>]]]]>
                </InputText>
            </PanelGroup>
        </Tab>

        <Tab label="{$.fh.docs.component.attributes}" id="_Form_TabContainer_Tab2">
            <Table iterator="item" collection="{attributes}" id="_Form_TabContainer_Tab2_Table">
                <Column label="{$.fh.docs.component.attributes_identifier}" value="{item.name}" width="15" id="_Form_TabContainer_Tab2_Table_Column1"/>
                <Column label="{$.fh.docs.component.attributes_type}" value="{item.type}" width="15" id="_Form_TabContainer_Tab2_Table_Column2"/>
                <Column label="{$.fh.docs.component.attributes_boundable}" value="{item.boundable}" width="10" id="_Form_TabContainer_Tab2_Table_Column3"/>
                <Column label="{$.fh.docs.component.attributes_default_value}" value="{item.defaultValue}" width="20" id="_Form_TabContainer_Tab2_Table_Column4"/>
                <Column label="{$.fh.docs.component.attributes_description}" value="{item.description}" width="40" id="_Form_TabContainer_Tab2_Table_Column5"/>
            </Table>
        </Tab>
    </TabContainer>
    <Button id="pBack" label="[icon='fa fa-chevron-left'] {$.fh.docs.component.back}" onClick="backToFormComponentsList()"/>

</Form>