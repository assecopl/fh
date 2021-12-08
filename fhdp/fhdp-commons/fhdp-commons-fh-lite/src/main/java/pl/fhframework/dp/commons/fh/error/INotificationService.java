package pl.fhframework.dp.commons.fh.error;

import pl.fhframework.UserSession;

/**
 * @author <a href="mailto:jacek_borowiec@skg.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 15/01/2021
 */
public interface INotificationService {
    void notifyAdmins(String emailAddresses, String message, UserSession session, String stackTrace);
}
