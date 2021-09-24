package pl.fhframework.core.uc.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import pl.fhframework.FormsHandler;
import pl.fhframework.core.logging.handler.ErrorHandlingConfiguration;
import pl.fhframework.core.uc.FormsContainer;
import pl.fhframework.core.uc.UseCaseContainer;
import pl.fhframework.ReflectionUtils;

import javax.annotation.PostConstruct;

/**
 * Created by pawel.ruta on 2018-11-26.
 */
@Component
public class UseCaseErrorsHandler implements INoFormHandler, IOnActionErrorHandler, IOnEventHandleError {
    @Value("${usecase.empty.form.stack.handler:pl.fhframework.core.uc.handlers.NoFormRunUseCase}")
    private String noFormHandlerClass;

    @Value("${usecase.action.error.handler:pl.fhframework.core.uc.handlers.RethrowOnActionError}")
    private String onActionErrorHandlerClass;

    @Autowired
    private ApplicationContext applicationContext;

    private INoFormHandler noFormHandler;

    private IOnActionErrorHandler onActionErrorHandler;

    @Autowired
    private ErrorHandlingConfiguration errorHandlingConfiguration;


    @PostConstruct
    void init() {
        noFormHandler = (INoFormHandler) applicationContext.getBean(ReflectionUtils.getClassForName(noFormHandlerClass));
        onActionErrorHandler = (IOnActionErrorHandler) applicationContext.getBean(ReflectionUtils.getClassForName(onActionErrorHandlerClass));
    }

    @Override
    public void handleNoFormCase(UseCaseContainer useCaseContainer, UseCaseContainer.UseCaseContext eventUseCaseContext, FormsContainer formsContainer) {
        noFormHandler.handleNoFormCase(useCaseContainer, eventUseCaseContext, formsContainer);
    }

    @Override
    public void onStartError(UseCaseContainer useCaseContainer, UseCaseContainer.UseCaseContext useCaseContext, RuntimeException error, boolean invalidPersistenceContext) {
        onActionErrorHandler.onStartError(useCaseContainer, useCaseContext, error, invalidPersistenceContext);
    }

    @Override
    public void onActionError(UseCaseContainer useCaseContainer, UseCaseContainer.UseCaseContext useCaseContext, RuntimeException error, boolean invalidPersistenceContext) {
        onActionErrorHandler.onActionError(useCaseContainer, useCaseContext, error, invalidPersistenceContext);
    }

    @Override
    public void onEventHandleError(UseCaseContainer useCaseContainer, UseCaseContainer.UseCaseContext useCaseContext, Throwable error, FormsHandler formsHandler, String requestId) {
        errorHandlingConfiguration.errorInformationHandler().onEventHandleError(useCaseContainer, useCaseContext, error, formsHandler, requestId);
    }

    @Override
    public String messageTitle() {
        return null;
    }

}
