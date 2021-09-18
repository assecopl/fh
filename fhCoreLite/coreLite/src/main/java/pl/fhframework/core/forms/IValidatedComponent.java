package pl.fhframework.core.forms;

import com.fasterxml.jackson.annotation.JsonIgnore;

import pl.fhframework.binding.ModelBinding;
import pl.fhframework.model.PresentationStyleEnum;
import pl.fhframework.model.dto.ElementChanges;
import pl.fhframework.model.forms.Form;

import java.util.Collections;
import java.util.List;

/**
 * This interface is defines a one step of validating form components based on given changes. That
 * contains already resolved binding for components, but before they will be send back to
 * client(view).
 */
public interface IValidatedComponent {
    String PRESENTATION_STYLE_ATTR = "presentationStyle";

    /**
     * This method should provide validation for form component. This method runs in lifecycle after
     * binding for component is resolved. Developer can add custom properties to given changes.
     */
    void validate();

    /**
     * This method process component, based on validation result.
     *
     * @param elementChanges - changes for component, so dev can add any properties, like
     *                       presentation style or message error.
     */
    void prepareComponentAfterValidation(ElementChanges elementChanges);

    /**
     * Label of component for custom validation message.
     *
     * @return - its optional name of form component
     */
    String getLabel();

    /**
     * Binding of label used in validation messages. If not set, defaults to label or id (when label is also not set).
     * @return binding of label used in validation messages
     */
    ModelBinding<String> getValidationLabelModelBinding();

    @JsonIgnore
    ModelBinding getModelBinding();

    /**
     * @return - Current presentation style for validation.
     */
    PresentationStyleEnum getPresentationStyle();

    /**
     * Returns this component's id.
     * @return this component's id.
     */
    String getId();

    @JsonIgnore
    default List<ModelBinding> getAllBingings() {
        return Collections.emptyList();
    }

    default void skipSettingPresentation(ElementChanges elementChanges, Form form) {
        // we have to block here resetting presentationStyle if validation was not part of current action
        // todo: maybe in near future create more readable code
        if (!form.getAbstractUseCase().getUserSession().getActionContext().isValidate() || getPresentationStyle() == null) {
            return;
        }
        elementChanges.addChange(PRESENTATION_STYLE_ATTR, getPresentationStyle());
    }

}
