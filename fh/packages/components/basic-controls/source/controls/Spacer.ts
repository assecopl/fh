import {HTMLFormComponent} from "fh-forms-handler";

class Spacer extends HTMLFormComponent {
    constructor(componentObj: any, parent: HTMLFormComponent) {
        super(componentObj, parent);
    }

    create() {
        this.component = document.createElement('div');
        this.component.id = this.id;
        this.wrap(true);

        // spacer.classList.add('spacer');
        this.component.parentNode.classList.add('spacer');

        this.addStyles();
        this.display();
    };

    getDefaultWidth(): string {
        return 'md-2';
    }
}

export {Spacer};
