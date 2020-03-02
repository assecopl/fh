import { FormComponent } from "fh-forms-handler";
declare class KeyboardEvent extends FormComponent {
    private readonly shortcut;
    private readonly event;
    constructor(componentObj: any, parent: FormComponent);
    destroy(removeFromParent: any): void;
}
export { KeyboardEvent };
