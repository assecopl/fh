package pl.fhframework.docs.forms.component.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pl.fhframework.core.designer.ComponentElement;
import pl.fhframework.docs.forms.model.example.Person;
import pl.fhframework.docs.forms.service.CountryService;
import pl.fhframework.docs.forms.service.PersonService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static org.springframework.data.domain.Sort.Direction;

@Getter
@Setter
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TableOptimizedElement extends ComponentElement {

    private List<OptimizedExample> people = new ArrayList<OptimizedExample>(Arrays.asList(
            new OptimizedExample("Bill",  LocalDate.now(), new CountryService.Country("United Kingdom", "London", 65.110, 242.495)),
            new OptimizedExample("Bill1",  LocalDate.now(), new CountryService.Country("United Kingdom", "London", 65.110, 242.495) ),
            new OptimizedExample("Bill2",  LocalDate.now(), new CountryService.Country("United Kingdom", "London", 65.110, 242.495) ),
            new OptimizedExample("Bill3",  LocalDate.now(), new CountryService.Country("United Kingdom", "London", 65.110, 242.495) ),
            new OptimizedExample("Bill4",  LocalDate.now(), new CountryService.Country("United Kingdom", "London", 65.110, 242.495) ),
            new OptimizedExample("Bill5",  LocalDate.now(), new CountryService.Country("United Kingdom", "London", 65.110, 242.495) ),
            new OptimizedExample("Bill6",  LocalDate.now(), new CountryService.Country("United Kingdom", "London", 65.110, 242.495) ),
            new OptimizedExample("Bill7",  LocalDate.now(), new CountryService.Country("United Kingdom", "London", 65.110, 242.495) ),
            new OptimizedExample("Bill8",  LocalDate.now(), new CountryService.Country("United Kingdom", "London", 65.110, 242.495) ),
            new OptimizedExample("Bill9",  LocalDate.now(), new CountryService.Country("United Kingdom", "London", 65.110, 242.495) ),
            new OptimizedExample("Bill10",  LocalDate.now(), new CountryService.Country("United Kingdom", "London", 65.110, 242.495) ),
            new OptimizedExample("Bill11",  LocalDate.now(), new CountryService.Country("United Kingdom", "London", 65.110, 242.495) ),
            new OptimizedExample("Bill12",  LocalDate.now(), new CountryService.Country("United Kingdom", "London", 65.110, 242.495) ),
            new OptimizedExample("Bill13",  LocalDate.now(), new CountryService.Country("United Kingdom", "London", 65.110, 242.495) ),
            new OptimizedExample("Bill14",  LocalDate.now(), new CountryService.Country("United Kingdom", "London", 65.110, 242.495) ),
            new OptimizedExample("Bill15",  LocalDate.now(), new CountryService.Country("United Kingdom", "London", 65.110, 242.495) ),
            new OptimizedExample("Bill16",  LocalDate.now(), new CountryService.Country("United Kingdom", "London", 65.110, 242.495) ),
            new OptimizedExample("Bill17",  LocalDate.now(), new CountryService.Country("United Kingdom", "London", 65.110, 242.495) ),
            new OptimizedExample("Bill18",  LocalDate.now(), new CountryService.Country("United Kingdom", "London", 65.110, 242.495) ),
            new OptimizedExample("Bill19",  LocalDate.now(), new CountryService.Country("United Kingdom", "London", 65.110, 242.495) ),
            new OptimizedExample("Bill20",  LocalDate.now(), new CountryService.Country("United Kingdom", "London", 65.110, 242.495) ),
            new OptimizedExample("Bill21",  LocalDate.now(), new CountryService.Country("United Kingdom", "London", 65.110, 242.495) ),
            new OptimizedExample("Bill22",  LocalDate.now(), new CountryService.Country("United Kingdom", "London", 65.110, 242.495) ),
            new OptimizedExample("Bill23",  LocalDate.now(), new CountryService.Country("United Kingdom", "London", 65.110, 242.495) ),
            new OptimizedExample("Bill24",  LocalDate.now(), new CountryService.Country("United Kingdom", "London", 65.110, 242.495) ),
            new OptimizedExample("Bill25",  LocalDate.now(), new CountryService.Country("United Kingdom", "London", 65.110, 242.495) ),
            new OptimizedExample("Bill26",  LocalDate.now(), new CountryService.Country("United Kingdom", "London", 65.110, 242.495) ),
            new OptimizedExample("Bill27",  LocalDate.now(), new CountryService.Country("United Kingdom", "London", 65.110, 242.495) ),
            new OptimizedExample("Bill28",  LocalDate.now(), new CountryService.Country("United Kingdom", "London", 65.110, 242.495) ),
            new OptimizedExample("Bill29",  LocalDate.now(), new CountryService.Country("United Kingdom", "London", 65.110, 242.495) ),
            new OptimizedExample("Bill30",  LocalDate.now(), new CountryService.Country("United Kingdom", "London", 65.110, 242.495) )
    ));


    private List<OptimizedExample> allPeople;
    private List<Person> restrictedPeople = PersonService.findAllRestricted();
    private List<CountryService.Country> modelCountries;
    private List<CountryService.Country> selectedCountry;
    private List<OptimizedExample> selectedElement;

    private Function<Direction, Integer> sortDirectionFactorFunction = (direction) -> {
        if (Direction.ASC.equals(direction)) {
            return 1;
        } else if (Direction.DESC.equals(direction)) {
            return -1;
        }

        return 0;
    };


    @Setter
    @Getter
    @AllArgsConstructor
    public class OptimizedExample {
        String name;
        LocalDate birthDate;
        CountryService.Country country;
    }

}
