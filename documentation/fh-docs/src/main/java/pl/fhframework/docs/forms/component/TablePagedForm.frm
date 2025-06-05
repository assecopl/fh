<?xml version="1.0" encoding="UTF-8"?>
<Form xmlns="http://fh.asseco.com/form/1.0" container="mainForm" id="documentationHolder" label="{componentName}">
    <AvailabilityConfiguration>
        <ReadOnly>tablePagedExampleCode1,tablePagedExampleCode2,tablePagedExampleCode3,tablePagedExampleJavaCode,tablePagedExampleJavaCode2,
            tablePagedExampleCode4,tablePagedExampleJavaCode3,tablePagedExampleCode5_1,tablePagedExampleCode5_2,table_inlineDataCode</ReadOnly>
    </AvailabilityConfiguration>
    <OutputLabel width="md-12" value="{description}" id="_Form_OutputLabel"/>
    <Spacer width="md-12" height="25px" id="_Form_Spacer"/>
    <TabContainer id="_Form_TabContainer">
        <Tab label="{$.fh.docs.component.examples}" id="_Form_TabContainer_Tab1">
                <TabContainer id="_Form_TabContainer_Tab1_TabContainer">
                    <Tab label="{$.fh.docs.component.tablepaged_paged_data}" id="_Form_TabContainer_Tab1_TabContainer_Tab1">
                        <PanelGroup width="md-12" label="{$.fh.docs.component.tablepaged_table_with_simple_usage}" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup">
                            <TablePaged id="tablePage_CompanyEmployeesReadOnly" height="400px" fixedHeader="true" iterator="person" collection="{pagedPeopleForRead}" onRowClick="-" minRows="7" pageSize="2">
                                <ColumnPaged id="el_wnioski_col_1" width="60" label="Rok" value="{person.name}"/>
                                <ColumnPaged id="el_wnioski_col_2" width="110" label="Nr wniosku" value="{person.surname}"/>
                                <ColumnPaged id="el_wnioski_col_3" width="110" label="PESEL" value="{person.city}"/>
                                <ColumnPaged id="el_wnioski_col_4" width="150" label="Nazwisko" value="{person.gender}"/>
                                <ColumnPaged id="el_wnioski_col_5" width="150" label="Imię" value="{person.status}"/>
                                <ColumnPaged id="el_wnioski_col_6" width="150" label="Oddział" value="{person.status}"/>
                                <ColumnPaged id="el_wnioski_col_7" width="120" label="Placówka" value="{person.status}"/>
                                <ColumnPaged id="el_wnioski_col_8" width="137" label="Rodzaj wniosku" value="{person.status}"/>
                                <ColumnPaged id="el_wnioski_col_9" width="130" label="Status wniosku" value="{person.status}"/>
                            </TablePaged>
                            <InputText id="tablePagedExampleCode1" label="{$.fh.docs.component.code}" width="md-12" height="230" rowsCount="7">
