import { HTMLFormComponent } from "fh-forms-handler";
declare class TableLazy extends HTMLFormComponent {
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
    private readonly multiselect;
    private readonly selectionCheckboxes;
    private selectionChanged;
    private readonly selectable;
    private ctrlIsPressed;
    totalColumns: number;
    private fixedHeader;
    protected ieFocusFixEnabled: boolean;
    protected table: HTMLTableElement;
    protected header: HTMLTableSectionElement;
    protected footer: HTMLTableSectionElement;
    private _dataWrapper;
    private sortedBy;
    private loadInfo;
    private startSize;
    private sortDirection;
    private readonly onLoadMore;
    private readonly totalRows;
    private loadMorePage;
    private readonly paginatorOffset;
    private loader;
    private lazyTr;
    private adder;
    private loadAllPages;
    private onlyNew;
    constructor(componentObj: any, parent: HTMLFormComponent);
    clearRows(): void;
    create(): void;
    toInt: (stringOrInt: any) => any;
    extractChangedAttributes(): {};
    static sleep(ms: any): Promise<unknown>;
    update(change: any): void;
    sleep(milliseconds: any): Promise<unknown>;
    loadAll(adder?: any): void;
    /**
     * @override
     * Override main function to add new rows without delete old ones.
     * @param clearSelection
     */
    refreshData(clearSelection?: any): void;
    createLoader(): void;
    readonly dataWrapper: HTMLTableSectionElement;
}
export { TableLazy };
