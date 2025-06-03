package pl.fhframework.compiler.core.generator.model.usecase;

import com.fasterxml.jackson.annotation.JsonGetter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.fhframework.compiler.core.uc.dynamic.model.element.attribute.Parameter;
import pl.fhframework.compiler.core.uc.dynamic.model.element.command.ShowForm;
import pl.fhframework.compiler.core.uc.dynamic.model.element.command.ShowMessage;
import pl.fhframework.event.dto.NotificationEvent;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ShowMessageMm extends ShowFormMm<ShowMessage> {
    public ShowMessageMm(ShowForm showForm, UseCaseMm parent) {
        super(showForm, parent);
    }

    @JsonGetter
    public ShowMessage.Type getMessageType() {
        return command.getType();
    }

    @JsonGetter
    public NotificationEvent.Level getSeverity() {
        return command.getSeverity();
    }

    @JsonGetter
    public String getTitle() {
        return command.getTitle();
    }

    @JsonGetter
    public String getMessage() {
        return command.getMessage();
    }

    @JsonGetter
    public List<Parameter> getActionButtons() { // todo: ButonMm? (name: ExpressionMm, value: ExpressionMM)
        return command.getActionButtons();
    }
}
