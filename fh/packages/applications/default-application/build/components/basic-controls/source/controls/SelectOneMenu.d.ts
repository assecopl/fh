import { HTMLFormComponent } from "fh-forms-handler";
declare class SelectOneMenu extends HTMLFormComponent {
    private options;
    private emptyValue;
    private emptyLabel;
    private emptyLabelText;
    private modelBindingText;
    private onChange;
    private defaultOption;
    private clearButton;
    constructor(componentObj: any, parent: HTMLFormComponent);
    create(): void;
    selectOnClickEvent(): void;
    selectChangeEvent(): void;
    buttonClickEvent(event: any): void;
    update(change: any): void;
    wrap(skipLabel: any): void;
    private removeDuplicatedOptions;
    buildOptionsList(): Array<any>;
    extractChangedAttributes(): {};
    setAccessibility(accessibility: any): void;
    getDefaultWidth(): string;
    destroy(removeFromParent: boolean): void;
}
export { SelectOneMenu };
