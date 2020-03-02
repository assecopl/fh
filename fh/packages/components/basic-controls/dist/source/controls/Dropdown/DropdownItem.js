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
var OutputLabel_1 = require("../OutputLabel");
var DropdownItem = /** @class */ (function (_super) {
    __extends(DropdownItem, _super);
    function DropdownItem(componentObj, parent) {
        var _this = _super.call(this, componentObj, parent) || this;
        _this.url = _this.componentObj.url || null;
        if (_this.url) {
            _this.url = _this.util.getPath(_this.url);
        }
        _this.onClick = _this.componentObj.onClick;
        _this.labelComponent = null;
        return _this;
    }
    DropdownItem.prototype.create = function () {
        var label = document.createElement('span');
        label.id = this.id + '_label';
        ['fc', 'outputLabel'].forEach(function (cssClass) {
            label.classList.add(cssClass);
        });
        this.component = label;
        this.buildInnerHTML();
        this.hintElement = this.component;
        this.labelComponent = this.component;
        this.labelComponent.id = label.id; // przeniesienie identyfikatora z utworzonej etykiety
        var element = document.createElement('a');
        element.classList.add('dropdown-item');
        element.id = this.id;
        this.labelComponent.setAttribute('for', this.id);
        this.labelComponent.setAttribute('aria-label', this.fhml.resolveValueTextOrEmpty(this.componentObj.value));
        element.setAttribute('aria-labeledby', this.labelComponent.id);
        if (this.url) {
            element.href = this.url;
        }
        element.appendChild(this.labelComponent);
        if (this.onClick) {
            element.addEventListener('click', this.onClickEvent.bind(this));
        }
        this.component = element;
        this.htmlElement = this.component;
        // this.wrap(true);
        this.display();
    };
    ;
    DropdownItem.prototype.onClickEvent = function () {
        this.fireEventWithLock('onClick', this.onClick);
    };
    DropdownItem.prototype.buildInnerHTML = function () {
        if (this.labelComponent != null) { // called during label update
            this.component = this.labelComponent;
            _super.prototype.buildInnerHTML.call(this);
            this.component = this.htmlElement;
        }
        else { // initial call inside OutputLabel.create()
            _super.prototype.buildInnerHTML.call(this);
        }
    };
    DropdownItem.prototype.destroy = function (removeFromParent) {
        if (this.onClick) {
            this.component.removeEventListener('click', this.onClickEvent.bind(this));
        }
        _super.prototype.destroy.call(this, removeFromParent);
    };
    return DropdownItem;
}(OutputLabel_1.OutputLabel));
exports.DropdownItem = DropdownItem;
//# sourceMappingURL=DropdownItem.js.map