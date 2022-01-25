package pl.fhframework.compiler.core.rules;

import org.junit.Assert;
import org.junit.Test;
import pl.fhframework.compiler.core.generator.RulesTypeProvider;
import pl.fhframework.compiler.core.rules.service.RulesServiceExtImpl;
import pl.fhframework.core.generator.GenericExpressionConverter;
import pl.fhframework.core.rules.service.RulesServiceImpl;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Piotr on 2017-09-15.
 */
public class RulesServiceImplTest {
    private GenericExpressionConverter expressionConverter = new GenericExpressionConverter();

    private RulesTypeProvider rulesTypeProvider = new RulesTypeProvider() {
        @Override
        public String getFullRuleName(String name) {
            return "pl.test." + name;
        }

        @Override
        public String getShortRuleName(String fullName) {
            return fullName.substring("pl.test.".length());
        }
    };

    private RulesServiceExtImpl rulesService = new RulesServiceExtImpl() {

        @Override
        protected RulesTypeProvider getRulesTypeProvider() {
            return rulesTypeProvider;
        }

        @Override
        protected GenericExpressionConverter getGenericExpressionConverter() {
            return new GenericExpressionConverter();
        }
    };

    @Test
    public void testSearchForRulesInExpression() {
        assertResolvedRules("RULE.pl.test.Rule()", new GenericExpressionConverter.SymbolInExpression("pl.test.Rule", 5, "Rule"));
        //                        ^^^^^^^^^^^^

        assertResolvedRules("RULE.Rule()", new GenericExpressionConverter.SymbolInExpression("Rule", 5, "Rule"));
        //                        ^^^^

        assertResolvedRules("RULE.pl.test.Rule(RULE.pl.test.RuleInner(test + 12))", new GenericExpressionConverter.SymbolInExpression("pl.test.Rule", 5, "Rule"), new GenericExpressionConverter.SymbolInExpression("pl.test.RuleInner", 23, "RuleInner"));
        //                        ^^^^^^^^^^^^      ^^^^^^^^^^^^^^^^^

        assertResolvedRules("RULE.pl.test.Rule(RULE.RuleInner(test + 12))", new GenericExpressionConverter.SymbolInExpression("pl.test.Rule", 5, "Rule"), new GenericExpressionConverter.SymbolInExpression("RuleInner", 23, "RuleInner"));
        //                        ^^^^^^^^^^^^      ^^^^^^^^^

        assertResolvedRules("!RULE.pl.test.Rule()", new GenericExpressionConverter.SymbolInExpression("pl.test.Rule", 6, "Rule"));
        //                         ^^^^^^^^^^^^

        assertResolvedRules("test.prop.RULE.pl.test.Rule()");
        //                   ??????????     XXXXXXXXXXXX    this is not a rule

        assertResolvedRules("test.prop + RULE.pl.test.Rule() + method1(test, RULE.pl.test.RuleTwo().next.next())", new GenericExpressionConverter.SymbolInExpression("pl.test.Rule", 17, "Rule"), new GenericExpressionConverter.SymbolInExpression("pl.test.RuleTwo", 53, "RuleTwo"));
        //                                    ^^^^^^^^^^^^                        ^^^^^^^^^^^^^^^
    }

    @Test
    public void testConvertToFullRulesInExpression() {
        assertToFullAndShort("RULE.Rule()", "RULE.pl.test.Rule()");
        assertToFullAndShort("RULE.Rule() + RULE.Rule2()", "RULE.pl.test.Rule() + RULE.pl.test.Rule2()");
        assertToFullAndShort("  prop.meth(1,   RULE.Rule(  ))", "  prop.meth(1,   RULE.pl.test.Rule(  ))");
        assertToFullAndShort("test.prop.RULE.pl.test.Rule()", "test.prop.RULE.pl.test.Rule()"); // same
    }

    private void assertResolvedRules(String expression, GenericExpressionConverter.SymbolInExpression... rules) {
        List<GenericExpressionConverter.SymbolInExpression> detectedRules = rulesService.searchCalledRules(expression, false);
        Assert.assertEquals(Arrays.asList(rules), detectedRules);
    }

    private void assertToFullAndShort(String shortExpr, String fullExpr) {
        Assert.assertEquals(fullExpr, rulesService.convertToFullNames(shortExpr));
        Assert.assertEquals(shortExpr, rulesService.convertToShortNames(fullExpr));
    }
}
