declare class FormComponentChangesQueue {
    private VALUE_ATTRIBUTE_NAME;
    private changedAttributes;
    constructor();
    queueAttributeChange(attribute: any, newValue: any): void;
    queueManyAttributesChange(attributes: any): void;
    queueValueChange(newValue: any): void;
    extractChangedAttributes(): {};
}
export { FormComponentChangesQueue };
