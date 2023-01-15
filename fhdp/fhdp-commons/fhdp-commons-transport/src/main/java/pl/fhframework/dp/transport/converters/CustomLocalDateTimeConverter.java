package pl.fhframework.dp.transport.converters;

import org.springframework.data.elasticsearch.core.mapping.PropertyValueConverter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Konwerter do zostosowania w przypadku adnitacji
 * @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis)
 * zamiast
 * @Field(type = FieldType.Date, format = DateFormat.custom, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSX")
 * które używa custom, a custom jest @Deprecated
 * {@link PropertyValueConverter}
 *
 */
public class CustomLocalDateTimeConverter implements PropertyValueConverter {
    private final DateTimeFormatter formatterWithoutZone = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss.SSS");


    @Override
    public Object write(Object value) {
        if (value instanceof LocalDateTime) {
            LocalDateTime val = (LocalDateTime) value;
            return val.format(formatterWithoutZone);
        } else {
            return value;
        }
    }

    @Override
    public Object read(Object value) {
        if (value instanceof String) {
            String s = (String) value;
            return LocalDateTime.parse(s,formatterWithoutZone);
        } else {
            return value;
        }
    }
}