package pl.fhframework;

import lombok.Getter;

public class BindingResult<T> {

    @Getter
    private Object parent;

    @Getter
    private String attributeName;

    @Getter
    private T value;

    public BindingResult(Object parent, String attributeName, T value){
        this.parent = parent;
        this.attributeName = attributeName;
        this.value = value;
    }
}