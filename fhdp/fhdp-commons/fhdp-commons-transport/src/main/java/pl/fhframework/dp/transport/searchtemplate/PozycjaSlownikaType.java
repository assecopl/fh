package pl.fhframework.dp.transport.searchtemplate;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
//TODO: DELETE WHEN FRONT DEPENDENCIES ARE FIXED
@Getter
@Setter
public class PozycjaSlownikaType implements Serializable {
    protected String kod;
    protected String opis;
}
