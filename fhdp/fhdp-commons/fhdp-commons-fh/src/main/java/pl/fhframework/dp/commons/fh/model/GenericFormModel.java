package pl.fhframework.dp.commons.fh.model;


import lombok.Getter;
import lombok.Setter;
import pl.fhframework.model.forms.IComboItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:jacek.borowiec@asseco.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 2019-08-06
 */
@Getter @Setter
public class GenericFormModel<F> {
    private F entity;
    private Map<String,List<IComboItem>> comboValues = new HashMap<>();
    private String filterText;
}
