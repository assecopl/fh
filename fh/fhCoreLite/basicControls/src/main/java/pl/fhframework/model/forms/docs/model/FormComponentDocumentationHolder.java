package pl.fhframework.model.forms.docs.model;

import pl.fhframework.core.designer.ComponentElement;

import java.util.*;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class FormComponentDocumentationHolder {
    private List<FormElementCategory> formElements = new LinkedList<>();
    private List<ComponentElement> mapElements = new LinkedList<>();
    private List<ComponentElement> map2dElements = new LinkedList<>();
    private ComponentElement selectedFormElement = null;
}
