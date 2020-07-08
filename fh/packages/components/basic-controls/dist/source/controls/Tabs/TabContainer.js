"use strict";
var __extends = (this && this.__extends) || (function () {
    var extendStatics = function (d, b) {
        extendStatics = Object.setPrototypeOf ||
            ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
            function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
        return extendStatics(d, b);
    };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();
Object.defineProperty(exports, "__esModule", { value: true });
var fh_forms_handler_1 = require("fh-forms-handler");
var fh_forms_handler_2 = require("fh-forms-handler");
var TabContainer = /** @class */ (function (_super) {
    __extends(TabContainer, _super);
    function TabContainer(componentObj, parent) {
        var _this = _super.call(this, componentObj, parent) || this;
        _this.findTab = function (tabId) {
            for (var i = this.components.length - 1; i >= 0; i--) {
                var tab = this.components[i];
                if (tab.id === tabId) {
                    return tab;
                }
            }
            return false;
        };
        _this.activeTabIndex = _this.designMode ? _this.findRecentlyUsedDesignerTab() || 0 : _this.componentObj.activeTabIndex || 0;
        _this.tabCount = 0;
        _this.navElement = null;
        _this.onTabChange = _this.componentObj.onTabChange;
        return _this;
    }
    TabContainer.prototype.create = function () {
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
    ;
    TabContainer.prototype.onTabNavClick = function (event) {
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
    ;
    TabContainer.prototype.registerTab = function (tabNav) {
        tabNav.addEventListener('click', this.onTabNavClick.bind(this));
        this.navElement.appendChild(tabNav);
        this.tabCount = this.tabCount + 1;
    };
    ;
    TabContainer.prototype.update = function (change) {
        _super.prototype.update.call(this, change);
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
    ;
    TabContainer.prototype.setAccessibility = function (accessibility) {
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
                this.accessibilityResolve(this.component, 'EDIT');
                break;
            case 'VIEW':
                this.accessibilityResolve(this.component, 'VIEW');
                break;
            case 'HIDDEN':
                if (this.invisible) {
                    this.navElement.classList.add('invisible');
                }
                else {
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
    };
    TabContainer.prototype.activateTab = function (tabIndex) {
        if (this.components[tabIndex]) {
            this.components[tabIndex].activate();
            this.activeTabIndex = tabIndex;
            if (this.designMode) {
                TabContainer.designerActiveTabs[this.componentObj.id] = tabIndex;
            }
        }
    };
    ;
    TabContainer.prototype.findRecentlyUsedDesignerTab = function () {
        return TabContainer.designerActiveTabs[this.componentObj.id];
    };
    ;
    TabContainer.prototype.extractChangedAttributes = function () {
        return this.changesQueue.extractChangedAttributes();
    };
    ;
    TabContainer.prototype.getAdditionalButtons = function () {
        return [
            new fh_forms_handler_2.AdditionalButton('addDefaultSubcomponent', 'plus', 'Add tab'),
        ];
    };
    TabContainer.designerActiveTabs = {};
    return TabContainer;
}(fh_forms_handler_1.HTMLFormComponent));
exports.TabContainer = TabContainer;
//# sourceMappingURL=TabContainer.js.map