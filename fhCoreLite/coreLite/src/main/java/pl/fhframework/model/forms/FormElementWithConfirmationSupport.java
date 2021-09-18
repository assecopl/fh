package pl.fhframework.model.forms;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import pl.fhframework.BindingResult;
import pl.fhframework.annotations.*;
import pl.fhframework.binding.*;
import pl.fhframework.model.dto.ElementChanges;
import pl.fhframework.model.dto.ValueChange;

import static pl.fhframework.annotations.DesignerXMLProperty.PropertyFunctionalArea.BEHAVIOR;

/**
 *  Base component with confirmation action support.
 */
public abstract class FormElementWithConfirmationSupport extends FormElement implements IChangeableByClient, Boundable {
    protected static final String CONFIRATION_MSG_ATTR = "confirmationMsg";

    @Getter
    private String confirmationMsg;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(value = CONFIRATION_MSG_ATTR)
    @DesignerXMLProperty(priority = 200, functionalArea = BEHAVIOR)
    @DocumentedComponentAttribute(boundable = true, value = "Binding represents value from confirmation message, used inside of '{}', like {model}.")
    private ModelBinding confirmationMsgBinding;

    @Getter
    @Setter
    @XMLProperty
    @DesignerXMLProperty(priority = 201, functionalArea = BEHAVIOR)
    @DocumentedComponentAttribute(value = "Defines pipe-separated list of events name that require confirmation dialog. Eg. onClick or onInput|onChange")
    private String confirmOnEvent;

    public FormElementWithConfirmationSupport(Form form) {
        super(form);
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void updateModel(ValueChange valueChange) {
        if (confirmationMsgBinding != null) {
            this.updateBinding(valueChange, confirmationMsgBinding, confirmationMsgBinding.getBindingExpression(), this.getOptionalFormatter());
        }
    }

    @Override
    public ElementChanges updateView() {
        ElementChanges elementChanges = super.updateView();

        boolean refreshView = processConfirmationMsgBinding(elementChanges);

        if (refreshView) {
            refreshView();
        }
        return elementChanges;
    }

    protected boolean processConfirmationMsgBinding(ElementChanges elementChanges) {
        BindingResult bindingResult = confirmationMsgBinding != null ? confirmationMsgBinding.getBindingResult() : null;
        if (bindingResult != null) {
            this.confirmationMsg = (String) bindingResult.getValue();
        }

        return false;
    }

    public void setConfirmationMsg(String message) {
        confirmationMsg = message;
        confirmationMsgBinding = new StaticBinding<>(message);
    }
}