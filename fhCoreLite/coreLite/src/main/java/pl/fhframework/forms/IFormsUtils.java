package pl.fhframework.forms;

import pl.fhframework.Binding;
import pl.fhframework.binding.ActionSignature;
import pl.fhframework.model.forms.Form;
import pl.fhframework.model.forms.IComponentBindingCreator;

import java.util.Set;
import java.util.function.Supplier;

/**
 * Created by pawel.ruta on 2018-09-07.
 */
public interface IFormsUtils {
    <M, F extends Form<M>> F createFormInstance(Class<F> formClass);

    <M, F extends Form<M>> F createFormInstance(Class<F> formClass, IComponentBindingCreator bindingCreator, Supplier<Binding> bindingMethods);

    Set<ActionSignature> getFormActions(Class<Form<?>> formClass);

    Class<? extends Form> getFormById(String formId);
}
