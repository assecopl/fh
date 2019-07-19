package pl.fhframework.model.dto;

import lombok.Data;
import pl.fhframework.Commands;

/**
 * Server input message - request to run use case
 */
@Data
public class InMessageRunUseCase extends AbstractMessage {

    private String useCaseQualifiedClassName;

    public InMessageRunUseCase() {
        super(Commands.IN_RUN_USE_CASE);
    }

    public InMessageRunUseCase( String useCaseQualifiedClassName) {
        this();
        this.useCaseQualifiedClassName = useCaseQualifiedClassName;
    }
}