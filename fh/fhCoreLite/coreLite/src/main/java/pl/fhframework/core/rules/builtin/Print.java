package pl.fhframework.core.rules.builtin;

import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.core.rules.BusinessRule;

/**
 * Created by pawel.ruta on 2017-05-29.
 */
@BusinessRule
public class Print {
    public void print(String input) {
        FhLogger.info(this.getClass(), input);
    }
}
