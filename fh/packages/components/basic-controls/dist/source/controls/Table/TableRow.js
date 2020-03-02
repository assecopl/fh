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
var TableRow = /** @class */ (function (_super) {
    __extends(TableRow, _super);
    function TableRow(componentObj, parent) {
        var _this = _super.call(this, componentObj, parent) || this;
        _this.combinedId = _this.parentId + '[' + _this.id + ']';
        _this.mainId = _this.componentObj.mainId;
        _this.isEmpty = _this.componentObj.empty;
        _this.container = parent.dataWrapper;
        return _this;
    }
    TableRow.prototype.create = function () {
        var row = document.createElement('tr');
        row.id = this.id;
        row.dataset.id = this.id;
        row.dataset.mainId = this.mainId;
        this.component = row;
        this.htmlElement = this.component;
        this.contentWrapper = this.htmlElement;
        this.display();
        if (this.componentObj.data) {
            this.addComponents(this.componentObj.data);
        }
    };
    ;
    TableRow.prototype.addComponent = function (componentObj) {
        _super.prototype.addComponent.call(this, componentObj);
        if (componentObj.type !== 'TableCell') {
            var component = this.components[this.components.length - 1];
            component.htmlElement.classList.remove('col-' + component.width);
            component.htmlElement.classList.add('col-md-12');
            var td = document.createElement('td');
            var row = document.createElement('div');
            //row.classList.add('row'); - removed to avoid horizontal scroll and problems with sticky header
            row.appendChild(component.htmlElement);
            td.appendChild(row);
            this.contentWrapper.appendChild(td);
        }
    };
    ;
    return TableRow;
}(fh_forms_handler_1.HTMLFormComponent));
exports.TableRow = TableRow;
//# sourceMappingURL=TableRow.js.map