package pl.fhframework.compiler.core.rules.service;

import pl.fhframework.compiler.core.generator.RuleMethodDescriptor;
import pl.fhframework.compiler.core.rules.dynamic.model.Rule;
import pl.fhframework.compiler.core.rules.dynamic.model.RuleType;
import pl.fhframework.compiler.core.uc.dynamic.model.VariableContext;
import pl.fhframework.core.dynamic.DynamicClassName;
import pl.fhframework.core.rules.dynamic.model.Statement;
import pl.fhframework.core.rules.service.RulesService;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Created by pawel.ruta on 2017-06-21.
 */
public interface RulesServiceExt extends RulesService {
    Set<DynamicClassName> resolveCalledRules(String expression);

    boolean isRuleRunnable(String ruleName);

    Collection<? extends VariableContext> getAvailableVars(Statement statement, Rule rule);

    Collection<? extends VariableContext> getInputVars();

    String shortNameToFullName(String shortRuleName);

    List<RuleMethodDescriptor> getValidationRules();

    List<RuleMethodDescriptor> getBusinessRules();

    List<RuleType> getRuleTypes();

    Set<DynamicClassName> provideDependencies(Rule rule);
}
