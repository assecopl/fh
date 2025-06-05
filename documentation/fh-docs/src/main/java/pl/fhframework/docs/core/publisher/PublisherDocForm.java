package pl.fhframework.docs.core.publisher;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.model.forms.Form;

/**
 * @author Tomasz Kozlowski (created on 18.12.2019)
 */

public class PublisherDocForm extends Form<PublisherDocForm.Model> {

    @Getter
    @Setter
    public static class Model {
        private String message;
    }

}
