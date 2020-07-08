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
var CheckBox = /** @class */ (function (_super) {
    __extends(CheckBox, _super);
    function CheckBox(componentObj, parent) {
        var _this = _super.call(this, componentObj, parent) || this;
        if (componentObj.labelPosition == null) {
            componentObj.labelPosition = 'right';
            componentObj.inputSize = undefined;
            _this.stickedLabel = true;
        }
        _this.rawValue = _this.componentObj.rawValue == true || _this.componentObj.rawValue == "true";
        _this.onChange = _this.componentObj.onChange;
        _this.input = null;
        return _this;
    }
    CheckBox.prototype.create = function () {
        var input = document.createElement('input');
        input.id = this.id;
        input.classList.add('fc');
        input.classList.add('form-check-input');
        input.type = 'checkbox';
        input.checked = this.rawValue;
        input.addEventListener('change', this.inputCheckEvent.bind(this));
        if (this.onChange) {
            input.addEventListener('change', this.onChangeEvent.bind(this));
        }
        this.input = input;
        this.component = input;
        this.focusableComponent = input;
        this.hintElement = this.component;
        this.wrap(false);
        if (this.labelElement) {
            this.labelElement.classList.remove('col-form-label');
            this.labelElement.classList.add('form-check-label');
        }
        if (this.stickedLabel) {
            this.htmlElement.classList.add('stickedLabel');
        }
        this.addStyles();
        this.display();
        if (this.input.disabled == false && !this.rawValue && this.componentObj.rawValue == undefined) {
            this.changesQueue.queueValueChange(false);
        }
        this.htmlElement.insertBefore(input, this.htmlElement.firstChild);
    };
    ;
    CheckBox.prototype.inputCheckEvent = function () {
        this.changesQueue.queueValueChange(this.input.checked);
    };
    CheckBox.prototype.onChangeEvent = function () {
        this.fireEventWithLock('onChange', this.onChange);
    };
    CheckBox.prototype.update = function (change) {
        _super.prototype.update.call(this, change);
        $.each(change.changedAttributes || [], function (name, newValue) {
            switch (name) {
                case 'rawValue':
                    this.rawValue = (newValue == true) || (newValue == "true");
                    this.input.checked = this.rawValue;
                    if (this.input.disabled == false && !this.rawValue && ((newValue !== false) && (newValue !== "false"))) {
                        this.changesQueue.queueValueChange(false);
                    }
                    break;
            }
        }.bind(this));
    };
    ;
    CheckBox.prototype.extractChangedAttributes = function () {
        return this.changesQueue.extractChangedAttributes();
    };
    ;
    CheckBox.prototype.addAlignStyles = function () {
        _super.prototype.addAlignStyles.call(this);
        var horizontalAlign = '';
        var verticalAlign = '';
        if (!this.componentObj.horizontalAlign) {
            horizontalAlign = 'start';
        }
        else if (this.componentObj.horizontalAlign && this.htmlElement) {
            switch (this.componentObj.horizontalAlign) {
                case "LEFT":
                    horizontalAlign = "start";
                    break;
                case "CENTER":
                    horizontalAlign = "center";
                    break;
                case "RIGHT":
                    horizontalAlign = "end";
                    break;
            }
        }
        this.htmlElement.classList.add('justify-content-' + horizontalAlign);
        if (!this.componentObj.verticalAlign) {
            verticalAlign = "start";
        }
        else if (this.componentObj.verticalAlign && this.htmlElement) {
            switch (this.componentObj.verticalAlign) {
                case "TOP":
                    verticalAlign = "start";
                    break;
                case "MIDDLE":
                    verticalAlign = "center";
                    break;
                case "BOTTOM":
                    verticalAlign = "end";
                    break;
            }
        }
        this.htmlElement.classList.add('align-items-' + verticalAlign);
    };
    ;
    CheckBox.prototype.wrap = function (skipLabel) {
        _super.prototype.wrap.call(this, skipLabel);
        this.htmlElement.classList.add('form-group');
        this.htmlElement.classList.add('form-check');
    };
    ;
    CheckBox.prototype.setAccessibility = function (accessibility) {
        _super.prototype.setAccessibility.call(this, accessibility);
        switch (accessibility) {
            case 'EDIT':
                this.input.disabled = false;
                break;
            default:
                this.input.disabled = true;
                break;
        }
    };
    ;
    CheckBox.prototype.setPresentationStyle = function (presentationStyle) {
        if (this.labelElement) {
            ['is-invalid'].forEach(function (cssClass) {
                this.labelElement.classList.remove(cssClass);
            }.bind(this));
            switch (presentationStyle) {
                case 'BLOCKER':
                case 'ERROR':
                    ['is-invalid'].forEach(function (cssClass) {
                        this.labelElement.classList.add(cssClass);
                    }.bind(this));
                    break;
            }
        }
        _super.prototype.setPresentationStyle.call(this, presentationStyle);
    };
    CheckBox.prototype.getDefaultWidth = function () {
        return "md-2";
    };
    CheckBox.prototype.destroy = function (removeFromParent) {
        this.input.removeEventListener('change', this.inputCheckEvent.bind(this));
        if (this.onChange) {
            this.input.removeEventListener('change', this.onChangeEvent.bind(this));
        }
        _super.prototype.destroy.call(this, removeFromParent);
    };
    /**
     * @Overwrite parent function
     * @param ttip
     */
    CheckBox.prototype.processStaticHintElement = function (ttip) {
        //if label is invisible we add static hint to content.
        this.htmlElement.appendChild(ttip);
        return this.htmlElement;
    };
    return CheckBox;
}(fh_forms_handler_1.HTMLFormComponent));
exports.CheckBox = CheckBox;
//# sourceMappingURL=CheckBox.js.map