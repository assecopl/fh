import {HTMLFormComponent} from "fh-forms-handler";
import {Dropdown} from "./Dropdown";

class ThreeDotsMenu extends Dropdown {
    constructor(componentObj: any, parent: HTMLFormComponent) {
        super(componentObj, parent);
    }

    create() {
        super.create();
        this.button.classList.remove('dropdown');
        this.button.classList.add('row-dropdown');
        this.contentWrapper.classList.add('row-dropdown-menu');
    }
}

export {ThreeDotsMenu};
