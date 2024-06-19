<?xml version="1.0" encoding="UTF-8"?>
<Form xmlns="http://fh.asseco.com/form/1.0" id="RemoteEventsDemoForm" label="Remote events demo" container="mainForm" formType="STANDARD">
    <Row>
        <InputText width="md-12" availability="VIEW" rowsCount="1" id="_Form_Row1_InputText">3 remote events are available: 'age', 'name', 'any'</InputText>
    </Row>
    <Row>
        <InputText width="md-12" availability="VIEW" rowsCount="3" id="_Form_Row2_InputText">Event 'age' is default and accept Integer param 'age'. Close Tab browser and in new Tab paste appropriate link, e.g.:
            http://localhost:8090/?age=50
            http://localhost:8090/?remoteEvent=age&amp;age=39
        </InputText>
    </Row>
    <Row>
        <InputText width="md-12" availability="VIEW" rowsCount="2" id="_Form_Row3_InputText">Event 'name' accept String param 'name'. Close Tab browser and in new Tab paste appropriate link, e.g.:
            http://localhost:8090/?remoteEvent=name&amp;name=Ala
        </InputText>
    </Row>
    <Row>
        <InputText width="md-12" availability="VIEW" rowsCount="2" id="_Form_Row4_InputText">Event 'any' accept any parameter. Close Tab browser and in new Tab paste appropriate link, e.g.:
            http://localhost:8090/?remoteEvent=any&amp;param_1=2.0&amp;param_2=tak
        </InputText>
    </Row>
    <Row>
        <Button label="Exit" onClick="onExit" id="_Form_Row5_Button"/>
    </Row>
</Form>