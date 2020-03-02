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
Object.defineProperty(exports, "__esModule", { value: true });
var fh_forms_handler_1 = require("fh-forms-handler");
var hotkeys_js_1 = require("hotkeys-js");
var KeyboardEvent = /** @class */ (function (_super) {
    __extends(KeyboardEvent, _super);
    function KeyboardEvent(componentObj, parent) {
        var _this = _super.call(this, componentObj, parent) || this;
        _this.shortcut = componentObj.shortcut.toLowerCase().replace('_', '+');
        _this.event = componentObj.eventBinding;
        hotkeys_js_1.default.filter = function () {
            return true;
        };
        hotkeys_js_1.default(_this.shortcut, function (event) {
            event.preventDefault();
            this.fireEventWithLock('onClick', this.event);
        }.bind(_this));
        return _this;
    }
    KeyboardEvent.prototype.destroy = function (removeFromParent) {
        hotkeys_js_1.default.unbind(this.shortcut);
        _super.prototype.destroy.call(this, removeFromParent);
    };
    ;
    return KeyboardEvent;
}(fh_forms_handler_1.FormComponent));
exports.KeyboardEvent = KeyboardEvent;
//# sourceMappingURL=KeyboardEvent.js.map