package pl.fhframework.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import pl.fhframework.core.model.dto.MessageEventParamsDeserializer;
import pl.fhframework.core.model.dto.ModuleSubTypesDeserializer;
import pl.fhframework.Commands;
import pl.fhframework.model.dto.AbstractMessage;
import pl.fhframework.model.dto.ValueChange;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Gabriel on 2015-12-07.
 */
@Getter
@Setter
public class InMessageEventData extends AbstractMessage {
    private String containerId;
    private String formId;
    private String eventType;
    private String eventSourceId;
    private List<ValueChange> changedFields;
    private int mouseX, mouseY;
    private Object optionalValue;
    private String actionName;
    @JsonDeserialize(contentUsing = MessageEventParamsDeserializer.class)
    private List<Object> params; // possible types: primitives, InMessageEventParam.class or array/list

    public InMessageEventData() {
        super(Commands.IN_HANDLE_EVENT);
    }

    public InMessageEventData clone() {
        InMessageEventData clone = new InMessageEventData();
        clone.setContainerId(containerId);
        clone.setFormId(formId);
        clone.setEventType(eventType);
        clone.setEventSourceId(eventSourceId);
        clone.setChangedFields(changedFields);
        clone.setMouseX(mouseX);
        clone.setMouseY(mouseY);
        clone.setOptionalValue(optionalValue);
        clone.setActionName(actionName);
        clone.setParams(params);
        return clone;
    }

    @JsonIgnore
    public boolean isEventPresent() {
        return eventType != null || eventSourceId != null;
    }
}
