package pl.fhframework.event.dto;

import lombok.Getter;

@Getter
public class ScrollEvent extends EventDTO {
    private String formElementId;
    private Boolean animate = false;
    private Integer animateDuration = 0;

    public ScrollEvent(String formElementId, Boolean animate, Integer animateDuration) {
        this.formElementId = formElementId;
        this.animate = animate;
        this.animateDuration = animateDuration;
    }
}
