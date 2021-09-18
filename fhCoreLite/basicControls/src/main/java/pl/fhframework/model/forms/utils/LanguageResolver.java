package pl.fhframework.model.forms.utils;

import pl.fhframework.UserSession;
import pl.fhframework.model.dto.ElementChanges;

import java.util.Locale;
import java.util.Objects;

/**
 * Helper class for resolving language from user session.
 */
public class LanguageResolver {

    public static String languageChanges(UserSession userSession, String currentLanguage, ElementChanges elementChange) {
        Locale languageLocale = userSession.getLanguage();
        if (languageLocale != null && !Objects.equals(languageLocale.getLanguage(), currentLanguage)) {
            currentLanguage = languageLocale.getLanguage();
            elementChange.addChange("language", currentLanguage);
        }
        return currentLanguage;
    }

}
