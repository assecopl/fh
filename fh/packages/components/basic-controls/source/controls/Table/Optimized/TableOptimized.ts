import {FhContainer, HTMLFormComponent} from "fh-forms-handler";
import {TableWithKeyboardEvents} from "./../Abstract/TableWithKeyboardEvents";
import {TableRowOptimized} from "./TableRowOptimized";

class TableOptimized extends TableWithKeyboardEvents {
    protected readonly visibleRows: any;
    protected readonly tableData: any[] = null;
    protected rows: Array<TableRowOptimized> = [];
    protected readonly rowIndexMappings: any;
    protected readonly minRows: number = null;
    protected readonly startSize: number = null;
    protected firstShow: boolean = false;
    private readonly tableStripes: any;

    public totalColumns: number;

    protected ieFocusFixEnabled: boolean;
    public table: HTMLTableElement;
    protected header: HTMLTableSectionElement;
    protected footer: HTMLTableSectionElement = null;

    private _dataWrapper: any;

    private sortedBy: any;
    private loadInfo: HTMLElement;
    private sortDirection: any;

    private timeoutedRows:Map<any, any> =  new Map<any, any>();

    /**
     * Rewrited logic
     */


    constructor(componentObj: any, parent: HTMLFormComponent) {
        super(componentObj, parent);

        this.tableStripes = componentObj.tableStripes || 'show';
        this.visibleRows = componentObj.displayedRowsCount || 0;
        this.startSize = componentObj.startSize || null;
        this.minRows = componentObj.minRows || null;

        this.tableData = componentObj.tableRows;
        this.ieFocusFixEnabled = this.componentObj.ieFocusFixEnabled || false;
    }

    clearRows() {
        this.rows.forEach(function (row) {
            row.destroy()
        }.bind(this));
        this.rows = [];
    };

    clearTimeouts() {
        this.timeoutedRows.forEach(function (id, key) {
           clearTimeout(id);
        }.bind(this));
        this.timeoutedRows.clear();
    }

