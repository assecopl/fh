package pl.fhframework.dp.commons.services.declaration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.MongoId;

/**
 * @author <a href="mailto:jacek_borowiec@skg.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 23/03/2021
 */
@Getter @Setter
public class MaxIdDto {
    @MongoId
    private String key;
    private Integer value;

    public MaxIdDto(String key) {
        this.key = key;
        this.value = 0;
    }
}
