package pl.fhframework.core.util;

import pl.fhframework.core.logging.FhLogger;

public class CallerResolver extends SecurityManager {

    public Class getCallerClass(Class myClass) {
        // In case this is not working on application server, uncomment and use getCallerClassNameStackTrace.
        Class[] classContext = getClassContext();
        for (int i = 1; i < classContext.length; i++) {
            Class ste = classContext[i];
            if (ste != myClass && ste != CallerResolver.class) {
                return ste;
            }
        }
        return FhLogger.class;
    }
}