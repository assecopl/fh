import { HTMLFormComponent } from "fh-forms-handler";
import { Table } from "./Table";
import { TableOptimized } from "./Optimized/TableOptimized";
declare class TableRow extends HTMLFormComponent {
    protected readonly mainId: string;
    readonly parent: TableOptimized;
    protected isEmpty: any;
    protected onRowClickEvent: () => void;
    constructor(componentObj: any, parent: Table);
    create(): void;
    addComponent(componentObj: any, cellTypeCheck?: string): void;
    highlightRow(scrollAnimate?: boolean): void;
}
export { TableRow };
