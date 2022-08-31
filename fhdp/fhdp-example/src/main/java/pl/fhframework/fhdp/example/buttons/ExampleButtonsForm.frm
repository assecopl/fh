<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<Form xmlns="http://fh.asseco.com/form/1.0" container="mainForm" formType="STANDARD" modalSize="REGULAR">
    <PanelHeaderFhDP title="Buttons" info="Example" onClick="close" width="md-12" />
    <Row>
        <Button  id="buttonCode6_1" width="md-4" label="Button without defined style" onClick="testError"/>

        <Button id="buttonCode6_2" width="md-2" label="Default" style="default" onClick="xxx"/>
    </Row>
    <Row width="md-6">
        <Button id="buttonCode6_3" width="md-3" label="Primary" style="primary" onClick="test"/>
        <Button id="buttonCode6_4" width="md-3" label="Success" style="success" onClick="test"/>
    </Row>
    <Row width="md-6">
        <Button id="buttonCode6_5" width="md-3"  label="Info" style="info" onClick="test"/>
        <Button id="buttonCode6_6" width="md-3" label="Warning" style="warning" onClick="test"/>
    </Row>
    <Row width="md-6">
        <Button id="buttonCode6_8" width="md-3" label="Buuton with recaptcha" reCAPTCHA="true"  onClick="test"/>
    </Row>
    <Button id="buttonCode6_7" label="Danger" style="danger" onClick="test"/>
    <PanelGroup label="TimerFhDP">
        <TimerFhDP timeout="2000" onInterval="handleInterval()"/>
        <OutputLabel width="md-12" value="This is interval counter: {getInterText()}"/>
    </PanelGroup>
</Form>
