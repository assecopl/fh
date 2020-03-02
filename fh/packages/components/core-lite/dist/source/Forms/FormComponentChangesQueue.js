"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
};
Object.defineProperty(exports, "__esModule", { value: true });
var inversify_1 = require("inversify");
var FormComponentChangesQueue = /** @class */ (function () {
    function FormComponentChangesQueue() {
        this.VALUE_ATTRIBUTE_NAME = 'value';
        this.changedAttributes = {};
    }
    /* Queue attribute change for sending to server */
    FormComponentChangesQueue.prototype.queueAttributeChange = function (attribute, newValue) {
        if (newValue === undefined) {
            delete this.changedAttributes[attribute];
        }
        else {
            this.changedAttributes[attribute] = newValue;
        }
    };
    ;
    /* Queue attributes change for sending to server */
    FormComponentChangesQueue.prototype.queueManyAttributesChange = function (attributes) {
        for (var attr in attributes) {
            this.queueAttributeChange(attr, attributes[attr]);
        }
    };
    ;
    /* Queue main value change for sending to server. Convenient use of queueAttributeChange with special attribute meaning main value. */
    FormComponentChangesQueue.prototype.queueValueChange = function (newValue) {
        this.queueAttributeChange(this.VALUE_ATTRIBUTE_NAME, newValue);
    };
    ;
    /* Get pending attributes' changes and forget them. */
    FormComponentChangesQueue.prototype.extractChangedAttributes = function () {
        var result = this.changedAttributes;
        this.changedAttributes = {};
        return result;
    };
    ;
    FormComponentChangesQueue = __decorate([
        inversify_1.injectable(),
        __metadata("design:paramtypes", [])
    ], FormComponentChangesQueue);
    return FormComponentChangesQueue;
}());
exports.FormComponentChangesQueue = FormComponentChangesQueue;
//# sourceMappingURL=FormComponentChangesQueue.js.map