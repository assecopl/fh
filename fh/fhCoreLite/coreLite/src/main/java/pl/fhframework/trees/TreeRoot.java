package pl.fhframework.trees;


import lombok.Getter;

import lombok.Setter;
import pl.fhframework.subsystems.Subsystem;

public class TreeRoot extends UseCasesGroup {

    @Getter
    private Subsystem subsystem;
    @Getter
    @Setter
    private long resourceLastChecked;
    @Getter
    @Setter
    private long resourceTimestamp;

    public TreeRoot(Subsystem subsystem) {
        super(null, null, null, null, 0, (String) null);
        this.subsystem = subsystem;
    }
}
