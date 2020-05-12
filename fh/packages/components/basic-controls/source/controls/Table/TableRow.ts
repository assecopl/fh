import {HTMLFormComponent} from "fh-forms-handler";
import {Table} from "./Table";
import {TableOptimized} from "./Optimized/TableOptimized";

class TableRow extends HTMLFormComponent {
    protected readonly mainId: string;
    public readonly parent: TableOptimized;
    protected isEmpty: any;
    protected onRowClickEvent:() => void = null;

    constructor(componentObj: any, parent: Table) {
        super(componentObj, parent);

        this.combinedId = this.parentId + '[' + this.id + ']';
        this.mainId = this.componentObj.mainId;
        this.isEmpty = this.componentObj.empty;
        this.container = parent.dataWrapper;
        this.onRowClickEvent = this.componentObj.onRowClickEvent;
    }

    create() {
        let row = document.createElement('tr');
        row.id = this.id;
        row.dataset.id = this.id;
        row.dataset.mainId = this.mainId;

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

        this.display();

        if (this.componentObj.data) {
            this.addComponents(this.componentObj.data);
        }
    };

    addComponent(componentObj , cellTypeCheck = 'TableCell') {
        super.addComponent(componentObj);

        if (componentObj.type !== cellTypeCheck) {
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
                let elementHeight = scrollTo.height();
                if ((realPositionElement - elementHeight) < containerScrollTop || realPositionElement + elementHeight
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

export {TableRow};
