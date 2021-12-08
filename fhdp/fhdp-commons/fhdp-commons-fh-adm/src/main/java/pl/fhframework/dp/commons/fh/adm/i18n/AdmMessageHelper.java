package pl.fhframework.dp.commons.fh.adm.i18n;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.fhframework.core.i18n.MessageService;
import pl.fhframework.event.EventRegistry;
import pl.fhframework.event.dto.NotificationEvent;

/**
 * @author Tomasz Kozlowski (created on 24.02.2021)
 */
@Component
@RequiredArgsConstructor
public class AdmMessageHelper {

    public static final String PERMISSION_SAVED_MESSAGE = "adm.message.permissions.saved";
    public static final String PERMISSION_DELETED_MESSAGE = "adm.message.permissions.deleted";
    public static final String ROLE_SAVED_MESSAGE = "adm.message.role.saved";
    public static final String ROLE_DELETED_MESSAGE = "adm.message.role.deleted";
    public static final String ROLE_EXISTS_MESSAGE = "adm.roles.create.validation.duplicated_role";

    private final MessageService messageService;
    private final EventRegistry eventRegistry;

    public String getMessage(String key, Object... args) {
        return messageService.getBundle(AdmMessageSourceConfig.SOURCE_NAME)
                .getMessage(key, args);
    }

    public void notifyMessage(NotificationEvent.Level level, String key, Object... args) {
        eventRegistry.fireNotificationEvent(level, getMessage(key, args));
    }

}
