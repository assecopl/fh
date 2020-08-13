package pl.fhframework.model.dto.cloud;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import pl.fhframework.Commands;
import pl.fhframework.model.dto.AbstractMessage;

/**
 * Server input message - request to run use case in cloud
 */
@Data
public class InMessageRunCloudUseCase extends AbstractMessage {

    private String useCaseQualifiedClassName;

    private String[] paramJsons;

    public InMessageRunCloudUseCase() {
        super(Commands.IN_CLOUD_RUN_USE_CASE);
    }

    public InMessageRunCloudUseCase(String useCaseQualifiedClassName, String[] paramJsons) {
        this();
        this.useCaseQualifiedClassName = useCaseQualifiedClassName;
        this.paramJsons = paramJsons;
    }
}
