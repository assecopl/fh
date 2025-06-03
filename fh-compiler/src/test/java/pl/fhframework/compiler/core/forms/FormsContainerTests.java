package pl.fhframework.compiler.core.forms;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pl.fhframework.binding.StaticBinding;
import pl.fhframework.core.uc.FormsContainer;
import pl.fhframework.core.uc.IFormUseCaseContext;
import pl.fhframework.helper.AutowireHelper;
import pl.fhframework.model.forms.AdHocForm;
import pl.fhframework.model.forms.Form;
import pl.fhframework.model.forms.FormState;
import pl.fhframework.model.forms.attribute.FormType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

/**
 * Tests of FormsContainer class
 */
public class FormsContainerTests {

    private FakeUseCaseContext UC_SYS_A = new FakeUseCaseContext();
    private FakeUseCaseContext UC_SYS_B = new FakeUseCaseContext();
    private FakeUseCaseContext UC_A = new FakeUseCaseContext();
    private FakeUseCaseContext UC_B = new FakeUseCaseContext();
    private FakeUseCaseContext UC_C = new FakeUseCaseContext();

    private static final String CONTAINER_A = "CONTAINER_A";
    private static final String CONTAINER_B = "CONTAINER_B";
    private static final String CONTAINER_C = "CONTAINER_C";
    private static final String CONTAINER_D = "CONTAINER_D";

    private List<FakeUseCaseContext> systemUseCases = new ArrayList<>(Arrays.asList(UC_SYS_A, UC_SYS_B));

    private Stack<FakeUseCaseContext> ucStack = new Stack<>();

    private FormsContainer formsContainer;

    @Before
    public void initContainer() {
        AutowireHelper.disable();
        formsContainer = new FormsContainer() {
            @Override
            protected boolean isTopStackUseCase(IFormUseCaseContext uc) {
                return !ucStack.isEmpty() && ucStack.peek() == uc;
            }

            @Override
            protected boolean isSystemUseCase(IFormUseCaseContext uc) {
                return systemUseCases.contains(uc);
            }
        };
    }

    @Test
    public void testStateSingleForms() {
        showForm("sys1", FormType.STANDARD, CONTAINER_B, UC_SYS_A);
        showForm("sys2", FormType.STANDARD, CONTAINER_C, UC_SYS_B);

        runUC(UC_A);
        showForm("main", FormType.STANDARD, CONTAINER_A, UC_A);

        assertForm("main", FormState.ACTIVE);
        assertForm("sys2", FormState.ACTIVE);
        assertForm("sys1", FormState.ACTIVE);
    }

    @Test
    public void testStateSingleFormsPlusModal() {
        showForm("sys1", FormType.STANDARD, CONTAINER_B, UC_SYS_A);
        showForm("sys2", FormType.STANDARD, CONTAINER_C, UC_SYS_B);

        runUC(UC_A);
        showForm("main", FormType.STANDARD, CONTAINER_A, UC_A);
        showForm("modal", FormType.MODAL, CONTAINER_A, UC_A);

        assertForm("modal", FormState.ACTIVE);
        assertForm("main", FormState.SHADOWED);
        assertForm("sys2", FormState.SHADOWED);
        assertForm("sys1", FormState.SHADOWED);

        closeForm("modal");

        assertForm("modal", FormState.CLOSED);
        assertForm("main", FormState.ACTIVE);
        assertForm("sys2", FormState.ACTIVE);
        assertForm("sys1", FormState.ACTIVE);
    }

