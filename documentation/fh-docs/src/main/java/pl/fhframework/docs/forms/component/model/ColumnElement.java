package pl.fhframework.docs.forms.component.model;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import pl.fhframework.docs.forms.model.example.Person;
import pl.fhframework.docs.forms.service.PersonService;
import pl.fhframework.core.designer.ComponentElement;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ColumnElement extends ComponentElement {

    private List<Person> people = PersonService.findAllRestricted();

    private String companyEmployeesEditableTableFullInfo = "All information about person";
    private String companyEmployeesEditableTableName = "Name";
    private String companyEmployeesEditableTableSurname = "Surname";
    private String companyEmployeesEditableTableNameAndSurname = "Name and Surname";
    private String companyEmployeesEditableTableNameAndSurnameAndCity = "Name, Surname and City";
    private String companyEmployeesEditableTablePersonalDate = "Personal data";
    private String companyEmployeesEditableTableCity = "City";
    private String companyEmployeesEditableTableGenderAndStatus = "Gender and Status";
    private String companyEmployeesEditableTableGender = "Gender";
    private String companyEmployeesEditableTableStatus = "Status";
    private String companyEmployeesEditableTableCitizenship = "Citizenship";
    private String companyEmployeesEditableTableDrivingLicense = "Driving license";
    private String companyEmployeesEditableTableBirthDate = "Birth date";
}
