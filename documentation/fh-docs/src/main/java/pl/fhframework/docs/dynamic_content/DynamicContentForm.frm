<?xml version="1.0" encoding="UTF-8"?>
<Form xmlns="http://fh.asseco.com/form/1.0" container="mainForm" id="DynamicContentForm" label="{$.fh.docs.dynamiccontent_dynamic_content}">

    <OutputLabel width="md-12" value=" " id="_Form_OutputLabel1"/>
    <!--<OutputLabel width="md-12" value="(Some examples don't work because of broken js scripts - being repaired)"/>-->
    <OutputLabel width="md-12" value=" " id="_Form_OutputLabel2"/>

    <PanelGroup id="placementGroup" label="{$.fh.docs.dynamiccontent_bag}"/>

    <Group id="_Form_Group1">
        <OutputLabel width="md-12" value=" " id="_Form_Group1_OutputLabel"/>
    </Group>

    <PanelGroup width="md-12" label="{$.fh.docs.dynamiccontent_choose_component_to_add}" id="_Form_PanelGroup2">
        <RadioOptionsGroup width="md-12" label="{$.fh.docs.dynamiccontent_available_components}" values="OutputLabel|InputText|SelectOneMenu|CheckBox|Repeater|Group|Group inside Repeater|Repeater in Repeater" value="{componentChosen}" required="true" onChange="onDynamicContentChangeComponentType(this)" id="_Form_PanelGroup2_RadioOptionsGroup"/>
    </PanelGroup>

    <Group id="_Form_Group2">
        <Button width="md-3" label="{$.fh.docs.dynamiccontent_add_component}" onClick="onDynamicContentAddComponent(this)" id="_Form_Group2_Button1"/>
        <Button width="md-3" label="{$.fh.docs.dynamiccontent_add_3_components}" onClick="onDynamicContentAdd3Components(this)" id="_Form_Group2_Button2"/>

    </Group>
    <Group id="_Form_Group3">
        <OutputLabel width="md-12" value=" " id="_Form_Group3_OutputLabel"/>
    </Group>
    <Group id="_Form_Group4">
        <Button width="md-3" label="{$.fh.docs.dynamiccontent_remove_component}" onClick="onDynamicContentRemoveComponent(this)" id="_Form_Group4_Button1"/>
        <Button width="md-3" label="{$.fh.docs.dynamiccontent_remove_3_components}" onClick="onDynamicContentRemove3Components(this)" id="_Form_Group4_Button2"/>
        <Button width="md-3" label="{$.fh.docs.dynamiccontent_remove_all}" onClick="onDynamicContentRemoveAll(this)" id="_Form_Group4_Button3"/>
    </Group>
    <Group id="_Form_Group5">
        <OutputLabel width="md-12" value=" " id="_Form_Group5_OutputLabel"/>
    </Group>
    <Group id="_Form_Group6">
        <Button width="md-6" label="{$.fh.docs.dynamiccontent_add_1_component_to_the_beginning_and_2_at_the_end}" onClick="onDynamicContentAdd_1Begining_2End_Components(this)" id="_Form_Group6_Button1"/>
        <OutputLabel width="md-12" value=" " id="_Form_Group6_OutputLabel"/>
        <Button width="md-6" label="{$.fh.docs.dynamiccontent_add_1_component_to_the_beginning_at_2_in_the_middle}" onClick="onDynamicContentAdd_1Begining_2Middle_Components(this)" id="_Form_Group6_Button2"/>
    </Group>
    <!-- <Row>
         <Button width="md-2" label="Add group" onClick="onAddGroup(this)"/>
     </Row>-->
    <Group id="_Form_Group7">
        <OutputLabel width="md-12" value=" " id="_Form_Group7_OutputLabel"/>
    </Group>

</Form>