package pl.fhframework.model.forms.dto;

import lombok.Getter;
import lombok.Setter;

public class RowExtendedFhDP extends RowFhDP {

    @Getter
    @Setter
    private String status;


    @Getter
    @Setter
    private String podstatus;


    public RowExtendedFhDP(){
        super();
    }

    public RowExtendedFhDP(int id, int pid, String name, String status, String podstatus){
        super(id,pid,name);
        this.status = status;
        this.podstatus = podstatus;

    }
}
