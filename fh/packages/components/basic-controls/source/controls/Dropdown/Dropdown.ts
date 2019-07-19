import 'bootstrap/js/dist/dropdown';
import {HTMLFormComponent} from "fh-forms-handler";
import {AdditionalButton} from "fh-forms-handler";

class Dropdown extends HTMLFormComponent {
    private button: any;
    private dropdown: any;
    private menu: any;
    private style: any;
    private readonly onClick: any;

    constructor(componentObj: any, parent: HTMLFormComponent) {
        super(componentObj, parent);

        this.style = this.componentObj.style;
        this.onClick = this.componentObj.onClick;

        this.button = null;
        this.menu = null;
    }

    create() {
        //Button.getPrototype().create.call(this);
        let label = this.componentObj.label;

        let button = document.createElement('button');
        button.id = this.id;
        ['fc', 'button', 'btn', 'btn-' + this.style, 'btn-block'].forEach(function (cssClass) {
            button.classList.add(cssClass);
        });

        label = this.resolveLabelAndIcon(label);
        button.innerHTML = label;

        if (this.onClick) {
            button.addEventListener('click', this.onButtonClickEvent.bind(this));
        }

        this.component = button;
        this.hintElement = this.component;
        this.wrap(true);
        this.addStyles();

        this.display();

        button = this.component;
        button.classList.add('dropdown-toggle');
        button.dataset.toggle = 'dropdown';

        this.button = button;
        this.addCaret();

        this.dropdown = document.createElement('div');
        this.dropdown.classList.add('fc');
        this.dropdown.classList.add('dropdown');

        this.dropdown.appendChild(button);

        let menu = document.createElement('ul');
        menu.classList.add('dropdown-menu');
        this.menu = menu;
        this.dropdown.appendChild(menu);

        this.component = this.dropdown;
        this.htmlElement.appendChild(this.component);
        this.contentWrapper = menu;
        this.hintElement = this.component;

        if (this.componentObj.subelements) {
            this.addComponents(this.componentObj.subelements);
        }
    };

    onButtonClickEvent(event) {
        event.stopPropagation();
        this.fireEventWithLock('onClick', this.onClick);
        event.target.blur();
    }

    update(change) {
        super.update(change);

        if (change.changedAttributes) {
            $.each(change.changedAttributes, function (name, newValue) {
                switch (name) {
                    case 'label':
                        this.button.innerHTML = this.resolveLabelAndIcon(newValue);
                        this.addCaret();
                        break;
                    case 'style':
                        this.button.classList.remove('btn-' + this.style);
                        this.button.classList.add('btn-' + newValue);
                        this.style = newValue;
                }
            }.bind(this));
        }
    };

    resolveLabelAndIcon(label) {
        return this.fhml.resolveValueTextOrEmpty(label);
    };

    addCaret() {
        let span = document.createElement('span');
        span.classList.add('caret');
        this.button.appendChild(span);
    };

    getAdditionalButtons(): AdditionalButton[] {
        return [
            new AdditionalButton('addDefaultSubcomponent', 'plus', 'Add dropdown item')
        ];
    }

    wrap(skipLabel) {
        super.wrap(skipLabel);
        this.htmlElement.classList.add('form-group');
    }

    setAccessibility(accessibility) {
        if (this.button != null) {
            // swap component while changing accessibility
            this.component = this.button;
            super.setAccessibility(accessibility);
            this.component = this.dropdown;
        } else {
            super.setAccessibility(accessibility);

            if (this.accessibility === 'VIEW') {
                this.component.disabled = false;
                this.component.classList.add('disabledElement');
            }
        }
    }

    destroy(removeFromParent: boolean) {
        if (this.onClick) {
            this.button.removeEventListener('click', this.onButtonClickEvent.bind(this));
        }

        super.destroy(removeFromParent);
    }

    /**
     * @Override
     */
    public getDefaultWidth():string {
        return null;
    }
}

export {Dropdown};