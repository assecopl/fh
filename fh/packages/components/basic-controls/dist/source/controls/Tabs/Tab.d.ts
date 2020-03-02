/// <reference types="jquery" />
import 'bootstrap/js/dist/tab';
import { HTMLFormComponent } from "fh-forms-handler";
import { AdditionalButton } from "fh-forms-handler";
declare class Tab extends HTMLFormComponent {
    private navElement;
    private tabIndex;
    private isRendered;
    private link;
    constructor(componentObj: any, parent: HTMLFormComponent);
    create(): void;
    /**
     * Propagacja zdarzenia kliknięcia na span z FHML do <a>
     */
    onLinkSpanClickEvent(): void;
    update(change: any): void;
    activate: () => void;
    setAccessibility(accessibility: any): void;
    focusCurrentComponent(deferred: any, options: any): any;
    deferUntilActive(): JQuery.Deferred<any, any, any>;
    setPresentationStyle(presentationStyle: any): void;
    switchPresentationStyles(presentationStyle: any): void;
    render(): void;
    destroy(removeFromParent: any): void;
    getAdditionalButtons(): AdditionalButton[];
}
export { Tab };
