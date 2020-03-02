import { InputText } from "../InputText";
import { HTMLFormComponent } from 'fh-forms-handler';
declare class SelectComboMenuOptimized extends InputText {
    protected values: any;
    protected autocompleter: any;
    protected selectedIndex: any;
    private highlighted;
    private blurEvent;
    private blurEventWithoutChange;
    private readonly onSpecialKey;
    private readonly onDblSpecialKey;
    private readonly freeTyping;
    private openButton;
    private changeToFired;
    private rawValueOnLatSpecialKey;
    private autocompleteOpen;
    private readonly emptyLabel;
    private readonly emptyLabelText;
    constructor(componentObj: any, parent: HTMLFormComponent);
    create(): void;
    protected innerWrap(): any;
    defineDefinitionSymbols(): void;
    createOpenButton(): void;
    inputPasteEvent(event: any): void;
    inputInputEvent(): void;
    inputKeydownEvent(event: any): void;
    inputInputEvent2(): void;
    specialKeyCapture(fireEvent: any, event: any): void;
    inputBlurEvent(): void;
    inputBlurEvent2(): void;
    update(change: any): void;
    updateModel(): void;
    hightlightValue(): void;
    findByValue(value: any): any;
    findValueByElement(value: any): any;
    openAutocomplete(): void;
    closeAutocomplete(): void;
    extractChangedAttributes(): {};
    setValues(values: any): void;
    resolveValue(value: any): any;
    setAccessibility(accessibility: any): void;
    destroy(removeFromParent: any): void;
    /**
     * @Override
     * @param accessibility
     */
    display(): void;
    render(): void;
}
export { SelectComboMenuOptimized };
