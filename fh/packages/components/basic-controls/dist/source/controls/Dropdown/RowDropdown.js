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
var Dropdown_1 = require("./Dropdown");
var RowDropdown = /** @class */ (function (_super) {
    __extends(RowDropdown, _super);
    function RowDropdown(componentObj, parent) {
        return _super.call(this, componentObj, parent) || this;
    }
    RowDropdown.prototype.create = function () {
        _super.prototype.create.call(this);
        this.button.classList.remove('dropdown');
        this.button.classList.add('row-dropdown');
        this.contentWrapper.classList.add('row-dropdown-menu');
    };
    return RowDropdown;
}(Dropdown_1.Dropdown));
exports.RowDropdown = RowDropdown;
//# sourceMappingURL=RowDropdown.js.map