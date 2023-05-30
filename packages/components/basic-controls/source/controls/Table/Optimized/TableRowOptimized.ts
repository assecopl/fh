import {HTMLFormComponent} from "fh-forms-handler";
import {TableOptimized} from "./TableOptimized";
import {TableRow} from "../TableRow";

class TableRowOptimized extends TableRow {
    // private onRowClick:any = null;
    // private selectable:boolean = true;

    constructor(componentObj: any, parent: TableOptimized & any) {
        super(componentObj, parent);
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
            this.htmlElement.addEventListener('click', function (e) {
                this.onRowClickEvent(e);
                this.highlightRow();
            }.bind(this));
        }

        if (this.componentObj.data) {
            this.addComponents(this.componentObj.data);
        }
    };

    addComponent(componentObj) {
        super.addComponent(componentObj, 'TableCellOptimized');
    };

    public display(): void {
        super.display();
        this.highlightRow();

        if (this.isEmpty) {
            this.htmlElement.style.pointerEvents = 'none';
            this.htmlElement.classList.add('emptyRow');
            this.htmlElement.style.backgroundColor = null;
        }
    }
}

export {TableRowOptimized};
