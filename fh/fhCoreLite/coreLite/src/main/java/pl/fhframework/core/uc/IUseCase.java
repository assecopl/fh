package pl.fhframework.core.uc;

import pl.fhframework.modules.services.IDescribableService;
import pl.fhframework.modules.services.ServiceHandle;
import pl.fhframework.SessionManager;
import pl.fhframework.UserSession;
import pl.fhframework.forms.IFieldsHighlightingList;
import pl.fhframework.model.PresentationStyleEnum;
import pl.fhframework.model.forms.AdHocForm;
import pl.fhframework.model.forms.Form;
import pl.fhframework.subsystems.Subsystem;

import java.util.List;

/**
 * Represents any use case. It delivers common API for all use cases.
 */
public interface IUseCase<C extends IUseCaseOutputCallback> {

    /**
     * Returns user session data
     *
     * @return user session data
     */
    default UserSession getUserSession() {
        return SessionManager.getUserSession();
    }

    /**
     * Runs use case with no inputs and specified output callback.
     *
     * @param useCaseClass use case class to be run
     * @param callback     callback object to be called on use case exit
     */
    default <C extends IUseCaseOutputCallback, U extends IUseCaseNoInput<C>> void runUseCase(Class<? extends U> useCaseClass, C callback) {
        getUserSession().getUseCaseContainer().runUseCase(useCaseClass, callback);
    }

    default <C extends IUseCaseOutputCallback, U extends IUseCaseNoInput<C> & IDescribableService<?>> void runUseCase(ServiceHandle<U, ?> serviceHandle, C callback) {
        getUserSession().getUseCaseContainer().runUseCase(serviceHandle.getServiceClassName(), new Object[]{}, callback);
    }

    /**
     * Runs use case with one input and specified output callback.
     *
     * @param useCaseClass use case class to be run
     * @param inputData    use case input
     * @param callback     callback object to be called on use case exit
     */
    default <INPUT, C extends IUseCaseOutputCallback, U extends IUseCaseOneInput<INPUT, C>> void runUseCase
    (Class<? extends U> useCaseClass, INPUT inputData, C callback) {
        getUserSession().getUseCaseContainer().runUseCase(useCaseClass, inputData, callback);
    }

    default <INPUT, C extends IUseCaseOutputCallback, U extends IUseCaseOneInput<INPUT, C> & IDescribableService<?>> void runUseCase(ServiceHandle<U, ?> serviceHandle, INPUT inputData, C callback) {
        getUserSession().getUseCaseContainer().runUseCase(serviceHandle.getServiceClassName(), new Object[]{inputData}, callback);
    }

    /**
     * Runs use case with two inputs and specified output callback.
     *
     * @param useCaseClass use case class to be run
     * @param inputData1    first use case input
     * @param inputData2    second use case input
     * @param callback     callback object to be called on use case exit
     */
    default <INPUT1, INPUT2, C extends IUseCaseOutputCallback, U extends IUseCaseTwoInput<INPUT1, INPUT2, C>> void runUseCase
    (Class<? extends U> useCaseClass, INPUT1 inputData1, INPUT2 inputData2, C callback) {
        getUserSession().getUseCaseContainer().runUseCase(useCaseClass, inputData1, inputData2, callback);
    }

    default <INPUT1, INPUT2, C extends IUseCaseOutputCallback, U extends IUseCaseTwoInput<INPUT1, INPUT2, C> & IDescribableService<?>> void runUseCase
    (ServiceHandle<U, ?> serviceHandle, INPUT1 inputData1, INPUT2 inputData2, C callback) {
        getUserSession().getUseCaseContainer().runUseCase(serviceHandle.getServiceClassName(), new Object[]{inputData1, inputData2}, callback);
    }

    /**
     * Runs sub use case with no inputs and specified output callback.
     *
     * @param useCaseClass use case class to be run as a sub use case
     * @param callback     callback object to be called on sub use case exit
     */
    default <C extends IUseCaseOutputCallback, U extends IUseCaseNoInput<C>> void runSubUseCase(Class<? extends U> useCaseClass, C callback) {
        getUserSession().getUseCaseContainer().runSubUseCase(useCaseClass, callback);
    }

    default <C extends IUseCaseOutputCallback, U extends IUseCaseNoInput<C> & IDescribableService<?>> void runSubUseCase(ServiceHandle<U, ?> serviceHandle, C callback) {
        getUserSession().getUseCaseContainer().runSubUseCase(serviceHandle.getServiceClassName(), new Object[]{}, callback);
    }

    /**
     * Runs sub use case with one input and specified output callback.
     *
     * @param useCaseClass use case class to be run as a sub use case
     * @param inputData    sub use case input
     * @param callback     callback object to be called on sub use case exit
     */
    default <INPUT, C extends IUseCaseOutputCallback, U extends IUseCaseOneInput<INPUT, C>> void runSubUseCase
    (Class<? extends U> useCaseClass, INPUT inputData, C callback) {
        getUserSession().getUseCaseContainer().runSubUseCase(useCaseClass, inputData, callback);
    }

