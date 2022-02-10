package pl.fhframework.compiler.core.model;

import lombok.Data;
import pl.fhframework.compiler.core.dynamic.DynamicClassMetadata;
import pl.fhframework.compiler.core.model.meta.ClassTag;

@Data
public class DynamicModelMetadata extends DynamicClassMetadata {

    private ClassTag dynamicClass;

}
