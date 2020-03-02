import { HTMLFormComponent } from "fh-forms-handler";
import { Dropdown } from "./Dropdown";
declare class RowDropdown extends Dropdown {
    constructor(componentObj: any, parent: HTMLFormComponent);
    create(): void;
}
export { RowDropdown };
