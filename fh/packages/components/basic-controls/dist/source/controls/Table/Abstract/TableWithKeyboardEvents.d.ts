import { HTMLFormComponent } from "fh-forms-handler";
import { TableFixedHeaderAndHorizontalScroll } from "./TableFixedHeaderAndHorizontalScroll";
import { TableRowOptimized } from "./../Optimized/TableRowOptimized";
declare abstract class TableWithKeyboardEvents extends TableFixedHeaderAndHorizontalScroll {
    /**
     * Rewrited logic
     */
    protected readonly selectable: boolean;
    protected readonly onRowClick: any;
    protected ctrlIsPressed: any;
    protected rows: Array<TableRowOptimized>;
    protected multiselect: boolean;
    protected keyEventTimer: any;
    protected doneEventInterval: number;
    private loopDown;
    constructor(componentObj: any, parent: HTMLFormComponent);
    protected initExtends(): void;
    private tableKeydownEvent;
    tableKeyupEvent(e: any): void;
    tableMousedownEvent(event: any): void;
    protected bindKeyboardEvents(): void;
    /**
     * Used For Optimized Tables
     * @param scrollAnimate
     */
    protected highlightSelectedRows(scrollAnimate?: boolean): void;
    update(change: any): void;
    destroy(removeFromParent: any): void;
    keyEventLoopDownTurnOff(): void;
}
export { TableWithKeyboardEvents };
