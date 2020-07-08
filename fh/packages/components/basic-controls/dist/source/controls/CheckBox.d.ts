import { HTMLFormComponent } from "fh-forms-handler";
declare class CheckBox extends HTMLFormComponent {
    private readonly stickedLabel;
    private readonly onChange;
    input: any;
    constructor(componentObj: any, parent: HTMLFormComponent);
    create(): void;
    inputCheckEvent(): void;
    onChangeEvent(): void;
    update(change: any): void;
    extractChangedAttributes(): {};
    addAlignStyles(): void;
    wrap(skipLabel: any): void;
    setAccessibility(accessibility: any): void;
    setPresentationStyle(presentationStyle: any): void;
    getDefaultWidth(): string;
    destroy(removeFromParent: boolean): void;
    /**
     * @Overwrite parent function
     * @param ttip
     */
    processStaticHintElement(ttip: any): any;
}
export { CheckBox };
