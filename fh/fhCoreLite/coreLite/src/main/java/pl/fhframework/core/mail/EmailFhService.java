package pl.fhframework.core.mail;

import org.springframework.beans.factory.annotation.Autowired;
import pl.fhframework.core.CoreSystemFunction;
import pl.fhframework.core.security.annotations.SystemFunction;
import pl.fhframework.core.services.ExcludeOperation;
import pl.fhframework.core.services.FhService;

import java.util.List;
import java.util.Map;

/**
 * Created by pawel.ruta on 2018-05-16.
 */
@FhService(value = "email", categories = "email")
@SystemFunction(CoreSystemFunction.CORE_MAIL_SENDING)
public class EmailFhService {
    @Autowired
    private EmailService emailService;

    public void send(final String title, final String message, final List<String> recipients) {
        emailService.send(title, message, recipients);
    }

    public void send(final String title, final String message, final String recipient) {
        emailService.send(title, message, recipient);
    }

    @ExcludeOperation
    public void sendTemplate(final String title, final String templateMsg, Map<String, Object> params, Map<String, Boolean> sections, final List<String> recipients) {
        emailService.sendTemplate(title, templateMsg, params, sections, recipients);
    }
}
