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
var inversify_inject_decorators_1 = require("inversify-inject-decorators");
var FormsManager_1 = require("../Socket/FormsManager");
var FhContainer_1 = require("../FhContainer");
var lazyInject = inversify_inject_decorators_1.default(FhContainer_1.FhContainer).lazyInject;
var CustomActions = /** @class */ (function () {
    function CustomActions() {
        this._callbacks = {};
    }
    CustomActions.prototype.CustomActions = function () {
    };
    Object.defineProperty(CustomActions.prototype, "callbacks", {
        get: function () {
            return this._callbacks;
        },
        enumerable: true,
        configurable: true
    });
    __decorate([
        lazyInject('FormsManager'),
        __metadata("design:type", FormsManager_1.FormsManager)
    ], CustomActions.prototype, "formsManager", void 0);
    CustomActions = __decorate([
        inversify_1.injectable()
    ], CustomActions);
    return CustomActions;
}());
exports.CustomActions = CustomActions;
//# sourceMappingURL=CustomActions.js.map