/// <reference types="jquery" />
/// <reference types="jqueryui" />
/// <reference types="bootstrap" />
import 'imports-loader?moment,define=>false,exports=>false!../../external/inputmask';
import { HTMLFormComponent, FormComponentKeySupport } from "fh-forms-handler";
declare class InputText extends HTMLFormComponent {
    protected keySupport: FormComponentKeySupport;
    private readonly isTextarea;
    private readonly textareaAutosize;
    private readonly inputType;
    protected keySupportCallback: any;
    private valueChanged;
    private readonly maskDefinition;
    protected mask: any;
    private lastValidMaskedValue;
    protected placeholder: any;
    private readonly maxLength;
    protected onInput: any;
    protected onChange: any;
    protected maskDynamic: boolean;
    protected readonly textAlign: string;
    private readonly height;
    protected format: string;
    private timeoutFunction;
    private readonly inputTimeout;
    protected inputmaskEnabled: boolean;
    protected maskPlugin: any;
    protected maskInsertMode: boolean;
    input: any;
    constructor(componentObj: any, parent: HTMLFormComponent);
    create(): void;
    protected createIcon(): void;
    protected getMainComponent(): any;
    onInputEvent(): void;
    onChangeEvent(): void;
    forceKeepMaskValid(): void;
    defineDefinitionSymbols(): void;
    protected disableMask(): void;
    protected applyMask(): void;
    handleContainerOverflow(parent: JQuery<any>, autocompleter: any, up?: boolean): void;
    removeInputPlaceholder(event: any): void;
    createCustomDefinitionSymbols(): {};
    update(change: any): void;
    updateModel(): void;
    extractChangedAttributes(): {};
    wrap(skipLabel: any, isInputElement: any): void;
    setAccessibility(accessibility: any): void;
    protected makePlaceholder(format: string): any;
    destroy(removeFromParent: any): void;
    getDefaultWidth(): string;
}
export { InputText };
