package pl.fhframework.model.forms.converters;

import pl.fhframework.format.AutoRegisteredConverter;
import pl.fhframework.format.FhConverter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@FhConverter
public class LocalDateTimeAndStringConverter extends AutoRegisteredConverter<String, LocalDateTime> {
    @Override
    public LocalDateTime convert(String source) {
        return LocalDateTime.parse(source);
    }
}
