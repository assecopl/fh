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
var TableRowOptimized = /** @class */ (function (_super) {
    __extends(TableRowOptimized, _super);
    function TableRowOptimized(componentObj, parent) {
        var _this = _super.call(this, componentObj, parent) || this;
        // private onRowClick:any = null;
        // private selectable:boolean = true;
        _this.onRowClickEvent = null;
        _this.onRowClickEvent = _this.componentObj.onRowClickEvent;
        _this.combinedId = _this.parentId + '[' + _this.id + ']';
        _this.mainId = _this.componentObj.mainId;
        _this.isEmpty = _this.componentObj.empty;
        _this.container = parent.dataWrapper;
        return _this;
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
        _super.prototype.addComponent.call(this, componentObj);
        if (componentObj.type !== 'TableCellOptimized') {
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
    /**
     * @Override
     * @param accessibility
     */
    TableRowOptimized.prototype.display = function () {
        _super.prototype.display.call(this);
        // this.container.appendChild(this.htmlElement);
    };
    TableRowOptimized.prototype.render = function () {
        // console.log("TableRowOptimized render - do nothing");
    };
    TableRowOptimized.prototype.highlightRow = function (scrollAnimate) {
        if (scrollAnimate === void 0) { scrollAnimate = false; }
        var idx = ((this.parent.rawValue) || []).findIndex(function (element, index) {
            return element == this.mainId;
        }.bind(this));
        if (idx != -1) {
            this.component.classList.add('table-primary');
            var container = $(this.parent.component);
            var scrollTo_1 = $(this.component);
            if (this.parent.rawValue.length < 2) {
                var containerHeight = container.height();
                var containerScrollTop = container.scrollTop();
                var realPositionElement = scrollTo_1.position().top;
                if (realPositionElement < containerScrollTop || realPositionElement
                    > containerScrollTop
                        + containerHeight) {
                    this.parent.scrolToRow($(this.component), scrollAnimate);
                }
            }
        }
        else {
            this.component.classList.remove('table-primary');
        }
    };
    ;
    return TableRowOptimized;
}(fh_forms_handler_1.HTMLFormComponent));
exports.TableRowOptimized = TableRowOptimized;
//# sourceMappingURL=TableRowOptimized.js.map