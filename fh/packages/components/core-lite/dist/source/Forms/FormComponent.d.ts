/// <reference types="jquery" />
import { FormComponentChangesQueue } from "./FormComponentChangesQueue";
import { FormsManager } from "../Socket/FormsManager";
import { FH } from "../Socket/FH";
import { Util } from "../Util";
declare abstract class FormComponent {
    protected formsManager: FormsManager;
    protected fh: FH;
    protected util: Util;
    protected changesQueue: FormComponentChangesQueue;
    protected static readonly VALUE_ATTRIBUTE_NAME: string;
    private _id;
    protected _formId: string;
    private _parent;
    private _componentObj;
    protected components: FormComponent[];
    protected nonVisualComponents: FormComponent[];
    protected destroyed: boolean;
    private onDesignerToolboxDrop;
    combinedId: string;
    contentWrapper: HTMLElement;
    designMode: boolean;
    designDeletable: boolean;
    constructor(componentObj: any, parent: FormComponent);
    protected create(): void;
    protected createComponents(): void;
    destroy(removeFromParent: any): void;
    protected collectAllChanges(): any;
    protected collectChanges(allChanges: any): any;
    protected extractChangedAttributes(): {};
    fireEvent(eventType: any, actionName: any): void;
    protected fireEventWithLock(eventType: any, actionName: any): void;
    protected fireEventImpl(eventType: any, actionName: any, doLock: any): void;
    protected fireHttpMultiPartEvent(eventType: any, actionName: any, url: any, data: FormData): {
        abortRequest: () => void;
        promise: JQuery.Promise<any, any, any>;
    };
    applyChange(change: any): void;
    protected update(change: any): void;
    protected addComponent(componentObj: any): any;
    protected addNonVisualComponent(componentObj: any): void;
    protected addComponents(componentsList: any): void;
    protected removeComponent(componentId: any): void;
    protected findComponent(componentId: any, onlyId: any, getPath?: boolean, deepSearch?: boolean): any;
    private findComponentImpl;
    protected focusComponent(path: any, index: any, options: any): any;
    protected getAllComponents(): FormComponent[];
    protected focusCurrentComponent(deferred: any, options: any): any;
    get id(): string;
    get formId(): string;
    set formId(_formId: string);
    get parent(): FormComponent;
    get componentObj(): any;
    get parentId(): string;
    protected processURL(url: string): any;
    /**
     * Returns form that component is display on
     * @returns {any}
     */
    private getForm;
    protected getViewMode(): string;
    protected getFormType(): string;
    protected isFormActive(): boolean;
}
export { FormComponent };
