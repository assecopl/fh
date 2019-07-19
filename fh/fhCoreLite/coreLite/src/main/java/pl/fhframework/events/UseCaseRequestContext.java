package pl.fhframework.events;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pl.fhframework.core.uc.service.UseCaseLayoutService;
import pl.fhframework.event.dto.EventDTO;
import pl.fhframework.helper.AutowireHelper;
import pl.fhframework.model.dto.ElementChanges;
import pl.fhframework.model.forms.Form;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Class that is responsible for managing forms, changes and events within one request.
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UseCaseRequestContext {

    /**
     * Holds set of forms that will be displayed at the end of event cycle.
     */
    @Getter
    private Set<Form> formsToDisplay = new LinkedHashSet<>();

    /**
     * Holds set of forms that will be hide at the end of event cycle.
     */
    @Getter
    private Set<Form> formsToHide = new LinkedHashSet<>();

    /**
     * Gather all changes for form components within one request. Will be cleared at the end of
     * event cycle.
     */
    @Getter
    private Set<ElementChanges> changes = new LinkedHashSet<>(100);

    /**
     * Gather all events for within one request. Will be cleared at the end of event cycle.
     */
    @Getter
    private List<EventDTO> events = new ArrayList<>();

    /**
     * External partial responses to current request
     */
    @Getter
    private List<String> propagatedExternalResponses = new ArrayList<>();

    /**
     * Layout for response to current use case request
     */
    @Getter
    @Setter
    private String layout = UseCaseLayoutService.mainLayout;

    @Setter
    @Getter
    private ExternalCallbackInvocation propagatedExternalCallback;

    public void finishEventContext() {
        formsToHide.clear();
        formsToDisplay.clear();
        changes.clear();
        events.clear();
        propagatedExternalResponses.clear();
        propagatedExternalCallback = null;
    }
}
