package pl.fhframework.docs.forms.component.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pl.fhframework.docs.forms.model.example.Person;
import pl.fhframework.docs.forms.service.CountryService;
import pl.fhframework.docs.forms.service.PersonService;
import pl.fhframework.model.forms.PageModel;
import pl.fhframework.core.designer.ComponentElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.springframework.data.domain.Sort.Direction;

@Getter
@Setter
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TablePagedElement extends ComponentElement {

    private PageModel<Person> pagedPeopleForRead;
    private PageModel<Person> pagedPeople;
    private PageModel<Person> pagedPeopleMergedColumns;
    private PageModel<Person> pagedPeopleColoredRows;
    private Map<Person, String> coloredPagedPeople = PersonService.findColoredPagedPeople();
    private PageModel<CountryService.Country> pageModelCountries;
    private List<CountryService.Country> selectedCountries = new ArrayList<>();

    private Person selectedPersonPage;
    private Person selectedPersonPageMergedColumns;
    private Person selectedPersonPageColoredRows;

    private Function<Direction, Integer> sortDirectionFactorFunction = (direction) -> {
        if (Direction.ASC.equals(direction)) {
            return 1;
        } else if (Direction.DESC.equals(direction)) {
            return -1;
        }

        return 0;
    };

    private String companyEmployeesEditableTablePersonalData = "Personal Data";
    private String companyEmployeesEditableTableNameAndSurname = "Name and Surname";
    private String companyEmployeesEditableTableName = "Name";
    private String companyEmployeesEditableTableSurname = "Surname";
    private String companyEmployeesEditableTableCountryAndGender = "Country and Gender";
    private String companyEmployeesEditableTableCountry = "Country";
    private String companyEmployeesEditableTableCity = "City";
    private String companyEmployeesEditableTableGender = "Gender";
    private String companyEmployeesEditableTableStatus = "Status";
    private String companyEmployeesEditableTableCitizenship = "Citizenship";
    private String companyEmployeesEditableTableDrivingLicense = "Driving license";
}