    @Test
    public void testStateSingleFormsPlusModalOtherUC() {
        showForm("sys1", FormType.STANDARD, CONTAINER_B, UC_SYS_A);
        showForm("sys2", FormType.STANDARD, CONTAINER_C, UC_SYS_B);

        runUC(UC_A);
        showForm("main", FormType.STANDARD, CONTAINER_A, UC_A);

        runUC(UC_B);
        showForm("modal", FormType.MODAL, CONTAINER_A, UC_B);

        assertForm("modal", FormState.ACTIVE);
        assertForm("main", FormState.SHADOWED);
        assertForm("sys2", FormState.SHADOWED);
        assertForm("sys1", FormState.SHADOWED);

        runUC(UC_C);
        showForm("modal2", FormType.MODAL, CONTAINER_A, UC_C);

        assertForm("modal2", FormState.ACTIVE);
        assertForm("modal", FormState.SHADOWED);
        assertForm("main", FormState.SHADOWED);
        assertForm("sys2", FormState.SHADOWED);
        assertForm("sys1", FormState.SHADOWED);

        terminateUC(UC_C);

        assertForm("modal2", FormState.CLOSED);
        assertForm("modal", FormState.ACTIVE);
        assertForm("main", FormState.SHADOWED);
        assertForm("sys2", FormState.SHADOWED);
        assertForm("sys1", FormState.SHADOWED);

        terminateUC(UC_B);

        assertForm("modal", FormState.CLOSED);
        assertForm("main", FormState.ACTIVE);
        assertForm("sys2", FormState.ACTIVE);
        assertForm("sys1", FormState.ACTIVE);
    }

    @Test
    public void testStateOtherContainer() {
        runUC(UC_A);
        showForm("contA", FormType.STANDARD, CONTAINER_A, UC_A);
        showForm("contB", FormType.STANDARD, CONTAINER_B, UC_A);

        assertForm("contB", FormState.ACTIVE);
        assertForm("contA", FormState.ACTIVE);

        showForm("contB2", FormType.STANDARD, CONTAINER_B, UC_A);

        assertForm("contB2", FormState.ACTIVE);
        assertForm("contA", FormState.ACTIVE);
        assertForm("contB", FormState.CLOSED);

        closeForm("contB2");

        assertForm("contB2", FormState.CLOSED);
        assertForm("contA", FormState.ACTIVE);
        assertForm("contB", FormState.CLOSED);
    }

    @Test
    public void testStateOtherContainerOtherUC() {
        runUC(UC_A);
        showForm("contA", FormType.STANDARD, CONTAINER_A, UC_A);
        showForm("contB", FormType.STANDARD, CONTAINER_B, UC_A);

        assertForm("contB", FormState.ACTIVE);
        assertForm("contA", FormState.ACTIVE);

        runUC(UC_B);

        assertForm("contB", FormState.INACTIVE_PENDING);
        assertForm("contA", FormState.INACTIVE_PENDING);

        showForm("contB2", FormType.STANDARD, CONTAINER_B, UC_B);

        assertForm("contB2", FormState.ACTIVE);
        assertForm("contA", FormState.INACTIVE_PENDING);
        assertForm("contB", FormState.HIDDEN);

        terminateUC(UC_B);

        assertForm("contB2", FormState.CLOSED);
        assertForm("contA", FormState.ACTIVE);
        assertForm("contB", FormState.ACTIVE);
    }

    @Test
    public void testStateStandardClosingModal() {
        runUC(UC_A);
        showForm("standard", FormType.STANDARD, CONTAINER_A, UC_A);
        showForm("modal", FormType.MODAL, CONTAINER_A, UC_A);
        showForm("modalOver", FormType.MODAL_OVERFLOW, CONTAINER_A, UC_A);

        assertForm("modalOver", FormState.ACTIVE);
        assertForm("modal", FormState.HIDDEN);
        assertForm("standard", FormState.SHADOWED);

        showForm("other", FormType.STANDARD, CONTAINER_A, UC_A);

        assertForm("other", FormState.ACTIVE);
        assertForm("modalOver", FormState.CLOSED);
        assertForm("modal", FormState.CLOSED);
        assertForm("standard", FormState.CLOSED);
    }


