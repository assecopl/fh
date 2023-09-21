import {HTMLFormComponent} from "fh-forms-handler";
import {TableFixedHeaderAndHorizontalScroll} from "./TableFixedHeaderAndHorizontalScroll";
import {TableRowOptimized} from "./../Optimized/TableRowOptimized";

abstract class TableWithKeyboardEvents extends TableFixedHeaderAndHorizontalScroll {

    /**
     * Rewrited logic
     */
        // Row selection
    protected readonly selectable: boolean = true;
    protected readonly onRowClick: any = null;
    protected ctrlIsPressed: any = false;
    protected shiftIsPressed: any = false;
    protected rows: Array<TableRowOptimized> = [];
    protected multiselect: boolean = false;

    protected keyEventTimer: any;                //timer identifier
    protected doneEventInterval: number = 500;   //event delay in miliseconds

    private loopDown: boolean = false; //key event loop turn on/off

    constructor(componentObj: any, parent: HTMLFormComponent) {
        super(componentObj, parent);

        this.onRowClick = this.componentObj.onRowClick;
        this.selectable = this.componentObj.selectable || true;
        this.multiselect = this.componentObj.multiselect || false;
        this.rawValue = this.componentObj.rawValue || this.componentObj.selectedRowsNumbers || [];

    }

    protected initExtends() {
        super.initExtends();
        if (this.selectable && this.onRowClick) {
            this.table.addEventListener('keydown', this.tableKeydownEvent.bind(this));
            this.table.addEventListener('keyup', this.tableKeyupEvent.bind(this));
        }
        this.bindKeyboardEvents();
        this.table.addEventListener('mousedown', this.tableMousedownEvent.bind(this));
    }

    //Capture ctrl click for keyboard events
    private tableKeydownEvent(event) {
        if (event.which == "17") {
            this.ctrlIsPressed = true;
        }
        if (event.which == "16") {
            this.shiftIsPressed = true;
        }
    }

    abstract onRowClickEvent(e, mainId, silent);

    //Realese ctrl click for keyboard events
    public tableKeyupEvent(e) {
        this.ctrlIsPressed = false;
        this.shiftIsPressed = false;
        if (e.which == 9 && $(document.activeElement).is("input:not([type='checkbox'])")) {
            let parent = $(document.activeElement).parents('tbody tr:not(.emptyRow)');
            if (parent && parent.length > 0) {
                // @ts-ignore
                this.onRowClickEvent({ ctrlKey: false, shiftKey: false }, parent[0].dataset.mainId, true);
            }
        }
    }

    tableMousedownEvent(event) {
        if (event.ctrlKey) {
            event.preventDefault();
        }
        if (event.shiftKey) {
            event.preventDefault();
        }
    }

    protected bindKeyboardEvents() {
        this.table.addEventListener('keydown', function (e) {
            if (document.activeElement == this.table) {
                if (e.which == 40) { // strzalka w dol
                    clearTimeout(this.keyEventTimer);
                    e.preventDefault();
                    let current = $(this.htmlElement).find('tbody tr.table-primary');
                    let next = null;

                    if (current.length == 0) {
                        next = $(this.htmlElement).find('tbody tr:not(.emptyRow)').first();
                    } else {
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
                        this.scrolIntoView(next);
                        this.keyEventTimer = setTimeout(function (elem) {
                            elem ? elem.trigger('click') : false;
                        }, this.doneEventInterval, next);
                    } else {
                        this.keyEventTimer = setTimeout(function (elem) {
                            elem ? elem.trigger('click') : false;
                        }, this.doneEventInterval, current);
                    }
                } else if (e.which == 38) { // strzalka w gore
                    e.preventDefault();
                    clearTimeout(this.keyEventTimer);
                    let current = $(this.htmlElement).find('tbody tr.table-primary');
                    let prev = null;

                    if (current.length == 0) {
                        prev = $(this.htmlElement).find('tbody tr:not(.emptyRow)').first();
                    } else {
                        prev = current.prev('tr:not(.emptyRow)');
                    }
                    if (prev && prev.length == 0) {
                        $(this.component).scrollTop(0);
                        this.keyEventTimer = setTimeout(function (elem) {
                            elem ? elem.trigger('click') : false;
                        }, this.doneEventInterval, current);
                    } else if (prev && prev.length > 0) {
                        current.removeClass('table-primary');
                        prev.addClass('table-primary');
                        this.scrolToRow(prev);
                        this.scrolIntoView(prev);
                        this.keyEventTimer = setTimeout(function (elem) {
                            elem ? elem.trigger('click') : false;
                        }, this.doneEventInterval, prev);
                    }


                } else if (e.which == 33 || e.which == 36) { // pgup i home
                    e.preventDefault();

                    let first = $(this.htmlElement).find('tbody tr:not(.emptyRow)').first();

                    if (first && first.length > 0) {
                        $(this.component).scrollTop(0);
                        first.trigger('click');
                        this.scrolIntoView(first);
                    }
                } else if (e.which == 34 || e.which == 35) { // pgdown i end
                    e.preventDefault();

                    let last = $(this.htmlElement).find('tbody tr:not(.emptyRow)').last();

                    if (last && last.length > 0) {
                        $(this.component).scrollTop($(last).position().top);
                        last.trigger('click');
                        this.scrolIntoView(last);

                    }
                } else if (e.which == 13) {
                    e.preventDefault();

                    let current = $(this.htmlElement).find('tbody tr.table-primary');
                    if (current.length) {
                        this.onRowDblClick(e, current[0]);
                    }
                }
            }
        }.bind(this));
    }

    /**
     * Used For Optimized Tables
     * @param scrollAnimate
     */
    protected highlightSelectedRows(scrollAnimate: boolean = false) {
        this.rows.forEach(row => {
           row.unhighlightRow();
        });
        (this.rawValue || []).forEach(function (value) {
            if (value != -1) {
                const row: TableRowOptimized = this.rows[parseInt(value)];
                row.highlightRow(scrollAnimate);
                if (this.selectionCheckboxes) {
                    row.component.querySelector('input[type="checkbox"]').checked = true;
                }
            } else {
                this.scrollTopInside();
            }


        }.bind(this));
    };

    update(change) {
        super.update(change);
    };


    destroy(removeFromParent) {
        this.table.removeEventListener('mousedown', this.tableMousedownEvent);
        if (this.selectable && this.onRowClick) {
            this.table.removeEventListener('keydown', this.tableKeydownEvent.bind(this));
            this.table.removeEventListener('keyup', this.tableKeyupEvent.bind(this));
        }
        super.destroy(removeFromParent);
    }

    public keyEventLoopDownTurnOff() {
        this.loopDown = false;
    }

}

export {TableWithKeyboardEvents};
