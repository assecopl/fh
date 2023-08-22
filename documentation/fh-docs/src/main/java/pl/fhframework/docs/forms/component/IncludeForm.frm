<?xml version="1.0" encoding="UTF-8"?>
<Form xmlns="http://fh.asseco.com/form/1.0" id="IncludeForm" container="mainForm" label="{componentName}">
    <AvailabilityConfiguration>
        <ReadOnly>includeCode1,includeCode2,includeCode3,includeCode4,includeCode5,includeCode6</ReadOnly>
    </AvailabilityConfiguration>

    <TabContainer id="_Form_TabContainer">
        <Tab label="{$.fh.docs.component.examples}" id="_Form_TabContainer_Tab1">
            <PanelGroup label="{$.fh.docs.component.include_shipping_details_main_form_with_two_templates}" id="_Form_TabContainer_Tab1_PanelGroup">
                <PanelGroup label="{$.fh.docs.component.include_shipping_adress_template_usage}" id="_Form_TabContainer_Tab1_PanelGroup_PanelGroup1">
                    <Include id="addressShippingForm" ref="AddressCompositeForm" model="shippingModel">
                        <OnEvent name="onSave" handler="onSave"/> <!--handler opcjonalnie-->
                    </Include>
                </PanelGroup>
                <Spacer width="md-12" id="_Form_TabContainer_Tab1_PanelGroup_Spacer"/>
                <PanelGroup label="{$.fh.docs.component.include_invoice_address_template_usage}" id="_Form_TabContainer_Tab1_PanelGroup_PanelGroup2">
                    <Include id="addressInvoiceForm" ref="AddressCompositeForm" model="invoiceModel"/>
                </PanelGroup>
            </PanelGroup>
        </Tab>
        <Tab label="{$.fh.docs.component.include_example_by_code}" id="_Form_TabContainer_Tab2">
            <!--<OutputLabel value="&#160;"/>
            <OutputLabel width="md-4" value="&#160;"/>
            <OutputLabel width="md-4" value="To be written"/>
            <OutputLabel width="md-4" value="&#160;"/>
            <OutputLabel value="&#160;"/>-->

            <InputText id="includeCode5" label="{$.fh.docs.component.include_preface}" width="md-12" rowsCount="8" value="Template consists of xml view part, model and form. Model is referenced within template. Form being composite is joined with its template using @Composite annotation.  Template is imported into main form xml template using Include tag. Properties required:  ref - reference to composite form with @Composite annotation. model - name of field referencing model template instance"/>

            <InputText id="includeCode1" label="{$.fh.docs.component.include_template_model_addressmodel}" width="md-12" rowsCount="30">
                <![CDATA[
    public class AddressModel \{

    @Getter @Setter
    private String city;

    @Getter @Setter
    private String country;

    @Getter @Setter
    private String province;

    @Getter @Setter
    private String streetNum;

    @Getter @Setter
    private String shippingInfo;

    @Getter @Setter
    private String giftInfo;

    @Getter
    private List<String> countries = Arrays.asList("-", "Poland", "Germany", "Spain" ,"France");

    @Getter
    private Map<String, List<String>> provinces = createProvincies();

    private static Map<String, List<String>> createProvincies()\{

    \}
}
                ]]>
            </InputText>

            <InputText id="includeCode2" label="{$.fh.docs.component.include_composite_definition_addresscompositeform}" width="md-12" rowsCount="4">
                <![CDATA[
@Composite(template = "Address.xml")
public class AddressCompositeForm extends CompositeForm<AddressModel> \{

\}
                        ]]>
            </InputText>

            <InputText id="includeCode3" label="{$.fh.docs.component.include_template_address_xml}" width="md-12" rowsCount="26">
                <![CDATA[
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<Form id="addressOrderForm" container="mainForm" label="Template" layout="vertical" xmlns="http://fh.asseco.com/form/1.0">
    <Group>
        <SelectOneMenu id="country" width="md-3"
                       label="Select country"
                       values="\{countries\}"
                       value="\{country\}"
                       onChange="onSelectCountry(this)"
                />
        <SelectOneMenu id="province"
                       width="md-4"
                       label="Select province"
                       values="\{provincesByCountry\}"
                       value="\{province\}"
                       onChange="onSelectProvince(this)"/>
        <OutputLabel width="md-3" value="&#160;"/>
        <OutputLabel id="shippingInfo" width="md-4" value="\{shippingInfo\}"/>
        <OutputLabel id="giftInfo" width="md-4" value="\{giftInfo\}"/>
    </Group>
    <Group>
        <InputText id="city" width="md-5" label="City" value="\{city\}"/>
    </Group>
    <Group>
        <InputText id="streetNum" width="md-5" label="Street" value="\{streetNum\}"/>
    </Group>
    <Button id="okBtn" label="Save" onClick="onAddressSave(this)"/>
</Form>
                        ]]>
            </InputText>

            <InputText id="includeCode4" label="{$.fh.docs.component.include_template_usage}" width="md-12" rowsCount="10">
                <![CDATA[
<PanelGroup label="Shipping details (main form with two templates)">
    <PanelGroup label="Shipping address (template usage)">
        <Include id="addressShippingForm" ref="AddressCompositeForm" model="shippingModel">
            <OnEvent name="onSave" handler="onSave"/> <!--handler optional-->
        </Include>
    </PanelGroup>
    <Spacer/>
    <PanelGroup label="Invoice address (template usage)">
        <Include id="addressInvoiceForm" ref="AddressCompositeForm" model="invoiceModel"/>
    </PanelGroup>
</PanelGroup>
                        ]]>
            </InputText>

            <InputText id="includeCode6" label="{$.fh.docs.component.include_template_model_creation_and_overriding_some_default_logic}" width="md-12" rowsCount="12" value="public class ShippingDetailsData \{   private AddressModel shippingModel = new AddressModel();    private AddressModel invoiceModel = new AddressModel()\{   @Override   public List&lt;String&gt; getCountries() \{    List&lt;String&gt; countries = super.getCountries();    return countries.stream().map((s)-&gt;s.toUpperCase()).collect(Collectors.toList());   \}  }; }"/>

        </Tab>
        <Tab label="{$.fh.docs.component.attributes}" id="_Form_TabContainer_Tab3">
            <Table iterator="item" collection="{attributes}" id="_Form_TabContainer_Tab3_Table">
                <Column label="{$.fh.docs.component.attributes_identifier}" value="{item.name}" width="15" id="_Form_TabContainer_Tab3_Table_Column1"/>
                <Column label="{$.fh.docs.component.attributes_type}" value="{item.type}" width="15" id="_Form_TabContainer_Tab3_Table_Column2"/>
                <Column label="{$.fh.docs.component.attributes_boundable}" value="{item.boundable}" width="10" id="_Form_TabContainer_Tab3_Table_Column3"/>
                <Column label="{$.fh.docs.component.attributes_default_value}" value="{item.defaultValue}" width="20" id="_Form_TabContainer_Tab3_Table_Column4"/>
                <Column label="{$.fh.docs.component.attributes_description}" value="{item.description}" width="40" id="_Form_TabContainer_Tab3_Table_Column5"/>
            </Table>
        </Tab>
    </TabContainer>

    <Button id="pBack" label="[icon='fa fa-chevron-left'] {$.fh.docs.component.back}" onClick="backToFormComponentsList()"/>
</Form>