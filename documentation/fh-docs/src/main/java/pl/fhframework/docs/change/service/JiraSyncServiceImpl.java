package pl.fhframework.docs.change.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.fhframework.docs.change.dto.Component;
import pl.fhframework.docs.change.dto.Issue;
import pl.fhframework.docs.change.dto.JiraJsonObject;
import pl.fhframework.docs.change.dto.Task;
import pl.fhframework.docs.change.model.Change;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.apache.commons.codec.binary.Base64.encodeBase64;

/**
 * Created by mateusz.zaremba
 */
@Service
public class JiraSyncServiceImpl implements JiraSyncService {

    private static final String JIRA_URI = "https://jira.pro.asseco.pl/rest/api/2/search";

    private static final String QUERY = "component='FH Core' AND (labels='BUG' OR labels='IMPROVEMENT')";

    private static final Set<String> TYPES = Stream.of("IMPROVEMENT", "BUG").collect(Collectors.toCollection(HashSet::new));

    private static final Set<String> AREAS = Stream.of("FH Core", "Form Components").collect(Collectors.toCollection(HashSet::new));

    private static final String TITLE_TAG = "<title>";

    private static final String DESCRIPTION_TAG = "<desc>";

    private static final String TITLE_REGEX = TITLE_TAG + ".*" + TITLE_TAG;

    private static final String DESCRIPTION_REGEX = DESCRIPTION_TAG + ".*" + DESCRIPTION_TAG;

    @Value("#{systemProperties.getProperty('jiraUsername') ?: 'DEFAULT'}")
    private String jiraUsername;

    @Value("#{systemProperties.getProperty('jiraPassword') ?: 'DEFAULT'}")
    private String jiraPassword;

    @Autowired
    private ChangeService changeService;

    @Override
    public void synchronizeJira() {
        changeService.addChanges(produceChangesList(Collections.singletonList(synchronizeTaskInformation(jiraUsername, jiraPassword, QUERY))));
    }

    /**
     * Synchronize information from jira about single given task.
     */
    private JiraJsonObject synchronizeTaskInformation(String userName, String password, String query) {
        RestTemplate template = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
        httpHeaders.setAccept(Arrays.asList(MediaType.ALL));
        httpHeaders.add("Authorization", "Basic " + buildBase64Credentials(userName, password));
        HttpEntity<String> entity = new HttpEntity<String>("parameters", httpHeaders);
        ResponseEntity<JiraJsonObject> result = template.exchange(JIRA_URI + "?jql=" + query, HttpMethod.GET, entity, JiraJsonObject.class);
        return result.getBody();
    }

    /**
     * Get all subtaskt numbers for given task.
     */
    private List<String> getAllSubtasksNumbers(String userName, String password, String query) {
        List<String> subtaskNumbers = new LinkedList<>();
        for (Issue issue : synchronizeTaskInformation(userName, password, query).getIssues()) {
            for (Task task : issue.getFields().getSubtasks()) {
                String taskNumber = task.getKey().split("-")[1];
                subtaskNumbers.add(taskNumber);
            }
        }
        return subtaskNumbers;
    }

    private String buildBase64Credentials(String user, String password) {
        byte[] plainCredsBytes = (user + ":" + password).getBytes();
        byte[] base64CredsBytes = encodeBase64(plainCredsBytes);
        return new String(base64CredsBytes);
    }

    /**
     * Produce changes list from given jira REST objects.
     */
    private List<Change> produceChangesList(List<JiraJsonObject> jiraJsonObjects) {
        List<Change> changes = new LinkedList<>();
        for (JiraJsonObject jiraJsonObject : jiraJsonObjects) {
            for (Issue issue : jiraJsonObject.getIssues()) {
                String summary = issue.getFields().getDescription() != null ? extractTitle(issue.getFields().getDescription()) : " ";
                String description = issue.getFields().getDescription() != null ? extractDescription(issue.getFields().getDescription()) : " ";
                String self = issue.getSelf() != null ? issue.getSelf() : " ";
                String area = getArea(issue.getFields().getComponents());
                Change.Type typeEnum = getType(issue.getFields().getLabels());
                String[] authors = issue.getFields().getAssignee() != null ? new String[]{issue.getFields().getAssignee().getDisplayName()} : new String[]{" "};
                changes.add(new Change(summary, description, issue.getFields().getCreated(), typeEnum, self, authors, area));
            }
        }
        return changes;
    }

    private Change.Type getType(List<String> labels) {
        for (String label : labels) {
            if (TYPES.contains(label)) {
                return Change.Type.valueOf(label);
            }
        }
        return Change.Type.IMPROVEMENT;
    }

    private String getArea(List<Component> components) {
        for (Component component : components) {
            if (AREAS.contains(component.getName())) {
                return component.getName();
            }
        }
        return " ";
    }

    private String extractTitle(String text) {
        Matcher matcher = Pattern.compile(TITLE_REGEX).matcher(text);
        if (matcher.find()) {
            String[] splitted = matcher.group(0).split(TITLE_TAG);
            return splitted.length > 1 ? splitted[1] : "";
        } else {
            return "";
        }
    }

    private String extractDescription(String text) {
        Matcher matcher = Pattern.compile(DESCRIPTION_REGEX).matcher(text);
        if (matcher.find()) {
            String[] splitted = matcher.group(0).split(DESCRIPTION_TAG);
            return splitted.length > 1 ? splitted[1] : "";
        } else {
            return "";
        }
    }

}
