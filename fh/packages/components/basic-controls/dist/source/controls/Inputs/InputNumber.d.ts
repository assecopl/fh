import 'imports-loader?moment,define=>false,exports=>false!../../external/inputmask';
import { HTMLFormComponent } from "fh-forms-handler";
declare class InputNumber extends HTMLFormComponent {
    private input;
    private keySupport;
    protected keySupportCallback: any;
    private isTextarea;
    private placeholder;
    private maxLength;
    private onInput;
    private onChange;
    private valueChanged;
    protected inputmaskEnabled: boolean;
    constructor(componentObj: any, parent: HTMLFormComponent);
    create(): void;
    applyMask(): void;
    disableMask(): void;
    onChangeEvent(): void;
    onInputEvent(): void;
    protected createIcon(): void;
    update(change: any): void;
    updateModel(): void;
    extractChangedAttributes(): {};
    wrap(skipLabel: any): void;
    setAccessibility(accessibility: any): void;
    destroy(removeFromParent: any): void;
}
export { InputNumber };
