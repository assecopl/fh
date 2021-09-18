package pl.fhframework.model.forms;

import java.util.List;

/**
 * Component which refers to other components by id
 */
public interface IComponentsReferrer {

    public List<String> getComponentIds();
}
