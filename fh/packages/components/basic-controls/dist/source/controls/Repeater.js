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
var Repeater = /** @class */ (function (_super) {
    __extends(Repeater, _super);
    function Repeater(componentObj, parent) {
        var _this = _super.call(this, componentObj, parent) || this;
        if (_this.parent instanceof fh_forms_handler_1.HTMLFormComponent) {
            _this.container = _this.parent.contentWrapper;
        }
        else {
            _this.container = _this.parent.container;
        }
        _this.contentWrapper = _this.container;
        _this.referenceNode = null;
        return _this;
    }
    Repeater.prototype.create = function () {
        if (this.designMode == true || this.componentObj.width != null) {
            var repeater = document.createElement('div');
            repeater.id = this.id;
            ['fc', 'row', 'eq-row', 'repeater'].forEach(function (cssClass) {
                repeater.classList.add(cssClass);
            });
            this.component = repeater;
            this.wrap(true);
            this.addStyles();
            this.display();
        }
        if (this.componentObj.subelements) {
            this.addComponents(this.componentObj.subelements);
        }
    };
    ;
    Repeater.prototype.setAccessibility = function (accessibility) {
        if (this.htmlElement) {
            if (accessibility === 'HIDDEN') {
                if (this.invisible) {
                    this.htmlElement.classList.add('invisible');
                }
                else {
                    this.htmlElement.classList.add('d-none');
                }
            }
            else {
                this.htmlElement.classList.remove('d-none');
                this.htmlElement.classList.remove('invisible');
            }
        }
        this.accessibility = accessibility;
    };
    // noinspection JSUnusedGlobalSymbols
    Repeater.prototype.setPresentationStyle = function (presentationStyle) {
        if (this.parent != null) {
            // @ts-ignore
            this.parent.setPresentationStyle(presentationStyle);
        }
    };
    return Repeater;
}(fh_forms_handler_1.HTMLFormComponent));
exports.Repeater = Repeater;
//# sourceMappingURL=Repeater.js.map