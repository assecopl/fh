package pl.fhframework.event.dto;

import lombok.Getter;

/**
 * Chat refresh event
 * @author Tomasz Kozlowski (created on 03.10.2019)
 */
@Getter
public class ChatEvent extends EventDTO {

    private Long chatId;

    public ChatEvent(Long chatId) {
        this.chatId = chatId;
    }

}
