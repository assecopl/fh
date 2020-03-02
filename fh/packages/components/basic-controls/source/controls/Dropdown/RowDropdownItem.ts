import { HTMLFormComponent } from 'fh-forms-handler';
import {DropdownItem} from "./DropdownItem";

class RowDropdownItem extends DropdownItem {
   constructor(componentObj: any, parent: HTMLFormComponent) {
        super(componentObj, parent);
    }
}

export {RowDropdownItem};
