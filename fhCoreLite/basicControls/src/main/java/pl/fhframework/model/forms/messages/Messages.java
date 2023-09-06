package pl.fhframework.model.forms.messages;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.fhframework.UserSession;
import pl.fhframework.binding.AdHocActionBinding;
import pl.fhframework.binding.AdHocModelBinding;
import pl.fhframework.binding.ModelBinding;
import pl.fhframework.binding.StaticBinding;
import pl.fhframework.configuration.FHConfiguration;
import pl.fhframework.core.i18n.MessageService;
import pl.fhframework.core.messages.Action;
import pl.fhframework.core.messages.IMessages;
import pl.fhframework.core.messages.MessagePopup;
import pl.fhframework.core.uc.UseCaseContainer;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.events.ViewEvent;
import pl.fhframework.helper.AutowireHelper;
import pl.fhframework.model.forms.Button;
import pl.fhframework.model.forms.Canvas;
import pl.fhframework.model.forms.OutputLabel;
import pl.fhframework.model.forms.Spacer;
import pl.fhframework.model.forms.attribute.FormType;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static pl.fhframework.model.forms.attribute.FormType.MODAL;
import static pl.fhframework.model.forms.attribute.FormType.MODAL_OVERFLOW;

/**
 * Class containing helper methods which creates dialogs.
 * Dialogs can be used as informative or
 * can be provided with actions.
 * Examples:
 * <pre>
 * <code>
 * public class MagicPU extends IUseCase&lt;...&gt; {
 *
 * <code>@Autowired private Messages messages</code>;
 *
 * public void doMagic() {
 * ...
 * messages.showMessage(getUserSession(), "Urgent infomation", Messages.Severity.INFO);
 * ...
 * messages.showActionMessage(getUserSession(), "Title", "Message", Messages.Severity.INFO,
 * ActionButton.get(
 * "Info",
 * ()-&gt; messages.showMessage(getUserSession(), "Success", "Info pressed", Messages.Severity.SUCCESS))
 * }
 * }
 * </code>
 * </pre>
 * Created by krzysztof.kobylarek on 2016-10-20.
 */
@Component
public class Messages implements IMessages {

    private static final String THIS_FOR_ACTION_NAME = "(EVENT)";

    private static String newline = System.getProperty("line.separator");

    private static final AtomicInteger ACTION_SEQ = new AtomicInteger(1);

    private static MessageService messageService;

    @Autowired
    private MessageService messageServiceWired;

    /**
     * Severity status os message
     */
    public enum Severity {
        SUCCESS("alert alert-success"),
        INFO("alert alert-info"),
        WARNING("alert alert-warning"),
        ERROR("alert alert-danger");

        @Getter
        String boostrapStyle = "";

        Severity(String style) {
            boostrapStyle = style;
        }
    }

    @PostConstruct
    public void init() {
        Messages.messageService = messageServiceWired;
    }

    /* *** Helper methods *** */

    /**
     * Helper method
     * Shows simple message with default title
     *
     * @param session user session
     * @param message actual message
     */
    public static MessagePopup showInfoMessage(UserSession session, String message) {
        return showInfoMessage(session, message, false);
    }

    /**
     * Helper method
     * Shows simple message with default title
     *
     * @param session user session
     * @param message actual message
     */
    public static MessagePopup showInfoMessageNoOverflow(UserSession session, String message) {
        return showInfoMessage(session, message, true);
    }

    /**
     * Helper method
     * Shows simple message with default title
     *
     * @param session user session
     * @param message actual message
     */
    protected static MessagePopup showInfoMessage(UserSession session, String message, boolean noOverflow) {
        String messageTitle = messageService.getAllBundles().getMessage(CoreKeysMessages.DIALOG_INFO_TITLE, "Information");
        return showMessage(session, messageTitle, message, Severity.INFO, noOverflow);
    }

    /**
     * Helper method
     * Shows message dialog and creates button provided with actions triggered when button is
     * pressed
     *
     * @param session     user session
     * @param title       message dialog title
     * @param textMessage actual message, instance of TextMessage
     * @param severity    severity of message
     * @param actions     actions to be triggered when button is pressed
     */
    public static MessagePopup showActionMessage(UserSession session, String title, TextMessage textMessage, Severity severity, ActionButton... actions) {
        return showActionMessage(session, title, textMessage.toMessageString(), severity, actions);
    }

    /**
     * Helper method
     * Shows message dialog and creates button provided with actions triggered when button is
     * pressed
     *
     * @param session  user session
     * @param title    message dialog title
     * @param message  actual message
     * @param severity severity of message
     * @param actions  actions to be triggered when button is pressed
     */

