<?xml version="1.0" encoding="UTF-8"?>
<Form xmlns="http://fh.asseco.com/form/1.0" id="CreatingNewModuleForm" label="{$.fh.docs.create_new_module_menu}" container="mainForm" formType="STANDARD">
    <AvailabilityConfiguration>
        <Preview>newModule4,config1,config2,config3</Preview>
    </AvailabilityConfiguration>
    <PanelGroup label="TODO: describe module.sys">

    </PanelGroup>
    <PanelGroup width="md-12" label="{$.fh.docs.create_new_module_archetype_configure}" id="_Form_PanelGroup3">
        <OutputLabel width="12" id="_Form_PanelGroup3_OutputLabel1">
            {$.fh.docs.create_new_module_archetype_configure_dependecy}
        </OutputLabel>
        <Spacer width="md-12" height="12px" id="_Form_PanelGroup3_Spacer1"/>
        <InputText id="newModule4" label="pom.xml" width="md-12" rowsCount="5">
        &lt;dependency&gt;
            &lt;groupId&gt;pl.sample.module &lt;/groupId&gt;
            &lt;artifactId&gt;module&lt;/artifactId&gt;
            &lt;version&gt;1.0-SNAPSHOT&lt;/version&gt;
        &lt;/dependency&gt;
        </InputText>
        <Spacer width="md-12" height="12px" id="_Form_PanelGroup3_Spacer2"/>
        <OutputLabel width="md-12" id="_Form_PanelGroup3_OutputLabel2">
            {$.fh.docs.create_new_module_archetype_generation_warning}
        </OutputLabel>
        <Spacer width="md-12" height="12px" id="_Form_PanelGroup3_Spacer3"/>
        <OutputLabel width="md-12" id="_Form_PanelGroup3_OutputLabel3">
            Every FH module can provide optional, additional configuration for Spring, JPA and Security.
        </OutputLabel>
        <Spacer width="md-12" height="12px" id="_Form_PanelGroup3_Spacer4"/>
        <OutputLabel width="md-12" id="_Form_PanelGroup3_OutputLabel4">
            By defining Java class (not a Spring bean) implementing PackagesScanConfiguration you can hand over additional packages (e.g. from external jar libraries) that will be included in scanning Spring Beans, Spring Data Repository and JPA Entities:
        </OutputLabel>
        <Spacer width="md-12" height="12px" id="_Form_PanelGroup3_Spacer5"/>
        <InputText id="config1" label="PackagesScanConfiguration" width="md-12" rowsCount="3">
additionalComponentPackages() - returns Strings list with packages name for Spring component scanning (equivalent of @ComponentScan)
additionalRepositoryPackages() - returns Strings list with packages name for Spring Data Repositories scanning (equivalent of @EnableJpaRepositories)
additionalEntityPackages() - returns Strings list with packages name for Entity scanning
        </InputText>
        <OutputLabel width="md-12" id="_Form_PanelGroup3_OutputLabel5">
            By defining Spring bean implementing FhWebConfiguration you can provide additional Http Security settings:
        </OutputLabel>
        <Spacer width="md-12" height="12px" id="_Form_PanelGroup3_Spacer6"/>
        <InputText id="config2" label="FhWebConfiguration" width="md-12" rowsCount="2">
permitedToAllRequestUrls() - returns Strings list with urls that don't require authentication (are available publicly)
configure(HttpSecurity http) - allows to configure HttpSecurity object
        </InputText>
        <OutputLabel width="md-12" id="_Form_PanelGroup3_OutputLabel6">
            Specific JPA configuration can be added with Spring bean implementaing FhJpaConfiguration:
        </OutputLabel>
        <Spacer width="md-12" height="12px" id="_Form_PanelGroup3_Spacer7"/>
        <InputText id="config3" label="FhJpaConfiguration" width="md-12" rowsCount="2">
configDefaultBuilder(Builder builder, ...) - allows for additional confugiration of default data source
configEntityManagerFactoryBuilder(EntityManagerFactoryBuilder factoryBuilder, ...) - allows to configure additional EntityManagerFactory instances
        </InputText>
    </PanelGroup>
</Form>