package pl.fhframework.model.dto;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.core.logging.ErrorInformation;
import pl.fhframework.Commands;
import pl.fhframework.core.uc.service.UseCaseLayoutService;
import pl.fhframework.event.dto.EventDTO;
import pl.fhframework.helper.AutowireHelper;
import pl.fhframework.model.forms.Form;

import java.util.*;

/**
 * Message from a server containig results of event handling.
 */
@Getter
@Setter
public class OutMessageEventHandlingResult  extends AbstractMessage implements IErrorCarrierMessage {

    private Set<String> closeForm = new HashSet<>();

    private Set<Form> openForm = new HashSet<>();

    private String layout = UseCaseLayoutService.mainLayout;

    private Set<ElementChanges> changes = new HashSet<>();

    private List<EventDTO> events = new ArrayList<>();

    private List<ErrorInformation> errors = new ArrayList<>();

    public OutMessageEventHandlingResult() {
        super(Commands.OUT_SET);
    }
}
