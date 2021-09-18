package pl.fhframework.format.formatter;

import org.springframework.format.datetime.DateFormatter;

import pl.fhframework.format.FhFormatter;

import java.util.TimeZone;

import javax.annotation.PostConstruct;

@FhFormatter("defaultDateTimeFormatter")
public class DefaultDateTimeFormatter extends DateFormatter {

    @PostConstruct
    public void postConstruct() {
        this.setPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
        this.setTimeZone(TimeZone.getDefault());
    }

}
