package pl.fhframework.compiler.forms;

import lombok.Data;
import pl.fhframework.compiler.core.dynamic.DynamicClassMetadata;
import pl.fhframework.core.dynamic.DynamicClassName;
import pl.fhframework.model.forms.Form;

import java.lang.reflect.Type;
import java.util.Collection;

/**
 * Created by Piotr on 2017-04-18.
 */
@Data
public class DynamicFormMetadata extends DynamicClassMetadata {

    private Class<? extends Collection> modelCollectionClass;

    private boolean useInternalModel;

    private DynamicClassName modelType;

    private Type modelFixedType;

    private Form<?> loadedForm;

    private Class<? extends Form> staticFormClass;
}
