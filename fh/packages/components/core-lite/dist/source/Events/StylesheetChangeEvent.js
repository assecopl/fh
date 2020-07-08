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
Object.defineProperty(exports, "__esModule", { value: true });
var inversify_1 = require("inversify");
var BaseEvent_1 = require("./BaseEvent");
var StylesheetChangeEvent = /** @class */ (function (_super) {
    __extends(StylesheetChangeEvent, _super);
    function StylesheetChangeEvent() {
        return _super !== null && _super.apply(this, arguments) || this;
    }
    StylesheetChangeEvent.prototype.fire = function (data) {
        var head = $('head');
        var stylesheet = head.find("link[title='alternate']");
        if (stylesheet.length > 0) {
            stylesheet.remove();
        }
        head.append($("<link>").attr({
            type: 'text/css',
            rel: 'stylesheet',
            title: 'alternate',
            href: data.name
        }));
    };
    StylesheetChangeEvent = __decorate([
        inversify_1.injectable()
    ], StylesheetChangeEvent);
    return StylesheetChangeEvent;
}(BaseEvent_1.BaseEvent));
exports.StylesheetChangeEvent = StylesheetChangeEvent;
//# sourceMappingURL=StylesheetChangeEvent.js.map