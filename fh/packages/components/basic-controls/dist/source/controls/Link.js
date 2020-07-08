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
var Link = /** @class */ (function (_super) {
    __extends(Link, _super);
    function Link(componentObj, parent) {
        var _this = _super.call(this, componentObj, parent) || this;
        if (componentObj.labelPosition == null) {
            componentObj.labelPosition = 'left';
            componentObj.inputSize = undefined;
            _this.stickedLabel = true;
        }
        return _this;
    }
    Link.prototype.create = function () {
        var link = document.createElement('a');
        var isIcon = !!this.componentObj.icon;
        var inner;
        link.id = this.id;
        link.classList.add('fc');
        link.classList.add('link');
        link.href = this.processURL(this.util.getPath(this.componentObj.url));
        if (this.componentObj.newWindow) {
            link.target = '_blank';
        }
        inner = (this.componentObj.value) ? this.componentObj.value.replace('\\(', '{').replace('\\)', '}') : '';
        if (isIcon) {
            var icon = document.createElement('i');
            var classes = this.componentObj.icon.split(' ');
            switch (classes[0]) {
                case 'fa':
                    icon.classList.add('fa-fw');
                    break;
            }
            icon.classList.add(classes[0]);
            if (classes[1]) {
                icon.classList.add(classes[1]);
            }
            if (this.componentObj.iconAlignment && this.componentObj.iconAlignment.toLowerCase() === 'after') {
                inner = inner + icon.outerHTML;
            }
            else {
                inner = icon.outerHTML + inner;
            }
        }
        if (inner !== "") {
            link.innerHTML += inner;
        }
        else if (!isIcon) {
            link.innerHTML += link.href;
        }
        var additionalWrapper = document.createElement('div');
        additionalWrapper.classList.add('link-wrapper');
        additionalWrapper.appendChild(link);
        this.component = additionalWrapper;
        this.hintElement = this.component;
        this.wrap();
        if (this.stickedLabel) {
            this.htmlElement.classList.add('stickedLabel');
        }
        this.addStyles();
        this.display();
    };
    ;
    Link.prototype.wrap = function () {
        var wrapper = document.createElement('div');
        ['fc', 'wrapper', 'form-group'].forEach(function (cssClass) {
            wrapper.classList.add(cssClass);
        });
        if (this.width) {
            // @ts-ignore
            this.setWrapperWidth(wrapper, undefined, this.width);
        }
        else {
            wrapper.classList.add('inline');
        }
        if (this.componentObj.value) {
            var label = document.createElement('label');
            var labelValue = this.fhml.resolveValueTextOrEmpty(this.componentObj.value);
            var labelId = this.id + '_label';
            label.id = labelId;
            label.classList.add('col-form-label');
            label.classList.add('sr-only');
            label.innerHTML = labelValue;
            label.setAttribute('for', this.id);
            label.setAttribute('aria-label', labelValue);
            this.component.insertBefore(label, this.component.firstChild);
            this.labelElement = label;
            this.component.setAttribute('aria-describedby', label.id);
        }
        wrapper.appendChild(this.component);
        this.htmlElement = wrapper;
        this.contentWrapper = this.component;
    };
    ;
    Link.prototype.update = function (change) {
        _super.prototype.update.call(this, change);
        $.each(change.changedAttributes || [], function (name, newValue) {
            // no special attributes supported
        }.bind(this));
    };
    ;
    /**
     * @Override
     */
    Link.prototype.getDefaultWidth = function () {
        return "md-2";
    };
    return Link;
}(fh_forms_handler_1.HTMLFormComponent));
exports.Link = Link;
//# sourceMappingURL=Link.js.map