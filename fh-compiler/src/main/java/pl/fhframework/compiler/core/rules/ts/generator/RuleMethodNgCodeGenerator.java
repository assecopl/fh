package pl.fhframework.compiler.core.rules.ts.generator;

import pl.fhframework.compiler.core.generator.AbstractNgClassCodeGenerator;
import pl.fhframework.compiler.core.generator.MetaModelService;
import pl.fhframework.compiler.core.generator.ModuleMetaModel;
import pl.fhframework.compiler.core.generator.model.MetaModel;
import pl.fhframework.compiler.core.tools.JavaNamesUtils;
import pl.fhframework.compiler.core.uc.dynamic.model.element.attribute.ParameterDefinition;
import pl.fhframework.core.generator.GenerationContext;

import java.util.List;

public abstract class RuleMethodNgCodeGenerator extends AbstractNgClassCodeGenerator {

    public RuleMethodNgCodeGenerator(MetaModel metaModel, ModuleMetaModel moduleMetaModel, MetaModelService metaModelService) {
        super(moduleMetaModel, moduleMetaModel.getDependency(metaModel.getId()), metaModel, metaModelService);
    }

    protected void generateRuleMethod(String id, List<ParameterDefinition> inputParams, ParameterDefinition outputParam, GenerationContext methodSection) {
        generateMethodSignature(methodSection, JavaNamesUtils.normalizeMethodName(getBaseName(id)),
                inputParams, outputParam);
        // todo:
        if (outputParam != null) {
            methodSection.addLineWithIndent(1, "return null;");
        }
        methodSection.addLine("}");
    }
}
