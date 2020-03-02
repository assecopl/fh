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
var Dropdown_1 = require("./Dropdown");
var ThreeDotsMenu = /** @class */ (function (_super) {
    __extends(ThreeDotsMenu, _super);
    function ThreeDotsMenu(componentObj, parent) {
        return _super.call(this, componentObj, parent) || this;
    }
    ThreeDotsMenu.prototype.create = function () {
        _super.prototype.create.call(this);
        this.button.classList.remove('dropdown');
        this.button.classList.add('row-dropdown');
        this.contentWrapper.classList.add('row-dropdown-menu');
    };
    ThreeDotsMenu.prototype.getDefaultWidth = function () {
        return "md-1";
    };
    ThreeDotsMenu.prototype.getAdditionalButtons = function () {
        return [
            new fh_forms_handler_1.AdditionalButton('addDefaultSubcomponent', 'plus', 'Add menu item')
        ];
    };
    return ThreeDotsMenu;
}(Dropdown_1.Dropdown));
exports.ThreeDotsMenu = ThreeDotsMenu;
//# sourceMappingURL=ThreeDotsMenu.js.map