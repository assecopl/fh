package pl.fhframework.core.uc.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pl.fhframework.core.i18n.MessageService;
import pl.fhframework.core.logging.ErrorTranslator;
import pl.fhframework.core.logging.ICodeRangeLogger;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.core.logging.handler.NonResponsiveInformationHandler;
import pl.fhframework.core.messages.IMessages;
import pl.fhframework.core.uc.UseCaseContainer;

import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by pawel.ruta on 2018-11-26.
 */
@Component
public class RunUseCaseOnActionError extends RunUseCaseHandler implements IOnActionErrorHandler {
    private static String newline = System.getProperty("line.separator");

    @Autowired
    private IMessages messages;

    @Autowired
    private MessageService messageService;


    @Autowired
    private ICodeRangeLogger codeRangeLogger;

    @Autowired
    private ErrorTranslator errorTranslator;

    @Value("${initial.use.case:}")
    private String autostartedUseCase;

    @Value("${usecase.action.error.redirect:}")
    private String redirectUseCase;

    @Override
    public void onStartError(UseCaseContainer useCaseContainer, UseCaseContainer.UseCaseContext useCaseContext, RuntimeException error, boolean invalidPersistenceContext) {
        onError(useCaseContainer, useCaseContext, error, invalidPersistenceContext);
    }

    @Override
    public void onActionError(UseCaseContainer useCaseContainer, UseCaseContainer.UseCaseContext useCaseContext, RuntimeException error, boolean invalidPersistenceContext) {
        onError(useCaseContainer, useCaseContext, error, invalidPersistenceContext);
    }

    protected void onError(UseCaseContainer useCaseContainer, UseCaseContainer.UseCaseContext useCaseContext, RuntimeException error, boolean invalidPersistenceContext) {
        showMessageAndRunUseCase(useCaseContainer, messageTitle(), buildMessage(useCaseContainer, useCaseContext, error, invalidPersistenceContext), redirectUseCase);
    }

    @Override
    public String buildMessage(UseCaseContainer useCaseContainer, UseCaseContainer.UseCaseContext useCaseContext, RuntimeException error, boolean invalidPersistenceContext) {
        Optional<String> message = codeRangeLogger.resolveCodeRangeMessage(error);
        if (!message.isPresent()) {
            message = errorTranslator.translateError(error);;
        }
        if (!message.isPresent()) {
            message = Optional.ofNullable(FhLogger.resolveThrowableMessage(error));
        }
        FhLogger.errorSuppressed(message.get(), error);

        String sessionErrors = useCaseContext.getUserSession().getAwaitingErrorInformations().stream().map(NonResponsiveInformationHandler::buildSingleErrorMessage).collect(Collectors.joining(newline));
        useCaseContext.getUserSession().getAwaitingErrorInformations().clear();

        String baseMessage = messageService.getAllBundles().getMessage(IOnActionErrorHandler.ACTION_ERROR_MESSAGE);

        return baseMessage + newline + newline + message.get() + newline + newline + sessionErrors;
    }
}
