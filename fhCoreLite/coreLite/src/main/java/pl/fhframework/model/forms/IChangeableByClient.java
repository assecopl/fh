package pl.fhframework.model.forms;

import pl.fhframework.model.dto.ValueChange;

/**
 * Interface for objects modified by web client
 */
public interface IChangeableByClient {

    void updateModel(ValueChange valueChange);
}
