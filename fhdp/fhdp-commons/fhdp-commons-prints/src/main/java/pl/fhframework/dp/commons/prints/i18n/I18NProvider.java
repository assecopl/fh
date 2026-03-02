/*
 * JAPIS 2018.
 */
package pl.fhframework.dp.commons.prints.i18n;

import java.util.Locale;

/**
 * I18NProvider
 *
 * @author <a href="mailto:pawel_kasprzak@skg.pl">Paweł Kasprzak</a>
 * @version $Revision: 2674 $, $Date: 2019-01-17 11:59:03 +0100 (czw., 17 sty
 * 2019) $
 */
public interface I18NProvider {

    String getString(Locale locale, String key, Object... params);

    default String getString(String key, Object... params) {
        return getString(LocaleAccessUtil.getLocale(), key, params);
    }

    default String getString(Class prefix, String key, Object... params) {
        return getString(LocaleAccessUtil.getLocale(), getKey(prefix, key), params);
    }

    default String getString(Enum object) {
        return object != null ? getString(getKey(object)) : null;
    }

    default String getString(Locale locale, Enum object) {
        return object != null ? getString(locale, getKey(object)) : null;
    }
    
    static String getKey(Enum object) {
        return getKey(object != null ? object.getClass() : null, object != null ? object.name() : null);
    }

    static String getKey(Class clazz, String key) {
        return clazz != null && key != null ? (clazz.getName() + "." + key) : null;
    }

    static String getNoExistingKeyString(Locale locale, String key) {
        return "???" + (locale != null ? locale.getLanguage() : null) + ":" + key + "???";
    }

    static boolean isNoExistingKeyString(String value) {
        return value != null
                && value.startsWith("???")
                && value.endsWith("???");
    }
}