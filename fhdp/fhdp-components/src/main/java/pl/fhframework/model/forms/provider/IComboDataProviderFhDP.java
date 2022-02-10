package pl.fhframework.model.forms.provider;

import java.util.List;

/**
 * @author <a href="mailto:jacek.borowiec@asseco.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 13/08/2020
 */
public interface IComboDataProviderFhDP<SRC, RESULT> extends IComboDataProvider<SRC, RESULT> {

    /**
     * Method returning paged values. Taken by reflection
     * @param text
     * @param pageable
     * @param param
     * @return
     */
//    PageModel<SRC> getValuesPaged(String text, Pageable pageable, Object... param);

    /**
     * Return list of column definitions
     * @return NameValuePair: name: column description; value: column binding
     */
    List<NameValue> getColumnDefinitions();

    /**
     * Returns title displayed on the list
     * @return
     */
//    String getTitle(LocalDate referenceDate, String codeListId);

    /**
     * Returns key for getting unique object value
     * @param element
     * @return
     */
    String getSrcKey(SRC element);

}
