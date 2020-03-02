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
var InputText_pl_1 = require("./i18n/InputText.pl");
var InputText_en_1 = require("./i18n/InputText.en");
var fh_forms_handler_1 = require("fh-forms-handler");
var fh_forms_handler_2 = require("fh-forms-handler");
var InputText = /** @class */ (function (_super) {
    __extends(InputText, _super);
    function InputText(componentObj, parent) {
        var _this = this;
        if (componentObj.rawValue == undefined) {
            componentObj.rawValue = '';
        }
        if (componentObj.value == undefined) {
            componentObj.value = '';
        }
        _this = _super.call(this, componentObj, parent) || this;
        _this.oldValue = componentObj.rawValue || componentObj.value || '';
        _this.i18n.registerStrings('pl', InputText_pl_1.InputTextPL);
        _this.i18n.registerStrings('en', InputText_en_1.InputTextEN);
        _this.isTextarea = _this.componentObj.rowsCount && _this.componentObj.rowsCount > 0;
        _this.inputType = _this.componentObj.inputType || null;
        _this.placeholder = _this.componentObj.placeholder || null;
        _this.maxLength = _this.componentObj.maxLength || null;
        _this.onInput = _this.componentObj.onInput;
        _this.onChange = _this.componentObj.onChange;
        _this.input = null;
        _this.valueChanged = false;
        _this.mask = _this.componentObj.mask;
        _this.maskDynamic = _this.componentObj.maskDynamic || false;
        _this.height = _this.componentObj.height;
        _this.maskDefinition = _this.componentObj.maskDefinition;
        // @ts-ignore
        _this.keySupport = fh_forms_handler_2.FhContainer.get("FormComponentKeySupport")(_this.componentObj, _this);
        _this.textAlign = _this.componentObj.textAlign || 'LEFT';
        _this.lastValidMaskedValue = _this.oldValue;
        _this.timeoutFunction = null;
        _this.maskInsertMode = _this.componentObj.maskInsertMode || false;
        _this.inputTimeout = 500; // timeout od użycia do zdarzenia
        _this.defineDefinitionSymbols();
        return _this;
    }
    InputText.prototype.create = function () {
        var input;
        if (this.isTextarea || this.height && parseInt(this.height) > 40) {
            input = document.createElement('textarea');
            if (this.componentObj.rowsCount) {
                input.rows = this.componentObj.rowsCount;
            }
            input.appendChild(document.createTextNode(this.rawValue));
        }
        else {
            input = document.createElement('input');
            if (this.inputType == 'password') {
                input.type = 'password';
            }
            else {
                input.type = 'text';
            }
            input.value = this.rawValue;
        }
        input.id = this.id;
        ['fc', 'inputText', 'form-control'].forEach(function (cssClass) {
            input.classList.add(cssClass);
        });
        if (this.placeholder) {
            input.placeholder = this.placeholder;
        }
        if (this.maxLength) {
            input.maxLength = this.maxLength;
        }
        if (this.textAlign !== 'LEFT') {
            input.classList.add('text-right');
        }
        // must be before onInput/onChange events
        this.keySupportCallback = this.keySupport.addKeyEventListeners(input);
        $(input).on('input', this.updateModel.bind(this));
        if (this.onChange) {
            $(input).on('change', this.onChangeEvent.bind(this));
        }
        $(input).on('input', this.onInputEvent.bind(this));
        this.input = input;
        this.component = input;
        this.focusableComponent = input;
        this.hintElement = this.component;
        var skipLabel = this.styleClasses.indexOf('hideLabel') !== -1;
        this.wrap(skipLabel, true);
        this.createIcon();
        this.display();
        this.addStyles();
        if (this.accessibility == 'EDIT') {
            this.applyMask();
        }
        if (this.component.classList.contains('servicesListControl')) {
            this.htmlElement.classList.add('servicesListControlWrapper');
        }
        if (this.fh.isIE()) {
            /**
             * For IE only - prevent of content delete by ESC key press (27)
             */
            this.input.addEventListener('keydown', function (e) {
                var keyCode = (window.event) ? e.which : e.keyCode;
                if (keyCode == 27) { //Escape keycode
                    e.preventDefault();
                    e.stopPropagation();
                    return false;
                }
            });
        }
    };
    InputText.prototype.createIcon = function () {
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
                this.inputGroupElement.insertBefore(group, this.component);
            }
            else if (this.componentObj.iconAlignment === 'AFTER') {
                group.classList.remove('input-group-prepend');
                group.classList.add('input-group-append');
                this.inputGroupElement.appendChild(group);
            }
            else {
                this.inputGroupElement.insertBefore(group, this.component);
            }
        }
    };
    InputText.prototype.onInputEvent = function () {
        clearTimeout(this.timeoutFunction);
        this.timeoutFunction = setTimeout(function () {
            this.forceKeepMaskValid();
            if (this.onInput) {
                this.fireEvent('onInput', this.onInput);
            }
        }.bind(this), this.inputTimeout);
    };
    InputText.prototype.onChangeEvent = function () {
        this.fireEventWithLock('onChange', this.onChange);
    };
    InputText.prototype.forceKeepMaskValid = function () {
        if (this.mask != null) {
            var validPositions = $(this.input)[0].inputmask.maskset.validPositions;
            for (var i = 0;; i++) {
                var validPos = validPositions['' + i];
                if (validPos == null) {
                    break;
                }
                if (validPos.input != null && validPos.match != null && validPos.match.fn != null) {
                    if (!validPos.input.match(validPos.match.fn)) {
                        console.log("Rolling back to: " + this.lastValidMaskedValue);
                        this.input.value = this.lastValidMaskedValue;
                    }
                }
            }
        }
        this.lastValidMaskedValue = this.input.value;
    };
    ;
    InputText.prototype.defineDefinitionSymbols = function () {
        var definitions = {
            "A": {
                validator: "[A-ZĄĆĘŁŃÓŚŹŻa-ząćęłńóśźż]",
                cardinality: 1,
                definitionSymbol: "*",
                casing: "upper"
            },
            "a": {
                validator: "[A-ZĄĆĘŁŃÓŚŹŻa-ząćęłńóśźż]",
                cardinality: 1,
                definitionSymbol: "*",
                casing: "lower"
            },
            "L": {
                validator: "[A-Za-z]",
                cardinality: 1,
                definitionSymbol: "*",
                casing: "upper"
            },
            "l": {
                validator: "[A-Za-z]",
                cardinality: 1,
                definitionSymbol: "*",
                casing: "lower"
            },
            "M": {
                validator: "[0-9a-zA-Z._-]",
                cardinality: 1,
                definitionSymbol: "*"
            },
            "N": {
                validator: "[0-9a-zA-Z ąćęłńóśźżĄĆĘŁŃÓŚŹŻ._-]",
                cardinality: 1,
                definitionSymbol: "*"
            },
            "*": {
                validator: "[0-9a-zA-ZąćęłńóśźżĄĆĘŁŃÓŚŹŻ]",
                cardinality: 1,
                definitionSymbol: "*"
            }
        };
        // @ts-ignore
        Inputmask.extendDefinitions(definitions);
    };
    ;
    InputText.prototype.disableMask = function () {
        if (this.mask && this.inputmaskEnabled) {
            // @ts-ignore
            Inputmask.remove(this.input);
            this.inputmaskEnabled = false;
        }
        this.input.removeEventListener('keydown', this.removeInputPlaceholder);
    };
    InputText.prototype.applyMask = function () {
        var _this = this;
        if (this.mask && !this.inputmaskEnabled) {
            var options = {
                clearMaskOnLostFocus: false,
                greedy: false,
                placeholder: this.makePlaceholder(this.format),
                definitions: {},
                alias: "",
                jitMasking: this.maskDynamic,
                mask: "",
                insertMode: undefined
            };
            if (this.maskInsertMode) {
                options.insertMode = false;
            }
            //create custom definitions
            var customDefs = this.createCustomDefinitionSymbols();
            if (customDefs != {}) {
                options.definitions = customDefs;
            }
            if (this.accessibility == 'EDIT' || (this.accessibility != 'EDIT' && this.rawValue != '')) {
                if (this.mask.startsWith('alias:')) {
                    options.alias = this.mask.replace('alias:', '');
                }
                else {
                    options.mask = this.mask;
                }
            }
            try {
                // @ts-ignore
                this.maskPlugin = Inputmask(options).mask(this.input);
                this.inputmaskEnabled = true;
            }
            catch (e) {
                console.error('Invalidmask library error:');
                console.error(e);
            }
            this.input.addEventListener('keydown', function (event) {
                _this.removeInputPlaceholder(event);
            });
        }
    };
    InputText.prototype.removeInputPlaceholder = function (event) {
        var key = event.keyCode;
        var targetAttributes = event.target.attributes;
        if (key === 8 || key === 46) {
            if (event.target.value.length === 0) {
                var attributePlaceholder = targetAttributes.getNamedItem('placeholder');
                attributePlaceholder.value = "";
            }
        }
    };
    InputText.prototype.createCustomDefinitionSymbols = function () {
        var definitions = {};
        if (this.maskDefinition) {
            var maskDefinitions = this.maskDefinition.split("||");
            for (var i in maskDefinitions) {
                var validator = maskDefinitions[i];
                var defSymbol = validator.substring(0, 1);
                validator = validator.substring(1, validator.length);
                // check for /lower/ and /upper/ options
                var lower = false;
                var upper = false;
                if (validator.startsWith('/lower/')) {
                    validator = validator.substring('/lower/'.length, validator.length);
                    lower = true;
                }
                else if (validator.startsWith('/upper/')) {
                    validator = validator.substring('/upper/'.length, validator.length);
                    upper = true;
                }
                var def = {
                    validator: validator,
                    cardinality: 1,
                    definitionSymbol: "*",
                    casing: ""
                };
                // apply /lower/ and /upper/ options
                if (lower) {
                    def.casing = 'lower';
                }
                else if (upper) {
                    def.casing = 'upper';
                }
                definitions[defSymbol] = def;
            }
        }
        return definitions;
    };
    InputText.prototype.update = function (change) {
        _super.prototype.update.call(this, change);
        if (change.changedAttributes) {
            $.each(change.changedAttributes, function (name, newValue) {
                switch (name) {
                    case 'rawValue':
                        if (!newValue && newValue !== 0) {
                            this.input.placeholder = "";
                        }
                        this.input.value = newValue;
                        this.lastValidMaskedValue = newValue;
                        break;
                    case 'maxLengthBinding':
                        this.maxLength = newValue;
                        this.input.maxLength = newValue;
                        break;
                }
            }.bind(this));
        }
    };
    ;
    InputText.prototype.updateModel = function () {
        this.valueChanged = true;
        this.oldValue = this.rawValue;
        this.rawValue = this.input.value;
    };
    ;
    InputText.prototype.extractChangedAttributes = function () {
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
    InputText.prototype.wrap = function (skipLabel, isInputElement) {
        _super.prototype.wrap.call(this, skipLabel, isInputElement);
        this.htmlElement.classList.add('form-group');
    };
    ;
    InputText.prototype.setAccessibility = function (accessibility) {
        _super.prototype.setAccessibility.call(this, accessibility);
        switch (accessibility) {
            case 'EDIT':
                this.input.readOnly = false;
                this.input.placeholder = this.makePlaceholder(this.format);
                this.applyMask();
                this.input.removeAttribute('tabIndex');
                break;
            case 'VIEW':
                this.input.disabled = false;
                this.input.readOnly = true;
                this.disableMask();
                this.input.placeholder = '';
                this.input.setAttribute('tabIndex', '-1');
                break;
            case 'DEFECTED':
                this.input.disabled = true;
                this.disableMask();
                this.input.placeholder = '';
                this.input.setAttribute('tabIndex', '-1');
                break;
        }
        this.accessibility = accessibility;
    };
    ;
    InputText.prototype.makePlaceholder = function (format) {
        if (this.format) {
            return this.format;
        }
        else if (this.placeholder) {
            return this.placeholder;
        }
        else {
            return '';
        }
    };
    InputText.prototype.destroy = function (removeFromParent) {
        if (this.keySupportCallback) {
            this.keySupportCallback();
            this.keySupportCallback = null;
        }
        if (this.onChange) {
            $(this.input).off('change', this.onChangeEvent.bind(this));
        }
        $(this.input).off('input', this.onInputEvent.bind(this));
        $(this.input).off('input', this.updateModel.bind(this));
        this.disableMask();
        _super.prototype.destroy.call(this, removeFromParent);
    };
    InputText.prototype.getDefaultWidth = function () {
        return 'md-3';
    };
    return InputText;
}(fh_forms_handler_1.HTMLFormComponent));
exports.InputText = InputText;
//# sourceMappingURL=InputText.js.map