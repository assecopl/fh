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
var SelectOneMenu = /** @class */ (function (_super) {
    __extends(SelectOneMenu, _super);
    function SelectOneMenu(componentObj, parent) {
        var _this = _super.call(this, componentObj, parent) || this;
        _this.options = _this.componentObj.rawOptions;
        _this.onChange = _this.componentObj.onChange;
        _this.emptyValue = _this.componentObj.emptyValue;
        _this.emptyLabel = _this.componentObj.emptyLabel;
        _this.emptyLabelText = _this.componentObj.emptyLabelText || '';
        _this.modelBindingText = _this.componentObj.modelBindingText;
        return _this;
    }
    SelectOneMenu.prototype.create = function () {
        var select = document.createElement('select');
        select.id = this.id;
        select.classList.add('form-control', 'selectOneMenu');
        this.buildOptionsList().forEach(function (option) {
            select.appendChild(option);
        });
        select.addEventListener('click', this.selectOnClickEvent.bind(this));
        select.addEventListener('change', this.selectChangeEvent.bind(this));
        this.component = select;
        this.focusableComponent = select;
        this.rawValue = this.componentObj.selectedIndex || 0;
        this.wrap(false);
        this.hintElement = this.inputGroupElement;
        this.addStyles();
        this.display();
        this.component.value = this.rawValue;
        if (this.emptyValue && this.emptyValue == true) {
            //if (this.emptyValue && this.emptyValue == true && (!this.emptyLabel ||
            // this.emptyLabel == false) ) {
            var componetParent = this.component.parentNode;
            componetParent.classList.add('SelectOneMenu');
            componetParent.removeChild(this.component);
            var wrapperDiv = document.createElement('div');
            wrapperDiv.classList.add('input-group');
            var clearButtonElement = document.createElement('div');
            clearButtonElement.classList.add('input-group-append');
            var button = document.createElement('span');
            button.classList.add('input-group-text');
            var icon = document.createElement('i');
            icon.classList.add('fa');
            icon.classList.add('fa-times');
            this.clearButton = button;
            button.addEventListener('click', this.buttonClickEvent.bind(this));
            button.appendChild(icon);
            clearButtonElement.appendChild(button);
            wrapperDiv.appendChild(this.component);
            wrapperDiv.appendChild(clearButtonElement);
            if (this.requiredElement) {
                wrapperDiv.appendChild(this.requiredElement);
            }
            componetParent.appendChild(wrapperDiv);
        }
        this.htmlElement.querySelector('.col-form-label').classList.add('selectOneMenuLabel');
        if (this.component.classList.contains('servicesListControl')) {
            this.htmlElement.classList.add('servicesListControlWrapper');
        }
    };
    ;
    SelectOneMenu.prototype.selectOnClickEvent = function () {
        event.stopPropagation();
    };
    SelectOneMenu.prototype.selectChangeEvent = function () {
        this.rawValue = this.component.value;
        this.changesQueue.queueValueChange(this.rawValue);
        this.defaultOption.disabled = this.rawValue != -1 && !this.emptyLabel;
        if (this.onChange) {
            if (this._formId === 'FormPreview') {
                this.fireEvent('onChange', this.onChange);
            }
            else {
                this.fireEventWithLock('onChange', this.onChange);
            }
        }
    };
    SelectOneMenu.prototype.buttonClickEvent = function (event) {
        // this.selectedIndex = {"":-1};
        this.component.selectedIndex = -1;
        this.rawValue = -1;
        this.changesQueue.queueValueChange(this.rawValue);
        event.stopPropagation();
        if (this.onChange) {
            this.fireEventWithLock('onChange', this.onChange);
        }
    };
    SelectOneMenu.prototype.update = function (change) {
        _super.prototype.update.call(this, change);
        if (change.changedAttributes) {
            $.each(change.changedAttributes, function (name, newValue) {
                switch (name) {
                    case 'rawOptions':
                        this.component.innerHTML = '';
                        this.options = newValue;
                        if (this.options !== undefined) {
                            this.buildOptionsList().forEach(function (option) {
                                this.component.appendChild(option);
                            }.bind(this));
                        }
                        break;
                    case 'selectedIndex':
                        if (typeof change.changedAttributes.selectedIndex === 'undefined') {
                            this.rawValue = 0;
                            this.component.value = 0;
                            this.changesQueue.queueValueChange(this.rawValue);
                        }
                        else {
                            this.component.value = newValue;
                            this.rawValue = newValue;
                        }
                        break;
                }
            }.bind(this));
        }
    };
    ;
    SelectOneMenu.prototype.wrap = function (skipLabel) {
        _super.prototype.wrap.call(this, skipLabel, true);
        this.htmlElement.classList.add('form-group');
    };
    ;
    SelectOneMenu.prototype.removeDuplicatedOptions = function (options) {
        return options.reduce(function (unique, o) {
            if (!unique.some(function (obj) { return obj.textContent === o.textContent; })) {
                unique.push(o);
            }
            return unique;
        }, []);
    };
    SelectOneMenu.prototype.buildOptionsList = function () {
        var options = [];
        if (this.designMode) {
            this.emptyLabel = true;
            this.emptyLabelText = this.modelBindingText;
        }
        var defaultOption = document.createElement('option');
        defaultOption.selected = true;
        defaultOption.disabled = this.rawValue != -1 && !this.emptyLabel;
        if (this.emptyLabel) {
            defaultOption.style.display = 'block';
        }
        else {
            defaultOption.style.display = 'none';
        }
        defaultOption.value = '-1';
        //defaultOption.disabled = true;  - not working in Edge and IE, disabled option is not selected on init (before input is shown on screen)
        defaultOption.appendChild(document.createTextNode(this.emptyLabelText));
        options.push(defaultOption);
        this.defaultOption = defaultOption;
        if (this.options) {
            for (var i = 0, size = this.options.length; i < size; i++) {
                var value = this.options[i];
                var option = document.createElement('option');
                option.value = i.toString();
                option.appendChild(document.createTextNode(value));
                if (this.rawValue === i) {
                    option.selected = true;
                }
                options.push(option);
            }
        }
        return this.removeDuplicatedOptions(options);
    };
    ;
    SelectOneMenu.prototype.extractChangedAttributes = function () {
        return this.changesQueue.extractChangedAttributes();
    };
    ;
    SelectOneMenu.prototype.setAccessibility = function (accessibility) {
        _super.prototype.setAccessibility.call(this, accessibility);
        this.htmlElement.classList.add('fc-editable');
    };
    SelectOneMenu.prototype.getDefaultWidth = function () {
        return "md-3";
    };
    SelectOneMenu.prototype.destroy = function (removeFromParent) {
        this.component.removeEventListener('click', this.selectOnClickEvent.bind(this));
        this.component.removeEventListener('change', this.selectChangeEvent.bind(this));
        if (this.emptyValue && this.emptyValue == true) {
            this.clearButton.removeEventListener('click', this.buttonClickEvent.bind(this));
            _super.prototype.destroy.call(this, removeFromParent);
        }
    };
    return SelectOneMenu;
}(fh_forms_handler_1.HTMLFormComponent));
exports.SelectOneMenu = SelectOneMenu;
//# sourceMappingURL=SelectOneMenu.js.map