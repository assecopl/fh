package pl.fhframework.model.forms.composites.picklist;

import pl.fhframework.model.forms.model.OptionsListElementModel;

import java.util.List;

/**
 * Created by krzysztof.kobylarek on 2017-01-07.
 */
public abstract class PickListModel {

    public abstract List<OptionsListElementModel> getValuesList1();

    public abstract List<OptionsListElementModel> getValuesList2();

    public abstract String getTitleList1();

    public abstract String getTitleList2();
}
