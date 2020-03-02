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
var BaseEvent_1 = require("./BaseEvent");
var CloseTabEvent = /** @class */ (function (_super) {
    __extends(CloseTabEvent, _super);
    function CloseTabEvent() {
        return _super !== null && _super.apply(this, arguments) || this;
    }
    CloseTabEvent.prototype.fire = function (data) {
        // @ts-ignore
        var openedExternalUseCases = window.openedExternalUseCases;
        if (!openedExternalUseCases)
            return;
        if (openedExternalUseCases.has(data.uuid)) {
            var tab = openedExternalUseCases.get(data.uuid);
            tab.close();
            openedExternalUseCases.delete(data.uuid);
        }
    };
    return CloseTabEvent;
}(BaseEvent_1.BaseEvent));
exports.CloseTabEvent = CloseTabEvent;
//# sourceMappingURL=CloseTabEvent.js.map