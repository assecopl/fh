import {HTMLFormComponent} from "fh-forms-handler";
import {TableOptimized} from "./TableOptimized";

class TableRowOptimized extends HTMLFormComponent {
    private readonly mainId: string;
    private isEmpty: any;
    readonly parent: TableOptimized;
    // private onRowClick:any = null;
    // private selectable:boolean = true;
    private onRowClickEvent:() => void = null;

    constructor(componentObj: any, parent: TableOptimized & any) {
        super(componentObj, parent);

        this.onRowClickEvent = this.componentObj.onRowClickEvent;
        this.combinedId = this.parentId + '[' + this.id + ']';
        this.mainId = this.componentObj.mainId;
        this.isEmpty = this.componentObj.empty;
        this.container = parent.dataWrapper;
    }

    create() {
        let row = document.createElement('tr');
        row.id = this.id;
        row.dataset.id = this.id;
        row.dataset.mainId = this.mainId;
        row.classList.add(this.parentId+"_tr");

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

    addComponent(componentObj) {
        super.addComponent(componentObj);

        if (componentObj.type !== 'TableCellOptimized') {
            let component = this.components[this.components.length - 1];
            component.htmlElement.classList.remove('col-' + component.width);
            component.htmlElement.classList.add('col-md-12');
            let td = document.createElement('td');

            let row = document.createElement('div');
            //row.classList.add('row'); - removed to avoid horizontal scroll and problems with sticky header
            row.appendChild(component.htmlElement);
            td.appendChild(row);
            this.contentWrapper.appendChild(td);
        }
    };
    /**
     * @Override
     * @param accessibility
     */
    public display(): void {
        super.display();
        // this.container.appendChild(this.htmlElement);
    }


    public render() {
        // console.log("TableRowOptimized render - do nothing");
    }

    public highlightRow(scrollAnimate:boolean = false) {
        const idx = ((this.parent.rawValue) || []).findIndex(function (element, index) {
                return element == this.mainId
        }.bind(this));
            if (idx != -1) {
                this.component.classList.add('table-primary');
                let container = $(this.parent.component);
                let scrollTo = $(this.component);
                if (this.parent.rawValue.length < 2) {
                    let containerHeight = container.height();
                    let containerScrollTop = container.scrollTop();
                    let realPositionElement = scrollTo.position().top;
                    if (realPositionElement < containerScrollTop || realPositionElement
                        > containerScrollTop
                        + containerHeight) {
                        this.parent.scrolToRow($(this.component), scrollAnimate);
                    }
                }
            } else {
                this.component.classList.remove('table-primary');
            }
    };
}

export {TableRowOptimized};
