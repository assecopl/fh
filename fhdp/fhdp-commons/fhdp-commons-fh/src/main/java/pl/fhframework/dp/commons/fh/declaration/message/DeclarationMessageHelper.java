package pl.fhframework.dp.commons.fh.declaration.message;

import pl.fhframework.dp.transport.drs.repository.OtherMetadata;
import pl.fhframework.dp.transport.enums.MessageDirectionEnum;
import pl.fhframework.dp.transport.msg.DeclarationMessageDto;

public class DeclarationMessageHelper {
    public static MessageDirectionEnum getDirectionFromDto(DeclarationMessageDto messageDto) {

        if(messageDto == null || messageDto.getMetadata() == null || messageDto.getMetadata().getOtherMetadata() == null || messageDto.getMetadata().getOtherMetadata().isEmpty()) {
            return MessageDirectionEnum.UNDEFINED;
        }
        if(messageDto.getDirection() != null) {
            return messageDto.getDirection();
        }

        OtherMetadata directionData = messageDto.getMetadata().getOtherMetadata().stream()
                .filter(direction -> "direction".equals(direction.getName()))
                .findAny()
                .orElse(null);

        if(directionData != null) {
            return MessageDirectionEnum.valueOf(directionData.getValue().toUpperCase());
        } else {
            return MessageDirectionEnum.UNDEFINED;
        }
    }
}
