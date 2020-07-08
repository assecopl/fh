import { HTMLFormComponent } from "fh-forms-handler";
import { AdditionalButton } from "fh-forms-handler";
declare class Column extends HTMLFormComponent {
    private readonly isSortable;
    private sorter;
    private readonly rowspan;
    private readonly subColumnsExists;
    private subelements;
    private colspan;
    private subcomponents;
    constructor(componentObj: any, parent: HTMLFormComponent);
    create(): void;
    calculateColspan(): void;
    setAccessibility(accessibility: any): void;
    setColspan(colspan: any): void;
    update(change: any): void;
    getDefaultWidth(): any;
    getAdditionalButtons(): AdditionalButton[];
    columnClickEvent(): void;
    destroy(removeFromParent: boolean): void;
}
export { Column };
