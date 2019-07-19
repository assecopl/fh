package pl.fhframework.core.mail;

import org.apache.commons.mail.EmailException;

/**
 * Created by pawel.ruta on 2018-05-15.
 */
public interface IMailClient {
    void send(final String title, final String message) throws EmailException;

    void sendTo(final String title, final String message, final String... recipients) throws EmailException;
}
