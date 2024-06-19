package pl.fhframework.docs.forms.model.example.formatter;

import pl.fhframework.docs.forms.model.example.Person;
import pl.fhframework.format.AutoRegisteredConverter;
import pl.fhframework.format.FhConverter;

/**
 * Person converter.
 */
@FhConverter
public class PersonConverter extends AutoRegisteredConverter<Person ,String> {

    @Override
    public String convert(Person person) {
        return person.getName();
    }
}
