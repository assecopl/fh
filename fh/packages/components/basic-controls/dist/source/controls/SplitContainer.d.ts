import { HTMLFormComponent } from "fh-forms-handler";
declare class SplitContainer extends HTMLFormComponent {
    private bothShown;
    private divider;
    private sizeleft;
    private sizeright;
    private fixed;
    private resizing;
    private startX;
    private startBasis;
    private resizeListener;
    private windowListenerMouseMove;
    private windowListenerMouseUp;
    constructor(componentObj: any, parent: HTMLFormComponent);
    create(): void;
    createPanelsAndDivider(): void;
    addSideComponent(componentObj: any, side: any): void;
    hideLeft(): void;
    hideRight(): void;
    showBoth(): void;
    destroy(removeFromParent: any): void;
}
export { SplitContainer };
