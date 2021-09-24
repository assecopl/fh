package pl.fhframework.core.forms.iterators;

/**
 * Created by Piotr on 2017-03-15.
 */

import pl.fhframework.binding.IRowNumberOffsetSupplier;
import pl.fhframework.model.forms.Component;
import pl.fhframework.model.forms.FormElement;
import pl.fhframework.model.forms.IGroupingComponent;

import java.util.List;

public interface IMultipleIteratorComponentFactory<G extends IGroupingComponent> {

    public List<FormElement> createComponentsForIterators(G myGroupingParent, IRowNumberOffsetSupplier rowNumberOffsetSupplier, int[] indices);

}
