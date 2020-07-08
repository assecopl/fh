import { HTMLFormComponent } from "fh-forms-handler";
declare class Widget extends HTMLFormComponent {
    private isCollapsible;
    private onToggle;
    private collapsed;
    private collapseToggler;
    private collapseChanged;
    private groupToolbox;
    private height;
    private attributes;
    constructor(componentObj: any, parent: HTMLFormComponent);
    create(): void;
    update(change: any): void;
    addComponent(componentObj: any): void;
    toggleCollapse(): void;
    collapse(): void;
    uncollapse(): void;
    focusCurrentComponent(deferred: any, options: any): any;
    extractChangedAttributes(): {};
    destroy(removeFromParent: any): void;
}
export { Widget };
