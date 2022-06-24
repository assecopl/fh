package pl.fhframework.dp.transport.converters;

import org.joda.time.DateTime;
import org.springframework.data.elasticsearch.core.mapping.PropertyValueConverter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

/**
 * @author <a href="mailto:jacek.borowiec@asseco.pl">Jacek Borowiec</a>
 * @created 15/04/2022
 */
public class CustomZonedDateTimeConverter implements PropertyValueConverter {
    private final DateTimeFormatter formatterWithZone = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss.SSSX");
    private final DateTimeFormatter formatterWithoutZone = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss.SSS");


    @Override
    public Object write(Object value) {
        if (value instanceof LocalDateTime) {
            LocalDateTime val = (LocalDateTime) value;
            ZonedDateTime zVal = val.atZone(ZoneId.of("UTC"));
            return zVal.format(formatterWithZone);
        } else {
            return value;
        }
    }

    @Override
    public Object read(Object value) {
        if (value instanceof String ) {
            String s = (String) value;
            try {
                return formatterWithZone.parse(s, ZonedDateTime::from);
            } catch (Exception e) {
                return formatterWithoutZone.parse(s, LocalDateTime::from).atZone(ZoneId.of("UTC"));
            }
        } else {
            return value;
        }
    }
}
