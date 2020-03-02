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
var Spacer = /** @class */ (function (_super) {
    __extends(Spacer, _super);
    function Spacer(componentObj, parent) {
        return _super.call(this, componentObj, parent) || this;
    }
    Spacer.prototype.create = function () {
        this.component = document.createElement('div');
        this.component.id = this.id;
        this.wrap(true);
        // spacer.classList.add('spacer');
        this.component.parentNode.classList.add('spacer');
        this.addStyles();
        this.display();
    };
    ;
    return Spacer;
}(fh_forms_handler_1.HTMLFormComponent));
exports.Spacer = Spacer;
//# sourceMappingURL=Spacer.js.map