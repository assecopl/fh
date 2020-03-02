import {HTMLFormComponent} from "fh-forms-handler";
import {Dropdown} from "./Dropdown/Dropdown";
import {Button} from "./Button";
import {AdditionalButton} from "fh-forms-handler";

class ButtonGroup extends HTMLFormComponent {
    private activeButton: any;
    private onButtonChange: any;
    private margin: boolean;

    constructor(componentObj: any, parent: HTMLFormComponent) {
        super(componentObj, parent);

        this.activeButton = this.componentObj.activeButton;
        this.onButtonChange = this.componentObj.onButtonChange;
        this.margin = this.componentObj.margin || false;
    }

    create() {
        var group = document.createElement('div');
        group.id = this.id;
        group.classList.add('fc');
        group.classList.add('buttonGroup');
        group.classList.add('btn-group');
        group.setAttribute('role','group');

        if (this.margin == true) {
            group.classList.add('margin');
        }

        this.component = group;
        this.htmlElement = this.component;
        this.wrap(true);
        this.handlemarginAndPAddingStyles();
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
    };

    update(change) {
        super.update(change);

        $.each(change.changedAttributes || [], function (name, newValue) {
            switch (name) {
                case 'activeButton':
                    this.activeButton = newValue;
                    var currentActive = this.component.querySelector('.active');
                    if (currentActive) {
                        currentActive.classList.remove('active');
                    }
                    if (this.activeButton > -1) {
                        this.components[newValue].component.classList.add('active');
                    }
                    break;
            }
        }.bind(this));
    };

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
                if (currentActive) {
                    currentActive.classList.remove('active');
                }
                if (currentActive === event.target) {
                    this.activeButton = -1;
                } else {
                    this.activeButton = event.target.dataset.index;
                    event.target.classList.add('active');
                }
                this.changesQueue.queueValueChange(this.activeButton);
                if (!component.onClick) {
                    this.fireEvent('onButtonChange', this.onButtonChange, event);
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
            // this.contentWrapper.appendChild(component.htmlElement);
        }.bind(this));
    };

    extractChangedAttributes() {
        return this.changesQueue.extractChangedAttributes();
    };

    getAdditionalButtons(): AdditionalButton[] {
        return [
            new AdditionalButton('addDefaultSubcomponent', 'plus', 'Add button'),
        ];
    }
}

export {ButtonGroup}