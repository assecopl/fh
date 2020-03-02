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
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
};
Object.defineProperty(exports, "__esModule", { value: true });
var fh_forms_handler_1 = require("fh-forms-handler");
var inversify_inject_decorators_1 = require("inversify-inject-decorators");
var fh_forms_handler_2 = require("fh-forms-handler");
var fh_forms_handler_3 = require("fh-forms-handler");
var lazyInject = inversify_inject_decorators_1.default(fh_forms_handler_3.FhContainer).lazyInject;
var TableCell = /** @class */ (function (_super) {
    __extends(TableCell, _super);
    function TableCell(componentObj, parent) {
        var _this = _super.call(this, componentObj, parent) || this;
        _this.rowspan = _this.componentObj.rowspan || null;
        _this.horizontalAlign = _this.componentObj.horizontalAlign || null;
        _this.verticalAlign = _this.componentObj.verticalAlign || null;
        _this.designMode = _this.componentObj.designMode || (_this.parent != null && _this.parent.designMode);
        // @ts-ignore
        _this.ieFocusFixEnabled = _this.parent.parent.ieFocusFixEnabled;
        return _this;
    }
    TableCell.prototype.create = function () {
        var cell = null;
        // @ts-ignore
        if (this.fh.isIE() && this.ieFocusFixEnabled == true) {
            cell = document.createElement('td-a');
        }
        else {
            cell = document.createElement('td');
        }
        cell.id = this.id;
        cell.classList.add('tableCell');
        if (this.rowspan) {
            // @ts-ignore
            cell.rowSpan = this.rowspan;
        }
        if (this.horizontalAlign) {
            cell.classList.add('col-halign-' + this.horizontalAlign.toLowerCase());
        }
        if (this.verticalAlign) {
            cell.classList.add('col-valign-' + this.verticalAlign.toLowerCase());
        }
        var row = null;
        if (this.fh.isIE() && this.ieFocusFixEnabled == true) {
            row = document.createElement('div-a');
        }
        else {
            row = document.createElement('div');
        }
        row.classList.add('pl-2');
        row.classList.add('pr-2');
        row.classList.add('row');
        cell.appendChild(row);
        this.component = cell;
        this.htmlElement = this.component;
        this.contentWrapper = row;
        this.display();
        if (this.componentObj.visibility === "HIDDEN") {
            cell.classList.add('d-none');
        }
        if (this.componentObj.tableCells) {
            this.addComponents(this.componentObj.tableCells);
        }
    };
    ;
    TableCell.prototype.applyChange = function (change) {
        // console.log('-------------------', this.id, this.combinedId);
        if (this.id === change.formElementId) {
            // console.log('--', this.id, change.formElementId, change);
            this.update(change);
        }
        else {
            this.components.forEach(function (component) {
                // console.log(component.id, change.formElementId, change);
                component.applyChange(change);
            });
        }
    };
    ;
    TableCell.prototype.setAccessibility = function (accessibility) {
        _super.prototype.setAccessibility.call(this, accessibility);
        if (!this.designMode) {
            this.htmlElement.classList.remove('disabled', 'fc-disabled');
            this.htmlElement.classList.add('fc-editable');
        }
        // remember accessibility changes in source compomenet objects to keep this change after refreshing the table
        this.componentObj.accessibility = accessibility;
        this.componentObj.visibility = accessibility;
    };
    ;
    __decorate([
        lazyInject("FH"),
        __metadata("design:type", fh_forms_handler_2.FH)
    ], TableCell.prototype, "fh", void 0);
    return TableCell;
}(fh_forms_handler_1.HTMLFormComponent));
exports.TableCell = TableCell;
//# sourceMappingURL=TableCell.js.map