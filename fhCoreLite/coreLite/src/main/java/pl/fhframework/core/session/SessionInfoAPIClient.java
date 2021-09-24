package pl.fhframework.core.session;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.core.security.model.SessionInfo;
import pl.fhframework.io.TemporaryResource;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * User sessions info REST API client.
 * @author Tomasz Kozlowski (created on 30.12.2019)
 */
@Service
@RequiredArgsConstructor
public class SessionInfoAPIClient {

    private final static String MANAGEMENT_API_PATH_SEGMENT = "managementAPI";
    private final static String SESSIONS_PATH_SEGMENT = "sessions";
    private static final String ACTIVE_FUNCTIONALITY_PATH_SEGMENT = "activeFunctionality";
    private static final String ECHO_PATH_SEGMENT = "echo";
    private static final String MESSAGE_PATH_SEGMENT = "message";
    private static final String LOGOUT_PATH_SEGMENT = "logout";
    private static final String SESSIONS_LOG_PATH_SEGMENT = "log";
    private static final String MESSAGE_IDS_PARAM = "ids";
    private static final String MESSAGE_TITLE_PARAM = "title";
    private static final String MESSAGE_CONTENT_PARAM = "message";

    private final MessageToTemporaryFileConverter messageToTemporaryFileConverter;

    /** Gets info about active functionality for given user session */
    public String getUserActiveFunctionality(SessionInfo sessionInfo) {
        String url = prepareURL(sessionInfo.getNodeUrl(), ACTIVE_FUNCTIONALITY_PATH_SEGMENT, sessionInfo.getSessionId());
        try {
            return createRestTemplate().getForObject(url, String.class);
        } catch (Throwable e) {
            FhLogger.errorSuppressed("Error while getting user active functionality from " + url, e);
            return null;
        }
    }

    /** Returns whether given node is active */
    public boolean isNodeActive(String nodeUrl) {
        String url = prepareURL(nodeUrl, ECHO_PATH_SEGMENT);
        try {
            String response = createRestTemplate().getForObject(url, String.class);
            return "OK".equals(response);
        } catch (Throwable e) {
            return false;
        }
    }

    /** Displays given message to specified users */
    public int sendMessage(String nodeUrl, List<SessionInfo> sessions, String title, String message) {
        String url = prepareURL(nodeUrl, MESSAGE_PATH_SEGMENT);
        try {
            // flat session ids
            String sessionIds = null;
            if (!CollectionUtils.isEmpty(sessions)) {
                sessionIds = sessions.stream()
                    .map(SessionInfo::getSessionId)
                    .collect(Collectors.joining(","));
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add(MESSAGE_IDS_PARAM, sessionIds);
            params.add(MESSAGE_TITLE_PARAM, title);
            params.add(MESSAGE_CONTENT_PARAM, message);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
            Integer result = createRestTemplate().postForObject(url, request, Integer.class);
            return result != null ? result : 0;
        } catch (Throwable e) {
            FhLogger.errorSuppressed("Error while sending message to user(s) at " + url, e);
            return 0;
        }
    }

    /** Force logout of given user */
    public boolean forceLogout(SessionInfo sessionInfo) {
        String url = prepareURL(sessionInfo.getNodeUrl(), LOGOUT_PATH_SEGMENT, sessionInfo.getSessionId());
        try {
            Boolean result = createRestTemplate().postForObject(url, null, Boolean.class);
            return result != null ? result : false;
        } catch (Throwable e) {
            FhLogger.errorSuppressed("Error while forcibly logging out user at " + url, e);
            return false;
        }
    }

    /** Downloads given user log file */
    public Resource donwloadUserLog(SessionInfo sessionInfo) {
        String url = prepareURL(sessionInfo.getNodeUrl(), SESSIONS_LOG_PATH_SEGMENT, sessionInfo.getSessionId());
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
            HttpEntity requestEntity = new HttpEntity(headers);
            RestTemplate restTemplate = createRestTemplate();
            restTemplate.setMessageConverters(Collections.singletonList(messageToTemporaryFileConverter));
            ResponseEntity<TemporaryResource> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, TemporaryResource.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            } else {
                throw new RuntimeException(response.getStatusCode() + ": " + response.getStatusCode().getReasonPhrase());
            }
        } catch (Throwable e) {
            FhLogger.errorSuppressed("Error while getting user log from " + url, e);
            return null;
        }
    }

    /** Prepares URL for REST service */
    private String prepareURL(String nodeUrl, String... pathSegments) {
        return UriComponentsBuilder.fromUriString(nodeUrl)
                .pathSegment(MANAGEMENT_API_PATH_SEGMENT)
                .pathSegment(SESSIONS_PATH_SEGMENT)
                .pathSegment(pathSegments)
                .toUriString();
    }

    /** Creates rest template */
    private RestTemplate createRestTemplate() { // TODO create REST template for secure version of services
        return new RestTemplateBuilder()
                .setConnectTimeout(Duration.ofMillis(10000L))
                .setReadTimeout(Duration.ofMillis(10000L))
                .build();
    }

}
