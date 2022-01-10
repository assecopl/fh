package pl.fhframework.dp.commons.fh.formatters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.Formatter;
import pl.fhframework.dp.commons.utils.lng.LngDescriptionUtil;
import pl.fhframework.SessionManager;
import pl.fhframework.core.i18n.MessageService;
import pl.fhframework.format.FhFormatter;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

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
    public String print(String key, Locale locale) {
        if(key.startsWith("$.")) {
            String msg;
            String[] elements = key.split(Pattern.quote("\b"));
            if(elements.length == 1) {
                msg = messageService.getAllBundles().getMessage(key.replace("$.", ""));
            } else {
                String[] params = Arrays.copyOfRange(elements, 1, elements.length);
                List<String> finalParams = new ArrayList<>();
                for(String p: params) {
                    finalParams.add(resolve(p, locale));
                }
                String msgKey = elements[0].replace("$.", "");
                msg = messageService.getAllBundles().getMessage(msgKey,
                        finalParams.toArray(new String[0]), locale, "?:" + msgKey);
            }
            return msg;
        } else {
            return LngDescriptionUtil.getDescription(key, SessionManager.getUserSession().getLanguage().toLanguageTag());
        }
    }

    private String resolve(String p, Locale locale) {
        if(p.startsWith("$.")) {
            String pKey = p.replace("$.", "");
            return messageService.getAllBundles().getMessage(pKey,null, locale, "?:" + pKey);
        } else {
            return p;
        }
    }

}
