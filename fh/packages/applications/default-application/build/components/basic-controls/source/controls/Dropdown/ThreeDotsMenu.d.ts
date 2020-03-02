import { AdditionalButton, HTMLFormComponent } from "fh-forms-handler";
import { Dropdown } from "./Dropdown";
declare class ThreeDotsMenu extends Dropdown {
    constructor(componentObj: any, parent: HTMLFormComponent);
    create(): void;
    getDefaultWidth(): string;
    getAdditionalButtons(): AdditionalButton[];
}
export { ThreeDotsMenu };
