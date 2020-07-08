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
require("bootstrap/js/dist/tab");
var fh_forms_handler_1 = require("fh-forms-handler");
var fh_forms_handler_2 = require("fh-forms-handler");
var Tab = /** @class */ (function (_super) {
    __extends(Tab, _super);
    function Tab(componentObj, parent) {
        var _this = _super.call(this, componentObj, parent) || this;
        _this.activate = function () {
            if (!this.isRendered) {
                $(this.navElement).find('a').one('shown.bs.tab', function () {
                    while (this.contentWrapper.firstChild)
                        this.contentWrapper.removeChild(this.contentWrapper.firstChild);
                    this.renderSubcomponents();
                    this.isRendered = true;
                }.bind(this));
            }
            $(this.navElement).find('a').tab('show');
        };
        _this.navElement = null;
        _this.tabIndex = 0;
        _this.isRendered = false;
        return _this;
    }
    Tab.prototype.create = function () {
        var tab = document.createElement('div');
        tab.id = this.id;
        ['fc', 'tab-pane', 'col-12'].forEach(function (cssClass) {
            tab.classList.add(cssClass);
        });
        tab.setAttribute("role", "tabpanel");
        var nav = document.createElement('li');
        nav.classList.add('nav-item');
        var link = document.createElement('a');
        link.href = '#' + this.id;
        link.classList.add('nav-link');
        link.innerHTML = this.fhml.resolveValueTextOrEmpty(this.componentObj.label);
        link.dataset.tabId = this.id;
        link.setAttribute("data-toggle", "tab");
        link.setAttribute("role", "tab");
        var row = document.createElement('div');
        row.classList.add('row');
        tab.appendChild(row);
        this.link = link;
        nav.appendChild(link);
        this.navElement = nav;
        if (this.link.firstElementChild != null) {
            this.link.firstElementChild.addEventListener('click', this.onLinkSpanClickEvent.bind(this));
        }
        // @ts-ignore
        this.parent.registerTab(nav);
        this.tabIndex = this.parent.tabCount - 1;
        this.navElement.querySelector('a').href = '#' + this.id;
        this.component = tab;
        this.htmlElement = this.component;
        this.contentWrapper = row;
        this.hintElement = this.navElement;
        this.addStyles();
        this.display();
        if (this.componentObj.subelements) {
            this.addComponents(this.componentObj.subelements);
        }
    };
    ;
    /**
     * Propagacja zdarzenia kliknięcia na span z FHML do <a>
     */
    Tab.prototype.onLinkSpanClickEvent = function () {
        this.link.click();
    };
    Tab.prototype.update = function (change) {
        _super.prototype.update.call(this, change);
        if (change.changedAttributes) {
            $.each(change.changedAttributes, function (name, newValue) {
                switch (name) {
                    case 'label':
                        this.link.innerHTML = this.fhml.resolveValueTextOrEmpty(newValue);
                        break;
                }
            }.bind(this));
        }
    };
    ;
    Tab.prototype.setAccessibility = function (accessibility) {
        // Alvays show in design mode.
        if (accessibility === 'HIDDEN' && this.designMode === true) {
            accessibility = 'EDIT';
        }
        _super.prototype.setAccessibility.call(this, accessibility);
        if (accessibility !== 'HIDDEN') {
            this.navElement.classList.remove('d-none');
            this.navElement.classList.remove('invisible');
        }
        if (accessibility !== 'VIEW') {
            this.navElement.classList.remove('disabled');
        }
        switch (accessibility) {
            case 'HIDDEN':
                if (this.invisible) {
                    this.navElement.classList.add('invisible');
                }
                else {
                    this.navElement.classList.add('d-none');
                }
                break;
            case 'VIEW':
                // TODO: Kamil Trusiak - ensure if below line is needed
                // this.navElement.classList.add('disabled');
                break;
        }
        this.accessibility = accessibility;
    };
    ;
    Tab.prototype.focusCurrentComponent = function (deferred, options) {
        var tabContainer = this.component.parentNode;
        // if (tabContainer.scrollHeight > tabContainer.offsetHeight) {
        options.scrollableElement = tabContainer;
        // }
        if (this.parent.activeTabIndex === this.tabIndex) {
            deferred.resolve(options);
        }
        else {
            var df_1 = $.Deferred();
            $(this.navElement).one('shown.bs.tab', function () {
                df_1.resolve();
            });
            this.activate();
            this.parent.activeTabIndex = this.tabIndex;
            if (this.parent.onTabChange) {
                this.parent.tabChanged = true;
                this.parent.fireEvent('onTabChange', this.parent.onTabChange);
            }
            $.when(df_1).then(function () {
                deferred.resolve(options);
            });
        }
        return deferred.promise();
    };
    ;
    Tab.prototype.deferUntilActive = function () {
        var df = $.Deferred();
        if (this.parent.activeTabIndex === this.tabIndex) {
            df.resolve();
        }
        else {
            $(this.navElement).one('shown.bs.tab', function () {
                df.resolve();
            });
        }
        return df;
    };
    ;
    // noinspection JSUnusedGlobalSymbols
    Tab.prototype.setPresentationStyle = function (presentationStyle) {
        var nestedTabs = this.parent.parent.componentObj.type === 'Tab' ? true : false;
        ['border', 'border-success', 'border-info', 'border-warning', 'border-danger', 'is-invalid'].forEach(function (cssClass) {
            this.navElement.classList.remove(cssClass);
            if (nestedTabs) {
                this.parent.parent.navElement.classList.remove(cssClass);
            }
        }.bind(this));
        this.switchPresentationStyles(presentationStyle);
        if (nestedTabs && presentationStyle) {
            switch (presentationStyle) {
                case 'BLOCKER':
                case 'ERROR':
                    ['is-invalid', 'border', 'border-danger'].forEach(function (cssClass) {
                        this.parent.parent.navElement.classList.add(cssClass);
                    }.bind(this));
                    break;
                case 'OK':
                    ['border', 'border-success'].forEach(function (cssClass) {
                        this.parent.parent.navElement.classList.add(cssClass);
                    }.bind(this));
                    break;
                case 'INFO':
                    ['border', 'border-info'].forEach(function (cssClass) {
                        this.parent.parent.navElement.classList.add(cssClass);
                    }.bind(this));
                    break;
                case 'WARNING':
                    ['border', 'border-warning'].forEach(function (cssClass) {
                        this.parent.parent.navElement.classList.add(cssClass);
                    }.bind(this));
                    break;
            }
        }
    };
    Tab.prototype.switchPresentationStyles = function (presentationStyle) {
        switch (presentationStyle) {
            case 'BLOCKER':
            case 'ERROR':
                ['is-invalid', 'border', 'border-danger'].forEach(function (cssClass) {
                    this.navElement.classList.add(cssClass);
                }.bind(this));
                break;
            case 'OK':
                ['border', 'border-success'].forEach(function (cssClass) {
                    this.navElement.classList.add(cssClass);
                }.bind(this));
                break;
            case 'INFO':
                ['border', 'border-info'].forEach(function (cssClass) {
                    this.navElement.classList.add(cssClass);
                }.bind(this));
                break;
            case 'WARNING':
                ['border', 'border-warning'].forEach(function (cssClass) {
                    this.navElement.classList.add(cssClass);
                }.bind(this));
                break;
        }
    };
    Tab.prototype.render = function () {
        // when tab is actived, render will be called within activate()
    };
    ;
    Tab.prototype.destroy = function (removeFromParent) {
        if (this.link.firstElementChild != null) {
            this.link.firstElementChild.removeEventListener('click', this.onLinkSpanClickEvent.bind(this));
        }
        _super.prototype.destroy.call(this, removeFromParent);
    };
    Tab.prototype.getAdditionalButtons = function () {
        return [
            new fh_forms_handler_2.AdditionalButton('moveUp', 'arrow-left', 'Move left'),
            new fh_forms_handler_2.AdditionalButton('moveDown', 'arrow-right', 'Move right'),
            new fh_forms_handler_2.AdditionalButton('addDefaultSubcomponent', 'plus', 'Add empty row'),
        ];
    };
    return Tab;
}(fh_forms_handler_1.HTMLFormComponent));
exports.Tab = Tab;
//# sourceMappingURL=Tab.js.map