import { HTMLFormComponent } from "fh-forms-handler";
import { AdditionalButton } from "fh-forms-handler";
declare class ColumnOptimized extends HTMLFormComponent {
    private readonly isSortable;
    private sorter;
    private readonly rowspan;
    private readonly subColumnsExists;
    private subelements;
    private colspan;
    private subcomponents;
    private fixedHeader;
    constructor(componentObj: any, parent: HTMLFormComponent);
    create(): void;
    /**
     * @Override
     * @param accessibility
     */
    display(): void;
    setAccessibility(accessibility: any): void;
    update(change: any): void;
    getDefaultWidth(): any;
    destroy(removeFromParent: boolean): void;
    getAdditionalButtons(): AdditionalButton[];
}
export { ColumnOptimized };
