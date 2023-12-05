<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<Form xmlns="http://fh.asseco.com/form/fhdp/1.0" container="mainForm" formType="STANDARD">
    <PanelHeaderFhDP title="Lookup fields" onClick="close" width="md-12"/>

    <Row>
<!--        <OutputLabel value="Selected country code: {countryEnum.englishName}"/>-->
        <OutputLabel value="Selected country code: {countryCode}"/>
    </Row>

    <Row>
        <InputText value="{regionFilter}" label="Filtr regionu"/>
    </Row>

    <Row>
        <DictionaryLookupFhDP id="dcCountry1"
                              emptyValue="true"
                              width="md-4"
                              label="Country"
                              required="true"
                              onChange="-"
                              hint="test hint" hintType="STATIC"
                              dispalyOnlyCode="false"
                              displayOnlyCode="true"
                              value="{countryCode}"
                              provider="pl.fhframework.fhdp.example.lookup.provider.CountryLookupProvider"
                              icon="fas fa-search" >
            <DictionaryComboParameterFhDP name="codeListId" value="356COM"/>
<!--            <DictionaryComboParameterFhDP name="referenceDate" value="{referenceDate}"/>-->
            <DictionaryComboParameterFhDP name="regionFilter" value="{regionFilter}"/>
        </DictionaryLookupFhDP>

        <DictionaryComboFhDP id="dcCountry2"
                              emptyValue="true"
                              width="md-4"
                              label="Country"
                              required="true"
                              onChange="-"
                              hint="test hint" hintType="STATIC"
                             displayOnlyCode="true"
                              value="{countryCode}"
                              provider="pl.fhframework.fhdp.example.lookup.provider.CountryLookupProvider"
                              icon="fas fa-search" >
            <DictionaryComboParameterFhDP name="codeListId" value="356COM"/>
            <!--            <DictionaryComboParameterFhDP name="referenceDate" value="{referenceDate}"/>-->
            <DictionaryComboParameterFhDP name="regionFilter" value="{regionFilter}"/>
        </DictionaryComboFhDP>
    </Row>



<!--    <Row>-->
<!--        <DictionaryLookupFhDP id="dcCountry2"-->
<!--                              emptyValue="true"-->
<!--                              width="md-4"-->
<!--                              label="Country"-->
<!--                              required="true"-->
<!--                              onChange="-"-->
<!--                              hint="test hint" hintType="STATIC"-->
<!--                              dispalyOnlyCode="false"-->
<!--                              value="{countryCode}"-->
<!--                              provider="pl.fhframework.fhdp.example.lookup.provider.CountryLookupProvider"-->
<!--                              icon="fas fa-search" >-->
<!--            <DictionaryComboParameterFhDP name="codeListId" value="356COM"/>-->
<!--            &lt;!&ndash;            <DictionaryComboParameterFhDP name="referenceDate" value="{referenceDate}"/>&ndash;&gt;-->
<!--            <DictionaryComboParameterFhDP name="regionFilter" value="{regionFilter}"/>-->
<!--        </DictionaryLookupFhDP>-->

<!--    </Row>-->

    <Row>
        <Button label="France" onClick="setCountry('FR')"/>
        <Button label="Poland" onClick="setCountry('PL')"/>
        <Button label="Germany" onClick="setCountry('DE')"/>
        <Button label="Close" onClick="close()"/>
    </Row>

<!--    <Row>-->
<!--        <DictionaryLookupFhDP id="dcCountry2"-->
<!--                              emptyValue="true"-->
<!--                              width="md-4"-->
<!--                              label="Country"-->
<!--                              required="true"-->
<!--                              onChange="-"-->
<!--                              hint="test hint" hintType="STATIC"-->
<!--                              dispalyOnlyCode="false"-->
<!--                              value="{countryCode}"-->
<!--                              provider="pl.fhframework.fhdp.example.lookup.provider.CountryNameValueLookupProvider"-->
<!--                              icon="fas fa-search" />-->

<!--    </Row>-->


    <!--        <DictionaryComboFhDP id="dcCountryNameValue"-->
    <!--                             emptyValue="true"-->
    <!--                             width="md-4"-->
    <!--                             label="Country by NameValueDto"-->
    <!--                             required="true"-->
    <!--                             onChange="-"-->
    <!--                             dispalyOnlyCode="false"-->
    <!--                             value="{countryCodeNameValue}"-->
    <!--                             provider="pl.fhframework.fhdp.example.lookup.provider.CountryNameValueLookupProvider"-->
    <!--                             icon="fas fa-search"/>-->
<!--    <DictionaryCombo-->
<!--            emptyValue="true"-->
<!--            width="md-4"-->
<!--            label="Country with CountryLookupSimpleProvider"-->
<!--            required="true"-->
<!--            onChange="-"-->
<!--            value="{countryCodeNameValue}"-->
<!--            provider="pl.fhframework.fhdp.example.lookup.provider.CountryLookupSimpleProvider"-->
<!--            icon="fas fa-search"/>-->

    <Spacer width="md-12" height="500px"/>
</Form>