    default <INPUT, C extends IUseCaseOutputCallback, U extends IUseCaseOneInput<INPUT, C> & IDescribableService<?>> void runSubUseCase
    (ServiceHandle<U, ?> serviceHandle, INPUT inputData, C callback) {
        getUserSession().getUseCaseContainer().runSubUseCase(serviceHandle.getServiceClassName(), new Object[]{inputData}, callback);
    }

    /**
     * Runs sub use case with two input2 and specified output callback.
     *
     * @param useCaseClass use case class to be run as a sub use case
     * @param inputData1   sub use case input 1
     * @param inputData2   sub use case input 2
     * @param callback     callback object to be called on sub use case exit
     */
    default <INPUT1, INPUT2, C extends IUseCaseOutputCallback, U extends IUseCaseTwoInput<INPUT1, INPUT2, C>> void runSubUseCase
    (Class<? extends U> useCaseClass, INPUT1 inputData1, INPUT2 inputData2, C callback) {
        getUserSession().getUseCaseContainer().runSubUseCase(useCaseClass, inputData1, inputData2, callback);
    }

    default <INPUT1, INPUT2, C extends IUseCaseOutputCallback, U extends IUseCaseTwoInput<INPUT1, INPUT2, C> & IDescribableService<?>> void runSubUseCase
    (ServiceHandle<U, ?> serviceHandle, INPUT1 inputData1, INPUT2 inputData2, C callback) {
        getUserSession().getUseCaseContainer().runSubUseCase(serviceHandle.getServiceClassName(), new Object[]{inputData1, inputData2}, callback);
    }

    /**
     * Runs sub use case with one input and no output callback.
     *
     * @param useCaseClass use case class to be run as a sub use case
     * @param inputData    sub use case input
     */
    default <INPUT, C extends IUseCaseNoCallback, U extends IUseCaseOneInput<INPUT, IUseCaseNoCallback>> void runSubUseCase
    (Class<? extends U> useCaseClass, INPUT inputData) {
        getUserSession().getUseCaseContainer().runSubUseCase(useCaseClass, inputData, (C) IUseCaseNoCallback.getCallback());
    }

    default <INPUT, C extends IUseCaseNoCallback, U extends IUseCaseOneInput<INPUT, IUseCaseNoCallback> & IDescribableService<?>> void runSubUseCase
    (ServiceHandle<U, ?> serviceHandle, INPUT inputData) {
        getUserSession().getUseCaseContainer().runSubUseCase(serviceHandle.getServiceClassName(), new Object[]{inputData}, (C) IUseCaseNoCallback.getCallback());
    }

    /**
     * Runs sub use case with two input and no output callback.
     *
     * @param useCaseClass use case class to be run as a sub use case
     * @param inputData1   sub use case input 1
     * @param inputData2   sub use case input 2
     */
    default <INPUT1, INPUT2, C extends IUseCaseNoCallback, U extends IUseCaseTwoInput<INPUT1, INPUT2, IUseCaseNoCallback>> void runSubUseCase
    (Class<? extends U> useCaseClass, INPUT1 inputData1, INPUT2 inputData2) {
        getUserSession().getUseCaseContainer().runSubUseCase(useCaseClass, inputData1, inputData2, (C) IUseCaseNoCallback.getCallback());
    }

    default <INPUT1, INPUT2, C extends IUseCaseNoCallback, U extends IUseCaseTwoInput<INPUT1, INPUT2, IUseCaseNoCallback> & IDescribableService<?>> void runSubUseCase
    (ServiceHandle<U, ?> serviceHandle, INPUT1 inputData1, INPUT2 inputData2) {
        getUserSession().getUseCaseContainer().runSubUseCase(serviceHandle.getServiceClassName(), new Object[]{inputData1, inputData2}, (C) IUseCaseNoCallback.getCallback());
    }


    /**
     * Runs sub use case with no input and no output callback.
     *
     * @param useCaseClass use case class to be run as a sub use case
     */
    default <C extends IUseCaseOutputCallback, U extends IUseCaseNoInput<C>> void runSubUseCase
    (Class<? extends U> useCaseClass) {
        getUserSession().getUseCaseContainer().runSubUseCase(useCaseClass, (C) IUseCaseNoCallback.getCallback());
    }

    default <C extends IUseCaseOutputCallback, U extends IUseCaseNoInput<C> & IDescribableService<?>> void runSubUseCase
    (ServiceHandle<U, ?> serviceHandle) {
        getUserSession().getUseCaseContainer().runSubUseCase(serviceHandle.getServiceClassName(), new Object[]{}, (C) IUseCaseNoCallback.getCallback());
    }

