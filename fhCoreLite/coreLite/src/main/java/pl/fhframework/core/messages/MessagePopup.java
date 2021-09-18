package pl.fhframework.core.messages;

import pl.fhframework.core.uc.UseCaseContainer;
import pl.fhframework.model.forms.Form;
import pl.fhframework.model.forms.FormElement;

/**
 * Message dialog created on top of Form
 * Created by krzysztof.kobylarek on 2016-10-20.
 */
public class MessagePopup extends Form<Object> {

    public void showDialog(){
        UseCaseContainer.PopupMessageUseCaseContextMessage useCaseWrapper = ( UseCaseContainer.PopupMessageUseCaseContextMessage) this.getAbstractUseCase();
        configure(useCaseWrapper, new Object());
        useCaseWrapper.showForm(this, false);
    }
}
