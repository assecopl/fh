package pl.fhframework.docs.rule;

import pl.fhframework.core.rules.BusinessRule;
import pl.fhframework.core.rules.FilteringRule;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.docs.model.DocsExampleMo;

/**
 * Created by pawel.ruta on 2017-05-29.
 */
@FilteringRule
public class FilteringRuleExample {
    public boolean notEmptyStringFiled(DocsExampleMo exampleMo) {
        return !StringUtils.isNullOrEmpty(exampleMo.getStringField());
    }
}
