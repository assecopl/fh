package pl.fhframework.core.util;

import pl.fhframework.core.logging.UserSessionBasedDiscriminator;
import pl.fhframework.UserSession;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public class LogUtils {
    private static final String LOG_BASE_PATH = "./log/";
    private static final String FILE_MASK = "users/$user$_$creationTimestamp$_$sessionId$.log";

    public static URL getUserLogFile(UserSession userSession) {
        String fileName = FILE_MASK.replace(UserSessionBasedDiscriminator.USER_TAG, userSession.getSystemUser().getLogin())
                .replace(UserSessionBasedDiscriminator.SESSION_TAG, userSession.getConversationUniqueId())
                .replace(UserSessionBasedDiscriminator.CREATION_TIMESTAMP_TAG, userSession.getCreationTimestampString())
                .replace(UserSessionBasedDiscriminator.CREATION_DATE_TAG, userSession.getCreationDateString());

        Path result = Paths.get(LOG_BASE_PATH, fileName);

        return FileUtils.getURL(result);
    }

    public static String getCauseMessage(Throwable throwable) {
        while (throwable.getCause() != null) {
            if (throwable.getCause().getMessage() != null)
                return throwable.getCause().getMessage();

            throwable = throwable.getCause();
        }
        return throwable.getMessage();
    }
}
