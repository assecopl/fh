package pl.fhframework.model.forms.attribute;

import pl.fhframework.annotations.XMLPropertyGlobalConverter;

/**
 * Vertical align form element XML attribute converter
 */
@XMLPropertyGlobalConverter(VerticalAlign.class)
public class VerticalAlignAttrConverter extends ToLowerCaseEnumAttrConverter<VerticalAlign> {

    public VerticalAlignAttrConverter() {
        super(VerticalAlign.class);
    }
}
