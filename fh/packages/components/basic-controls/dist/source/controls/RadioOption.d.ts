import { HTMLFormComponent } from "fh-forms-handler";
declare class RadioOption extends HTMLFormComponent {
    private readonly onChange;
    private readonly groupName;
    private readonly checked;
    input: any;
    private valueChanged;
    constructor(componentObj: any, parent: HTMLFormComponent);
    create(): void;
    update(change: any): void;
    wrap(skipLabel: any): void;
    setAccessibility(accessibility: any): void;
    setPresentationStyle(presentationStyle: any): void;
    updateModel(): void;
    extractChangedAttributes(): {};
}
export { RadioOption };
