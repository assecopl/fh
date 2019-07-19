package pl.fhframework.model.forms;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.annotations.Control;
import pl.fhframework.annotations.XMLProperty;

/**
 * Created by krzysztof.kobylarek on 2016-10-21.
 */
@Control(parents = {Form.class})
public class Canvas extends PanelGroup {

    public static final String CSS_CLASS = "class";

    @Getter
    @Setter
    @XMLProperty(value = CSS_CLASS)
    private String cssClass;

    public Canvas(Form form) {
        super(form);
    }

}