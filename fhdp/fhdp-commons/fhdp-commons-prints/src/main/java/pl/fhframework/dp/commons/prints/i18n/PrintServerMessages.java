/*
 * JAPIS 2020.
 */
package pl.fhframework.dp.commons.prints.i18n;



/**
 * PrintServerMessages
 *
 * @author <a href="mailto:pawel.kasprzak@asseco.pl">Paweł Kasprzak</a>
 * @version $Revision: 3701 $, $Date: 2020-11-13 12:22:23 +0100 (pią) $
 */
public class PrintServerMessages extends SimplePropertiesI18NProvider {

    private static PrintServerMessages i18NProvider;

    public PrintServerMessages() {
        super("print-server-messages");
    }

    public PrintServerMessages(String... bundleNames) {
        super(bundleNames);
    }

    public PrintServerMessages(I18NProvider parentProvider, String... bundleNames) {
        super(parentProvider, bundleNames);
    }

    public PrintServerMessages(I18NProvider[] parentProviders, String[] bundleNames) {
        super(parentProviders, bundleNames);
    }

    public static PrintServerMessages get() {
        if (i18NProvider == null) {
            i18NProvider = new PrintServerMessages();
        }
        return i18NProvider;
    }

}
