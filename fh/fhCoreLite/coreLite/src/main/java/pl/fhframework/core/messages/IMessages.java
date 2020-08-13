package pl.fhframework.core.messages;

import pl.fhframework.UserSession;

/**
 * Created by pawel.ruta on 2018-11-26.
 */
public interface IMessages {
    MessagePopup showError(UserSession session, String message, Action action);

    MessagePopup showInfo(UserSession session, String message, Action action);

    MessagePopup showConfirmation(UserSession session, String message, Action action);

    MessagePopup showError(UserSession session, String title, String message, Action action);

    MessagePopup showInfo(UserSession session, String title, String message, Action action);

    MessagePopup showConfirmation(UserSession session, String title, String message, Action action);
}
