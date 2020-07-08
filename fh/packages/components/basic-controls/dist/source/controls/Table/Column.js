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
var Table_1 = require("./Table");
var fh_forms_handler_2 = require("fh-forms-handler");
var Column = /** @class */ (function (_super) {
    __extends(Column, _super);
    function Column(componentObj, parent) {
        var _this = _super.call(this, componentObj, parent) || this;
        _this.isSortable = Boolean(_this.componentObj.sortable);
        _this.sorter = null;
        _this.rowspan = _this.componentObj.rowspan || null;
        _this.subColumnsExists = _this.componentObj.subColumnsExists || false;
        _this.subelements = _this.componentObj.subelements || [];
        _this.colspan = _this.componentObj.colspan || 0;
        _this.subcomponents = [];
        return _this;
    }
    Column.prototype.create = function () {
        var column = document.createElement('th');
        column.id = this.id;
        if (this.width && this.width.length > 0) {
            column.style.width = this.width[0].includes("px") ? this.width[0] : this.width[0] + '%';
        }
        // HTMLComponent recognized and updated label
        this.labelElement = document.createElement('span');
        column.appendChild(this.labelElement);
        this.labelElement.innerHTML = this.fhml.resolveValueTextOrEmpty(this.componentObj.label);
        if (this.isSortable) {
            var sorter = document.createElement('span');
            sorter.classList.add('pull-right');
            sorter.classList.add('clearfix');
            sorter.classList.add('sorter');
            var icon = document.createElement('i');
            icon.classList.add('fa');
            icon.classList.add('fa-sort');
            sorter.appendChild(icon);
            column.appendChild(sorter);
            this.sorter = sorter;
            column.classList.add('sortable');
            // column.classList.add(column.id);
            column.addEventListener('click', this.columnClickEvent.bind(this));
        }
        this.component = column;
        this.htmlElement = this.component;
        this.contentWrapper = this.component;
        this.display();
        if (this.parent instanceof Table_1.Table) {
            this.parent.totalColumns++;
        }
        if (this.subColumnsExists && this.subColumnsExists === true) {
            var currentRow = $(this.component).parent('tr');
            if (currentRow.next().length === 0) {
                currentRow.after('<tr></tr>');
            }
            var nextRow = currentRow.next();
            var oldContentWrapper = this.contentWrapper;
            this.contentWrapper = nextRow[0];
            this.subelements.forEach(function (subelement) {
                var subcomponent = this.fh.createComponent(subelement, this);
                subcomponent.create();
                this.components.push(subcomponent);
                if (subelement.subColumnsExists && subelement.subColumnsExists === true) {
                    this.colspan += subcomponent.colspan;
                }
                else {
                    this.colspan++;
                }
            }.bind(this));
            this.contentWrapper = oldContentWrapper;
        }
        if (this.rowspan) {
            this.component.setAttribute('rowspan', this.rowspan);
        }
        this.setColspan(this.colspan);
    };
    ;
    Column.prototype.calculateColspan = function () {
        if (this.subColumnsExists && this.subColumnsExists === true) {
            this.colspan = 0;
            var hiddenCount_1 = 0;
            this.components.forEach(function (subcomponent) {
                if (subcomponent.accessibility === 'HIDDEN') {
                    hiddenCount_1 += 1;
                    return;
                }
                if (subcomponent.componentObj.subColumnsExists && subcomponent.componentObj.subColumnsExists === true) {
                    this.colspan += subcomponent.colspan;
                }
                else {
                    this.colspan++;
                }
            }.bind(this));
            this.setColspan(this.colspan);
        }
    };
    ;
    Column.prototype.setAccessibility = function (accessibility) {
        // Alvays show in design mode.
        if (this.designMode && accessibility === 'HIDDEN') {
            accessibility = 'EDIT';
        }
        _super.prototype.setAccessibility.call(this, accessibility);
        if (this.parent instanceof Column && this.parent.subColumnsExists === true) {
            var hiddenSubcolumnsCount_1 = 0;
            this.parent.components.forEach(function (subcomponent) {
                if (subcomponent.accessibility === 'HIDDEN') {
                    hiddenSubcolumnsCount_1 += 1;
                }
            });
            if (hiddenSubcolumnsCount_1 > 0 && this.parent.components.length === hiddenSubcolumnsCount_1) {
                this.parent.setAccessibility('HIDDEN');
            }
        }
        if (this.designMode) {
            this.htmlElement.classList.remove('disabled', 'fc-disabled');
            this.htmlElement.classList.add('fc-editable');
        }
    };
    ;
    Column.prototype.setColspan = function (colspan) {
        if (colspan === 0 && this.component.hasAttribute('colspan')) {
            this.component.removeAttribute('colspan');
            return;
        }
        this.component.setAttribute('colspan', colspan);
    };
    ;
    Column.prototype.update = function (change) {
        _super.prototype.update.call(this, change);
        if (change.changedAttributes) {
            $.each(change.changedAttributes, function (name) {
                switch (name) {
                    case 'accessibility':
                        // setting accessibility done in HTMLFormComponent.update()
                        // just redraw columns
                        var parentTable = this.parent;
                        while (parentTable.componentObj.type !== 'Table') {
                            parentTable = parentTable.parent;
                        }
                        parentTable.redrawColumns();
                        break;
                }
            }.bind(this));
        }
    };
    ;
    Column.prototype.getDefaultWidth = function () {
        return null;
    };
    ;
    Column.prototype.getAdditionalButtons = function () {
        return [
            new fh_forms_handler_2.AdditionalButton('moveUp', 'arrow-left', 'Move left'),
            new fh_forms_handler_2.AdditionalButton('moveDown', 'arrow-right', 'Move right')
        ];
    };
    Column.prototype.columnClickEvent = function () {
        var sortDirection;
        var icon = this.sorter.firstChild;
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
        // @ts-ignore
        this.parent.changeSort(this.id, sortDirection);
    };
    Column.prototype.destroy = function (removeFromParent) {
        if (this.isSortable) {
            this.component.removeEventListener('click', this.columnClickEvent.bind(this));
        }
        _super.prototype.destroy.call(this, removeFromParent);
    };
    return Column;
}(fh_forms_handler_1.HTMLFormComponent));
exports.Column = Column;
//# sourceMappingURL=Column.js.map