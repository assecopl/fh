<?xml version="1.0" encoding="UTF-8"?>
<Form xmlns="http://fh.asseco.com/form/1.0" id="Address" container="mainForm" label="Template">
    <Group id="_Form_Group1">
        <SelectOneMenu id="country" width="md-6" label="{$.fh.docs.component.address_select_country}" values="{countries}" value="{country}" onChange="onSelectCountry(this)"/>
        <SelectOneMenu id="province" width="md-6" label="{$.fh.docs.component.address_select_province}" values="{provincesByCountry}" value="{province}" onChange="onSelectProvince(this)"/>
        <OutputLabel id="shippingInfo" width="md-6" value="{shippingInfo}" horizontalAlign="center"/>
        <OutputLabel id="giftInfo" width="md-6" value="{giftInfo}" horizontalAlign="center"/>
    </Group>
    <Group id="_Form_Group2">
        <InputText id="city" width="md-6" label="{$.fh.docs.component.address_city}" value="{city}"/>
    </Group>
    <Group id="_Form_Group3">
        <InputText id="streetNum" width="md-6" label="{$.fh.docs.component.address_street}" value="{streetNum}"/>
    </Group>
    <Button id="okBtn" label="{$.fh.docs.service.save}" onClick="onAddressSave()"/>
</Form>