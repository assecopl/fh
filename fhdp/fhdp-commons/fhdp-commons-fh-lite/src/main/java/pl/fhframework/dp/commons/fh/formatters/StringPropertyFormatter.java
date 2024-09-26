package pl.fhframework.dp.commons.fh.formatters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.Formatter;
import pl.fhframework.SessionManager;
import pl.fhframework.UserSession;
import pl.fhframework.core.i18n.MessageService;
import pl.fhframework.dp.commons.utils.lng.LngDescriptionUtil;
import pl.fhframework.format.FhFormatter;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:jacek.borowiec@asseco.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 09/08/2020
 */
@FhFormatter("stringPropertyFormatter")
public class StringPropertyFormatter implements Formatter<String> {
    @Autowired
    MessageService messageService;

    @Value("${fhframework.language.viewRawKeyMode:false}")
    private Boolean viewRawKeyMode = false;
    @Value("${fhframework.language.default:pl}")
    private String defaultLanguage;

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
                // Error in fh: locale parameter is always pl_PL
                msg = messageService.getAllBundles().getMessage(msgKey,
                        finalParams.toArray(new String[0]), getUserLanguage(), "?:" + msgKey);
            }
            return msg;
        } else if(key.startsWith("enum.")) {
            return messageService.getAllBundles().getMessage(key);
        }
        else {
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

    private Locale getUserLanguage() {
        UserSession session = SessionManager.getUserSession();
        if (session != null) {
            return session.getLanguage();
        } else {
            return getLanguageOrDefault(null);
        }
    }

    private Locale getLanguageOrDefault(Locale locale) {
        if (locale == null) {
            return Locale.forLanguageTag(defaultLanguage);
        }
        return locale;
    }

    private String getDefaultMessage(String key) {
        if(viewRawKeyMode || getUserLanguage() != null && getUserLanguage().getLanguage() == MessageService.LOCALE_VIEWKEY){
            return key;
        }
        return "Key: " + key + " not found";
    }

}
