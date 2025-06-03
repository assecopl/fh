package pl.fhframework.compiler.core.rules.dynamic.generator;

import pl.fhframework.compiler.core.dynamic.dependency.DependenciesContext;
import pl.fhframework.compiler.core.dynamic.dependency.DependencyResolution;
import pl.fhframework.compiler.core.rules.dynamic.model.Rule;
import pl.fhframework.compiler.core.uc.dynamic.model.element.attribute.ParameterDefinition;
import pl.fhframework.core.dynamic.DynamicClassName;
import pl.fhframework.core.util.StringUtils;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

// todo:
public class BlocklyPlPgSqlGenerator extends BlocklyFhGenerator {
    private final String targetClassName;

    public BlocklyPlPgSqlGenerator(Rule rule, DependenciesContext dependenciesContext, String targetClassName) {
        super(rule, dependenciesContext);
        this.targetClassName = targetClassName;
    }

    @Override
    public void generateBody() {
        generateOutputVar();

        Map<String, Type> contextVars = initVars();

        String typeStr = rule.getOutputParams().stream().findFirst().orElse(null).getType();

        DependencyResolution dependencyResolution = dependenciesContext.resolve(DynamicClassName.forClassName(typeStr));

        String tempSql = String.format("select * from %s(:typeName)", StringUtils.firstLetterToLower(targetClassName));
        body.addLine("%s<%s, %s> inArgs = new %s<>();", toTypeLiteral(Map.class), toTypeLiteral(String.class), toTypeLiteral(Object.class), toTypeLiteral(HashMap.class));
        body.addLine("inArgs.put(\"typeName\", \"%s\");", typeStr);

        String callFunction = String.format("__storeAccessService.callFunction(\"%s\", inArgs, %s.class)", tempSql, dependencyResolution.getFullClassName());

        ParameterDefinition output = rule.getOutputParams().stream().findFirst().orElse(null);
        if (output != null) {
            body.addLine(generateExpression(output.getName(), callFunction, contextVars) + ";");
        } else {
            body.addLine(callFunction + ";");
        }

        generateReturn(null, contextVars, body);
    }
}
