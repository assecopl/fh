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
var Footer = /** @class */ (function (_super) {
    __extends(Footer, _super);
    function Footer(componentObj, parent) {
        return _super.call(this, componentObj, parent) || this;
    }
    Footer.prototype.create = function () {
        var footer = document.createElement('div');
        ['fc', 'footer', 'card-footer'].forEach(function (cssClass) {
            footer.classList.add(cssClass);
        });
        var content = document.createElement('div');
        content.classList.add('row');
        content.classList.add('eq-row');
        footer.appendChild(content);
        this.component = footer;
        this.htmlElement = this.component;
        this.contentWrapper = content;
        this.display();
        if (this.componentObj.subelements) {
            this.addComponents(this.componentObj.subelements);
        }
    };
    ;
    return Footer;
}(fh_forms_handler_1.HTMLFormComponent));
exports.Footer = Footer;
//# sourceMappingURL=Footer.js.map