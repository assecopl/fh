package pl.fhframework.model.forms.converters;

import pl.fhframework.format.AutoRegisteredConverter;
import pl.fhframework.format.FhConverter;

import java.time.LocalDate;

@FhConverter
public class LocalDateAndStringConverter extends AutoRegisteredConverter<String, LocalDate> {
    @Override
    public LocalDate convert(String source) {
        return LocalDate.parse(source);
    }
}
