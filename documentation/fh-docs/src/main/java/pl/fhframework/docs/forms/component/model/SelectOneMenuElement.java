package pl.fhframework.docs.forms.component.model;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import pl.fhframework.docs.converter.model.User;
import pl.fhframework.docs.converter.service.UserService;
import pl.fhframework.docs.forms.model.example.Person;
import pl.fhframework.docs.forms.model.example.SizeEnum;
import pl.fhframework.docs.forms.service.PersonService;
import pl.fhframework.core.designer.ComponentElement;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SelectOneMenuElement extends ComponentElement {

    private String selectOneMenuValue = "";
    private String selectOneMenuToSelectOneMenuValue = "UK";

    private List<User> boundUsers;
    private User selectedUserBidingExample;

    private SizeEnum selectedSize = SizeEnum.BIG;
    private List<SizeEnum> allSizes = Arrays.asList(SizeEnum.values());
    private List<SizeEnum> sizes = Arrays.asList(SizeEnum.values());
    private List<String> keepRemovedValueStringList = Arrays.asList("One", "Two", "Three", "Four", "Five");
    private String keepRemovedValueString = "Value outside values collection";

    private List<Person> keepRemovedValuePeopleList = Arrays.asList(
            new Person(100L, "John", "Doe", "London", "Man", "Active", "England", "A", null, null),
            new Person(102L, "Marry", "Doe", "London", "Female", "Active", "England", "A", null, null),
            new Person(103L, "George", "Lucas", "London", "Man", "Active", "England", "A", null, null),
            new Person(104L, "Barry", "Corgan", "New York", "Man", "Active", "USA", "A", null, null),
            new Person(105L, "Samuel", "Sanches", "Madrid", "Man", "Active", "Spain", "A", null, null),
            new Person(106L, "Fabian", "Cancellara", "Brno", "Man", "Active", "Switzerland", "A", null, null)
    );

    private Person keepRemovedValuePerson = new Person(107L, "Peter", "Sagan", "Bratislava", "Man", "Active", "Slovakia", "A", null, null);
    private SizeEnum selectedSizeToRemove = SizeEnum.SMALL;
    private Person selectedPersonConvExample =  new Person(3l, "Sara", "Larson", "San Francisco", "Female", "Active", "France", "A", null, null);
    private Person emptyValueSelectedPersonExample =  new Person(2l, "Larry", "Ellison", "Los Angeles", "Male", "Inactive", "USA", "B", new Date(1220227200l), null);

    private List<Person> people = PersonService.findAll();
    private List<Person> peopleWithEmptyValue = PersonService.findPeopleForEmpty();

    private String selectOneMenuOnChangeValue = "Poland";

    public SelectOneMenuElement(UserService userService){
        this.boundUsers = userService.findAll();
        this.selectedUserBidingExample = boundUsers.get(1);
    }
}
