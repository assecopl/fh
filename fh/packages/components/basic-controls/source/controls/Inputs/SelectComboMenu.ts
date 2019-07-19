import {InputText} from "./InputText";
import {HTMLFormComponent} from 'fh-forms-handler';

class SelectComboMenu extends InputText {
    protected values: any;
    protected highlights: any;
    protected autocompleter: any;
    protected selectedIndex: any;
    private highlighted: any;
    private blurEvent: any;
    private blurEventWithoutChange: boolean = false;
    private readonly onSpecialKey: any;
    private readonly onDblSpecialKey: any;
    private readonly freeTyping: boolean;
    private openButton: HTMLDivElement;
    private changeToFired: boolean;
    private rawValueOnLatSpecialKey: any;
    private autocompleteOpen: boolean;
    private blurredAutocompleteScrollbard: boolean;
    private emptyLabel: boolean;
    private emptyLabelText: string;

    constructor(componentObj: any, parent: HTMLFormComponent) {
        super(componentObj, parent);

        this.values = this.componentObj.filteredValues || [];
        this.highlights = this.componentObj.highlightedValues;
        this.input = null;
        this.autocompleter = null;
        this.selectedIndex = null;
        this.highlighted = null;
        this.blurEvent = false;
        this.autocompleteOpen = false;

        this.onSpecialKey = this.componentObj.onSpecialKey;
        this.onDblSpecialKey = this.componentObj.onDblSpecialKey;
        this.freeTyping = this.componentObj.freeTyping;

        this.emptyLabel = this.componentObj.emptyLabel || false;
        this.emptyLabelText = this.componentObj.emptyLabelText || '';
    }

