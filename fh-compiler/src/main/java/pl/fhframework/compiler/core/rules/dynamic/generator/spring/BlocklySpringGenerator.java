package pl.fhframework.compiler.core.rules.dynamic.generator.spring;

import pl.fhframework.compiler.core.dynamic.dependency.DependenciesContext;
import pl.fhframework.compiler.core.rules.dynamic.generator.BlocklyJavaGenerator;
import pl.fhframework.compiler.core.uc.dynamic.model.element.attribute.ParameterDefinition;
import pl.fhframework.core.generator.GenerationContext;
import pl.fhframework.core.rules.dynamic.model.Statement;
import pl.fhframework.core.rules.dynamic.model.ValidationMessage;
import pl.fhframework.core.rules.dynamic.model.dataaccess.From;
import pl.fhframework.core.util.StringUtils;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class BlocklySpringGenerator extends BlocklyJavaGenerator {
    public BlocklySpringGenerator(List<ParameterDefinition> inputParams, List<Statement> statements, List<ParameterDefinition> outputParams, DependenciesContext dependenciesContext) {
        super(inputParams, statements, outputParams, dependenciesContext);
    }

    @Override
    protected void generateFrom(From from, Map<String, Type> contextVars, GenerationContext fromSection, boolean existsExpression) {
        if (StringUtils.isNullOrEmpty(from.getCollection())) {
            // todo: Database access using Spring Data Repository
        }
        else {
            super.generateFrom(from, contextVars, fromSection, existsExpression);
        }
    }

    @Override
    protected void generateValidationMessage(ValidationMessage validationMessage, Map<String, Type> contextVars, GenerationContext body) {
        // todo:
    }
}
