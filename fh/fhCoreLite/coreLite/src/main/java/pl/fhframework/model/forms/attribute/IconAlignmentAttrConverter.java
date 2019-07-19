package pl.fhframework.model.forms.attribute;

import pl.fhframework.annotations.XMLPropertyGlobalConverter;

@XMLPropertyGlobalConverter(IconAlignment.class)
public class IconAlignmentAttrConverter extends ToLowerCaseEnumAttrConverter<IconAlignment> {
    public IconAlignmentAttrConverter() {
        super(IconAlignment.class);
    }
}