<![CDATA[![ESCAPE[<TablePaged id="tablePage_CompanyEmployeesReadOnly" iterator="person" collection="{pagedPeopleForRead}" onRowClick="-" pageSize="10">
    <ColumnPaged label="Name" value="{person.name}"/>
    <ColumnPaged label="Surname" value="{person.surname}"/>
    <ColumnPaged label="City" value="{person.city}"/>
    <ColumnPaged label="Gender" value="{person.gender}"/>
    <ColumnPaged label="Status" value="{person.status}"/>
</TablePaged>]]]]>
                            </InputText>
                        </PanelGroup>
                    </Tab>

                    <Tab label="{$.fh.docs.component.tablepaged_paged_sortable_data}" id="_Form_TabContainer_Tab1_TabContainer_Tab2">
                        <PanelGroup width="md-12" label="{$.fh.docs.component.tablepaged_table_with_nested_and_binded_form_components_columns_lambda_for_sorting}" id="_Form_TabContainer_Tab1_TabContainer_Tab2_PanelGroup">
                            <TablePaged id="tablePage_CompanyEmployeesEditable" iterator="person" collection="{pagedPeople}" onRowClick="-" selected="{selectedPersonPage}" pageSize="5">
                                <ColumnPaged label="{companyEmployeesEditableTableName}" value="{person.name}" sortBy="PersonName" id="_Form_TabContainer_Tab1_TabContainer_Tab2_PanelGroup_TablePaged_ColumnPaged1"/>
                                <ColumnPaged label="{companyEmployeesEditableTableSurname}" value="{person.surname}" sortBy="PersonSurname" id="_Form_TabContainer_Tab1_TabContainer_Tab2_PanelGroup_TablePaged_ColumnPaged2"/>
                                <ColumnPaged label="{companyEmployeesEditableTableCity}" value="{person.city}" sortBy="PersonCity" id="_Form_TabContainer_Tab1_TabContainer_Tab2_PanelGroup_TablePaged_ColumnPaged3"/>
                                <ColumnPaged label="{companyEmployeesEditableTableGender}" value="{person.gender}" sortBy="PersonGender" id="_Form_TabContainer_Tab1_TabContainer_Tab2_PanelGroup_TablePaged_ColumnPaged4"/>
                                <ColumnPaged label="{companyEmployeesEditableTableStatus}" value="{person.status}" sortBy="PersonStatus" id="_Form_TabContainer_Tab1_TabContainer_Tab2_PanelGroup_TablePaged_ColumnPaged5"/>
                                <ColumnPaged label="{companyEmployeesEditableTableCitizenship}" value="{person.citizenship}" sortBy="PersonCitizenship" id="_Form_TabContainer_Tab1_TabContainer_Tab2_PanelGroup_TablePaged_ColumnPaged6"/>
                                <ColumnPaged label="{companyEmployeesEditableTableName} + {companyEmployeesEditableTableSurname}" sortBy="PersonNameAndSurname" id="_Form_TabContainer_Tab1_TabContainer_Tab2_PanelGroup_TablePaged_ColumnPaged7">
                                    <OutputLabel width="md-12" value="{person.name}" id="_Form_TabContainer_Tab1_TabContainer_Tab2_PanelGroup_TablePaged_ColumnPaged7_OutputLabel1"/>
                                    <OutputLabel width="md-12" value="{person.surname}" id="_Form_TabContainer_Tab1_TabContainer_Tab2_PanelGroup_TablePaged_ColumnPaged7_OutputLabel2"/>
                                </ColumnPaged>
                            </TablePaged>
                            <!--<Row layout="horizontal" label=" ">-->
                            <!--<Spacer width="md-9"/>-->
                            <!--<Button id="pAdd" width="md-1" label="Add new" onClick="addNewPersonWindow"/>-->
                            <!--<Button id="pEdit" width="md-1" label="Edit"-->
                            <!--onClick="editPersonWindow(selectedPerson)"/>-->
                            <!--<Button id="pDelete" width="md-1" label="Delete"-->
                            <!--onClick="removePerson(selectedPerson)"/>-->
                            <!--</Row>-->
                            <InputText id="tablePagedExampleCode2" label="{$.fh.docs.component.code}" width="md-12" height="400" rowsCount="12">
