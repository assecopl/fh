package pl.fhframework.model.forms.dto;


import lombok.Getter;
import lombok.Setter;

public class RowFhDP {

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private int id;

    @Getter
    @Setter
    private int pid;

    public RowFhDP(){

    }
    public RowFhDP(int id, int pid, String name){
        this.id = id;
        this.pid = pid;
        this.name = name;
    }
}
