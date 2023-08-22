<?xml version="1.0" encoding="UTF-8"?>
<Form xmlns="http://fh.asseco.com/form/1.0" container="mainForm" id="documentationHolder" label="{$.fh.docs.management_application_modes}">
    <AvailabilityConfiguration>
        <ReadOnly>springActiveProfile,exampleConfigurationFile,groupWithoutMode,groupWithDevMode,nodeWithDevMode</ReadOnly>
    </AvailabilityConfiguration>

    <TabContainer id="_Form_TabContainer">
        <Tab label="{$.fh.docs.management_application_modes}" id="_Form_TabContainer_Tab1">
            <PanelGroup width="md-12" label="{$.fh.docs.management_available_application_modes}" id="_Form_TabContainer_Tab1_PanelGroup">
                <OutputLabel width="md-12" value="{$.fh.docs.management_it_is_possible_to_select_one}:" id="_Form_TabContainer_Tab1_PanelGroup_OutputLabel"/>
                <PanelGroup id="_Form_TabContainer_Tab1_PanelGroup_PanelGroup1">
                    <OutputLabel width="md-12" value="- {$.fh.docs.management_empty_none_mode_is_selected}" id="_Form_TabContainer_Tab1_PanelGroup_PanelGroup1_OutputLabel1"/>
                    <OutputLabel width="md-12" value="- {$.fh.docs.management_dev_development_mode}" id="_Form_TabContainer_Tab1_PanelGroup_PanelGroup1_OutputLabel2"/>
                    <OutputLabel width="md-12" value="- {$.fh.docs.management_prod_production_mode}" id="_Form_TabContainer_Tab1_PanelGroup_PanelGroup1_OutputLabel3"/>
                </PanelGroup>

                <PanelGroup width="md-12" label="{$.fh.docs.management_how_to_change_application_mode}" id="_Form_TabContainer_Tab1_PanelGroup_PanelGroup2">
                    <OutputLabel width="md-12" value="{$.fh.docs.management_application_mode_is_based_on}: " id="_Form_TabContainer_Tab1_PanelGroup_PanelGroup2_OutputLabel1"/>
                    <Link width="md-12" url="https://docs.spring.io/spring-boot/docs/current/reference/html/howto-properties-and-configuration.html#howto-set-active-spring-profiles" value="{$.fh.docs.management_set_the_active_spring_profiles}" id="_Form_TabContainer_Tab1_PanelGroup_PanelGroup2_Link"/>
                    <Spacer width="md-12" height="30" id="_Form_TabContainer_Tab1_PanelGroup_PanelGroup2_Spacer1"/>
                    <OutputLabel width="md-12" value="{$.fh.docs.management_to_set_up_value_of_active_application}:" id="_Form_TabContainer_Tab1_PanelGroup_PanelGroup2_OutputLabel2"/>
                    <Spacer width="md-12" height="10" id="_Form_TabContainer_Tab1_PanelGroup_PanelGroup2_Spacer2"/>
                    <InputText id="springActiveProfile" width="md-12" rowsCount="1" value="spring.profiles.active=dev"/>
                    <OutputLabel width="md-12" value="{$.fh.docs.management_pura_application_is_set_to_start}:" id="_Form_TabContainer_Tab1_PanelGroup_PanelGroup2_OutputLabel3"/>
                    <Spacer width="md-12" height="10" id="_Form_TabContainer_Tab1_PanelGroup_PanelGroup2_Spacer3"/>
                    <InputText id="exampleConfigurationFile" width="md-12" rowsCount="1" value="pura\web\src\main\resources\application.properties"/>
                </PanelGroup>
            </PanelGroup>
        </Tab>
        <Tab label="{$.fh.docs.management_menu_nodes_visibility}" id="_Form_TabContainer_Tab2">
            <PanelGroup width="md-12" label="{$.fh.docs.management_steering_of_tree_nodes_visibility}" id="_Form_TabContainer_Tab2_PanelGroup">
                <OutputLabel width="md-12" value="{$.fh.docs.management_since_application_modes_are_available}" id="_Form_TabContainer_Tab2_PanelGroup_OutputLabel1"/>
                <OutputLabel width="md-12" value="{$.fh.docs.management_it_is_not_possible_to_run_manually}" id="_Form_TabContainer_Tab2_PanelGroup_OutputLabel2"/>
                <Spacer width="md-12" height="20px" id="_Form_TabContainer_Tab2_PanelGroup_Spacer"/>

                <PanelGroup width="md-12" label="{$.fh.docs.management_show_nodes_always}" id="_Form_TabContainer_Tab2_PanelGroup_PanelGroup1">
                    <OutputLabel width="md-12" value="{$.fh.docs.management_it_is_possible_to_define} " id="_Form_TabContainer_Tab2_PanelGroup_PanelGroup1_OutputLabel1"/>
                    <OutputLabel width="md-12" value="{$.fh.docs.management_in_example_below}" id="_Form_TabContainer_Tab2_PanelGroup_PanelGroup1_OutputLabel2"/>
                    <InputText id="groupWithoutMode" label="{$.fh.docs.management_xml}" width="md-12" rowsCount="7" value="&lt;Group label=&quot;Documentation&quot; &gt;    &lt;UseCase ref=&quot;pl.fhframework.docs.uc.FormComponentsDocumentationUC&quot; label=&quot;Form components&quot;/&gt;    &lt;Group label=&quot;Fh concepts&quot;&gt;        &lt;UseCase ref=&quot;pl.fhframework.docs.converter.ConverterDocumentationUC&quot; label=&quot;Type conversion&quot; /&gt;    &lt;/Group&gt;    &lt;UseCase ref=&quot;pl.fhframework.docs.widgets.WidgetStatisticsUC&quot; label=&quot;Widgets statistics&quot; /&gt;&lt;/Group&gt;"/>
                </PanelGroup>
                <PanelGroup width="md-12" label="{$.fh.docs.management_hide_nodes_based_on_application_mode}" id="_Form_TabContainer_Tab2_PanelGroup_PanelGroup2">
                    <OutputLabel width="md-12" value="{$.fh.docs.management_in_example_below_2}" id="_Form_TabContainer_Tab2_PanelGroup_PanelGroup2_OutputLabel"/>
                    <InputText id="groupWithDevMode" label="{$.fh.docs.management_xml}" width="md-12" rowsCount="7" value="&lt;Group label=&quot;Documentation&quot; mode=&quot;dev&quot;&gt;    &lt;UseCase ref=&quot;pl.fhframework.docs.uc.FormComponentsDocumentationUC&quot; label=&quot;Form components&quot;/&gt;    &lt;Group label=&quot;Fh concepts&quot;&gt;        &lt;UseCase ref=&quot;pl.fhframework.docs.converter.ConverterDocumentationUC&quot; label=&quot;Type conversion&quot; /&gt;    &lt;/Group&gt;    &lt;UseCase ref=&quot;pl.fhframework.docs.widgets.WidgetStatisticsUC&quot; label=&quot;Widgets statistics&quot; /&gt;&lt;/Group&gt;"/>
                </PanelGroup>
                <PanelGroup width="md-12" label="{$.fh.docs.management_hide_only_specific_node_not_whole_group}" id="_Form_TabContainer_Tab2_PanelGroup_PanelGroup3">
                    <OutputLabel width="md-12" value="{$.fh.docs.management_in_example_below_3}" id="_Form_TabContainer_Tab2_PanelGroup_PanelGroup3_OutputLabel"/>
                    <InputText id="nodeWithDevMode" label="{$.fh.docs.management_xml}" width="md-12" rowsCount="7" value="&lt;Group label=&quot;Documentation&quot;&gt;    &lt;UseCase ref=&quot;pl.fhframework.docs.uc.FormComponentsDocumentationUC&quot; label=&quot;Form components&quot; mode=&quot;dev&quot;/&gt;    &lt;Group label=&quot;Fh concepts&quot;&gt;        &lt;UseCase ref=&quot;pl.fhframework.docs.converter.ConverterDocumentationUC&quot; label=&quot;Type conversion&quot; /&gt;    &lt;/Group&gt;    &lt;UseCase ref=&quot;pl.fhframework.docs.widgets.WidgetStatisticsUC&quot; label=&quot;Widgets statistics&quot; /&gt;&lt;/Group&gt;"/>
                </PanelGroup>
            </PanelGroup>
        </Tab>
        <Tab label="{$.fh.docs.management_other_aspects_impacts}" id="_Form_TabContainer_Tab3">
            <PanelGroup label="{$.fh.docs.management_dependent_functionalities}" id="_Form_TabContainer_Tab3_PanelGroup">
                <OutputLabel width="md-12" value="{$.fh.docs.management_this_section_describes_other_impacts}" id="_Form_TabContainer_Tab3_PanelGroup_OutputLabel"/>
                <Spacer width="md-12" height="20px" id="_Form_TabContainer_Tab3_PanelGroup_Spacer"/>

                <PanelGroup width="md-12" label="{$.fh.docs.management_logging_level}" id="_Form_TabContainer_Tab3_PanelGroup_PanelGroup">
                    <OutputLabel width="md-12" value="{$.fh.docs.management_in_production_mode_ui_errors}" id="_Form_TabContainer_Tab3_PanelGroup_PanelGroup_OutputLabel"/>
                </PanelGroup>
            </PanelGroup>
        </Tab>
    </TabContainer>
</Form>