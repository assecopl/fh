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
var inversify_inject_decorators_1 = require("inversify-inject-decorators");
var Util_1 = require("../Util");
var FhContainer_1 = require("../FhContainer");
var lazyInject = inversify_inject_decorators_1.default(FhContainer_1.FhContainer).lazyInject;
var MessageEvent = /** @class */ (function (_super) {
    __extends(MessageEvent, _super);
    function MessageEvent() {
        var _this = _super !== null && _super.apply(this, arguments) || this;
        _this.escapedMap = {
            '&': '&amp;',
            '<': '&lt;',
            '>': '&gt;',
            '"': '&quot;',
            "'": '&#39;',
            '/': '&#x2F;',
            '`': '&#x60;',
            '=': '&#x3D;',
            '\n': '<br>'
        };
        return _this;
    }
    MessageEvent.prototype.fire = function (data) {
        this.util.showDialog(this.escapeHtml(data.title), this.escapeHtml(data.message), 'OK', 'btn-secondary', null);
    };
    MessageEvent.prototype.escapeHtml = function (string) {
        return String(string).replace(/[&<>"'\/]|[\n]/g, function (s) {
            return this.escapedMap[s];
        }.bind(this));
    };
    __decorate([
        lazyInject("Util"),
        __metadata("design:type", Util_1.Util)
    ], MessageEvent.prototype, "util", void 0);
    MessageEvent = __decorate([
        inversify_1.injectable()
    ], MessageEvent);
    return MessageEvent;
}(BaseEvent_1.BaseEvent));
exports.MessageEvent = MessageEvent;
//# sourceMappingURL=MessageEvent.js.map