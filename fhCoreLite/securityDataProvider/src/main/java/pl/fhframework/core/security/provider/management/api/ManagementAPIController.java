package pl.fhframework.core.security.provider.management.api;

import com.fasterxml.jackson.databind.util.RawValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.core.security.ISecurityDataProvider;
import pl.fhframework.core.security.model.SessionInfo;
import pl.fhframework.core.security.provider.management.status.ApplicationStatus;
import pl.fhframework.core.security.provider.management.status.ApplicationStatusHelper;
import pl.fhframework.core.security.provider.service.LocalUserSessionService;
import pl.fhframework.core.session.UserSessionRepository;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * Created by Piotr on 2018-04-17.
 */
@RestController
@ConditionalOnProperty(name = "fhframework.managementApi.enabled", havingValue = "true")
public class ManagementAPIController {

    public static final String MANAGEMENT_API_URI = "/managementAPI";

    public static final String MANAGEMENT_API_STATUS_URI = MANAGEMENT_API_URI + "/status";

    public static final String MANAGEMENT_API_SESSIONS_LIST_URI = MANAGEMENT_API_URI + "/sessions/list";

    public static final String MANAGEMENT_API_SESSIONS_LOG_URI = MANAGEMENT_API_URI + "/sessions/log/{sessionId}";

    public static final String MANAGEMENT_API_MESSAGE_URI = MANAGEMENT_API_URI + "/sessions/message";

    public static final String MANAGEMENT_API_SESSIONS_LOG_OUT_URI = MANAGEMENT_API_URI + "/sessions/logout/{sessionId}";

    public static final String MANAGEMENT_API_ACTIVE_FUNCTIONALITY_URI = MANAGEMENT_API_URI + "/sessions/activeFunctionality/{sessionId}";

    public static final String MANAGEMENT_API_ECHO_URI = MANAGEMENT_API_URI + "/sessions/echo";

    public static final String MANAGEMENT_API_MESSAGE_IDS = "ids";

    public static final String MANAGEMENT_API_MESSAGE_TITLE = "title";

    public static final String MANAGEMENT_API_MESSAGE_MSG = "message";

    public static final String LOG_FILENAME_HEADER = "X-Log-Filename";

    @Autowired
    private UserSessionRepository userSessionRepository;

    @Autowired
    private LocalUserSessionService localUserSessionService;

    @Autowired
    private ApplicationStatusHelper applicationStatusHelper;

    @Autowired
    private ISecurityDataProvider securityDataProvider;

    @GetMapping(MANAGEMENT_API_STATUS_URI)
    public ApplicationStatus getApplicationStatus() {
        ApplicationStatus applicationStatus = new ApplicationStatus();
        applicationStatus.setUserSessionCount(userSessionRepository.getUserSessionCount());
        applicationStatus.setErrorLogsCount(applicationStatusHelper.getErrorLogsCount());
        applicationStatus.setWarningLogsCount(applicationStatusHelper.getWarningLogsCount());
        applicationStatus.setUserAccountProviderType(securityDataProvider.getUserAccountProviderType());
        applicationStatus.setUserAccountProviderSource(securityDataProvider.getUserAccountProviderSource());
        applicationStatus.setBusinessRoleProviderType(securityDataProvider.getBusinessRoleProviderType());
        applicationStatus.setBusinessRoleProviderSource(securityDataProvider.getBusinessRoleProviderSource());
        return applicationStatus;
    }

    @GetMapping(MANAGEMENT_API_SESSIONS_LIST_URI)
    public List<SessionInfo> getUserSessions() {
        return localUserSessionService.getUserSessions();
    }

    @GetMapping(path = MANAGEMENT_API_SESSIONS_LOG_URI, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void donwloadUserLog(@PathVariable("sessionId") String sessionId, HttpServletResponse response) {
        Resource resource = localUserSessionService.donwloadUserLog(sessionId);
        response.setHeader(LOG_FILENAME_HEADER, resource.getFilename());
        response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setStatus(HttpServletResponse.SC_OK);
        try (InputStream input = resource.getInputStream(); OutputStream output = response.getOutputStream()) {
            StreamUtils.copy(input, output);
            output.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping(path = MANAGEMENT_API_MESSAGE_URI)
    public int sendMessage(@RequestParam(MANAGEMENT_API_MESSAGE_IDS) List<String> sessionIds,
                           @RequestParam(MANAGEMENT_API_MESSAGE_TITLE) String title,
                           @RequestParam(MANAGEMENT_API_MESSAGE_MSG) String message) {
        return localUserSessionService.sendMessage(sessionIds, title, message);
    }

    @PostMapping(path = MANAGEMENT_API_SESSIONS_LOG_OUT_URI)
    public boolean forceLogout(@PathVariable("sessionId") String userSessionId) {
        return localUserSessionService.forceLogout(userSessionId);
    }

    @GetMapping(path = MANAGEMENT_API_ACTIVE_FUNCTIONALITY_URI)
    public ResponseEntity getUserActiveFunctionality(@PathVariable String sessionId) {
        try {
            String activeFunctionality = localUserSessionService.getUserActiveFunctionality(sessionId);
            return ResponseEntity.ok(new RawValue(activeFunctionality));
        } catch (Exception e) {
            FhLogger.error(e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    @GetMapping(path = MANAGEMENT_API_ECHO_URI)
    public ResponseEntity echo() {
        return ResponseEntity.ok(new RawValue("OK"));
    }

}
