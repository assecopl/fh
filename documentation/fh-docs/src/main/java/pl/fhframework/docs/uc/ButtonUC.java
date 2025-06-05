package pl.fhframework.docs.uc;

import pl.fhframework.core.designer.IDocumentationUseCase;
import pl.fhframework.core.uc.UseCase;
import pl.fhframework.core.uc.url.UseCaseWithUrl;
import pl.fhframework.docs.forms.component.ButtonForm;
import pl.fhframework.docs.forms.component.model.ButtonElement;
import pl.fhframework.annotations.Action;
import pl.fhframework.event.dto.NotificationEvent;
import pl.fhframework.events.BreakLevelEnum;
import pl.fhframework.model.forms.Styleable;

import java.util.List;


/**
 * Use case supporting Button documentation
 */
@UseCase
public class ButtonUC implements IDocumentationUseCase<ButtonElement> {
    private ButtonElement model;

    @Override
    public void start(ButtonElement model) {
        this.model = model;
        model.setCounter(0);
        model.setCounterArea(0);
        model.setOnClickedMessage("Component clicked 0 times.");
        model.setOnAreaClickedMessage("Component area clicked 0 times.");
        showForm(ButtonForm.class, model);
    }


    @Action(breakOnErrors = BreakLevelEnum.NEVER)
    public void changeButtonStyle() {
        int activeStyleIdx = model.getStyleIndex();
        List<Styleable.Style> styles = model.getStyles();
        int newActiveStyleIdx = (activeStyleIdx + 1) % styles.size();
        model.setStyleIndex(newActiveStyleIdx);
        String newButtonStyle = styles.get(newActiveStyleIdx).toValue();
        model.setSelectedStyle(newButtonStyle);
        model.setSelectedEnumStyle(Styleable.Style.forValue(newButtonStyle));
    }

    @Action(breakOnErrors = BreakLevelEnum.NEVER)
    public void resetButtonStyle() {
        List<Styleable.Style> defaultStyles = model.getStyles();
        final String primary = Styleable.Style.PRIMARY.toValue();
        model.setSelectedStyle(primary);
        model.setSelectedEnumStyle(Styleable.Style.forValue(primary));
        model.setStyleIndex(defaultStyles.indexOf(Styleable.Style.forValue(primary)));
    }


    @Action(breakOnErrors = BreakLevelEnum.NEVER)
    public void buttonClicked() {
        model.incrementCounter();
        model.setOnClickedMessage("Component clicked " + model.getCounter() + " times.");
    }

    @Action(breakOnErrors = BreakLevelEnum.NEVER)
    public void buttonClickedNotification() {
        getUserSession().getEventRegistry().fireNotificationEvent(NotificationEvent.Level.INFO, "Action performed");
    }

    @Action(breakOnErrors = BreakLevelEnum.NEVER)
    public void fizzBuzz() {
        model.fizzBuzz();
    }


}

