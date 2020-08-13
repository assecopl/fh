package pl.fhframework.model.forms.docs.model;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.annotations.DocumentedComponent;
import pl.fhframework.core.designer.ComponentElement;

import java.util.List;

@Getter
@Setter
public class FormElementCategory {
    private String name;
    private DocumentedComponent.Category category;
    private List<ComponentElement> components;

    public FormElementCategory(String name, DocumentedComponent.Category category, List<ComponentElement> components) {
        this.name = name;
        this.category = category;
        this.components = components;
    }
}
