<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<Form xmlns="http://fh.asseco.com/form/1.0"  container="mainForm"  hideHeader="true" formType="MODAL" modalSize="XLARGE">
    <PanelHeaderFhDP title="Checkbox in list" info="Verify" onClick="close" width="md-12" />
    <PanelGroup width="md-12" label="Test panel group label 1" >
        <Row elementsHorizontalAlign="CENTER">
            <Table id="exampleTable" width="md-10"
                   iterator="row"
                   collection="{elements}"
                   minRows="1" label="Example table with checkBox">
                <Column width="90" label="Text" value="{row.element.text}"/>
                <Column width="10" label="Checked" value="{row.element.checked}"
                        formatter="booleanColumnFormatter"
                        horizontalAlign="center"/>
            </Table>
        </Row>
    </PanelGroup>
    <PanelGroup width="md-12" label="[className='header-light'][color='red']Test panel group label 2 - modified[/color][/className]" >
        <OutputLabel width="md-12" value="Test output label in panel"/>

    </PanelGroup>

    <Model externalClass="pl.fhframework.fhdp.example.list.checkbox.CheckBoxOnTheListModel"/>
</Form>