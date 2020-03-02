import { HTMLFormComponent } from "fh-forms-handler";
import { Table } from "./Table";
declare class TableRow extends HTMLFormComponent {
    private readonly mainId;
    private isEmpty;
    constructor(componentObj: any, parent: Table);
    create(): void;
    addComponent(componentObj: any): void;
}
export { TableRow };
