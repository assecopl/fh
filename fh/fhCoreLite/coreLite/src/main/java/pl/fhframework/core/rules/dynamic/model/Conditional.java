package pl.fhframework.core.rules.dynamic.model;

import java.io.Serializable;

/**
 * Created by pawel.ruta on 2017-06-20.
 */
public interface Conditional extends Block {
    Condition getCondition();

    Then getThen();
}
