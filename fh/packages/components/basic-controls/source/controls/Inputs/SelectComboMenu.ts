import {InputText} from "./InputText";
import {HTMLFormComponent} from 'fh-forms-handler';
import * as _ from "lodash";

class SelectComboMenu extends InputText {
    protected values: any;
    protected autocompleter: any;
    protected selectedIndex: any;
    protected highlighted: any;
    protected blurEvent: any;
    protected blurEventWithoutChange: boolean = false;
    protected onSpecialKey: any;
    protected onDblSpecialKey: any;
    protected freeTyping: boolean;
    protected openButton: HTMLDivElement;
    protected changeToFired: boolean;
    protected rawValueOnLatSpecialKey: any;
    protected autocompleteOpen: boolean;
    protected emptyLabel: boolean;
    protected emptyLabelText: string;

    constructor(componentObj: any, parent: HTMLFormComponent) {
        super(componentObj, parent);

        this.values = this.componentObj.filteredValues || [];
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
        if (this.rawValue === "") {
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

        input.addEventListener('input',  this.inputInputEvent.bind(this));
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

        let autocompleter = document.createElement('ul');
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
        this.autocompleter.addEventListener("mousedown", function(event) {
            event.preventDefault();
            event.stopPropagation();
        })
        this.setValues(this.values);
        this.component = this.input;
        this.focusableComponent = input;

        this.wrap(false, true);
        this.hintElement = this.inputGroupElement;
        this.inputGroupElement.appendChild(autocompleter);

        this.createOpenButton();
        this.setRequiredField(false);

        this.addStyles();
        this.display();

        if (this.component.classList.contains('servicesListControl')) {
            this.htmlElement.classList.add('servicesListControlWrapper');
        }


        if (this.componentObj.highlightedValue) {
            this.highlighted = this.findByValue(this.componentObj.highlightedValue.targetValue);
        }
        this.hightlightValue();
    };

    // noinspection JSUnusedGlobalSymbols
    protected innerWrap() {
        return this.input;
    }

    protected getInputDebounceTime() {
        return 200;
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
            // noinspection JSPotentiallyInvalidUsageOfClassThis
            if (this.autocompleteOpen) {
                // noinspection JSPotentiallyInvalidUsageOfClassThis
                this.closeAutocomplete();
            } else {
                // noinspection JSPotentiallyInvalidUsageOfClassThis
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
        // tab or enter
        if (keyCode === 9 || keyCode === 13) {
            let shouldBlur = true;
            if (this.highlighted != null) {
                let element = this.highlighted.element.firstChild;
                    this.selectedIndex = this.selectedIndex;
                this.input.value = this.highlighted.displayAsTarget ? this.highlighted.targetValue : (this.highlighted.displayedValue ? this.highlighted.displayedValue : "");
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
            // spacja
            if (!this.freeTyping) {
                event.preventDefault();
            }
            this.openAutocomplete();
        } else if (keyCode == 27) {
            // escape
            this.hightlightValue();
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

                let h = null;
                if (this.highlighted) {
                    h = parseInt(this.highlighted.element.firstChild.dataset.index);
                }
                if ((move < 0 && h === 0) || (h + move) >= options.length) return;

                if (this.autocompleteOpen) {
                    if (h === null && move === 1) {
                        h = 1;
                    } else {
                        h = h + move;
                    }

                    let next = this.findValueByElement(options[h]);

                    if (next) {
                        this.highlighted = next;
                        this.hightlightValue();
                    }
                } else {
                    if (h === null && move === 1) {
                        h = 1;
                    } else {
                        h = h + move;
                    }

                    let current = this.findValueByElement(options[h]);
                    if (current) {
                        this.input.value = current.displayAsTarget ? current.targetValue : (current.displayedValue ? current.displayedValue : "");
                        this.selectedIndex = h;
                        this.highlighted = current;
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
                event.preventDefault();
                // home, pg up
                if (options.length === 0) return;

                if (this.highlighted) {
                    this.highlighted.element.firstChild.classList.remove('selected');
                }

                this.highlighted = this.findValueByElement(options[0]);
                this.highlighted.element.classList.add('selected');
                this.autocompleter.scrollTop = this.highlighted.element.offsetTop;
                return;
            } else if (keyCode == 35 || keyCode == 34) {
                event.preventDefault();

                // end, pg down
                if (options.length === 0) return;

                if (this.highlighted) {
                    this.highlighted.element.firstChild.classList.remove('selected');
                }

                this.highlighted = this.findValueByElement(options[options.length - 1]);
                this.highlighted.element.classList.add('selected');
                this.autocompleter.scrollTop = this.highlighted.element.offsetTop;
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

    inputBlurEvent() {
        this.closeAutocomplete();


        if (this.highlighted != null) {
            let element = this.highlighted.element.firstChild;
            this.selectedIndex = this.selectedIndex;
            this.input.value = this.highlighted.displayAsTarget ? this.highlighted.targetValue : (this.highlighted.displayedValue ? this.highlighted.displayedValue : "");
            this.updateModel();
        } else {
            if (this.rawValue == null) {
                //if (this.emptyLabel) {
                this.input.value = this.emptyLabelText;
                this.selectedIndex = 1;
                this.updateModel();
                this.changeToFired = true;
            }
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

                        if (newValue) {
                            this.highlighted = this.findByValue(newValue);
                            if(!this.highlighted && this.freeTyping){
                                this.input.value = newValue;
                            } else {
                                this.input.value = this.highlighted.displayAsTarget ? this.highlighted.targetValue : (this.highlighted.displayedValue ? this.highlighted.displayedValue : "");
                            }
                            this.rawValue = newValue;
                            //Must be before change oldValue
                            this.oldValue = newValue;


                        } else {
                            if (this.emptyLabelText) {
                                this.input.value = this.emptyLabelText;
                                this.rawValue = this.emptyLabelText;
                                this.oldValue = this.emptyLabelText;
                                this.highlighted = this.findByValue("nullValue");
                            } else {
                                this.input.value = null;
                                this.rawValue = null;
                                this.oldValue = null;
                                this.highlighted = null;
                                this.highlighted = this.findByValue("nullValue");
                            }
                        }
                        this.hightlightValue();
                        break;
                    case 'filteredValues':
                        this.highlighted = null;
                        this.setValues(newValue);
                        break;
                    case 'highlightedValue':
                        if (newValue == null) {
                            this.highlighted = null;
                        } else {
                            this.highlighted = this.findByValue(newValue.targetValue);
                        }
                        this.hightlightValue();
                        break;
                    case 'fireChange':
                        //Event fired after model update from search text.
                        if (this.onChange && this.accessibility === 'EDIT') {
                            this.fireEventWithLock('onChange', this.onChange);
                        }
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

    hightlightValue() {
        for (let value of this.values) {
            if(value.element) {
                value.element.classList.remove('selected');
            }
        }

        if (this.highlighted == null) {
            return;
        }
        if(this.highlighted.element) {
            this.highlighted.element.classList.add('selected');
            this.autocompleter.scrollTop = this.highlighted.element.offsetTop;
        }
    }

    findByValue(value) {
        value = value || '';
        for (let option of this.values) {
            if (option.targetValue.trim() === value.trim()) {
                return option;
            }
        }
        //If not found we return first element as it represents null value.
        // return null if freeTyping option is enabled as element won't be on list.
        return this.freeTyping ? null : this.values[0];
    }

    findValueByElement(value) {
        for (let option of this.values) {
            if (option.element === value) {
                return option;
            }
        }

        return null;
    }

    openAutocomplete() {
        if (this.autocompleteOpen || this.accessibility !== 'EDIT') return;
        this.autocompleteOpen = true;
        this.autocompleter.classList.add('show');

        let formType = this.getFormType();
        let targetWidth = this.component.clientWidth + this.openButton.clientWidth;
        if ($(this.autocompleter).outerWidth() < targetWidth) {
            $(this.autocompleter).css('width', targetWidth);
        }

        this.hightlightValue();

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

        let parent: JQuery<any> = null;
        const sumHeight = bounding.height + this.inputGroupElement.offsetHeight + 30; //Height of autocompleter and input.

        if (formType === 'STANDARD') {
            parent = $(this.component).closest('.panel,.splitContainer,.hasHeight');


            //If autocompleter is about to open in container with fixed height we change it's open direction. Direction will be UP.
            if (parent.hasClass('hasHeight')) {
                const parentBound = parent[0].getBoundingClientRect();

                //Chcek if autocompleter i bigger ten parent fixed container so it will be open nex to it to prevent disapering.
                const biggerThenParent = (parentBound.height <= sumHeight);
                const completerYmaks = bounding.height + bounding.top;
                const parentYmaks = parentBound.top + parentBound.height;
                //Put it as sibling of parent becouse parent has height and elements inside it wont overflow it. Close it when parent begins to scroll.
                if (biggerThenParent) {
                    this.handleContainerOverflow($("body"), this.autocompleter, (completerYmaks > parentYmaks));
                    parent.on("scroll", this.closeAutocomplete.bind(this));
                } else {
                    (completerYmaks > parentYmaks) ? this.inputGroupElement.classList.add("dropup") : "";
                }
            } else if (bottomOverlap > 20) {
                this.inputGroupElement.classList.add("dropup");
            }
            if (!parent.hasClass('floating') && !parent.hasClass('splitContainer')) {
                return;
            }
        } else if (formType === 'MODAL' || formType === 'MODAL_OVERFLOW') {
            parent = $(this.component).closest('.modal-content');
            this.handleContainerOverflow(parent, this.autocompleter);
        } else {
            console.error('Parent not defined.');
            return;
        }
    }

    closeAutocomplete() {
        if (!this.autocompleteOpen) return;
        this.autocompleteOpen = false;

        let formType = this.getFormType();

        this.autocompleter.classList.remove('show');
        this.inputGroupElement.classList.remove("dropup");

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
        } else {
            console.error('Parent not defined.');
            return;
        }

        this.input.focus();
    }

    extractChangedAttributes() {
        if (this.designMode === true) {
            return this.changesQueue.extractChangedAttributes();
        }
        if (this.accessibility !== 'EDIT') {
            return {};
        }

        if (this.rawValue !== this.oldValue || this.rawValue == null) {
            this.oldValue = this.rawValue;

            if (this.selectedIndex != null) {
                return {selectedIndex: this.selectedIndex -1};
            } else {
                return {text: this.rawValue};
            }
        }

        return {};
    }

    setValues(values) {
        while (this.autocompleter.firstChild) {
            this.autocompleter.removeChild(this.autocompleter.firstChild);
        }

        this.values = values;

        if (!values || values.length === 0) {
            this.autocompleter.classList.add('isEmpty');
            return;
        }
        let index = 0;

        this.autocompleter.classList.remove('isEmpty');
            console.log("Values", values)
        for (let i = 0, len = values.length; i < len; i++) {
            let itemValue = values[i];
            let li = document.createElement('li');
            itemValue.element = li;

            let a = document.createElement('a');
            a.classList.add('dropdown-item');
            if(i==0){
                a.classList.add( this.emptyLabel ? 'dropdown-empty' : 'd-none');
            }

            let displayValue = itemValue.displayAsTarget ? itemValue.targetValue : (itemValue.displayedValue ? itemValue.displayedValue : "");


            let disabled = false;

            a.dataset.index = i.toString();
            a.dataset.findex = index.toString();
            a.dataset.targetValue = itemValue.targetValue;

            // escape and parse FHML
            displayValue = this.resolveValue(displayValue);
            a.innerHTML = displayValue;
            a.addEventListener('mousedown', function (index, targetItem) {
                event.preventDefault();

                if (this.accessibility !== 'EDIT' || disabled) {
                    return;
                }
                this.highlighted = this.findByValue(targetItem.targetValue);
                this.selectedIndex = index ;
                //if (this.emptyLabel) {
                //    this.selectedIndex = this.selectedIndex - 1;
                //}
                this.input.value = itemValue.displayAsTarget ? itemValue.targetValue : (itemValue.displayedValue ? itemValue.displayedValue : "");

                this.updateModel();
                if (this.onChange && (this.rawValue !== this.oldValue)) {
                    this.fireEventWithLock('onChange', this.onChange);
                    this.changeToFired = false;
                }

                this.closeAutocomplete();
                this.input.select();
            }.bind(this, i, itemValue));

            li.appendChild(a);
            this.autocompleter.appendChild(li);
            index = index + 1;
        }
    }

    resolveValue(value) {
        value = this.fhml.resolveValueTextOrEmpty(value);
        return value;
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

    getDefaultWidth(): string {
        return "md-3";
    }

    destroy(removeFromParent) {
        if (this.keySupportCallback) {
            this.keySupportCallback();
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
