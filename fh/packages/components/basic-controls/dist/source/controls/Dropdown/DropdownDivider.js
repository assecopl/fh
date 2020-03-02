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
var DropdownDivider = /** @class */ (function (_super) {
    __extends(DropdownDivider, _super);
    function DropdownDivider(componentObj, parent) {
        return _super.call(this, componentObj, parent) || this;
    }
    DropdownDivider.prototype.create = function () {
        var element = document.createElement('div');
        element.classList.add('dropdown-divider');
        element.id = this.id;
        this.component = element;
        this.htmlElement = this.component;
        this.display();
    };
    ;
    return DropdownDivider;
}(fh_forms_handler_1.HTMLFormComponent));
exports.DropdownDivider = DropdownDivider;
//# sourceMappingURL=DropdownDivider.js.map