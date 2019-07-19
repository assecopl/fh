package pl.fhframework.event.dto;

import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;

@Getter
public class NotificationEvent extends EventDTO {
    private Level level;
    private String message;

    public NotificationEvent(Level level, String message) {
        this.level = level;
        this.message = message;
    }

    public enum Level {
        INFO("info"),
        SUCCESS("success"),
        WARNING("warning"),
        ERROR("error");

        private String value;

        Level(String value) {
            this.value = value;
        }

        @JsonValue
        public String getValue() {
            return value;
        }

    }

}
