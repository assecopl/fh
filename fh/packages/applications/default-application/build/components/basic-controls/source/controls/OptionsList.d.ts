import { HTMLFormComponent } from "fh-forms-handler";
declare class OptionsList extends HTMLFormComponent {
    private onIconClick;
    private elements;
    constructor(componentObj: any, parent: HTMLFormComponent);
    create(): void;
    extractChangedAttributes(): {};
    addComponent(componentObj: any): void;
}
export { OptionsList };
