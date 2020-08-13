package pl.fhframework.model.forms;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import pl.fhframework.annotations.Control;
import pl.fhframework.annotations.XMLProperty;
import pl.fhframework.tools.loading.IBodyXml;

/**
 * Form comment element
 */
@Control
public class Comment extends Component implements IBodyXml {

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty
    private String text;

    public Comment(Form form) {
        super(form);
    }

    public Comment(Form form, String text) {
        super(form);
        this.text = text;
    }


    @Override
    public void setBody(String body) {
        text = body;
    }

    @Override
    public String getBodyAttributeName() {
        return "text";
    }

    @Override
    public boolean shouldWriteToBody(String value) {
        return true;
    }
}