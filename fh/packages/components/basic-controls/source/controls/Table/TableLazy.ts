import {TableLazyPL} from './i18n/TableLazy.pl';
import {TableLazyEN} from './i18n/TableLazy.en';
import {Table} from "./Table";
import {FhContainer, HTMLFormComponent} from "fh-forms-handler";

class TableLazy extends HTMLFormComponent {
    protected readonly visibleRows: any;
    protected readonly tableData: any;
    protected rows: Array<any> = [];
    protected readonly rowIndexMappings: any;
    private readonly rowStylesMapping: any;
    private readonly minRows: any;
    private readonly rowHeight: any;
    private readonly tableGrid: any;
    private readonly tableStripes: any;
    protected readonly onRowClick: any;
    private readonly onRowDoubleClick: any;
    private readonly multiselect: any;
    private readonly selectionCheckboxes: any;
    private selectionChanged: any;
    private readonly selectable: boolean;
    private ctrlIsPressed: any;
    public totalColumns: number;
    private fixedHeader: boolean;
    protected ieFocusFixEnabled: boolean;
    protected table: HTMLTableElement;
    protected header: HTMLTableSectionElement;
    protected footer: HTMLTableSectionElement = null;
    private _dataWrapper: HTMLTableSectionElement;
    private sortedBy: any;
    private loadInfo: HTMLElement;
    private startSize: number;
    private sortDirection: any;

    private readonly onLoadMore: any;
    private readonly totalRows: number;
    private loadMorePage: any;
    private readonly paginatorOffset: any;
    private loader: HTMLElement;
    private lazyTr: HTMLElement;
    private adder: HTMLElement = null;
    private loadAllPages: boolean;
    private onlyNew: boolean = true;

    constructor(componentObj: any, parent: HTMLFormComponent) {
        super(componentObj, parent);

        this.i18n.registerStrings('en', TableLazyEN);
        this.i18n.registerStrings('pl', TableLazyPL);

        // this.selectable = this.componentObj.selectable || true;
        this.visibleRows = this.componentObj.displayedRowsCount || 0;
        this.tableData = this.componentObj.tableRows;
        this.rows = [];
        this.rowIndexMappings = this.componentObj.rowIndexMappings || null;
        this.rowStylesMapping = this.componentObj.rowStylesMapping || [];
        this.minRows = this.componentObj.minRows || null;
        // this.fixedHeader = this.componentObj.fixedHeader || false;
        this.rowHeight = this.componentObj.rowHeight || 'normal';
        this.tableGrid = this.componentObj.tableGrid || 'show';
        this.tableStripes = this.componentObj.tableStripes || 'show';
        // this.ieFocusFixEnabled = this.componentObj.ieFocusFixEnabled || false;
        // this.rawValue = this.componentObj.rawValue || this.componentObj.selectedRowsNumbers || [];

        this._dataWrapper = null;
        // this.onRowClick = this.componentObj.onRowClick;
        // this.onRowDoubleClick = this.componentObj.onRowDoubleClick;
        // this.multiselect = this.componentObj.multiselect || false;
        // this.selectionCheckboxes = this.componentObj.selectionCheckboxes || false;
        this.selectionChanged = false;
        // this.ctrlIsPressed = false;

        this.componentObj.verticalAlign = this.componentObj.verticalAlign || 'top';
        this.totalColumns = 0;

        this.table = null;

        this.loadInfo = null;
        this.totalRows = this.componentObj.totalRows;
        this.loadMorePage = this.componentObj.loadable.pageNumber || 0;
        this.paginatorOffset = 2;
        this.onLoadMore = this.componentObj.onLoadMore;
        this.loadAllPages = this.componentObj.loadAllPages;

        this.sortedBy = null;
        this.sortDirection = null;

        this.startSize = this.componentObj.loadable.pageSize || 9999;
    }

    clearRows() {
        this.rows.forEach(function (row) {
            row.destroy()
        }.bind(this));
        this.rows = [];
    };


    create() {
        let table = document.createElement('table');
        table.classList.add('table')

        table.id = this.id;
        table.tabIndex = 0;
        //
        table.classList.add('fc');
        table.classList.add('table','table-hover','table-bordered');
        let heading = document.createElement('thead');
        let headingRow = document.createElement('tr');
        heading.appendChild(headingRow);
        this.header = heading;

        let body = document.createElement('tbody');

        table.appendChild(heading);
        table.appendChild(body);


         this.table = table;
        //
         let div = document.createElement('div');

        table.style.tableLayout = 'fixed';
         div.appendChild(table);
         this.component = div;

         this.wrap();
        //
        this.contentWrapper = headingRow;
        this._dataWrapper = body;
        //
        this.addStyles();
         this.display();
        //
        //
        if (this.componentObj.columns) {
            this.totalColumns = this.componentObj.columns.length;
            this.addComponents(this.componentObj.columns);
        }

        this.refreshData();
        this.createLoader();
        if (this.loadAllPages && !this.designMode && this.visibleRows < this.totalRows) {
            /**
             * On designer there is no need to show all data.
             */
            this.loadAll();
        }

    };

