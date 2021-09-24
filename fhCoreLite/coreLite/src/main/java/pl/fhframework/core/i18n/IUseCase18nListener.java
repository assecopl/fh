package pl.fhframework.core.i18n;

/**
 * Use case which listens for internationalization events.
 */
public interface IUseCase18nListener {

    /**
     * Called after current user session's language change.
     */
    public void onSessionLanguageChange();
}
