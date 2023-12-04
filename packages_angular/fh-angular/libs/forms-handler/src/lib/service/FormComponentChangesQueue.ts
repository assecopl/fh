class FormComponentChangesQueue {
    private VALUE_ATTRIBUTE_NAME: string;
    private changedAttributes: {};

    constructor() {
        this.VALUE_ATTRIBUTE_NAME = 'value';
        this.changedAttributes = {};
    }

    /* Queue attribute change for sending to server */
    public queueAttributeChange(attribute, newValue) {
        if (newValue === undefined) {
            delete this.changedAttributes[attribute];
        } else {
            this.changedAttributes[attribute] = newValue;
        }
    };

    /* Queue attributes change for sending to server */
    public queueManyAttributesChange(attributes) {
        for (var attr in attributes) {
            this.queueAttributeChange(attr, attributes[attr]);
        }
    };

    /* Queue main value change for sending to server. Convenient use of queueAttributeChange with special attribute meaning main value. */
    public queueValueChange(newValue) {
        this.queueAttributeChange(this.VALUE_ATTRIBUTE_NAME, newValue);
    };

    /* Get pending attributes' changes and forget them. */
    public extractChangedAttributes() {
        var result = this.changedAttributes;
        this.changedAttributes = {};
        return result;
    };
}

export {FormComponentChangesQueue};
