import {HTMLFormComponent} from "fh-forms-handler";

class Canvas extends HTMLFormComponent {
    constructor(componentObj: any, parent: HTMLFormComponent) {
        super(componentObj, parent);
    }

    create() {
        this.component = document.createElement('div');
        this.handlemarginAndPAddingStyles();
        this.wrap(true);
        this.display();

        if (this.componentObj.subelements) {
            this.addComponents(this.componentObj.subelements);
        }
    };
}

export {Canvas};