    create() {
        let input = document.createElement('input');
        this.input = input;
        input.id = this.id;
        input.type = 'text';
        if (this.emptyLabel && this.rawValue === "") {
            input.value = this.emptyLabelText;
            this.rawValue = this.emptyLabelText;
            this.oldValue = this.emptyLabelText;
        } else {
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
            input.addEventListener('input', this.inputInputEvent2.bind(this));
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

        let autocompleter = document.createElement('ul');
        ['autocompleter', 'dropdown-menu', 'fc', 'selectcombo'].forEach(function (cssClass) {
            autocompleter.classList.add(cssClass);
        });
        autocompleter.id = this.id + '_autocompleter';

        this.autocompleter = autocompleter;
        this.component = this.input;
        this.focusableComponent = input;

        this.wrap(false, true);
        this.hintElement = this.inputGroupElement;
        this.inputGroupElement.appendChild(autocompleter);

        if (this.fh.isIE()) {
            $(this.autocompleter).on('mousedown', this.autocompleteMousedownEvent.bind(this));
            $(this.autocompleter).on('scroll', this.autocompelteScrollEvent.bind(this));
        }

        this.createOpenButton();
        this.setRequiredField(false);

        this.addStyles();
        this.display();

        if (this.component.classList.contains('servicesListControl')) {
            this.htmlElement.classList.add('servicesListControlWrapper');
        }

        this.setValues(this.values);
        this.highlightValue(this.highlights);
    };

    clickedOnAutocompleterScrollbar(mouseX) {
        return $(this.autocompleter).outerWidth() <= mouseX;

    }

    autocompelteScrollEvent(e) {
        this.input.focus();
    }

    autocompleteMousedownEvent(e) {
        e.preventDefault();
        e.stopImmediatePropagation();
        if (this.clickedOnAutocompleterScrollbar(e.clientX)) {
            this.blurredAutocompleteScrollbard = true;
        } else {
            this.blurredAutocompleteScrollbard = false;
            this.closeAutocomplete();
        }
        this.input.select();
        this.input.focus();
    }

    // noinspection JSUnusedGlobalSymbols
    protected innerWrap() {
        return this.input;
    }

    defineDefinitionSymbols(): void {
        super.defineDefinitionSymbols();
    }

    createOpenButton() {
        let button = document.createElement('div');
        button.classList.add('input-group-append');
        button.classList.add('openButton');

        let buttonSpan = document.createElement('span');
        buttonSpan.classList.add('input-group-text');

        let icon = document.createElement('i');
        icon.classList.add('fa');
        icon.classList.add('fa-sort-down');

        button.addEventListener('mousedown', function (e) {
            e.preventDefault();
            if (this.autocompleteOpen) {
                this.closeAutocomplete();
            } else {
                this.openAutocomplete();
            }
            this.input.select();
            this.input.focus();
        }.bind(this));

        buttonSpan.appendChild(icon);
        button.appendChild(buttonSpan);
        this.openButton = button;
        this.component.parentNode.appendChild(button);
    }

    inputPasteEvent(event) {
        event.stopPropagation();
        if (this.accessibility !== 'EDIT') {
            return;
        }
        this.updateModel();
        if (this.onChange && (this.rawValue !== this.oldValue)) {
            this.fireEventWithLock('onChange', this.onChange);
            this.changeToFired = false
        }
    }

    inputInputEvent() {
        this.selectedIndex = null;
        this.updateModel();
    }

    inputKeydownEvent(event) {
        if (this.accessibility !== 'EDIT') {
            return;
        }

        let keyCode = event.which;
        let options = this.autocompleter.querySelectorAll('li:not(.dropdown-header)');
        if (keyCode === 9 || keyCode === 13) {
            let shouldBlur = true;
            if (this.highlighted != null) {
                let element = options[this.highlighted].firstChild;
                this.selectedIndex = parseInt(element.dataset.index);
                if (this.emptyLabel) {
                    this.selectedIndex = this.selectedIndex - 1;
                }

                this.input.value = element.dataset.targetValue;
            }
            this.updateModel();
            if (this.onChange && (this.rawValue !== this.oldValue || this.changeToFired)) {
                this.fireEventWithLock('onChange', this.onChange);
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
            this.closeAutocomplete();
        } else if (keyCode == 32 && !this.autocompleteOpen) {
            if (!this.freeTyping) {
                event.preventDefault();
            }
            this.openAutocomplete();
        } else if (keyCode == 27) {
            if (this.selectedIndex == null && this.emptyLabel) {
                this.highlightValue([this.values[0]]);
            } else {
                this.highlightValue([this.values[this.selectedIndex]]);
            }
            this.closeAutocomplete();
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
            if ((keyCode === 40 || keyCode === 38) && !this.autocompleter.classList.contains(
                'isEmpty')) {
                // top arrow , down arrow

                if ((move < 0 && this.highlighted === 0) || (this.highlighted + move) >= options.length) return;

                if (this.autocompleteOpen) {
                    let current = options[this.highlighted];
                    if (current) {
                        current.classList.remove('selected');
                    }

                    if (this.highlighted === null) {
                        if (move === 1) {
                            this.highlighted = 0;
                        }
                    } else {
                        this.highlighted = this.highlighted + move;
                    }

                    if (this.highlighted != null) {
                        options[this.highlighted].classList.add('selected');
                        this.autocompleter.scrollTop = options[this.highlighted].offsetTop;
                    }
                } else {
                    if (this.highlighted === null) {
                        if (move === 1) {
                            this.highlighted = 1;
                        }
                    } else {
                        this.highlighted = this.highlighted + move;
                    }

                    let current = this.values[this.highlighted];
                    if (current) {
                        this.input.value = current.targetValue;
                        this.selectedIndex = this.highlighted - (this.emptyLabel ? 1 : 0);
                        this.updateModel();
                        if (this.onChange && (this.rawValue !== this.oldValue || this.changeToFired)) {
                            this.fireEventWithLock('onChange', this.onChange);
                            this.changeToFired = false;
                        }
                    }
                }
            } else if (keyCode == 37 || keyCode == 39) {
                // left arrow , right arrow
                return;
            } else if (keyCode == 36 || keyCode == 33) {
                // home, pg up
                if (options.length === 0) return;

                let current = options[this.highlighted];
                if (current) {
                    current.classList.remove('selected');
                }

                this.highlighted = 0;
                options[this.highlighted].classList.add('selected');
                this.autocompleter.scrollTop = options[this.highlighted].offsetTop;
                return;
            } else if (keyCode == 35 || keyCode == 34) {
                event.preventDefault();

                // end, pg down
                if (options.length === 0) return;

                let current = options[this.highlighted];
                if (current) {
                    current.classList.remove('selected');
                }

                this.highlighted = options.length - 1;
                options[this.highlighted].classList.add('selected');
                this.autocompleter.scrollTop = options[this.highlighted].offsetTop;
                return;
            } else {
                this.openAutocomplete();
            }
        }
    }

    inputInputEvent2() {
        if (this.accessibility === 'EDIT') {
            this.fireEvent('onInput', this.onInput);
        }
    }

    specialKeyCapture(fireEvent, event) {
        if (event.ctrlKey && event.which === 32 && this.accessibility === 'EDIT') {
            event.stopPropagation();
            event.preventDefault();
            let doubleSepcialKey = this.onDblSpecialKey && this.input && this.input.value == this.rawValueOnLatSpecialKey;
            if (fireEvent) {
                if (!doubleSepcialKey) {
                    this.rawValueOnLatSpecialKey = this.input.value;
                    if (this.onSpecialKey) {
                        this.fireEventWithLock('onSpecialKey', this.onSpecialKey);
                    }
                } else if (doubleSepcialKey) {
                    this.rawValueOnLatSpecialKey = null;
                    if (this.onDblSpecialKey) {
                        this.changeToFired = false;
                        this.fireEventWithLock('onDblSpecialKey', this.onDblSpecialKey);
                    }
                }
            }
        }
    }

    inputBlurEvent(e) {
        if (this.blurredAutocompleteScrollbard) {
            e.preventDefault();
            e.stopImmediatePropagation();
            this.blurredAutocompleteScrollbard = false;
            return;
        }
        this.closeAutocomplete();

        if (this.emptyLabel && this.rawValue == null) {
            this.input.value = this.emptyLabelText;
            this.selectedIndex = -1;
            this.updateModel();
            this.changeToFired = true;
        }
    }

    inputBlurEvent2() {
        if (this.accessibility === 'EDIT' && !this.blurEventWithoutChange && (this.changeToFired || this.rawValue !== this.oldValue)) {
            this.blurEvent = true;
            this.fireEventWithLock('onChange', this.onChange);
            this.changeToFired = false;
        }
        this.blurEventWithoutChange = false;
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
                        break;
                    case 'filteredValues':
                        this.highlighted = null;
                        this.setValues(newValue);
                        break;
                    case 'highlightedValues':
                        this.highlighted = null;
                        this.highlightValue(newValue);
                        break;
                }
            }.bind(this));
        }
    }

    updateModel() {
        this.rawValue = this.input.value;
        if (this.rawValue === "") {
            this.rawValue = null;
        }
    }

    openAutocomplete() {
        if (this.autocompleteOpen || this.accessibility !== 'EDIT') return;
        this.autocompleteOpen = true;

        if (this.values.length > 0 && this.selectedIndex == null) {
            this.highlightValue([this.values[0]]);
        }

        let formType = this.getFormType();

        if ($(this.autocompleter).outerWidth() < $(this.component).outerWidth()) {
            $(this.autocompleter).css('width', $(this.component).outerWidth());
        }
        this.autocompleter.classList.add('show');

        let bounding = this.autocompleter.getBoundingClientRect();
        let rightOverlap = bounding.right - (window.innerWidth || document.documentElement.clientWidth);
        if (rightOverlap > 0) {
            const scrollbarWidth = 17;
            $(this.autocompleter).css('left', (-1 * rightOverlap) - scrollbarWidth);
        }
        let parent = null;

        if (formType === 'STANDARD') {
            parent = $(this.component).closest('.panel,.splitContainer');
            if (!parent.hasClass('floating') && !parent.hasClass('splitContainer')) {
                return;
            }
        } else if (formType === 'MODAL' || formType === 'MODAL_OVERFLOW') {
            parent = $(this.component).closest('.modal-content');
        } else {
            console.error('Parent not defined.');
            return;
        }

        parent.append(this.autocompleter);
        let _component = $(this.component);
        let _autocompleter = $(this.autocompleter);

        _autocompleter.css('top', _component.offset().top - parent.offset().top + _component.height());
        _autocompleter.css('left', _component.offset().left - parent.offset().left);
        _autocompleter.css('width', _component.width());
    }

    closeAutocomplete() {
        if (!this.autocompleteOpen) return;
        this.autocompleteOpen = false;

        let formType = this.getFormType();

        this.autocompleter.classList.remove('show');

        let parent = null;
        if (formType === 'STANDARD') {
            parent = $(this.component).closest('.panel');

            if (!parent.hasClass('floating')) {
                return;
            }
        } else if (formType === 'MODAL' || formType === 'MODAL_OVERFLOW') {
            parent = $(this.component).closest('.modal-content');
        } else {
            console.error('Parent not defined.');
            return;
        }

        parent.find('.autocompleter').remove();
        this.component.appendChild(this.autocompleter);
        this.input.focus();
    }

    extractChangedAttributes() {
        if (this.designMode === true) {
            return this.changesQueue.extractChangedAttributes();
        }
        if (this.accessibility !== 'EDIT') {
            return {};
        }
        let attrs = {
            blur: undefined,
            text: undefined,
            addedTag: undefined,
            selectedIndexGroup: undefined,
            selectedIndex: undefined,
        };
        if (this.rawValue !== this.oldValue) {
            if (this.selectedIndex != null) {
                attrs.selectedIndex = this.selectedIndex;
            } else {
                attrs.text = this.rawValue;
            }

            this.oldValue = this.rawValue;
            this.selectedIndex = null;
        }
        if (this.blurEvent) {
            attrs.blur = true;
            this.blurEvent = false;
        }

        return attrs;
    }

    setValues(values) {
        while (this.autocompleter.firstChild) {
            this.autocompleter.removeChild(this.autocompleter.firstChild);
        }

        if (this.emptyLabel) {
            values.unshift({
                displayAsTarget: true,
                targetId: -1,
                targetValue: this.emptyLabelText
            });
        }

        this.values = values;

        if (!values || values.length === 0) {
            this.autocompleter.classList.add('isEmpty');
            return;
        }
        let index = 0;

        this.autocompleter.classList.remove('isEmpty');

        for (let i = 0, len = values.length; i < len; i++) {
            let itemValue = values[i];
            let li = document.createElement('li');
            itemValue.element = li;

            let a = document.createElement('a');
            a.classList.add('dropdown-item');
            let displayValue = itemValue.displayAsTarget ? itemValue.targetValue : itemValue.displayedValue;

            let disabled = false;

            a.dataset.index = i.toString();
            a.dataset.findex = index.toString();
            a.dataset.targetValue = itemValue.targetValue;

            // escape and parse FHML
            displayValue = this.resolveValue(displayValue);
            a.innerHTML = displayValue;
            a.addEventListener('mousedown', function (index, targetValue, targetId, event) {
                event.preventDefault();

                if (this.accessibility !== 'EDIT' || disabled) {
                    return;
                }

                this.selectedIndex = index;
                this.highlightValue([this.values[this.selectedIndex]]);
                if (this.emptyLabel) {
                    this.selectedIndex = this.selectedIndex - 1;
                }

                this.input.value = targetValue;

                this.updateModel();
                if (this.onChange && (this.rawValue !== this.oldValue)) {
                    this.fireEventWithLock('onChange', this.onChange, event);
                    this.changeToFired = false;
                }

                this.closeAutocomplete();
                this.input.select();
            }.bind(this, i, itemValue.targetValue, itemValue.targetId));

            li.appendChild(a);
            this.autocompleter.appendChild(li);
            index = index + 1;
        }
    }

    resolveValue(value) {
        value = this.fhml.resolveValueTextOrEmpty(value);
        return value;
    }

    highlightValue(highlights) {
        if (!highlights) {
            highlights = [];
        }

        this.selectedIndex = null;

        let options = this.values;
        if (highlights.length == 0) {
            for (let optionIndex = 0; optionIndex < options.length; optionIndex++) {
                let option = options[optionIndex];
                option.element.classList.remove('selected');
            }
            return;
        }

        let highlight = highlights[0];

        for (let optionIndex = 0; optionIndex < options.length; optionIndex++) {
            let option = options[optionIndex];

            option.element.classList.remove('selected');

            if (option.targetValue === highlight.targetValue) {
                this.selectedIndex = optionIndex;
                this.highlighted = optionIndex;

                option.element.classList.add('selected');
                this.autocompleter.scrollTop = option.element.offsetTop;
            }
        }
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
            this.openButton.classList.remove('fc-editable');
        }

        switch (accessibility) {
            case 'EDIT':
                this.accessibilityResolve(this.component, 'EDIT');
                this.autocompleter.classList.add('fc-editable');
                this.openButton.classList.add('fc-editable');
                break;
            case 'VIEW':
                this.accessibilityResolve(this.component, 'VIEW');
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

    destroy(removeFromParent) {
        if (this.keySupportCallback) {
            this.keySupportCallback();
        }

        if (this.fh.isIE()) {
            $(this.autocompleter).off('mousedown', this.autocompleteMousedownEvent.bind(this));
            $(this.autocompleter).off('scroll', this.autocompelteScrollEvent.bind(this));
        }

        this.input.removeEventListener('input', this.inputInputEvent.bind(this));
        $(this.input).off('paste', this.inputPasteEvent.bind(this));
        $(this.input).off('keydown', this.inputKeydownEvent.bind(this));

        if (this.onInput) {
            this.input.removeEventListener('input', this.inputInputEvent2.bind(this));
        }
        if (this.onSpecialKey || this.onDblSpecialKey) {
            this.input.removeEventListener('keyup', this.specialKeyCapture.bind(this, true));
            this.input.removeEventListener('keydown', this.specialKeyCapture.bind(this, false));
            this.input.removeEventListener('input', this.specialKeyCapture.bind(this, false));
        }

        $(this.input).off('blur', this.inputBlurEvent.bind(this));
        if (this.onChange) {
            $(this.input).off('blur', this.inputBlurEvent2.bind(this));
        }

        super.destroy(removeFromParent);
    }
}

export {
    SelectComboMenu
}
