import { HTMLFormComponent } from "fh-forms-handler";
declare class Repeater extends HTMLFormComponent {
    private referenceNode;
    private rWrapper;
    constructor(componentObj: any, parent: HTMLFormComponent);
    create(): void;
    setAccessibility(accessibility: any): void;
    setPresentationStyle(presentationStyle: any): void;
}
export { Repeater };