    @Test
    public void testStateStandardClosingModalOtherUC() {
        showForm("sysStandard", FormType.STANDARD, CONTAINER_D, UC_SYS_A);

        runUC(UC_A);
        showForm("standard", FormType.STANDARD, CONTAINER_A, UC_A);
        showForm("modal", FormType.MODAL, CONTAINER_A, UC_A);
        showForm("modalOver", FormType.MODAL_OVERFLOW, CONTAINER_A, UC_A);

        assertForm("modalOver", FormState.ACTIVE);
        assertForm("modal", FormState.HIDDEN);
        assertForm("standard", FormState.SHADOWED);
        assertForm("sysStandard", FormState.SHADOWED);

        runUC(UC_B);

        assertForm("modalOver", FormState.INACTIVE_PENDING);
        assertForm("modal", FormState.HIDDEN);
        assertForm("standard", FormState.SHADOWED);
        assertForm("sysStandard", FormState.SHADOWED);

        showForm("other", FormType.STANDARD, CONTAINER_A, UC_B);

        assertForm("other", FormState.ACTIVE);
        assertForm("modalOver", FormState.HIDDEN);
        assertForm("modal", FormState.HIDDEN);
        assertForm("standard", FormState.HIDDEN);
        assertForm("sysStandard", FormState.ACTIVE);

        terminateUC(UC_B);

        assertForm("other", FormState.CLOSED);
        assertForm("modalOver", FormState.ACTIVE);
        assertForm("modal", FormState.HIDDEN);
        assertForm("standard", FormState.SHADOWED);
        assertForm("sysStandard", FormState.SHADOWED);
    }

    @Test
    public void testStateModalVsModalOver() {
        runUC(UC_A);
        showForm("standard", FormType.STANDARD, CONTAINER_A, UC_A);
        showForm("modalA", FormType.MODAL, CONTAINER_A, UC_A);

        assertForm("modalA", FormState.ACTIVE);
        assertForm("standard", FormState.SHADOWED);

        showForm("modalB", FormType.MODAL, CONTAINER_A, UC_A);

        assertForm("modalB", FormState.ACTIVE);
        assertForm("modalA", FormState.CLOSED);
        assertForm("standard", FormState.SHADOWED);

        showForm("modalOverA", FormType.MODAL_OVERFLOW, CONTAINER_A, UC_A);

        assertForm("modalOverA", FormState.ACTIVE);
        assertForm("modalB", FormState.HIDDEN);
        assertForm("standard", FormState.SHADOWED);

        showForm("modalOverB", FormType.MODAL_OVERFLOW, CONTAINER_A, UC_A);

        assertForm("modalOverB", FormState.ACTIVE);
        assertForm("modalOverA", FormState.HIDDEN);
        assertForm("modalB", FormState.HIDDEN);
        assertForm("standard", FormState.SHADOWED);

        closeForm("modalOverB");

        assertForm("modalOverB", FormState.CLOSED);
        assertForm("modalOverA", FormState.ACTIVE);
        assertForm("modalB", FormState.HIDDEN);
        assertForm("standard", FormState.SHADOWED);

        closeForm("modalOverA");

        assertForm("modalOverA", FormState.CLOSED);
        assertForm("modalB", FormState.ACTIVE);
        assertForm("standard", FormState.SHADOWED);

        closeForm("modalB");

        assertForm("modalB", FormState.CLOSED);
        assertForm("standard", FormState.ACTIVE);
    }

    @Test
    public void testModalOtherUC() {
        runUC(UC_A);
        showForm("standardA", FormType.STANDARD, CONTAINER_A, UC_A);

        assertForm("standardA", FormState.ACTIVE);

        runUC(UC_B);

        showForm("modalB", FormType.MODAL, CONTAINER_A, UC_B);

        assertForm("modalB", FormState.ACTIVE);
        assertForm("standardA", FormState.SHADOWED);

        terminateUC(UC_B);

        showForm("modalA", FormType.MODAL, CONTAINER_A, UC_A);

        assertForm("modalA", FormState.ACTIVE);
        assertForm("standardA", FormState.SHADOWED);

        runUC(UC_B);

        showForm("modalB", FormType.MODAL, CONTAINER_A, UC_B);

        assertForm("modalB", FormState.ACTIVE);
        assertForm("modalA", FormState.SHADOWED);
        assertForm("standardA", FormState.SHADOWED);
    }

