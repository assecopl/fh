package pl.fhframework.model.forms;

import com.fasterxml.jackson.annotation.JsonIgnore;
import pl.fhframework.core.forms.IterationContext;

import java.util.List;

/**
 * Created by krzysztof.kobylarek on 2016-12-04.
 */
public interface IRepeatable extends IRepeatableComponentsHolder {

    @JsonIgnore
    List<IterationContext> getBindedSubcomponents();

    void processComponents();
}
