package pl.fhframework.dp.commons.jaxb;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author <a href="mailto:jacek.borowiec@asseco.pl">Jacek Borowiec</a>
 * @created 19/05/2022
 */
public class XSLocalDateTimeAdapterUTC extends XmlAdapter<String, LocalDateTime> {
    private final ZoneId zone;

    public XSLocalDateTimeAdapterUTC() {
        String zone = System.getProperty("fhdp.timeZone", "Europe/Vilnius");
        this.zone = ZoneId.of(zone);
    }

    @Override
    public LocalDateTime unmarshal(String v) throws Exception {
        ZonedDateTime utcZoned = ZonedDateTime.parse(v);
        ZonedDateTime ltZoned = utcZoned.withZoneSameInstant(zone);
        LocalDateTime ret = ltZoned.toLocalDateTime();
        return ret;
    }

    @Override
    public String marshal(LocalDateTime v) throws Exception {
        ZonedDateTime zdt = v.atZone( zone );
        ZoneId utcZone = ZoneId.of("UTC");
        ZonedDateTime utcZoned = zdt.withZoneSameInstant(utcZone);
        return utcZoned.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX"));
    }
}
