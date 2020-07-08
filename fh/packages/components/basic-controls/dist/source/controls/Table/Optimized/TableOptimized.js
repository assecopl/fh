"use strict";
var __extends = (this && this.__extends) || (function () {
    var extendStatics = function (d, b) {
        extendStatics = Object.setPrototypeOf ||
            ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
            function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
        return extendStatics(d, b);
    };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();
Object.defineProperty(exports, "__esModule", { value: true });
var fh_forms_handler_1 = require("fh-forms-handler");
var TableWithKeyboardEvents_1 = require("./../Abstract/TableWithKeyboardEvents");
var TableOptimized = /** @class */ (function (_super) {
    __extends(TableOptimized, _super);
    /**
     * Rewrited logic
     */
    function TableOptimized(componentObj, parent) {
        var _this = _super.call(this, componentObj, parent) || this;
        _this.tableData = null;
        _this.rows = [];
        _this.minRows = null;
        _this.startSize = null;
        _this.firstShow = false;
        _this.footer = null;
        _this.timeoutedRows = new Map();
        _this.toInt = function (stringOrInt) {
            if (typeof stringOrInt == 'string') {
                return parseInt(stringOrInt);
            }
            else {
                return stringOrInt;
            }
        };
        _this.tableStripes = componentObj.tableStripes || 'show';
        _this.visibleRows = componentObj.displayedRowsCount || 0;
        _this.startSize = componentObj.startSize || null;
        _this.minRows = componentObj.minRows || null;
        _this.tableData = componentObj.tableRows;
        _this.ieFocusFixEnabled = _this.componentObj.ieFocusFixEnabled || false;
        return _this;
    }
    TableOptimized.prototype.clearRows = function () {
        this.rows.forEach(function (row) {
            row.destroy();
        }.bind(this));
        this.rows = [];
    };
    ;
    TableOptimized.prototype.clearTimeouts = function () {
        this.timeoutedRows.forEach(function (id, key) {
            clearTimeout(id);
        }.bind(this));
        this.timeoutedRows.clear();
    };
    TableOptimized.prototype.create = function () {
        var table = document.createElement('table');
        //Tabindex need to be set if we want to capture keybord events.
        table.tabIndex = 0;
        table.id = this.id;
        //
        table.className = 'fc table table-bordered';
        if (!this.rowIndexMappings) {
            if (this.tableStripes.toLowerCase() === 'hide') {
                table.className += ' table-hover';
            }
            else {
                table.className += ' table-striped table-hover';
            }
        }
        var heading = document.createElement('thead');
        var headingRow = document.createElement('tr');
        heading.appendChild(headingRow);
        this.header = heading;
        var body = document.createElement('tbody');
        table.appendChild(heading);
        table.appendChild(body);
        this.table = table;
        var div = document.createElement('div');
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
    ;
    TableOptimized.prototype.extractChangedAttributes = function () {
        return this.changesQueue.extractChangedAttributes();
    };
    ;
    TableOptimized.prototype.update = function (change) {
        _super.prototype.update.call(this, change);
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
            this.updateFixedHeaderWidth();
        }
    };
    TableOptimized.prototype.sleep = function (milliseconds) {
        return new Promise(function (resolve) {
            return setTimeout(resolve, milliseconds);
        });
    };
    /**
     * @override
     * Override main function to add new rows without delete old ones.
     * @param clearSelection
     */
    TableOptimized.prototype.refreshData = function (clearSelection) {
        if (clearSelection === void 0) { clearSelection = undefined; }
        //Clear timeouts in case new rows will be loaded before last load finish.
        this.clearTimeouts();
        if (clearSelection) {
            this.rawValue = [];
            this.changesQueue.queueValueChange(null);
            this.scrollTopInside();
        }
        this.clearRows();
        this.tableData != null ? this.tableData.forEach(function (row, index) {
            var rowData = {
                id: this.id + '_row_' + (index),
                mainId: (index),
                empty: row.empty,
                data: row.tableCells,
                styleClasses: "row_" + index,
                onRowClickEvent: null
            };
            if (this.selectable && this.onRowClick) {
                rowData.onRowClickEvent = this.onRowClickEvent.bind(this, index);
            }
            this.addRow(rowData, index);
        }.bind(this)) : null;
        this.firstShow = true;
        this.addMinRowRows();
    };
    ;
    Object.defineProperty(TableOptimized.prototype, "dataWrapper", {
        get: function () {
            return this._dataWrapper;
        },
        enumerable: true,
        configurable: true
    });
    TableOptimized.prototype.display = function () {
        this.addStyles();
        _super.prototype.display.call(this);
    };
    /**
     * OVERRIDES
     * @param componentsList
     */
    TableOptimized.prototype.deleteRow = function (row) {
        row.components.forEach(function (column) {
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
    ;
    TableOptimized.prototype.removeMinRowRows = function () {
        if (this.rows.length > this.visibleRows) {
            var i = void 0;
            for (i = this.visibleRows; i < this.rows.length; i++) {
                this.deleteRow(this.rows[i]);
            }
            this.rows = this.rows.splice(0, this.visibleRows);
        }
    };
    ;
    TableOptimized.prototype.addMinRowRows = function () {
        if (this.visibleRows < this.minRows) {
            var visibleColumnsCount = this.countVisibleColumns();
            var _loop_1 = function (i) {
                var rowData = {
                    id: i,
                    mainId: i,
                    data: Array.apply(null, new Array(visibleColumnsCount)).map(function (a, b) {
                        return {
                            type: 'OutputLabel',
                            id: this.id + 'EmptyRow' + i + '_' + b,
                            value: '&nbsp;'
                        };
                    }.bind(this_1))
                };
                this_1.addRow(rowData, i);
                var row = this_1.rows[i];
                row.htmlElement.style.pointerEvents = 'none';
                row.htmlElement.classList.add('emptyRow');
                row.htmlElement.style.backgroundColor = null;
            };
            var this_1 = this;
            for (var i = this.visibleRows; i < this.minRows; i++) {
                _loop_1(i);
            }
            this.recalculateGripHeight();
        }
    };
    ;
    TableOptimized.prototype.applyChange = function (change) {
        if (this.id === change.formElementId) {
            this.update(change);
        }
        else {
            this.rows.forEach(function (row) {
                row.applyChange(change);
            });
            this.components.forEach(function (component) {
                component.applyChange(change);
            });
        }
    };
    ;
    TableOptimized.prototype.collectAllChanges = function () {
        var allChanges = [];
        this.rows.forEach(function (component) {
            var changes = component.collectAllChanges();
            allChanges = allChanges.concat(changes);
        }.bind(this));
        return this.collectChanges(allChanges);
    };
    ;
    TableOptimized.prototype.countVisibleColumns = function () {
        var result = 0;
        this.components.forEach(function (c) {
            if (c.componentObj != null && c.componentObj.type.startsWith('Column') && c.accessibility !== 'HIDDEN') {
                result++;
            }
        });
        return result;
    };
    ;
    TableOptimized.prototype.addRow = function (rowData, index) {
        var row = fh_forms_handler_1.FhContainer.get('TableRowOptimized')(rowData, this);
        this.rows.push(row);
        var last = this.rows.length - 1;
        var func = null;
        if (!this.horizontalScrolling && (index + 1) == this.visibleRows) {
            func = this.recalculateGripHeight.bind(this);
        }
        /**
         * Oddelegowanie ładowania rekordów większych niż "startSize" w czasie.
         * Oddelegowanie następuje tylko dla pierwszego renderowania tabeli.
         * Przy aktualizacji rekordy dodawane są od razu.
         */
        if (this.startSize && index > this.startSize && !this.firstShow) {
            var timeOutId = setTimeout(function (row, callback, updateFixedHeaderWidth) {
                row.create();
                updateFixedHeaderWidth();
                if (callback) {
                    callback();
                }
                //Delete row timeout from list after finish loading.
                this.timeoutedRows.delete(row.id);
            }.bind(this), (100 * (index)), row, func, this.updateFixedHeaderWidth.bind(this));
            this.timeoutedRows.set(row.id, timeOutId);
        }
        else {
            row.create();
        }
    };
    ;
    TableOptimized.prototype.onRowClickEvent = function (index) {
        if (this.accessibility != 'EDIT')
            return;
        var mainId = index;
        if (this.multiselect == false) {
            if (this.ctrlIsPressed) {
                if (this.rawValue.indexOf(mainId) !== -1) {
                    this.rawValue = [];
                    this.rawValue.push(-1);
                }
            }
            else {
                this.rawValue = [];
                this.rawValue.push(mainId);
            }
        }
        else {
            if (this.ctrlIsPressed) {
                this.selectRow(mainId);
            }
            else {
                this.rawValue = [];
                this.rawValue.push(mainId);
            }
        }
        this.changesQueue.queueValueChange(this.rawValue);
        if (this._formId === 'FormPreview') {
            this.fireEvent('onRowClick', this.onRowClick);
        }
        else {
            this.fireEventWithLock('onRowClick', this.onRowClick);
        }
    };
    TableOptimized.prototype.selectRow = function (mainId) {
        var index = this.rawValue.indexOf(parseInt(mainId));
        if (index == -1) {
            this.rawValue.push(mainId);
        }
        else if (index != -1) {
            this.rawValue.splice(index, 1);
            this.rawValue.filter(function (idx) { return idx > -1; });
            if (this.rawValue.length == 0) {
                this.rawValue.push(-1);
            }
        }
    };
    ;
    TableOptimized.prototype.redrawColumns = function () {
        this.calculateColumnWidths();
        // if minRows is present
        if (this.minRows !== null && this.rows.length > 0) {
            this.removeMinRowRows();
            this.addMinRowRows();
        }
    };
    ;
    TableOptimized.prototype.destroy = function (removeFromParent) {
        //Stop loading columns
        this.clearTimeouts();
        _super.prototype.destroy.call(this, removeFromParent);
    };
    // noinspection JSUnusedGlobalSymbols
    TableOptimized.prototype.setPresentationStyle = function (presentationStyle) {
        if (this.parent != null) {
            // @ts-ignore
            this.parent.setPresentationStyle(presentationStyle);
        }
    };
    TableOptimized.prototype.accessibilityResolve = function (node, access) {
        // intentionally left blank
    };
    return TableOptimized;
}(TableWithKeyboardEvents_1.TableWithKeyboardEvents));
exports.TableOptimized = TableOptimized;
//# sourceMappingURL=TableOptimized.js.map