import { HTMLFormComponent } from "fh-forms-handler";
import { TableWithKeyboardEvents } from "./../Abstract/TableWithKeyboardEvents";
import { TableRowOptimized } from "./TableRowOptimized";
declare class TableOptimized extends TableWithKeyboardEvents {
    protected readonly visibleRows: any;
    protected readonly tableData: any[];
    protected rows: Array<TableRowOptimized>;
    protected readonly rowIndexMappings: any;
    protected readonly minRows: number;
    protected readonly startSize: number;
    protected firstShow: boolean;
    private readonly tableStripes;
    totalColumns: number;
    protected ieFocusFixEnabled: boolean;
    table: HTMLTableElement;
    protected header: HTMLTableSectionElement;
    protected footer: HTMLTableSectionElement;
    private _dataWrapper;
    private sortedBy;
    private loadInfo;
    private sortDirection;
    /**
     * Rewrited logic
     */
    constructor(componentObj: any, parent: HTMLFormComponent);
    clearRows(): void;
    create(): void;
    toInt: (stringOrInt: any) => any;
    extractChangedAttributes(): {};
    update(change: any): void;
    sleep(milliseconds: any): Promise<unknown>;
    /**
     * @override
     * Override main function to add new rows without delete old ones.
     * @param clearSelection
     */
    refreshData(clearSelection?: any): void;
    readonly dataWrapper: HTMLDivElement;
    /**
     * Turn on render for designer purpose
     */
    render(): void;
    display(): void;
    /**
     * OVERRIDES
     * @param componentsList
     */
    deleteRow(row: any): void;
    removeMinRowRows(): void;
    addMinRowRows(): void;
    applyChange(change: any): void;
    collectAllChanges(): any;
    countVisibleColumns(): number;
    addRow(rowData: any, index: any): void;
    onRowClickEvent(index: any): void;
    private selectRow;
    redrawColumns(): void;
}
export { TableOptimized };
