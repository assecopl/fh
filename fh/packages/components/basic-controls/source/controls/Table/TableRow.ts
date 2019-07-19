import {HTMLFormComponent} from "fh-forms-handler";
import {Table} from "./Table";

class TableRow extends HTMLFormComponent {
    private readonly mainId: string;
    private isEmpty: any;

    constructor(componentObj: any, parent: Table) {
        super(componentObj, parent);

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

        this.component = row;
        this.htmlElement = this.component;
        this.contentWrapper = this.htmlElement;

        this.display();

        if (this.componentObj.data) {
            this.addComponents(this.componentObj.data);
        }
    };

    addComponent(componentObj) {
        super.addComponent(componentObj);

        if (componentObj.type !== 'TableCell') {
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
}

export {TableRow};
