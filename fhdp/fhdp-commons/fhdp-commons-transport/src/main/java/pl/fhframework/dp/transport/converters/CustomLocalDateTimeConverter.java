package pl.fhframework.dp.transport.converters;

import org.springframework.data.elasticsearch.core.mapping.PropertyValueConverter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Converter for adnotation
 * @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis)
 * instead of currently used
 * @Field(type = FieldType.Date, format = DateFormat.custom, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSX")
 * that current uses custom, but custom is @Deprecated and should be removed soon
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