<![CDATA[![ESCAPE[<TablePaged id="tablePage_CompanyEmployeesEditable" iterator="person" collection="{pagedPeople}" onRowClick="-" selected="{selectedPersonPage}" pageSize="5">
    <ColumnPaged label="{companyEmployeesEditableTableName}" value="{person.name}" sortBy="PersonName" />
    <ColumnPaged label="{companyEmployeesEditableTableSurname}" value="{person.surname}" sortBy="PersonSurname"/>
    <ColumnPaged label="{companyEmployeesEditableTableCity}" value="{person.city}" sortBy="PersonCity"/>
    <ColumnPaged label="{companyEmployeesEditableTableGender}" value="{person.gender}" sortBy="PersonGender"/>
    <ColumnPaged label="{companyEmployeesEditableTableStatus}" value="{person.status}" sortBy="PersonStatus"/>
    <ColumnPaged label="{companyEmployeesEditableTableCitizenship}" value="{person.citizenship}" sortBy="PersonCitizenship"/>
    <ColumnPaged label="{companyEmployeesEditableTableName} + {companyEmployeesEditableTableSurname}">
    <OutputLabel value="{person.name}"/>
    <OutputLabel value="{person.surname}"/>
    </ColumnPaged>
</TablePaged>]]]]>
                            </InputText>

                            <InputText id="tablePagedExampleJavaCode" label="{$.fh.docs.component.java}" width="md-12" height="500" rowsCount="15"><![CDATA[![ESCAPE[
private enum SortedProperty {
    PersonNameAndSurname((firstPerson, secondPerson) -> (firstPerson.getName() + firstPerson.getSurname()).compareTo(secondPerson.getName() + secondPerson.getSurname())),
    PersonName((firstPerson, secondPerson) -> firstPerson.getName().compareTo(secondPerson.getName())),
    PersonSurname((firstPerson, secondPerson) -> firstPerson.getSurname().compareTo(secondPerson.getSurname())),
    PersonCity((firstPerson, secondPerson) -> firstPerson.getCity().compareTo(secondPerson.getCity())),
    PersonGender((firstPerson, secondPerson) -> firstPerson.getGender().compareTo(secondPerson.getGender())),
    PersonStatus((firstPerson, secondPerson) -> firstPerson.getStatus().compareTo(secondPerson.getStatus())),
    PersonCitizenship((firstPerson, secondPerson) -> firstPerson.getCitizenship().compareTo(secondPerson.getCitizenship()));

    private ComparatorFunction<Person> comparator;

    SortedProperty(ComparatorFunction<Person> comparator) {
        this.comparator = comparator;
    }
}
]]]]>
                            </InputText>
                        </PanelGroup>
                    </Tab>



                    <Tab label="{$.fh.docs.component.tablepaged_paged_data_with_multiselect}" id="_Form_TabContainer_Tab1_TabContainer_Tab3">
                        <PanelGroup width="md-12" label="{$.fh.docs.component.tablepaged_tablepaged_with_sortable_data_and_multiselect}" id="_Form_TabContainer_Tab1_TabContainer_Tab3_PanelGroup">
                            <TablePaged id="countriesTable" iterator="country" collection="{pageModelCountries}" onRowClick="-" selected="{selectedCountries}" pageSize="10" multiselect="true" selectionCheckboxes="true">
                                <ColumnPaged label="{$.fh.docs.component.tablepaged_country_name}" value="{country.name}" sortBy="CountryName" id="_Form_TabContainer_Tab1_TabContainer_Tab3_PanelGroup_TablePaged_ColumnPaged1"/>
                                <ColumnPaged label="{$.fh.docs.component.tablepaged_capital}" value="{country.capital}" sortBy="CountryCapital" id="_Form_TabContainer_Tab1_TabContainer_Tab3_PanelGroup_TablePaged_ColumnPaged2"/>
                                <ColumnPaged label="{$.fh.docs.component.tablepaged_population_mln}" value="{country.population}" sortBy="CountryArea" id="_Form_TabContainer_Tab1_TabContainer_Tab3_PanelGroup_TablePaged_ColumnPaged3"/>
                                <ColumnPaged label="{$.fh.docs.component.tablepaged_area_10_3_km_2}" value="{country.area}" sortBy="CountryPopulation" id="_Form_TabContainer_Tab1_TabContainer_Tab3_PanelGroup_TablePaged_ColumnPaged4"/>
                                <ColumnPaged id="_Form_TabContainer_Tab1_TabContainer_Tab3_PanelGroup_TablePaged_ColumnPaged5">
                                    <Button width="md-12" label="{$.fh.docs.component.tablepaged_remove}" onClick="removeCountry(country)" id="_Form_TabContainer_Tab1_TabContainer_Tab3_PanelGroup_TablePaged_ColumnPaged5_Button"/>
                                </ColumnPaged>
                            </TablePaged>
                            <Button label="{$.fh.docs.component.tablepaged_remove_selected}" onClick="removeCountries(selectedCountries)" id="_Form_TabContainer_Tab1_TabContainer_Tab3_PanelGroup_Button1"/>
                            <Button label="{$.fh.docs.component.tablepaged_reset_countries}" onClick="resetCountriesTable()" id="_Form_TabContainer_Tab1_TabContainer_Tab3_PanelGroup_Button2"/>

                        <InputText id="tablePagedExampleCode5_1" label="{$.fh.docs.component.code}" width="md-12" rowsCount="11">
