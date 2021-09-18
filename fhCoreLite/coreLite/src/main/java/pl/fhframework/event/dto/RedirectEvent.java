package pl.fhframework.event.dto;

import lombok.Getter;

import java.util.UUID;

@Getter
public class RedirectEvent extends EventDTO {
    private String uuid;
    private String url;
    private boolean newWindow;
    private boolean closeable;

    public RedirectEvent(String url, boolean newWindow) {
        this(UUID.randomUUID().toString(), url, newWindow);
    }

    public RedirectEvent(String uuid, String url, boolean newWindow) {
        this(uuid, url, newWindow, false);
    }

    public RedirectEvent(String uuid, String url, boolean newWindow, boolean closeable) {
        this.uuid = uuid;
        this.url = url;
        this.newWindow = newWindow;
        this.closeable = closeable;
    }
}
