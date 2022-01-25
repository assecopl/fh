package pl.fhframework.model.forms.provider;

import lombok.Getter;
import lombok.Setter;

/**
 * @author <a href="mailto:jacek_borowiec@skg.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 14/08/2020
 */
@Getter @Setter
public class NameValue {
    private String title;
    private String field;

    public NameValue(String title, String field) {
        this.title = title;
        this.field = field;
    }
}
