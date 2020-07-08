"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
var AdditionalButton = /** @class */ (function () {
    function AdditionalButton(action, icon, title) {
        this.action = action;
        this.icon = icon;
        this.title = title;
    }
    AdditionalButton.prototype.getAction = function () {
        return this.action;
    };
    AdditionalButton.prototype.getIcon = function () {
        return this.icon;
    };
    AdditionalButton.prototype.getTitle = function () {
        return this.title;
    };
    return AdditionalButton;
}());
exports.AdditionalButton = AdditionalButton;
//# sourceMappingURL=AdditionalButton.js.map