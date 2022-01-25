import * as Inputmask from 'fh-basic-controls/dist/source/external/inputmask';
import {FhContainer, FormComponent, FormComponentKeySupport, HTMLFormComponent} from "fh-forms-handler";

class InputNumberFhDP extends HTMLFormComponent {
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
    protected lastValue: any;
    protected hideCrossed: any;
    protected newValueText: any;
    protected isLastValueEnabled: boolean;

    constructor(componentObj: any, parent: HTMLFormComponent) {
        console.log('In InputNumberFhDP ', componentObj);
        if (componentObj.rawValue != undefined) {
            componentObj.rawValue = componentObj.rawValue.replace(',', '.');
        }

        super(componentObj, parent);
        if (this.componentObj.hideCrossed == undefined) {
            this.hideCrossed = false;
        } else {
            this.hideCrossed = this.componentObj.hideCrossed;
        }

        if (componentObj.rawValue == undefined && componentObj.value == undefined) {
            this.oldValue = '';
        }

        this.lastValue = this.componentObj.lastValue;
        this.newValueText = this.componentObj.newValueText;
        this.isLastValueEnabled = this.componentObj.isLastValueEnabled;
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
        if(this.isLastValueEnabled!==false) {
            this.createLastValueElement();
        }

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
                radixPoint: (Intl.NumberFormat() as any).formatToParts(1.1).find(part => part.type === 'decimal').value || '.',
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
        console.log('W on input');
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
    
    unifyValue(value: string | number) {
        let valueToUnify = `${value}`;
        valueToUnify = valueToUnify.replace(',' , '.');
        return valueToUnify;
    }

    updateModel() {
        this.valueChanged = true;
        this.oldValue = this.rawValue;
        this.rawValue = this.input.value;
        console.log('w updateModel');
        let theSameValue = this.unifyValue(this.rawValue) == this.unifyValue(this.lastValue);
        if(this.lastValue===undefined && !!this.newValueText) {
            theSameValue = this.rawValue === '';
        }
        this.toogleLastValueElement(theSameValue);

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
    protected createLastValueElement(){
        console.log('in createLastValueElement ', this.componentObj.lastValue);
        if(this.componentObj.lastValue){
            let group = document.createElement('div');
            group.classList.add('input-group-append');

            let groupSpan = document.createElement('span');
            groupSpan.classList.add('input-group-text');
            groupSpan.classList.add('input-old-value');
            if(this.hideCrossed == "true"){
                groupSpan.classList.add('input-old-value-remove-line');
            }
            if(this.unifyValue(this.rawValue) == this.unifyValue(this.lastValue)){
                groupSpan.classList.add('hide-old-value');
            }
            groupSpan.innerText  = this.lastValue;

            group.appendChild(groupSpan);
            this.inputGroupElement.appendChild(group);
        } else if(this.componentObj.newValueText && this.componentObj.lastValue===undefined) {
            let group = document.createElement('div');
            group.classList.add('input-group-append');

            let groupSpan = document.createElement('span');
            groupSpan.classList.add('input-group-text');
            groupSpan.classList.add('input-old-value');
            groupSpan.classList.add('text-decoration-none');
            if(this.rawValue === '' || this.rawValue === undefined){
                groupSpan.classList.add('hide-old-value');
            }

            groupSpan.innerText = this.newValueText;

            group.appendChild(groupSpan);
            this.inputGroupElement.appendChild(group);
        }
    }
    protected toogleLastValueElement(theSameValue){
        console.log('w toogleLastValueElement');
        let oldValueElement = this.inputGroupElement.getElementsByClassName('input-old-value');
        if(oldValueElement[0]) {
            if(theSameValue){
                oldValueElement[0].classList.add('hide-old-value');
                if(this.hideCrossed == "true"){
                    oldValueElement[0].classList.add('input-old-value-remove-line');

                }
            } else {
                oldValueElement[0].classList.remove('hide-old-value');
            }

        }
    }



}

export {InputNumberFhDP};
