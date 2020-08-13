package pl.fhframework.event.dto;

import lombok.Getter;

@Getter
public class FileDownloadEvent extends EventDTO {
    private String url;

    public FileDownloadEvent(String url) {
        this.url = url;
    }
}
