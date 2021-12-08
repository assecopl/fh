package pl.fhframework.dp.commons.utils.date;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:jacek_borowiec@skg.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 23/04/2021
 */
public class LocalDateTimeDeserializer extends StdDeserializer<LocalDateTime> {

    private Pattern pattern = Pattern.compile("^.*\\+....");

    public LocalDateTimeDeserializer() {
        super(LocalDateTime.class);
    }

    @Override
    public LocalDateTime deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        String valueStr = parser.readValueAs(String.class);
        if(valueStr == null) return null;

        Matcher matcher = pattern.matcher(valueStr);
        if(matcher.matches()){
            valueStr = valueStr.substring(0, valueStr.length() - 2) + ":" + valueStr.substring(valueStr.length() - 2);
        }

        OffsetDateTime ofdt = OffsetDateTime.parse(valueStr);
        return ofdt.toLocalDateTime();
    }
}
