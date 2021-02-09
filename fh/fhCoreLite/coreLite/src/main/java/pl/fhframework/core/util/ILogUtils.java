package pl.fhframework.core.util;

import pl.fhframework.UserSession;

import java.net.URL;

public interface ILogUtils {
    String LOG_BASE_PATH = "./log/";
    String FILE_MASK = "users/$user$_$creationTimestamp$_$sessionId$.log";

    String USER_TAG = "$user$";

    String SESSION_TAG = "$sessionId$";

    String CREATION_DATE_TAG = "$creationDate$";

    String CREATION_TIMESTAMP_TAG = "$creationTimestamp$";

    String FLUSH_MESSAGE = "Flushing the log buffer out";

    URL getUserLogFile(UserSession userSession);
}
