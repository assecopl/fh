declare class FHML {
    private supportedTags;
    private escapedMap;
    constructor();
    escapeHtml(string: any): string;
    registerTag(name: any, fn: any, attributes?: any, noContent?: any): void;
    needParse(string: any): boolean;
    parse(source: any, skipHtmlEscape?: any): any;
    resolveValueTextOrEmpty(valueText: any): any;
    removeHtmlTags(text: string): string;
}
export { FHML };