    //-------- -------- -------- -------- --------
    public static MessagePopup showActionMessage(UserSession session, String title, String message, Severity severity, ActionButton... actions) {
        MessagePopup dialog = builder(session)
                .withDialogTitle(title)
                .withMessage(message)
                .withButtonActions(Arrays.asList(actions))
                .withSeverityLevel(severity)
                .build();

        dialog.setFocusFirstElement(true);
        dialog.setBlockFocusForModal(true);
        dialog.showDialog();

        return dialog;
    }

    /**
     * Helper method
     * Shows dialog with one close button
     *
     * @param session     user session
     * @param dialogTitle message dialog title
     * @param message     actual message
     * @param severity    severity of message
     */
    public static MessagePopup showMessage(UserSession session, String dialogTitle, String message, Severity severity) {
        return showMessage(session, dialogTitle, message, severity, false);
    }

    /**
     * Helper method
     * Shows dialog with one close button
     *
     * @param session     user session
     * @param dialogTitle message dialog title
     * @param message     actual message
     * @param severity    severity of message
     */
    public static MessagePopup showMessageNoOverflow(UserSession session, String dialogTitle, String message, Severity severity) {
        return showMessage(session, dialogTitle, message, severity, true);
    }

    /**
     * Helper method
     * Shows dialog with one close button
     *
     * @param session     user session
     * @param dialogTitle message dialog title
     * @param message     actual message
     * @param severity    severity of message
     */
    protected static MessagePopup showMessage(UserSession session, String dialogTitle, String message, Severity severity, boolean noOverflow) {
        MessagePopup dialog = builder(session)
                .withDialogTitle(dialogTitle)
                .withMessage(message)
                .withSeverityLevel(severity)
                .withFormType(noOverflow ? MODAL : MODAL_OVERFLOW)
                .build();

        UseCaseContainer.PopupMessageUseCaseContextMessage<?, ?> usecaseWrapper = (UseCaseContainer.PopupMessageUseCaseContextMessage) dialog.getAbstractUseCase();

        Button closeButton = new Button(dialog);
        closeButton.setWidth("md-3");
        String actionName = nextActionName();

        String closeBtnLabel = messageService.getAllBundles().getMessage(CoreKeysMessages.DIALOG_BTN_CLOSE, "Close");
        closeButton.setLabelModelBinding(closeButton.createAdHocModelBinding(closeBtnLabel));
        closeButton.setOnClick(new AdHocActionBinding(actionName + THIS_FOR_ACTION_NAME, dialog, dialog)); // FormElement.runAction workaround
        dialog.addSubcomponent(closeButton);
        usecaseWrapper.setActionViewEvent(actionName, Messages::close);
        dialog.setFocusFirstElement(true);
        dialog.setBlockFocusForModal(true);
        dialog.configure(usecaseWrapper, null);
        usecaseWrapper.showForm(dialog, false);

        return dialog;
    }


    /**
     * Helper method
     *
     * @param session
     * @param message
     * @param error
     */
    public static MessagePopup showErrorMessage(UserSession session, String message, Throwable error) {
        return showErrorMessage(session, message, error, false);
    }

    /**
     * Helper method
     *
     * @param session
     * @param message
     * @param error
     */
    public static MessagePopup showErrorMessageNoOverflow(UserSession session, String message, Throwable error) {
        return showErrorMessage(session, message, error, true);
    }

    /**
     * Helper method
     *
     * @param session
     * @param message
     * @param error
     */
    protected static MessagePopup showErrorMessage(UserSession session, String message, Throwable error, boolean noOverflow) {
        StringBuilder sb = new StringBuilder();
        sb.append(StringUtils.nullToEmpty(message)).append(newline);
        if (error != null) {
            sb.append(newline).append("[ ").append(error.getMessage()).append(" ]");
        }
        String errorTitle = messageService.getAllBundles().getMessage(CoreKeysMessages.DIALOG_ERROR_TITLE, "Error");
        return showMessage(session, errorTitle, message, Severity.ERROR, noOverflow);
    }

    /**
     * Helper method
     * Shows simple message with default title
     *
     * @param session user session
     * @param message actual message
     */
    public MessagePopup showInfo(UserSession session, String message, Action action) {
        return showInfo(session, null, message, action);
    }

    /**
     * Helper method
     * Shows simple message with default title
     *
     * @param session user session
     * @param message actual message
     */
    public MessagePopup showInfo(UserSession session, String title, String message, Action action) {
        if (title == null) {
            title = messageService.getAllBundles().getMessage(CoreKeysMessages.DIALOG_INFO_TITLE, "Information");
        }

        return showDialog(session, title, message, Severity.INFO, action);
    }

    /**
     * Helper method
     * Shows simple message with default title
     *
     * @param session user session
     * @param message actual message
     */
    public MessagePopup showConfirmation(UserSession session, String message, Action action) {
        return showConfirmation(session, null, message, action);
    }

