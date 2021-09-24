package pl.fhframework.service;

import pl.fhframework.model.forms.Component;
import pl.fhframework.model.forms.IGroupingComponent;

/**
 * Created by Adam Zareba on 28.02.2017.
 */
public interface ComponentService {

    <T> T getComponent(IGroupingComponent<? extends Component> groupingComponent, String subComponentId, Class<T> subComponentClass);

}
