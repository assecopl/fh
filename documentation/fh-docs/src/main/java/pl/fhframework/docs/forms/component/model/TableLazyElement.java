package pl.fhframework.docs.forms.component.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pl.fhframework.core.designer.ComponentElement;
import pl.fhframework.docs.forms.model.example.Person;
import pl.fhframework.docs.forms.service.CountryService;
import pl.fhframework.docs.forms.service.PersonService;
import pl.fhframework.model.forms.PageModel;

import java.util.List;
import java.util.function.Function;

import static org.springframework.data.domain.Sort.Direction;

@Getter
@Setter
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TableLazyElement extends ComponentElement {

    private PageModel<Person> pagedPeopleForRead;
    private PageModel<Person> pagedPeople;
    private PageModel<CountryService.Country> pageModelCountries;
    private List<Person> allPeople = PersonService.findAll();

    private Function<Direction, Integer> sortDirectionFactorFunction = (direction) -> {
        if (Direction.ASC.equals(direction)) {
            return 1;
        } else if (Direction.DESC.equals(direction)) {
            return -1;
        }

        return 0;
    };


}
