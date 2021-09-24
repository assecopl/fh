package pl.fhframework.model.forms.attribute;

import pl.fhframework.annotations.XMLPropertyGlobalConverter;

/**
 * Align form element XML attribute converter
 */
@XMLPropertyGlobalConverter(HorizontalAlign.class)
public class HorizontalAlignAttrConverter extends ToLowerCaseEnumAttrConverter<HorizontalAlign> {

    public HorizontalAlignAttrConverter() {
        super(HorizontalAlign.class);
    }
}
