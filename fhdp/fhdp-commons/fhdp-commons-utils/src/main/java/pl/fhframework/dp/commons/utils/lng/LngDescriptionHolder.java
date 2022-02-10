package pl.fhframework.dp.commons.utils.lng;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:jacek.borowiec@asseco.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 28/12/2020
 */
@Getter @Setter
public class LngDescriptionHolder {
    private List<LngDescription> descriptions = new ArrayList<>();
}
