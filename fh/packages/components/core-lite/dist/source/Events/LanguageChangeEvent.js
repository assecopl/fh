"use strict";
var __extends = (this && this.__extends) || (function () {
    var extendStatics = function (d, b) {
        extendStatics = Object.setPrototypeOf ||
            ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
            function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
        return extendStatics(d, b);
    };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();
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
var BaseEvent_1 = require("./BaseEvent");
var I18n_1 = require("../I18n/I18n");
var inversify_inject_decorators_1 = require("inversify-inject-decorators");
var FhContainer_1 = require("../FhContainer");
var lazyInject = inversify_inject_decorators_1.default(FhContainer_1.FhContainer).lazyInject;
var LanguageChangeEvent = /** @class */ (function (_super) {
    __extends(LanguageChangeEvent, _super);
    function LanguageChangeEvent() {
        return _super !== null && _super.apply(this, arguments) || this;
    }
    LanguageChangeEvent.prototype.fire = function (data) {
        var head = $('html');
        head.attr('lang', data.code);
        this.i18n.selectLanguage(data.code);
    };
    __decorate([
        lazyInject("I18n"),
        __metadata("design:type", I18n_1.I18n)
    ], LanguageChangeEvent.prototype, "i18n", void 0);
    LanguageChangeEvent = __decorate([
        inversify_1.injectable()
    ], LanguageChangeEvent);
    return LanguageChangeEvent;
}(BaseEvent_1.BaseEvent));
exports.LanguageChangeEvent = LanguageChangeEvent;
//# sourceMappingURL=LanguageChangeEvent.js.map