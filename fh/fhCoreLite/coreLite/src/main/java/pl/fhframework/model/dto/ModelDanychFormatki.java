package pl.fhframework.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gabriel on 2015-11-25.
 */
@Deprecated
@Getter
@Setter
public class ModelDanychFormatki {
    private List<FieldData<WartoscPola>> wartosciPol  = new ArrayList<FieldData<WartoscPola>>();
}
