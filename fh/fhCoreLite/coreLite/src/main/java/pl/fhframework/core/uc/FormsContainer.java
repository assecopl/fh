package pl.fhframework.core.uc;

import lombok.AllArgsConstructor;
import pl.fhframework.core.FhFormException;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.events.UseCaseRequestContext;
import pl.fhframework.model.dto.cloud.CloudFormInfo;
import pl.fhframework.model.forms.Form;
import pl.fhframework.model.forms.FormState;
import pl.fhframework.model.forms.attribute.FormType;

import java.time.Instant;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Forms manager for UseCaseContainer
 */
public class FormsContainer {

    @AllArgsConstructor
    private static class HistoricalFormState {

        private FormState state;

        private Instant showingTimestamp;

        private Form<?> form;
    }

    private static final HistoricalFormState NON_EXISTING_STATE = new HistoricalFormState(FormState.CLOSED, null, null);

    private Map<String, HistoricalFormState> clientState = new LinkedHashMap<>();

    // list of all managed forms
    private List<Form<?>> managedForms = Collections.synchronizedList(new ArrayList<Form<?>>() {
        @Override
        public boolean add(Form<?> form) {
            if (this.contains(form)) {
                throw new FhFormException("Attempted to display already shown form with id: " + form.getId());
            }
            return super.add(form);
        }
    }); // there can only be one form with the same id

    public void showForm(Form form) {
        form.setShowingTimestamp(Instant.now());
        managedForms.add(0, form);
        recalculateStates();
    }

    public void closeForm(Form form) {
        if (!managedForms.remove(form)) {
            throw new FhFormException("Cannot close form which is not currently visible: " + form.getId());
        }
        recalculateStates();
    }

    public String logState() {
        StringBuilder msg = new StringBuilder("FORMS: [\n");
        for (Form<?> form : new ArrayList<>(managedForms)) {
            msg.append("\t\t")
                    .append(form.getClass().getSimpleName())
                    .append(" [").append(form.getState())
                    .append("] (").append(form.getAbstractUseCase().getUseCase().getClass().getSimpleName())
                    .append(") @ ").append(form.getShowingTimestamp().toString())
                    .append(" {id: ").append(form.getId())
                    .append(", variant: ").append(StringUtils.evl(form.getVariant(), ""))
                    .append("}\n");
        }
        msg.append("\n]");
        return msg.toString();
    }

