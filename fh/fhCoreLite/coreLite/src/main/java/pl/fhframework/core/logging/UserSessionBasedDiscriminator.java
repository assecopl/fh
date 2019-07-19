package pl.fhframework.core.logging;

import pl.fhframework.UserSession;
import pl.fhframework.SessionManager;

import java.util.Map;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.sift.AbstractDiscriminator;
import ch.qos.logback.core.util.OptionHelper;

/**
 * Created by Piotr on 2017-01-09.
 */
public class UserSessionBasedDiscriminator extends AbstractDiscriminator<ILoggingEvent> {

    private static final String EXPORTED_FILENAME = "logFileName";

    public static final String USER_TAG = "$user$";

    public static final String SESSION_TAG = "$sessionId$";

    public static final String CREATION_DATE_TAG = "$creationDate$";

    public static final String CREATION_TIMESTAMP_TAG = "$creationTimestamp$";

    private String nonUserFileName;

    private String userFileName;

    public String getDiscriminatingValue(ILoggingEvent event) {
        Map<String, String> mdcMap = event.getMDCPropertyMap();
        if (mdcMap == null) {
            return nonUserFileName;
        }
        UserSession userSession = SessionManager.getUserSession();
        if (userSession == null) {
            return nonUserFileName;
        } else {
            return userFileName
                    .replace(USER_TAG, userSession.getSystemUser().getLogin())
                    .replace(SESSION_TAG, userSession.getConversationUniqueId())
                    .replace(CREATION_TIMESTAMP_TAG, userSession.getCreationTimestampString())
                    .replace(CREATION_DATE_TAG, userSession.getCreationDateString());
        }
    }

    @Override
    public String getKey() {
        return EXPORTED_FILENAME;
    }

    @Override
    public void start() {
        int errorCount = 0;
        if (OptionHelper.isEmpty(nonUserFileName)) {
            addError("The \"nonUserFileName\" property must be set");
            errorCount++;
        }
        if (OptionHelper.isEmpty(userFileName)) {
            addError("The \"userFileName\" property must be set");
            errorCount++;
        }
        if (errorCount == 0) {
            super.start();
        }
    }

    public String getNonUserFileName() {
        return nonUserFileName;
    }

    public void setNonUserFileName(String nonUserFileName) {
        this.nonUserFileName = nonUserFileName;
    }

    public String getUserFileName() {
        return userFileName;
    }

    public void setUserFileName(String userFileName) {
        this.userFileName = userFileName;
    }
}
