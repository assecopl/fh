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
require("imports-loader?moment,define=>false,exports=>false!../../external/inputmask");
var fh_forms_handler_1 = require("fh-forms-handler");
var fh_forms_handler_2 = require("fh-forms-handler");
var InputNumber = /** @class */ (function (_super) {
    __extends(InputNumber, _super);
    function InputNumber(componentObj, parent) {
        var _this = this;
        if (componentObj.rawValue != undefined) {
            componentObj.rawValue = componentObj.rawValue.replace(',', '.');
        }
        _this = _super.call(this, componentObj, parent) || this;
        if (componentObj.rawValue == undefined && componentObj.value == undefined) {
            _this.oldValue = '';
        }
        _this.isTextarea = _this.componentObj.rowsCount && _this.componentObj.rowsCount > 0;
        _this.placeholder = _this.componentObj.placeholder || null;
        _this.maxLength = _this.componentObj.maxLength || null;
        _this.inputmaskEnabled = false;
        _this.onInput = _this.componentObj.onInput;
        _this.onChange = _this.componentObj.onChange;
        _this.maxFractionDigits = _this.componentObj.maxFractionDigits != undefined ? _this.componentObj.maxFractionDigits : null;
        _this.maxIntigerDigits = _this.componentObj.maxIntigerDigits != undefined ? _this.componentObj.maxIntigerDigits : null;
        _this.input = null;
        _this.valueChanged = false;
        // @ts-ignore
        _this.keySupport = fh_forms_handler_2.FhContainer.get("FormComponentKeySupport")(_this.componentObj, _this);
        return _this;
    }
    InputNumber.prototype.create = function () {
        var input;
        input = document.createElement('input');
        input.type = 'text';
        input.value = this.rawValue;
        input.id = this.id;
        ['fc', 'InputNumber', 'form-control'].forEach(function (cssClass) {
            input.classList.add(cssClass);
        });
        if (this.placeholder) {
            input.placeholder = this.placeholder;
        }
        if (this.maxLength) {
            input.maxLength = this.maxLength;
        }
        // must be before onInput/onChange events
        this.keySupportCallback = this.keySupport.addKeyEventListeners(input);
        $(input).on('input', this.updateModel.bind(this));
        if (this.onChange) {
            $(input).on('change', this.onChangeEvent.bind(this));
        }
        if (this.onInput) {
            $(input).on('input', this.onInputEvent.bind(this));
        }
        this.input = input;
        this.component = input;
        this.focusableComponent = input;
        this.hintElement = this.component;
        this.wrap(false);
        this.addStyles();
        this.createIcon();
        // else {
        //     this.htmlElement.appendChild(input);
        // }
        this.display();
        this.applyMask();
    };
    ;
    InputNumber.prototype.applyMask = function () {
        if (!this.inputmaskEnabled) {
            // @ts-ignore
            Inputmask({
                radixPoint: ".",
                regex: this.resolveRegex()
            }).mask(this.input);
            this.inputmaskEnabled = true;
        }
    };
    InputNumber.prototype.disableMask = function () {
        if (this.inputmaskEnabled) {
            // @ts-ignore
            Inputmask.remove(this.input);
            this.inputmaskEnabled = false;
        }
    };
    InputNumber.prototype.onChangeEvent = function () {
        this.fireEventWithLock('onChange', this.onChange);
    };
    InputNumber.prototype.onInputEvent = function () {
        this.fireEvent('onInput', this.onInput);
    };
    InputNumber.prototype.createIcon = function () {
        if (this.componentObj.icon) {
            var group = document.createElement('div');
            group.classList.add('input-group-prepend');
            var groupSpan = document.createElement('span');
            groupSpan.classList.add('input-group-text');
            group.appendChild(groupSpan);
            this.htmlElement.classList.add('hasInputIcon');
            var icon = document.createElement('i');
            var classes = this.componentObj.icon.split(' ');
            icon.classList.add(classes[0]);
            if (classes[1]) {
                icon.classList.add(classes[1]);
            }
            groupSpan.appendChild(icon);
            if (this.componentObj.iconAlignment === 'BEFORE') {
                this.inputGroupElement.insertBefore(group, this.inputGroupElement.firstChild);
            }
            else if (this.componentObj.iconAlignment === 'AFTER') {
                group.classList.remove('input-group-prepend');
                group.classList.add('input-group-append');
                this.inputGroupElement.appendChild(group);
            }
            else {
                this.inputGroupElement.insertBefore(group, this.inputGroupElement.firstChild);
            }
        }
    };
    InputNumber.prototype.update = function (change) {
        _super.prototype.update.call(this, change);
        $.each(change.changedAttributes || [], function (name, newValue) {
            switch (name) {
                case 'rawValue':
                    if (newValue != undefined) {
                        newValue = newValue.replace(',', '.');
                    }
                    this.input.value = newValue;
                    break;
            }
        }.bind(this));
    };
    ;
    InputNumber.prototype.updateModel = function () {
        this.valueChanged = true;
        this.oldValue = this.rawValue;
        this.rawValue = this.input.value;
    };
    ;
    InputNumber.prototype.extractChangedAttributes = function () {
        if (this.designMode == true) {
            return this.changesQueue.extractChangedAttributes();
        }
        var attrs = {};
        if (this.valueChanged) {
            attrs[fh_forms_handler_1.FormComponent.VALUE_ATTRIBUTE_NAME] = this.rawValue;
            this.valueChanged = false;
        }
        return attrs;
    };
    ;
    InputNumber.prototype.wrap = function (skipLabel) {
        _super.prototype.wrap.call(this, skipLabel, true);
        this.htmlElement.classList.add('form-group');
    };
    ;
    InputNumber.prototype.setAccessibility = function (accessibility) {
        _super.prototype.setAccessibility.call(this, accessibility);
        switch (accessibility) {
            case 'EDIT':
                this.applyMask();
                this.input.readOnly = false;
                this.input.removeAttribute('tabIndex');
                break;
            case 'VIEW':
                this.disableMask();
                this.input.disabled = false;
                this.input.readOnly = true;
                this.input.setAttribute('tabIndex', '-1');
                break;
            case 'DEFECTED':
                this.disableMask();
                this.input.disabled = true;
                this.input.setAttribute('tabIndex', '-1');
                break;
        }
        this.accessibility = accessibility;
    };
    ;
    InputNumber.prototype.destroy = function (removeFromParent) {
        if (this.keySupportCallback) {
            this.keySupportCallback();
        }
        if (this.onChange) {
            $(this.input).off('change', this.onChangeEvent.bind(this));
        }
        if (this.onInput) {
            $(this.input).off('input', this.onInputEvent.bind(this));
        }
        $(this.input).off('input', this.updateModel.bind(this));
        this.disableMask();
        _super.prototype.destroy.call(this, removeFromParent);
    };
    InputNumber.prototype.resolveRegex = function () {
        var fractionMark = "[\\d]*)"; // Matches between one and unlimited times
        var integerMark = "[\\d]*"; // Matches between one and unlimited times
        var separatorMark = "([.]{0,1}"; //Matches between zero and one times
        if (this.maxFractionDigits != null) {
            if (this.maxFractionDigits == 0) {
                fractionMark = "";
                separatorMark = "";
            }
            else {
                fractionMark = "[\\d]{0," + this.maxFractionDigits + "})";
            }
        }
        if (this.maxIntigerDigits != null) {
            if (this.maxIntigerDigits == 0) {
                integerMark = "[0]{1}"; // Only 0 can be put before separator
            }
            else {
                integerMark = "[\\d]{0," + this.maxIntigerDigits + "}";
            }
        }
        return "^([-]?" + integerMark + "" + separatorMark + "" + fractionMark + ")$";
    };
    return InputNumber;
}(fh_forms_handler_1.HTMLFormComponent));
exports.InputNumber = InputNumber;
//# sourceMappingURL=InputNumber.js.map