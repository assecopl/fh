package pl.fhframework.model.dto;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.InicjujacyPrzypadekUzycia;

/**
 * Created by Gabriel on 2015-11-21.
 */
@Deprecated
@Getter
@Setter
public class UseCaseMetadata {

    private String id;
    private String label;
    private boolean springBean;
    private boolean singleton;

    private  Class<? extends InicjujacyPrzypadekUzycia> clazz;


}
