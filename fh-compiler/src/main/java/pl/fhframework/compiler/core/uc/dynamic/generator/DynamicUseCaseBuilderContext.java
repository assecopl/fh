package pl.fhframework.compiler.core.uc.dynamic.generator;

import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by pawel.ruta on 2017-03-24.
 */
@Getter
@Setter
public class DynamicUseCaseBuilderContext {
    private UseCaseElementsCollection elementsCollection;

    private List<String> innerStatic = new LinkedList<>();

    public String mapMethodName(String methodName) {
        return getElementsCollection().getActionsNameMap().getOrDefault(methodName, methodName);
    }
}