    /**
     * Presents form to the user.
     *
     * @param formClazz form class
     * @param model     model object to be supplied to the form
     * @return form instance
     */
    default <T, F extends Form<T>> F showForm(Class<F> formClazz, T model) {
        return getUserSession().getUseCaseContainer().showForm(this, formClazz, model);
    }

    /**
     * Presents form in the given variant to the user.
     *
     * @param formClazz   form class
     * @param model       model object to be supplied to the form
     * @param formVariant form variant
     * @return form instance
     */
    default <T, F extends Form<T>> F showForm(Class<F> formClazz, T model, String formVariant) {
        return getUserSession().getUseCaseContainer().showForm(this, formClazz, model, formVariant);
    }

    /**
     * Hides form instance
     *
     * @param form form instance to be hidden
     */
    default void hideForm(Form form) {
        getUserSession().getUseCaseContainer().hideForm(this, form);
    }

    /**
     * Reports validation error
     */
    //TODO: Upper beam can't report errors.
    default void reportValidationError(Object parent, String attributeName, String message, PresentationStyleEnum errorStyle) {
        getUserSession().getValidationResults().addCustomMessage(parent, attributeName, message, errorStyle);
    }

    /**
     * Reports validation error based on keyMessage, NOT full message. Key should look like this:
     * "fh.docs.validation.example.msg", without quotes.
     */
    default void reportValidationTemplateError(Object parent, String attributeName, String keyMessage, PresentationStyleEnum errorStyle) {
        getUserSession().getValidationResults().addCustomTemplateMessage(parent, attributeName, keyMessage, errorStyle);
    }

    /**
     * Reports validation error based on keyMessage, NOT full message. Key should look like this:
     * "fh.docs.validation.example.msg", without quotes.
     */
    default void reportValidationTemplateError(Object parent, String attributeName, String keyMessage, Object[] args, PresentationStyleEnum errorStyle) {
        getUserSession().getValidationResults().addCustomTemplateMessage(parent, attributeName, keyMessage, args, errorStyle);
    }


    /**
     * Returns callback given to this use case instance during use case instantiation.
     *
     * @return callback instance
     */
    default C exit() {
        return getUserSession().getUseCaseContainer().getCallback(this, IUseCaseOutputCallback.class);
    }


    //TODO:SSO Methods below move to other interface.

    /**
     * Presents form to the user.
     *
     * @param form form instance
     */
    default void showForm(Form form) {
        getUserSession().getUseCaseContainer().showForm(this, form);
    }

    /**
     * @param formId
     * @param model
     * @return
     */
    default Form showForm(String formId, Object model) {
        return showForm(formId, model, "");
    }

    default Form showForm(String formId, Object model, String variantId) {
        return getUserSession().getUseCaseContainer().showForm(this, formId, model, variantId);
    }

    /**
     * Runs an action with the given name (launches the method annotated as "Action").
     *
     * @param actionName      Action Name.
     * @param attributesValue Value of Attributes.
     */
    default void runAction(String actionName, Object... attributesValue) {
        if (attributesValue == null) {
            attributesValue = new Object[]{null};
        }
        getUserSession().getUseCaseContainer().getCurrentUseCaseContext().runAction(actionName, null, attributesValue);
    }

    /**
     * Runs an action on given form type with the given name (launches the method annotated as "Action").
     *
     * @param actionName      Action Name.
     * @param formTypeId      Form Type Id.
     * @param attributesValue Value of Attributes.
     */
    default void runEvent(String actionName, String formTypeId, Object... attributesValue) {
        if (attributesValue == null) {
            attributesValue = new Object[]{null};
        }
        getUserSession().getUseCaseContainer().getCurrentUseCaseContext().runAction(actionName, formTypeId, attributesValue);
    }

    default Subsystem getSubsystem() {
        return getUserSession().getUseCaseContainer().getCurrentUseCaseContext().getSubsystem();
    }

    // DO NOT REMOVE - used in generated classes
    default Form getActiveForm() {
        List<Form<?>> forms = getUserSession().getUseCaseContainer().getUseCaseActiveForms(this);
        return !forms.isEmpty() ? forms.get(0) : null;
    }

    default <I extends ICustomUseCase> void runUseCase(I usecase) {
        getUserSession().getUseCaseContainer().runUseCase(usecase);
    }

    default <I extends ICustomUseCase> void runSubUseCase(I usecase) {
        getUserSession().getUseCaseContainer().runSubUseCase(usecase);
    }

    /**
     * Return fields highlighting container for top form
     *
     * @return fields highlighting container for top form
     */
    default IFieldsHighlightingList getFieldsHighlightingList() {
        return getActiveForm().getFieldsHighlightingList();
    }

    /**
     * Return fields highlighting container for form
     * @param form form
     * @return fields highlighting container for form
     */
    default IFieldsHighlightingList getFieldsHighlightingList(Form form) {
        return form.getFieldsHighlightingList();
    }
}