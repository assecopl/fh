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
var fh_forms_handler_2 = require("fh-forms-handler");
var ColumnPaged = /** @class */ (function (_super) {
    __extends(ColumnPaged, _super);
    function ColumnPaged(componentObj, parent) {
        var _this = _super.call(this, componentObj, parent) || this;
        _this.isSortable = Boolean(_this.componentObj.sortable);
        _this.sorter = null;
        _this.rowspan = _this.componentObj.rowspan || null;
        _this.subColumnsExists = _this.componentObj.subColumnsExists || false;
        _this.subelements = _this.componentObj.subelements || [];
        return _this;
    }
    ColumnPaged.prototype.create = function () {
        var column = document.createElement('th');
        if (this.width && this.width.length > 0) {
            column.style.width = this.width[0].includes("px") ? this.width[0] : this.width[0] + '%';
        }
        // HTMLComponent recognized and updated label
        this.labelElement = document.createElement('span');
        column.appendChild(this.labelElement);
        this.labelElement.innerHTML = this.fhml.resolveValueTextOrEmpty(this.componentObj.label);
        this.component = column;
        this.htmlElement = this.component;
        this.contentWrapper = this.component;
        this.display();
        if (this.subColumnsExists) {
            var jQueryComponent = $(this.component);
            var currentRow = jQueryComponent.parent('tr');
            this.setSubcolumns(this, this, jQueryComponent, currentRow, this.setSubcolumns);
        }
        else {
            if (this.isSortable) {
                this.setSorter(this, this, $(this.component));
            }
        }
        if (this.rowspan) {
            this.component.setAttribute('rowspan', this.rowspan);
        }
    };
    ;
    ColumnPaged.prototype.setSorter = function (parentObject, columnObject, columnElement) {
        var sorter = document.createElement('span');
        sorter.classList.add('clearfix');
        sorter.classList.add('sorter');
        sorter.classList.add('parent-' + parentObject.id);
        sorter.setAttribute('sorter', columnObject.id);
        var icon = document.createElement('i');
        icon.classList.add('fa');
        icon.classList.add('fa-sort');
        sorter.appendChild(icon);
        columnElement.append($(sorter));
        parentObject.sorter = sorter;
        this.component.classList.add('sortable');
        this.component.addEventListener('click', function (event) {
            var icon = sorter.firstChild;
            var sortDirection;
            if (icon.classList.contains('fa-sort')) {
                icon.classList.remove('fa-sort');
                icon.classList.add('fa-sort-amount-down');
                sortDirection = 'ASC';
            }
            else if (icon.classList.contains('fa-sort-amount-up')) {
                icon.classList.remove('fa-sort-amount-up');
                icon.classList.add('fa-sort-amount-down');
                sortDirection = 'ASC';
            }
            else if (icon.classList.contains('fa-sort-amount-down')) {
                icon.classList.remove('fa-sort-amount-down');
                icon.classList.add('fa-sort-amount-up');
                sortDirection = 'DESC';
            }
            parentObject.sorter = sorter;
            parentObject.parent.changeSort(this.id, sortDirection);
        }.bind(columnObject));
    };
    ;
    ColumnPaged.prototype.setSubcolumns = function (parentObject, columnObject, columnElement, currentRow, callback) {
        if (currentRow.next().length == 0) {
            currentRow.after('<tr></tr>');
        }
        var colspan = 0;
        var nextRow = currentRow.next();
        columnObject.subelements.forEach(function (item) {
            var newColumnElement = $('<th>' + item.label + '</th>');
            // newColumnElement.addClass('parent-'+parentObject.id);
            nextRow.append(newColumnElement);
            // item.parent = columnObject;
            if (item.rowspan) {
                newColumnElement.attr('rowspan', item.rowspan);
            }
            if (item.subColumnsExists && item.subColumnsExists === true) {
                colspan += callback(parentObject, item, newColumnElement, nextRow, callback);
            }
            else {
                colspan++;
                if (item.sortable && item.sortable == true) {
                    parentObject.setSorter(parentObject, item, newColumnElement);
                }
            }
        });
        columnElement.attr('colspan', colspan);
        if ((columnObject.isSortable && columnObject.isSortable == true) ||
            (columnObject.sortable && columnObject.sortable == true)) {
            parentObject.setSorter(parentObject, parentObject, columnElement);
        }
        return colspan;
    };
    ;
    ColumnPaged.prototype.update = function (change) {
        _super.prototype.update.call(this, change);
        if (ENV_IS_DEVELOPMENT) {
            console.log('%c update ', 'background: #F00; color: #00F', change);
        }
        if (change.changedAttributes) {
            $.each(change.changedAttributes, function (name) {
                switch (name) {
                    case 'accessibility':
                        // setting accessibility done in HTMLFormComponent.update()
                        // just redraw columns
                        var parentTable = this.parent;
                        while (parentTable.componentObj.type !== 'TablePaged') {
                            parentTable = parentTable.parent;
                        }
                        parentTable.redrawColumns();
                        break;
                }
            }.bind(this));
        }
    };
    ;
    ColumnPaged.prototype.getDefaultWidth = function () {
        return null;
    };
    ;
    ColumnPaged.prototype.getAdditionalButtons = function () {
        return [
            new fh_forms_handler_2.AdditionalButton('moveUp', 'arrow-left', 'Move left'),
            new fh_forms_handler_2.AdditionalButton('moveDown', 'arrow-right', 'Move right')
        ];
    };
    return ColumnPaged;
}(fh_forms_handler_1.HTMLFormComponent));
exports.ColumnPaged = ColumnPaged;
//# sourceMappingURL=ColumnPaged.js.map