<![CDATA[![ESCAPE[   <TablePaged id="countriesTable" iterator="country" collection="{pageModelCountries}" onRowClick="-" selected="{selectedCountries}" pageSize="10" multiselect="true" selectionCheckboxes="true">
                           <ColumnPaged label="Name" value="{country.name}" sortBy="CountryName"/>
                           <ColumnPaged label="Capital" value="{country.capital}"  sortBy="CountryCapital"/>
                           <ColumnPaged label="Population [mln]" value="{country.population}" sortBy="CountryArea"/>
                           <ColumnPaged label="Area [10^3 * km^2]" value="{country.area}" sortBy="CountryPopulation"/>
                           <ColumnPaged>
                               <Button label="Remove" onClick="removeCountry(country)" />
                           </ColumnPaged>
                     </TablePaged>
                        <Button label="Remove selected" onClick="removeCountries(selectedCountries)" />
                        <Button label="Reset Countries" onClick="resetCountriesTable()"/>]]]]>
                        </InputText>

                        <InputText id="tablePagedExampleCode5_2" label="{$.fh.docs.component.java}" width="md-12" rowsCount="15" height="600">
                            <![CDATA[![ESCAPE[  private enum SortedProperty {

        CountryName((firstCountry, secondCountry) -> firstCountry.getName().compareTo(secondCountry.getName())),
        CountryCapital((firstCountry, secondCountry) -> firstCountry.getCapital().compareTo(secondCountry.getCapital())),
        CountryArea((firstCountry, secondCountry) -> Double.compare(firstCountry.getArea(), secondCountry.getArea())),
        CountryPopulation((firstCountry, secondCountry) -> Double.compare(firstCountry.getPopulation(), secondCountry.getPopulation()));

        private ComparatorFunction<Country> comparator;

        SortedProperty(ComparatorFunction<Country> comparator) {
            this.comparator = comparator;
        }
    }


//Actions defined in UseCase
    @Override
    public void start() {
        model = new TablePagedElement();
        // independent page models for each table
        model.setPagedPeople(new PageModel<>(personService::findAllPeople));
        model.setPagedPeopleForRead(new PageModel<>(personService::findAllPeople));
        model.setPagedPeopleMergedColumns(new PageModel<>(personService::findAllPeople));
        model.setPagedPeopleColoredRows(new PageModel<>(personService::findAllPeople));
        model.setPageModelCountries(new PageModel<>(countryService::createPage));
        showForm(TablePagedForm.class, model);
    }

    /**
     * Removes country from pageModel
     *
     * @param pageModel - pageModel to refresh
     * @param country   - country to remove
     */
    @Action
    public void removeCountry(CountryService.Country country) {
        countryService.remove(country);
        model.getPageModelCountries().refreshNeeded();
    }

    /**
     * Removes countries from pageModel
     *
     * @param pageModel - pageModel to refresh
     * @param countries - countries to remove
     */
    @Action
    public void removeCountries(List<CountryService.Country> countries) {
        countryService.removeAll(countries);
        countries.clear(); // clear selection
        model.getPageModelCountries().refreshNeeded();
    }

    /**
     * Resets countriesTable
     */
    @Action
    public void resetCountriesTable() {
        countryService.resetCountries();
        model.getPageModelCountries().refreshNeeded();
    }
]]]]>
                        </InputText>
                        </PanelGroup>
                    </Tab>

                    <Tab label="{$.fh.docs.component.tablepaged_paged_sortable_data_with_merged_columns}" id="_Form_TabContainer_Tab1_TabContainer_Tab4">
                        <PanelGroup width="md-12" label="{$.fh.docs.component.tablepaged_tablepaged_with_nested_and_binded_form_components_columns_property_or_lambda_for_sorting}" id="_Form_TabContainer_Tab1_TabContainer_Tab4_PanelGroup">
                            <TablePaged id="tablePage_CompanyEmployeesEditableNested" iterator="person" collection="{pagedPeopleMergedColumns}" onRowClick="-" selected="{selectedPersonPageMergedColumns}" pageSize="5">
                                <ColumnPaged label="{$.fh.docs.component.tablepaged_no}" value="{person$rowNo}" id="_Form_TabContainer_Tab1_TabContainer_Tab4_PanelGroup_TablePaged_ColumnPaged1"/>
                                <ColumnPaged label="{companyEmployeesEditableTablePersonalData}" id="_Form_TabContainer_Tab1_TabContainer_Tab4_PanelGroup_TablePaged_ColumnPaged2">
                                    <ColumnPaged label="{companyEmployeesEditableTableNameAndSurname}" id="_Form_TabContainer_Tab1_TabContainer_Tab4_PanelGroup_TablePaged_ColumnPaged2_ColumnPaged1">
                                        <ColumnPaged label="{companyEmployeesEditableTableName}" value="{person.name}" sortBy="PersonName" id="_Form_TabContainer_Tab1_TabContainer_Tab4_PanelGroup_TablePaged_ColumnPaged2_ColumnPaged1_ColumnPaged1"/>
                                        <ColumnPaged label="{companyEmployeesEditableTableSurname}" value="{person.surname}" sortBy="PersonSurname" id="_Form_TabContainer_Tab1_TabContainer_Tab4_PanelGroup_TablePaged_ColumnPaged2_ColumnPaged1_ColumnPaged2"/>
                                    </ColumnPaged>
                                    <ColumnPaged label="{companyEmployeesEditableTableCountryAndGender}" id="_Form_TabContainer_Tab1_TabContainer_Tab4_PanelGroup_TablePaged_ColumnPaged2_ColumnPaged2">
                                        <ColumnPaged label="{companyEmployeesEditableTableCountry}" id="_Form_TabContainer_Tab1_TabContainer_Tab4_PanelGroup_TablePaged_ColumnPaged2_ColumnPaged2_ColumnPaged1">
                                            <ColumnPaged label="{companyEmployeesEditableTableCity}" value="{person.city}" sortBy="PersonCity" id="_Form_TabContainer_Tab1_TabContainer_Tab4_PanelGroup_TablePaged_ColumnPaged2_ColumnPaged2_ColumnPaged1_ColumnPaged1"/>
                                            <ColumnPaged label="{companyEmployeesEditableTableCitizenship}" value="{person.citizenship}" sortBy="PersonCitizenship" id="_Form_TabContainer_Tab1_TabContainer_Tab4_PanelGroup_TablePaged_ColumnPaged2_ColumnPaged2_ColumnPaged1_ColumnPaged2"/>
                                        </ColumnPaged>
                                        <ColumnPaged label="{companyEmployeesEditableTableGender}" value="{person.gender}" sortBy="PersonGender" id="_Form_TabContainer_Tab1_TabContainer_Tab4_PanelGroup_TablePaged_ColumnPaged2_ColumnPaged2_ColumnPaged2"/>
                                    </ColumnPaged>
                                </ColumnPaged>
                                <ColumnPaged label="{companyEmployeesEditableTableStatus}" value="{person.status}" sortBy="PersonStatus" id="_Form_TabContainer_Tab1_TabContainer_Tab4_PanelGroup_TablePaged_ColumnPaged3"/>
                                <ColumnPaged label="{companyEmployeesEditableTableName} + {companyEmployeesEditableTableSurname}" id="_Form_TabContainer_Tab1_TabContainer_Tab4_PanelGroup_TablePaged_ColumnPaged4">
                                    <OutputLabel width="md-12" value="{person.name}" id="_Form_TabContainer_Tab1_TabContainer_Tab4_PanelGroup_TablePaged_ColumnPaged4_OutputLabel1"/>
                                    <OutputLabel width="md-12" value="{person.surname}" id="_Form_TabContainer_Tab1_TabContainer_Tab4_PanelGroup_TablePaged_ColumnPaged4_OutputLabel2"/>
                                </ColumnPaged>
                            </TablePaged>
                            <!--<Row layout="poziomy" label=" ">-->
                            <!--<Spacer width="md-9"/>-->
                            <!--<Button id="pAdd" width="md-1" label="Add new" onClick="addNewPersonWindow"/>-->
                            <!--<Button id="pEdit" width="md-1" label="Edit"-->
                            <!--onClick="editPersonWindow(selectedPerson)"/>-->
                            <!--<Button id="pDelete" width="md-1" label="Delete"-->
                            <!--onClick="removePerson(selectedPerson)"/>-->
                            <!--</Row>-->
                            <InputText id="tablePagedExampleCode3" label="{$.fh.docs.component.code}" width="md-12" height="680" rowsCount="21">