    create() {

        let table = document.createElement('table');

        //Tabindex need to be set if we want to capture keybord events.
        table.tabIndex = 0;
        table.id = this.id;
        //
        table.className = 'fc table table-bordered';
        if (!this.rowIndexMappings) {

            if (this.tableStripes.toLowerCase() === 'hide') {
                table.className += ' table-hover';
            } else {
                table.className += ' table-striped table-hover';
            }
        }
        let heading = document.createElement('thead');
        let headingRow = document.createElement('tr');
        heading.appendChild(headingRow);
        this.header = heading;

        let body = document.createElement('tbody');

        table.appendChild(heading);
        table.appendChild(body);


        this.table = table;
        let div = document.createElement('div');
        table.style.tableLayout = 'fixed';
        div.appendChild(table);


        this.component = div;


        this.wrap(false);

        this.contentWrapper = headingRow;
        this._dataWrapper = body;

        if (this.componentObj.columns) {
            this.addComponents(this.componentObj.columns);
        }

        this.refreshData();


        this.display();
        this.initExtends();

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
                        this.refreshData(true);
                    case 'selectedRowNumber':
                        this.rawValue = change.changedAttributes['selectedRowNumber'];
                        this.highlightSelectedRows();
                        break;
                }
            }.bind(this));
        }
    }

    sleep(milliseconds) {
        return new Promise(function (resolve) {
            return setTimeout(resolve, milliseconds)
        })
    }

    /**
     * @override
     * Override main function to add new rows without delete old ones.
     * @param clearSelection
     */
    refreshData(clearSelection = undefined) {
        //Clear timeouts in case new rows will be loaded before last load finish.
        this.clearTimeouts();
        if (clearSelection) {
            this.rawValue = [];
            this.changesQueue.queueValueChange(null);
        }
        this.clearRows();
        this.tableData != null ? this.tableData.forEach(function (row: any, index: number) {
            let rowData = {
                id: this.id + '_row_' + (index),
                mainId: (index),
                empty: row.empty,
                data: row.tableCells,
                styleClasses: "row_" + index,
                onRowClickEvent: null
            };
            if (this.selectable && this.onRowClick) {
                rowData.onRowClickEvent = this.onRowClickEvent.bind(this, event, index)
            }

            this.addRow(rowData, index);
        }.bind(this)) : null;

        this.firstShow = true;
        this.addMinRowRows();

    };

    public get dataWrapper(): HTMLDivElement {
        return this._dataWrapper;
    }

    public display() {
        this.addStyles();
        super.display();
    }

    /**
     * OVERRIDES
     * @param componentsList
     */

    deleteRow(row) {
        row.components.forEach(column => {
            column._parent = null;
            column._parent = null;
            column.contentWrapper = null;
            column.container = null;
            if (row._dataWrapper) {
                row._dataWrapper.removeChild(column.htmlElement);
            }
            column.destroy();
        });
        row.components = [];
        row.component = null;
        row._parent = null;
        row.contentWrapper = null;
        row.container = null;
        row.htmlElement.removeEventListener('click', this.onRowClickEvent.bind(this));

        $(row.htmlElement).unbind().remove();
        row.htmlElement = null;
        row.destroy();
    };

    removeMinRowRows() {
        if (this.rows.length > this.visibleRows) {
            let i;
            for (i = this.visibleRows; i < this.rows.length; i++) {
                this.deleteRow(this.rows[i]);
            }
            this.rows = this.rows.splice(0, this.visibleRows);
        }
    };

    addMinRowRows() {
        if (this.visibleRows < this.minRows) {
            let visibleColumnsCount = this.countVisibleColumns();

            for (let i = this.visibleRows; i < this.minRows; i++) {
                let rowData = {
                    id: i,
                    mainId: i,
                    data: Array.apply(null, new Array(visibleColumnsCount)).map(function (a, b) {
                        return {
                            type: 'OutputLabel',
                            id: this.id + 'EmptyRow' + i + '_' + b,
                            value: '&nbsp;'
                        };
                    }.bind(this))
                };
                this.addRow(rowData, i);
                let row = this.rows[i];
                row.htmlElement.style.pointerEvents = 'none';
                row.htmlElement.classList.add('emptyRow');
                row.htmlElement.style.backgroundColor = null;
            }
            this.recalculateGripHeight();
        }
    };

    applyChange(change) {
        if (this.id === change.formElementId) {
            this.update(change);
        } else {
            this.rows.forEach(function (row) {
                row.applyChange(change);
            });
            this.components.forEach(function (component) {
                component.applyChange(change);
            });
        }
    };

    collectAllChanges() {
        let allChanges = [];

        this.rows.forEach(function (component) {
            let changes = component.collectAllChanges();
            allChanges = allChanges.concat(changes);
        }.bind(this));

        return this.collectChanges(allChanges);
    };

    countVisibleColumns() {
        let result = 0;

        this.components.forEach(function (c: any) {
            if (c.componentObj != null && c.componentObj.type.startsWith('Column') && c.accessibility !== 'HIDDEN') {
                result++;
            }
        });

        return result;
    };

    addRow(rowData, index) {

        let row = (<any>FhContainer.get('TableRowOptimized'))(rowData, this);
        this.rows.push(row);

        let last = this.rows.length - 1;

        let func = null;

        if (!this.horizontalScrolling && (index + 1) == this.visibleRows) {
            func = this.recalculateGripHeight.bind(this);
        }

        /**
         * Oddelegowanie ładowania rekordów większych niż "startSize" w czasie.
         * Oddelegowanie następuje tylko dla pierwszego renderowania tabeli.
         * Przy aktualizacji rekordy dodawane są od razu.
         */
        if (this.startSize && index > this.startSize && !this.firstShow) {
          const timeOutId =   setTimeout(function (row, callback, updateFixedHeaderWidth) {
                row.create();
                updateFixedHeaderWidth();
                if (callback) {
                    callback();
                }
                //Delete row timeout from list after finish loading.
                this.timeoutedRows.delete(row.id);
            }.bind(this), (100 * (index)), row, func, this.updateFixedHeaderWidth.bind(this));
            this.timeoutedRows.set(row.id, timeOutId);
        } else {
            row.create();
        }
    };


    onRowClickEvent(event, index) {
        if (this.accessibility != 'EDIT') return;

        let mainId = index;

        if (this.multiselect == false) {
            if (this.ctrlIsPressed) {
                if (this.rawValue.indexOf(mainId) !== -1) {
                    this.rawValue = [];
                    this.rawValue.push(-1);
                }
            } else {
                this.rawValue = [];
                this.rawValue.push(mainId);
            }
        } else {
            if (this.ctrlIsPressed) {
                this.selectRow(mainId);
            } else {
                this.rawValue = [];
                this.rawValue.push(mainId);
            }
        }

        this.changesQueue.queueValueChange(this.rawValue);

        if (this._formId === 'FormPreview') {
            this.fireEvent('onRowClick', this.onRowClick);
        } else {
            this.fireEventWithLock('onRowClick', this.onRowClick);
        }
    }

    private selectRow(mainId) {
        let index = this.rawValue.indexOf(parseInt(mainId));
        if (index == -1) {
            this.rawValue.push(mainId);
        } else if (index != -1) {
            this.rawValue.splice(index, 1);
            this.rawValue.filter(idx => idx > -1);
            if (this.rawValue.length == 0) {
                this.rawValue.push(-1);
            }
        }
    };

    redrawColumns() {
        this.calculateColumnWidths();

        // if minRows is present
        if (this.minRows !== null && this.rows.length > 0) {
            this.removeMinRowRows();
            this.addMinRowRows();
        }
    };

    destroy(removeFromParent: boolean) {
        //Stop loading columns
       this.clearTimeouts();

        super.destroy(removeFromParent);
    }


}

export {TableOptimized};
