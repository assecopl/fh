import { AdditionalButton, HTMLFormComponent, FormComponent } from "fh-forms-handler";
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
    private readonly synchronizeScrolling;
    private selectionChanged;
    totalColumns: number;
    protected ieFocusFixEnabled: boolean;
    protected table: HTMLTableElement;
    protected header: HTMLTableSectionElement;
    protected footer: HTMLTableSectionElement;
    private _dataWrapper;
    private checkAllArray;
    constructor(componentObj: any, parent: HTMLFormComponent);
    create(): void;
    update(change: any): void;
    onRowClickEvent(event: any, mainId: any): void;
    addRow(rowObj: any): void;
    deleteRow(row: any): void;
    clearRows(): void;
    redrawColumns(): void;
    addComponent(componentObj: any): void;
    refreshData(clearSelection?: any): void;
    addMinRowRows(): void;
    removeMinRowRows(): void;
    countVisibleColumns(): number;
    collectAllChanges(): any;
    applyChange(change: any): void;
    selectRow(mainId: any): void;
    selectAllRows(selectOrClear: any): void;
    extractChangedAttributes(): {};
    getAdditionalButtons(): AdditionalButton[];
    setPresentationStyle(presentationStyle: any): void;
    accessibilityResolve(node: HTMLElement, access: string): void;
    destroy(removeFromParent: any): void;
    readonly dataWrapper: HTMLTableSectionElement;
    render(): void;
    protected getAllComponents(): FormComponent[];
    /**
     * Ads Cell to header with checkbox for selecting all records on current page.
     */
    protected addCheckAllCell(): void;
}
export { Table };