<![CDATA[![ESCAPE[<TablePaged id="tablePage_CompanyEmployeesEditableNested" iterator="person" collection="{pagedPeople}" onRowClick="-" selected="{selectedPersonPage}" pageSize="5">
<ColumnPaged label="No." value="{person$rowNo}"/>
<ColumnPaged label="{companyEmployeesEditableTablePersonalData}">
    <ColumnPaged label="{companyEmployeesEditableTableNameAndSurname}">
        <ColumnPaged label="{companyEmployeesEditableTableName}" value="{person.name}" sortBy="PersonName"/>
        <ColumnPaged label="{companyEmployeesEditableTableSurname}" value="{person.surname}" sortBy="PersonSurname"/>
    </ColumnPaged>
    <ColumnPaged label="{companyEmployeesEditableTableCountryAndGender}">
        <ColumnPaged label="{companyEmployeesEditableTableCountry}">
            <ColumnPaged label="{companyEmployeesEditableTableCity}" value="{person.city}" sortBy="PersonCity"/>
            <ColumnPaged label="{companyEmployeesEditableTableCitizenship}" value="{person.citizenship}" sortBy="PersonCitizenship"/>
        </ColumnPaged>
        <ColumnPaged label="{companyEmployeesEditableTableGender}" value="{person.gender}" sortBy="PersonGender"/>
    </ColumnPaged>
</ColumnPaged>
<ColumnPaged label="{companyEmployeesEditableTableStatus}" value="{person.status}" sortBy="PersonStatus"/>
<ColumnPaged label="{companyEmployeesEditableTableName} + {companyEmployeesEditableTableSurname}">
    <OutputLabel value="{person.name}"/>
    <OutputLabel value="{person.surname}"/>
</ColumnPaged>
</TablePaged>]]]]>
                            </InputText>

                            <InputText id="tablePagedExampleJavaCode2" label="{$.fh.docs.component.java}" width="md-12" height="490" rowsCount="15">
                                <![CDATA[![ESCAPE[
private enum SortedProperty {
    PersonNameAndSurname((firstPerson, secondPerson) -> (firstPerson.getName() + firstPerson.getSurname()).compareTo(secondPerson.getName() + secondPerson.getSurname())),
    PersonName((firstPerson, secondPerson) -> firstPerson.getName().compareTo(secondPerson.getName())),
    PersonSurname((firstPerson, secondPerson) -> firstPerson.getSurname().compareTo(secondPerson.getSurname())),
    PersonCity((firstPerson, secondPerson) -> firstPerson.getCity().compareTo(secondPerson.getCity())),
    PersonGender((firstPerson, secondPerson) -> firstPerson.getGender().compareTo(secondPerson.getGender())),
    PersonStatus((firstPerson, secondPerson) -> firstPerson.getStatus().compareTo(secondPerson.getStatus())),
    PersonCitizenship((firstPerson, secondPerson) -> firstPerson.getCitizenship().compareTo(secondPerson.getCitizenship()));

    private ComparatorFunction<Person> comparator;

    SortedProperty(ComparatorFunction<Person> comparator) {
        this.comparator = comparator;
    }
}
]]]]>
                            </InputText>
                        </PanelGroup>
                    </Tab>


                    <Tab label="{$.fh.docs.component.tablepaged_paged_sortable_data_with_colored_rows}" id="_Form_TabContainer_Tab1_TabContainer_Tab5">
                        <PanelGroup width="md-12" label="{$.fh.docs.component.tablepaged_tablepaged_with_colored_rows}" id="_Form_TabContainer_Tab1_TabContainer_Tab5_PanelGroup">
                            <TablePaged id="tablePage_ColoredCompanyEmployeesEditableNested" iterator="person" collection="{pagedPeopleColoredRows}" onRowClick="-" selected="{selectedPersonPageColoredRows}" rowStylesMap="{coloredPagedPeople}" pageSize="5">
                                <ColumnPaged label="{$.fh.docs.component.tablepaged_no}" value="{person$rowNo}" id="_Form_TabContainer_Tab1_TabContainer_Tab5_PanelGroup_TablePaged_ColumnPaged1"/>
                                <ColumnPaged label="{companyEmployeesEditableTablePersonalData}" id="_Form_TabContainer_Tab1_TabContainer_Tab5_PanelGroup_TablePaged_ColumnPaged2">
                                    <ColumnPaged label="{companyEmployeesEditableTableNameAndSurname}" id="_Form_TabContainer_Tab1_TabContainer_Tab5_PanelGroup_TablePaged_ColumnPaged2_ColumnPaged1">
                                        <ColumnPaged label="{companyEmployeesEditableTableName}" value="{person.name}" sortBy="PersonName" id="_Form_TabContainer_Tab1_TabContainer_Tab5_PanelGroup_TablePaged_ColumnPaged2_ColumnPaged1_ColumnPaged1"/>
                                        <ColumnPaged label="{companyEmployeesEditableTableSurname}" value="{person.surname}" sortBy="PersonSurname" id="_Form_TabContainer_Tab1_TabContainer_Tab5_PanelGroup_TablePaged_ColumnPaged2_ColumnPaged1_ColumnPaged2"/>
                                    </ColumnPaged>
                                    <ColumnPaged label="{companyEmployeesEditableTableCountryAndGender}" id="_Form_TabContainer_Tab1_TabContainer_Tab5_PanelGroup_TablePaged_ColumnPaged2_ColumnPaged2">
                                        <ColumnPaged label="{companyEmployeesEditableTableCountry}" id="_Form_TabContainer_Tab1_TabContainer_Tab5_PanelGroup_TablePaged_ColumnPaged2_ColumnPaged2_ColumnPaged1">
                                            <ColumnPaged label="{companyEmployeesEditableTableCity}" value="{person.city}" sortBy="PersonCity" id="_Form_TabContainer_Tab1_TabContainer_Tab5_PanelGroup_TablePaged_ColumnPaged2_ColumnPaged2_ColumnPaged1_ColumnPaged1"/>
                                            <ColumnPaged label="{companyEmployeesEditableTableCitizenship}" value="{person.citizenship}" sortBy="PersonCitizenship" id="_Form_TabContainer_Tab1_TabContainer_Tab5_PanelGroup_TablePaged_ColumnPaged2_ColumnPaged2_ColumnPaged1_ColumnPaged2"/>
                                        </ColumnPaged>
                                        <ColumnPaged label="{companyEmployeesEditableTableGender}" value="{person.gender}" sortBy="PersonGender" id="_Form_TabContainer_Tab1_TabContainer_Tab5_PanelGroup_TablePaged_ColumnPaged2_ColumnPaged2_ColumnPaged2"/>
                                    </ColumnPaged>
                                </ColumnPaged>
                                <ColumnPaged label="{companyEmployeesEditableTableStatus}" value="{person.status}" sortBy="PersonStatus" id="_Form_TabContainer_Tab1_TabContainer_Tab5_PanelGroup_TablePaged_ColumnPaged3"/>
                                <ColumnPaged label="{companyEmployeesEditableTableName} + {companyEmployeesEditableTableSurname}" id="_Form_TabContainer_Tab1_TabContainer_Tab5_PanelGroup_TablePaged_ColumnPaged4">
                                    <OutputLabel width="md-12" value="{person.name}" id="_Form_TabContainer_Tab1_TabContainer_Tab5_PanelGroup_TablePaged_ColumnPaged4_OutputLabel1"/>
                                    <OutputLabel width="md-12" value="{person.surname}" id="_Form_TabContainer_Tab1_TabContainer_Tab5_PanelGroup_TablePaged_ColumnPaged4_OutputLabel2"/>
                                </ColumnPaged>
                            </TablePaged>
                            <InputText id="tablePagedExampleCode4" label="{$.fh.docs.component.code}" width="md-12" height="710" rowsCount="22">
