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
var TableFixedHeaderAndHorizontalScroll_1 = require("./TableFixedHeaderAndHorizontalScroll");
var TableWithKeyboardEvents = /** @class */ (function (_super) {
    __extends(TableWithKeyboardEvents, _super);
    function TableWithKeyboardEvents(componentObj, parent) {
        var _this = _super.call(this, componentObj, parent) || this;
        /**
         * Rewrited logic
         */
        // Row selection
        _this.selectable = true;
        _this.onRowClick = null;
        _this.ctrlIsPressed = false;
        _this.rows = [];
        _this.multiselect = false;
        _this.doneEventInterval = 500; //event delay in miliseconds
        _this.loopDown = false; //key event loop turn on/off
        _this.onRowClick = _this.componentObj.onRowClick;
        _this.selectable = _this.componentObj.selectable || true;
        _this.multiselect = _this.componentObj.multiselect || false;
        _this.rawValue = _this.componentObj.rawValue || _this.componentObj.selectedRowsNumbers || [];
        return _this;
    }
    TableWithKeyboardEvents.prototype.initExtends = function () {
        _super.prototype.initExtends.call(this);
        if (this.selectable && this.onRowClick) {
            this.table.addEventListener('keydown', this.tableKeydownEvent.bind(this));
            this.table.addEventListener('keyup', this.tableKeyupEvent.bind(this));
        }
        this.bindKeyboardEvents();
        this.table.addEventListener('mousedown', this.tableMousedownEvent.bind(this));
    };
    //Capture ctrl click for keyboard events
    TableWithKeyboardEvents.prototype.tableKeydownEvent = function (event) {
        if (event.which == "17") {
            this.ctrlIsPressed = true;
        }
    };
    //Realese ctrl click for keyboard events
    TableWithKeyboardEvents.prototype.tableKeyupEvent = function (e) {
        this.ctrlIsPressed = false;
        if (e.which == 9 && $(document.activeElement).is(":input")) {
            var parent_1 = $(document.activeElement).parents('tbody tr:not(.emptyRow)');
            if (parent_1 && parent_1.length > 0) {
                parent_1.trigger('click');
            }
        }
    };
    TableWithKeyboardEvents.prototype.tableMousedownEvent = function (event) {
        if (event.ctrlKey) {
            event.preventDefault();
        }
    };
    TableWithKeyboardEvents.prototype.bindKeyboardEvents = function () {
        this.table.addEventListener('keydown', function (e) {
            if (document.activeElement == this.table) {
                if (e.which == 40) { // strzalka w dol
                    clearTimeout(this.keyEventTimer);
                    e.preventDefault();
                    var current = $(this.htmlElement).find('tbody tr.table-primary');
                    var next = null;
                    if (current.length == 0) {
                        next = $(this.htmlElement).find('tbody tr:not(.emptyRow)').first();
                    }
                    else {
                        next = current.next('tr:not(.emptyRow)');
                    }
                    //If there isn't next element we go back to first one.
                    if (next && next.length == 0 && this.loopDown) {
                        next = $(this.htmlElement).find('tbody tr:not(.emptyRow)').first();
                    }
                    if (next && next.length > 0) {
                        current.removeClass('table-primary');
                        next.addClass('table-primary');
                        this.scrolToRow(next);
                        this.keyEventTimer = setTimeout(function (elem) {
                            elem ? elem.trigger('click') : false;
                        }, this.doneEventInterval, next);
                    }
                    else {
                        this.keyEventTimer = setTimeout(function (elem) {
                            elem ? elem.trigger('click') : false;
                        }, this.doneEventInterval, current);
                    }
                }
                else if (e.which == 38) { // strzalka w gore
                    e.preventDefault();
                    clearTimeout(this.keyEventTimer);
                    var current = $(this.htmlElement).find('tbody tr.table-primary');
                    var prev = null;
                    if (current.length == 0) {
                        prev = $(this.htmlElement).find('tbody tr:not(.emptyRow)').first();
                    }
                    else {
                        prev = current.prev('tr:not(.emptyRow)');
                    }
                    if (prev && prev.length == 0) {
                        $(this.component).scrollTop(0);
                        this.keyEventTimer = setTimeout(function (elem) {
                            elem ? elem.trigger('click') : false;
                        }, this.doneEventInterval, current);
                    }
                    else if (prev && prev.length > 0) {
                        current.removeClass('table-primary');
                        prev.addClass('table-primary');
                        this.scrolToRow(prev);
                        this.keyEventTimer = setTimeout(function (elem) {
                            elem ? elem.trigger('click') : false;
                        }, this.doneEventInterval, prev);
                    }
                }
                else if (e.which == 33 || e.which == 36) { // pgup i home
                    e.preventDefault();
                    var first = $(this.htmlElement).find('tbody tr:not(.emptyRow)').first();
                    if (first && first.length > 0) {
                        $(this.component).scrollTop(0);
                        first.trigger('click');
                    }
                }
                else if (e.which == 34 || e.which == 35) { // pgdown i end
                    e.preventDefault();
                    var last = $(this.htmlElement).find('tbody tr:not(.emptyRow)').last();
                    if (last && last.length > 0) {
                        $(this.component).scrollTop($(last).position().top);
                        last.trigger('click');
                    }
                }
            }
        }.bind(this));
    };
    /**
     * Used For Optimized Tables
     * @param scrollAnimate
     */
    TableWithKeyboardEvents.prototype.highlightSelectedRows = function (scrollAnimate) {
        if (scrollAnimate === void 0) { scrollAnimate = false; }
        var oldSelected = this.table.querySelectorAll('.table-primary');
        if (oldSelected && oldSelected.length) {
            [].forEach.call(oldSelected, function (row) {
                row.classList.remove('table-primary');
            }.bind(this));
        }
        (this.rawValue || []).forEach(function (value) {
            if (value != -1) {
                var row = this.rows[parseInt(value)];
                row.highlightRow(scrollAnimate);
            }
        }.bind(this));
    };
    ;
    TableWithKeyboardEvents.prototype.update = function (change) {
        _super.prototype.update.call(this, change);
        if (change.changedAttributes) {
            $.each(change.changedAttributes, function (name, newValue) {
                switch (name) {
                    case 'selectedRowNumber':
                        this.rawValue = change.changedAttributes['selectedRowNumber'];
                        this.highlightSelectedRows();
                        break;
                }
            }.bind(this));
        }
    };
    ;
    TableWithKeyboardEvents.prototype.destroy = function (removeFromParent) {
        this.table.removeEventListener('mousedown', this.tableMousedownEvent);
        if (this.selectable && this.onRowClick) {
            this.table.removeEventListener('keydown', this.tableKeydownEvent.bind(this));
            this.table.removeEventListener('keyup', this.tableKeyupEvent.bind(this));
        }
        _super.prototype.destroy.call(this, removeFromParent);
    };
    TableWithKeyboardEvents.prototype.keyEventLoopDownTurnOff = function () {
        this.loopDown = false;
    };
    return TableWithKeyboardEvents;
}(TableFixedHeaderAndHorizontalScroll_1.TableFixedHeaderAndHorizontalScroll));
exports.TableWithKeyboardEvents = TableWithKeyboardEvents;
//# sourceMappingURL=TableWithKeyboardEvents.js.map