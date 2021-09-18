package pl.fhframework.model.forms.attribute;

import pl.fhframework.annotations.XMLPropertyGlobalConverter;

/**
 * Layout form XML attribute converter
 */
@XMLPropertyGlobalConverter(Layout.class)
public class LayoutAttrConverter extends ToLowerCaseEnumAttrConverter<Layout> {

    public LayoutAttrConverter() {
        super(Layout.class);
    }
}