    toInt = function (stringOrInt) {
        if (typeof stringOrInt == 'string') {
            return parseInt(stringOrInt);
        } else {
            return stringOrInt;
        }
    };

    extractChangedAttributes() {
        return this.changesQueue.extractChangedAttributes();
    };

    static sleep(ms) {
        return new Promise(resolve => setTimeout(resolve, ms));
    }

    update(change) {
            super.update(change);

            if (change.changedAttributes) {
                $.each(change.changedAttributes, function (name, newValue) {
                    switch (name) {
                        case 'rowIndexMappings':
                            this.rowIndexMappings = newValue;
                            this.refreshData(true);
                            break;
                        case 'displayedRowsCount':
                            this.visibleRows = newValue;
                            this.tableData = change.changedAttributes['tableRows'];
                            if(newValue == this.totalRows){
                                if( this.adder) {
                                    this.adder.remove();
                                    this.adder = null;
                                }
                                $(this.footer).fadeOut();
                            }
                            this.refreshData(true);
                            break;
                    }
                }.bind(this));
            }
        if (this.loadAllPages && this.visibleRows < this.totalRows) {
            /**
             * On designer there is no need to show all data.
             */
            setTimeout( function () {
                this.loadAll()
            }.bind(this)  , 400);
        }

    }

     sleep(milliseconds) {
        return new Promise(function (resolve) { return setTimeout(resolve, milliseconds)})
    }

    loadAll(adder = null) {
        if (this.visibleRows < this.totalRows) {
            new Promise(function (resolve, reject) {
                if(this.adder) {
                    this.adder.style.display = 'none';
                }

                $(this.loader).show();
                // this.changesQueue.queueAttributeChange('loadAllPages', true);
                this.loadMorePage = this.toInt(this.loadMorePage) + 1;
                this.changesQueue.queueAttributeChange('loadMorePage', this.loadMorePage);
                this.fireEvent('onLoadMore', this.onLoadMore);

                return this.sleep(500).then(function () {
                    return resolve(true);
                });
            }.bind(this)).then(function () {
                $(this.loader).fadeOut('slow',function () {
                    if(this.adder) {
                        this.adder.style.display = 'inline';
                    }
                }.bind(this));
            }.bind(this)).catch((e: any) => {
                this.loader.innerHTML = "<p>" + this.i18n.__('lazy_loading_fail') + "</p>";
            });
        } else {
            $(this.footer).fadeOut('slow');
        }
    }

    /**
     * @override
     * Override main function to add new rows without delete old ones.
     * @param clearSelection
     */
    refreshData(clearSelection = undefined) {
        if (clearSelection) {
            this.rawValue = [];
            this.changesQueue.queueValueChange(null);
        }
        // this.clearRows()
        const currentRowsCount = this.rows.length;
        for (let i = currentRowsCount; i < this.visibleRows; i++) {
                let row = this.tableData[i];
                let rowData = {
                    id: this.id + '_row_' + (i),
                    mainId: (i),
                    empty: row.empty,
                    data: row.tableCells
                };

                let rowObj = (<any>FhContainer.get('TableRow'))(rowData, this);
                this.rows.push(rowObj);
                rowObj.create();
        }

    };


    createLoader() {
        let footer: any = null;
        const loader = document.createElement("tr");
        const loadertd = document.createElement("td");
        const loaderspan = document.createElement("span");
        loadertd.classList.add("py-4");
        loadertd.classList.add("p-2");
        loadertd.classList.add("text-center");
        loadertd.colSpan = this.totalColumns;
        loaderspan.innerHTML = "<i class='fas fa-spinner fa-spin' style='font-size: 30px'></i>";
        loadertd.appendChild(loaderspan);
        loader.appendChild(loadertd);
        this.loader = loaderspan;
        this.lazyTr = loader;
        this.lazyTr.style.display = 'none';
        this.loader.style.display = 'none';

        if(!this.loadAllPages && this.visibleRows < this.totalRows){
            const adder = document.createElement("span");
            loadertd.style.cursor = "pointer";
            adder.innerHTML = "<i class='fas fa-plus' style='font-size: 30px;'></i>";

            loadertd.appendChild(adder);
            this.adder = adder;
            this.adder.style.display = 'inline';
        }
        if(this.visibleRows < this.totalRows){
            this.lazyTr.style.display = 'table-row';
        }


            footer = document.createElement("tfoot");
            footer.appendChild(loader);
            this.footer = footer;
            this.table.appendChild(footer);

        if(this.adder){
            this.adder.addEventListener('click', this.loadAll.bind(this, this.adder));
        }
    }


    public get dataWrapper(): HTMLTableSectionElement {
        return this._dataWrapper;
    }

}

export {TableLazy};