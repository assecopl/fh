<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<Form xmlns="http://fh.asseco.com/form/1.0" container="mainForm" formType="STANDARD">
    <PanelHeaderFhDP title="Lookup fields" onClick="close" width="md-12" />


    <DictionaryComboFhDP id="dcCountry"
                         emptyValue="true"
                         width="md-4"
                         label="Country"
                         required="true"
                         onChange="-"
                         hint="test hint" hintType="STATIC"
                         dispalyOnlyCode="false"
                         value="{countryCode}"
                         provider="pl.fhframework.fhdp.example.lookup.provider.CountryLookupProvider"
                         icon="fas fa-search" />

</Form>
