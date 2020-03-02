import { HTMLFormComponent } from "fh-forms-handler";
import { TableOptimized } from "./TableOptimized";
declare class TableRowOptimized extends HTMLFormComponent {
    private readonly mainId;
    private isEmpty;
    readonly parent: TableOptimized;
    private onRowClickEvent;
    constructor(componentObj: any, parent: TableOptimized & any);
    create(): void;
    addComponent(componentObj: any): void;
    /**
     * @Override
     * @param accessibility
     */
    display(): void;
    render(): void;
    highlightRow(scrollAnimate?: boolean): void;
}
export { TableRowOptimized };
