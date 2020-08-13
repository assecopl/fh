package pl.fhframework.core.forms.iterators;

import com.fasterxml.jackson.annotation.JsonIgnore;
import pl.fhframework.model.forms.IGroupingComponent;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Piotr on 2017-03-15.
 */
public interface ISingleIteratorRepeatable<G extends IGroupingComponent> extends IIteratorRepeatable {

    @JsonIgnore
    public IRepeatableIteratorInfo getIteratorInfo();

    public void setInteratorComponentFactory(ISingleIteratorComponentFactory<G> iteratorComponentFactory);

    @JsonIgnore
    @Override
    default public List<IRepeatableIteratorInfo> getIteratorInfos() {
        IRepeatableIteratorInfo iterator = getIteratorInfo();
        if (iterator != null) {
            return Arrays.asList(getIteratorInfo());
        } else {
            return Collections.emptyList();
        }
    }
}
