package pl.fhframework;

import pl.fhframework.core.util.StringUtils;

import javax.servlet.http.HttpSession;

public class ClientAddressProvider {
    public static String HTTP_SESSION_KEY_NAME = "real_client_address";

    /**
     * Tries to pull out the client address from derived httpSession. If it cannot take it from HttpSession returns client address from derived description
     *
     * @return Ip address combined with port.
     */
    public static String getClientAddress(HttpSession httpSession, SessionDescription description) {
        if (httpSession != null) {
            String derivedClientAddress = (String) httpSession.getAttribute(HTTP_SESSION_KEY_NAME);
            if (!StringUtils.isNullOrEmpty(derivedClientAddress)) {
                return derivedClientAddress;
            }
        }
        return getClientAddress(description);
    }

    /**
     * Pull the client address (ip combined with port) from derived description
     *
     * @return return string with combined ip and port or returns null if there is no description or description doesn't contain any value.
     */
    public static String getClientAddress(SessionDescription description) {
        if (description instanceof UserSessionDescription) {
            String value = ((UserSessionDescription) description).getUserAddress();
            if (!StringUtils.isNullOrEmpty(value)) {
                if (value.charAt(0) == '/') {
                    return value.substring(1);
                }
                return value;
            }
        }
        return null;
    }
}
