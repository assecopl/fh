package pl.fhframework.dp.commons.fh.formatters;

import org.springframework.format.Formatter;
import pl.fhframework.format.FhFormatter;

import java.text.ParseException;
import java.util.Locale;

/**
 * @author <a href="mailto:jacek_borowiec@skg.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 09/08/2020
 */
@FhFormatter("booleanColumnFormatter")
public class BooleanColumnFormatter implements Formatter<Boolean> {
    @Override
    public Boolean parse(String s, Locale locale) throws ParseException {
        return null;
    }

    @Override
    public String print(Boolean aBoolean, Locale locale) {
        return aBoolean? "[color='#437b41'][icon='fas fa-check-circle'][/color]":"[color='#dc3545'][icon='fas fa fa-times-circle'][/color]";
    }
}
