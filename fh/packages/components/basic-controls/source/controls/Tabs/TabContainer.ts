import {HTMLFormComponent} from "fh-forms-handler";
import {AdditionalButton} from "fh-forms-handler";

class TabContainer extends HTMLFormComponent {
    private static designerActiveTabs: any = {};
    private navElement: HTMLUListElement;
    private tabCount: number;
    private activeTabIndex: any;
    private onTabChange: any;

    constructor(componentObj: any, parent: HTMLFormComponent) {
        super(componentObj, parent);

        this.activeTabIndex = this.designMode ? this.findRecentlyUsedDesignerTab() || 0 : this.componentObj.activeTabIndex || 0;

        this.tabCount = 0;
        this.navElement = null;
        this.onTabChange = this.componentObj.onTabChange;
    }

    create() {
        var tabContainer = document.createElement('div');
        tabContainer.id = this.id;
        ['fc', 'tabContainer'].forEach(function (cssClass) {
            tabContainer.classList.add(cssClass);
        });

        var nav = document.createElement('ul');
        ['nav', 'nav-tabs'].forEach(function (cssClass) {
            nav.classList.add(cssClass);
        });
        this.navElement = nav;

        var body = document.createElement('div');
        ['tab-content', 'row', 'eq-row'].forEach(function (cssClass) {
            body.classList.add(cssClass);
        });

        tabContainer.appendChild(nav);
        tabContainer.appendChild(body);

        this.component = tabContainer;
        this.hintElement = this.component;

        this.wrap();
        this.contentWrapper = body;
        this.addStyles();
        this.addAlignStyles();
        this.display();

        if (this.componentObj.subelements) {
            this.addComponents(this.componentObj.subelements);
            this.activateTab(this.activeTabIndex);
        }
    };

    findTab = function (tabId) {
        for (var i = this.components.length - 1; i >= 0; i--) {
            var tab = this.components[i];
            if (tab.id === tabId) {
                return tab;
            }
        }
        return false;
    };

    onTabNavClick(event) {
        event.preventDefault();
        var tabNav = event.target;
        if (!tabNav.parentNode.classList.contains('active')) {
            var tabId = tabNav.dataset.tabId;
            var tab = this.findTab(tabId);
            this.changesQueue.queueValueChange(tab.tabIndex);
            if (this.onTabChange) {
                this.fireEvent('onTabChange', this.onTabChange);
            }
            this.activateTab(tab.tabIndex);
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

    setAccessibility(accessibility) {
        if (accessibility !== 'HIDDEN') {
            this.htmlElement.classList.remove('d-none');
            this.htmlElement.classList.remove('invisible');
            this.navElement.classList.add('fc-editable');
        }
        if (accessibility !== 'DEFECTED' || accessibility !== 'VIEW') {
            this.component.classList.remove('fc-disabled', 'disabled');
        }
        if (accessibility !== 'EDIT') {
            this.component.classList.remove('fc-editable');
        }

        switch (accessibility) {
            case 'EDIT':
                this.accessibilityResolve(this.component, 'EDIT');
                break;
            case 'VIEW':
                this.accessibilityResolve(this.component, 'VIEW');
                break;
            case 'HIDDEN':
                if(this.invisible){
                    this.navElement.classList.add('invisible');
                } else {
                    this.navElement.classList.add('d-none');
                }
                this.navElement.classList.remove('fc-editable');
                break;
            case 'DEFECTED':
                this.accessibilityResolve(this.component, 'DEFECTED');
                this.component.title = 'Broken control';
                break;
        }

        this.accessibility = accessibility;
    }


    activateTab(tabIndex) {
        if (this.components[tabIndex]) {
            (<any>this.components[tabIndex]).activate();
            this.activeTabIndex = tabIndex;
            if (this.designMode) {
                TabContainer.designerActiveTabs[this.componentObj.id] = tabIndex;
            }
        }
    };

    findRecentlyUsedDesignerTab() {
        return TabContainer.designerActiveTabs[this.componentObj.id];
    };

    extractChangedAttributes() {
        return this.changesQueue.extractChangedAttributes();
    };

    getAdditionalButtons(): AdditionalButton[] {
        return [
            new AdditionalButton('addDefaultSubcomponent', 'plus', 'Add tab'),
        ];
    }
}

export {TabContainer};
