package pl.fhframework.model.forms.dto;

import lombok.Getter;
import lombok.Setter;

public class OptionFhDP {

    @Getter
    @Setter
    private String value; //kod słownika

    @Getter
    @Setter
    private String text;// opis wartości

    public OptionFhDP(String value, String text){
        this.value = value;
        this.text = text;

    }
}
