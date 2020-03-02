import { HTMLFormComponent } from "fh-forms-handler";
import { AdditionalButton } from "fh-forms-handler";
declare class Wizard extends HTMLFormComponent {
    private static designerActiveTabs;
    private activeTabIndex;
    private tabCount;
    private navElement;
    private onTabChange;
    constructor(componentObj: any, parent: HTMLFormComponent);
    create(): void;
    findTab(tabId: any): false | HTMLFormComponent;
    onTabNavClick(event: any): void;
    registerTab(tabNav: any): void;
    update(change: any): void;
    activateTab(tabId: any): void;
    findRecentlyUsedDesignerTab(): any;
    extractChangedAttributes(): {};
    getAdditionalButtons(): AdditionalButton[];
}
export { Wizard };
