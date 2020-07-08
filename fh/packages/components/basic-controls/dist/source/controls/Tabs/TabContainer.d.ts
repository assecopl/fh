import { HTMLFormComponent } from "fh-forms-handler";
import { AdditionalButton } from "fh-forms-handler";
declare class TabContainer extends HTMLFormComponent {
    private static designerActiveTabs;
    private navElement;
    private tabCount;
    private activeTabIndex;
    private onTabChange;
    constructor(componentObj: any, parent: HTMLFormComponent);
    create(): void;
    findTab: (tabId: any) => any;
    onTabNavClick(event: any): void;
    registerTab(tabNav: any): void;
    update(change: any): void;
    setAccessibility(accessibility: any): void;
    activateTab(tabIndex: any): void;
    findRecentlyUsedDesignerTab(): any;
    extractChangedAttributes(): {};
    getAdditionalButtons(): AdditionalButton[];
}
export { TabContainer };
