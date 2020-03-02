import { HTMLFormComponent } from "fh-forms-handler";
declare class MarkdownGrid extends HTMLFormComponent {
    private values;
    private subsystem;
    private grid;
    private selectedIndex;
    private selectedFolder;
    private subDirectories;
    private showDir;
    constructor(componentObj: any, parent: HTMLFormComponent);
    create(): void;
    update(change: any): void;
    clearValues(): void;
    setValues(values: Array<any>): void;
    extractChangedAttributes(): {};
    private addElement;
    private addFolderUp;
}
export { MarkdownGrid };
