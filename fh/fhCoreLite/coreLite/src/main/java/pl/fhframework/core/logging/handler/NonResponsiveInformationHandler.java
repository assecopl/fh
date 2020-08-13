package pl.fhframework.core.logging.handler;

import pl.fhframework.core.logging.ErrorInformation;
import pl.fhframework.FormsHandler;

import java.time.format.DateTimeFormatter;

/**
 * Created by Adam Zareba on 31.01.2017.
 */
public interface NonResponsiveInformationHandler extends IErrorInformationHandler {

    int MAX_MESSAGE_LENGTH = 250;

    DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    static String buildSingleErrorMessage(ErrorInformation error) {
        return String.format("%s | %s, %s, %s", proccessMessage(error), error.getNodeName(), error.getId(),
                TIME_FORMATTER.format(error.getTimestamp()));
    }

    static String proccessMessage(ErrorInformation error) {
        String message = error.getMessage();
        if (message == null) {
            if (error.getException() != null) {
                return error.getException().getClass().getSimpleName();
            } else {
                return "(no message)";
            }
        }
        if (message.length() > MAX_MESSAGE_LENGTH) {
            return message.substring(0, MAX_MESSAGE_LENGTH) + "(...)";
        } else {
            return message;
        }
    }
}
