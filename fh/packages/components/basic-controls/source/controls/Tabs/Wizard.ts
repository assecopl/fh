import {HTMLFormComponent} from "fh-forms-handler";
import {AdditionalButton} from "fh-forms-handler";

class Wizard extends HTMLFormComponent {
    private static designerActiveTabs = {};
    private activeTabIndex: any;
    private tabCount: number;
    private navElement: any;
    private onTabChange: any;

    constructor(componentObj: any, parent: HTMLFormComponent) {
        super(componentObj, parent);

        this.activeTabIndex = this.designMode ? this.findRecentlyUsedDesignerTab() || 0 : this.componentObj.activeTabIndex || 0;

        this.tabCount = 0;
        this.navElement = null;
        this.onTabChange = this.componentObj.onTabChange;
    }

    create() {
        let wizard = document.createElement('div');
        wizard.id = this.id;
        ['fc', 'wizard'].forEach(function (cssClass) {
            wizard.classList.add(cssClass);
        });

        let nav = document.createElement('ul');
        ['nav', 'nav-pills', 'nav-wizard'].forEach(function (cssClass) {
            nav.classList.add(cssClass);
        });
        this.navElement = nav;

        let body = document.createElement('div');
        body.classList.add('tab-content');
        body.classList.add('row');

        wizard.appendChild(nav);
        wizard.appendChild(body);

        this.component = wizard;
        this.wrap();
        this.contentWrapper = body;
        this.addStyles();
        this.display();

        if (this.componentObj.subelements) {
            this.addComponents(this.componentObj.subelements);
            this.activateTab(this.activeTabIndex);
        }
    };

    findTab(tabId) {
        for (let i = this.components.length - 1; i >= 0; i--) {
            let tab = this.components[i];
            if (tab.id === tabId) {
                return tab;
            }
        }
        return false;
    };

    onTabNavClick(event) {
        event.preventDefault();
        let tabNav = event.target;
        if (!tabNav.parentNode.classList.contains('active')) {
            let tabId = tabNav.dataset.tabId;
            let tab = this.findTab(tabId);
            this.activateTab((<any>tab).tabIndex);
            this.changesQueue.queueValueChange(this.activeTabIndex);
            if (this.onTabChange) {
                this.fireEventWithLock('onTabChange', this.onTabChange);
            }
        }
    };

    registerTab(tabNav) {
        tabNav.addEventListener('click', this.onTabNavClick.bind(this));
        this.navElement.appendChild(tabNav);
        this.tabCount = this.tabCount + 1;
    };

    update(change) {
        super.update(change);

        if (change.changedAttributes) {
            $.each(change.changedAttributes, function (name, newValue) {
                switch (name) {
                    case 'activeTabIndex':
                        this.activateTab(newValue);
                        break;
                }
            }.bind(this));
        }
    };

    activateTab(tabId) {
        if (this.components[tabId]) {
            (<any>this.components[tabId]).activate();
            this.activeTabIndex = tabId;
            if (this.designMode) {
                Wizard.designerActiveTabs[this.componentObj.id] = tabId;
            }
        }
    };

    findRecentlyUsedDesignerTab() {
        return Wizard.designerActiveTabs[this.componentObj.id];
    };

    extractChangedAttributes() {
        return this.changesQueue.extractChangedAttributes();
    };

    getAdditionalButtons(): AdditionalButton[] {
        return [
            new AdditionalButton('addDefaultSubcomponent', 'plus', 'Add step')
        ];
    }
}

export {Wizard};