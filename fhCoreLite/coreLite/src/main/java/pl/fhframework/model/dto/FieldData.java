package pl.fhframework.model.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Gabriel on 2015-11-25.
 */
@Deprecated
@Getter
@Setter
public class FieldData<T> {
    private String fieldId;
    private T value;
}
