package pl.fhframework.dp.commons.services.i18n;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.fhframework.core.i18n.MessageService;

@Component
public class ServicesMessageHelper {
    @Autowired
    private MessageService messageService;

    public String getMessage(String messageKey, Object... params) {
        return messageService.getBundle(ServicesMessageSourceConfig.SOURCE_NAME).getMessage(messageKey, params);
    }
}
