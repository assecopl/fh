package pl.fhframework.core.mail;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import pl.fhframework.core.FhException;

/**
 * Created by pawel.ruta on 2018-05-15.
 */
@Service
@ConfigurationProperties(prefix = "fh.mail.server")
@Getter
@Setter
public class MailClient implements IMailClient {
    boolean ssl;

    String hostName;

    String smtpPort;

    String from;

    String login;

    String passwd;

    String to;

    @Override
    public void send(final String title, final String message) throws EmailException {
        if (!StringUtils.isEmpty(to)) {
            sendTo(title, message, to);
        }
    }

    @Override
    public void sendTo(final String title, final String message, final String... recipients) throws EmailException {
        if (StringUtils.isEmpty(hostName) || StringUtils.isEmpty(smtpPort)) {
            throw new FhException("Email server configuration is missing");
        }
        if (recipients != null && recipients.length > 0) {
            Email email = new SimpleEmail();
            email.setHostName(hostName);
            email.setSslSmtpPort(smtpPort);
            email.setSmtpPort(Integer.parseInt(smtpPort));
            if (!StringUtils.isEmpty(login)) {
                email.setAuthenticator(new DefaultAuthenticator(login, passwd));
            }
            email.setSSLOnConnect(ssl);
            email.setFrom(from);
            email.setSubject(title);
            email.setMsg(message);
            email.addTo(recipients);
            email.send();
        }
        else {
            throw new FhException("Recipient is undefined");
        }
    }
}
