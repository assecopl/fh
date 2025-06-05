<?xml version="1.0" encoding="UTF-8"?>
<Form xmlns="http://fh.asseco.com/form/1.0" id="RunningDocumentationForm" label="{$.fh.docs.running_running_fh}" container="mainForm" formType="STANDARD">
    <AvailabilityConfiguration>
        <Preview>listing1,listing2</Preview>
    </AvailabilityConfiguration>
    <TabContainer width="md-12" id="_Form_TabContainer">
        <Tab label="Dynamic XML files" id="_Form_TabContainer_Tab1">
            <PanelGroup width="md-12" label="Location" id="_Form_TabContainer_Tab1_PanelGroup1">
                <OutputLabel width="md-12" id="_Form_TabContainer_Tab1_PanelGroup1_OutputLabel">
                    FH modules are being discovered by finding all module.sys files in JVM's classpath.
                    Each module may use XML files of dynamic classes (form, use case, rule, model). Files are being read from module’s base URL. Base URL of a module is detected as (in that order):
                    1. An URL to a directory pointed with Spring application property „fhframework.path.MODULENAME” if such property exists.
                    2. (otherwise) An URL to a same-name-as-module subdirectory of a collective directory pointed with Spring application property „fhframework.basepath” if such property exists.
                    3. (otherwise) An URL sources directory if  module.sys was found in target/classes directory and analogous src/main/java sources directory exists.
                    4. (otherwise) An URL to classpath where module.sys was found.
                </OutputLabel>
            </PanelGroup>
            <PanelGroup width="md-12" label="Scanning for new / deleted files" id="_Form_TabContainer_Tab1_PanelGroup2">
                <OutputLabel width="md-12" id="_Form_TabContainer_Tab1_PanelGroup2_OutputLabel">
                    List of files is created once during startup. By default FH doesn’t scan for new or deleted files after initial scan during its startup.
                    If many instances of FH share filesystem and the list of files may change at runtime, then auto scanning for new or deleted files may be configured using fhframework.dynamic.autoscan.* Spring application properties, e.g.:
                </OutputLabel>
                <InputText id="listing2" width="md-12" rowsCount="6">
# Delay between scans in millis
fhframework.dynamic.autoscan.delay=60000
# Comma-separated list of autoscanned modules
fhframework.dynamic.autoscan.modules=demo,sam
# Comma-separated list of autoscanned dynamic areas (MODEL,RULE,FORM,USE_CASE)
fhframework.dynamic.autoscan.areas=RULE,FORM
                </InputText>
            </PanelGroup>
            <PanelGroup width="md-12" label="Editing private (build-in) modules in Designer" id="_Form_TabContainer_Tab1_PanelGroup3">
                <OutputLabel width="md-12" id="_Form_TabContainer_Tab1_PanelGroup3_OutputLabel">
                    By default FH Designer doesn’t allow to edit private (build-in) modules. To enable editing private modules set fhframework.designer.usingPrivateModules application property to true.
                </OutputLabel>
            </PanelGroup>
        </Tab>
        <Tab label="{$.fh.docs.running_jvm_options}" id="_Form_TabContainer_Tab2">
            <PanelGroup width="md-12" label="{$.fh.docs.running_mandatory_options}" id="_Form_TabContainer_Tab2_PanelGroup">
                <OutputLabel width="md-12" id="_Form_TabContainer_Tab2_PanelGroup_OutputLabel">
                    {$.fh.docs.running_to_avoid_loosing_stacktraces_which_collides_with} CompiledClassesHelper.isLocalNullPointerException() {$.fh.docs.running_implementation_always_add}:
                </OutputLabel>
                <InputText id="listing1" width="md-12" value="-XX:-OmitStackTraceInFastThrow"/>
            </PanelGroup>
        </Tab>
        <Tab label="Bez bazy danych (JDBC)" id="_Form_TabContainer_Tab3">
            <MdFileViewer id="mdFileViewerExampleCode1_1" src="dokumentacja/running/withoutDataBase.md" width="md-12" resourceBasePath="dokumentacja/running/"/>
        </Tab>
    </TabContainer>
</Form>