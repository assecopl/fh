import {HTMLFormComponent} from "fh-forms-handler";
// import {Dropdown} from "./Dropdown/Dropdown";
// import {Button} from "./Button";
import {AdditionalButton} from "fh-forms-handler";
import {Dropdown} from "./Dropdown/Dropdown";
import {Button} from "./Button";

class ImagesSwitchButtons extends HTMLFormComponent {
    private activeButton: any;
    private onButtonChange: any;
    private cssClass: string;
    private label: string;

    constructor(componentObj: any, parent: HTMLFormComponent) {
        super(componentObj, parent);

        this.activeButton = this.componentObj.activeButton;
        this.onButtonChange = this.componentObj.onButtonChange;
        this.cssClass = this.componentObj.cssClass || "";
        this.label = this.componentObj.label || "";
    }

    create() {
        var group: HTMLDivElement = document.createElement('div');

        group.classList.add('fc');
        group.classList.add('fh-images-switch-group');

        group.id = this.id;

        if (this.label) {
            var label = document.createElement('label');
            label.innerText = this.i18n.__(this.label);
            label.classList.add("mr-2")
            this.labelElement = label;
            group.appendChild(label);
        }

        this.component = group;
        this.htmlElement = this.component;
        this.wrap(true);
        this.display();

        if (this.componentObj.subelements) {
            if (this.componentObj.subelements[0].type === 'Repeater') {
                this.addComponents(this.componentObj.subelements[0].subelements);
            } else {
                this.addComponents(this.componentObj.subelements);
            }
        }

        if (this.activeButton > -1) {
            (<any>this.components[this.activeButton]).component.classList.add('active');
        }
    }

    update(change) {
        super.update(change);

        for(let elementHTML of this.component.querySelectorAll('.active') || []) {
            elementHTML.classList.remove('active')
        }

        $.each(change.changedAttributes || [], function (name, newValue) {
            switch (name) {
                case 'activeButton':
                    this.activeButton = newValue;

                    if (this.activeButton > -1) {
                        this.components[newValue].component.classList.add('active');
                    }
                    if (this.activeButton == 1) {
                        this.wcagUtil.turnOnImagesHidden(this.cssClass);
                    } else {
                        if (document.body.classList) {
                            this.wcagUtil.turnOffImagesHidden()
                        }
                    }
                    break;
            }
        }.bind(this));
    }

    addComponents(componentsList) {
        if (!componentsList.length) {
            return;
        }

        componentsList.forEach(function (componentObj) {
            this.addComponent(componentObj);

            var component = this.components[this.components.length - 1];
            this.contentWrapper.removeChild(component.htmlElement);
            component.htmlElement = component.component;
            component.display();

            component.component.dataset.index = this.components.length - 1;

            component.component.addEventListener('click', function (event) {
                var currentActive = this.component.querySelector('.active');

                if (currentActive !== event.target) {
                    this.activeButton = event.target.dataset.index;
                    event.target.classList.add('active');
                }

                this.changesQueue.queueValueChange(this.activeButton);

                if (!component.onClick) {
                    this.fireEvent('onButtonChange', this.onButtonChange);
                }
            }.bind(this));

            if (component instanceof Dropdown) {
                component.htmlElement.classList.add('btn-group');
            } else if (component instanceof Button) {
                (<any>component).component.classList.remove('btn-block');
                var btnWrapper = document.createElement('div');
                if ((<any>component).toolbox) {
                    btnWrapper.classList.add('wrapper');
                    btnWrapper.appendChild((<any>component).toolbox);
                    component.htmlElement = btnWrapper;
                }
            }
            component.display();
        }.bind(this));
    }

    extractChangedAttributes(): {} {
        return this.changesQueue.extractChangedAttributes();
    }

    getAdditionalButtons(): AdditionalButton[] {
        return [
            new AdditionalButton('addDefaultSubcomponent', 'plus', 'Add button'),
        ];
    }
}

export {ImagesSwitchButtons}