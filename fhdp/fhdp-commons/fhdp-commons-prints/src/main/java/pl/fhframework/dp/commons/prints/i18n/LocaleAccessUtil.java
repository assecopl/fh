package pl.fhframework.dp.commons.prints.i18n;
/**
 * Klasa przechowująca ustawienia regionalne (locale) bieżącego wątku
 * @author Paweł Kasprzak
 *
 * @version $Revision: 1551 $, $Date: 2019-01-17 11:59:03 +0100 (Cz, 17 sty 2019) $
 * @since 2009.03.06 <a href="mailto:dariusz_skrudlik@skg.pl">Dariusz Skrudlik</a>
 */
public class LocaleAccessUtil {
    
    private static final ThreadLocal<java.util.Locale> threadLocal = new ThreadLocal<java.util.Locale>();
    
    /**
     * Set locale for current session/thread.
     * @param locale locale do ustawienia
     */
    public static void setLocale(java.util.Locale locale) {
        threadLocal.set(locale != null ? locale  : java.util.Locale.getDefault());
    }
    
    /**
     * Return locale from storage for current session/thread.
     * @return Locale for current thread
     */
    public static java.util.Locale getLocale() {
        return threadLocal.get() != null ? threadLocal.get() : java.util.Locale.getDefault();
    }

    /**
     * Remove locale from storage for current session/thread.
     */
    public static void closeSession() {
        threadLocal.set(null);
    }

     /**
     * Return true if locale exist in storage for current session/thread.
     * @return true - existing locale
     */
    public static boolean isLocale() {
        return threadLocal.get() != null ;
    }

}
