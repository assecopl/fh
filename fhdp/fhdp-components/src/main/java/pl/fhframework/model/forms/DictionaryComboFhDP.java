package pl.fhframework.model.forms;

import lombok.extern.slf4j.Slf4j;
import pl.fhframework.annotations.*;

/**
 * @author <a href="mailto:jacek.borowiec@asseco.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 02/06/2020
 */
@Slf4j
@DocumentedComponent(value = "Enables users to quickly find and select from a pre-populated list of values as they type, leveraging searching and filtering.",
        icon = "fa fa-outdent")
@Control(parents = {PanelGroup.class, Group.class, Column.class, Tab.class, Row.class, Form.class, Repeater.class}, invalidParents = {Table.class}, canBeDesigned = false)
//public class DictionaryLookupFhDP extends BaseInputFieldWithKeySupport implements IGroupingComponent<DictionaryComboParameterFhDP>, I18nFormElement {
public class DictionaryComboFhDP extends DictionaryLookup {
    public DictionaryComboFhDP(Form form) {
        super(form);
    }
}
