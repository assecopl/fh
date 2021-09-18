package pl.fhframework;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.core.session.UserSessionRepository;
import pl.fhframework.io.FileService;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Component
public class FhEventListener {

    @Autowired
    private FileService fileService;

    @Autowired
    private UserSessionRepository userSessionRepository;

    @PostConstruct
    public void init() {
        userSessionRepository.addUserSessionDestroyedListener(session -> {
            session.clearUseCaseStack();
            fileService.deleteUserTemporaryFiles(session);
        });
    }

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent refreshedEvent) {
        try {
            fileService.deleteAllTemporaryFiles();
        } catch (IOException e) {
            FhLogger.error("Error during deleting temporary directory", e);
        }
    }
}

