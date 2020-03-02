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
var TableWithKeyboardEvents_1 = require("./Abstract/TableWithKeyboardEvents");
var Table = /** @class */ (function (_super) {
    __extends(Table, _super);
    function Table(componentObj, parent) {
        var _this = _super.call(this, componentObj, parent) || this;
        _this.rows = [];
        _this.footer = null;
        _this.visibleRows = _this.componentObj.displayedRowsCount || 0;
        _this.tableData = _this.componentObj.tableRows;
        _this.rows = [];
        _this.rowIndexMappings = _this.componentObj.rowIndexMappings || null;
        _this.rowStylesMapping = _this.componentObj.rowStylesMapping || [];
        _this.minRows = _this.componentObj.minRows || null;
        _this.rowHeight = _this.componentObj.rowHeight || 'normal';
        _this.tableGrid = _this.componentObj.tableGrid || 'hide';
        _this.tableStripes = _this.componentObj.tableStripes || 'hide';
        _this.ieFocusFixEnabled = _this.componentObj.ieFocusFixEnabled || false;
        _this._dataWrapper = null;
        _this.onRowDoubleClick = _this.componentObj.onRowDoubleClick;
        _this.selectionCheckboxes = _this.componentObj.selectionCheckboxes || false;
        _this.selectionChanged = false;
        _this.componentObj.verticalAlign = _this.componentObj.verticalAlign || 'top';
        _this.totalColumns = 0;
        _this.table = null;
        return _this;
    }
    Table.prototype.create = function () {
        var table = document.createElement('table');
        table.id = this.id;
        table.tabIndex = 0;
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
        var heading = document.createElement('thead');
        var headingRow = document.createElement('tr');
        if (this.tableGrid.toLowerCase() === 'hide') {
            table.classList.remove('table-bordered');
            headingRow.classList.add('table-heading');
        }
        heading.appendChild(headingRow);
        this.header = heading;
        if (this.selectionCheckboxes) {
            var cell = document.createElement('th');
            cell.classList.add('selectionColumn');
            headingRow.appendChild(cell);
        }
        var body = document.createElement('tbody');
        table.appendChild(heading);
        table.appendChild(body);
        this.table = table;
        var div = document.createElement('div');
        if (this.componentObj.horizontalScrolling) {
            if (this.fh.isIE()) {
                table.style.tableLayout = 'auto';
            }
            else {
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
            var footer = document.createElement('tfoot');
            var row = document.createElement('tr');
            var footCell = document.createElement('td');
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
        this.initExtends();
    };
    Table.prototype.update = function (change) {
        _super.prototype.update.call(this, change);
        if (change.changedAttributes) {
            $.each(change.changedAttributes, function (name, newValue) {
                switch (name) {
                    case 'rowIndexMappings':
                        this.rowIndexMappings = newValue;
                        this.refreshData(true);
                        this.updateFixedHeaderWidth();
                        break;
                    case 'displayedRowsCount':
                        this.visibleRows = change.changedAttributes['displayedRowsCount'];
                        this.tableData = change.changedAttributes['tableRows'];
                        this.refreshData(true);
                        this.updateFixedHeaderWidth();
                        this.scrollTopInside();
                        break;
                    case 'selectedRowNumber':
                        this.rawValue = change.changedAttributes['selectedRowNumber'];
                        this.highlightSelectedRows();
                        break;
                    case 'rowStylesMapping':
                        this.rowStylesMapping = newValue;
                        this.rows.forEach(function (row) {
                            var index = row.mainId;
                            if (this.rowStylesMapping[index]) {
                                row.component.style.backgroundColor = this.rowStylesMapping[index];
                            }
                            else {
                                row.component.style.backgroundColor = null;
                            }
                        }.bind(this));
                        break;
                }
            }.bind(this));
        }
    };
    ;
    Table.prototype.onRowClickEvent = function (event) {
        if (this.accessibility != 'EDIT')
            return;
        var element = event.target;
        while (element.tagName !== 'TR' && (element = element.parentElement)) {
        }
        var mainId = parseInt(element.dataset.mainId);
        if (this.multiselect == false) {
            if (event.ctrlKey) {
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
            if (event.ctrlKey) {
                this.selectRow(mainId);
            }
            else {
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
        }
        else {
            this.fireEventWithLock('onRowClick', this.onRowClick);
        }
    };
    Table.prototype.addRow = function (rowObj) {
        var row = fh_forms_handler_1.FhContainer.get('TableRow')(rowObj, this);
        this.rows.push(row);
        row.create();
        var last = this.rows.length - 1;
        var index = this.rows[last].mainId;
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
                if (this.accessibility != 'EDIT')
                    return;
                var element = event.target;
                while (element.tagName !== 'TR' && (element = element.parentElement)) {
                }
                var mainId = parseInt(element.dataset.mainId);
                this.rawValue = [];
                this.rawValue.push(mainId);
                this.changesQueue.queueValueChange(this.rawValue);
                if (!this.onRowDoubleClick || this.onRowDoubleClick === '-') {
                    this.highlightSelectedRows();
                }
                if (this._formId === 'FormPreview') {
                    this.fireEvent('onRowDoubleClick', this.onRowDoubleClick);
                }
                else {
                    this.fireEventWithLock('onRowDoubleClick', this.onRowDoubleClick, event);
                }
            }.bind(this));
        }
        if (this.selectable && this.selectionCheckboxes) {
            var cell = document.createElement('td');
            var checkbox = document.createElement('input');
            checkbox.id = row.id + "_check";
            checkbox.type = 'checkbox';
            checkbox.style.pointerEvents = 'none';
            checkbox.classList.add('selectionCheckbox');
            cell.appendChild(checkbox);
            var checkboxLabel = document.createElement('label');
            checkboxLabel.setAttribute('for', row.id + "_check");
            cell.appendChild(checkboxLabel);
            cell.addEventListener('click', function (event) {
                event.stopPropagation();
                if (this.accessibility != 'EDIT')
                    return;
                var element = event.target;
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
                }
                else {
                    this.fireEventWithLock('onRowClick', this.onRowClick, event);
                }
            }.bind(this));
            row.contentWrapper.insertBefore(cell, row.contentWrapper.firstChild);
        }
    };
    ;
    Table.prototype.deleteRow = function (row) {
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
    Table.prototype.clearRows = function () {
        this.rows.forEach(function (row) {
            this.deleteRow(row);
        }.bind(this));
        this.rows = [];
    };
    ;
    Table.prototype.redrawColumns = function () {
        this.calculateColumnWidths();
        this.components.forEach(function (column) {
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
    ;
    Table.prototype.addComponent = function (componentObj) {
        var component = this.fh.createComponent(componentObj, this);
        this.components.push(component);
        component.create();
    };
    ;
    Table.prototype.refreshData = function (clearSelection) {
        if (clearSelection === void 0) { clearSelection = undefined; }
        if (clearSelection) {
            this.rawValue = [];
            this.changesQueue.queueValueChange(null);
        }
        this.clearRows();
        for (var i = 0; i < this.visibleRows; i++) {
            var row = this.tableData[i];
            var rowData = {
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
            this.highlightSelectedRows(false);
        }
    };
    ;
    Table.prototype.addMinRowRows = function () {
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
                this_1.addRow(rowData);
                var row = this_1.rows[i];
                row.htmlElement.style.pointerEvents = 'none';
                row.htmlElement.classList.add('emptyRow');
                row.htmlElement.style.backgroundColor = null;
            };
            var this_1 = this;
            for (var i = this.visibleRows; i < this.minRows; i++) {
                _loop_1(i);
            }
        }
    };
    ;
    Table.prototype.removeMinRowRows = function () {
        if (this.rows.length > this.visibleRows) {
            var i = void 0;
            for (i = this.visibleRows; i < this.rows.length; i++) {
                this.deleteRow(this.rows[i]);
            }
            this.rows = this.rows.splice(0, this.visibleRows);
        }
    };
    ;
    Table.prototype.countVisibleColumns = function () {
        var result = 0;
        this.components.forEach(function (c) {
            if (c.componentObj != null && c.componentObj.type.startsWith('Column') && c.accessibility !== 'HIDDEN') {
                result++;
            }
        });
        return result;
    };
    ;
    /**
     * @override
     * Used for standard tables
     * @param scrollAnimate
     */
    Table.prototype.highlightSelectedRows = function (scrollAnimate) {
        if (scrollAnimate === void 0) { scrollAnimate = false; }
        var oldSelected = this.table.querySelectorAll('.table-primary');
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
                var row = this.table.querySelector(('[data-main-id="' + value + '"]'));
                if (row == null) {
                    return;
                }
                row.classList.add('table-primary');
                var container = $(this.component);
                var scrollTo_1 = $(row);
                if (this.rawValue.length < 2) {
                    var containerHeight = container.height();
                    var containerScrollTop = container.scrollTop();
                    var realPositionElement = scrollTo_1.position().top;
                    if (realPositionElement < containerScrollTop || realPositionElement
                        > containerScrollTop
                            + containerHeight) {
                        this.scrolToRow(scrollTo_1, scrollAnimate);
                    }
                }
                if (this.selectionCheckboxes) {
                    row.firstChild.querySelector('input[type="checkbox"]').checked = true;
                }
            }
        }.bind(this));
    };
    ;
    Table.prototype.collectAllChanges = function () {
        var allChanges = [];
        this.rows.forEach(function (component) {
            var changes = component.collectAllChanges();
            allChanges = allChanges.concat(changes);
        }.bind(this));
        return this.collectChanges(allChanges);
    };
    ;
    Table.prototype.applyChange = function (change) {
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
    Table.prototype.selectRow = function (mainId) {
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
    Table.prototype.extractChangedAttributes = function () {
        return this.changesQueue.extractChangedAttributes();
    };
    ;
    Table.prototype.getAdditionalButtons = function () {
        return [
            new fh_forms_handler_1.AdditionalButton('addDefaultSubcomponent', 'plus', 'Add column')
        ];
    };
    // noinspection JSUnusedGlobalSymbols
    Table.prototype.setPresentationStyle = function (presentationStyle) {
        if (this.parent != null) {
            // @ts-ignore
            this.parent.setPresentationStyle(presentationStyle);
        }
    };
    Table.prototype.accessibilityResolve = function (node, access) {
        // intentionally left blank
    };
    Table.prototype.destroy = function (removeFromParent) {
        this.clearRows();
        _super.prototype.destroy.call(this, removeFromParent);
    };
    Object.defineProperty(Table.prototype, "dataWrapper", {
        get: function () {
            return this._dataWrapper;
        },
        enumerable: true,
        configurable: true
    });
    Table.prototype.render = function () {
        _super.prototype.render.call(this);
        if (!this.onRowClick || this.onRowClick === '-') {
            //Show highlighted record after showing table again , Works only with animate set to true.
            this.highlightSelectedRows(true);
        }
    };
    Table.prototype.getAllComponents = function () {
        var result = this.components;
        this.rows.forEach(function (value) {
            result = result.concat(value.components);
        });
        return result;
    };
    return Table;
}(TableWithKeyboardEvents_1.TableWithKeyboardEvents));
exports.Table = Table;
//# sourceMappingURL=Table.js.map