    /**
     * Helper method
     * Shows simple message with default title
     *
     * @param session user session
     * @param message actual message
     */
    public MessagePopup showConfirmation(UserSession session, String title, String message, Action action) {
        if (title == null) {
            title = messageService.getAllBundles().getMessage(CoreKeysMessages.DIALOG_CONFIRMATION_TITLE, "Confirmation");
        }

        Messages.Builder confirmDialogBuilder = Messages.builder(session)
                .withDialogTitle(title)
                .withSeverityLevel(Severity.INFO)
                .withMessage(message)
                .withButtonAction(ActionButton.get(messageService.getAllBundles().getMessage(CoreKeysMessages.DIALOG_BTN_YES, "Yes"), (viewEvent) -> {
                    Messages.close(viewEvent);
                    action.doAction();
                }))
                .withButtonAction(ActionButton.getClose(messageService.getAllBundles().getMessage(CoreKeysMessages.DIALOG_BTN_NO, "No")));

        MessagePopup confirmDialog = confirmDialogBuilder.build();
        confirmDialog.setFocusFirstElement(true);
        confirmDialog.setBlockFocusForModal(true);
        confirmDialog.showDialog();

        return confirmDialog;
    }

    /**
     * Helper method
     *
     * @param session
     * @param message
     */
    public MessagePopup showError(UserSession session, String message, Action action) {
        return showError(session, null, message, action);
    }

    /**
     * Helper method
     *
     * @param session
     * @param message
     */
    public MessagePopup showError(UserSession session, String title, String message, Action action) {
        if (title == null) {
            title = messageService.getAllBundles().getMessage(CoreKeysMessages.DIALOG_ERROR_TITLE, "Error");
        }
        return showDialog(session, title, message, Severity.ERROR, action);
    }

    private MessagePopup showDialog(UserSession session, String title, String message, Severity severity, Action action) {
        MessagePopup dialog = builder(session)
                .withDialogTitle(title)
                .withMessage(message)
                .withSeverityLevel(severity)
                .build();

        UseCaseContainer.PopupMessageUseCaseContextMessage<?, ?> usecaseWrapper = (UseCaseContainer.PopupMessageUseCaseContextMessage) dialog.getAbstractUseCase();

        Button closeButton = new Button(dialog);
        closeButton.setWidth("md-3");
        String actionName = nextActionName();

        String closeBtnLabel = messageService.getAllBundles().getMessage(CoreKeysMessages.DIALOG_BTN_CLOSE, "Close");
        closeButton.setLabelModelBinding(closeButton.createAdHocModelBinding(closeBtnLabel));
        closeButton.setOnClick(() -> {
            session.getUseCaseContainer().getFormsContainer().closeForm(dialog);
            if (action != null) {
                action.doAction();
            }
        });

        dialog.addSubcomponent(closeButton);
        usecaseWrapper.setActionViewEvent(actionName, Messages::close);
        dialog.setFocusFirstElement(true);
        dialog.setBlockFocusForModal(true);
        dialog.configure(usecaseWrapper, null);
        usecaseWrapper.showForm(dialog, false);

        return dialog;
    }

    /**
     * Shows message represented by message builder.
     *
     * @param builder Message builder filled with message data and action. Created to provide more flexibility than helper methods
     */
    public static void showMessage(Builder builder) {
        builder.build().showDialog();
    }

    /**
     * Action to close message dialog
     *
     * @param viewEvent
     */
    public static void close(ViewEvent viewEvent) {
        ((UseCaseContainer.PopupMessageUseCaseContextMessage) viewEvent.getSourceForm().getAbstractUseCase()).close(viewEvent);
    }

    // private methods

    private static String nextActionName() {
        return "messageAction" + ACTION_SEQ.getAndIncrement();
    }

    /**
     * Helper class used to facilitate creation of messages
     */
    public static class TextMessage {
        List<String> lines = new LinkedList<>();

        public TextMessage(String message) {
            lines.add(message);
        }

        public TextMessage() {
        }

        public TextMessage newLine(String message) {
            lines.add(message);
            return this;
        }

        public String toMessageString() {
            return lines.stream().collect(Collectors.joining("<br/>"));
        }
    }

    public static class Builder {

        private Optional<String>
                messageTitle = Optional.empty(),
                dialogId = Optional.empty(),
                dialogTitle = Optional.empty(),
                messageData = Optional.empty();

        private Optional<Severity> severity = Optional.empty();

        private Optional<FormType> formType = Optional.empty();

        private List<ActionButton> actions = new LinkedList<>();
        private UserSession session;
        private boolean bindableText = false;

        Builder(UserSession session) {
            this.session = session;
        }

        @Deprecated
        public Builder withMessageTitle(String messageTitle) {
            this.messageTitle = Optional.ofNullable(messageTitle);
            return this;
        }

        public Builder withDialogId(String dialogId) {
            this.dialogId = Optional.ofNullable(dialogId);
            return this;
        }