    public void recalculateStates() {
        List<Form<?>> forms = new ArrayList<>(managedForms);
        // reset states to active / inactive
        for (Form<?> form : forms) {
            if (isFullyManaged(form)) {
                boolean isUseCaseActive = isSystemUseCase(form.getAbstractUseCase()) || isTopStackUseCase(form.getAbstractUseCase());
                form.setState(isUseCaseActive ? FormState.ACTIVE : FormState.INACTIVE_PENDING);
            }
        }

        // calculate state of a form based on forms above this form
        processedForms:
        for (int procIndex = 0; procIndex < forms.size(); procIndex++) {
            Form procForm = forms.get(procIndex);
            if (!isFullyManaged(procForm)) {
                continue;
            }

            FormType procType = procForm.getEffectiveFormType();
            // is processed form from system use case
            boolean isProcSystem = isSystemUseCase(procForm.getAbstractUseCase());

            for (int aboveIndex = 0; aboveIndex < forms.size() && aboveIndex < procIndex; aboveIndex++) {
                Form aboveForm = forms.get(aboveIndex);
                FormType aboveType = aboveForm.getEffectiveFormType();
                boolean isSameUseCase = procForm.getAbstractUseCase() == aboveForm.getAbstractUseCase();
                boolean isSameContainer = procForm.getContainer().equals(aboveForm.getContainer());

                // is above form from system use case
                boolean isAboveSystem = isSystemUseCase(aboveForm.getAbstractUseCase());

                // on the same stack when none of then is system use case
                boolean isSameUseCaseStack = !isProcSystem && !isAboveSystem;

                FormState targetState;
                if (procType.isModal()) { // processed is modal
                    if (aboveType == FormType.MODAL) {
                        if (isSameUseCase) {
                            targetState = FormState.CLOSED;
                        } else if (isSameUseCaseStack) {
                            if (aboveForm.getState().isDisplayed()) { // hidden modal cannot put shadow
                                targetState = FormState.SHADOWED;
                            } else {
                                targetState = FormState.INACTIVE_PENDING;
                            }
                        } else {
                            // other use case stack e.g. system use case
                            if (aboveForm.getState().isDisplayed()) { // hidden modal cannot put shadow
                                targetState = FormState.SHADOWED;
                            } else {
                                targetState = FormState.ACTIVE;
                            }
                        }
                    } else if (aboveType == FormType.MODAL_OVERFLOW) {
                        if (aboveForm.getState().isDisplayed()) { // hidden modal cannot put shadow
                            targetState = FormState.HIDDEN;
                        } else {
                            targetState = FormState.ACTIVE;
                        }
                    } else { // STANDARD
                        if (isSameUseCase) {
                            targetState = FormState.CLOSED;
                        } else if (isSameUseCaseStack) {
                            targetState = FormState.HIDDEN;
                        } else {
                            // other use case stack e.g. system use case
                            targetState = FormState.ACTIVE;
                        }
                    }
                } else { // processed is standard
                    if (aboveType == FormType.MODAL) {
                        if (aboveForm.getState().isDisplayed()) { // hidden modal cannot put shadow
                            targetState = FormState.SHADOWED;
                        } else {
                            targetState = FormState.ACTIVE;
                        }
                    } else if (aboveType == FormType.MODAL_OVERFLOW) {
                        if (aboveForm.getState().isDisplayed()) { // hidden modal cannot put shadow
                            targetState = FormState.SHADOWED;
                        } else {
                            targetState = FormState.ACTIVE;
                        }
                    } else {
                        if (isSameContainer) {
                            if (isSameUseCase) {
                                targetState = FormState.CLOSED;
                            } else {
                                targetState = FormState.HIDDEN;
                            }
                        } else {
                            targetState = FormState.ACTIVE;
                        }
                    }
                }

                // if state is CLOSED, this form is no longer managed
                if (targetState == FormState.CLOSED) {
                    // remove like that is not atomic operation, but should be
                    // remove from managedForms only if form is still at the same position from bottom (end)
                    int managedFormsIdx = managedForms.size() - (forms.size() - procIndex);
                    if (managedFormsIdx < managedForms.size() && managedForms.get(managedFormsIdx) == procForm) {
                        managedForms.remove(managedFormsIdx);
                    }
                    forms.remove(procIndex);
                    procIndex--; // when removing the form must shift the index back not to skip the form after the removed form
                    continue processedForms;
                }

                // set form state if target state is more restrictive
                procForm.setState(FormState.selectMoreRestrictive(procForm.getState(), targetState));
            }
        }

        boolean anyModalVisible = forms.stream().anyMatch(form -> form.getEffectiveFormType().isModal() && form.getState().isDisplayed());

        if (anyModalVisible) {
            // Nn client side modals is always over standard forms, even is they are shown later than this modal.
            // Any visible modal puts shadow on any visible non-modal.
            for (Form<?> form : forms) {
                if (!form.getEffectiveFormType().isModal()) {
                    form.setState(FormState.selectMoreRestrictive(form.getState(), FormState.SHADOWED));
                }
            }
        }
    }

    public void useCaseTerminated(IFormUseCaseContext uc) {
        managedForms.removeIf(form -> form.getAbstractUseCase().getRealUseCaseContext() == uc);
        recalculateStates();
    }

    public void useCaseStarted(IFormUseCaseContext uc) {
        recalculateStates();
    }

