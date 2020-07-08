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
var fh_forms_handler_2 = require("fh-forms-handler");
var RadioOption = /** @class */ (function (_super) {
    __extends(RadioOption, _super);
    function RadioOption(componentObj, parent) {
        var _this = _super.call(this, componentObj, parent) || this;
        _this.valueChanged = false;
        _this.onChange = _this.componentObj.onChange;
        _this.rawValue = _this.componentObj.rawValue;
        _this.groupName = _this.componentObj.groupName;
        _this.checked = _this.componentObj.checked;
        return _this;
    }
    RadioOption.prototype.create = function () {
        var input = document.createElement('input');
        input.type = 'radio';
        input.name = this.groupName;
        input.checked = this.checked;
        input.classList.add('fc');
        input.classList.add('form-check-input');
        input.id = this.id;
        input.addEventListener('click', function () {
            this.updateModel();
        }.bind(this));
        input.addEventListener('click', function (event) {
            this.fireEventWithLock('onChange', this.onChange, event);
        }.bind(this));
        this.input = input;
        this.component = input;
        this.hintElement = this.component;
        this.wrap(false);
        if (this.labelElement) {
            this.labelElement.classList.remove('col-form-label');
            this.labelElement.classList.add('form-check-label');
        }
        this.addStyles();
        this.display();
        this.focusableComponent = this.htmlElement;
        this.htmlElement.tabIndex = 0;
        this.htmlElement.insertBefore(input, this.htmlElement.firstChild);
    };
    RadioOption.prototype.update = function (change) {
        _super.prototype.update.call(this, change);
        if (change.changedAttributes) {
            $.each(change.changedAttributes, function (name, newValue) {
                switch (name) {
                    case 'checkedRadio':
                        this.componentObj.checked = newValue;
                        this.checked = newValue;
                        this.input.checked = newValue;
                        break;
                }
            }.bind(this));
        }
    };
    ;
    RadioOption.prototype.wrap = function (skipLabel) {
        _super.prototype.wrap.call(this, skipLabel);
        this.htmlElement.classList.add('form-group');
        this.htmlElement.classList.add('form-check');
        this.htmlElement.classList.add('positioned');
        this.htmlElement.classList.add('right');
        this.htmlElement.classList.add('radioOption');
    };
    ;
    RadioOption.prototype.setAccessibility = function (accessibility) {
        _super.prototype.setAccessibility.call(this, accessibility);
        switch (accessibility) {
            case 'VIEW':
                this.input.disabled = true;
                this.component.classList.remove('fc-editable');
                this.component.classList.add('fc-disabled');
                break;
            default:
                this.input.disabled = false;
                this.component.classList.add('fc-editable');
                this.htmlElement.classList.remove('fc-disabled');
                break;
        }
    };
    ;
    RadioOption.prototype.setPresentationStyle = function (presentationStyle) {
        if (this.labelElement && this.labelElement.classList.contains('empty-label')) {
            ['border', 'border-success', 'border-info', 'border-warning', 'border-danger', 'is-invalid'].forEach(function (cssClass) {
                this.htmlElement.classList.remove(cssClass);
            }.bind(this));
            switch (presentationStyle) {
                case 'BLOCKER':
                case 'ERROR':
                    ['is-invalid', 'border', 'border-danger'].forEach(function (cssClass) {
                        this.htmlElement.classList.add(cssClass);
                    }.bind(this));
                    break;
                case 'OK':
                    ['border', 'border-success'].forEach(function (cssClass) {
                        this.htmlElement.classList.add(cssClass);
                    }.bind(this));
                    break;
                case 'INFO':
                    ['border', 'border-info'].forEach(function (cssClass) {
                        this.htmlElement.classList.add(cssClass);
                    }.bind(this));
                    break;
                case 'WARNING':
                    ['border', 'border-warning'].forEach(function (cssClass) {
                        this.htmlElement.classList.add(cssClass);
                    }.bind(this));
                    break;
            }
        }
        _super.prototype.setPresentationStyle.call(this, presentationStyle);
    };
    RadioOption.prototype.updateModel = function () {
        this.valueChanged = true;
    };
    ;
    RadioOption.prototype.extractChangedAttributes = function () {
        if (this.designMode == true) {
            return this.changesQueue.extractChangedAttributes();
        }
        var attrs = {};
        if (this.valueChanged && this.input.checked) {
            attrs[fh_forms_handler_2.FormComponent.VALUE_ATTRIBUTE_NAME] = this.rawValue;
            this.valueChanged = false;
        }
        return attrs;
    };
    ;
    return RadioOption;
}(fh_forms_handler_1.HTMLFormComponent));
exports.RadioOption = RadioOption;
//# sourceMappingURL=RadioOption.js.map