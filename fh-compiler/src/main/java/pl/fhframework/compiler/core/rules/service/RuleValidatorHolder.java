package pl.fhframework.compiler.core.rules.service;

import lombok.Data;
import pl.fhframework.compiler.core.rules.dynamic.model.Rule;
import pl.fhframework.core.rules.dynamic.model.RuleElement;
import pl.fhframework.core.rules.dynamic.model.StatementsList;
import pl.fhframework.validation.IValidationResults;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

/**
 * Created by pawel.ruta on 2017-06-30.
 */
@Data
public class RuleValidatorHolder {
    private Rule rule;

    private IValidationResults validationResults;

    private RulesServiceExtImpl rulesService;

    private Map<RuleElement, StatementsList> childParentMap = new LinkedHashMap<>();

    private Stack<Map<String, Type>> contextVars = new Stack<>();

    private Set<String> validatedRules;

    public RuleValidatorHolder(Rule rule, IValidationResults validationResults, RulesServiceExtImpl rulesService, Set<String> validatedRules) {
        this.rule = rule;
        this.validationResults = validationResults;
        this.rulesService = rulesService;
        this.validatedRules = validatedRules;
    }

    public void pushContextCopy() {
        getContextVars().push(new LinkedHashMap<>(getContextVars().peek()));
    }
}
