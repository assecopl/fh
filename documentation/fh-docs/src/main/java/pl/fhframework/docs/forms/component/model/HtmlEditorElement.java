package pl.fhframework.docs.forms.component.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pl.fhframework.core.designer.ComponentElement;

@Getter
@Setter
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class HtmlEditorElement extends ComponentElement {
    private String editorText =
            "<h1>h1 Nagłówek</h1>\n" +
            "<h2>h2 Nagłówek</h2>\n" +
            "<h3>h3 Nagłówek</h3>\n" +
            "<h4>h4 Nagłówek</h4>\n" +
            "<h5>h5 Nagłówek</h5>\n" +
            "<h6>h6 Nagłówek</h6>\n" +
            "<p>Przykładowy tekst</p>";
}
