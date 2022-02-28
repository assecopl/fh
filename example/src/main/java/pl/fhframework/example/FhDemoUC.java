package pl.fhframework.example;

import org.springframework.beans.factory.annotation.Autowired;
import pl.fhframework.UserSession;
import pl.fhframework.annotations.Action;
import pl.fhframework.core.session.UserSessionRepository;
import pl.fhframework.core.uc.IInitialUseCase;
import pl.fhframework.core.uc.UseCase;


@UseCase
public class FhDemoUC implements IInitialUseCase {
    @Autowired
    private UserSessionRepository userSessionRepository;

    @Override
    public void start() {
        UserSession userSession = userSessionRepository.getAllUserSessions().stream().findFirst().get();
        userSession.getAttributes().put("id", "123");
        showForm(FhDemoForm.class, "");
    }

    @Action
    public void onClose() {
        userSessionRepository.removeObsoleteSessionsWithAttribute("id", "123");
    }
}
