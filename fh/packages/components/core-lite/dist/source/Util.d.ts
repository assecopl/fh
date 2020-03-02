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
}
export { Util };
