package pl.fhframework.core.services.meta;

import lombok.AllArgsConstructor;
import lombok.Data;
import pl.fhframework.modules.services.ServiceTypeEnum;

/**
 * Created by pawel.ruta on 2018-04-09.
 */
@Data
@AllArgsConstructor
public class ServiceType {
    private ServiceTypeEnum typeId;

    private String typeLabel;
}
