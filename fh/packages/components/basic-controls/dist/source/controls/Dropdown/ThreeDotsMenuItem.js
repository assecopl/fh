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
var DropdownItem_1 = require("./DropdownItem");
var ThreeDotsMenuItem = /** @class */ (function (_super) {
    __extends(ThreeDotsMenuItem, _super);
    function ThreeDotsMenuItem(componentObj, parent) {
        return _super.call(this, componentObj, parent) || this;
    }
    return ThreeDotsMenuItem;
}(DropdownItem_1.DropdownItem));
exports.ThreeDotsMenuItem = ThreeDotsMenuItem;
//# sourceMappingURL=ThreeDotsMenuItem.js.map