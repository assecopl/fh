import { HTMLFormComponent } from 'fh-forms-handler';
import {DropdownItem} from "./DropdownItem";

class ThreeDotsMenuItem extends DropdownItem {
   constructor(componentObj: any, parent: HTMLFormComponent) {
        super(componentObj, parent);
    }
}

export {ThreeDotsMenuItem};
