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
var inversify_inject_decorators_1 = require("inversify-inject-decorators");
var Combo_1 = require("./Combo");
var lazyInject = inversify_inject_decorators_1.default(fh_forms_handler_1.FhContainer).lazyInject;
var DictionaryCombo = /** @class */ (function (_super) {
    __extends(DictionaryCombo, _super);
    function DictionaryCombo(componentObj, parent) {
        return _super.call(this, componentObj, parent) || this;
    }
    return DictionaryCombo;
}(Combo_1.Combo));
exports.DictionaryCombo = DictionaryCombo;
//# sourceMappingURL=DictionaryCombo.js.map