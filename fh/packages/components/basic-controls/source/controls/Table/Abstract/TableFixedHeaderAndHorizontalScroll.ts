import {FhContainer, HTMLFormComponent} from "fh-forms-handler";
import {ColumnOptimized} from "../Optimized/ColumnOptimized";
import {Column} from "../Column";

abstract class TableFixedHeaderAndHorizontalScroll extends HTMLFormComponent {
    public components: ColumnOptimized[] & Column[] = []; //List of columns

    protected abstract table: HTMLTableElement;
    protected abstract header: HTMLTableSectionElement;

    protected fixedHeader: boolean;
    protected fixedHeaderWrapper: HTMLDivElement = null;
    protected initFixedHeader: boolean = false;

    protected clonedTable: any = null;
    protected lastThColumn: any = null;
    protected scrollbarWidth: number = 0;
    protected lastThWidth: number = 0;

    //Resizable columns
    protected thElm: any;

    public horizontalScrolling: boolean = false;
    protected startPageX: any;
    protected startOffset: any;
    protected secondOffset: any;
    protected initColumnResize: boolean = false;

    constructor(componentObj: any, parent: HTMLFormComponent) {
        super(componentObj, parent);
        this.fixedHeader = this.componentObj.fixedHeader || false;
        this.horizontalScrolling = this.componentObj.horizontalScrolling || false;
    }

    protected initExtends() {
        this.calculateColumnWidths();
        this.initFixedHeaderAndScrolling();
    }

