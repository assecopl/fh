package pl.fhframework.compiler.core.uc.dynamic.model;

import lombok.Data;
import pl.fhframework.compiler.core.dynamic.DynamicClassMetadata;

@Data
public class DynamicUseCaseMetadata extends DynamicClassMetadata {

    private UseCase dynamicUseCase;

}
