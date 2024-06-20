package pl.fhframework.dp.commons.mongo.entities;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import pl.fhframework.dp.commons.base.model.IPersistentObject;

import java.time.LocalDateTime;


@Getter @Setter
public class OperationStep implements Comparable<OperationStep>, IPersistentObject<String> {
    @Id
    private String id;
    private String description;
    private LocalDateTime started;
    private LocalDateTime finished;
    @Indexed
    private Long docID;
    @Indexed
    private String operationGUID;
    private String masterProcessId;
    private String processId;
    private String stepId;
    private float duration;

    @Override
    public int compareTo(OperationStep o) {
        if(this.started.equals(o.started)) return 0;
        return (this.started.isAfter(o.started))?-1:1;
    }
}
