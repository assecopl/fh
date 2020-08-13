package pl.fhframework.model.forms.composites.picklist;

import pl.fhframework.annotations.Action;
import pl.fhframework.annotations.composite.Composite;
import pl.fhframework.annotations.composite.FireEvent;
import pl.fhframework.model.forms.CompositeForm;
import pl.fhframework.model.forms.model.OptionsListElementModel;
import pl.fhframework.events.ViewEvent;

import java.util.Collections;
import java.util.Iterator;

/**
 * Created by krzysztof.kobylarek on 2017-01-07.
 */

@Composite(template = "PickList", registeredEvents = {"onList1Changed", "onList2Changed"})
public class PickList extends CompositeForm<PickListModel> {

    @Action
    @FireEvent(name = "onList1Changed")
    public void onMoveToList1(ViewEvent<PickListModel> event){
        PickListModel model = event.getSourceForm().getModel();
        Iterator<OptionsListElementModel> list2Iterator =  model.getValuesList2().iterator();
        while(list2Iterator.hasNext()){
            OptionsListElementModel listElementModel = list2Iterator.next();
            if (listElementModel.isChecked()){
                listElementModel.setChecked(false);
                model.getValuesList1().add(listElementModel);
                list2Iterator.remove();
            }
        }

        Collections.sort(model.getValuesList1(), (o1, o2) -> o1.getValue().compareTo(o2.getValue()) );
        Collections.sort(model.getValuesList2(), (o1, o2) -> o1.getValue().compareTo(o2.getValue()) );
    }

    @Action
    @FireEvent(name = "onList2Changed")
    public void onMoveToList2(ViewEvent<PickListModel> event){
        PickListModel model = event.getSourceForm().getModel();
        Iterator<OptionsListElementModel> list1Iterator =  model.getValuesList1().iterator();
        while(list1Iterator.hasNext()){
            OptionsListElementModel listElementModel = list1Iterator.next();
            if (listElementModel.isChecked()){
                listElementModel.setChecked(false);
                model.getValuesList2().add(listElementModel);
                list1Iterator.remove();
            }
        }
        Collections.sort(model.getValuesList1(), (o1, o2) -> o1.getValue().compareTo(o2.getValue()) );
        Collections.sort(model.getValuesList2(), (o1, o2) -> o1.getValue().compareTo(o2.getValue()) );
    }

}
