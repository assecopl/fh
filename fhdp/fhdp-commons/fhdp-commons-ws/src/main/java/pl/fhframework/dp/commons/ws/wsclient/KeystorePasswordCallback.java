package pl.fhframework.dp.commons.ws.wsclient;

import org.apache.wss4j.common.ext.WSPasswordCallback;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import java.io.IOException;

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
