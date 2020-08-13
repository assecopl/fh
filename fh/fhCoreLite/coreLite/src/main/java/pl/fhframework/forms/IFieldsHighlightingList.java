package pl.fhframework.forms;

import pl.fhframework.model.PresentationStyleEnum;
import pl.fhframework.validation.FormFieldHints;

import java.util.Map;

public interface IFieldsHighlightingList {
    FormFieldHints getPresentationStyleForField(Object parent, String attributeName);

    void add(Object parent, String attributeName, PresentationStyleEnum presentationStyleEnum);

    void add(Object parent, String attributeName, PresentationStyleEnum presentationStyleEnum, String hint);

    void remove(Object parent, String attributeName);

    void clear();

    Map<Object, Map<String, FormFieldHints>> getFieldsHighlight();
}
