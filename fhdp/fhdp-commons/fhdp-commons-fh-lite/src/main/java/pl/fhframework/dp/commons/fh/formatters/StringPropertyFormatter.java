package pl.fhframework.dp.commons.fh.formatters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.Formatter;
import pl.fhframework.dp.commons.utils.lng.LngDescriptionUtil;
import pl.fhframework.SessionManager;
import pl.fhframework.core.i18n.MessageService;
import pl.fhframework.format.FhFormatter;

import java.text.ParseException;
import java.util.Locale;

/**
 * @author <a href="mailto:jacek_borowiec@skg.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 09/08/2020
 */
@FhFormatter("stringPropertyFormatter")
public class StringPropertyFormatter implements Formatter<String> {
    @Autowired
    MessageService messageService;

    @Override
    public String parse(String s, Locale locale) throws ParseException {
        return s;
    }

    @Override
    public String print(String s, Locale locale) {
        if(s.startsWith("$.")) {
            String msg = messageService.getAllBundles().getMessage(s.replace("$.", ""));
            return msg;
        } else {
            return LngDescriptionUtil.getDescription(s, SessionManager.getUserSession().getLanguage().toLanguageTag());
        }
    }
}
