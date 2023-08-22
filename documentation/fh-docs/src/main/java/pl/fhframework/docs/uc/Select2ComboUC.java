package pl.fhframework.docs.uc;

import org.springframework.beans.factory.annotation.Autowired;
import pl.fhframework.annotations.Action;
import pl.fhframework.core.designer.IDocumentationUseCase;
import pl.fhframework.core.uc.UseCase;
import pl.fhframework.docs.converter.model.User;
import pl.fhframework.docs.forms.component.ComboForm;
import pl.fhframework.docs.forms.component.Select2ComboForm;
import pl.fhframework.docs.forms.component.model.ComboElement;
import pl.fhframework.docs.forms.component.model.Select2ComboElement;
import pl.fhframework.event.EventRegistry;
import pl.fhframework.event.dto.NotificationEvent;
import pl.fhframework.events.BreakLevelEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import static pl.fhframework.docs.converter.service.UserService.ADMINS_CATEGORY;
import static pl.fhframework.docs.converter.service.UserService.EMPTY_CATEGORY;


/**
 * Use case supporting Wizard documentation
 */
@UseCase
public class Select2ComboUC implements IDocumentationUseCase<Select2ComboElement> {
    private Select2ComboElement model;

    @Autowired
    private EventRegistry eventRegistry;


    @Override
    public void start(Select2ComboElement model) {
        this.model = model;
        showForm(Select2ComboForm.class, model);
    }


    @Action(breakOnErrors = BreakLevelEnum.NEVER)
    public void addUserToAdmins() {
        //ComboElement comboElement = (ComboElement) formComponent.getSelectedFormElement();
        Set<User> users = model.getComboData().values().stream().flatMap(List::stream).collect(Collectors.toSet());
        Random r = new Random();
        int randomIndex = r.ints(0, users.size() - 1).findFirst().getAsInt();
        User user = new ArrayList<>(users).get(randomIndex);
        model.getComboData().add(EMPTY_CATEGORY, user);
        eventRegistry.fireNotificationEvent(NotificationEvent.Level.SUCCESS, "+ " + user.getLastName());
    }

}

