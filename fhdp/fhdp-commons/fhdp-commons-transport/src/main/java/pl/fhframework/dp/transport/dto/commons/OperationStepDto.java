package pl.fhframework.dp.transport.dto.commons;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import pl.fhframework.dp.transport.dto.document.SeverityEnum;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author <a href="mailto:jacek.borowiec@asseco.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 28/05/2020
 */
@Getter @Setter
public class OperationStepDto  implements Comparable<OperationStepDto>{
    private String description;
    private SeverityEnum type;
    private List<String> descriptionParams;
    @Field(type = FieldType.Date, format = DateFormat.custom, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSX")
    private LocalDateTime started;
    @Field(type = FieldType.Date, format = DateFormat.custom, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSX")
    private LocalDateTime finished;
    private float duration;

    @Override
    public int compareTo(OperationStepDto o) {
        if(this.started.equals(o.started)) return 0;
        return (this.started.isAfter(o.started))?-1:1;
    }
}
