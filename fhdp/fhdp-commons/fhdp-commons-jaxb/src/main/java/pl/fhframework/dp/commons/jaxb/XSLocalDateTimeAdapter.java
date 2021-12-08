/*
 * Copyright (c) 2019. SKG S.A.
 */

package pl.fhframework.dp.commons.jaxb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:jacek_borowiec@skg.pl">Jacek Borowiec</a>
 * @version $Revision:  $, $Date:  $
 * @created 2019-01-16
 */
public class XSLocalDateTimeAdapter extends XmlAdapter<String, LocalDateTime> {
    private static Logger log = LoggerFactory.getLogger(XSLocalDateTimeAdapter.class);

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

    List<String> knownPatterns = new ArrayList<>();

    {
        knownPatterns.add("yyyy-MM-dd'T'HH:mm:ssXXX");
        knownPatterns.add("yyyy-MM-dd'T'HH:mm:ss XXX");
        knownPatterns.add("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        knownPatterns.add("yyyy-MM-dd'T'HH:mm:ss.SSSSXXX");
        knownPatterns.add("yyyy-MM-dd'T'HH:mm:ss.SSSSSXXX");
        knownPatterns.add("yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX");
        knownPatterns.add("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSXXX");
        knownPatterns.add("yyyy-MM-dd'T'HH:mm:ss.SSS XXX");
        knownPatterns.add("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        knownPatterns.add("yyyy-MM-dd'T'HH:mm:ss.SSSS'Z'");
        knownPatterns.add("yyyy-MM-dd'T'HH:mm:ss.SSSSS'Z'");
        knownPatterns.add("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");

        knownPatterns.add("yyyy-MM-dd'T'HH:mm:ss");
        knownPatterns.add("yyyy-MM-dd' 'HH:mm:ss");
    }


    @Override
    public LocalDateTime unmarshal(String v)  {
        if(v == null) return null;
        try {
            return LocalDateTime.parse(v);
        } catch (DateTimeParseException pe) {
            for (String pattern : knownPatterns) {
                try {
                    return LocalDateTime.parse(v, DateTimeFormatter.ofPattern(pattern));
                } catch (DateTimeParseException px) {
                }
            }
            throw new DateTimeParseException(pe.getMessage(), pe.getParsedString(), pe.getErrorIndex());
        }
    }

    @Override
    public String marshal(LocalDateTime v)  {
        if(v == null) return null;
        ZoneId zone = ZoneId.of("Europe/Warsaw");
        ZoneOffset offset = zone.getRules().getOffset(LocalDateTime.now());

//        ZoneOffset offset = OffsetDateTime.now().getOffset();
        OffsetDateTime offSetByTwo = OffsetDateTime
                .of(v.withNano(0), offset);
        DateTimeFormatter formatter =  new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd'T'HH:mm:ss")
                .parseDefaulting(ChronoField.OFFSET_SECONDS, 0)
                // or OffsetDateTime.now().getLong(ChronoField.OFFSET_SECONDS)
                .toFormatter();;
        String ret = offSetByTwo.format(formatter);
        log.debug("marshal: {}", ret);
        return ret;
    }
}
