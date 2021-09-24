package pl.fhframework.forms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import pl.fhframework.Binding;
import pl.fhframework.core.FhFormException;
import pl.fhframework.ReflectionUtils;
import pl.fhframework.binding.ActionSignature;
import pl.fhframework.model.forms.Form;
import pl.fhframework.model.forms.IComponentBindingCreator;

import java.lang.reflect.Field;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Forms manager which creates new form instances from XML or compiled classes. Manages compiled classes' cache.
 */
@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON) // must be a singleton
public class FormsUtils implements IFormsUtils {
    public static final String FORM_ACTIONS_FIELD = "____actions";
    public static final String EXTENDED_SUFIX = "__View";
    @Autowired
    private ApplicationContext applicationContext;

    /**
     * Creates form instance based on form's class.
     * @param formClass form's class
     * @param <M> type of model
     * @param <F> type of form
     * @return new form instance
     */
    @Override
    public <M, F extends Form<M>> F createFormInstance(Class<F> formClass) {
            return createFormInstance(formClass, null, null);
    }

    /**
     * Creates form instance based on form's class.
     * @param formClass form's class
     * @param <M> type of model
     * @param <F> type of form
     * @return new form instance
     */
    @Override
    public <M, F extends Form<M>> F createFormInstance(Class<F> formClass, IComponentBindingCreator bindingCreator, Supplier<Binding> bindingMethods) {
            return constructNewInstance(formClass, bindingCreator, bindingMethods);
    }

    /**
     * Returns declared form's actions signatures
     * @param formClass real form class
     * @return signatures of actions set
     */
    @Override
    public Set<ActionSignature> getFormActions(Class<Form<?>> formClass) {
        Field actionsField = org.springframework.util.ReflectionUtils.findField(formClass, FORM_ACTIONS_FIELD);
        if (actionsField == null) {
            throw new FhFormException(formClass.getName() + " class doesn't have form's action list compiled in");
        }
        return (Set<ActionSignature>) org.springframework.util.ReflectionUtils.getField(actionsField, formClass);
    }

    /**
     * Created a new instance of a provided form class and injects Spring dependencies to it.
     */
    private <F extends Form<?>> F constructNewInstance(Class<F> formClass, IComponentBindingCreator bindingCreator, Supplier<Binding> bindingMethods) {
        try {
            Class<F> extendedClass = (Class<F>) ReflectionUtils.tryGetClassForName(formClass.getName() + EXTENDED_SUFIX);
            if (extendedClass != null) {
                formClass = extendedClass;
            }
            F instance;
            if (applicationContext != null) {
                instance = applicationContext.getAutowireCapableBeanFactory().createBean(formClass);
                if (bindingCreator != null) {
                    instance.setComponentBindingCreator(bindingCreator);
                }
                if (bindingMethods != null) {
                    instance.setBindingMethodsCreator(bindingMethods);
                    instance.setBindingMethods(bindingMethods.get());
                }
            } else {
                // useful in maven
                instance = ReflectionUtils.newInstance(formClass, bindingCreator, bindingMethods);
            }
            return instance;
        } catch (Throwable e) {
            throw new FhFormException("Error creating new form instance " + formClass.getName(), e);
        }
    }

    @Override
    public Class<? extends Form> getFormById(String formId) {
        return (Class<? extends Form>) ReflectionUtils.getClassForName(formId);
    }
}
