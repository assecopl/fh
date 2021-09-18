package pl.fhframework.core.rules.service;

import pl.fhframework.core.dynamic.DynamicClassName;
import pl.fhframework.core.generator.IExpressionConverter;

import java.util.Set;

/**
 * Created by pawel.ruta on 2017-06-21.
 */
public interface RulesService {
    <T> T runRule(String ruleName, Object... args);

    String convertToFullNames(String expression);

    String convertToShortNames(String expression);

    void startRuleLookupCache();

    void stopRuleLookupCache();
}
