package pl.fhframework.event.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Tomasz Kozlowski (created on 24.09.2020)
 */
@Getter
@AllArgsConstructor
public class RedirectPostEvent extends EventDTO {

    private final String url;

    private final Map<String, String> params;

    public RedirectPostEvent(String url) {
        this(url, new HashMap<>());
    }

}
