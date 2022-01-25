package pl.fhframework.compiler.core.rules.ts.generator;

import pl.fhframework.compiler.core.generator.MetaModelService;
import pl.fhframework.compiler.core.generator.ModuleMetaModel;
import pl.fhframework.compiler.core.generator.model.rule.RuleMm;

public class RuleNgCodeGenerator extends RuleMethodNgCodeGenerator {
    private final RuleMm rule;
    private final String classBaseName;

    public RuleNgCodeGenerator(RuleMm rule, ModuleMetaModel moduleMetaModel, MetaModelService metaModelService) {
        super(rule, moduleMetaModel, metaModelService);
        this.rule = rule;
        classBaseName = getBaseName(this.rule.getId());
    }

    @Override
    protected void generateClassBody() {
        generateClassSignature(rule);

        generateRuleMethod(rule.getId(), rule.getInputParams(), rule.getOutputParam(), methodSection);
    }

    private void generateClassSignature(RuleMm rule) {
        makeInjectable();

        classSignatureSection.addLine("export class %s", classBaseName);
    }
}
