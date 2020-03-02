declare class LayoutHandler {
    static mainLayout: string;
    private prefix;
    currentMainLayout: string;
    targetLayout: string;
    /**
     * Functions that finds specific container in target layout
     * Created for bakward compatibility with JS function getElementById.
     * @param containerId
     * @param jqueryObject
     * @return HTML DOM Object or null*
     */
    getLayoutContainer(containerId: string, jqueryObject?: boolean): any;
    /**
     * Functions that finds specific container in current layout
     * Created for bakward compatibility with JS function getElementById.
     * @param containerId
     * @param jqueryObject
     * @return HTML DOM Object or null*
     *
     */
    getCurrentLayoutContainer(containerId: string, jqueryObject?: boolean): any;
    getCurrentMainLayout(): string;
    /**
     * Function that prepare age for layout processing. If layout will be changed function hides all layouts.
     * @param layout
     */
    startLayoutProcessing(layout: string): void;
    /**
     * Fuction that block layout change. Used when another UC shows form on modal element.
     * Used in Form.ts;
     * @param isModal
     */
    blockLayoutChangeForModal(): void;
    /**
     * Fuction that block layout change. Used when UC is in design mode
     * Used in Form.ts;
     */
    blockLayoutChangeForDesigner(): void;
    /**
     * Function that finish layout processing. Moves exist contetnt from one layout to another.
     * Moving designer components is not implemented.
     */
    finishLayoutProcessing(): void;
}
export { LayoutHandler };
