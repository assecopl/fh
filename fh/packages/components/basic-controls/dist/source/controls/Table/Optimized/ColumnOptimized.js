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
var ColumnOptimized = /** @class */ (function (_super) {
    __extends(ColumnOptimized, _super);
    function ColumnOptimized(componentObj, parent) {
        var _this = _super.call(this, componentObj, parent) || this;
        _this.isSortable = Boolean(_this.componentObj.sortable);
        _this.sorter = null;
        _this.rowspan = _this.componentObj.rowspan || null;
        _this.subColumnsExists = _this.componentObj.subColumnsExists || false;
        _this.fixedHeader = _this.componentObj.fixedHeader || false;
        _this.subelements = _this.componentObj.subelements || [];
        _this.colspan = _this.componentObj.colspan || 0;
        _this.subcomponents = [];
        return _this;
    }
    ColumnOptimized.prototype.create = function () {
        var column = document.createElement('th');
        column.id = this.id;
        column.classList.add(this.id);
        if (this.width) {
            column.style.width = this.width + '%';
        }
        if (this.fixedHeader) {
            column.classList.add("fixedHeader_" + this.parentId);
        }
        // HTMLComponent recognized and updated label
        this.labelElement = document.createElement('span');
        column.appendChild(this.labelElement);
        this.labelElement.innerHTML = this.fhml.resolveValueTextOrEmpty(this.componentObj.label);
        this.component = column;
        this.htmlElement = this.component;
        this.contentWrapper = this.component;
        this.container.appendChild(this.htmlElement);
        //console.log("ColumnOptimized before ", this.htmlElement.offsetWidth);
    };
    ;
    /**
     * @Override
     * @param accessibility
     */
    ColumnOptimized.prototype.display = function () {
        this.container.appendChild(this.htmlElement);
    };
    ColumnOptimized.prototype.setAccessibility = function (accessibility) {
        // Alvays show in design mode.
        if (this.designMode && accessibility === 'HIDDEN') {
            accessibility = 'EDIT';
        }
        _super.prototype.setAccessibility.call(this, accessibility);
        if (this.parent instanceof ColumnOptimized && this.parent.subColumnsExists === true) {
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
    ColumnOptimized.prototype.update = function (change) {
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
    ColumnOptimized.prototype.getDefaultWidth = function () {
        return null;
    };
    ;
    ColumnOptimized.prototype.destroy = function (removeFromParent) {
        _super.prototype.destroy.call(this, removeFromParent);
    };
    ColumnOptimized.prototype.getAdditionalButtons = function () {
        return [
            new fh_forms_handler_2.AdditionalButton('moveUp', 'arrow-left', 'Move left'),
            new fh_forms_handler_2.AdditionalButton('moveDown', 'arrow-right', 'Move right')
        ];
    };
    return ColumnOptimized;
}(fh_forms_handler_1.HTMLFormComponent));
exports.ColumnOptimized = ColumnOptimized;
//# sourceMappingURL=ColumnOptimized.js.map