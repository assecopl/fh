import {FhContainer, FormsManager, HTMLFormComponent} from 'fh-forms-handler';
import getDecorators from "inversify-inject-decorators";
import { InputTextFhDP } from './InputTextFhDP';

let {lazyInject} = getDecorators(FhContainer);

class ComboFhDP extends InputTextFhDP {
    @lazyInject("FormsManager")
    protected formsManager: FormsManager;

    protected values: any;
    protected autocompleter: any;
    private selectedIndexGroup: any;
    private selectedIndex: any;
    private removedIndex: any;
    private highlighted: any;
    private forceSendSelectedIndex: any;
    private cleared: any;
    private addedTag: boolean;
    private lastCursorPosition: any;
    private blurEvent: any;
    private blurEventWithoutChange: boolean;
    private readonly emptyValue: any;
    private readonly onSpecialKey: any;
    private readonly onDblSpecialKey: any;
    private onEmptyValue: any;
    private readonly multiselect: boolean;
    private readonly freeTyping: boolean;
    private tagslist: Array<string> = [];
    private tagsInputData: any;
    private multiselectRawValue: any;
    private multiselectOldValue: any;
    private changeToFired: boolean;

    private onInputTimer: any;
    private openOnFocus: boolean = true;
    private readonly onInputTimeout: number;

    private cursorPositionOnLastSpecialKey: any;

    protected isTableMode: boolean;

    //Is focus on dropdown element. For IE11 click on srcoll problem.
    private autocompleterFocus:boolean = false;

    constructor(componentObj: any, parent: HTMLFormComponent) {
        super(componentObj, parent);

        this.values = this.componentObj.filteredValues;
        this.onInputTimeout = this.componentObj.onInputTimeout ? this.componentObj.onInputTimeout : null;
        this.input = null;
        this.autocompleter = null;
        this.selectedIndexGroup = null;
        this.selectedIndex = null;
        this.forceSendSelectedIndex = false;
        this.highlighted = null;
        this.cleared = false;
        this.lastCursorPosition = this.componentObj.cursor;
        this.blurEvent = false;
        this.openOnFocus = typeof this.componentObj.openOnFocus === 'undefined' ? true : this.componentObj.openOnFocus;
        this.emptyValue = this.componentObj.emptyValue;
        this.onSpecialKey = this.componentObj.onSpecialKey;
        this.onDblSpecialKey = this.componentObj.onDblSpecialKey;
        this.onEmptyValue = this.componentObj.onEmptyValue;
        this.freeTyping = this.componentObj.freeTyping;
        this.multiselect = this.componentObj.multiselect;
        this.multiselectRawValue = this.componentObj.multiselectRawValue;
        this.isTableMode = this.componentObj.isTableMode;
    }

    private createInput(): HTMLInputElement | HTMLSpanElement {
        if(!!this.isTableMode) {
            const input = document.createElement('span');
            input.textContent = this.rawValue;
            return input;
        }
        const input = document.createElement('input');
        input.type = 'text';
        input.value = this.rawValue;
        input.autocomplete = 'off';
        if (this.placeholder) {
            input.placeholder = this.placeholder;
        }
        return input;
    }

