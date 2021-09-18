import {AdditionalButton, HTMLFormComponent, FhContainer} from "fh-forms-handler";
import {TableFixedHeaderAndHorizontalScroll} from "./Abstract/TableFixedHeaderAndHorizontalScroll";

class Table extends TableFixedHeaderAndHorizontalScroll {
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
    protected ieFocusFixEnabled: boolean;
    protected table: HTMLTableElement;
    protected header: HTMLTableSectionElement;
    protected footer: HTMLTableSectionElement = null;
    private _dataWrapper: HTMLTableSectionElement;

    private keyEventTimer: any;                //timer identifier
    private doneEventInterval: number = 500;   //event delay in miliseconds

    constructor(componentObj: any, parent: HTMLFormComponent) {
        super(componentObj, parent);
        this.selectable = this.componentObj.selectable || true;
        this.visibleRows = this.componentObj.displayedRowsCount || 0;
        this.tableData = this.componentObj.tableRows;
        this.rows = [];
        this.rowIndexMappings = this.componentObj.rowIndexMappings || null;
        this.rowStylesMapping = this.componentObj.rowStylesMapping || [];
        this.minRows = this.componentObj.minRows || null;
        this.rowHeight = this.componentObj.rowHeight || 'normal';
        this.tableGrid = this.componentObj.tableGrid || 'hide';
        this.tableStripes = this.componentObj.tableStripes || 'hide';
        this.ieFocusFixEnabled = this.componentObj.ieFocusFixEnabled || false;
        this.rawValue = this.componentObj.rawValue || this.componentObj.selectedRowsNumbers || [];

        this._dataWrapper = null;
        this.onRowClick = this.componentObj.onRowClick;
        this.onRowDoubleClick = this.componentObj.onRowDoubleClick;
        this.multiselect = this.componentObj.multiselect || false;
        this.selectionCheckboxes = this.componentObj.selectionCheckboxes || false;
        this.selectionChanged = false;
        this.ctrlIsPressed = false;

        this.componentObj.verticalAlign = this.componentObj.verticalAlign || 'top';
        this.totalColumns = 0;

        this.table = null;
    }

