import { HTMLFormComponent } from "fh-forms-handler";
declare class OptionsListElement extends HTMLFormComponent {
    private readonly onIconClick;
    private readonly onChange;
    private isChecked;
    private isTitle;
    private canCheck;
    private checked;
    private checkBox;
    constructor(componentObj: any, parent: HTMLFormComponent);
    create(): void;
    destroy(removeFromParent: any): void;
    toogleElement(element: any): void;
    toogleCheckOneCheckBox(listComponent: any, elementHtml: any, elementCheckboxHtml: any, forcedValue?: any): void;
    toggleCheckAll(listContainer: any, listElement: any, titleCheckboxHtml: any): void;
}
export { OptionsListElement };
