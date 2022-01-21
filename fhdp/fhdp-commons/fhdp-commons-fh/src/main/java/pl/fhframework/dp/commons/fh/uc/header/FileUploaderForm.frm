<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<Form xmlns="http://fh.asseco.com/form/1.0" id="fileUploaderForm" container="mainForm" formType="STANDARD">
    <PanelHeaderFhDP width="md-12" onClick="close" title="{$.fhdp.app.fileUploaderForm.title} {fileName}" info="{$.fhdp.app.fileUploaderForm.subtitle}"/>
    <Spacer width="md-12" height="80px"/>
    <Row width="md-12" elementsHorizontalAlign="CENTER">

        <FileUpload id="fileUploadButton"
                    width="md-3" file="{fileResource}"
                    label="{$.fhdp.document.ct.btn.importFromFile.label}"
                    onUpload="fileUploadMenu" maxSize="15728640" labelHidden="true" extensions=".xml" />
    </Row>
</Form>
