package pl.fhframework.compiler.core.generator.model.spel;

import org.springframework.expression.spel.ast.MethodReference;
import org.springframework.expression.spel.ast.SpelNodeImpl;
import pl.fhframework.compiler.core.generator.FhServicesTypeProvider;

import java.util.List;

public class ServiceNode extends RuleNode {
    public ServiceNode(SpelNodeImpl service, String id, String method, List<SpelNodeImpl> children, MethodReference methodReference) {
        super(service, id, method, children, methodReference);
    }

    @Override
    protected String getPrefixName() {
        return FhServicesTypeProvider.SERVICE_PREFIX;
    }
}
