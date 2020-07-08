import { HTMLFormComponent } from "fh-forms-handler";
import { ColumnOptimized } from "../Optimized/ColumnOptimized";
import { Column } from "../Column";
declare abstract class TableFixedHeaderAndHorizontalScroll extends HTMLFormComponent {
    components: ColumnOptimized[] & Column[];
    protected abstract table: HTMLTableElement;
    protected abstract header: HTMLTableSectionElement;
    protected fixedHeader: boolean;
    protected fixedHeaderWrapper: HTMLDivElement;
    protected initFixedHeader: boolean;
    protected clonedTable: any;
    protected lastThColumn: any;
    protected scrollbarWidth: number;
    protected lastThWidth: number;
    protected thElm: any;
    horizontalScrolling: boolean;
    protected startPageX: any;
    protected startOffset: any;
    protected secondOffset: any;
    protected initColumnResize: boolean;
    constructor(componentObj: any, parent: HTMLFormComponent);
    protected initExtends(): void;
    protected initFixedHeaderAndScrolling(): void;
    protected wrap(skipLabel?: boolean): void;
    calculateColumnWidths(): void;
    private calculateColumnWidth;
    private setColumnOriginalWidth;
    private calculateColumnsWidth;
    recalculateColumnWidths(): void;
    handleFixedHeader(): void;
    handleColumnResize(): void;
    recalculateGripHeight(): void;
    setAccessibility(accessibility: any): void;
    updateFixedHeaderWidth(): void;
    refreashHeader(): void;
    scrolToRow(jQueryRowObject: any, animate?: boolean): void;
    scrollTopInside(): void;
    /**
     * Function helps get next or prevoius Visible Sibling.
     * @param column
     */
    getVisiableSibling(htmlElement: any): any;
    private isVisiable;
    convertToPrecentageWidth(widthInPx: any): string;
}
export { TableFixedHeaderAndHorizontalScroll };
