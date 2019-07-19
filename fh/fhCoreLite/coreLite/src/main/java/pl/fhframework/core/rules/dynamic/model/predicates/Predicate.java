package pl.fhframework.core.rules.dynamic.model.predicates;

import pl.fhframework.core.rules.dynamic.model.Condition;
import pl.fhframework.core.rules.dynamic.model.RuleElement;

/**
 * Created by pawel.ruta on 2017-08-18.
 */
public interface Predicate extends RuleElement {
    Condition getWhen();
}
