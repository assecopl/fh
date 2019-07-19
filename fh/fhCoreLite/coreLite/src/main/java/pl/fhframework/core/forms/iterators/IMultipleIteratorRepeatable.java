package pl.fhframework.core.forms.iterators;

import pl.fhframework.model.forms.IGroupingComponent;

/**
 * Created by Piotr on 2017-03-15.
 */
public interface IMultipleIteratorRepeatable<G extends IGroupingComponent> extends IIteratorRepeatable<G> {

    public void setInteratorComponentFactory(IMultipleIteratorComponentFactory<G> iteratorComponentFactory);

}
