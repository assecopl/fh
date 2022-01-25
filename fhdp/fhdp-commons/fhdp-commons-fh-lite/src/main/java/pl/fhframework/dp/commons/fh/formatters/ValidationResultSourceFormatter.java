package pl.fhframework.dp.commons.fh.formatters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.Formatter;
import pl.fhframework.core.i18n.MessageService;
import pl.fhframework.format.FhFormatter;

import java.text.ParseException;
import java.util.Locale;

/**
 * @author <a href="mailto:jacek_borowiec@skg.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 09/08/2020
 */
@FhFormatter("validationResultSourceFormatter")
public class ValidationResultSourceFormatter implements Formatter<String> {
    @Autowired
    MessageService messageService;

    @Override
    public String parse(String s, Locale locale) throws ParseException {
        return s;
    }

    @Override
    public String print(String s, Locale locale) {
            String msg = messageService.getAllBundles().getMessage("validation.result.source." + s);
            return msg;
    }
}
