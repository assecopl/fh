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
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
};
Object.defineProperty(exports, "__esModule", { value: true });
var InputText_1 = require("./InputText");
var fh_forms_handler_1 = require("fh-forms-handler");
var inversify_inject_decorators_1 = require("inversify-inject-decorators");
var lazyInject = inversify_inject_decorators_1.default(fh_forms_handler_1.FhContainer).lazyInject;
var Combo = /** @class */ (function (_super) {
    __extends(Combo, _super);
    function Combo(componentObj, parent) {
        var _this = _super.call(this, componentObj, parent) || this;
        _this.tagslist = [];
        _this.openOnFocus = true;
        //Is focus on dropdown element. For IE11 click on srcoll problem.
        _this.autocompleterFocus = false;
        _this.values = _this.componentObj.filteredValues;
        _this.onInputTimeout = _this.componentObj.onInputTimeout ? _this.componentObj.onInputTimeout : null;
        _this.input = null;
        _this.autocompleter = null;
        _this.selectedIndexGroup = null;
        _this.selectedIndex = null;
        _this.forceSendSelectedIndex = false;
        _this.highlighted = null;
        _this.cleared = false;
        _this.lastCursorPosition = _this.componentObj.cursor;
        _this.blurEvent = false;
        _this.openOnFocus = typeof _this.componentObj.openOnFocus === 'undefined' ? true : _this.componentObj.openOnFocus;
        _this.emptyValue = _this.componentObj.emptyValue;
        _this.onSpecialKey = _this.componentObj.onSpecialKey;
        _this.onDblSpecialKey = _this.componentObj.onDblSpecialKey;
        _this.onEmptyValue = _this.componentObj.onEmptyValue;
        _this.freeTyping = _this.componentObj.freeTyping;
        _this.multiselect = _this.componentObj.multiselect;
        _this.multiselectRawValue = _this.componentObj.multiselectRawValue;
        return _this;
    }
    Combo.prototype.create = function () {
        var input = document.createElement('input');
        this.input = input;
        input.id = this.id;
        input.type = 'text';
        input.value = this.rawValue;
        input.autocomplete = 'off';
        ['fc', 'form-control'].forEach(function (cssClass) {
            input.classList.add(cssClass);
        });
        if (this.placeholder) {
            input.placeholder = this.placeholder;
        }
        this.setCursorPositionToInput(this.lastCursorPosition);
        // must be before onInput/onChange events
        this.keySupport.addKeyEventListeners(input);
        input.addEventListener('input', function () {
            this.selectedIndexGroup = null;
            this.selectedIndex = null;
            this.removedIndex = null;
            this.updateModel();
        }.bind(this));
        $(input).on('paste', function (event) {
            event.stopPropagation();
            if (this.accessibility !== 'EDIT') {
                return;
            }
            this.updateModel();
            if (this.onChange && (this.rawValue !== this.oldValue || this.multiselectRawValue !== this.multiselectOldValue)) {
                this.fireEventWithLock('onChange', this.onChange, event);
                this.changeToFired = false;
            }
        }.bind(this));
        $(input).on('keydown', function (event) {
            if (this.accessibility !== 'EDIT') {
                return;
            }
            var keyCode = event.which;
            var options = this.autocompleter.querySelectorAll('li:not(.dropdown-header)');
            if (keyCode === 9 || keyCode === 13) {
                var shouldBlur = true;
                if (this.highlighted != null) {
                    var element = options[this.highlighted].firstChild;
                    this.selectedIndexGroup = element.dataset.group;
                    this.selectedIndex = parseInt(element.dataset.index);
                    this.forceSendSelectedIndex = true;
                    var val = this.values[this.selectedIndexGroup][this.selectedIndex];
                    if (val) {
                        this.input.value = val.displayAsTarget ? val.targetValue : val.displayedValue;
                    }
                    if (element.dataset.targetCursorPosition !== undefined) {
                        this.setCursorPositionToInput(parseInt(element.dataset.targetCursorPosition));
                        shouldBlur = false;
                    }
                }
                this.updateModel();
                if (this.onChange && (this.rawValue !== this.oldValue || this.multiselectRawValue !== this.multiselectOldValue || this.changeToFired)) {
                    this.fireEventWithLock('onChange', this.onChange, event);
                    this.changeToFired = false;
                }
                if (shouldBlur) {
                    this.blurEventWithoutChange = true;
                    this.input.blur(); // must be after onChange
                }
                this.input.focus();
                // @ts-ignore
                $(this.input).trigger({
                    type: 'keypress',
                    which: 9
                });
            }
            else {
                var move = 0;
                switch (keyCode) {
                    case 40: // down arrow
                        move = 1;
                        break;
                    case 38: // up arrow
                        move = -1;
                        break;
                }
                if ((keyCode === 40 || keyCode === 38) && !this.autocompleter.classList.contains('isEmpty')) {
                    var current = options[this.highlighted];
                    if (current) {
                        current.classList.remove('selected');
                    }
                    if (this.highlighted === null) {
                        if (move === 1) {
                            this.highlighted = 0;
                        }
                        else {
                            this.highlighted = options.length - 1;
                        }
                    }
                    else {
                        this.highlighted = this.highlighted + move;
                    }
                    if (this.highlighted <= 0) {
                        this.highlighted = this.highlighted + options.length;
                    }
                    this.highlighted = this.highlighted % options.length;
                    options[this.highlighted].classList.add('selected');
                    this.autocompleter.scrollTop = options[this.highlighted].offsetTop;
                }
                if (this.multiselect && keyCode == 8 && $(this.input).val() === '' && this.tagslist.length > 0) {
                    event.preventDefault();
                    var lastTag = this.tagslist[this.tagslist.length - 1];
                    this.removeTag(encodeURI(lastTag), {});
                    $(this.input).trigger('focus');
                    this.openAutocomplete();
                }
            }
        }.bind(this));
        if (this.onInput) {
            input.addEventListener('input', function () {
                if (this.accessibility === 'EDIT') {
                    if (!this.openOnFocus) {
                        this.openAutocomplete();
                    }
                    this.onInputEvent();
                }
            }.bind(this));
        }
        var specialKeyCapture = function (fireEvent, event) {
            if (event.ctrlKey && event.which === 32 && this.accessibility === 'EDIT') {
                event.stopPropagation();
                event.preventDefault();
                var doubleSepcialKey = this.onDblSpecialKey && this.input && this.input.value == this.rawValueOnLatSpecialKey && this.input.selectionStart == this.cursorPositionOnLastSpecialKey;
                if (fireEvent) {
                    if (!doubleSepcialKey) {
                        this.openAutocomplete();
                        this.rawValueOnLatSpecialKey = this.input.value;
                        this.cursorPositionOnLastSpecialKey = this.input.selectionStart;
                        if (this.onSpecialKey) {
                            this.fireEventWithLock('onSpecialKey', this.onSpecialKey);
                        }
                    }
                    else if (doubleSepcialKey) {
                        this.rawValueOnLatSpecialKey = null;
                        this.cursorPositionOnLastSpecialKey = null;
                        if (this.onDblSpecialKey) {
                            this.changeToFired = false;
                            this.fireEventWithLock('onDblSpecialKey', this.onDblSpecialKey);
                        }
                    }
                }
            }
        };
        if (this.onSpecialKey || this.onDblSpecialKey) {
            input.addEventListener('keyup', specialKeyCapture.bind(this, true)); // firing event
            input.addEventListener('keydown', specialKeyCapture.bind(this, false)); // not firing event, just stop propagation
            input.addEventListener('input', specialKeyCapture.bind(this, false)); // not firing event, just stop propagation
        }
        input.addEventListener('focus', function () {
            if (this.accessibility === 'EDIT') {
                this.onInputEvent();
                if (this.openOnFocus) {
                    this.openAutocomplete();
                }
                this.autocompleterFocus = false;
            }
        }.bind(this));
        input.addEventListener('blur', function (event) {
            //For IE11. Check if event was fired by action on dropdown element.
            if (!this.autocompleterFocus) {
                this.closeAutocomplete();
                if (this.multiselect) {
                    this.addTag(this.input.value);
                }
            }
            else {
                //IE11 Put back focus on input.
                this.input.focus();
                this.autocompleterFocus = false;
                return false;
            }
        }.bind(this));
        if (this.onChange) {
            $(input).on('blur', function (event) {
                if (this.accessibility === 'EDIT' && !this.blurEventWithoutChange && (this.changeToFired || this.rawValue !== this.oldValue || this.multiselectRawValue !== this.multiselectOldValue || this.forceSendSelectedIndex)) {
                    this.blurEvent = true;
                    this.fireEventWithLock('onChange', this.onChange, event.originalEvent);
                    this.changeToFired = false;
                }
                this.blurEventWithoutChange = false;
            }.bind(this));
        }
        var autocompleter = document.createElement('ul');
        ['autocompleter', 'dropdown-menu', 'fc', 'combo'].forEach(function (cssClass) {
            autocompleter.classList.add(cssClass);
        });
        autocompleter.id = this.id + '_autocompleter';
        this.autocompleter = autocompleter;
        this.component = this.input;
        this.focusableComponent = input;
        this.hintElement = this.component;
        this.wrap(false, true);
        this.createIcon();
        if (this.multiselect) {
            this.inputGroupElement.classList.add('tagsinput');
            $(this.inputGroupElement).attr('id', input.id + '_tagsinput');
        }
        this.createClearButton();
        this.setRequiredField(false);
        this.inputGroupElement.appendChild(autocompleter);
        this.addStyles();
        this.display();
        if (this.component.classList.contains('servicesListControl')) {
            this.htmlElement.classList.add('servicesListControlWrapper');
        }
        if (this.multiselect) {
            this.importTags(this.multiselectRawValue ? JSON.parse(this.multiselectRawValue) : []);
            this.multiselectOldValue = this.multiselectRawValue;
            this.setAccessibility(this.accessibility);
        }
        this.setValues(this.values);
    };
    ;
    Combo.prototype.innerWrap = function () {
        if (this.multiselect) {
            return this.prepareTagsInput();
        }
        return this.input;
    };
    Combo.prototype.defineDefinitionSymbols = function () {
        _super.prototype.defineDefinitionSymbols.call(this);
    };
    Combo.prototype.update = function (change) {
        _super.prototype.update.call(this, change);
        if (change.changedAttributes) {
            $.each(change.changedAttributes, function (name, newValue) {
                switch (name) {
                    case 'rawValue':
                        this.input.value = newValue;
                        this.rawValue = newValue;
                        this.oldValue = newValue;
                        break;
                    case 'multiselectRawValue':
                        this.multiselectRawValue = newValue;
                        this.multiselectOldValue = newValue;
                        this.tagslist = [];
                        this.clearTagsInput();
                        this.importTags(JSON.parse(this.multiselectRawValue) || []);
                        this.setValues(this.values);
                        this.setAccessibility(this.accessibility);
                        break;
                    case 'filteredValues':
                        this.highlighted = null;
                        this.setValues(newValue);
                        break;
                    case 'cursor':
                        this.setCursorPositionToInput(newValue);
                        this.lastCursorPosition = newValue;
                        break;
                }
            }.bind(this));
        }
    };
    ;
    Combo.prototype.updateModel = function () {
        this.rawValue = this.input.value;
        if (this.multiselect) {
            this.multiselectRawValue = JSON.stringify(this.tagslist);
        }
    };
    ;
    Combo.prototype.openAutocomplete = function () {
        var formType = this.getFormType();
        var componentsContainer = $(this.component).closest('.card-body');
        var containerHeightAfterListDisplay;
        var containerHeightBeforeListDisplay;
        if (componentsContainer.length > 0) {
            containerHeightBeforeListDisplay = componentsContainer[0].scrollHeight;
        }
        this.autocompleter.classList.add('show');
        if (this.formId === 'designerProperties') {
            containerHeightAfterListDisplay = componentsContainer[0].scrollHeight;
        }
        if (containerHeightAfterListDisplay > containerHeightBeforeListDisplay) {
            this.autocompleter.scrollIntoView({
                behavior: "smooth",
                block: "nearest"
            });
        }
        /**
         * Check if component will overflow window
         * and change direction of view if necessary.
         */
        var bounding = this.autocompleter.getBoundingClientRect();
        var rightOverlap = bounding.right - (window.innerWidth || document.documentElement.clientWidth);
        var bottomOverlap = bounding.bottom - (window.innerHeight || document.documentElement.clientHeight);
        if (rightOverlap > -17) {
            this.autocompleter.style.setProperty('right', '0px', "important");
            this.autocompleter.style.setProperty('left', 'auto', "important");
        }
        //IE11 Set true for autocompleter actions. We need to prevent focusout on input then.
        this.autocompleter.addEventListener('mousedown', function (event) {
            this.autocompleterFocus = true;
        }.bind(this));
        var parent = null;
        if (formType === 'STANDARD') {
            parent = $(this.component).closest('.panel,.splitContainer,.hasHeight');
            //If autocompleter is about to open in container with fixed height we change it's open direction. Direction will be UP.
            if (parent.hasClass('hasHeight')) {
                var parentBound = parent[0].getBoundingClientRect();
                var completerYmaks = bounding.height + bounding.top;
                var parentYmaks = parentBound.top + parentBound.height;
                //Put it as sibling of parent becouse parent has height and elements inside it wont overflow it. Close it when parent begins to scroll.
                if (completerYmaks > parentYmaks) {
                    this.handleContainerOverflow(parent.parent(), this.autocompleter, true);
                }
                else {
                    this.handleContainerOverflow(parent.parent(), this.autocompleter);
                }
                parent.on("scroll", this.closeAutocomplete.bind(this));
            }
            else if (bottomOverlap > 20) {
                this.inputGroupElement.classList.add("dropup");
            }
            if (!parent.hasClass('floating') && !parent.hasClass('splitContainer')) {
                return;
            }
        }
        else if (formType === 'MODAL' || formType === 'MODAL_OVERFLOW') {
            parent = $(this.component).closest('.modal-content');
            this.handleContainerOverflow(parent, this.autocompleter);
        }
        else {
            console.error('Parent not defined.');
            return;
        }
    };
    ;
    Combo.prototype.createClearButton = function () {
        if (this.emptyValue && this.emptyValue === true) {
            // combo.classList.add('input-group');
            var button = document.createElement('div');
            button.classList.add('input-group-append');
            button.classList.add('clearButton');
            var buttonSpan = document.createElement('span');
            buttonSpan.classList.add('input-group-text');
            var icon = document.createElement('i');
            icon.classList.add('fa');
            icon.classList.add('fa-times');
            button.addEventListener('click', function (event) {
                if (this.accessibility === 'EDIT') {
                    this.selectedIndexGroup = "";
                    this.selectedIndex = -1;
                    this.removedIndex = null;
                    this.forceSendSelectedIndex = true;
                    this.cleared = true;
                    this.values = null;
                    this.input.value = '';
                    this.closeAutocomplete();
                    this.setValues(null);
                    this.tagslist = [];
                    if (this.multiselect) {
                        this.clearTagsInput();
                    }
                    this.updateModel();
                    if (this.onChange && (this.rawValue !== this.oldValue || this.multiselectRawValue !== this.multiselectOldValue)) {
                        this.fireEventWithLock('onChange', this.onChange, event);
                        this.changeToFired = false;
                    }
                    if (this.onEmptyValue) {
                        if (this._formId === 'FormPreview') {
                            this.fireEvent('onEmptyValue', this.onEmptyValue);
                        }
                        else {
                            this.fireEventWithLock('onEmptyValue', this.onEmptyValue, event);
                        }
                    }
                }
            }.bind(this));
            buttonSpan.appendChild(icon);
            button.appendChild(buttonSpan);
            if (this.multiselect) {
                this.component.parentNode.parentNode.appendChild(button);
            }
            else {
                this.component.parentNode.appendChild(button);
            }
        }
    };
    Combo.prototype.closeAutocomplete = function () {
        var formType = this.getFormType();
        this.autocompleter.classList.remove('show');
        this.autocompleter.classList.remove('dropup');
        /**
         * Clear inline styles for right direction view.
         */
        this.autocompleter.style.setProperty('right', '', null);
        this.autocompleter.style.setProperty('left', '', null);
        this.autocompleter.style.setProperty('top', '', null);
        var parent = null;
        if (formType === 'STANDARD') {
            parent = $(this.component).closest('.panel');
            if (!this.inputGroupElement.contains(this.autocompleter)) {
                this.inputGroupElement.appendChild(this.autocompleter);
            }
            if (!parent.hasClass('floating')) {
                return;
            }
        }
        else if (formType === 'MODAL' || formType === 'MODAL_OVERFLOW') {
            if (!this.inputGroupElement.contains(this.autocompleter)) {
                this.inputGroupElement.appendChild(this.autocompleter);
            }
        }
    };
    ;
    Combo.prototype.extractChangedAttributes = function () {
        if (this.designMode === true) {
            return this.changesQueue.extractChangedAttributes();
        }
        if (this.accessibility !== 'EDIT') {
            return {};
        }
        var attrs = {
            blur: undefined,
            cleared: undefined,
            text: undefined,
            addedTag: undefined,
            selectedIndexGroup: undefined,
            selectedIndex: undefined,
            cursor: undefined,
            removedIndex: undefined
        };
        if (this.rawValue !== this.oldValue || this.multiselectRawValue !== this.multiselectOldValue || this.forceSendSelectedIndex) {
            if (!this.forceSendSelectedIndex) {
                this.changeToFired = true;
            }
            if (this.cleared) {
                attrs.cleared = this.cleared;
                this.cleared = false;
            }
            if (this.selectedIndex != null) {
                attrs.selectedIndexGroup = this.selectedIndexGroup;
                attrs.selectedIndex = this.selectedIndex;
            }
            else if (this.removedIndex != null) {
                attrs.removedIndex = this.removedIndex;
            }
            else {
                attrs.text = this.rawValue;
            }
            attrs.addedTag = this.addedTag;
            this.addedTag = false;
            this.oldValue = this.rawValue;
            this.multiselectOldValue = this.multiselectRawValue;
            this.forceSendSelectedIndex = false;
            this.selectedIndexGroup = null;
            this.selectedIndex = null;
            this.removedIndex = null;
        }
        if (this.input.selectionStart !== this.lastCursorPosition) {
            this.lastCursorPosition = attrs.cursor = this.input.selectionStart;
        }
        if (this.blurEvent) {
            attrs.blur = true;
            this.blurEvent = false;
        }
        return attrs;
    };
    ;
    Combo.prototype.setValues = function (values) {
        while (this.autocompleter.firstChild) {
            this.autocompleter.removeChild(this.autocompleter.firstChild);
        }
        this.values = values;
        if (!values || Object.keys(values).length === 0) {
            this.autocompleter.classList.add('isEmpty');
            return;
        }
        var index = 0;
        this.autocompleter.classList.remove('isEmpty');
        Object.keys(values).forEach(function (key) {
            var heading = document.createElement('li');
            heading.classList.add('dropdown-header');
            heading.appendChild(document.createTextNode(key));
            this.autocompleter.appendChild(heading);
            var _loop_1 = function (i, len) {
                var itemValue = values[key][i];
                var li = document.createElement('li');
                var a = document.createElement('a');
                a.classList.add('dropdown-item');
                var displayValue = void 0;
                if (this_1.formId === 'designerProperties') {
                    displayValue = itemValue.displayAsTarget ? itemValue.targetValue : itemValue.displayedValueWithoutExtras;
                }
                else {
                    displayValue = itemValue.displayAsTarget ? itemValue.targetValue : itemValue.displayedValue;
                }
                var disabled = false;
                if (this_1.multiselect && this_1.tagslist.indexOf(displayValue) >= 0) {
                    a.classList.add('disabled');
                    a.href = "#";
                    a.onclick = function () {
                        return false;
                    };
                    disabled = true;
                }
                a.dataset.index = i.toString();
                a.dataset.findex = index.toString();
                a.dataset.group = key;
                a.dataset.targetValue = itemValue.targetValue;
                if (itemValue.targetCursorPosition !== undefined) {
                    a.dataset.targetCursorPosition = itemValue.targetCursorPosition;
                }
                // escape and parse FHML
                displayValue = this_1.resolveValue(displayValue);
                a.innerHTML = displayValue;
                a.addEventListener('mousedown', function (itemValue, indexGroup, index, targetValue, targetCursorPosition, event) {
                    event.preventDefault();
                    if (this.accessibility !== 'EDIT' || disabled) {
                        return;
                    }
                    var shouldBlur = true;
                    this.selectedIndexGroup = indexGroup;
                    this.selectedIndex = index;
                    this.removedIndex = null;
                    this.forceSendSelectedIndex = true;
                    this.cleared = false;
                    this.input.value = itemValue.displayAsTarget ? itemValue.targetValue : itemValue.displayedValue;
                    if (targetCursorPosition !== undefined) {
                        this.setCursorPositionToInput(parseInt(targetCursorPosition));
                        shouldBlur = false;
                    }
                    this.updateModel();
                    if (this.onChange && (this.rawValue !== this.oldValue || this.multiselectRawValue !== this.multiselectOldValue)) {
                        this.fireEventWithLock('onChange', this.onChange, event);
                        this.changeToFired = false;
                    }
                    if (shouldBlur) {
                        this.blurEventWithoutChange = true;
                        this.input.blur(); // must be after onChange
                    }
                }.bind(this_1, itemValue, key, i, itemValue.targetValue, itemValue.targetCursorPosition));
                li.appendChild(a);
                this_1.autocompleter.appendChild(li);
                index = index + 1;
            };
            var this_1 = this;
            for (var i = 0, len = values[key].length; i < len; i++) {
                _loop_1(i, len);
            }
        }.bind(this));
    };
    ;
    Combo.prototype.resolveValue = function (value) {
        value = this.fhml.resolveValueTextOrEmpty(value);
        return value;
    };
    ;
    Combo.prototype.setCursorPositionToInput = function (caretPos) {
        if (typeof this.input.selectionStart !== 'undefined' && this.input.setSelectionRange) {
            try {
                this.input.focus();
                this.input.setSelectionRange(caretPos, caretPos);
            }
            catch (ignored) {
                // ignore IE 11 throwing error for non-displayed input
            }
        }
        else if (this.input.createTextRange) {
            var range = this.input.createTextRange();
            range.move('character', caretPos);
            range.select();
        }
    };
    ;
    Combo.prototype.wrap = function (skipLabel, isInputElement) {
        _super.prototype.wrap.call(this, skipLabel, isInputElement);
    };
    Combo.prototype.prepareTagsInput = function () {
        var _this = this;
        var settings = jQuery.extend({
            interactive: true,
            placeholder: 'Add a tag',
            minChars: 0,
            maxChars: null,
            limit: null,
            validationPattern: null,
            width: 'auto',
            height: 'auto',
            autocomplete: null,
            hide: true,
            delimiter: ',',
            unique: true,
            removeWithBackspace: true
        }, {
            onRemoveTag: function (x, value) {
                _this.selectedIndex = null;
                _this.input.value = '';
                _this.closeAutocomplete();
                _this.setValues(_this.values);
                _this.updateModel();
                _this.fireEventWithLock('onChange', _this.onChange);
                _this.changeToFired = false;
            },
            onAddTag: function (x, value) {
                _this.selectedIndex = null;
                _this.input.value = '';
                _this.setValues(_this.values);
                _this.addedTag = true;
                _this.updateModel();
                _this.fireEventWithLock('onChange', _this.onChange);
                _this.changeToFired = false;
            }
        });
        var id = this.id;
        this.tagsInputData = jQuery.extend({
            pid: id,
            real_input: '#' + id,
            holder: '#' + id + '_tagsinput',
            input_wrapper: '#' + id + '_addTag',
        }, settings);
        var markup = $('<div>', { class: 'rawinput', id: id + '_addTag' }).append($(this.input));
        $(this.tagsInputData.holder).css('width', settings.width);
        $(this.tagsInputData.holder).css('min-height', settings.height);
        $(this.tagsInputData.holder).css('height', settings.height);
        return $(markup)[0];
    };
    Combo.prototype.addTag = function (value, options) {
        var _this = this;
        options = jQuery.extend({
            focus: true,
            callback: true,
            noCheck: false
        }, options);
        var id = this.id;
        var tagslist = this.tagslist;
        value = jQuery.trim(value);
        if (value.length > 0 && tagslist.indexOf(value) == -1) {
            if (options.noCheck || this.freeTyping || this.containsValue(value)) {
                $('<span>', { class: 'tag btn btn-outline-secondary' }).append($('<span>', { class: 'tag-text' }).text(value), $('<button>', { class: 'tag-remove' }).click(function () { return _this.removeTag(encodeURI(value), {}); })).insertBefore('#' + id);
                tagslist.push(value);
                $('#' + id + '_tagsinput').val('');
                $('#' + id + '_tagsinput').blur();
            }
        }
        if (options.callback) {
            this.selectedIndex = null;
            this.input.value = '';
            this.setValues(this.values);
            this.closeAutocomplete();
            this.addedTag = true;
            this.updateModel();
            this.changeToFired = false;
        }
    };
    Combo.prototype.removeTag = function (value, options) {
        options = jQuery.extend({
            focus: false,
            callback: true
        }, options);
        value = decodeURI(value);
        this.clearTagsInput();
        this.removedIndex = this.tagslist.indexOf(value);
        this.selectedIndex = null;
        this.selectedIndexGroup = "";
        var reAddTags = Object.assign([], this.tagslist.filter(function (obj) { return obj !== value; }));
        this.tagslist = [];
        this.importTags(reAddTags);
        if (options.callback) {
            this.selectedIndex = null;
            this.input.value = '';
            this.closeAutocomplete();
            this.setValues(this.values);
            this.updateModel();
            this.fireEventWithLock('onChange', this.onChange);
            this.changeToFired = false;
        }
    };
    Combo.prototype.clearTagsInput = function () {
        var id = this.id;
        $('#' + id + '_tagsinput .tag').remove();
    };
    Combo.prototype.importTags = function (tagslist) {
        var _this = this;
        tagslist.forEach(function (value) {
            _this.addTag(value, { callback: false, focus: false, noCheck: true });
        });
    };
    Combo.prototype.getMainComponent = function () {
        if (this.multiselect) {
            return this.component.parentNode;
        }
        return this.component;
    };
    Combo.prototype.setAccessibility = function (accessibility) {
        var _this = this;
        if (accessibility !== 'HIDDEN') {
            this.htmlElement.classList.remove('d-none');
            this.htmlElement.classList.remove('invisible');
        }
        if (accessibility !== 'DEFECTED' || accessibility !== 'VIEW') {
            this.component.classList.remove('fc-disabled', 'disabled');
        }
        if (accessibility !== 'EDIT') {
            this.component.classList.remove('fc-editable');
        }
        switch (accessibility) {
            case 'EDIT':
                this.accessibilityResolve(this.component, 'EDIT');
                this.autocompleter.classList.add('fc-editable');
                if (this.multiselect) {
                    var tags = this.htmlElement.querySelectorAll(".tag-remove");
                    if (tags.length > 0) {
                        tags.forEach(function (node) { return _this.accessibilityResolve(node, 'EDIT'); });
                    }
                }
                break;
            case 'VIEW':
                this.accessibilityResolve(this.component, 'VIEW');
                if (this.multiselect) {
                    var tags = this.htmlElement.querySelectorAll(".tag-remove");
                    if (tags.length > 0) {
                        tags.forEach(function (node) { return _this.accessibilityResolve(node, 'VIEW'); });
                    }
                }
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
    Combo.prototype.containsValue = function (value) {
        if (this.values) {
            for (var _i = 0, _a = Object.keys(this.values); _i < _a.length; _i++) {
                var key = _a[_i];
                for (var _b = 0, _c = this.values[key]; _b < _c.length; _b++) {
                    var element = _c[_b];
                    if (element.targetValue === value) {
                        return true;
                    }
                }
            }
        }
        return false;
    };
    Combo.prototype.onInputEvent = function () {
        if (this.onInput) {
            if (this.onInputTimeout) {
                clearTimeout(this.onInputTimer);
                this.onInputTimer = setTimeout(function () {
                    this.fireEvent('onInput', this.onInput);
                }.bind(this), this.onInputTimeout);
            }
            else {
                this.fireEvent('onInput', this.onInput);
            }
        }
    };
    Combo.prototype.getDefaultWidth = function () {
        return _super.prototype.getDefaultWidth.call(this);
    };
    __decorate([
        lazyInject("FormsManager"),
        __metadata("design:type", fh_forms_handler_1.FormsManager)
    ], Combo.prototype, "formsManager", void 0);
    return Combo;
}(InputText_1.InputText));
exports.Combo = Combo;
//# sourceMappingURL=Combo.js.map