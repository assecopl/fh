package pl.fhframework.core.forms.iterators;

import com.fasterxml.jackson.annotation.JsonIgnore;
import pl.fhframework.model.forms.Component;
import pl.fhframework.model.forms.FormElement;
import pl.fhframework.model.forms.IGroupingComponent;
import pl.fhframework.model.forms.IRepeatableComponentsHolder;

import java.util.List;

/**
 * Created by Piotr on 2017-03-15.
 */
public interface IIteratorRepeatable<G extends IGroupingComponent> extends IRepeatableComponentsHolder {

    @JsonIgnore
    public List<IRepeatableIteratorInfo> getIteratorInfos();

    @JsonIgnore
    default public boolean isComponentFactorySupported() {
        return true;
    }

    @JsonIgnore
    public G getGroupingComponentForNewComponents();

    @JsonIgnore
    public default Component getIteratorDefiningComponent() {
        return (Component) this;
    }
}