        public Builder withDialogTitle(String dialogTitle) {
            this.dialogTitle = Optional.ofNullable(dialogTitle);
            return this;
        }

        public Builder withMessage(String messageData) {
            this.messageData = Optional.ofNullable(messageData);
            return this;
        }

        public Builder withButtonAction(ActionButton action) {
            this.actions.add(action);
            return this;
        }

        public Builder withButtonActions(Collection<ActionButton> actions) {
            this.actions.addAll(actions);
            return this;
        }

        public Builder withSeverityLevel(Severity severityLevel) {
            this.severity = Optional.ofNullable(severityLevel);
            return this;
        }

        public Builder enableBindableText() {
            this.bindableText = true;
            return this;
        }

        public Builder withFormType(FormType formType) {
            this.formType = Optional.ofNullable(formType);
            return this;
        }

        public MessagePopup build() {
            MessagePopup dialog = createDialog(session, dialogId, dialogTitle, messageTitle, messageData, severity, formType);

            for (ActionButton actionButton : actions) {
                Button button = new Button(dialog);
                button.setWidth("sm-4");
                String actionName = nextActionName();
                button.setLabelModelBinding(binding(actionButton.getButtonLabel(), button));
                if(actionButton.getButtonStyle() != null) {
                    button.setStyleModelBinding(binding(actionButton.getButtonStyle(), button));
                }
                button.setOnClick(new AdHocActionBinding(actionName, dialog, dialog));
                dialog.addSubcomponent(button);
                UseCaseContainer.PopupMessageUseCaseContextMessage useCaseWrapper = (UseCaseContainer.PopupMessageUseCaseContextMessage) dialog.getAbstractUseCase();

                if (actionButton.getAction() != null)
                    useCaseWrapper.setAction(actionName, actionButton.getAction());
                if (actionButton.getViewEventAction() != null) {
                    useCaseWrapper.setActionViewEvent(actionName, actionButton.getViewEventAction());
                    button.setOnClick(new AdHocActionBinding(actionName + THIS_FOR_ACTION_NAME, dialog, dialog));
                }

            }
            return dialog;
        }

        /*
         *  Creates internal structure of message dialog
         */
        private MessagePopup createDialog(UserSession session,
                                          Optional<String> dialogId,
                                          Optional<String> dialogTitle,
                                          Optional<String> messageTitle,
                                          Optional<String> messageData,
                                          Optional<Severity> severity, Optional<FormType> formType) {

            MessagePopup dialog = new MessagePopup();

            //dialog.setId("popup");
            String mainContainerName = AutowireHelper.getApplicationProperty("fhframework.mainContainer");
            if (mainContainerName == null || mainContainerName.isEmpty()) {
                mainContainerName = FHConfiguration.DEFAULT_MAIN_CONTAINER;
            }
            dialog.setDeclaredContainer(mainContainerName);

            dialog.setFormType(formType.orElse(MODAL_OVERFLOW)); // by default Messages overflow other modal forms (if are any)
            dialog.setWidth("md-2");
            dialog.setLabelModelBinding(binding(dialogTitle.isPresent() ? dialogTitle.get() : "", dialog));

            if (dialogId.isPresent())
                dialog.setId(dialogId.get());

            dialog.addSubcomponent(new Spacer(dialog));
            Spacer spacer = new Spacer(dialog);
            spacer.setWidth("md-4");
            dialog.addSubcomponent(spacer);

            Canvas canvas = new Canvas(dialog);
            if (severity.isPresent())
                canvas.setStyleClasses(severity.get().getBoostrapStyle());

            dialog.addSubcomponent(canvas);

            OutputLabel label = new OutputLabel(dialog);
            label.setWidth("md-12");
            label.setValueBinding(binding(messageData.orElse(null), label));
            canvas.addSubcomponent(label);

            Spacer spacer2 = new Spacer(dialog);
            spacer2.setWidth("md-12");
            spacer2.setHeight("20px");
            dialog.addSubcomponent(spacer2);

            UseCaseContainer useCaseContainer = session.getUseCaseContainer();
            UseCaseContainer.PopupMessageUseCaseContextMessage popupMessageUseCaseContextMessage = useCaseContainer.new PopupMessageUseCaseContextMessage(useCaseContainer.getCurrentUseCaseContextOrActionSystemUseCase());

            dialog.setUseCase(popupMessageUseCaseContextMessage);

            return dialog;
        }

        private ModelBinding<?> binding(String text, pl.fhframework.model.forms.Component component) {
            if (bindableText && text != null && !text.isEmpty()) {
                return new AdHocModelBinding<>(component.getForm(), component, text);
            } else {
                return new StaticBinding<>(text);
            }
        }
    }

    public static Builder builder(UserSession userSession) {
        return new Builder(userSession);
    }
}
