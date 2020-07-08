import '../../external/inputmask.js';
import {HTMLFormComponent, FormComponent} from "fh-forms-handler";
import {FhContainer} from "fh-forms-handler";
import {FormComponentKeySupport} from "fh-forms-handler";

class InputNumber extends HTMLFormComponent {
    public input: any;
    private keySupport: FormComponentKeySupport;
    protected keySupportCallback: any;
    private isTextarea: any;
    private placeholder: any;
    private maxLength: any;
    private onInput: any;
    private onChange: any;
    private valueChanged: boolean;
    protected inputmaskEnabled: boolean;
    private maxFractionDigits:any;
    private maxIntigerDigits:any;

    constructor(componentObj: any, parent: HTMLFormComponent) {
        if (componentObj.rawValue != undefined) {
            componentObj.rawValue = componentObj.rawValue.replace(',', '.');
        }

        super(componentObj, parent);

        if (componentObj.rawValue == undefined && componentObj.value == undefined) {
            this.oldValue = '';
        }

        this.isTextarea = this.componentObj.rowsCount && this.componentObj.rowsCount > 0;
        this.placeholder = this.componentObj.placeholder || null;
        this.maxLength = this.componentObj.maxLength || null;
        this.inputmaskEnabled = false;

        this.onInput = this.componentObj.onInput;
        this.onChange = this.componentObj.onChange;

        this.maxFractionDigits = this.componentObj.maxFractionDigits != undefined ? this.componentObj.maxFractionDigits : null;
        this.maxIntigerDigits = this.componentObj.maxIntigerDigits != undefined ? this.componentObj.maxIntigerDigits : null;

        this.input = null;
        this.valueChanged = false;
        // @ts-ignore
        this.keySupport = FhContainer.get("FormComponentKeySupport")(this.componentObj, this);
    }

    create() {
        var input;
        input = document.createElement('input');
        input.type = 'text';
        input.value = this.rawValue;
        input.id = this.id;
        ['fc', 'InputNumber', 'form-control'].forEach(function (cssClass) {
            input.classList.add(cssClass);
        });

        if (this.placeholder) {
            input.placeholder = this.placeholder;
        }
        if (this.maxLength) {
            input.maxLength = this.maxLength;
        }

        // must be before onInput/onChange events
        this.keySupportCallback = this.keySupport.addKeyEventListeners(input);

        $(input).on('input', this.updateModel.bind(this));
        if (this.onChange) {
            $(input).on('change', this.onChangeEvent.bind(this));
        }
        if (this.onInput) {
            $(input).on('input', this.onInputEvent.bind(this));
        }

        this.input = input;
        this.component = input;
        this.focusableComponent = input;
        this.hintElement = this.component;

        this.wrap(false);
        this.addStyles();
        this.createIcon();

        // else {
        //     this.htmlElement.appendChild(input);
        // }
        this.display();
        this.applyMask();
    };

    applyMask() {
        if (!this.inputmaskEnabled) {
            // @ts-ignore
            Inputmask({
                radixPoint: ".",
                regex: this.resolveRegex()
            }).mask(this.input);
            this.inputmaskEnabled = true;
        }
    }

    disableMask() {
        if (this.inputmaskEnabled) {
            // @ts-ignore
            Inputmask.remove(this.input);
            this.inputmaskEnabled = false;
        }
    }

    onChangeEvent() {
        this.fireEventWithLock('onChange', this.onChange);
    }

    onInputEvent() {
        this.fireEvent('onInput', this.onInput);
    }

    protected createIcon() {
        if (this.componentObj.icon) {
            let group = document.createElement('div');
            group.classList.add('input-group-prepend');

            let groupSpan = document.createElement('span');
            groupSpan.classList.add('input-group-text');
            group.appendChild(groupSpan);

            this.htmlElement.classList.add('hasInputIcon');
            let icon = document.createElement('i');
            let classes = this.componentObj.icon.split(' ');
            icon.classList.add(classes[0]);
            if (classes[1]) {
                icon.classList.add(classes[1]);
            }
            groupSpan.appendChild(icon);

            if (this.componentObj.iconAlignment === 'BEFORE') {
                this.inputGroupElement.insertBefore(group, this.inputGroupElement.firstChild);
            } else if (this.componentObj.iconAlignment === 'AFTER') {
                group.classList.remove('input-group-prepend');
                group.classList.add('input-group-append');
                this.inputGroupElement.appendChild(group);
            } else {
                this.inputGroupElement.insertBefore(group, this.inputGroupElement.firstChild);
            }
        }
    }

    update(change) {
        super.update(change);

        $.each(change.changedAttributes || [], function (name, newValue) {
            switch (name) {
                case 'rawValue':
                    if (newValue != undefined) {
                        newValue = newValue.replace(',', '.')
                    }
                    this.input.value = newValue;
                    break;
            }
        }.bind(this));
    };

    updateModel() {
        this.valueChanged = true;
        this.oldValue = this.rawValue;
        this.rawValue = this.input.value;
    };

    extractChangedAttributes() {
        if (this.designMode == true) {
            return this.changesQueue.extractChangedAttributes();
        }
        var attrs = {};
        if (this.valueChanged) {
            attrs[FormComponent.VALUE_ATTRIBUTE_NAME] = this.rawValue;
            this.valueChanged = false;
        }

        return attrs;
    };

    wrap(skipLabel) {
        super.wrap(skipLabel, true);
        this.htmlElement.classList.add('form-group');
    };

    setAccessibility(accessibility) {
        super.setAccessibility(accessibility);

        switch (accessibility) {
            case 'EDIT':
                this.applyMask();
                this.input.readOnly = false;
                this.input.removeAttribute('tabIndex');
                break;
            case 'VIEW':
                this.disableMask();
                this.input.disabled = false;
                this.input.readOnly = true;
                this.input.setAttribute('tabIndex', '-1');
                break;
            case 'DEFECTED':
                this.disableMask();
                this.input.disabled = true;
                this.input.setAttribute('tabIndex', '-1');
                break;
        }

        this.accessibility = accessibility;
    };

    destroy(removeFromParent) {
        if (this.keySupportCallback) {
            this.keySupportCallback();
        }

        if (this.onChange) {
            $(this.input).off('change', this.onChangeEvent.bind(this));
        }
        if (this.onInput) {
            $(this.input).off('input', this.onInputEvent.bind(this));
        }

        $(this.input).off('input', this.updateModel.bind(this));

        this.disableMask();

        super.destroy(removeFromParent);
    }


    resolveRegex(){
        let fractionMark = "[\\d]*)"; // Matches between one and unlimited times
        let integerMark = "[\\d]*"; // Matches between one and unlimited times
        let separatorMark = "([.]{0,1}" //Matches between zero and one times

        if(this.maxFractionDigits != null) {
            if (this.maxFractionDigits == 0) {
                fractionMark = "";
                separatorMark = "";
            } else {
                fractionMark = "[\\d]{0,"+this.maxFractionDigits+"})"
            }
        }

        if(this.maxIntigerDigits != null) {
            if (this.maxIntigerDigits == 0) {
                integerMark = "[0]{1}"; // Only 0 can be put before separator
            } else {
                integerMark = "[\\d]{0,"+this.maxIntigerDigits+"}"
            }
        }

        return "^([-]?"+integerMark+""+separatorMark+""+fractionMark+")$";

    }



}

export {InputNumber};
