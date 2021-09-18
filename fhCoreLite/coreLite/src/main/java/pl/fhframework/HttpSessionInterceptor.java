package pl.fhframework;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import javax.servlet.http.HttpSession;
import java.util.Map;


public class HttpSessionInterceptor extends HttpSessionHandshakeInterceptor {
    public HttpSessionInterceptor() {
        setCreateSession(true); // for guests
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        boolean res = super.beforeHandshake(request, response, wsHandler, attributes);
        HttpSession httpSession = getSession(request);
        WebSocketSessionManager.prepareHttpSession(httpSession);
        attributes.put(WebSocketSessionManager.HTTP_SESSION_KEY, httpSession);
        return res;
    }

    private HttpSession getSession(ServerHttpRequest request) {
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest serverRequest = (ServletServerHttpRequest) request;
            return serverRequest.getServletRequest().getSession(isCreateSession());
        }
        return null;
    }
}
