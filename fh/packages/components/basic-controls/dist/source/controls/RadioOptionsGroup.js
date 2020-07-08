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
var RadioOptionsGroup = /** @class */ (function (_super) {
    __extends(RadioOptionsGroup, _super);
    function RadioOptionsGroup(componentObj, parent) {
        var _this = this;
        if (componentObj.selectedIndex != null) {
            componentObj.rawValue = componentObj.selectedIndex;
        }
        _this = _super.call(this, componentObj, parent) || this;
        _this.onChange = _this.componentObj.onChange;
        return _this;
    }
    RadioOptionsGroup.prototype.create = function () {
        this.groupDiv = document.createElement('fieldset');
        this.groupDiv.id = this.id;
        this.options = this.componentObj.rawOptions;
        this.refreshOptions();
        this.groupDiv.addEventListener('click', function (event) {
            if (this.accessibility != 'EDIT')
                return;
            if (event.target.tagName === 'LABEL') {
                this.changesQueue.queueValueChange(event.target.parentElement.querySelector('input').dataset.value);
            }
            else if (event.target.tagName === 'INPUT') {
                this.changesQueue.queueValueChange(event.target.dataset.value);
            }
        }.bind(this));
        if (this.onChange) {
            this.groupDiv.addEventListener('click', function (event) {
                if (this.accessibility != 'EDIT')
                    return;
                if (event.target.tagName === 'LABEL' || event.target.tagName === 'INPUT') {
                    this.fireEvent('onChange', this.onChange);
                }
            }.bind(this));
        }
        this.component = this.groupDiv;
        this.hintElement = this.component;
        this.wrap();
        this.addStyles();
        this.display();
    };
    ;
    RadioOptionsGroup.prototype.update = function (change) {
        _super.prototype.update.call(this, change);
        if (change.changedAttributes) {
            $.each(change.changedAttributes, function (name, newValue) {
                switch (name) {
                    case 'selectedIndex':
                        $(this.component).find('input').prop('checked', false);
                        if (newValue != -1) {
                            var selector = 'input[data-value="' + newValue + '"]';
                            $(this.component).find(selector).prop('checked', true);
                        }
                        break;
                    case 'rawOptions':
                        this.options = newValue;
                        this.refreshOptions();
                        break;
                }
            }.bind(this));
        }
    };
    ;
    RadioOptionsGroup.prototype.setAccessibility = function (accessibility) {
        if (accessibility !== 'HIDDEN') {
            this.htmlElement.classList.remove('invisible');
            this.htmlElement.classList.remove('d-none');
        }
        if (accessibility !== 'DEFECTED' || accessibility !== 'EDIT') {
            this.htmlElement.classList.remove('fc-disabled');
        }
        if (accessibility !== 'EDIT') {
            this.htmlElement.classList.remove('fc-editable');
        }
        var inputs = this.component.querySelectorAll('input');
        switch (accessibility) {
            case 'EDIT':
                [].forEach.call(inputs, function (input) {
                    input.disabled = false;
                    input.classList.add('fc-editable');
                });
                break;
            case 'VIEW':
                [].forEach.call(inputs, function (input) {
                    input.disabled = true;
                    input.classList.add('fc-disabled');
                });
                break;
            case 'HIDDEN':
                if (this.invisible) {
                    this.htmlElement.classList.add('invisible');
                }
                else {
                    this.htmlElement.classList.add('d-none');
                }
                break;
            case 'DEFECTED':
                [].forEach.call(inputs, function (input) {
                    input.disabled = true;
                    input.classList.add('fc-disabled');
                    input.title = 'Broken control';
                });
                break;
        }
        this.accessibility = accessibility;
    };
    ;
    RadioOptionsGroup.prototype.extractChangedAttributes = function () {
        return this.changesQueue.extractChangedAttributes();
    };
    ;
    RadioOptionsGroup.prototype.getDefaultWidth = function () {
        return "md-3";
    };
    RadioOptionsGroup.prototype.refreshOptions = function () {
        this.groupDiv.innerHTML = '';
        if (this.options !== undefined) {
            for (var i = 0, size = this.options.length; i < size; i++) {
                var value = this.options[i];
                var formGroup = document.createElement('div');
                formGroup.classList.add('form-group');
                var radio = document.createElement('div');
                radio.classList.add('form-check');
                var label = document.createElement('label');
                label.classList.add('form-check-label');
                var input = document.createElement('input');
                input.classList.add('form-check-input');
                input.type = 'radio';
                input.name = this.id;
                input.id = this.id + "_" + i;
                input.dataset.value = i.toString();
                label.setAttribute('for', input.id);
                if (this.rawValue === i) {
                    input.checked = true;
                }
                label.appendChild(document.createTextNode(value));
                radio.appendChild(input);
                radio.appendChild(label);
                formGroup.appendChild(radio);
                this.groupDiv.appendChild(formGroup);
            }
        }
    };
    return RadioOptionsGroup;
}(fh_forms_handler_1.HTMLFormComponent));
exports.RadioOptionsGroup = RadioOptionsGroup;
//# sourceMappingURL=RadioOptionsGroup.js.map