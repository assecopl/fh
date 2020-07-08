import { HTMLFormComponent } from "fh-forms-handler";
declare class Tree extends HTMLFormComponent {
    private icons;
    constructor(componentObj: any, parent: HTMLFormComponent);
    create(): void;
    decomposeIcon(icon: any): any;
    addComponents(componentsList: any): void;
    collapseAll(): void;
    expandAll(): void;
}
export { Tree };
