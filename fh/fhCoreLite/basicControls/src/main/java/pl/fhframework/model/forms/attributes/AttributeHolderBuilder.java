package pl.fhframework.model.forms.attributes;

public class AttributeHolderBuilder {
    private AttributeHolder attributeHolder;

    public AttributeHolderBuilder() {
        attributeHolder = new AttributeHolder();
    }

    public AttributeHolderBuilder attribute(Attribute attribute){
        attributeHolder.addAttribute(attribute);
        return this;
    }

    public AttributeHolder build() {
        return attributeHolder;
    }
}