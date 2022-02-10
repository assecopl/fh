package pl.fhframework.dp.commons.ws.wsclient;

import org.apache.wss4j.common.ext.WSPasswordCallback;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: dariuszs
 * Date: 16.10.13
 * Time: 18:17
 * To change this template use File | Settings | File Templates.
 */
public class KeystorePasswordCallback implements CallbackHandler {

    final String login;
    final String password;


    public KeystorePasswordCallback(String login, String password) {
        this.login = login;
        this.password = password;
    }

    /**
     * It attempts to get the password from the private
     * alias/passwords map.
     */
    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        for (int i = 0; i < callbacks.length; i++) {
            WSPasswordCallback pc = (WSPasswordCallback) callbacks[i];

            if (login != null && login.equals(pc.getIdentifier())) {
                pc.setPassword(password);
                return;
            }
        }
    }

}