<![CDATA[![ESCAPE[<TablePaged id="tablePage_ColoredCompanyEmployeesEditableNested" iterator="person" collection="{pagedPeople}" onRowClick="-" selected="{selectedPersonPage}"
     rowStylesMap="{coloredPagedPeople}" pageSize="5">
<ColumnPaged label="No." value="{person$rowNo}" />
<ColumnPaged label="{companyEmployeesEditableTablePersonalData}">
    <ColumnPaged label="{companyEmployeesEditableTableNameAndSurname}">
        <ColumnPaged label="{companyEmployeesEditableTableName}" value="{person.name}" sortBy="PersonName"/>
        <ColumnPaged label="{companyEmployeesEditableTableSurname}" value="{person.surname}" sortBy="PersonSurname"/>
    </ColumnPaged>
    <ColumnPaged label="{companyEmployeesEditableTableCountryAndGender}">
        <ColumnPaged label="{companyEmployeesEditableTableCountry}">
            <ColumnPaged label="{companyEmployeesEditableTableCity}" value="{person.city}" sortBy="PersonCity"/>
            <ColumnPaged label="{companyEmployeesEditableTableCitizenship}" value="{person.citizenship}" sortBy="PersonCitizenship}/>
        </ColumnPaged>
        <ColumnPaged label="{companyEmployeesEditableTableGender}" value="{person.gender}" sortBy="PersonGender"/>
    </ColumnPaged>
</ColumnPaged>
<ColumnPaged label="{companyEmployeesEditableTableStatus}" value="{person.status}" sortBy="PersonStatus"/>
<ColumnPaged label="{companyEmployeesEditableTableName} + {companyEmployeesEditableTableSurname}">
    <OutputLabel value="{person.name}"/>
    <OutputLabel value="{person.surname}"/>
</ColumnPaged>
</TablePaged>]]]]>
                            </InputText>

                            <InputText id="tablePagedExampleJavaCode3" label="{$.fh.docs.component.java}" width="md-12" height="490" rowsCount="15">
                                <![CDATA[![ESCAPE[
private enum SortedProperty {
    PersonNameAndSurname((firstPerson, secondPerson) -> (firstPerson.getName() + firstPerson.getSurname()).compareTo(secondPerson.getName() + secondPerson.getSurname())),
    PersonName((firstPerson, secondPerson) -> firstPerson.getName().compareTo(secondPerson.getName())),
    PersonSurname((firstPerson, secondPerson) -> firstPerson.getSurname().compareTo(secondPerson.getSurname())),
    PersonCity((firstPerson, secondPerson) -> firstPerson.getCity().compareTo(secondPerson.getCity())),
    PersonGender((firstPerson, secondPerson) -> firstPerson.getGender().compareTo(secondPerson.getGender())),
    PersonStatus((firstPerson, secondPerson) -> firstPerson.getStatus().compareTo(secondPerson.getStatus())),
    PersonCitizenship((firstPerson, secondPerson) -> firstPerson.getCitizenship().compareTo(secondPerson.getCitizenship()));

    private ComparatorFunction<Person> comparator;

    SortedProperty(ComparatorFunction<Person> comparator) {
        this.comparator = comparator;
    }
}
]]]]>
                            </InputText>
                        </PanelGroup>
                    </Tab>
                    <Tab label="{$.fh.docs.component.tablepaged_prototyping_with_inline_data}" id="_Form_TabContainer_Tab1_TabContainer_Tab6">
                        <PanelGroup width="md-12" label="{$.fh.docs.component.tablepaged_table_with_inline_data_binding}" id="_Form_TabContainer_Tab1_TabContainer_Tab6_PanelGroup">
                            <OutputLabel width="md-12" value="{$.fh.docs.component.tablepaged_for_better_prototyping}" id="_Form_TabContainer_Tab1_TabContainer_Tab6_PanelGroup_OutputLabel"/>
                            <TablePaged id="table_inlineData" pageSize="5" iterator="row" collection="{RULE.pl.fhframework.core.rules.builtin.CsvRowsPageable.csvRowsPageable('John;Black;34|Tom;White;67|Florence;Dalton;25|Mary;Lee;33|Antony;Blue;20|John;Doe;44|Mary;Parker;90')}">
                                <ColumnPaged label="{$.fh.docs.component.tablepaged_first_name}" value="{row.column1}" sortBy="column1" id="_Form_TabContainer_Tab1_TabContainer_Tab6_PanelGroup_TablePaged_ColumnPaged1"/>
                                <ColumnPaged label="{$.fh.docs.component.tablepaged_last_name}" value="{row.column2}" sortBy="column2" id="_Form_TabContainer_Tab1_TabContainer_Tab6_PanelGroup_TablePaged_ColumnPaged2"/>
                                <ColumnPaged label="{$.fh.docs.component.tablepaged_age}" value="{row.column3}" sortBy="column3" id="_Form_TabContainer_Tab1_TabContainer_Tab6_PanelGroup_TablePaged_ColumnPaged3"/>
                            </TablePaged>
                            <InputText id="table_inlineDataCode" label="{$.fh.docs.component.code}" width="md-12" height="170" rowsCount="5">
    <![CDATA[![ESCAPE[<TablePaged id="table_inlineData" pageSize="5" iterator="row" collection="{RULE.pl.fhframework.core.rules.builtin.CsvRowsPageable.csvRowsPageable('John;Black;34|Tom;White;67|Florence;Dalton;25|Mary;Lee;33|Antony;Blue;20|John;Doe;44|Mary;Parker;90')}">
    <ColumnPaged label="First name" value="{row.column1}" sortBy="column1"/>
    <ColumnPaged label="Last name" value="{row.column2}" sortBy="column2"/>
    <ColumnPaged label="Age" value="{row.column3}" sortBy="column3"/>
</TablePaged>
]]]]>
                            </InputText>
                        </PanelGroup>
                    </Tab>
                    <Tab label="{$.fh.docs.component.tablepaged_model_header}" id="_Form_TabContainer_Tab1_TabContainer_Tab7">
                        <OutputLabel width="md-12" value="{$.fh.docs.component.tablepaged_model}" id="_Form_TabContainer_Tab1_TabContainer_Tab7_OutputLabel"/>
                    </Tab>

                </TabContainer>
        </Tab>

        <Tab label="{$.fh.docs.component.attributes}" id="_Form_TabContainer_Tab2">
            <Table iterator="item" collection="{attributes}" id="_Form_TabContainer_Tab2_Table">
                <Column label="{$.fh.docs.component.attributes_identifier}" value="{item.name}" width="15" id="_Form_TabContainer_Tab2_Table_Column1"/>
                <Column label="{$.fh.docs.component.attributes_type}" value="{item.type}" width="15" id="_Form_TabContainer_Tab2_Table_Column2"/>
                <Column label="{$.fh.docs.component.attributes_boundable}" value="{item.boundable}" width="10" id="_Form_TabContainer_Tab2_Table_Column3"/>
                <Column label="{$.fh.docs.component.attributes_default_value}" value="{item.defaultValue}" width="20" id="_Form_TabContainer_Tab2_Table_Column4"/>
                <Column label="{$.fh.docs.component.attributes_description}" value="{item.description}" width="40" id="_Form_TabContainer_Tab2_Table_Column5"/>
            </Table>
        </Tab>
    </TabContainer>
    <Button id="pBack" label="[icon='fa fa-chevron-left'] {$.fh.docs.component.back}" onClick="backToFormComponentsList"/>

</Form>