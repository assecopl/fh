package pl.fhframework.core.externalusecase;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.core.util.StringUtils;

import java.net.URL;

/**
 * @author Tomasz Kozlowski (created on 08.07.2019)
 */
@Getter
public class ExternalUseCaseEntry {

    private String uuid;
    private String callbackTemplate;
    @Setter
    private URL externalURL;
    @Setter
    private boolean closeable;

    public ExternalUseCaseEntry(String uuid, String callbackTemplate) {
        this(uuid, callbackTemplate, false);
    }

    public ExternalUseCaseEntry(String uuid, String callbackTemplate, boolean closeable) {
        this.uuid = uuid;
        this.callbackTemplate = callbackTemplate;
        this.closeable = closeable;
    }

    /** Builds callback URL */
    public String getCallbackURL() {
        return getCallbackURL(null);
    }

    /** Builds callback URL with path */
    public String getCallbackURL(String path) {
        StringBuilder builder = new StringBuilder(callbackTemplate);
        if (StringUtils.hasText(path)) {
            if (!path.startsWith("/")) {
                builder.append("/");
            }
            builder.append(path);
        }
        builder.append("?uuid=").append(uuid);
        return builder.toString();
    }

}
