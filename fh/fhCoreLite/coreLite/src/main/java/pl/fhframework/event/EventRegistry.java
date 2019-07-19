package pl.fhframework.event;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import pl.fhframework.SessionManager;
import pl.fhframework.event.dto.*;
import pl.fhframework.core.model.dto.client.AbstractClientOutputData;
import pl.fhframework.model.forms.Component;
import pl.fhframework.model.forms.FormElement;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class EventRegistry {

    private static final String FILE_DOWNLOAD_RESOURCE_URL = "fileDownload/resource/";
    private final static String FILE_DOWNLOAD_FORM_ELEMENT_URL = "fileDownload/formElement";

    private final static String FILE_DOWNLOAD_BINDING_URL = "fileDownload/binding";
    private final static String FORM_ID_PARAM = "formId";
    private final static String FORM_ELEMENT_ID_PARAM = "id";
    private final static String BINDING_PARAM = "binding";

    public void fireDataToClientEvent(AbstractClientOutputData clientData) {
        getEvents().add(new DataToClientEvent(clientData));
    }

    public void fireDownloadEvent(Resource resource) {
        String uuid = UUID.randomUUID().toString();
        SessionManager.getUserSession().getDownloadFileIndexes().put(uuid, resource);

        fireDownloadEvent(FILE_DOWNLOAD_RESOURCE_URL + uuid);
    }

    public void fireDownloadEvent(FormElement formElement) {
        final Map<String, String> map = Collections.unmodifiableMap(Stream.of(
                new AbstractMap.SimpleEntry<>(FORM_ELEMENT_ID_PARAM, formElement.getId()),
                new AbstractMap.SimpleEntry<>(FORM_ID_PARAM, formElement.getForm().getId()))
                .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue)));

        fireDownloadEvent(buildUrl(FILE_DOWNLOAD_FORM_ELEMENT_URL, map));
    }

    public void fireDownloadEventByBinding(Component formElement, String binding) {
        final Map<String, String> map = Collections.unmodifiableMap(Stream.of(
                new AbstractMap.SimpleEntry<>(BINDING_PARAM, binding),
                new AbstractMap.SimpleEntry<>(FORM_ID_PARAM, formElement.getForm().getId()))
                .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue)));

        fireDownloadEvent(buildUrl(FILE_DOWNLOAD_BINDING_URL, map));
    }

    public void fireNotificationEvent(NotificationEvent.Level level, String message) {
        getEvents().add(new NotificationEvent(level, message));
    }

    public void fireRedirectEvent(String url, boolean newWindow) {
        getEvents().add(new RedirectEvent(url, newWindow));
    }

    public void fireRedirectEvent(String uuid, String url, boolean newWindow) {
        getEvents().add(new RedirectEvent(uuid, url, newWindow));
    }

    public void fireRedirectEvent(String uuid, String url, boolean newWindow, boolean closeable) {
        getEvents().add(new RedirectEvent(uuid, url, newWindow, closeable));
    }

    public void fireCloseTabEvent(String uuid) {
        getEvents().add(new CloseTabEvent(uuid));
    }

    public void fireShutdownEvent(boolean graceful) {
        getEvents().add(new ShutdownEvent(graceful));
    }

    public void fireFrocedLogoutEvent(ForcedLogoutEvent.Reason reason){
        getEvents().add(new ForcedLogoutEvent(reason));
    }

    public void fireFocusEvent(String containerId, String formElementId) {
        getEvents().add(new FocusEvent(containerId, formElementId));
    }

    public void fireMessageEvent(String title, String message) {
        getEvents().add(new MessageEvent(title, message));
    }

    public void fireStylesheetChangeEvent(String styleName) {
        getEvents().add(new StylesheetChangeEvent(styleName));
    }

    public void fireLanguageChangeEvent(String code) {
        getEvents().add(new LanguageChangeEvent(code));
    }

    public void fireCustomActionEvent(String actionName) {
        fireCustomActionEvent(actionName, null);
    }

    public void fireCustomActionEvent(String actionName, String data) {
        getEvents().add(new CustomActionEvent(actionName, data));
    }

    private void fireDownloadEvent(String url) {
        getEvents().add(new FileDownloadEvent(addURLPreffix(url)));
    }

    private String buildUrl(String url, Map<String, String> params) {
        final StringBuilder stringBuilder = new StringBuilder().append(url);
        if (!params.isEmpty()) {
            stringBuilder.append("?");
            params.entrySet().forEach(entry -> {
                stringBuilder.append(entry.getKey()).append("=").append(entry.getValue());
                stringBuilder.append("&");
            });
            stringBuilder.setLength(stringBuilder.length() - 1);
        }
        return stringBuilder.toString();
    }

    private List<EventDTO> getEvents() {
        return SessionManager.getUserSession().getUseCaseRequestContext().getEvents();
    }

    private String addURLPreffix(String url) {
        String prefix = SessionManager.getUserSession().getResourcesUrlPrefix();
        return prefix != null ? prefix + url : url;
    }

}
