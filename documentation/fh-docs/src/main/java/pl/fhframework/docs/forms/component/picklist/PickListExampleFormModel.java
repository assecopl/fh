package pl.fhframework.docs.forms.component.picklist;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import pl.fhframework.core.designer.ComponentElement;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by krzysztof.kobylarek on 2017-01-07.
 */
@Getter
@Setter
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PickListExampleFormModel extends ComponentElement {

    private PickListExampleModel carsListModel = new PickListExampleModel();

    public PickListExampleModel getCarsListModel() {
        return carsListModel;
    }


}
