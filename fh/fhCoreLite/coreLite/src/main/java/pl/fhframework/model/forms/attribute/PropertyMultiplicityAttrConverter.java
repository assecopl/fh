package pl.fhframework.model.forms.attribute;

import pl.fhframework.model.forms.Component;
import pl.fhframework.model.forms.Property;

/**
 * Property.multiplicity attribute converter
 */
public class PropertyMultiplicityAttrConverter extends ToLowerCaseEnumAttrConverter<Property.PropertyMultiplicity> {

    public PropertyMultiplicityAttrConverter() {
        super(Property.PropertyMultiplicity.class);
    }

    @Override
    public Property.PropertyMultiplicity fromXML(Component owner, String value) {
        // migrate old multiple=true/false attribute
        if ("false".equals(value)) {
            value = Property.PropertyMultiplicity.SINGLE.name().toLowerCase();
        } else if ("true".equals(value)) {
            value = Property.PropertyMultiplicity.MULTIPLE.name().toLowerCase();
        }

        if (value != null) {
            // mutiple-pageable ->  mutiple_pageable ( -> MUTIPLE_PAGEABLE )
            value = value.replace('-', '_');
        }
        return super.fromXML(owner, value);
    }

    @Override
    public String toXML(Class<? extends Component> ownerClass, Property.PropertyMultiplicity value) {
        String valueStr = super.toXML(ownerClass, value);
        if (valueStr != null) {
            // MUTIPLE_PAGEABLE -> MUTIPLE-PAGEABLE ( ->  mutiple-pageable )
            valueStr = valueStr.replace('_', '-');
        }
        return valueStr;
    }
}
