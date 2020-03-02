import {HTMLFormComponent} from "fh-forms-handler";

class CheckBox extends HTMLFormComponent {
    private readonly stickedLabel: boolean;
    private readonly onChange: any;
    public input: any;

    constructor(componentObj: any, parent: HTMLFormComponent) {
        super(componentObj, parent);

        if (componentObj.labelPosition == null) {
            componentObj.labelPosition = 'right';
            componentObj.inputSize = undefined;
            this.stickedLabel = true;
        }

        this.rawValue = this.componentObj.rawValue == true || this.componentObj.rawValue == "true";
        this.onChange = this.componentObj.onChange;

        this.input = null;
    }

    create() {
        let input = document.createElement('input');
        input.id = this.id;
        input.classList.add('fc');
        input.classList.add('form-check-input');
        input.type = 'checkbox';
        input.checked = this.rawValue;

        input.addEventListener('change', this.inputCheckEvent.bind(this));
        if (this.onChange) {
            input.addEventListener('change', this.onChangeEvent.bind(this));
        }

        this.input = input;
        this.component = input;
        this.focusableComponent = input;
        this.hintElement = this.component;
        this.wrap(false);

        let label = this.htmlElement.getElementsByTagName('label')[0];
        label.classList.remove('col-form-label');
        label.classList.add('form-check-label');
        if (this.stickedLabel) {
            this.htmlElement.classList.add('stickedLabel');
        }
        this.addStyles();
        this.display();

        if (this.input.disabled == false && !this.rawValue && this.componentObj.rawValue == undefined) {
            this.changesQueue.queueValueChange(false);
        }
        this.htmlElement.insertBefore(input, this.htmlElement.firstChild);
    };

    inputCheckEvent() {
        this.changesQueue.queueValueChange(this.input.checked);
    }

    onChangeEvent() {
        this.fireEventWithLock('onChange', this.onChange);
    }

    update(change) {
        super.update(change);

        $.each(change.changedAttributes || [], function (name, newValue) {
            switch (name) {
                case 'rawValue':
                    this.rawValue = (newValue == true) || (newValue == "true");
                    this.input.checked = this.rawValue;
                    if (this.input.disabled == false && !this.rawValue && ((newValue !== false) && (newValue !== "false"))) {
                        this.changesQueue.queueValueChange(false);
                    }
                    break;
            }
        }.bind(this));
    };

    extractChangedAttributes() {
        return this.changesQueue.extractChangedAttributes();
    };

    addAlignStyles() {
        super.addAlignStyles();
        let horizontalAlign = '';
        let verticalAlign = '';

        if (!this.componentObj.horizontalAlign) {
            horizontalAlign = 'start';
        } else if (this.componentObj.horizontalAlign && this.htmlElement) {
            switch (this.componentObj.horizontalAlign) {
                case "LEFT":
                    horizontalAlign = "start";
                    break;
                case "CENTER":
                    horizontalAlign = "center";
                    break;
                case "RIGHT":
                    horizontalAlign = "end";
                    break;
            }
        }
        this.htmlElement.classList.add('justify-content-' + horizontalAlign);

        if (!this.componentObj.verticalAlign) {
            verticalAlign = "start"
        } else if (this.componentObj.verticalAlign && this.htmlElement) {
            switch (this.componentObj.verticalAlign) {
                case "TOP":
                    verticalAlign = "start";
                    break;
                case "MIDDLE":
                    verticalAlign = "center";
                    break;
                case "BOTTOM":
                    verticalAlign = "end";
                    break;
            }
        }
        this.htmlElement.classList.add('align-items-' + verticalAlign);
    };

    wrap(skipLabel) {
        super.wrap(skipLabel);
        this.htmlElement.classList.add('form-group');
        this.htmlElement.classList.add('form-check');
    };

    setAccessibility(accessibility) {
        super.setAccessibility(accessibility);

        switch (accessibility) {
            case 'EDIT':
                this.input.disabled = false;
                break;
            default:
                this.input.disabled = true;
                break;
        }
    };

    setPresentationStyle(presentationStyle) {
        ['is-invalid'].forEach(function (cssClass) {
            this.labelElement.classList.remove(cssClass);
        }.bind(this));

        switch (presentationStyle) {
            case 'BLOCKER':
            case 'ERROR':
                ['is-invalid'].forEach(function (cssClass) {
                    this.labelElement.classList.add(cssClass);
                }.bind(this));
                break;
        }

        super.setPresentationStyle(presentationStyle);
    }

    getDefaultWidth(): string {
        return "md-2";
    }

    destroy(removeFromParent: boolean) {
        this.input.removeEventListener('change', this.inputCheckEvent.bind(this));
        if (this.onChange) {
            this.input.removeEventListener('change', this.onChangeEvent.bind(this));
        }

        super.destroy(removeFromParent);
    }
}

export {CheckBox}
