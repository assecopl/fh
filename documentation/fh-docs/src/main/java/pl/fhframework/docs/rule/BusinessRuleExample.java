package pl.fhframework.docs.rule;

import pl.fhframework.core.rules.BusinessRule;

/**
 * Created by pawel.ruta on 2017-05-29.
 */
@BusinessRule
public class BusinessRuleExample {
    public String echo(String input) {
        return input;
    }
}
