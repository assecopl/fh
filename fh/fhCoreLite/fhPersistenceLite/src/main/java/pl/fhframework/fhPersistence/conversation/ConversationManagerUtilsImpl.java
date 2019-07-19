package pl.fhframework.fhPersistence.conversation;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import pl.fhframework.core.session.scope.SessionScope;
import pl.fhframework.fhPersistence.transaction.ConversationStatelessManagerImpl;
import pl.fhframework.SessionManager;

import javax.annotation.PostConstruct;

/**
 * Created by pawel.ruta on 2018-03-19.
 */
@Component
@Scope(scopeName = SessionScope.SESSION_SCOPE, proxyMode = ScopedProxyMode.INTERFACES)
public class ConversationManagerUtilsImpl implements ConversationManagerUtils {
    @Autowired
    private ApplicationContext applicationContext;

    @Getter
    private ConversationManager conversationManager;

    @PostConstruct
    public void init() {
        if (SessionManager.getUserSession() != null) {
            conversationManager = applicationContext.getBean(ConversationManager.class);
        }
        if (SessionManager.getNoUserSession() != null) {
            conversationManager = new ConversationStatelessManagerImpl();
        }
    }
}
