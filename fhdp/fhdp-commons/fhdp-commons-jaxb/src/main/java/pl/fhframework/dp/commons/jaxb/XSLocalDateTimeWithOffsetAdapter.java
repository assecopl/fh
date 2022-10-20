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
 * Created by Bartosz Moś on 30.08.2019.
 */
public class XSLocalDateTimeWithOffsetAdapter extends XmlAdapter<String, LocalDateTime> {
    private static Logger log = LoggerFactory.getLogger(XSLocalDateTimeWithOffsetAdapter.class);

    private final ZoneId zone;

    public XSLocalDateTimeWithOffsetAdapter() {
        String zone = System.getProperty("fhdp.timeZone", "Europe/Vilnius");
        this.zone = ZoneId.of(zone);
    }

    public XSLocalDateTimeWithOffsetAdapter(ZoneId zone) {
        this.zone = zone;
    }

    List<String> knownPatterns = new ArrayList<>();
    {
        knownPatterns.add("yyyy-MM-dd'T'HH:mm:ss[XXX]");
        knownPatterns.add("yyyy-MM-dd'T'HH:mm:ss [XXX]");
        knownPatterns.add("yyyy-MM-dd'T'HH:mm:ss[.SSS][XXX]");
        knownPatterns.add("yyyy-MM-dd'T'HH:mm:ss[.SSSS][XXX]");
        knownPatterns.add("yyyy-MM-dd'T'HH:mm:ss[.SSSSS][XXX]");
        knownPatterns.add("yyyy-MM-dd'T'HH:mm:ss[.SSSSSS][XXX]");
        knownPatterns.add("yyyy-MM-dd'T'HH:mm:ss[.SSSSSSS][XXX]");
        knownPatterns.add("yyyy-MM-dd'T'HH:mm:ss[.SSS] [XXX]");
        knownPatterns.add("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        knownPatterns.add("yyyy-MM-dd'T'HH:mm:ss.SSSS'Z'");
        knownPatterns.add("yyyy-MM-dd'T'HH:mm:ss.SSSSS'Z'");
        knownPatterns.add("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");

        knownPatterns.add("yyyy-MM-dd'T'HH:mm:ss");
        knownPatterns.add("yyyy-MM-dd' 'HH:mm:ss");
    }

    @Override
    public LocalDateTime unmarshal(String v) {
        if (v == null) return null;

        LocalDateTime ldt = null;

        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendPattern("yyyy-MM-dd'T'HH:mm:ss[[.SSSSSS]][XXXXX]")
                .parseDefaulting(ChronoField.OFFSET_SECONDS, 0)
                .toFormatter();

        if (v.length() > 10) {
            String time = v.substring(9);
            if (time.contains("+") || time.contains("-") || time.contains("Z")) {
                OffsetDateTime ofdt = OffsetDateTime.parse(v);
                ldt = ofdt.withOffsetSameInstant(zone.getRules().getOffset(ofdt.toLocalDateTime())).toLocalDateTime();
            } else {
                try {
                    ldt = LocalDateTime.parse(v, formatter);
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
        }

        return ldt;
    }

    @Override
    public String marshal(LocalDateTime v) {
        if (v == null) return null;
        ZoneOffset offset = zone.getRules().getOffset(v);

        OffsetDateTime offSetByTwo = OffsetDateTime
                .of(v.withNano(0), offset);
        DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd'T'HH:mm:ss[XXXXX]")
                .parseDefaulting(ChronoField.OFFSET_SECONDS, 0)
                .toFormatter();

        String ret = offSetByTwo.format(formatter);
        log.debug("marshal: {}", ret);
        return ret;
    }

}
