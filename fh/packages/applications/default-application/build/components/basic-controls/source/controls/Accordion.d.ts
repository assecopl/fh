import { HTMLFormComponent } from "fh-forms-handler";
import { AdditionalButton } from "fh-forms-handler";
import 'bootstrap/js/dist/collapse';
declare class Accordion extends HTMLFormComponent {
    private onGroupChange;
    private activeGroup;
    constructor(componentObj: any, parent: HTMLFormComponent);
    create(): void;
    addComponent(componentObj: any): void;
    update(change: any): void;
    getAdditionalButtons(): AdditionalButton[];
    setButtonAccessibility(button: any, accessibility: any): void;
    collapse(groupIdx: any): void;
    show(groupIdx: any): void;
}
export { Accordion };
