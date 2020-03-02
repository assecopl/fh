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
var sanitizeHtml = require("sanitize-html/dist/sanitize-html");
var HtmlView = /** @class */ (function (_super) {
    __extends(HtmlView, _super);
    function HtmlView(componentObj, parent) {
        var _this = _super.call(this, componentObj, parent) || this;
        _this.code = _this.sanitizeCode(_this.componentObj.text || '');
        return _this;
    }
    HtmlView.prototype.sanitizeCode = function (code) {
        // @ts-ignore
        return sanitizeHtml(code, {
            allowedTags: ['h1', 'h2', 'h3', 'h4', 'h5', 'h6', 'blockquote', 'p', 'a', 'ul', 'ol',
                'nl', 'li', 'b', 'i', 'strong', 'em', 'strike', 'code', 'hr', 'br', 'div',
                'table', 'thead', 'caption', 'tbody', 'tr', 'th', 'td', 'pre', 'iframe', 'center'],
            allowedAttributes: {
                '*': ['class', 'style'],
                a: ['href', 'name', 'target'],
                img: ['src']
            },
            selfClosing: ['img', 'br', 'hr', 'area', 'base', 'basefont', 'input', 'link', 'meta'],
            allowedSchemes: ['http', 'https', 'ftp', 'mailto'],
            allowedSchemesByTag: {},
            allowedSchemesAppliedToAttributes: ['href', 'src', 'cite'],
            allowProtocolRelative: true
        });
    };
    HtmlView.prototype.create = function () {
        this.element = document.createElement('div');
        this.element.id = this.id + "_element";
        this.element.innerHTML = this.code;
        this.component = this.element;
        this.htmlElement = this.component;
        this.contentWrapper = this.htmlElement;
        this.wrap(false, false);
        this.display();
    };
    ;
    HtmlView.prototype.update = function (change) {
        _super.prototype.update.call(this, change);
        if (change.changedAttributes) {
            $.each(change.changedAttributes, function (name, newValue) {
                if (name === 'text') {
                    this.element.innerHTML = this.sanitizeCode(newValue);
                }
            }.bind(this));
        }
    };
    return HtmlView;
}(fh_forms_handler_1.HTMLFormComponent));
exports.HtmlView = HtmlView;
//# sourceMappingURL=HtmlView.js.map