import {AdditionalButton, HTMLFormComponent} from "fh-forms-handler";

class TabContainerFhDP extends HTMLFormComponent {
    private static designerActiveTabs: any = {};
    private navElement: HTMLUListElement;
    private tabCount: number;
    private activeTabIndex: any;
    private onTabChange: any;
    private portalContainerId: any;
    private isTabsSeparated: any;

    constructor(componentObj: any, parent: HTMLFormComponent) {
        super(componentObj, parent);

        this.isTabsSeparated = this.componentObj.isTabsSeparated;
        this.portalContainerId = this.componentObj.portalContainerId ? this.componentObj.portalContainerId: null;
        this.activeTabIndex = this.designMode ? this.findRecentlyUsedDesignerTab() || 0 : this.componentObj.activeTabIndex || 0;
        this.tabCount = 0;
        this.navElement = null;
        this.onTabChange = this.componentObj.onTabChange;
    }

    create() {
        let tabContainer = document.createElement('div');
        tabContainer.id = this.id;
        ['fc', 'tabContainer'].forEach(function (cssClass) {
            tabContainer.classList.add(cssClass);
        });

        let header = document.createElement('div');
        ['panel-sub-header', 'panel-sub-header--margin'].forEach(function (cssClass) {
            header.classList.add(cssClass);
        })

        let tabs = document.createElement('div');
        let tabsClass = ['tabs'];
        if(this.isTabsSeparated) {
            tabsClass.push('tabs--separated');
        }
        tabsClass.forEach(function (cssClass) {
            tabs.classList.add(cssClass);
        })

        let tabNav = document.createElement('div');
        ['tabs-group'].forEach(function (cssClass) {
            tabNav.classList.add(cssClass);
        })

        let tabAdditional = document.createElement('div');
        ['tabs-group'].forEach(function (cssClass) {
            tabAdditional.classList.add(cssClass);
        })
        if(!!this.portalContainerId) {
            let portal = document.getElementById(this.portalContainerId);
            tabAdditional.appendChild(portal);
        }

        let nav = document.createElement('ul');
        ['nav', 'nav-tabs', 'pt-0'].forEach(function (cssClass) {
            nav.classList.add(cssClass);
        });
        this.navElement = nav;

        let body = document.createElement('div');
        ['tab-content', 'row', 'eq-row'].forEach(function (cssClass) {
            body.classList.add(cssClass);
        });

        tabNav.appendChild(nav);
        tabs.appendChild(tabNav);
        tabs.appendChild(tabAdditional);
        header.appendChild(tabs);

        tabContainer.appendChild(header);
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
                this.navElement.classList.remove('d-none');
                this.navElement.classList.remove('invisible');
                this.component.classList.remove('d-none');
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
                    this.component.classList.add('d-none');
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
                TabContainerFhDP.designerActiveTabs[this.componentObj.id] = tabIndex;
            }
        }
    };

    findRecentlyUsedDesignerTab() {
        return TabContainerFhDP.designerActiveTabs[this.componentObj.id];
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

export {TabContainerFhDP};
