import {HTMLFormComponent} from "fh-forms-handler";
import {TableOptimized} from "./TableOptimized";
import {TableRow} from "../TableRow";

class TableRowOptimized extends TableRow {
    readonly parent: TableOptimized;
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
        super.addComponent(componentObj, 'TableCellOptimized');
    };
    /**
     * @Override
     * @param accessibility
     */
    public display(): void {
        super.display();
    }
}

export {TableRowOptimized};
