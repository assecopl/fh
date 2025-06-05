package pl.fhframework.docs.uc;

import pl.fhframework.core.designer.IDocumentationUseCase;
import pl.fhframework.core.uc.UseCase;
import pl.fhframework.core.uc.url.UseCaseWithUrl;
import pl.fhframework.docs.forms.component.FloatingGroupForm;
import pl.fhframework.docs.forms.component.model.FloatingGroupElement;
import pl.fhframework.annotations.Action;
import pl.fhframework.events.BreakLevelEnum;
import pl.fhframework.model.forms.attributes.FloatingGroupStateAttribute;


/**
 * Use case supporting FloatingGroup documentation
 */
@UseCase
public class FloatingGroupUC implements IDocumentationUseCase<FloatingGroupElement> {

    private FloatingGroupElement model;

    @Override
    public void start(FloatingGroupElement model) {
        this.model = model;
        showForm(FloatingGroupForm.class, model);
    }

    //pl/fhframework/docs/forms/component/FloatingGroupForm.frm actions,
    //from revision: 4401
    @Action(breakOnErrors = BreakLevelEnum.NEVER)
    public void pinnedMinimized() {
        model.setBoundFloatingState(FloatingGroupStateAttribute.FloatingState.PINNED_MINIMIZED);
    }

    @Action(breakOnErrors = BreakLevelEnum.NEVER)
    public void pinnedMaximized() {
        model.setBoundFloatingState(FloatingGroupStateAttribute.FloatingState.PINNED_MAXIMIZED);
    }

    @Action(breakOnErrors = BreakLevelEnum.NEVER)
    public void unpinnedMinimized() {
        model.setBoundFloatingState(FloatingGroupStateAttribute.FloatingState.UNPINNED_MINIMIZED);
    }

    @Action(breakOnErrors = BreakLevelEnum.NEVER)
    public void notDraggablePinnedMinimized() {
        model.setNotDraggableFloatingState(FloatingGroupStateAttribute.FloatingState.PINNED_MINIMIZED);
    }

    @Action(breakOnErrors = BreakLevelEnum.NEVER)
    public void notDraggableUnpinnedMinimized() {
        model.setNotDraggableFloatingState(FloatingGroupStateAttribute.FloatingState.UNPINNED_MINIMIZED);
    }



}
