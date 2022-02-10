package pl.fhframework.dp.commons.fh.document.handling;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.model.forms.AccessibilityEnum;
import pl.fhframework.model.forms.Tab;

/**
 * @author <a href="mailto:jacek.borowiec@asseco.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 07/12/2021
 */
@Getter
@Setter
public class DynamicTab implements Comparable<DynamicTab>{
    private String tabId;
    private String label;
    private String formReference;
    private AccessibilityEnum accessibility;
    private Tab tab;//internally used
    private Object model;

    @Override
    public int compareTo(DynamicTab o) {
        return this.getTabId().compareTo(o.getTabId());
    }

}
