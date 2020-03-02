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
var TableLazy_pl_1 = require("./i18n/TableLazy.pl");
var TableLazy_en_1 = require("./i18n/TableLazy.en");
var fh_forms_handler_1 = require("fh-forms-handler");
var TableLazy = /** @class */ (function (_super) {
    __extends(TableLazy, _super);
    function TableLazy(componentObj, parent) {
        var _this = _super.call(this, componentObj, parent) || this;
        _this.rows = [];
        _this.footer = null;
        _this.adder = null;
        _this.onlyNew = true;
        _this.toInt = function (stringOrInt) {
            if (typeof stringOrInt == 'string') {
                return parseInt(stringOrInt);
            }
            else {
                return stringOrInt;
            }
        };
        _this.i18n.registerStrings('en', TableLazy_en_1.TableLazyEN);
        _this.i18n.registerStrings('pl', TableLazy_pl_1.TableLazyPL);
        // this.selectable = this.componentObj.selectable || true;
        _this.visibleRows = _this.componentObj.displayedRowsCount || 0;
        _this.tableData = _this.componentObj.tableRows;
        _this.rows = [];
        _this.rowIndexMappings = _this.componentObj.rowIndexMappings || null;
        _this.rowStylesMapping = _this.componentObj.rowStylesMapping || [];
        _this.minRows = _this.componentObj.minRows || null;
        // this.fixedHeader = this.componentObj.fixedHeader || false;
        _this.rowHeight = _this.componentObj.rowHeight || 'normal';
        _this.tableGrid = _this.componentObj.tableGrid || 'show';
        _this.tableStripes = _this.componentObj.tableStripes || 'show';
        // this.ieFocusFixEnabled = this.componentObj.ieFocusFixEnabled || false;
        // this.rawValue = this.componentObj.rawValue || this.componentObj.selectedRowsNumbers || [];
        _this._dataWrapper = null;
        // this.onRowClick = this.componentObj.onRowClick;
        // this.onRowDoubleClick = this.componentObj.onRowDoubleClick;
        // this.multiselect = this.componentObj.multiselect || false;
        // this.selectionCheckboxes = this.componentObj.selectionCheckboxes || false;
        _this.selectionChanged = false;
        // this.ctrlIsPressed = false;
        _this.componentObj.verticalAlign = _this.componentObj.verticalAlign || 'top';
        _this.totalColumns = 0;
        _this.table = null;
        _this.loadInfo = null;
        _this.totalRows = _this.componentObj.totalRows;
        _this.loadMorePage = _this.componentObj.loadable.pageNumber || 0;
        _this.paginatorOffset = 2;
        _this.onLoadMore = _this.componentObj.onLoadMore;
        _this.loadAllPages = _this.componentObj.loadAllPages;
        _this.sortedBy = null;
        _this.sortDirection = null;
        _this.startSize = _this.componentObj.loadable.pageSize || 9999;
        return _this;
    }
    TableLazy.prototype.clearRows = function () {
        this.rows.forEach(function (row) {
            row.destroy();
        }.bind(this));
        this.rows = [];
    };
    ;
    TableLazy.prototype.create = function () {
        var table = document.createElement('table');
        table.classList.add('table');
        table.id = this.id;
        table.tabIndex = 0;
        //
        table.classList.add('fc');
        table.classList.add('table', 'table-hover', 'table-bordered');
        var heading = document.createElement('thead');
        var headingRow = document.createElement('tr');
        heading.appendChild(headingRow);
        this.header = heading;
        var body = document.createElement('tbody');
        table.appendChild(heading);
        table.appendChild(body);
        this.table = table;
        //
        var div = document.createElement('div');
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
    ;
    TableLazy.prototype.extractChangedAttributes = function () {
        return this.changesQueue.extractChangedAttributes();
    };
    ;
    TableLazy.sleep = function (ms) {
        return new Promise(function (resolve) { return setTimeout(resolve, ms); });
    };
    TableLazy.prototype.update = function (change) {
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
                        if (newValue == this.totalRows) {
                            if (this.adder) {
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
            setTimeout(function () {
                this.loadAll();
            }.bind(this), 400);
        }
    };
    TableLazy.prototype.sleep = function (milliseconds) {
        return new Promise(function (resolve) { return setTimeout(resolve, milliseconds); });
    };
    TableLazy.prototype.loadAll = function (adder) {
        var _this = this;
        if (adder === void 0) { adder = null; }
        if (this.visibleRows < this.totalRows) {
            new Promise(function (resolve, reject) {
                if (this.adder) {
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
                $(this.loader).fadeOut('slow', function () {
                    if (this.adder) {
                        this.adder.style.display = 'inline';
                    }
                }.bind(this));
            }.bind(this)).catch(function (e) {
                _this.loader.innerHTML = "<p>" + _this.i18n.__('lazy_loading_fail') + "</p>";
            });
        }
        else {
            $(this.footer).fadeOut('slow');
        }
    };
    /**
     * @override
     * Override main function to add new rows without delete old ones.
     * @param clearSelection
     */
    TableLazy.prototype.refreshData = function (clearSelection) {
        if (clearSelection === void 0) { clearSelection = undefined; }
        if (clearSelection) {
            this.rawValue = [];
            this.changesQueue.queueValueChange(null);
        }
        // this.clearRows()
        var currentRowsCount = this.rows.length;
        for (var i = currentRowsCount; i < this.visibleRows; i++) {
            var row = this.tableData[i];
            var rowData = {
                id: this.id + '_row_' + (i),
                mainId: (i),
                empty: row.empty,
                data: row.tableCells
            };
            var rowObj = fh_forms_handler_1.FhContainer.get('TableRow')(rowData, this);
            this.rows.push(rowObj);
            rowObj.create();
        }
    };
    ;
    TableLazy.prototype.createLoader = function () {
        var footer = null;
        var loader = document.createElement("tr");
        var loadertd = document.createElement("td");
        var loaderspan = document.createElement("span");
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
        if (!this.loadAllPages && this.visibleRows < this.totalRows) {
            var adder = document.createElement("span");
            loadertd.style.cursor = "pointer";
            adder.innerHTML = "<i class='fas fa-plus' style='font-size: 30px;'></i>";
            loadertd.appendChild(adder);
            this.adder = adder;
            this.adder.style.display = 'inline';
        }
        if (this.visibleRows < this.totalRows) {
            this.lazyTr.style.display = 'table-row';
        }
        footer = document.createElement("tfoot");
        footer.appendChild(loader);
        this.footer = footer;
        this.table.appendChild(footer);
        if (this.adder) {
            this.adder.addEventListener('click', this.loadAll.bind(this, this.adder));
        }
    };
    Object.defineProperty(TableLazy.prototype, "dataWrapper", {
        get: function () {
            return this._dataWrapper;
        },
        enumerable: true,
        configurable: true
    });
    return TableLazy;
}(fh_forms_handler_1.HTMLFormComponent));
exports.TableLazy = TableLazy;
//# sourceMappingURL=TableLazy.js.map