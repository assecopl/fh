package pl.fhframework.docs.forms.component.composits;


import pl.fhframework.core.designer.DocumentedAttribute;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class AttributesTableModel {

    @Getter
    @Setter
    private List<DocumentedAttribute> attributes = new ArrayList<>();
}
