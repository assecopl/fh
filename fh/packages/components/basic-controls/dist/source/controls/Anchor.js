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
var $ = require("jquery");
var Anchor = /** @class */ (function (_super) {
    __extends(Anchor, _super);
    function Anchor(componentObj, parent) {
        var _this = _super.call(this, componentObj, parent) || this;
        _this.scrollOnStart = false;
        _this.scroll = false;
        _this.animateDuration = 0;
        _this.scrollOnStart = _this.componentObj.scrollOnStart ? _this.componentObj.scrollOnStart : false;
        _this.scroll = _this.componentObj.scroll ? _this.componentObj.scroll : false;
        _this.animateDuration = _this.componentObj.animateDuration ? _this.componentObj.animateDuration : 0;
        if (_this.parent instanceof fh_forms_handler_1.HTMLFormComponent) {
            _this.container = _this.parent.contentWrapper;
        }
        else {
            _this.container = _this.parent.container;
        }
        _this.contentWrapper = _this.container;
        return _this;
    }
    Anchor.prototype.create = function () {
        var element = document.createElement('div');
        element.id = this.id;
        // element.textContent = " ";
        element.style.display = 'inline-block';
        element.style.width = "100%";
        element.style.height = "1px";
        // element.style.background = "";
        this.component = element;
        this.htmlElement = this.component;
        this.contentWrapper = this.htmlElement;
        // this.wrap(true);
        this.display();
    };
    ;
    Anchor.prototype.display = function () {
        this.container.appendChild(this.htmlElement);
        if (this.scrollOnStart) {
            this.addAfterInitActions(this.scrollNow.bind(this));
            this.scrollOnStart = false;
        }
    };
    Anchor.prototype.update = function (change) {
        if (change.changedAttributes) {
            $.each(change.changedAttributes, function (name, newValue) {
                switch (name) {
                    case 'scroll':
                        if (newValue) {
                            this.scrollNow(true);
                        }
                        this.setAccessibility(newValue);
                        break;
                }
            }.bind(this));
        }
    };
    ;
    Anchor.prototype.scrollNow = function () {
        this.util.scrollToComponent(this.id, this.animateDuration);
    };
    return Anchor;
}(fh_forms_handler_1.HTMLFormComponent));
exports.Anchor = Anchor;
//# sourceMappingURL=Anchor.js.map