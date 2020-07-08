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
var Wizard = /** @class */ (function (_super) {
    __extends(Wizard, _super);
    function Wizard(componentObj, parent) {
        var _this = _super.call(this, componentObj, parent) || this;
        _this.activeTabIndex = _this.designMode ? _this.findRecentlyUsedDesignerTab() || 0 : _this.componentObj.activeTabIndex || 0;
        _this.tabCount = 0;
        _this.navElement = null;
        _this.onTabChange = _this.componentObj.onTabChange;
        return _this;
    }
    Wizard.prototype.create = function () {
        var wizard = document.createElement('div');
        wizard.id = this.id;
        ['fc', 'wizard'].forEach(function (cssClass) {
            wizard.classList.add(cssClass);
        });
        var nav = document.createElement('ul');
        ['nav', 'nav-pills', 'nav-wizard'].forEach(function (cssClass) {
            nav.classList.add(cssClass);
        });
        this.navElement = nav;
        var body = document.createElement('div');
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
    ;
    Wizard.prototype.findTab = function (tabId) {
        for (var i = this.components.length - 1; i >= 0; i--) {
            var tab = this.components[i];
            if (tab.id === tabId) {
                return tab;
            }
        }
        return false;
    };
    ;
    Wizard.prototype.onTabNavClick = function (event) {
        event.preventDefault();
        var tabNav = event.target;
        if (!tabNav.parentNode.classList.contains('active')) {
            var tabId = tabNav.dataset.tabId;
            var tab = this.findTab(tabId);
            this.activateTab(tab.tabIndex);
            this.changesQueue.queueValueChange(this.activeTabIndex);
            if (this.onTabChange) {
                this.fireEventWithLock('onTabChange', this.onTabChange);
            }
        }
    };
    ;
    Wizard.prototype.registerTab = function (tabNav) {
        tabNav.addEventListener('click', this.onTabNavClick.bind(this));
        this.navElement.appendChild(tabNav);
        this.tabCount = this.tabCount + 1;
    };
    ;
    Wizard.prototype.update = function (change) {
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
    Wizard.prototype.activateTab = function (tabId) {
        if (this.components[tabId]) {
            this.components[tabId].activate();
            this.activeTabIndex = tabId;
            if (this.designMode) {
                Wizard.designerActiveTabs[this.componentObj.id] = tabId;
            }
            if (this.component.children) {
                var wizardTabs_1;
                var tabs = this.component.children;
                Array.from(tabs).forEach(function (element) {
                    // @ts-ignore
                    if (element.classList.contains('tab-content')) {
                        // @ts-ignore
                        wizardTabs_1 = element.children;
                    }
                });
                if (wizardTabs_1) {
                    var activeTab = wizardTabs_1[tabId];
                    activeTab.classList.add('active');
                }
            }
        }
    };
    ;
    Wizard.prototype.findRecentlyUsedDesignerTab = function () {
        return Wizard.designerActiveTabs[this.componentObj.id];
    };
    ;
    Wizard.prototype.extractChangedAttributes = function () {
        return this.changesQueue.extractChangedAttributes();
    };
    ;
    Wizard.prototype.getAdditionalButtons = function () {
        return [
            new fh_forms_handler_2.AdditionalButton('addDefaultSubcomponent', 'plus', 'Add step')
        ];
    };
    Wizard.designerActiveTabs = {};
    return Wizard;
}(fh_forms_handler_1.HTMLFormComponent));
exports.Wizard = Wizard;
//# sourceMappingURL=Wizard.js.map