    create() {
        let table = document.createElement('table');
        table.id = this.id;
        table.tabIndex = 0;

        if (this.selectable && this.onRowClick) {
            $(table).on('keydown', this.tableKeydownEvent.bind(this));
            $(table).on('keyup', this.tableKeyupEvent.bind(this));
        }

        ['fc', 'table', 'table-hover', 'table-bordered'].forEach(function (cssClass) {
            table.classList.add(cssClass);
        });

        if (this.rowHeight.toLowerCase() === 'small') {
            table.classList.add('table-sm');
        }

        if (!this.rowIndexMappings) {
            table.classList.add('table-hover');
            table.classList.add('table-striped');
        }

        if (this.tableStripes.toLowerCase() === 'hide') {
            table.classList.remove('table-striped');
        }

        let heading = document.createElement('thead');
        let headingRow = document.createElement('tr');

        if (this.tableGrid.toLowerCase() === 'hide') {
            table.classList.remove('table-bordered');
            headingRow.classList.add('table-heading');
        }
        heading.appendChild(headingRow);
        this.header = heading;

        if (this.selectionCheckboxes) {
            let cell = document.createElement('th');
            cell.classList.add('selectionColumn');
            headingRow.appendChild(cell);
        }

        let body = document.createElement('tbody');

        table.appendChild(heading);
        table.appendChild(body);


        this.table = table;
        this.bindKeyboardEvents();

        let div = document.createElement('div');
        if (this.componentObj.horizontalScrolling) {
            if (this.fh.isIE()) {
                table.style.tableLayout = 'auto';
            } else {
                table.style.tableLayout = 'initial';
            }

            div.classList.add('table-responsive');
        }

        div.appendChild(table);
        this.component = div;

        this.wrap();

        this.contentWrapper = headingRow;
        this._dataWrapper = body;

        this.addStyles();
        this.display();

        if (this.componentObj.columns) {
            this.totalColumns = this.componentObj.columns.length;
            this.addComponents(this.componentObj.columns);
        }

        if (this.componentObj.footer) {
            let footer = document.createElement('tfoot');
            let row = document.createElement('tr');
            let footCell = document.createElement('td');
            footCell.colSpan = this.componentObj.columns.length;
            row.appendChild(footCell);
            footer.appendChild(row);
            this.table.appendChild(footer);
            this.footer = footer;

            this.contentWrapper = footCell;
            this.addComponent(this.componentObj.footer);
            this.contentWrapper = headingRow;
        }

        this.refreshData();
        $(this.table).on('mousedown', this.tableMousedownEvent);
        this.calculateColumnWidths();
        this.initFixedHeaderAndScrolling();

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
                        this.visibleRows = change.changedAttributes['displayedRowsCount'];
                        this.tableData = change.changedAttributes['tableRows'];
                        this.refreshData(true);
                        break;
                    case 'selectedRowNumber':
                        this.rawValue = change.changedAttributes['selectedRowNumber'];
                        this.highlightSelectedRows();
                        break;
                    case 'rowStylesMapping':
                        this.rowStylesMapping = newValue;
                        this.rows.forEach(function (row) {
                            let index = row.mainId;
                            if (this.rowStylesMapping[index]) {
                                row.component.style.backgroundColor = this.rowStylesMapping[index];
                            } else {
                                row.component.style.backgroundColor = null;
                            }
                        }.bind(this));

                        break;
                }
            }.bind(this));
        }
    };

    tableMousedownEvent(event) {
        if (event.ctrlKey) {
            event.preventDefault();
        }
    }

    tableKeydownEvent(event) {
        if (event.which == "17") {
            this.ctrlIsPressed = true;
        }
    }

    // fixedHeaderEvent(e) {
    //     const el = e.target;
    //     $(this.header)
    //         .find('th')
    //         .css('transform', 'translateY(' + el.scrollTop + 'px)');
    // }

    onRowClickEvent(event) {
        if (this.accessibility != 'EDIT') return;
        let element = event.target;
        while (element.tagName !== 'TR' && (element = element.parentElement)) {
        }

        let mainId = parseInt(element.dataset.mainId);

        if (this.multiselect == false) {
            if (event.ctrlKey) {
                if (this.rawValue.indexOf(mainId) !== -1) {
                    this.rawValue = [];
                    this.rawValue.push(-1);
                }
            } else {
                this.rawValue = [];
                this.rawValue.push(mainId);
            }
        } else {
            if (event.ctrlKey) {
                this.selectRow(mainId);
            } else {
                this.rawValue = [];
                this.rawValue.push(mainId);
            }
        }

        this.changesQueue.queueValueChange(this.rawValue);
        if (!this.onRowClick || this.onRowClick === '-') {
            this.highlightSelectedRows();
        }

        if (this._formId === 'FormPreview') {
            this.fireEvent('onRowClick', this.onRowClick);
        } else {
            this.fireEventWithLock('onRowClick', this.onRowClick);
        }
    }

    addRow(rowObj) {
        let row = (<any>FhContainer.get('TableRow'))(rowObj, this);
        this.rows.push(row);
        row.create();
        let last = this.rows.length - 1;
        let index = this.rows[last].mainId;
        if (this.rowStylesMapping[index]) {
            this.rows[last].component.style.backgroundColor = this.rowStylesMapping[index];
        }
        if (this.selectable && this.onRowClick) {
            // @ts-ignore
            row.component.style.cursor = 'pointer';
            row.htmlElement.addEventListener('click', this.onRowClickEvent.bind(this));
        }
        if (this.onRowDoubleClick) {
            // @ts-ignore
            row.component.style.cursor = 'pointer';
            row.htmlElement.addEventListener('dblclick', function (event) {
                if (this.accessibility != 'EDIT') return;
                let element = event.target;
                while (element.tagName !== 'TR' && (element = element.parentElement)) {
                }

                let mainId = parseInt(element.dataset.mainId);
                this.rawValue = [];
                this.rawValue.push(mainId);

                this.changesQueue.queueValueChange(this.rawValue);
                if (!this.onRowDoubleClick || this.onRowDoubleClick === '-') {
                    this.highlightSelectedRows();
                }

                if (this._formId === 'FormPreview') {
                    this.fireEvent('onRowDoubleClick', this.onRowDoubleClick);
                } else {
                    this.fireEventWithLock('onRowDoubleClick', this.onRowDoubleClick, event);
                }
            }.bind(this));
        }
        if (this.selectable && this.selectionCheckboxes) {
            let cell = document.createElement('td');
            let checkbox = document.createElement('input');
            checkbox.id = row.id + "_check";
            checkbox.type = 'checkbox';
            checkbox.style.pointerEvents = 'none';
            checkbox.classList.add('selectionCheckbox');
            cell.appendChild(checkbox);

            let checkboxLabel = document.createElement('label');
            checkboxLabel.setAttribute('for', row.id + "_check");
            cell.appendChild(checkboxLabel);

            cell.addEventListener('click', function (event) {
                event.stopPropagation();
                if (this.accessibility != 'EDIT') return;

                let element = event.target;
                if (event.currentTarget != null) {
                    element = event.currentTarget;
                }
                element.firstChild.checked = !element.firstChild.checked;

                this.selectRow(element.parentElement.dataset.mainId);

                this.changesQueue.queueValueChange(this.rawValue);
                if (!this.onRowClick || this.onRowClick === '-') {
                    this.highlightSelectedRows();
                }

                if (this._formId === 'FormPreview') {
                    this.fireEvent('onRowClick', this.onRowClick);
                } else {
                    this.fireEventWithLock('onRowClick', this.onRowClick, event);
                }
            }.bind(this));
            row.contentWrapper.insertBefore(cell, row.contentWrapper.firstChild);
        }
    };

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

    clearRows() {
        this.rows.forEach(function (row) {
            this.deleteRow(row);
        }.bind(this));
        this.rows = [];
    };

    redrawColumns() {
        this.calculateColumnWidths();
        this.components.forEach(function (column: any) {
            if (column.componentObj.type === 'Column') {
                column.calculateColspan();
            }
        });
        // if minRows is present
        if (this.minRows !== null && this.rows.length > 0) {
            this.removeMinRowRows();
            this.addMinRowRows();
        }
    };

    addComponent(componentObj) {
        let component = this.fh.createComponent(componentObj, this);

        this.components.push(component);
        component.create();
    };

    refreshData(clearSelection = undefined) {
        if (clearSelection) {
            this.rawValue = [];
            this.changesQueue.queueValueChange(null);
        }

        this.clearRows();
        for (let i = 0; i < this.visibleRows; i++) {
            let row = this.tableData[i];
            let rowData = {
                id: this.id + '_row_' + i,
                mainId: i,
                empty: row.empty,
                data: row.tableCells
            };
            if (this.rowIndexMappings) {
                rowData.mainId = this.rowIndexMappings[i];
            }
            this.addRow(rowData);
        }

        this.addMinRowRows();

        if (this.onRowClick === '-' || !clearSelection) {
            this.highlightSelectedRows();
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
                this.addRow(rowData);
                let row = this.rows[i];
                row.htmlElement.style.pointerEvents = 'none';
                row.htmlElement.classList.add('emptyRow');
                row.htmlElement.style.backgroundColor = null;
            }
        }
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

    countVisibleColumns() {
        let result = 0;

        this.components.forEach(function (c: any) {
            if (c.componentObj != null && c.componentObj.type.startsWith('Column') && c.accessibility !== 'HIDDEN') {
                result++;
            }
        });

        return result;
    };


    highlightSelectedRows() {
        let oldSelected = this.table.querySelectorAll('.table-primary');
        if (oldSelected && oldSelected.length) {
            [].forEach.call(oldSelected, function (row) {
                row.classList.remove('table-primary');

                if (this.selectionCheckboxes) {
                    row.firstChild.querySelector('input[type="checkbox"]').checked = false;
                }
            }.bind(this));
        }
        (this.rawValue || []).forEach(function (value) {
            if (value != -1) {
                let row = this.table.querySelector(('[data-main-id="' + value + '"]'));
                row.classList.add('table-primary');
                let container = $(this.component);
                let scrollTo = $(row);
                if (this.rawValue.length < 2) {
                    let containerHeight = container.height();
                    let containerScrollTop = container.scrollTop();
                    let realPositionElement = containerScrollTop + scrollTo.position().top;
                    if (realPositionElement < containerScrollTop || realPositionElement
                        > containerScrollTop
                        + containerHeight) {
                        $(this.component).animate({
                            scrollTop: realPositionElement - scrollTo.outerHeight()
                        });
                    }
                }
                if (this.selectionCheckboxes) {
                    row.firstChild.querySelector('input[type="checkbox"]').checked = true;
                }
            }
        }.bind(this));
    };

    collectAllChanges() {
        let allChanges = [];

        this.rows.forEach(function (component) {
            let changes = component.collectAllChanges();
            allChanges = allChanges.concat(changes);
        }.bind(this));

        return this.collectChanges(allChanges);
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

    selectRow(mainId) {
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

    tableKeyupEvent(e) {
        this.ctrlIsPressed = false;
        if (e.which == 9 && $(document.activeElement).is(":input")) {
            let parent = $(document.activeElement).parents('tbody tr:not(.emptyRow)');

            if (parent && parent.length > 0) {
                parent.trigger('click');
            }
        }
    }

    bindKeyboardEvents() {
        this.table.addEventListener('keydown', function (e) {
            if (document.activeElement == this.table) {
                if (e.which == 40) { // strzalka w dol
                    clearTimeout(this.keyEventTimer);
                    e.preventDefault();
                    let current = $(this.htmlElement).find('tbody tr.table-primary');
                    let next = null;

                    if (current.length == 0) {
                        next = $(this.htmlElement).find('tbody tr:not(.emptyRow)').first();
                    } else {
                        next = current.next('tr:not(.emptyRow)');
                    }
                    //If there isn't next element we go back to first one.
                    if (next && next.length == 0) {
                        next = $(this.htmlElement).find('tbody tr:not(.emptyRow)').first();
                    }

                    if (next && next.length > 0) {
                        current.removeClass('table-primary');
                        next.addClass('table-primary');
                        let offset = $(next).position().top;
                        if (this.fixedHeader) {
                            offset -= this.header.clientHeight;
                        }
                        $(this.component).scrollTop(offset - (this.component.clientHeight / 2));
                        this.keyEventTimer = setTimeout(function (elem) {
                            elem.trigger('click');
                        }, this.doneEventInterval, next);
                    }
                } else if (e.which == 38) { // strzalka w gore
                    e.preventDefault();
                    clearTimeout(this.keyEventTimer);
                    let current = $(this.htmlElement).find('tbody tr.table-primary');
                    let prev = null;

                    if (current.length == 0) {
                        prev = $(this.htmlElement).find('tbody tr:not(.emptyRow)').first();
                    } else {
                        prev = current.prev('tr:not(.emptyRow)');
                    }

                    if (prev && prev.length == 0) {
                        $(this.component).scrollTop(0);
                    } else if (prev && prev.length > 0) {
                        current.removeClass('table-primary');
                        prev.addClass('table-primary');
                        let offset = $(prev).position().top;
                        if (this.fixedHeader) {
                            offset -= this.header.clientHeight;
                        }
                        $(this.component).scrollTop(offset - (this.component.clientHeight / 2));
                        this.keyEventTimer = setTimeout(function (elem) {
                            elem.trigger('click');
                        }, this.doneEventInterval, prev);

                    }
                }else if (e.which == 33 || e.which == 36) { // pgup i home
                    e.preventDefault();

                    let first = $(this.htmlElement).find('tbody tr:not(.emptyRow)').first();

                    if (first && first.length > 0) {
                        $(this.component).scrollTop(0);
                        first.trigger('click');
                    }
                } else if (e.which == 34 || e.which == 35) { // pgdown i end
                    e.preventDefault();

                    let last = $(this.htmlElement).find('tbody tr:not(.emptyRow)').last();

                    if (last && last.length > 0) {
                        $(this.component).scrollTop($(last).position().top);
                        last.trigger('click');
                    }
                }
            }
        }.bind(this));
    }

    extractChangedAttributes() {
        return this.changesQueue.extractChangedAttributes();
    };

    getAdditionalButtons(): AdditionalButton[] {
        return [
            new AdditionalButton('addDefaultSubcomponent', 'plus', 'Add column')
        ];
    }

    // noinspection JSUnusedGlobalSymbols
    setPresentationStyle(presentationStyle) {
        if (this.parent != null) {
            // @ts-ignore
            this.parent.setPresentationStyle(presentationStyle);
        }
    }

    accessibilityResolve(node: HTMLElement, access: string) {
        // intentionally left blank
    }

    destroy(removeFromParent) {
        $(this.table).off('mousedown', this.tableMousedownEvent);

        if (this.selectable && this.onRowClick) {
            $(this.table).off('keydown', this.tableKeydownEvent.bind(this));
            $(this.table).off('keyup', this.tableKeyupEvent.bind(this));
        }

        this.clearRows();
        super.destroy(removeFromParent);
    }

    public get dataWrapper(): HTMLTableSectionElement {
        return this._dataWrapper;
    }

    render() {
        super.render();
        if (!this.onRowClick || this.onRowClick === '-') {
            this.highlightSelectedRows();
        }
    }

}

export {Table};
