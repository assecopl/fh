import { HTMLFormComponent } from "fh-forms-handler";
import { AdditionalButton } from "fh-forms-handler";
declare class ButtonGroup extends HTMLFormComponent {
    private activeButton;
    private onButtonChange;
    private margin;
    constructor(componentObj: any, parent: HTMLFormComponent);
    create(): void;
    update(change: any): void;
    addComponents(componentsList: any): void;
    extractChangedAttributes(): {};
    getAdditionalButtons(): AdditionalButton[];
}
export { ButtonGroup };
