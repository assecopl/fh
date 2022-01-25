package pl.fhframework.dp.commons.fh.uc;

import pl.fhframework.core.uc.IUseCaseOutputCallback;

/**
 * @author <a href="mailto:jacek_borowiec@skg.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 2019-08-06
 */
public interface IGenericListOutputCallback<T> extends IUseCaseOutputCallback {
    void delete();
    void cancel();
    void save(T result);
}
