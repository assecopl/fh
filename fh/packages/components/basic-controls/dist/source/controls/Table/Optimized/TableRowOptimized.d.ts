import { TableOptimized } from "./TableOptimized";
import { TableRow } from "../TableRow";
declare class TableRowOptimized extends TableRow {
    readonly parent: TableOptimized;
    constructor(componentObj: any, parent: TableOptimized & any);
    create(): void;
    addComponent(componentObj: any): void;
    /**
     * @Override
     * @param accessibility
     */
    display(): void;
}
export { TableRowOptimized };
