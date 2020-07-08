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
var TableRow_1 = require("../TableRow");
var TableRowOptimized = /** @class */ (function (_super) {
    __extends(TableRowOptimized, _super);
    // private onRowClick:any = null;
    // private selectable:boolean = true;
    function TableRowOptimized(componentObj, parent) {
        return _super.call(this, componentObj, parent) || this;
    }
    TableRowOptimized.prototype.create = function () {
        var row = document.createElement('tr');
        row.id = this.id;
        row.dataset.id = this.id;
        row.dataset.mainId = this.mainId;
        row.classList.add(this.parentId + "_tr");
        this.component = row;
        this.htmlElement = this.component;
        this.contentWrapper = this.htmlElement;
        if (this.onRowClickEvent) {
            // @ts-ignore
            this.component.style.cursor = 'pointer';
            this.htmlElement.addEventListener('click', function () {
                this.onRowClickEvent();
                this.highlightRow();
            }.bind(this));
        }
        if (this.componentObj.data) {
            this.addComponents(this.componentObj.data);
        }
        this.highlightRow();
        this.display();
    };
    ;
    TableRowOptimized.prototype.addComponent = function (componentObj) {
        _super.prototype.addComponent.call(this, componentObj, 'TableCellOptimized');
    };
    ;
    /**
     * @Override
     * @param accessibility
     */
    TableRowOptimized.prototype.display = function () {
        _super.prototype.display.call(this);
    };
    return TableRowOptimized;
}(TableRow_1.TableRow));
exports.TableRowOptimized = TableRowOptimized;
//# sourceMappingURL=TableRowOptimized.js.map