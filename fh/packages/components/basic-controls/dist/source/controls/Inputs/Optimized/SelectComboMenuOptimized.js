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
var SelectComboMenu_1 = require("../SelectComboMenu");
var _ = require("lodash");
var SelectComboMenuOptimized = /** @class */ (function (_super) {
    __extends(SelectComboMenuOptimized, _super);
    function SelectComboMenuOptimized(componentObj, parent) {
        var _this = _super.call(this, componentObj, parent) || this;
        _this.values = _this.componentObj.filteredValues || [];
        _this.input = null;
        _this.autocompleter = null;
        _this.selectedIndex = null;
        _this.highlighted = null;
        _this.blurEvent = false;
        _this.autocompleteOpen = false;
        _this.onSpecialKey = _this.componentObj.onSpecialKey;
        _this.onDblSpecialKey = _this.componentObj.onDblSpecialKey;
        _this.freeTyping = _this.componentObj.freeTyping;
        _this.emptyLabel = _this.componentObj.emptyLabel || false;
        _this.emptyLabelText = _this.componentObj.emptyLabelText || '';
        return _this;
    }
    SelectComboMenuOptimized.prototype.create = function () {
        var input = document.createElement('input');
        this.input = input;
        input.id = this.id;
        input.type = 'text';
        if (this.emptyLabel && this.rawValue === "") {
            input.value = this.emptyLabelText;
            this.rawValue = this.emptyLabelText;
            this.oldValue = this.emptyLabelText;
        }
        else {
            input.value = this.rawValue;
            this.oldValue = this.rawValue;
        }
        input.autocomplete = 'off';
        ['fc', 'form-control'].forEach(function (cssClass) {
            input.classList.add(cssClass);
        });
        if (this.placeholder) {
            input.placeholder = this.placeholder;
        }
        // must be before onInput/onChange events
        this.keySupport.addKeyEventListeners(input);
        input.addEventListener('input', this.inputInputEvent.bind(this));
        $(input).on('paste', this.inputPasteEvent.bind(this));
        $(input).on('focus', function () {
            this.input.select();
        }.bind(this));
        $(input).on('keydown', this.inputKeydownEvent.bind(this));
        if (this.onInput) {
            input.addEventListener('input', _.debounce(this.inputInputEvent2.bind(this), this.getInputDebounceTime()));
        }
        if (this.onSpecialKey || this.onDblSpecialKey) {
            input.addEventListener('keyup', this.specialKeyCapture.bind(this, true)); // firing event
            input.addEventListener('keydown', this.specialKeyCapture.bind(this, false)); // not firing event, just stop propagation
            input.addEventListener('input', this.specialKeyCapture.bind(this, false)); // not firing event, just stop propagation
        }
        $(input).on('blur', this.inputBlurEvent.bind(this));
        if (this.onChange) {
            $(input).on('blur', this.inputBlurEvent2.bind(this));
        }
        var autocompleter = document.createElement('ul');
        autocompleter.setAttribute('unselectable', 'on');
        ['autocompleter', 'dropdown-menu', 'fc', 'selectcombo'].forEach(function (cssClass) {
            autocompleter.classList.add(cssClass);
        });
        autocompleter.id = this.id + '_autocompleter';
        this.autocompleter = autocompleter;
        /**
         * Prevent clicking on autocompleter from bubbling.
         * There is problem when component is placed inside focusable element(Table).
         * Clicking on scroll bar makes input lose his focus and close autocompleter automaticly
         */
        this.autocompleter.addEventListener("mousedown", function (event) {
            event.preventDefault();
            event.stopPropagation();
        });
        this.component = this.input;
        this.focusableComponent = input;
        this.wrap(false, true);
        this.hintElement = this.inputGroupElement;
        this.inputGroupElement.appendChild(autocompleter);
        this.createOpenButton();
        this.setRequiredField(false);
        if (this.component.classList.contains('servicesListControl')) {
            this.htmlElement.classList.add('servicesListControlWrapper');
        }
        if (this.componentObj.highlightedValue) {
            this.highlighted = this.findByValue(this.componentObj.highlightedValue.targetValue);
        }
        this.display();
    };
    ;
    SelectComboMenuOptimized.prototype.setValues = function (values) {
        while (this.autocompleter.firstChild) {
            this.autocompleter.removeChild(this.autocompleter.firstChild);
        }
        this.values = values;
        if (!values || values.length === 0) {
            this.autocompleter.classList.add('isEmpty');
            return;
        }
        var index = 0;
        this.autocompleter.classList.remove('isEmpty');
        var _loop_1 = function (i, len) {
            var itemValue = values[i];
            var li = document.createElement('li');
            itemValue.element = li;
            var a = document.createElement('a');
            a.classList.add('dropdown-item');
            if (i == 0) {
                a.classList.add(this_1.emptyLabel ? 'dropdown-empty' : 'd-none');
            }
            var displayValue = itemValue.displayAsTarget ? itemValue.targetValue : itemValue.displayedValue;
            var disabled = false;
            a.dataset.index = i.toString();
            a.dataset.findex = index.toString();
            a.dataset.targetValue = itemValue.targetValue;
            // escape and parse FHML
            displayValue = this_1.resolveValue(displayValue);
            a.innerHTML = displayValue;
            a.addEventListener('mousedown', function (index, targetItem) {
                event.preventDefault();
                if (this.accessibility !== 'EDIT' || disabled) {
                    return;
                }
                this.highlighted = this.findByValue(targetItem.targetValue);
                this.selectedIndex = index;
                //if (this.emptyLabel) {
                //    this.selectedIndex = this.selectedIndex - 1;
                //}
                this.input.value = itemValue.displayAsTarget ? itemValue.targetValue : itemValue.displayedValue;
                this.updateModel();
                if (this.onChange && (this.rawValue !== this.oldValue)) {
                    this.fireEventWithLock('onChange', this.onChange);
                    this.changeToFired = false;
                }
                this.closeAutocomplete();
                this.input.select();
            }.bind(this_1, i, itemValue));
            li.appendChild(a);
            this_1.autocompleter.appendChild(li);
            index = index + 1;
        };
        var this_1 = this;
        for (var i = 0, len = values.length; i < len; i++) {
            _loop_1(i, len);
        }
        this.hightlightValue();
        this.container.onmouseover = null;
        this.container.onmousemove = null;
        this.htmlElement.onmousemove = null;
        this.input.onfocus = null;
    };
    SelectComboMenuOptimized.prototype.setAccessibility = function (accessibility) {
        if (accessibility !== 'HIDDEN') {
            this.htmlElement.classList.remove('d-none');
            this.htmlElement.classList.remove('invisible');
        }
        if (accessibility !== 'DEFECTED' || accessibility !== 'VIEW') {
            this.component.classList.remove('fc-disabled', 'disabled');
        }
        if (accessibility !== 'EDIT') {
            this.component.classList.remove('fc-editable');
            this.openButton.classList.remove('fc-editable');
        }
        switch (accessibility) {
            case 'EDIT':
                this.accessibilityResolve(this.component, 'EDIT');
                this.autocompleter.classList.add('fc-editable');
                this.openButton.classList.add('fc-editable');
                this.container.onmousemove = this.setValues.bind(this, this.values);
                this.input.onfocus = this.setValues.bind(this, this.values);
                break;
            case 'VIEW':
                this.accessibilityResolve(this.component, 'VIEW');
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
                this.accessibilityResolve(this.component, 'DEFECTED');
                this.component.title = 'Broken control';
                break;
        }
        this.accessibility = accessibility;
    };
    /**
     * @Override
     * @param accessibility
     */
    SelectComboMenuOptimized.prototype.display = function () {
        _super.prototype.display.call(this);
        // this.container.appendChild(this.htmlElement);
    };
    SelectComboMenuOptimized.prototype.render = function () {
    };
    return SelectComboMenuOptimized;
}(SelectComboMenu_1.SelectComboMenu));
exports.SelectComboMenuOptimized = SelectComboMenuOptimized;
//# sourceMappingURL=SelectComboMenuOptimized.js.map