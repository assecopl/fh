package pl.fhframework.dp.commons.fh.declaration.message;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.dp.transport.msg.DeclarationMessageDto;
import pl.fhframework.core.model.Model;
import pl.fhframework.model.forms.AccessibilityEnum;

import java.util.Optional;

@Getter
@Setter
@Model
public class DeclarationMessageFormModel {

    DeclarationMessageDto message;
    DeclarationMessageHelper declarationMessageHelper = new DeclarationMessageHelper();
    private int messagesTabIndex=0;

    public DeclarationMessageFormModel() {}

    public DeclarationMessageFormModel(DeclarationMessageDto message) {
        this.message = message;
    }

    public String getBaseMessageIdentifier() {
        return Optional.ofNullable(message.getMetadata().getLocalReferenceNumber()).orElse(
                Optional.ofNullable(message.getMetadata().getMessageIdentification()).orElse(""));
    }

    public AccessibilityEnum getAvailabilityDeliveredDate() {
        if(message.getDelivered() != null)
            return AccessibilityEnum.VIEW;

        return AccessibilityEnum.HIDDEN;
    }
}
