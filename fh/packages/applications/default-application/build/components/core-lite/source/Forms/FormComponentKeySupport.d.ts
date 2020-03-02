declare class FormComponentKeySupport {
    private keyDefinitions;
    private formElement;
    private postponedKeyDownHandler;
    private keyToHandlerMap;
    constructor(componentObj: any, formElement: any);
    private modifiers_map;
    private special_keys_map;
    /**
     * Adds event listener to input (or other element). Returns function which removes that listener.
     */
    addKeyEventListeners(input: any, requiredEventSource?: any): () => void;
    supportsKey(event: any): boolean;
    findSupportedKeyHandler(event: any): any;
    createKeyToHandlerMap(componentObj: any): {};
    transformKeyDefinition(definition: any): number;
    transformKeyEvent(event: any): number;
}
export { FormComponentKeySupport };
