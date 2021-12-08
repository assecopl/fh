package pl.fhframework.dp.commons.fh.messages;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.dp.commons.fh.outline.TreeElement;
import pl.fhframework.dp.transport.msg.MessageDto;
import pl.fhframework.core.model.Model;
import pl.fhframework.model.forms.AccessibilityEnum;

import java.util.List;
import java.util.Optional;

@Model
@Getter
@Setter
public class MessageHandlingFormModel {

    private TreeElement<MessageDto> selectedMessageTreeElement;
    private List<TreeElement<MessageDto>> selectMessagesLeftMenu;

    private int activeTabIndex=0;

    public String getBaseMessageIdentifier() {
        return Optional.ofNullable(selectedMessageTreeElement.getObj().getMetadata().getLocalReferenceNumber()).orElse(
                Optional.ofNullable(selectedMessageTreeElement.getObj().getMetadata().getMessageIdentification()).orElse(""));
    }

    public AccessibilityEnum getAvailabilityDeliveredDate() {
        if(selectedMessageTreeElement.getObj().getDelivered() != null)
            return AccessibilityEnum.VIEW;

        return AccessibilityEnum.HIDDEN;
    }

}
