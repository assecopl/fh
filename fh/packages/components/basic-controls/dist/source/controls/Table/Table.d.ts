import { AdditionalButton, HTMLFormComponent } from "fh-forms-handler";
import { TableWithKeyboardEvents } from "./Abstract/TableWithKeyboardEvents";
declare class Table extends TableWithKeyboardEvents {
    protected readonly visibleRows: any;
    protected readonly tableData: any;
    protected rows: Array<any>;
    protected readonly rowIndexMappings: any;
    private readonly rowStylesMapping;
    private readonly minRows;
    private readonly rowHeight;
    private readonly tableGrid;
    private readonly tableStripes;
    protected readonly onRowClick: any;
    private readonly onRowDoubleClick;
    private readonly selectionCheckboxes;
    private selectionChanged;
    totalColumns: number;
    protected ieFocusFixEnabled: boolean;
    protected table: HTMLTableElement;
    protected header: HTMLTableSectionElement;
    protected footer: HTMLTableSectionElement;
    private _dataWrapper;
    constructor(componentObj: any, parent: HTMLFormComponent);
    create(): void;
    update(change: any): void;
    onRowClickEvent(event: any): void;
    addRow(rowObj: any): void;
    deleteRow(row: any): void;
    clearRows(): void;
    redrawColumns(): void;
    addComponent(componentObj: any): void;
    refreshData(clearSelection?: any): void;
    addMinRowRows(): void;
    removeMinRowRows(): void;
    countVisibleColumns(): number;
    /**
     * @override
     * Used for standard tables
     * @param scrollAnimate
     */
    highlightSelectedRows(scrollAnimate?: boolean): void;
    collectAllChanges(): any;
    applyChange(change: any): void;
    selectRow(mainId: any): void;
    extractChangedAttributes(): {};
    getAdditionalButtons(): AdditionalButton[];
    setPresentationStyle(presentationStyle: any): void;
    accessibilityResolve(node: HTMLElement, access: string): void;
    destroy(removeFromParent: any): void;
    get dataWrapper(): HTMLTableSectionElement;
    render(): void;
}
export { Table };
