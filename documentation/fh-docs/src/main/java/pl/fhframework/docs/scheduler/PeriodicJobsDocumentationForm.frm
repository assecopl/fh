<?xml version="1.0" encoding="UTF-8"?>
<Form xmlns="http://fh.asseco.com/form/1.0" id="PeriodicJobsDocumentationForm" label="{$.fh.docs.periodic_title}" container="mainForm" formType="STANDARD">
    <AvailabilityConfiguration>
        <Preview>listing1</Preview>
    </AvailabilityConfiguration>
    <OutputLabel width="md-12" value="{$.fh.docs.periodic_info}" id="_Form_OutputLabel"/>
    <InputText id="listing1" width="md-12" rowsCount="13">
<![CDATA[![ESCAPE[
@Component
public class MyScheduledService implements IPeriodicJob {

    @Override
    public void executePeriodicaly() throws Exception {
        // LOGIC every 10 seconds
    }

    @Override
    public String getCronExpression() {
        return "0/10 * * * * ?";
    }
}]]]]>
    </InputText>
</Form>