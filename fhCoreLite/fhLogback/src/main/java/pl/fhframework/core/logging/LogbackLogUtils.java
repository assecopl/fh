package pl.fhframework.core.logging;

import pl.fhframework.UserSession;
import pl.fhframework.core.FhException;
import pl.fhframework.core.util.FileUtils;
import pl.fhframework.core.util.ILogUtils;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public class LogbackLogUtils implements ILogUtils {
    public URL getUserLogFile(UserSession userSession) {
        String fileName = FILE_MASK.replace(USER_TAG, userSession.getSystemUser().getLogin())
                .replace(SESSION_TAG, userSession.getConversationUniqueId())
                .replace(CREATION_TIMESTAMP_TAG, userSession.getCreationTimestampString())
                .replace(CREATION_DATE_TAG, userSession.getCreationDateString());

        Path result = Paths.get(LOG_BASE_PATH, fileName);
        if (!result.toFile().exists()) {
            try {
                result.toFile().createNewFile(); // TEAM-872
            } catch (IOException e) {
                throw new FhException("Can't create log file");
            }
        }
        return FileUtils.getURL(result);
    }
}
