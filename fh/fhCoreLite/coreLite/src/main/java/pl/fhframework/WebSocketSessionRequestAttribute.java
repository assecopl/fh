package pl.fhframework;

import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;


public class WebSocketSessionRequestAttribute implements RequestAttributes {

    private Map<String, Object> requestAttributes = new HashMap<>();

    private HttpSession httpSession;

    public WebSocketSessionRequestAttribute(HttpSession httpSession) {
      this.httpSession= httpSession;
    }

    @Override
    public Object getAttribute(String name, int scope) {
        if (scope == SCOPE_REQUEST) {
            return requestAttributes.get(name);
        } else {
            return httpSession.getAttribute(name);
        }
    }

    @Override
    public void setAttribute(String name, Object value, int scope) {
        if (scope == SCOPE_REQUEST) {
            requestAttributes.put(name, value);
        } else {
            httpSession.setAttribute(name, value);
        }
    }

    @Override
    public void removeAttribute(String name, int scope) {
        if (scope == SCOPE_REQUEST) {
            requestAttributes.remove(name);
        } else {
            httpSession.removeAttribute(name);
        }
    }

    @Override
    public String[] getAttributeNames(int scope) {
        if (scope == SCOPE_REQUEST) {
            return StringUtils.toStringArray(requestAttributes.keySet());
        } else {
            return StringUtils.toStringArray(httpSession.getAttributeNames());
        }

    }

    @Override
    public void registerDestructionCallback(String name, Runnable callback, int scope) {

    }

    @Override
    public Object resolveReference(String key) {
        if (key.equals(REFERENCE_SESSION)) {
            return httpSession;
        } else {
            throw new UnsupportedOperationException("Can't resolve reference to request in websocket connection");
        }


    }

    @Override
    public String getSessionId() {
        return  httpSession.getId();
    }

    @Override
    public Object getSessionMutex() {
        return WebUtils.getSessionMutex(httpSession);
    }
}
