import { HTMLFormComponent } from "fh-forms-handler";
import { AdditionalButton } from "fh-forms-handler";
declare class PanelGroup extends HTMLFormComponent {
    protected readonly isCollapsible: any;
    private readonly onToggle;
    private collapsed;
    private collapseToggler;
    private collapseChanged;
    protected groupToolbox: any;
    private readonly height;
    private readonly borderVisible;
    private collapsedOld;
    private headingElement;
    protected forceHeader: any;
    constructor(componentObj: any, parent: HTMLFormComponent);
    create(): void;
    update(change: any): void;
    updateHeaderVisibility(newTitle: any): void;
    toggleCollapse(): void;
    collapse(): void;
    uncollapse(): void;
    extractChangedAttributes(): {};
    resolveValue: (value: any) => any;
    setPresentationStyle(presentationStyle: any): void;
    getAdditionalButtons(): AdditionalButton[];
}
export { PanelGroup };
