package pl.fhframework.docs.forms.component.model;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import pl.fhframework.docs.forms.model.example.Person;
import pl.fhframework.docs.forms.model.example.Student;
import pl.fhframework.docs.forms.service.PersonService;
import pl.fhframework.docs.forms.service.RepeaterExampleDataService;
import pl.fhframework.core.designer.ComponentElement;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class RepeaterElement extends ComponentElement {
    private List<Person> people = PersonService.findAll();

    public List<Student> getStudents(){
        return RepeaterExampleDataService.getStudents();
    }

    public List<RepeaterExampleDataService.Book> getBooks(){
        return RepeaterExampleDataService.getBooks();
    }

    public String getLink(){
        return RepeaterExampleDataService.getLink();
    }

    public RepeaterExampleDataService.Basket getBasket(){
        return RepeaterExampleDataService.getBasket();
    }

    public List<RepeaterExampleDataService.CurrencyCalc> getCurrencyCalcs(){
        return RepeaterExampleDataService.getCurrencyCalcs();
    }
}
