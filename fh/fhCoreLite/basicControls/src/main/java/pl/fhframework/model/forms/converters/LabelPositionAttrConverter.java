package pl.fhframework.model.forms.converters;

import pl.fhframework.annotations.XMLPropertyGlobalConverter;
import pl.fhframework.model.forms.attribute.ToLowerCaseEnumAttrConverter;
import pl.fhframework.model.forms.model.LabelPosition;

/**
 * Label position input element XML attribute converter
 */
@XMLPropertyGlobalConverter(LabelPosition.class)
public class LabelPositionAttrConverter extends ToLowerCaseEnumAttrConverter<LabelPosition> {

    public LabelPositionAttrConverter() {
        super(LabelPosition.class);
    }
}
