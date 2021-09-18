import {HTMLFormComponent} from "fh-forms-handler";

class SelectOneMenu extends HTMLFormComponent {
    private options: any;
    private emptyValue: boolean;
    private emptyLabel: boolean;
    private emptyLabelText: string;
    private modelBindingText: string;
    private onChange: any;
    private defaultOption: any;
    private clearButton: HTMLSpanElement;

    constructor(componentObj: any, parent: HTMLFormComponent) {
        super(componentObj, parent);

        this.options = this.componentObj.rawOptions;
        this.onChange = this.componentObj.onChange;

        this.emptyValue = this.componentObj.emptyValue;
        this.emptyLabel = this.componentObj.emptyLabel;
        this.emptyLabelText = this.componentObj.emptyLabelText || '';
        this.modelBindingText = this.componentObj.modelBindingText;

    }

    create() {
        let select = document.createElement('select');
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
            let componetParent = this.component.parentNode;
            componetParent.classList.add('SelectOneMenu');

            componetParent.removeChild(this.component);

            let wrapperDiv = document.createElement('div');
            wrapperDiv.classList.add('input-group');

            let clearButtonElement = document.createElement('div');
            clearButtonElement.classList.add('input-group-append');
            let button = document.createElement('span');
            button.classList.add('input-group-text');
            let icon = document.createElement('i');
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

    selectOnClickEvent() {
        event.stopPropagation();
    }

    selectChangeEvent() {
        this.rawValue = this.component.value;
        this.changesQueue.queueValueChange(this.rawValue);
        this.defaultOption.disabled = this.rawValue != -1 && !this.emptyLabel;
        if (this.onChange) {
            if (this._formId === 'FormPreview') {
                this.fireEvent('onChange', this.onChange);
            } else {
                this.fireEventWithLock('onChange', this.onChange);
            }
        }
    }

    buttonClickEvent(event) {
        // this.selectedIndex = {"":-1};
        this.component.selectedIndex = -1;
        this.rawValue = -1;
        this.changesQueue.queueValueChange(this.rawValue);
        event.stopPropagation();
        if (this.onChange) {
            this.fireEventWithLock('onChange', this.onChange);
        }
    }

    update(change) {
        super.update(change);

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
                        } else {
                            this.component.value = newValue;
                            this.rawValue = newValue;
                        }
                        break;
                }
            }.bind(this));

        }
    };

    wrap(skipLabel) {
        super.wrap(skipLabel, true);
        this.htmlElement.classList.add('form-group');
    };

    private removeDuplicatedOptions(options: Array<any>): Array<any> {
        return options.reduce((unique, o) => {
            if (!unique.some(obj => obj.textContent === o.textContent)) {
                unique.push(o);
            }
            return unique;
        }, []);
    }

    buildOptionsList(): Array<any> {
        let options = [];

        if (this.designMode) {
            this.emptyLabel = true;
            this.emptyLabelText = this.modelBindingText;
        }

        let defaultOption = document.createElement('option');
        defaultOption.selected = true;
        defaultOption.disabled = this.rawValue != -1 && !this.emptyLabel;
        if (this.emptyLabel) {
            defaultOption.style.display = 'block';
        } else {
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

    extractChangedAttributes() {
        return this.changesQueue.extractChangedAttributes();
    };

    setAccessibility(accessibility) {
        super.setAccessibility(accessibility);
        this.htmlElement.classList.add('fc-editable');
    }

    destroy(removeFromParent: boolean) {
        this.component.removeEventListener('click', this.selectOnClickEvent.bind(this));
        this.component.removeEventListener('change', this.selectChangeEvent.bind(this));

        if (this.emptyValue && this.emptyValue == true) {
            this.clearButton.removeEventListener('click', this.buttonClickEvent.bind(this));

            super.destroy(removeFromParent);
        }
    }
}

export {SelectOneMenu};
