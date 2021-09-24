package pl.fhframework.model.forms.formatters;

import org.springframework.format.annotation.DateTimeFormat;
import pl.fhframework.format.FhFormatter;
import pl.fhframework.format.formatter.DefaultDateTimeFormatter;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

/**
 * M. Szklarski
 */
@FhFormatter("dataFormatter")
public class DataFormatter extends DefaultDateTimeFormatter {

    @Override
    @PostConstruct
    public void postConstruct() {
        this.setIso(DateTimeFormat.ISO.DATE_TIME);
        this.setTimeZone(TimeZone.getDefault());
        this.setPattern("dd-MM-yyyy");
    }
}
