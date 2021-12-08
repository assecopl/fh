package pl.fhframework.dp.commons.utils.date;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.time.LocalDate;

/**
 * @author <a href="mailto:jacek_borowiec@skg.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 23/04/2021
 */
public class LocalDateSerializer extends StdSerializer<LocalDate> {

    public LocalDateSerializer() {
        super(LocalDate.class);
    }

    @Override
    public void serialize(LocalDate value, JsonGenerator generator, SerializerProvider provider) throws IOException {
        if(value == null) return;
//        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
//        DateFormat tzFormatter = new SimpleDateFormat("Z");
//        Object dt = Date.from(value.atStartOfDay(ZoneId.systemDefault()).toInstant());
//        String timezone = tzFormatter.format(dt);
//        String ret = formatter.format(dt) + timezone.substring(0, 3) + ":"
//                + timezone.substring(3);
        generator.writeString(value.toString());

    }
}
