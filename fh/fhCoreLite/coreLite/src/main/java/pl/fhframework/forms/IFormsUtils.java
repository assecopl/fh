package pl.fhframework.forms;

import pl.fhframework.binding.ActionSignature;
import pl.fhframework.model.forms.Form;

import java.util.Set;

/**
 * Created by pawel.ruta on 2018-09-07.
 */
public interface IFormsUtils {
    <M, F extends Form<M>> F createFormInstance(Class<F> formClass);

    Set<ActionSignature> getFormActions(Class<Form<?>> formClass);
}
