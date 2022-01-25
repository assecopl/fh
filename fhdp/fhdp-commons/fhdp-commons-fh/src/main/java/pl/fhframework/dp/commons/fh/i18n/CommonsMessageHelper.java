package pl.fhframework.dp.commons.fh.i18n;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.fhframework.core.i18n.MessageService;

@Component
public class CommonsMessageHelper {
    @Autowired
    private MessageService messageService;

    public String getMessage(String messageKey, Object... params) {
        return messageService.getBundle(CommonsMessageSourceConfig.SOURCE_NAME).getMessage(messageKey, params);
    }
}
