import {HTMLFormComponent} from "fh-forms-handler";

class RadioOptionsGroup extends HTMLFormComponent {
    private onChange: any;
    private groupDiv : HTMLDivElement;
    private options : string[];

    constructor(componentObj: any, parent: HTMLFormComponent) {
        if (componentObj.selectedIndex != null) {
            componentObj.rawValue = componentObj.selectedIndex;
        }
        super(componentObj, parent);

        this.onChange = this.componentObj.onChange;
    }

    create() {
        this.groupDiv = document.createElement('div');
        this.groupDiv.id = this.id;

        this.options = this.componentObj.rawOptions;
        this.refreshOptions();

        this.groupDiv.addEventListener('click', function (event) {
            if (this.accessibility != 'EDIT') return;
            if (event.target.tagName === 'LABEL') {
                this.changesQueue.queueValueChange(
                    event.target.parentElement.querySelector('input').dataset.value);
            } else if (event.target.tagName === 'INPUT') {
                this.changesQueue.queueValueChange(event.target.dataset.value);
            }
        }.bind(this));
        if (this.onChange) {
            this.groupDiv.addEventListener('click', function (event) {
                if (this.accessibility != 'EDIT') return;
                if (event.target.tagName === 'LABEL' || event.target.tagName === 'INPUT') {
                    this.fireEvent('onChange', this.onChange);
                }
            }.bind(this));
        }

        this.component = this.groupDiv;
        this.hintElement = this.component;
        this.wrap();
        this.addStyles();
        this.display();
    };

    update(change) {
        super.update(change);

        if (change.changedAttributes) {
            $.each(change.changedAttributes, function (name, newValue) {
                switch (name) {
                    case 'selectedIndex':
                        $(this.component).find('input').prop('checked', false);
                        if (newValue != -1) {
                            var selector = 'input[data-value="' + newValue + '"]';
                            $(this.component).find(selector).prop('checked', true);
                        }
                        break;
                    case 'rawOptions':
                        this.options = newValue;
                        this.refreshOptions();
                        break;
                }
            }.bind(this));
        }
    };

    setAccessibility(accessibility) {
        if (accessibility !== 'HIDDEN') {
            this.htmlElement.classList.remove('invisible');
            this.htmlElement.classList.remove('d-none');
        }
        if (accessibility !== 'DEFECTED' || accessibility !== 'EDIT') {
            this.htmlElement.classList.remove('fc-disabled');
        }
        if (accessibility !== 'EDIT') {
            this.htmlElement.classList.remove('fc-editable');
        }

        const inputs = this.component.querySelectorAll('input');

        switch (accessibility) {
            case 'EDIT':
                [].forEach.call(inputs, function (input) {
                    input.disabled = false;
                    input.classList.add('fc-editable');
                });
                break;
            case 'VIEW':
                [].forEach.call(inputs, function (input) {
                    input.disabled = true;
                    input.classList.add('fc-disabled');
                });
                break;
            case 'HIDDEN':
                if(this.invisible){
                    this.htmlElement.classList.add('invisible');
                } else {
                    this.htmlElement.classList.add('d-none');
                }
                break;
            case 'DEFECTED':
                [].forEach.call(inputs, function (input) {
                    input.disabled = true;
                    input.classList.add('fc-disabled');
                    input.title = 'Broken control';
                });
                break;
        }

        this.accessibility = accessibility;
    };

    extractChangedAttributes() {
        return this.changesQueue.extractChangedAttributes();
    };

    getDefaultWidth(): string {
        return "md-3";
    }

    refreshOptions() {
        this.groupDiv.innerHTML = '';
        if (this.options !== undefined) {
            for (let i = 0, size = this.options.length; i < size; i++) {
                let value = this.options[i];

                let formGroup = document.createElement('div');
                formGroup.classList.add('form-group');
                let radio = document.createElement('div');
                radio.classList.add('form-check');
                let label = document.createElement('label');
                label.classList.add('form-check-label');
                label.setAttribute('for', this.id);
                let input = document.createElement('input');
                input.classList.add('form-check-input');
                input.type = 'radio';
                input.name = this.id;
                input.id = this.id;
                input.dataset.value = i.toString();

                if (this.rawValue === i) {
                    input.checked = true;
                }

                label.appendChild(document.createTextNode(value));

                radio.appendChild(input);
                radio.appendChild(label);
                formGroup.appendChild(radio);
                this.groupDiv.appendChild(formGroup);
            }
        }
    }
}

export {RadioOptionsGroup};
