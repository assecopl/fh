package pl.fhframework.docs.forms.component.model;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import pl.fhframework.core.designer.ComponentElement;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class InputTimestampElement extends ComponentElement {
    private Date inputTimestampRequired = null;
    private Date inputTimestampFormatExample = Date.from(Instant.now());
    private LocalDateTime localDateTimeExample = LocalDateTime.now();
    private Date inputTimestampOnChange = null;
}