    public synchronized void commitStateToClient(UseCaseRequestContext requestContext) {
        Map<String, HistoricalFormState> currentClientState = buildCurrentState();

        // process currently managed forms
        currentClientState.forEach((id, current) -> {
            HistoricalFormState client = clientState.getOrDefault(id, NON_EXISTING_STATE);
            if (current.state.isDisplayed()) {
                if (client.state.isDisplayed()) {
                    // client showing form - check timestamp, if not equal re-display the form
                    if (!current.showingTimestamp.equals(client.showingTimestamp)) {
                        requestContext.getFormsToHide().add(current.form);
                        requestContext.getFormsToDisplay().add(current.form);
                    }
                } else {
                    // client is not showing the form - display it
                    requestContext.getFormsToDisplay().add(current.form);
                }
            } else if (client.state.isDisplayed()) {
                // client is showing the form - hide it
                requestContext.getFormsToHide().add(client.form);
            }
        });

        // close forms that are no longer managed
        clientState.forEach((id, client) -> {
            if (!currentClientState.containsKey(id) && client.state.isDisplayed()) {
                requestContext.getFormsToHide().add(client.form);
            }
        });

        clientState = currentClientState;
    }

    public synchronized void clearState() {
        clientState = new LinkedHashMap<>();
        managedForms.clear();
    }

    public Optional<Form<?>> findDisplayedFormById(String formId) {
        return managedForms.stream().filter(form -> form.getId().equals(formId) && form.getState().isDisplayed()).findFirst();
    }

    public Optional<Form<?>> findActiveFormById(String formId) {
        return managedForms.stream().filter(form -> form.getId().equals(formId) && form.getState() == FormState.ACTIVE).findFirst();
    }

    public void doForEachFullyManagedActiveForm(Consumer<Form<?>> action) {
        doForEachFullyManagedFormInState(FormState.ACTIVE, action);
    }

    public void doForEachFullyManagedForm(Consumer<Form<?>> action) {
        managedForms.stream().filter(this::isFullyManaged).forEach(action);
    }

    public void doForEachFullyManagedFormInState(FormState state, Consumer<Form<?>> action) {
        managedForms.stream().filter(form -> (form.getState() == state && isFullyManaged(form))).forEach(action);
    }

    public List<Form<?>> getUseCaseActiveForms(IFormUseCaseContext useCaseContext) {
        return managedForms.stream().filter(form -> (form.getState() == FormState.ACTIVE && form.getAbstractUseCase() == useCaseContext)).collect(Collectors.toList());
    }

    public List<Form<?>> getUseCaseForms(IFormUseCaseContext useCaseContext) {
        return managedForms.stream().filter(form -> (form.getAbstractUseCase() == useCaseContext)).collect(Collectors.toList());
    }

    public void updateCloudForms(IFormUseCaseContext useCaseContext, List<CloudFormInfo> cloudForms) {
        // remove forms of this use case (old state)
        managedForms.removeIf(form -> form.getAbstractUseCase() == useCaseContext);

        // add current cloud forms
        for (CloudFormInfo cloudForm : cloudForms) {
            Form<?> form = CloudFormInfo.toArtificialForm(cloudForm);
            form.setUseCase(useCaseContext);
            form.setFromCloud(true);
            managedForms.add(form);
        }

        sortFormsByShowingTimestamp();
        recalculateStates();
    }

    public List<Form<?>> getManagedForms() {
        return new ArrayList<>(managedForms);
    }

    protected boolean isTopStackUseCase(IFormUseCaseContext uc) {
        return uc.isTopStackUseCase();
    }

    protected boolean isSystemUseCase(IFormUseCaseContext uc) {
        return uc.isSystemUseCase();
    }

    private Map<String, HistoricalFormState> buildCurrentState() {
        Map<String, HistoricalFormState> currentState = new LinkedHashMap<>();
        for (int i = managedForms.size() - 1; i >= 0; i--) {
            Form form = managedForms.get(i);
            if (isFullyManaged(form)) {
                currentState.put(form.getId(), new HistoricalFormState(form.getState(), form.getShowingTimestamp(), form));
            }
        }
        return currentState;
    }

    private void sortFormsByShowingTimestamp() {
        Collections.sort(managedForms, new Comparator<Form<?>>() {
            @Override
            public int compare(Form<?> o1, Form<?> o2) {
                return o2.getShowingTimestamp().compareTo(o1.getShowingTimestamp());
            }
        });
    }

    /**
     * Checks if this form is fully managed by this form's container. Cloud forms are NOT fully managed.
     */
    private boolean isFullyManaged(Form<?> form) {
        return form.getAbstractUseCase().getRemoteServerName() == null;
    }
}
