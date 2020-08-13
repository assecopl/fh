package pl.fhframework.model.dto;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.model.forms.AccessibilityEnum;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gabriel on 2015-11-25.
 */
@Deprecated
@Getter
@Setter
public class ModelDostepnosciFormatki {
    private List<FieldData<AccessibilityEnum>> dostepnosciPol  = new ArrayList<FieldData<AccessibilityEnum>>();
}
