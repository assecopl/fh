import 'bootstrap/js/dist/tab';
import {HTMLFormComponent} from "fh-forms-handler";
import {AdditionalButton} from "fh-forms-handler";

class Tab extends HTMLFormComponent {
    private navElement: any;
    private tabIndex: any;
    private isRendered: any;
    private link: HTMLAnchorElement;

    constructor(componentObj: any, parent: HTMLFormComponent) {
        super(componentObj, parent);

        this.navElement = null;
        this.tabIndex = 0;
        this.isRendered = false;
    }

    create() {
        let tab = document.createElement('div');
        tab.id = this.id;
        ['fc', 'tab-pane', 'col-12'].forEach(function (cssClass) {
            tab.classList.add(cssClass);
        });
        tab.setAttribute("role", "tabpanel");

        let nav = document.createElement('li');
        nav.classList.add('nav-item');
        let link = document.createElement('a');
        link.href = '#' + this.id;
        link.classList.add('nav-link');
        link.innerHTML = this.fhml.resolveValueTextOrEmpty(this.componentObj.label);
        link.dataset.tabId = this.id;
        link.setAttribute("data-toggle", "tab");
        link.setAttribute("role", "tab");


        let row = document.createElement('div');
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
        this.tabIndex = (<any>this.parent).tabCount - 1;
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

    /**
     * Propagacja zdarzenia kliknięcia na span z FHML do <a>
     */
    onLinkSpanClickEvent() {
        this.link.click();
    }

    update(change) {
        super.update(change);

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

    activate = function () {
        if (!this.isRendered) {
            $(this.navElement).find('a').one('shown.bs.tab', function () {
                while (this.contentWrapper.firstChild) this.contentWrapper.removeChild(this.contentWrapper.firstChild);
                this.renderSubcomponents();

                this.isRendered = true;
            }.bind(this));
        }
        $(this.navElement).find('a').tab('show');
    };

    deactivate() {
        let dismissHints = (components) => {
            components.forEach(function(component) {
                component.hideHint();

                dismissHints(component.components);
            });
        }
        if (this.isRendered) {
            dismissHints(this.components);
        }
    }

    setAccessibility(accessibility) {
        // Alvays show in design mode.
        if (accessibility === 'HIDDEN' && this.designMode === true) {
            accessibility = 'EDIT';
        }

        super.setAccessibility(accessibility);

        if (accessibility !== 'HIDDEN') {
            this.navElement.classList.remove('d-none');
            this.navElement.classList.remove('invisible');
        }
        if (accessibility !== 'VIEW') {
            this.navElement.classList.remove('disabled');
        }
        switch (accessibility) {
            case 'HIDDEN':
                if(this.invisible){
                    this.navElement.classList.add('invisible');
                } else {
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

    focusCurrentComponent(deferred, options) {
        let tabContainer = this.component.parentNode;
        // if (tabContainer.scrollHeight > tabContainer.offsetHeight) {
        options.scrollableElement = tabContainer;
        // }

        if ((<any>this.parent).activeTabIndex === this.tabIndex) {
            deferred.resolve(options);
        } else {
            let df = $.Deferred();
            $(this.navElement).one('shown.bs.tab', function () {
                df.resolve();
            });
            this.activate();
            (<any>this.parent).activeTabIndex = this.tabIndex;
            if ((<any>this.parent).onTabChange) {
                (<any>this.parent).tabChanged = true;
                this.parent.fireEvent('onTabChange', (<any>this.parent).onTabChange);
            }
            $.when(df).then(function () {
                deferred.resolve(options);
            })
        }

        return deferred.promise();
    };

    deferUntilActive() {
        let df = $.Deferred();
        if ((<any>this.parent).activeTabIndex === this.tabIndex) {
            df.resolve();
        } else {
            $(this.navElement).one('shown.bs.tab', function () {
                df.resolve();
            });
        }
        return df;
    };

    // noinspection JSUnusedGlobalSymbols
    setPresentationStyle(presentationStyle) {
        let nestedTabs = this.parent.parent.componentObj.type === 'Tab' ? true : false;
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
    }

    switchPresentationStyles(presentationStyle) {
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
    }

    render() {
        // when tab is actived, render will be called within activate()
    };

    destroy(removeFromParent) {
        if (this.link.firstElementChild != null) {
            this.link.firstElementChild.removeEventListener('click', this.onLinkSpanClickEvent.bind(this));
        }

        super.destroy(removeFromParent);
    }

    getAdditionalButtons(): AdditionalButton[] {
        return [
            new AdditionalButton('moveUp', 'arrow-left', 'Move left'),
            new AdditionalButton('moveDown', 'arrow-right', 'Move right'),
            new AdditionalButton('addDefaultSubcomponent', 'plus', 'Add empty row'),
        ];
    }
}

export {Tab};
