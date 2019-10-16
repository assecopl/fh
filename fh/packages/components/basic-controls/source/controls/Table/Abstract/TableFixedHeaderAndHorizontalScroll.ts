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

    //Resizable columns
    protected thElm: any;
    public horizontalScrolling:boolean = false;
    protected startPageX: any;
    protected startOffset: any;
    protected secondOffset: any;
    protected initColumnResize: boolean = false;

    constructor(componentObj: any, parent: HTMLFormComponent) {
        super(componentObj, parent);
        this.fixedHeader = this.componentObj.fixedHeader || false;
        this.horizontalScrolling = this.componentObj.horizontalScrolling || false;
    }

    protected initExtends(){
        this.initFixedHeaderAndScrolling();
    }

    protected initFixedHeaderAndScrolling() {

        if (this.fixedHeader) {
            this.table.classList.add('tableOptimizedFixedHeader');
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
                total -= column.width;
                count--;
            }
        });
        if (count && total > count) {
            this.components.forEach(function (column: any) {
                if (!column.width) {
                    column.width = total / count;
                    column.component.style.width = column.width + '%';
                }
            });
        }
    };


    handleFixedHeader() {
        if (this.fixedHeader && this.inputGroupElement && !this.initFixedHeader && (!this.designMode || this._formId === 'FormPreview')) {
            const colCount = this.components.length;
            (this.components || []).forEach(function (column, index) {
                if (index == colCount - 1) {
                    this.lastThColumn = column;
                    column.htmlElement.style.width = "auto";
                } else {
                    column.htmlElement.style.width = column.htmlElement.offsetWidth + "px";
                }
                column.htmlElement.classList.add(column.id);

            }.bind(this));

            const scrollbarWidth = this.component.offsetWidth - this.component.clientWidth;
            let outter: HTMLDivElement = document.createElement('div');
            let table: any = this.table.cloneNode(false);

            table.style.cssText += "background:white;margin-bottom:0px;";
            outter.style.cssText = "position:absolute;top:0px;left:0px;width:calc(100% - " + scrollbarWidth + "px);overflow:hidden";
            table.appendChild(this.header.cloneNode(true));

            outter.appendChild(table);
            this.inputGroupElement.style.position = "relative";
            this.inputGroupElement.className = "";
            this.inputGroupElement.appendChild(outter);

            this.fixedHeaderWrapper = outter;

            this.component.addEventListener('scroll', function (e) {
                const left = e.currentTarget.scrollLeft;
                this.fixedHeaderWrapper.scrollLeft = left;
            }.bind(this));
            this.initFixedHeader = true;
        }
    }

    handleColumnResize(): void {
        if (!this.initColumnResize && !this.horizontalScrolling) {

            (this.components || []).forEach(function (column) {
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
                    grip.style.height = this.container.offsetHeight+"px";
                    grip.classList.add(this.table.id+"_grip");

                    const listener = function (e) {
                        this.thElm = this.container.querySelectorAll("." + column.id);
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
                    [].forEach.call(this.thElm, function (column) {
                        let lastScroll = 0;
                        if (column.nextSibling) {
                            column.nextSibling.style.width = this.secondOffset - (e.pageX - this.startPageX) + 'px';
                            //check is this is last column
                            if(!column.nextSibling.nextSibling){
                                column.style.width = "auto";
                            } else {
                                column.style.width = this.startOffset + (e.pageX - this.startPageX) + 'px';
                            }
                        } else {
                            column.previousSibling.style.width = this.secondOffset - (e.pageX - this.startPageX) + 'px';
                            //Left dynamic width for last column for dynamic scroll appear
                            column.style.width = "auto";
                        }
                        this.updateFixedHeaderWidth();
                    }.bind(this));
                }
            }.bind(this));

            this.container.addEventListener('mouseup', function (e) {
                this.thElm = undefined;
            }.bind(this));

            this.initColumnResize = true;
        }
    }


    recalculateGripHeight(){
        $("."+this.table.id+"_grip").css('height', this.table.offsetHeight+"px") ;
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
        if (this.componentObj.height && this.fixedHeader) {
            const scrollbarWidth = this.component.offsetWidth - this.component.clientWidth;
            if(scrollbarWidth != this.scrollbarWidth && this.fixedHeaderWrapper) {
                this.fixedHeaderWrapper.style.width = "calc(100% - " + scrollbarWidth + "px)";
                this.scrollbarWidth = scrollbarWidth;
            }
        }
    }

}

export {TableFixedHeaderAndHorizontalScroll};
