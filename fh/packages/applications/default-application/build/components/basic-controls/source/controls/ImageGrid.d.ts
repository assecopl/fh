import { HTMLFormComponent } from "fh-forms-handler";
declare class ImageGrid extends HTMLFormComponent {
    private values;
    private subsystem;
    private grid;
    private selectedIndex;
    constructor(componentObj: any, parent: HTMLFormComponent);
    create(): void;
    update(change: any): void;
    clearValues(): void;
    setValues(values: any): void;
    extractChangedAttributes(): {};
}
export { ImageGrid };
