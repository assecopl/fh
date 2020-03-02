import {HTMLFormComponent} from "fh-forms-handler";
import {FormComponent} from "fh-forms-handler";

class RadioOption extends HTMLFormComponent {
    private readonly onChange: any;
    private readonly groupName: string;
    private readonly checked: boolean;
    private input: any;
    private valueChanged: boolean = false;

    constructor(componentObj: any, parent: HTMLFormComponent) {
        super(componentObj, parent);
        this.onChange = this.componentObj.onChange;
        this.rawValue = this.componentObj.rawValue;
        this.groupName = this.componentObj.groupName;
        this.checked = this.componentObj.checked;
    }

    create() {
        let input = document.createElement('input');
        input.type = 'radio';
        input.name = this.groupName;
        input.checked = this.checked;
        input.classList.add('fc');
        input.classList.add('form-check-input');
        input.id = this.id;

        input.addEventListener('click', function () {
            this.updateModel();
        }.bind(this));

        input.addEventListener('click', function (event) {
            this.fireEventWithLock('onChange', this.onChange, event);
        }.bind(this));


        this.input = input;
        this.component = input;
        this.hintElement = this.component;
        this.wrap(false);

        let label = this.htmlElement.getElementsByTagName('label')[0];
        label.classList.remove('col-form-label');
        label.classList.add('form-check-label');


        this.addStyles();
        this.display();

        this.focusableComponent = this.htmlElement;
        this.htmlElement.tabIndex = 0;
        this.htmlElement.insertBefore(input, this.htmlElement.firstChild);
    }

    update(change) {
        super.update(change);

        if (change.changedAttributes) {
            $.each(change.changedAttributes, function (name, newValue) {
                switch (name) {
                    case 'checkedRadio':
                        this.componentObj.checked = newValue;
                        this.checked = newValue;
                        this.input.checked = newValue;
                        break;
                }
            }.bind(this));
        }
    };

    wrap(skipLabel) {
        super.wrap(skipLabel);
        this.htmlElement.classList.add('form-group');
        this.htmlElement.classList.add('form-check');
        this.htmlElement.classList.add('positioned');
        this.htmlElement.classList.add('right');
        this.htmlElement.classList.add('radioOption');
    };

    setAccessibility(accessibility) {
        super.setAccessibility(accessibility);
        switch (accessibility) {
            case 'VIEW':
                this.input.disabled = true;
                this.component.classList.remove('fc-editable');
                this.component.classList.add('fc-disabled');
                break;
            default:
                this.input.disabled = false;
                this.component.classList.add('fc-editable');
                this.htmlElement.classList.remove('fc-disabled');
                break;
        }
    };

    setPresentationStyle(presentationStyle) {
        if (this.labelElement.classList.contains('empty-label')) {
            ['border', 'border-success', 'border-info', 'border-warning', 'border-danger', 'is-invalid'].forEach(function (cssClass) {
                this.htmlElement.classList.remove(cssClass);
            }.bind(this));

            switch (presentationStyle) {
                case 'BLOCKER':
                case 'ERROR':
                    ['is-invalid', 'border', 'border-danger'].forEach(function (cssClass) {
                        this.htmlElement.classList.add(cssClass);
                    }.bind(this));
                    break;
                case 'OK':
                    ['border', 'border-success'].forEach(function (cssClass) {
                        this.htmlElement.classList.add(cssClass);
                    }.bind(this));
                    break;
                case 'INFO':
                    ['border', 'border-info'].forEach(function (cssClass) {
                        this.htmlElement.classList.add(cssClass);
                    }.bind(this));
                    break;
                case 'WARNING':
                    ['border', 'border-warning'].forEach(function (cssClass) {
                        this.htmlElement.classList.add(cssClass);
                    }.bind(this));
                    break;
            }
        }

        super.setPresentationStyle(presentationStyle);
    }

    updateModel() {
        this.valueChanged = true;
    };

    extractChangedAttributes() {
        if (this.designMode == true) {
            return this.changesQueue.extractChangedAttributes();
        }
        let attrs = {};
        if (this.valueChanged && this.input.checked) {
            attrs[FormComponent.VALUE_ATTRIBUTE_NAME] = this.rawValue;
            this.valueChanged = false;
        }

        return attrs;
    };
}

export {RadioOption};
