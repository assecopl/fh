package pl.fhframework.dp.commons.utils.date;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

/**
 *
 * @author <a href="mailto:jacek_borowiec@skg.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 23/04/2021
 */
public class LocalDateTimeSerializer extends StdSerializer<LocalDateTime> {
    public LocalDateTimeSerializer() {
        super(LocalDateTime.class);
    }

    public void serialize(LocalDateTime value, JsonGenerator generator, SerializerProvider provider) throws IOException {
        if (value != null) {
            ZoneId zone = ZoneId.of("Europe/Warsaw");
            ZoneOffset offset = zone.getRules().getOffset(LocalDateTime.now());
            OffsetDateTime offSetByTwo = OffsetDateTime.of(value.withNano(0), offset);
            DateTimeFormatter formatter = (new DateTimeFormatterBuilder()).appendPattern("yyyy-MM-dd'T'HH:mm:ss[XXXXX]").parseDefaulting(ChronoField.OFFSET_SECONDS, 0L).toFormatter();
            String ret = offSetByTwo.format(formatter);
            generator.writeString(ret);
        }
    }
}
