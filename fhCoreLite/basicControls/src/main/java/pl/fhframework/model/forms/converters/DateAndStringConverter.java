package pl.fhframework.model.forms.converters;

import pl.fhframework.format.AutoRegisteredConverter;
import pl.fhframework.format.FhConverter;

import java.time.ZonedDateTime;
import java.util.Date;

/**
 * ISO-8601 compilant
 */
@FhConverter
public class DateAndStringConverter extends AutoRegisteredConverter<String, Date> {
    @Override
    public Date convert(String source) {
        return Date.from(ZonedDateTime.parse(source).toInstant());
    }
}
