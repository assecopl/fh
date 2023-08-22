<?xml version="1.0" encoding="UTF-8"?>
<Form xmlns="http://fh.asseco.com/form/1.0" container="mainForm" id="documentationHolder" label="{$.fh.docs.event.file_file_download_event}">
    <AvailabilityConfiguration>
        <ReadOnly>code1,code2</ReadOnly>
    </AvailabilityConfiguration>
    <OutputLabel width="md-12" value="{$.fh.docs.event.file_file_download_event_is_an_event}." id="_Form_OutputLabel1"/>
    <OutputLabel width="md-12" value="{$.fh.docs.event.to_use_notification_use_following} API: pl.fhframework.event.EventRegistry.fireDownloadEvent." id="_Form_OutputLabel2"/>
    <PanelGroup width="md-12" label="{$.fh.docs.event.example_usage}" id="_Form_PanelGroup">
        <OutputLabel width="md-12" value="{$.fh.docs.event.file_there_is_many_possible_api_parameters_depending_on_needs}." id="_Form_PanelGroup_OutputLabel1"/>
        <OutputLabel width="md-12" value="{$.fh.docs.event.file_if_you_will_upload_file_to_a_fileupload} org.springframework.core.io.Resource, {$.fh.docs.event.file_you_can_just_point_it_from_code}" id="_Form_PanelGroup_OutputLabel2"/>
        <FileUpload width="md-3" id="fileUploadId1" file="{modelBindingResource}" label="{$.fh.docs.event.file_upload_file}" onUpload="-" maxSize="3145728"/>
        <Button label="{$.fh.docs.event.file_download_by_resource}" onClick="downloadByResource(this)" id="_Form_PanelGroup_Button1"/>
        <Button label="{$.fh.docs.event.file_download_by_form_element_binding}" onClick="downloadByBinding(this)" id="_Form_PanelGroup_Button2"/>
        <Button label="{$.fh.docs.event.file_download_by_iresourced_form_element}" onClick="downloadByFormElement(this)" id="_Form_PanelGroup_Button3"/>
        <PanelGroup width="md-12" label="{$.fh.docs.event.form_code}" id="_Form_PanelGroup_PanelGroup1">
            <InputText width="md-12" id="code1" rowsCount="4" label="{$.fh.docs.event.use_case_code}" value="&lt;FileUpload id=&quot;fileUploadId1&quot; file=&quot;\{modelBindingResource\}&quot; label=&quot;Upload file&quot; onUpload=&quot;-&quot; maxSize=&quot;3145728&quot;/&gt;
&lt;Button label=&quot;Download by resource&quot; onClick=&quot;downloadByResource(this)&quot;/&gt;
&lt;Button label=&quot;Download by form element binding&quot; onClick=&quot;downloadByBinding(this)&quot;/&gt;
&lt;Button label=&quot;Download by IResourced form element&quot; onClick=&quot;downloadByFormElement(this)&quot;/&gt;"/>
        </PanelGroup>

        <PanelGroup width="md-12" label="{$.fh.docs.event.java_code}" id="_Form_PanelGroup_PanelGroup2">
            <InputText width="md-12" id="code2" rowsCount="21" label="{$.fh.docs.event.use_case_code}">
                <![CDATA[![ESCAPE[
@Autowired
private EventRegistry eventRegistry;

@Action
private void downloadByResource(ViewEvent<FileDownloadEventModel> event) {
    final FileDownloadEventModel model = event.getSourceForm().getModel();
    //below resource does not have to be bound to model, it is only example
    final Resource resource = model.getModelBindingResource();
    eventRegistry.fireDownloadEvent(resource);
}

@Action
private void downloadByBinding(ViewEvent<FileDownloadEventModel> event) {
    eventRegistry.fireDownloadEventByBinding(event.getSourceObject(), "modelBindingResource");
}

@Action
private void downloadByFormElement(ViewEvent<FileDownloadEventMode> event){
    eventRegistry.fireDownloadEvent(event.getSourceForm().getFormElement("fileUploadId1"));
}
                ]]]]>
            </InputText>
        </PanelGroup>

    </PanelGroup>
</Form>