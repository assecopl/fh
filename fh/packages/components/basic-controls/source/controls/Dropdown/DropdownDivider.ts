import { HTMLFormComponent } from 'fh-forms-handler';

class DropdownDivider extends HTMLFormComponent {
    constructor(componentObj: any, parent: HTMLFormComponent) {
        super(componentObj, parent);
    }

    create() {
        let element = document.createElement('div');
        element.classList.add('dropdown-divider');
        element.id = this.id;
        this.component = element;
        this.htmlElement = this.component;

        this.display();
    };
}

export {DropdownDivider};