    create() {
        let input = this.createInput();
        this.input = input;
        input.id = this.id;
        ['fc', 'form-control'].forEach(function (cssClass) {
            input.classList.add(cssClass);
        });

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
                this.changeToFired = false
            }
        }.bind(this));
        $(input).on('keydown', function (event) {

            if (this.accessibility !== 'EDIT') {
                return;
            }
            let keyCode = event.which;
            let options = this.autocompleter.querySelectorAll('li:not(.dropdown-header)');
            if (keyCode === 9 || keyCode === 13) {
                let shouldBlur = true;
                if (this.highlighted != null) {
                    let element = options[this.highlighted].firstChild;
                    this.selectedIndexGroup = element.dataset.group;
                    this.selectedIndex = parseInt(element.dataset.index);
                    this.forceSendSelectedIndex = true;
                    const val = this.values[this.selectedIndexGroup][this.selectedIndex];
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
            } else {
                let move = 0;
                switch (keyCode) {
                    case 40: // down arrow
                        move = 1;
                        break;
                    case 38: // up arrow
                        move = -1;
                        break;
                }
                if ((keyCode === 40 || keyCode === 38) && !this.autocompleter.classList.contains('isEmpty')) {
                    let current = options[this.highlighted];
                    if (current) {
                        current.classList.remove('selected');
                    }
                    if (this.highlighted === null) {
                        if (move === 1) {
                            this.highlighted = 0;
                        } else {
                            this.highlighted = options.length - 1;
                        }
                    } else {
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
        let specialKeyCapture = function (fireEvent, event) {
            if (event.ctrlKey && event.which === 32 && this.accessibility === 'EDIT') {
                event.stopPropagation();
                event.preventDefault();

                let doubleSepcialKey = this.onDblSpecialKey && this.input && this.input.value == this.rawValueOnLatSpecialKey && this.input.selectionStart == this.cursorPositionOnLastSpecialKey;
                if (fireEvent) {
                    if (!doubleSepcialKey) {
                        this.openAutocomplete();
                        this.rawValueOnLatSpecialKey = this.input.value;
                        this.cursorPositionOnLastSpecialKey = this.input.selectionStart;
                        if (this.onSpecialKey) {
                            this.fireEventWithLock('onSpecialKey', this.onSpecialKey);
                        }
                    } else if (doubleSepcialKey) {
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
            if(!this.autocompleterFocus) {
                this.closeAutocomplete();

                if (this.multiselect) {
                    this.addTag(this.input.value);
                }
            } else {
                //IE11 Put back focus on input.
                // this.input.focus();
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


        let autocompleter = document.createElement('ul');
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
        if(this.isLastValueEnabled!==false) {
            this.createLastValueElement();
        }

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

    protected innerWrap() {
        if (this.multiselect) {
            return this.prepareTagsInput();
        }

        return this.input;
    }

    defineDefinitionSymbols(): void {
        super.defineDefinitionSymbols();
    }

    update(change) {
        super.update(change);

        if (change.changedAttributes) {
            $.each(change.changedAttributes, function (name, newValue) {
                switch (name) {
                    case 'rawValue':
                        this.input.value = newValue;
                        this.rawValue = newValue;
                        this.oldValue = newValue;
                        this.toogleLastValueElement(this.rawValue == this.lastValue);
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

    updateModel() {

        this.rawValue = this.input.value;
        if (this.multiselect) {
            this.multiselectRawValue = JSON.stringify(this.tagslist);
        }

        let theSameValue = this.rawValue == this.lastValue;
        if(this.lastValue===undefined && !!this.newValueText) {
            theSameValue = this.rawValue === '';
        }
        this.toogleLastValueElement(theSameValue);
    };

    openAutocomplete() {
        let formType = this.getFormType();
        let componentsContainer = $(this.component).closest('.card-body');
        let containerHeightAfterListDisplay;
        let containerHeightBeforeListDisplay;
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
        let bounding = this.autocompleter.getBoundingClientRect();
        let rightOverlap = bounding.right - (window.innerWidth || document.documentElement.clientWidth);
        let bottomOverlap = bounding.bottom - (window.innerHeight || document.documentElement.clientHeight);

        if (rightOverlap > -17) {
            this.autocompleter.style.setProperty('right', '0px', "important");
            this.autocompleter.style.setProperty('left', 'auto', "important");
        }

        //IE11 Set true for autocompleter actions. We need to prevent focusout on input then.
        this.autocompleter.addEventListener('mousedown', function (event) {
            this.autocompleterFocus = true;
        }.bind(this));

        let parent = null;

        if (formType === 'STANDARD') {
            parent = $(this.component).closest('.panel,.splitContainer,.hasHeight');

            //If autocompleter is about to open in container with fixed height we change it's open direction. Direction will be UP.
            if(parent.hasClass('hasHeight')){
                const parentBound = parent[0].getBoundingClientRect();
                let completerYmaks = bounding.height + bounding.top;
                let parentYmaks = parentBound.top + parentBound.height;
                //Put it as sibling of parent becouse parent has height and elements inside it wont overflow it. Close it when parent begins to scroll.
                if(completerYmaks > parentYmaks){
                    this.handleContainerOverflow(parent.parent(),  this.autocompleter, true);
                } else {
                    this.handleContainerOverflow(parent.parent(),  this.autocompleter);
                }

                parent.on("scroll", this.closeAutocomplete.bind(this));
            } else if(bottomOverlap > 20){
                this.inputGroupElement.classList.add("dropup");
            }
            if (!parent.hasClass('floating') && !parent.hasClass('splitContainer') ) {
                return;
            }
        } else if (formType === 'MODAL' || formType === 'MODAL_OVERFLOW') {
            parent = $(this.component).closest('.modal-content');
            this.handleContainerOverflow(parent, this.autocompleter);
        } else {
            console.error('Parent not defined.');
            return;
        }
    };

    createClearButton() {
        if (this.emptyValue && this.emptyValue === true) {
            // combo.classList.add('input-group');
            let button = document.createElement('div');
            button.classList.add('input-group-append');
            button.classList.add('clearButton');

            let buttonSpan = document.createElement('span');
            buttonSpan.classList.add('input-group-text');

            let icon = document.createElement('i');
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
                        this.changeToFired = false
                    }
                    if (this.onEmptyValue) {
                        if (this._formId === 'FormPreview') {
                            this.fireEvent('onEmptyValue', this.onEmptyValue);
                        } else {
                            this.fireEventWithLock('onEmptyValue', this.onEmptyValue, event);
                        }
                    }
                }
            }.bind(this));

            buttonSpan.appendChild(icon);
            button.appendChild(buttonSpan);
            if (this.multiselect) {
                this.component.parentNode.parentNode.appendChild(button);
            } else {
                this.component.parentNode.appendChild(button);
            }
        }
    }

    closeAutocomplete() {
        let formType = this.getFormType();

        this.autocompleter.classList.remove('show');
        this.autocompleter.classList.remove('dropup');

        /**
         * Clear inline styles for right direction view.
         */
        this.autocompleter.style.setProperty('right', '', null);
        this.autocompleter.style.setProperty('left', '', null);
        this.autocompleter.style.setProperty('top', '', null);

        let parent = null;
        if (formType === 'STANDARD') {
            parent = $(this.component).closest('.panel');

            if (!this.inputGroupElement.contains(this.autocompleter)) {
                this.inputGroupElement.appendChild(this.autocompleter);
            }

            if (!parent.hasClass('floating')) {
                return;
            }
        } else if (formType === 'MODAL' || formType === 'MODAL_OVERFLOW') {
            if (!this.inputGroupElement.contains(this.autocompleter)) {
                this.inputGroupElement.appendChild(this.autocompleter);
            }
        }
    };

    extractChangedAttributes() {

        if (this.designMode === true) {
            return this.changesQueue.extractChangedAttributes();
        }
        if (this.accessibility !== 'EDIT') {
            return {};
        }
        let attrs = {
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
            } else if (this.removedIndex != null) {
                attrs.removedIndex = this.removedIndex;
            } else {
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

    setValues(values) {
        while (this.autocompleter.firstChild) {
            this.autocompleter.removeChild(this.autocompleter.firstChild);
        }
        this.values = values;

        if (!values || Object.keys(values).length === 0) {
            this.autocompleter.classList.add('isEmpty');
            return;
        }
        let index = 0;

        this.autocompleter.classList.remove('isEmpty');
        Object.keys(values).forEach(function (key) {
            let heading = document.createElement('li');
            heading.classList.add('dropdown-header');
            heading.onclick = () => {
                this.closeAutocomplete();
                this.input.blur();
               return false;
            };
            heading.appendChild(document.createTextNode(key));
            this.autocompleter.appendChild(heading);

            for (let i = 0, len = values[key].length; i < len; i++) {
                let itemValue = values[key][i];
                let li = document.createElement('li');

                let a = document.createElement('a');
                a.classList.add('dropdown-item');
                a.style.pointerEvents = 'auto';
                let displayValue;
                if (this.formId === 'designerProperties') {
                    displayValue = itemValue.displayAsTarget ? itemValue.targetValue : itemValue.displayedValueWithoutExtras;
                } else {
                    displayValue = itemValue.displayAsTarget ? itemValue.targetValue : itemValue.displayedValue;
                }
                let disabled = false;
                if (this.multiselect && this.tagslist.indexOf(displayValue) >= 0) {
                    a.classList.add('disabled');
                    a.href = "#";
                    a.onclick = () => {
                        this.closeAutocomplete();
                        this.input.blur();
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
                displayValue = this.resolveValue(displayValue);
                a.innerHTML = displayValue;
                a.addEventListener('mousedown', function (itemValue, indexGroup, index, targetValue, targetCursorPosition, event) {
                    event.preventDefault();

                    if (this.accessibility !== 'EDIT' || disabled) {
                        return;
                    }

                    let shouldBlur = true;
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
                        this.changeToFired = false
                    }
                    if (shouldBlur) {
                        this.blurEventWithoutChange = true;
                        this.input.blur(); // must be after onChange
                    }
                }.bind(this, itemValue, key, i, itemValue.targetValue, itemValue.targetCursorPosition));

                li.appendChild(a);
                this.autocompleter.appendChild(li);
                index = index + 1;
            }
        }.bind(this));
    };

    resolveValue(value) {
        value = this.fhml.resolveValueTextOrEmpty(value);
        return value;
    };

    setCursorPositionToInput(caretPos) {
        if (typeof this.input.selectionStart !== 'undefined' && this.input.setSelectionRange) {
            try {
                this.input.focus();
                this.input.setSelectionRange(caretPos, caretPos);
            } catch (ignored) {
                // ignore IE 11 throwing error for non-displayed input
            }
        } else if (this.input.createTextRange) {
            let range = this.input.createTextRange();
            range.move('character', caretPos);
            range.select();
        }
    };

    wrap(skipLabel, isInputElement) {
        super.wrap(skipLabel, isInputElement);
    }

    protected prepareTagsInput() {
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
            onRemoveTag: (x, value) => {
                this.selectedIndex = null;
                this.input.value = '';
                this.closeAutocomplete();
                this.setValues(this.values);
                this.updateModel();
                this.fireEventWithLock('onChange', this.onChange);
                this.changeToFired = false
            },
            onAddTag: (x, value) => {
                this.selectedIndex = null;
                this.input.value = '';
                this.setValues(this.values);
                this.addedTag = true;
                this.updateModel();
                this.fireEventWithLock('onChange', this.onChange);
                this.changeToFired = false
            }
        });

        var id = this.id;

        this.tagsInputData = jQuery.extend({
            pid: id,
            real_input: '#' + id,
            holder: '#' + id + '_tagsinput',
            input_wrapper: '#' + id + '_addTag',
        }, settings);

        var markup = $('<div>', {class: 'rawinput', id: id + '_addTag'}).append($(this.input));
        $(this.tagsInputData.holder).css('width', settings.width);
        $(this.tagsInputData.holder).css('min-height', settings.height);
        $(this.tagsInputData.holder).css('height', settings.height);

        return $(markup)[0];
    }

    addTag(value: string, options) {
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
                $('<span>', {class: 'tag btn btn-outline-secondary'}).append(
                    $('<span>', {class: 'tag-text'}).text(value),
                    $('<button>', {class: 'tag-remove'}).click(() => this.removeTag(encodeURI(value), {}))
                ).insertBefore('#' + id);

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
            this.changeToFired = false
        }
    }

    removeTag(value: string, options) {
        options = jQuery.extend({
            focus: false,
            callback: true
        }, options);

        value = decodeURI(value);

        this.clearTagsInput();

        this.removedIndex = this.tagslist.indexOf(value);
        this.selectedIndex = null;
        this.selectedIndexGroup = "";
        var reAddTags = Object.assign([], this.tagslist.filter(obj => obj !== value));
        this.tagslist = [];
        this.importTags(reAddTags);

        if (options.callback) {
            this.selectedIndex = null;
            this.input.value = '';
            this.closeAutocomplete();
            this.setValues(this.values);
            this.updateModel();
            this.fireEventWithLock('onChange', this.onChange);
            this.changeToFired = false
        }
    }

    clearTagsInput() {
        var id = this.id;

        $('#' + id + '_tagsinput .tag').remove();
    }

    importTags(tagslist: Array<string>) {
        tagslist.forEach(value => {
            this.addTag(value, {callback: false, focus: false, noCheck: true});
        });
    }

    protected getMainComponent() {
        if (this.multiselect) {
            return this.component.parentNode;
        }

        return this.component;
    }

    setAccessibility(accessibility) {
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
                    let tags = this.htmlElement.querySelectorAll(".tag-remove");
                    if (tags.length > 0) {
                        tags.forEach((node) => this.accessibilityResolve(node, 'EDIT'));
                    }
                }
                break;
            case 'VIEW':
                this.accessibilityResolve(this.component, 'VIEW');
                if (this.multiselect) {
                    let tags = this.htmlElement.querySelectorAll(".tag-remove");
                    if (tags.length > 0) {
                        tags.forEach((node) => this.accessibilityResolve(node, 'VIEW'));
                    }
                }
                break;
            case 'HIDDEN':
                if (this.invisible) {
                    this.htmlElement.classList.add('invisible');
                } else {
                    this.htmlElement.classList.add('d-none');
                }
                break;
            case 'DEFECTED':
                this.accessibilityResolve(this.component, 'DEFECTED');
                this.component.title = 'Broken control';
                break;
        }

        this.accessibility = accessibility;
    }

    private containsValue(value: string) {
        if (this.values) {
            for (let key of Object.keys(this.values)) {
                for (let element of this.values[key]) {
                    if (element.targetValue === value) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    onInputEvent() {
        if (this.onInput) {
            if (this.onInputTimeout) {
                clearTimeout(this.onInputTimer);
                this.onInputTimer = setTimeout(function () {
                    this.fireEvent('onInput', this.onInput);
                }.bind(this), this.onInputTimeout);
            } else {
                this.fireEvent('onInput', this.onInput);
            }
        }
    }

    getDefaultWidth(): string {
        return super.getDefaultWidth();
    }
}

export {ComboFhDP}
