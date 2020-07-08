import { HTMLFormComponent } from "fh-forms-handler";
import { AdditionalButton } from "fh-forms-handler";
declare class ColumnLazy extends HTMLFormComponent {
    private isSortable;
    private rowspan;
    private sorter;
    private subColumnsExists;
    private subelements;
    constructor(componentObj: any, parent: HTMLFormComponent);
    create(): void;
    setSorter(parentObject: any, columnObject: any, columnElement: any): void;
    setSubcolumns(parentObject: any, columnObject: any, columnElement: any, currentRow: any, callback: any): number;
    update(change: any): void;
    getDefaultWidth(): any;
    getAdditionalButtons(): AdditionalButton[];
}
export { ColumnLazy };
