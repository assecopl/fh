import { HTMLFormComponent } from "fh-forms-handler";
import { AdditionalButton } from "fh-forms-handler";
declare class Button extends HTMLFormComponent {
    private readonly style;
    private readonly onClick;
    private ButtonPL;
    private ButtonEN;
    constructor(componentObj: any, parent: HTMLFormComponent);
    create(): void;
    onClickEvent(event: any): void;
    update(change: any): void;
    setAccessibility(accessibility: any): void;
    resolveLabelPosition(): void;
    wrap(skipLabel: any): void;
    resolveLabelAndIcon(label: any): any;
    extractChangedAttributes(): {};
    getAdditionalButtons(): AdditionalButton[];
    destroy(removeFromParent: any): void;
    /**
     * @Override
     */
    getDefaultWidth(): string;
}
export { Button };
