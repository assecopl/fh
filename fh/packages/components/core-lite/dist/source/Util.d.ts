declare class Util {
    countProperties(object: any): number;
    getPath(resource: any): any;
    areArraysEqualsSkipOrder(arrayA: any, arrayB: any): boolean;
    showDialog(title: any, message: any, closeButtonLabel: any, closeButtonClass: any, closeCallback: any): void;
    /**
     *
     * @param url
     */
    isUrlRelative(url: string): boolean;
    scrollToComponent(formElementId: string, animateDuration?: number): void;
    /**
     * Count offsetTop if there are more containers with relative position inside parent container.
     * @param element
     * @param parentId
     */
    getOffsetTop(element: any, parentId: any): number;
}
export { Util };
