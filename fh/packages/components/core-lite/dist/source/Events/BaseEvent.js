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
var I18n_1 = require("../I18n/I18n");
var inversify_inject_decorators_1 = require("inversify-inject-decorators");
var FhContainer_1 = require("../FhContainer");
var lazyInject = inversify_inject_decorators_1.default(FhContainer_1.FhContainer).lazyInject;
var BaseEvent = /** @class */ (function () {
    function BaseEvent() {
    }
    __decorate([
        lazyInject("I18n"),
        __metadata("design:type", I18n_1.I18n)
    ], BaseEvent.prototype, "i18n", void 0);
    BaseEvent = __decorate([
        inversify_1.injectable()
    ], BaseEvent);
    return BaseEvent;
}());
exports.BaseEvent = BaseEvent;
//# sourceMappingURL=BaseEvent.js.map