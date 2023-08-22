package pl.fhframework.docs.forms.component.include.template;

import pl.fhframework.annotations.Action;
import pl.fhframework.annotations.composite.Composite;
import pl.fhframework.annotations.composite.FireEvent;
import pl.fhframework.model.forms.CompositeForm;
import pl.fhframework.events.ViewEvent;

/**
 * Created by krzysztof.kobylarek on 2016-12-21.
*/
@Composite(template = "Address.frm",
    registeredEvents = {"onSave"} /*?*/
)
public class AddressCompositeForm extends CompositeForm<AddressModel> {

    @Action
    @FireEvent(name="onSave")
    public void onAddressSave(){
        //getAbstractUseCase().showForm(SummaryForm.class, viewEvent.getSourceForm().getModel());
        //TODO:SSO
    }

    @Action
    public void onSelectCountry(ViewEvent viewEvent){
        AddressModel model = (AddressModel) viewEvent.getSourceForm().getModel();
        model.setGiftInfo("");
        if (model.getCountry().length()%2==0){
            model.setShippingInfo("Free shipping to "+model.getCountry()+" !!!");
        } else {
            model.setShippingInfo("");
        }
    }

    @Action
    public void onSelectProvince(ViewEvent viewEvent){
        AddressModel model = (AddressModel) viewEvent.getSourceForm().getModel();
        if (model.getProvince().length()%2==0){
            model.setGiftInfo("Free gift for orders from "+model.getProvince()+" !!!");
        } else {
            model.setGiftInfo("");
        }
    }
}
