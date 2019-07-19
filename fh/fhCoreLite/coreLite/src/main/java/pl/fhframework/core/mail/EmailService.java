package pl.fhframework.core.mail;

import org.apache.commons.mail.EmailException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.fhframework.core.FhException;
import pl.fhframework.core.util.TemplateVariablesExpander;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

/**
 * Created by pawel.ruta on 2018-05-16.
 */
@Service
public class EmailService {
    @Autowired
    private IMailClient mailClient;

    private TemplateVariablesExpander templateVariablesExpander;

    @PostConstruct
    void init() {
        templateVariablesExpander = new TemplateVariablesExpander();
    }

    public void send(final String title, final String message, final List<String> recipients) {
        try {
            mailClient.sendTo(title, message, recipients.toArray(new String[]{}));
        } catch (EmailException ex) {
            throw new FhException(ex);
        }
    }

    public void send(final String title, final String message, final String recipient) {
        try {
            mailClient.sendTo(title, message, recipient);
        } catch (EmailException ex) {
            throw new FhException(ex);
        }
    }

    public void sendTemplate(final String title, final String templateMsg, Map<String, Object> params, Map<String, Boolean> sections, final List<String> recipients) {
        try {
            mailClient.sendTo(templateVariablesExpander.expand(title, params, sections), templateVariablesExpander.expand(templateMsg, params, sections), recipients.toArray(new String[]{}));
        } catch (EmailException ex) {
            throw new FhException(ex);
        }
    }
}
