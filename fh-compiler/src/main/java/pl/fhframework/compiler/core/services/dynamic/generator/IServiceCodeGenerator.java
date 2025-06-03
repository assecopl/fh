package pl.fhframework.compiler.core.services.dynamic.generator;

import pl.fhframework.compiler.core.dynamic.dependency.DependenciesContext;
import pl.fhframework.compiler.core.services.dynamic.model.Operation;
import pl.fhframework.compiler.core.services.dynamic.model.Service;
import pl.fhframework.core.generator.GenerationContext;

import java.util.Set;

/**
 * Created by pawel.ruta on 2018-04-09.
 */
public interface IServiceCodeGenerator {
    default void generateClassAnnotations(Service service, GenerationContext classSignatureSection, DependenciesContext dependenciesContext) {}

    default void generateOperationAnnotations(Operation operation, Service service, GenerationContext methodSection, DependenciesContext dependenciesContext) {}

    void generateOperationSignature(Operation operation, Service service, GenerationContext methodSection, DependenciesContext dependenciesContext);

    void generateOperationBody(Operation operation, Service service, GenerationContext methodSection, DependenciesContext dependenciesContext);

    default void addServices(GenerationContext fieldSection, DependenciesContext dependenciesContext, Set<String> fieldsBean) {}
}
