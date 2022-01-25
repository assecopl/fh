package pl.fhframework.compiler.core.generator.model.usecase;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.fhframework.compiler.core.generator.model.ParameterMm;
import pl.fhframework.compiler.core.uc.dynamic.model.element.command.detail.ActionLink;
import pl.fhframework.events.BreakLevelEnum;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class ActionLinkMm implements Link {
    @JsonIgnore
    private boolean inDirection;

    @JsonIgnore
    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PROTECTED)
    private ActionLink actionLink;

    @JsonIgnore
    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PROTECTED)
    private UseCaseMm parent;

    public ActionLinkMm(ActionLink actionLink, UseCaseMm parent, boolean inDirection) {
        this.actionLink = actionLink;
        this.parent = parent;
        this.inDirection = inDirection;
    }

    @JsonGetter
    public ElementInfo getTarget() {
        return getSourceOrTarget(actionLink.getTarget(), parent);
    }

    @JsonGetter
    public ElementInfo getSource() {
        if (!inDirection) {
            return null;
        }
        return getSourceOrTarget(actionLink.getParent().getParent(), parent);
    }

    @JsonGetter
    public String getFormEvent() {
        return actionLink.getFormAction();
    }

    @JsonGetter
    public List<ParameterMm> getParameters() {
        return actionLink.getParameters().stream().map(ParameterMm::new).collect(Collectors.toList());
    }

    @JsonGetter
    public boolean isValidate() {
        return actionLink.isValidate();
    }

    @JsonGetter
    public boolean isClearValidationContext() {
        return actionLink.isClearValidationContext();
    }

    @JsonGetter
    public boolean isImmediate() {
        return actionLink.isImmediate();
    }

    @JsonGetter
    public boolean isConfirmationDialog() {
        return actionLink.isConfirmationDialog();
    }

    @JsonGetter
    public String getDialogTitle() {
        return actionLink.getDialogTitle();
    }

    @JsonGetter
    public String getDialogMessage() {
        return actionLink.getDialogMessage();
    }

    @JsonGetter
    public String getConfirmButton() {
        return actionLink.getConfirmButton();
    }

    @JsonGetter
    public String getCancelButton() {
        return actionLink.getCancelButton();
    }

    @JsonGetter
    public BreakLevelEnum getBreakLevel() {
        return actionLink.getBreakLevel();
    }

    @Override
    @JsonGetter
    public String getId() {
        return actionLink.getId();
    }

    @Override
    public <T> T provideImpl() {
        return (T) actionLink;
    }
}
