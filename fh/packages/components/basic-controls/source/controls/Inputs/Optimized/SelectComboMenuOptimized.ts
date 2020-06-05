import {HTMLFormComponent} from 'fh-forms-handler';
import {SelectComboMenu} from '../SelectComboMenu';
import * as _ from "lodash";

class SelectComboMenuOptimized extends SelectComboMenu {

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

        for (let i = 0, len = values.length; i < len; i++) {
            let itemValue = values[i];
            let li = document.createElement('li');
            itemValue.element = li;

            let a = document.createElement('a');
            a.classList.add('dropdown-item');

            if(i==0){
                a.classList.add( this.emptyLabel ? 'dropdown-empty' : 'd-none');
            }

            let displayValue = itemValue.displayAsTarget ? itemValue.targetValue : itemValue.displayedValue;

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
                this.input.value = itemValue.displayAsTarget ? itemValue.targetValue : itemValue.displayedValue;

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

        this.hightlightValue();
        this.container.onmouseover = null;
        this.container.onmousemove =  null;
        this.htmlElement.onmousemove =  null;
        this.input.onfocus = null;
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
                this.container.onmousemove =  this.setValues.bind(this,this.values);
                this.input.onfocus =  this.setValues.bind(this,this.values);
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

    /**
     * @Override
     * @param accessibility
     */
    public display(): void {
        super.display();
        // this.container.appendChild(this.htmlElement);
    }

    public render() {
    }

}

export {
    SelectComboMenuOptimized
}
