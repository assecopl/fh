package pl.fhframework.model.forms;

import pl.fhframework.binding.ModelBinding;

import java.lang.reflect.Field;

public interface IComponentBindingCreator {
    ModelBinding create(Component component, String attrValue, Field field);
}
