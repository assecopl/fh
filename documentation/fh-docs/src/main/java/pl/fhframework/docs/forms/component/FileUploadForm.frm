<?xml version="1.0" encoding="UTF-8"?>
<Form xmlns="http://fh.asseco.com/form/1.0" container="mainForm" id="documentationHolder" label="{componentName}">
    <AvailabilityConfiguration>
        <ReadOnly>fileUploadExampleCode1,fileUploadExampleCode2,fileUploadExampleCode3,fileUploadExampleCode4,fileUploadExampleCode5</ReadOnly>
        </AvailabilityConfiguration>
   <OutputLabel width="md-12" value="{description}" id="_Form_OutputLabel"/>
    <Spacer width="md-12" height="25px" id="_Form_Spacer"/>
    <TabContainer id="_Form_TabContainer">
          <Tab label="{$.fh.docs.component.examples}" id="_Form_TabContainer_Tab1">
            <PanelGroup width="md-12" label="{$.fh.docs.component.fileupload_example}" id="_Form_TabContainer_Tab1_PanelGroup1">
                <OutputLabel width="md-12" value="{$.fh.docs.component.fileupload_fileupload_component_must_have_binding_type_of}: [i]org.springframework.core.io.Resource[/i] {$.fh.docs.component.fileupload_or} [i]pl.fhframework.io.TemporaryResource[/i]." id="_Form_TabContainer_Tab1_PanelGroup1_OutputLabel1"/>
                <OutputLabel width="md-12" value="{$.fh.docs.component.fileupload_please_note_that_resource}" id="_Form_TabContainer_Tab1_PanelGroup1_OutputLabel2"/>
                <FileUpload width="md-3" file="{data}" label="{$.fh.docs.component.fileupload_upload_file}" onUpload="-" maxSize="3145728" id="_Form_TabContainer_Tab1_PanelGroup1_FileUpload"/>
                <OutputLabel width="md-12" value="File name is: {data.filename}" id="_Form_TabContainer_Tab1_PanelGroup1_OutputLabel3"/>

                <InputText width="md-12" id="fileUploadExampleCode1" rowsCount="5">
                    <![CDATA[
<FileUpload files="\{data\}" label="{uploadLabel}" onUpload="-"
            maxSize="3145728"/>
<OutputLabel value="File name is: \{data.filename\}"/>
                        ]]>
                </InputText>
            </PanelGroup>
              <PanelGroup width="md-12" label="{$.fh.docs.component.fileupload_multiple_example}" id="_Form_TabContainer_Tab1_PanelGroup2">
                  <OutputLabel width="md-12" value="{$.fh.docs.component.fileupload_fileupload_multiple_component_must_have_binding_type_of}: [i]org.springframework.core.io.Resource[/i] {$.fh.docs.component.fileupload_or} [i]pl.fhframework.io.TemporaryResource[/i]." id="_Form_TabContainer_Tab1_PanelGroup2_OutputLabel1"/>
                  <OutputLabel width="md-12" value="{$.fh.docs.component.fileupload_please_note_that_resource}" id="_Form_TabContainer_Tab1_PanelGroup2_OutputLabel2"/>
                  <FileUpload width="md-3" multiple="true" files="{datas}" label="{$.fh.docs.component.fileupload_upload_files}" onUpload="-" maxSize="3145728" id="_Form_TabContainer_Tab1_PanelGroup2_FileUpload"/>
                  <Repeater collection="{datas}" iterator="data" id="_Form_TabContainer_Tab1_PanelGroup2_Repeater">
                      <OutputLabel width="md-12" value="File name is: {data.filename}" id="_Form_TabContainer_Tab1_PanelGroup2_Repeater_OutputLabel"/>
                  </Repeater>

                  <InputText width="md-12" id="fileUploadExampleCode5" rowsCount="5">
                      <![CDATA[
<FileUpload multiple="true" files="\{datas\}" label="\{uploadLabel\}" onUpload="-" maxSize="3145728"/>
<Repeater collection="\{datas\}" iterator="data">
    <OutputLabel value="File name is: \{data.filename\}"/>
</Repeater>]]>
                  </InputText>
              </PanelGroup>

            <PanelGroup width="md-12" label="{$.fh.docs.component.fileupload_example_with_hidden_file_name}" id="_Form_TabContainer_Tab1_PanelGroup3">
                <FileUpload width="md-3" file="{data}" label="{$.fh.docs.component.fileupload_upload_file}" onUpload="-" maxSize="3145728" labelHidden="true" id="_Form_TabContainer_Tab1_PanelGroup3_FileUpload"/>

                <InputText width="md-12" id="fileUploadExampleCode4" rowsCount="5">
                    <![CDATA[
<FileUpload file="\{data\}" label="{uploadLabel}" onUpload="-"
            maxSize="3145728" labelHidden="true"/>
                        ]]>
                </InputText>
            </PanelGroup>

            <PanelGroup width="md-12" label="{$.fh.docs.component.fileupload_example_with_extensions_file}" id="_Form_TabContainer_Tab1_PanelGroup4">
                <OutputLabel width="md-12" value="{$.fh.docs.component.fileupload_in_below_example_you_can_upload_only_files_with_xls_and_.xlsx_extensions}" id="_Form_TabContainer_Tab1_PanelGroup4_OutputLabel1"/>
                <FileUpload width="md-3" extensions=".txt,.xls" file="{resourceWithExt}" label="{$.fh.docs.component.fileupload_upload_file}" onUpload="-" maxSize="3145728" id="_Form_TabContainer_Tab1_PanelGroup4_FileUpload"/>
                <OutputLabel width="md-12" value="File name is: {resourceWithExt.filename}" id="_Form_TabContainer_Tab1_PanelGroup4_OutputLabel2"/>

                <InputText width="md-12" id="fileUploadExampleCode3" rowsCount="5">
                    <![CDATA[
<FileUpload extensions=".xls,.xlsx" file="\{resourceWithExt\}" label="{uploadLabel}" onUpload="-"
                            maxSize="3145728"/>
<OutputLabel value="File name is: \{resourceWithExt.filename\}"/>
                        ]]>
                </InputText>
            </PanelGroup>

            <PanelGroup id="fileUploadValidatedGroup" width="md-12" label="{$.fh.docs.component.fileupload_with_required_attribute}">
                <OutputLabel width="md-12" value="{$.fh.docs.component.fileupload_required_label}" id="_Form_TabContainer_Tab1_PanelGroup5_OutputLabel1"/>
                <ValidateMessages id="val1" componentIds="fileUploadValidatedGroup" level="error"/>

                <InputText width="md-12" required="true" value="{exampleText}" onChange="+" id="_Form_TabContainer_Tab1_PanelGroup5_InputText1"/>
                <FileUpload width="md-3" required="true" file="{requiredData}" label="{$.fh.docs.component.fileupload_upload_file}" onUpload="+" maxSize="3145728" id="_Form_TabContainer_Tab1_PanelGroup5_FileUpload"/>
                <OutputLabel width="md-12" value="File name is: {requiredData.filename}" id="_Form_TabContainer_Tab1_PanelGroup5_OutputLabel2"/>

                <InputText width="md-12" id="fileUploadExampleCode2" rowsCount="5">
                    <![CDATA[
<ValidateMessages id="val1" componentIds="fileUploadValidatedGroup" level="error"/>

<InputText required="true" value="\{exampleText\}" onChange="+" />
<FileUpload required="true" file="\{requiredData\}" label="You have to upload file here" onUpload="+" maxSize="3145728"/>
<OutputLabel value="File name is: \{requiredData.name\}"/>

                        ]]>
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