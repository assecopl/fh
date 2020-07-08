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
var TableFixedHeaderAndHorizontalScroll = /** @class */ (function (_super) {
    __extends(TableFixedHeaderAndHorizontalScroll, _super);
    function TableFixedHeaderAndHorizontalScroll(componentObj, parent) {
        var _this = _super.call(this, componentObj, parent) || this;
        _this.components = []; //List of columns
        _this.fixedHeaderWrapper = null;
        _this.initFixedHeader = false;
        _this.clonedTable = null;
        _this.lastThColumn = null;
        _this.scrollbarWidth = 0;
        _this.lastThWidth = 0;
        _this.horizontalScrolling = false;
        _this.initColumnResize = false;
        _this.fixedHeader = _this.componentObj.fixedHeader || false;
        _this.horizontalScrolling = _this.componentObj.horizontalScrolling || false;
        return _this;
    }
    TableFixedHeaderAndHorizontalScroll.prototype.initExtends = function () {
        this.calculateColumnWidths();
        this.initFixedHeaderAndScrolling();
    };
    TableFixedHeaderAndHorizontalScroll.prototype.initFixedHeaderAndScrolling = function () {
        if (this.fixedHeader) {
            this.table.classList.add('tableOptimizedFixedHeader');
            this.inputGroupElement.style.position = "relative";
            this.inputGroupElement.className = "";
        }
        /**
         * Fire fixed header logic after tabel dispaly becouse we need to have widths to fixed columns widths.
         */
        this.component.addEventListener('mouseover', function () {
            this.handleFixedHeader();
            this.handleColumnResize();
        }.bind(this));
        this.component.onscroll = this.handleFixedHeader.bind(this);
    };
    ;
    TableFixedHeaderAndHorizontalScroll.prototype.wrap = function (skipLabel) {
        if (skipLabel === void 0) { skipLabel = false; }
        /**
         *  If header neeed to be fixed we call wrap method with additional wrapper component (inputGroupElement)
         */
        _super.prototype.wrap.call(this, skipLabel, this.fixedHeader);
    };
    TableFixedHeaderAndHorizontalScroll.prototype.calculateColumnWidths = function () {
        var total = 100;
        var count = this.components.length;
        this.components.forEach(function (column) {
            if (column.width) {
                column.component.style.width = column.width + '%';
                if (this.fixedHeader && this.initFixedHeader) {
                    $('.' + column.id).css('width', column.width + '%');
                }
                total -= column.width;
                count--;
            }
        }.bind(this));
        if (count && total > count) {
            this.components.forEach(function (column) {
                if (!column.width) {
                    column.width = total / count;
                    column.component.style.width = column.width + '%';
                    if (this.fixedHeader && this.initFixedHeader) {
                        $('.' + column.id).css('width', column.width + '%');
                    }
                }
            }.bind(this));
        }
        if (this.fixedHeader && this.initFixedHeader) {
            this.calculateColumnsWidth();
        }
    };
    ;
    TableFixedHeaderAndHorizontalScroll.prototype.calculateColumnWidth = function (column, precentage) {
        this.lastThColumn = column;
        column.htmlElement.dataset.originalWidth = column.htmlElement.style.width;
        column.htmlElement.classList.add(column.id);
        var offsetWidth = column.htmlElement.offsetWidth + "px";
        if (precentage) {
            offsetWidth = this.convertToPrecentageWidth(column.htmlElement.offsetWidth) + "%";
        }
        if (this.initFixedHeader) {
            /**
             * if fixedheader is already initilized we need to set widths also on cloned objects.
             */
            $('.' + column.id).css("width", offsetWidth);
            $('.' + column.id).attr("class", column.htmlElement.classList.toString());
        }
        else {
            column.htmlElement.style.width = offsetWidth;
        }
    };
    TableFixedHeaderAndHorizontalScroll.prototype.setColumnOriginalWidth = function (column) {
        /**
         * Use jquery to make changes to all columns(original and cloned)
         */
        $('.' + column.id).width(column.htmlElement.dataset.originalWidth);
    };
    TableFixedHeaderAndHorizontalScroll.prototype.calculateColumnsWidth = function () {
        var colCount = this.components.length;
        //To keep proper size - first we need to set px then rewrite it to %.
        (this.components || []).forEach(function (column, index) {
            this.calculateColumnWidth(column);
        }.bind(this));
        (this.components || []).forEach(function (column, index) {
            this.calculateColumnWidth(column, true);
        }.bind(this));
    };
    TableFixedHeaderAndHorizontalScroll.prototype.recalculateColumnWidths = function () {
        var colCount = this.components.length;
        (this.components || []).forEach(function (column, index) {
            this.setColumnOriginalWidth(column);
        }.bind(this));
        this.calculateColumnsWidth();
    };
    TableFixedHeaderAndHorizontalScroll.prototype.handleFixedHeader = function () {
        if (this.fixedHeader && this.inputGroupElement && !this.initFixedHeader && (!this.designMode || this._formId === 'FormPreview')) {
            this.calculateColumnsWidth();
            this.scrollbarWidth = this.component.offsetWidth - this.component.clientWidth;
            var outter = document.createElement('div');
            var table = this.table.cloneNode(false);
            var outterWidth = this.component.style.width ? this.component.style.width : "100%";
            table.classList.add('clonedTableHeader');
            table.id = table.id + "_clone";
            table.style.cssText += "background:white;margin-bottom:0px;";
            outter.style.cssText = "position:absolute;top:0px;left:0px;width:calc(" + outterWidth + " - " + this.scrollbarWidth + "px);overflow:hidden";
            //append main table header into cloned table.
            table.appendChild(this.header);
            //fill main table with cloned header
            $(this.header).clone(true).appendTo(this.table);
            $(table).find("th").each(function () {
                $(this).attr("id", this.id + "_clone");
            });
            // this.table.appendChild(this.header.cloneNode(true));
            //
            outter.appendChild(table);
            this.inputGroupElement.appendChild(outter);
            this.fixedHeaderWrapper = outter;
            this.component.addEventListener('scroll', function (e) {
                var left = e.currentTarget.scrollLeft;
                this.fixedHeaderWrapper.scrollLeft = left;
            }.bind(this));
            this.initFixedHeader = true;
            this.updateFixedHeaderWidth();
        }
    };
    TableFixedHeaderAndHorizontalScroll.prototype.handleColumnResize = function () {
        if (!this.initColumnResize && !this.horizontalScrolling) {
            var colCount_1 = this.components.length;
            (this.components || []).forEach(function (column, index) {
                if (index == colCount_1 - 1) {
                    this.lastThColumn = column;
                    this.lastThWidth = this.lastThColumn.htmlElement.offsetWidth;
                }
                if (!column.htmlElement.classList.contains(column.id)) {
                    column.htmlElement.classList.add(column.id);
                }
                var sibling = this.getVisiableSibling(column.htmlElement);
                if (sibling) {
                    var col = null;
                    col = $("." + column.id);
                    col.css('position', 'relative');
                    var grip_1 = document.createElement('div');
                    grip_1.innerHTML = "&nbsp;";
                    grip_1.style.cssText = "background:transparent;top:0px;right:0px;width:5px;position:absolute;cursor:col-resize;";
                    grip_1.style.height = this.container.offsetHeight + "px";
                    grip_1.classList.add(this.table.id + "_grip");
                    var listener_1 = function (e) {
                        this.thElm = this.container.querySelectorAll("." + column.id);
                        this.scrollbarWidth = this.component.offsetWidth - this.component.clientWidth;
                        this.lastThWidth = this.lastThColumn.htmlElement.offsetWidth;
                        var htmlElement = column.htmlElement;
                        this.startPageX = e.pageX;
                        this.startOffset = htmlElement.offsetWidth;
                        this.secondOffset = sibling.offsetWidth;
                    }.bind(this);
                    col.each(function (index) {
                        var gripClone = grip_1.cloneNode(true);
                        gripClone.addEventListener('mousedown', listener_1);
                        $(this).append(gripClone);
                    });
                }
            }.bind(this));
            this.container.addEventListener('mousemove', function (e) {
                if (this.thElm) {
                    var scrollbarWidth = this.component.offsetWidth - this.component.clientWidth;
                    [].forEach.call(this.thElm, function (column) {
                        var lastScroll = 0;
                        var sibling = this.getVisiableSibling(column);
                        if (sibling) {
                            var colWidth = this.startOffset + (e.pageX - this.startPageX);
                            var nextSiblingWidth = this.secondOffset - (e.pageX - this.startPageX);
                            //Minimal column width is 20px. We can't go less.
                            if (colWidth > 25 && nextSiblingWidth > 25) {
                                sibling.style.width = this.convertToPrecentageWidth(nextSiblingWidth) + '%';
                                column.style.width = this.convertToPrecentageWidth(colWidth) + '%';
                            }
                        }
                    }.bind(this));
                    this.updateFixedHeaderWidth();
                }
            }.bind(this));
            this.container.addEventListener('mouseup', function (e) {
                this.thElm = undefined;
            }.bind(this));
            this.initColumnResize = true;
        }
    };
    TableFixedHeaderAndHorizontalScroll.prototype.recalculateGripHeight = function () {
        $("." + this.table.id + "_grip").css('height', this.table.offsetHeight + "px");
    };
    TableFixedHeaderAndHorizontalScroll.prototype.setAccessibility = function (accessibility) {
        _super.prototype.setAccessibility.call(this, accessibility);
        switch (accessibility) {
            case 'EDIT':
                this.component.addEventListener('mouseover', function () {
                    this.handleFixedHeader();
                    this.handleColumnResize();
                }.bind(this));
                this.component.addEventListener('scroll', this.handleFixedHeader.bind(this));
                break;
            case 'VIEW':
                this.component.addEventListener('mouseover', function () {
                    this.handleFixedHeader();
                    this.handleColumnResize();
                }.bind(this));
                this.component.addEventListener('scroll', this.handleFixedHeader.bind(this));
                break;
        }
    };
    TableFixedHeaderAndHorizontalScroll.prototype.updateFixedHeaderWidth = function () {
        if (this.componentObj.height && this.fixedHeader && this.initFixedHeader) {
            var scrollbarWidth = this.component.offsetWidth - this.component.clientWidth;
            if (scrollbarWidth != this.scrollbarWidth && this.fixedHeaderWrapper) {
                var outterWidth = this.component.style.width ? this.component.style.width : "100%";
                this.fixedHeaderWrapper.style.width = "calc(" + outterWidth + " - " + scrollbarWidth + "px)";
                this.scrollbarWidth = scrollbarWidth;
            }
        }
    };
    TableFixedHeaderAndHorizontalScroll.prototype.refreashHeader = function () {
        this.fixedHeaderWrapper.remove();
        this.initFixedHeader = false;
        this.handleFixedHeader();
    };
    TableFixedHeaderAndHorizontalScroll.prototype.scrolToRow = function (jQueryRowObject, animate) {
        if (animate === void 0) { animate = false; }
        var offset = $(jQueryRowObject).position().top;
        if (this.fixedHeader) {
            offset -= (this.component.clientHeight) / 2;
        }
        else {
            offset -= this.component.clientHeight / 2;
        }
        if (animate) {
            $(this.component).animate({
                scrollTop: offset
            });
        }
        else {
            $(this.component).scrollTop(offset);
        }
    };
    TableFixedHeaderAndHorizontalScroll.prototype.scrollTopInside = function () {
        if (this.hasHeight()) {
            /**
             * If table is inside scrollable container we scoll it to the top when selection need to be cleared.
             */
            $(this.component).scrollTop(0);
        }
    };
    // fixedHeaderEvent(e) {
    //     const el = e.target;
    //     $(this.header)
    //         .find('th')
    //         .css('transform', 'translateY(' + el.scrollTop + 'px)');
    // }
    /**
     * Function helps get next or prevoius Visible Sibling.
     * @param column
     */
    TableFixedHeaderAndHorizontalScroll.prototype.getVisiableSibling = function (htmlElement) {
        var sibling = htmlElement.nextSibling;
        while (sibling && !this.isVisiable(sibling)) {
            sibling = sibling.nextSibling;
        }
        if (!sibling) {
            var sibling_1 = htmlElement.previousSibling;
            while (sibling_1 && !this.isVisiable(sibling_1)) {
                sibling_1 = sibling_1.previousSibling;
            }
        }
        return sibling;
    };
    TableFixedHeaderAndHorizontalScroll.prototype.isVisiable = function (htmlElement) {
        var style = window.getComputedStyle(htmlElement);
        return !(style.display === 'none');
    };
    TableFixedHeaderAndHorizontalScroll.prototype.convertToPrecentageWidth = function (widthInPx) {
        return (((widthInPx / this.component.clientWidth)) * 100).toExponential(2);
    };
    return TableFixedHeaderAndHorizontalScroll;
}(fh_forms_handler_1.HTMLFormComponent));
exports.TableFixedHeaderAndHorizontalScroll = TableFixedHeaderAndHorizontalScroll;
//# sourceMappingURL=TableFixedHeaderAndHorizontalScroll.js.map