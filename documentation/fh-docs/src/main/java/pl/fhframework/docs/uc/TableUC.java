package pl.fhframework.docs.uc;

import org.springframework.beans.factory.annotation.Autowired;
import pl.fhframework.core.designer.IDocumentationUseCase;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.core.uc.UseCase;
import pl.fhframework.core.uc.url.UseCaseWithUrl;
import pl.fhframework.docs.forms.NewPersonForm;
import pl.fhframework.docs.forms.component.TableForm;
import pl.fhframework.docs.forms.component.model.TableElement;
import pl.fhframework.docs.forms.model.example.Person;
import pl.fhframework.docs.forms.model.tv.Episode;
import pl.fhframework.docs.forms.model.tv.Season;
import pl.fhframework.docs.forms.model.tv.TvSeries;
import pl.fhframework.docs.forms.service.PersonService;
import pl.fhframework.annotations.Action;
import pl.fhframework.event.EventRegistry;
import pl.fhframework.event.dto.NotificationEvent;
import pl.fhframework.events.BreakLevelEnum;
import pl.fhframework.model.forms.messages.ActionButton;
import pl.fhframework.model.forms.messages.Messages;

import java.util.Map;


/**
 * Use case supporting Table documentation
 */
@UseCase
public class TableUC implements IDocumentationUseCase<TableElement> {
    private TableElement model;
    @Autowired
    private EventRegistry eventRegistry;
    @Autowired
    private PersonService personService;
    private NewPersonForm newPersonForm;


    @Override
    public void start(TableElement model) {
        this.model = model;
        showForm(TableForm.class, model);
    }

    @Action(breakOnErrors = BreakLevelEnum.NEVER)
    public void onDraw(Person person) {
        FhLogger.debug(this.getClass(), logger -> logger.log("Selected button in row. Action invoked for person {}", person.getName()));
    }

    @Action(breakOnErrors = BreakLevelEnum.NEVER)
    public void onPaint(Person person) {
        model.setSelectedReadOnlyPerson(null);
        FhLogger.debug(this.getClass(), logger -> logger.log("Selected button in row. Action invoked for person {}", person.getName()));
    }

    @Action(breakOnErrors = BreakLevelEnum.NEVER)
    public void resetColoredRestrictedPeople() {
        model.getColoredRestrictedPeople().clear();
    }

    @Action(breakOnErrors = BreakLevelEnum.NEVER)
    public void resetColoredRestrictedPerson(Person person) {
        Map<Person, String> coloredRestrictedPeople = model.getColoredRestrictedPeople();
        coloredRestrictedPeople.remove(person);
    }

    @Action(breakOnErrors = BreakLevelEnum.NEVER)
    public void onAddClick() {
        eventRegistry.fireNotificationEvent(NotificationEvent.Level.SUCCESS, "ADD clicked");
    }

    @Action(breakOnErrors = BreakLevelEnum.NEVER)
    public void addNewPersonWindow() {
        newPersonForm = showForm(NewPersonForm.class, new Person());
    }

    @Action(breakOnErrors = BreakLevelEnum.NEVER)
    public void savePerson() {
        if(newPersonForm.getModel().getId() == null) {
            personService.save(newPersonForm.getModel());
        } else {
            personService.update(newPersonForm.getModel());
        }

        model.setSelectedPerson(null);
        hideForm(newPersonForm);
    }

    @Action(breakOnErrors = BreakLevelEnum.NEVER)
    public void editPersonWindow(Person person) {
        newPersonForm = showForm(NewPersonForm.class, person);
    }

    @Action(breakOnErrors = BreakLevelEnum.NEVER)
    public void removePerson(Person person) {
        Messages.showActionMessage(getUserSession(), "Information", "Are you sure you want to delete : " + person.getName() + " ?", Messages.Severity.INFO,
                ActionButton.get("Delete", (v) -> {
                    personService.delete(person);
                    model.setSelectedPerson(null);
                    Messages.close(v);
                }),
                ActionButton.getClose("Close")
        );
    }

    @Action(breakOnErrors = BreakLevelEnum.NEVER)
    public void closeForm() {
        hideForm(newPersonForm);
    }

    @Action(breakOnErrors = BreakLevelEnum.NEVER)
    public void removeSeason(TvSeries serial, Season season) {
        Messages.showActionMessage(getUserSession(), "Information", "Are you sure you want to delete: " +
                        season.getNumber() + " from " + serial.getName() + " ?", Messages.Severity.INFO,
                ActionButton.get("Delete", (v) -> {
                    serial.getSeasons().remove(season);
                    Messages.close(v);
                }),
                ActionButton.getClose("Close")
        );
    }

    @Action(breakOnErrors = BreakLevelEnum.NEVER)
    public void removeEpisode(Season season, Episode episode) {
        Messages.showActionMessage(getUserSession(), "Information", "Are you sure you want to delete: " +
                        episode.getName() + " from " + season.getNumber() + " ?", Messages.Severity.INFO,
                ActionButton.get("Delete", (v) -> {
                    season.getEpisodes().remove(episode);
                    Messages.close(v);
                }),
                ActionButton.getClose("Close")
        );
    }

    @Action(breakOnErrors = BreakLevelEnum.NEVER)
    public void showDescription(Episode episode) {
        Messages.showActionMessage(getUserSession(), "Information", episode.getPlot(), Messages.Severity.INFO,
                ActionButton.getClose("Close")
        );
    }



}

