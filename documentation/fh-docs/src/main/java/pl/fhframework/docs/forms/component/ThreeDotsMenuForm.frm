<?xml version="1.0" encoding="UTF-8"?>
<Form xmlns="http://fh.asseco.com/form/1.0" container="mainForm" id="documentationHolder" label="{componentName}">
    <Table width="md-12" iterator="wiersz" collection="{RULE.pl.fhframework.core.rules.builtin.CsvRows.csvRows('Pracownicy Działu Marketingu|Płace|Kierownicy|Sprzedaż|Magazyn')}" minRows="3" id="_Form_Table">
        <Column label="Nazwa grupy" value="{wiersz.column1}" id="_Form_Table_Column1"/>
        <Column label="Test" id="_Form_Table_Column2">
            <ThreeDotsMenu width="md-1" id="_Form_Table_Column2_ThreeDotsMenu">
                <ThreeDotsMenuItem value="Test 1" id="_Form_Table_Column2_ThreeDotsMenu_ThreeDotsMenuItem1"/>
                <ThreeDotsMenuItem value="Test 2" id="_Form_Table_Column2_ThreeDotsMenu_ThreeDotsMenuItem2"/>
                <ThreeDotsMenuItem value="Test 3" id="_Form_Table_Column2_ThreeDotsMenu_ThreeDotsMenuItem3"/>
            </ThreeDotsMenu>
        </Column>
    </Table>

    <OutputLabel width="md-12" value="Przykład:" id="_Form_OutputLabel"/>
    <InputText width="md-12" availability="VIEW" value="" rowsCount="5" id="_Form_InputText">
        <![CDATA[![ESCAPE[<Table width="md-12" iterator="wiersz" collection="{RULE.pl.fhframework.core.rules.builtin.CsvRows.csvRows('Pracownicy Działu Marketingu|Płace|Kierownicy|Sprzedaż|Magazyn')}" minRows="3">
        <Column label="Nazwa grupy" value="{wiersz.column1}"/>
        <Column label="Test">
            <ThreeDotsMenu>
                <ThreeDotsMenuItem value="Test 1" />
                <ThreeDotsMenuItem value="Test 2" />
                <ThreeDotsMenuItem value="Test 3" />
            </ThreeDotsMenu>
        </Column>
    </Table>
        ]]]]>
    </InputText>
    <Button id="pBack" label="[icon='fa fa-chevron-left'] {$.fh.docs.component.back}" onClick="backToFormComponentsList()"/>
</Form>