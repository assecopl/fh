import 'bootstrap/js/dist/dropdown';
import { HTMLFormComponent } from "fh-forms-handler";
import { AdditionalButton } from "fh-forms-handler";
declare class Dropdown extends HTMLFormComponent {
    protected button: any;
    protected dropdown: any;
    private menu;
    private style;
    private readonly onClick;
    constructor(componentObj: any, parent: HTMLFormComponent);
    create(): void;
    onButtonClickEvent(event: any): void;
    update(change: any): void;
    resolveLabelAndIcon(label: any): any;
    addCaret(): void;
    getAdditionalButtons(): AdditionalButton[];
    wrap(skipLabel: any): void;
    setAccessibility(accessibility: any): void;
    destroy(removeFromParent: boolean): void;
    /**
     * @Override
     */
    getDefaultWidth(): string;
}
export { Dropdown };