    @Test
    public void testSystemUCModal() {
        showForm("sys", FormType.STANDARD, CONTAINER_A, UC_SYS_A);

        runUC(UC_A);
        showForm("standard", FormType.STANDARD, CONTAINER_B, UC_A);

        assertForm("standard", FormState.ACTIVE);
        assertForm("sys", FormState.ACTIVE);

        showForm("sysModal", FormType.MODAL, CONTAINER_A, UC_SYS_A);

        assertForm("sysModal", FormState.ACTIVE);
        assertForm("standard", FormState.SHADOWED);
        assertForm("sys", FormState.SHADOWED);
    }

    @Test
    public void testSystemUCModalAfterSystemModal() {
        showForm("sys", FormType.STANDARD, CONTAINER_A, UC_SYS_A);

        runUC(UC_A);
        showForm("standard", FormType.STANDARD, CONTAINER_B, UC_A);

        assertForm("standard", FormState.ACTIVE);
        assertForm("sys", FormState.ACTIVE);

        showForm("sysModal", FormType.MODAL, CONTAINER_A, UC_SYS_A);

        assertForm("sysModal", FormState.ACTIVE);
        assertForm("standard", FormState.SHADOWED);
        assertForm("sys", FormState.SHADOWED);

        showForm("sysOther", FormType.STANDARD, CONTAINER_A, UC_SYS_A);

        assertForm("sysOther", FormState.ACTIVE);
        assertForm("sysModal", FormState.CLOSED);
        assertForm("standard", FormState.ACTIVE);
        assertForm("sys", FormState.CLOSED);
    }

    @Test
    public void testSystemUCStdAfterNormalModal() {
        showForm("sys", FormType.STANDARD, CONTAINER_A, UC_SYS_A);

        runUC(UC_A);
        showForm("standard", FormType.STANDARD, CONTAINER_B, UC_A);

        assertForm("standard", FormState.ACTIVE);
        assertForm("sys", FormState.ACTIVE);

        showForm("sysModal", FormType.MODAL, CONTAINER_A, UC_SYS_A);

        assertForm("sysModal", FormState.ACTIVE);
        assertForm("standard", FormState.SHADOWED);
        assertForm("sys", FormState.SHADOWED);
    }

    private void runUC(FakeUseCaseContext uc) {
        ucStack.push(uc);
        formsContainer.useCaseStarted(uc);
    }

    private void terminateUC(FakeUseCaseContext uc) {
        if (ucStack.peek() != uc) {
            throw new RuntimeException("Must be the top UC");
        }
        ucStack.pop();
        formsContainer.useCaseTerminated(uc);
    }

    private void showForm(String formId, FormType type, String container, FakeUseCaseContext uc) {
        Form newForm = new AdHocForm<>();
        newForm.setId(formId);
        newForm.setUseCase(uc);
        newForm.setDeclaredContainer(container);
        newForm.setFormType(type);
        newForm.setViewMode(Form.ViewMode.NORMAL);
        newForm.setLabelModelBinding(new StaticBinding<>(formId));
        formsContainer.showForm(newForm);
    }

    private void closeForm(String formId) {
        formsContainer.closeForm(findForm(formId));
    }

    private Form findForm(String formId) {
        return formsContainer.getManagedForms().stream().filter(form -> form.getId().equals(formId)).findFirst().orElse(null);
    }

    private void assertForm(String formId, FormState state) {
        Form foundForm = findForm(formId);
        FormState foundState = foundForm == null ? FormState.CLOSED : foundForm.getState();
        Assert.assertEquals(formsContainer.logState() + "\nForm " + formId + " in state " + foundState + " expected " + state,
                state, foundState);
    }
}