    protected initFixedHeaderAndScrolling() {

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

    protected wrap(skipLabel: boolean = false) {
        /**
         *  If header neeed to be fixed we call wrap method with additional wrapper component (inputGroupElement)
         */
        super.wrap(skipLabel, this.fixedHeader);

    }

    public calculateColumnWidths() {
        let total = 100;
        let count = this.components.length;

        this.components.forEach(function (column: any) {
            if (column.width) {
                column.component.style.width = column.width + '%';
                if(this.fixedHeader && this.initFixedHeader) {
                    $('.'+column.id).css('width', column.width + '%');
                }
                total -= column.width;
                count--;
            }
        }.bind(this));
        if (count && total > count) {
            this.components.forEach(function (column: any) {
                if (!column.width) {
                    column.width = total / count;
                    column.component.style.width = column.width + '%';
                    if(this.fixedHeader && this.initFixedHeader) {
                        $('.'+column.id).css('width', column.width + '%');
                    }
                }
            }.bind(this));
        }
        if(this.fixedHeader && this.initFixedHeader) {
             this.calculateColumnsWidth();
        }
    };

    private calculateColumnWidth(column:any, isLat:boolean){
        if (isLat) {
            /**
             * Need to remamber last column to recalculate whne vertical scroll appears.
             */
            this.lastThColumn = column;
            this.lastThWidth = this.lastThColumn.htmlElement.offsetWidth;
        }
        column.htmlElement.dataset.originalWidth =  column.htmlElement.style.width;
        column.htmlElement.style.width = column.htmlElement.offsetWidth + "px";
        column.htmlElement.classList.add(column.id);
        if(this.initFixedHeader){
            /**
             * if fixedheader is already initilized we need to set widths also on cloned objects.
             */
            $('.'+column.id).css("width", column.htmlElement.offsetWidth + "px");
            $('.'+column.id).attr("class", column.htmlElement.classList.toString());
        } else {
            column.htmlElement.style.width = column.htmlElement.offsetWidth + "px";
        }
    }

    private setColumnOriginalWidth(column:any){
        /**
         * Use jquery to make changes to all columns(original and cloned)
         */
        $('.'+column.id).width(column.htmlElement.dataset.originalWidth);
    }

    private calculateColumnsWidth() {
        const colCount = this.components.length;
        (this.components || []).forEach(function (column, index) {
            this.calculateColumnWidth(column, (index == colCount - 1))
        }.bind(this));
    }

    public recalculateColumnWidths() {
        const colCount = this.components.length;
        (this.components || []).forEach(function (column, index) {
            this.setColumnOriginalWidth(column)
        }.bind(this));

        // this.calculateColumnsWidth();

    }


    handleFixedHeader() {
        if (this.fixedHeader && this.inputGroupElement && !this.initFixedHeader && (!this.designMode || this._formId === 'FormPreview')) {
            const colCount = this.components.length;
           this.calculateColumnsWidth();
            this.scrollbarWidth = this.component.offsetWidth - this.component.clientWidth;
            let outter: HTMLDivElement = document.createElement('div');
            let table: any = this.table.cloneNode(false);

            let outterWidth = this.component.style.width ? this.component.style.width : "100%";

            table.classList.add('clonedTableHeader');
            table.style.cssText += "background:white;margin-bottom:0px;";
            outter.style.cssText = "position:absolute;top:0px;left:0px;width:calc(" + outterWidth + " - " + this.scrollbarWidth + "px);overflow:hidden";
            //append main table header into cloned table.
            table.appendChild(this.header);

            //fill main table with cloned header
            $(this.header).clone(true).appendTo(this.table);
            // this.table.appendChild(this.header.cloneNode(true));
            //

            outter.appendChild(table);

            this.inputGroupElement.appendChild(outter);

            this.fixedHeaderWrapper = outter;

            this.component.addEventListener('scroll', function (e) {
                const left = e.currentTarget.scrollLeft;
                this.fixedHeaderWrapper.scrollLeft = left;
            }.bind(this));
            this.initFixedHeader = true;
            this.updateFixedHeaderWidth();
        }
    }



    handleColumnResize(): void {
        if (!this.initColumnResize && !this.horizontalScrolling) {
            const colCount = this.components.length;
            (this.components || []).forEach(function (column, index) {
                if (index == colCount - 1) {
                    this.lastThColumn = column;
                    this.lastThWidth = this.lastThColumn.htmlElement.offsetWidth;
                }
                if (!column.htmlElement.classList.contains(column.id)) {
                    column.htmlElement.classList.add(column.id)
                }
                if (column.htmlElement.nextSibling) {
                    let col = null;
                    col = $("." + column.id);

                    col.css('position', 'relative');

                    let grip: HTMLDivElement = document.createElement('div');
                    grip.innerHTML = "&nbsp;";
                    grip.style.cssText = "background:transparent;top:0px;right:0px;width:5px;position:absolute;cursor:col-resize;";
                    grip.style.height = this.container.offsetHeight + "px";
                    grip.classList.add(this.table.id + "_grip");

                    const listener = function (e) {
                        this.thElm = this.container.querySelectorAll("." + column.id);
                        this.scrollbarWidth = this.component.offsetWidth - this.component.clientWidth;
                        this.lastThWidth = this.lastThColumn.htmlElement.offsetWidth;
                        const htmlElement = column.htmlElement;
                        this.startPageX = e.pageX;
                        this.startOffset = htmlElement.offsetWidth;
                        this.secondOffset = htmlElement.nextSibling ? (htmlElement.nextSibling.offsetWidth) : (htmlElement.previousSibling.offsetWidth);
                    }.bind(this);

                    col.each(function (index) {
                        const gripClone = <any>grip.cloneNode(true);
                        gripClone.addEventListener('mousedown', listener);
                        $(this).append(gripClone);
                    });

                }
            }.bind(this));

            this.container.addEventListener('mousemove', function (e) {
                if (this.thElm) {
                    let scrollbarWidth = this.component.offsetWidth - this.component.clientWidth;
                    [].forEach.call(this.thElm, function (column) {
                        let lastScroll = 0;
                        if (column.nextSibling) {
                            const colWidth = this.startOffset + (e.pageX - this.startPageX);
                            let nextSiblingWidth = this.secondOffset - (e.pageX - this.startPageX);
                            //Minimal column width is 20px. We can't go less.

                           if(colWidth > 25 && nextSiblingWidth > 25) {
                               //check is this is last column. If scrollbar appeared we need to calculate width of lats column.
                               if (!column.nextSibling.nextSibling) {
                                   nextSiblingWidth = nextSiblingWidth - scrollbarWidth;
                               } else {
                                   if(this.scrollbarWidth != scrollbarWidth) {
                                       $("." + this.lastThColumn.id).css('width', this.lastThWidth - scrollbarWidth + 'px');
                                   }
                               }
                               column.nextSibling.style.width = nextSiblingWidth + 'px'
                               column.style.width = colWidth + 'px';
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
    }


    recalculateGripHeight() {
        $("." + this.table.id + "_grip").css('height', this.table.offsetHeight + "px");
    }

    setAccessibility(accessibility) {
        super.setAccessibility(accessibility);

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
    }

    public updateFixedHeaderWidth() {
        if (this.componentObj.height && this.fixedHeader && this.initFixedHeader) {
            const scrollbarWidth = this.component.offsetWidth - this.component.clientWidth;
            if (scrollbarWidth != this.scrollbarWidth && this.fixedHeaderWrapper) {
                let outterWidth = this.component.style.width ? this.component.style.width : "100%";
                this.fixedHeaderWrapper.style.width = "calc("+outterWidth+" - " + scrollbarWidth + "px)";
                this.scrollbarWidth = scrollbarWidth;
                $("." + this.lastThColumn.id).css('width', (this.lastThWidth - scrollbarWidth) + 'px');
            }
        }
    }

    refreashHeader(){
        this.fixedHeaderWrapper.remove();
        this.initFixedHeader = false;
        this.handleFixedHeader();
    }


    scrolToRow(jQueryRowObject: any, animate: boolean = false) {
        let offset = $(jQueryRowObject).position().top;
        if (this.fixedHeader) {
            offset -= (this.component.clientHeight) / 2;
        } else {
            offset -= this.component.clientHeight / 2;
        }
        if (animate) {
            $(this.component).animate({
                scrollTop: offset
            });
        } else {
            $(this.component).scrollTop(offset);
        }
    }

    // fixedHeaderEvent(e) {
    //     const el = e.target;
    //     $(this.header)
    //         .find('th')
    //         .css('transform', 'translateY(' + el.scrollTop + 'px)');
    // }

}

export {TableFixedHeaderAndHorizontalScroll};
