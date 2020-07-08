import { HTMLFormComponent } from "fh-forms-handler";
import { PanelGroup } from "./PanelGroup";
declare class FloatingGroup extends PanelGroup {
    private resizing;
    private drag;
    private dragging;
    private isDraggable;
    private dimensions;
    private position;
    private bound;
    private hideHeader;
    private floatingState;
    private pinningMode;
    private onToggleFullScreen;
    private onTogglePin;
    private floatingOnly;
    private hideButtons;
    private resizeListener;
    private headerClick;
    constructor(componentObj: any, parent: HTMLFormComponent);
    create(): void;
    togglePin(skipEvent?: any, skipValueChange?: any): void;
    pinGroup(skipValueChange: any): void;
    unpinGroup(skipValueChange: any, skipClass?: any): void;
    toggleFullScreen(skipEvent: any, skipValueChange: any): void;
    isUnpinned(value: any): boolean;
    isPinned(value: any): boolean;
    isMaximized(value: any): boolean;
    isMinimized(value: any): boolean;
    startFullScreen(skipValueChange: any): void;
    endFullScreen(skipValueChange: any): void;
    update(change: any): void;
    addInlineStyles(): void;
    removeInlineStyles(): void;
    toggleMouseDown(event: any): void;
    resizeMouseDown(event: any): void;
    mouseDown(event: any): void;
    mouseMove(event: any): void;
    mouseUp(event: any): void;
    extractChangedAttributes(): {};
    destroy(removeFromParent: any): void;
    /**
     * @Override default collapse behaviour so we can set default values for resizing and dragging attributes
     * This method fires after mouseUp event
     */
    toggleCollapse(): void;
}
export { FloatingGroup };
