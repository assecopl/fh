package pl.fhframework.events;

/**
 * Defines form control that is aware of user language change
 */
public interface I18nFormElement {
    /**
     * Method called after user language is changed
     * @param lang Language code
     */
    void onSessionLanguageChange(String lang);
}
