import { HTMLFormComponent } from "fh-forms-handler";
declare class RadioOptionsGroup extends HTMLFormComponent {
    private onChange;
    private groupDiv;
    private options;
    constructor(componentObj: any, parent: HTMLFormComponent);
    create(): void;
    update(change: any): void;
    setAccessibility(accessibility: any): void;
    extractChangedAttributes(): {};
    getDefaultWidth(): string;
    refreshOptions(): void;
}
export { RadioOptionsGroup };
