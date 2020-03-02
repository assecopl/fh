    package pl.fhframework.core.designer;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import pl.fhframework.annotations.DocumentedComponent;
import pl.fhframework.core.uc.IUseCaseNoCallback;
import pl.fhframework.core.uc.IUseCaseNoInput;
import pl.fhframework.model.forms.Form;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public abstract class ComponentElement {
    private String componentName;
    private Class<? extends Form> form;
    private Class<IDocumentationUseCase> useCase;
    private Class<?> clazz;
    private String description;
    private String icon;
    private String size;
    private DocumentedComponent.Category category;
    private List<DocumentedAttribute> attributes;

    private DocumentedAttribute selectedBasicAttribute; // used for nested attributes for now, can be null for some components
    private List<DocumentedAttribute> nestedAttributes = new ArrayList<>();
}
