package pl.fhframework.event.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Hide/show chat list event
 * @author Tomasz Kozlowski (created on 29.11.2019)
 */
@Getter
@AllArgsConstructor
public class ChatListEvent extends EventDTO {

    private boolean show;

}
