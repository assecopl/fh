package pl.fhframework.core.rules;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.core.model.Model;

/**
 * Created by pawel.ruta on 2017-05-29.
 */
@Getter
@Setter
@Model
public class ValidationRuleResult {
    private boolean valid = true;

    private String messageKey;
}
