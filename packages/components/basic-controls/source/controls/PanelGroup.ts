import {HTMLFormComponent} from "fh-forms-handler";
import {AdditionalButton} from "fh-forms-handler";

class PanelGroup extends HTMLFormComponent {
    protected isCollapsible: any;
    private readonly onToggle: any;
    public collapsed: any;
    private collapseToggler: any;
    private collapseChanged: any;
    protected groupToolbox: any;
    private readonly height: any;
    private readonly borderVisible: any;
    private collapsedOld: any;
    private headingElement: any;
    protected forceHeader: any;
    protected headingTypeValue: "h1" | "h2" | "h3" | "h4" | "h5" | "h6" = null;

    public iconOpened: string;
    public iconClosed: string;

    constructor(componentObj: any, parent: HTMLFormComponent) {
        super(componentObj, parent);

        this.componentObj.verticalAlign = this.componentObj.verticalAlign || 'top';

        this.isCollapsible = Boolean(this.componentObj.collapsible);
        this.onToggle = this.componentObj.onToggle;
        this.headingTypeValue = this.componentObj.headingTypeValue? this.componentObj.headingTypeValue : "span";
        this.collapsed = Boolean(this.componentObj.collapsed);
        this.collapseToggler = null;
        this.collapseChanged = false;
        this.groupToolbox = null;
        this.height = this.componentObj.height || null;
        this.borderVisible = Boolean(this.componentObj.borderVisible);
        this.collapsedOld = this.collapsed;
        this.headingElement = null;

        this.iconOpened = this.componentObj.iconOpened? this.componentObj.iconOpened : "fa-arrow-down";
        this.iconClosed = this.componentObj.iconClosed? this.componentObj.iconClosed : "fa-arrow-up";
    }

    create() {
        let group = document.createElement('div');
        ['fc', 'group', 'panelGroup', 'mb-3', 'card', 'card-default'].forEach(function (cssClass) {
            group.classList.add(cssClass);
        });
        if (!this.borderVisible) {
            group.classList.add('borderHidden');
        }
        group.id = this.id;

        let heading = document.createElement('div');
        heading.classList.add('card-header');
        heading.classList.add('d-flex');

        let titleElm = document.createElement(this.componentObj.label != null ? this.headingTypeValue: "span"); //Default span
        titleElm.classList.add('mr-auto');
        titleElm.classList.add('card-title');
        titleElm.classList.add('mb-0');

        let titleElmIn = document.createElement('span');
        titleElmIn.id = this.id + '_label';
        titleElm.appendChild(titleElmIn);

        if (this.componentObj.label != null) {
            titleElmIn.innerHTML = this.resolveValue(this.componentObj.label);
        } else {
            titleElmIn.innerHTML = '&nbsp;';
        }
        heading.appendChild(titleElm);

        let toolbox = document.createElement('span');
        toolbox.classList.add('toolbox');
        toolbox.classList.add('align-self-center');

        if (this.isCollapsible) {
            let collapseToggle = document.createElement('span');

            collapseToggle.classList.add('collapseToggle');

            let icon = document.createElement('i');
            icon.classList.add('fa');
            // let text = document.createElement('span');
            if (this.collapsed) {
                group.classList.add('collapsed');
                icon.classList.add(this.iconOpened);
                // text.appendChild(document.createTextNode('rozwiń'));
            } else {
                icon.classList.add(this.iconClosed);
                // text.appendChild(document.createTextNode('zwiń'));
            }

            // collapseToggle.appendChild(text);
            collapseToggle.appendChild(icon);
            this.collapseToggler = collapseToggle;
            toolbox.appendChild(collapseToggle);

            heading.addEventListener('click', function (event) {
                this.toggleCollapse();
                if (this.onToggle) {
                    this.fireEvent('onToggle', this.onToggle, event);
                }
            }.bind(this));
        }

        this.groupToolbox = toolbox;

        heading.appendChild(toolbox);
        group.appendChild(heading);
        this.headingElement = heading;
        this.updateHeaderVisibility(this.componentObj.label);

        this.hintElement = group;

        let body = document.createElement('div');
        body.classList.add('card-body');

        let row = document.createElement('div');
        row.classList.add('row');
        row.classList.add('eq-row');

        body.appendChild(row);
        group.appendChild(body);

        this.component = group;
        this.wrap(true);

        this.contentWrapper = this.component.querySelector('div.row');
        this.addAlignStyles();
        this.handlemarginAndPAddingStyles();
        this.display();

        if (this.componentObj.subelements) {
            this.addComponents(this.componentObj.subelements);
        }

        body = $(this.component).children('.card-body')[0];
        let footer = $(body).children('.row').children('.card-footer');
        if (footer.length) {
            if (this.componentObj.height) {
                body.style.height = 'calc(' + this.componentObj.height + ' - 49px - ' +  footer[0].clientHeight + 'px)';
                body.classList.add('hasHeight');
            } else {
                body.style.height = 'calc(100% - 49px - ' +  footer[0].clientHeight + 'px)';
            }
        } else {
            if (this.componentObj.height) {
                body.style['overflow-y'] = 'auto';
                body.style.height = this.height;
                body.classList.add('hasHeight');
            }
        }

    };

    update(change) {
        super.update(change);
        $.each(change.changedAttributes, function (name, newValue) {
            switch (name) {
                case 'collapsed':
                    if (newValue) {
                        this.collapse();
                        this.collapsed = true;
                    } else {
                        this.uncollapse();
                        this.collapsed = false;
                    }
                    this.collapsedOld = this.collapsed;
                    break;
                case 'label':
                    let label = this.component.querySelector('.card-title');
                    if (label != null) {
                        label.removeChild(label.firstChild);
                        let titleElm = document.createElement('span');
                        titleElm.innerHTML = this.resolveValue(newValue);
                        label.appendChild(titleElm);
                    }
                    this.updateHeaderVisibility(newValue);
                    break;
            }
        }.bind(this));
        $(this.component).scrollTop(this.component.clientHeight);

    };

    updateHeaderVisibility(newTitle) {
        let hasTitle = this.forceHeader || this.isCollapsible || (newTitle != null && newTitle.trim() != '');
        if (hasTitle) {
            this.headingElement.classList.add('d-flex');
            this.headingElement.classList.remove('d-none');
        } else {
            this.headingElement.classList.remove('d-flex');
            this.headingElement.classList.add('d-none');
        }
    };

    toggleCollapse() {
        if (this.collapsed) {
            this.uncollapse();
        } else {
            this.collapse();
        }


        /**
         * Ads support for PanelGroupWrapper toggleAll value callculations.
         */
        if(this.parent["updateToggleAllStatus"] && this.parent.componentObj.type == "PanelGroupWrapper") {
            (this.parent as any).updateToggleAllStatus()
        }

    };

    collapse() {
        // let text = this.collapseToggler.firstChild;
        let icon = this.collapseToggler.firstChild; //childNodes[1];
        icon.classList.remove(this.iconClosed);
        icon.classList.add(this.iconOpened);
        // text.removeChild(text.firstChild);
        // text.appendChild(document.createTextNode('rozwiń'));
        this.component.classList.add('collapsed');
        this.collapsed = true;
        this.collapseChanged = true;
    };

    uncollapse() {
        // let text = this.collapseToggler.firstChild;
        let icon = this.collapseToggler.firstChild; //childNodes[1];
        icon.classList.remove(this.iconOpened);
        icon.classList.add(this.iconClosed);
        // text.removeChild(text.firstChild);
        // text.appendChild(document.createTextNode('zwiń'));
        this.component.classList.remove('collapsed');
        this.collapsed = false;
        this.collapseChanged = true;
    };

    extractChangedAttributes() {
        let changes = {};
        if (this.collapsedOld != this.collapsed) {
            (<any>changes).value = this.collapsed;
            this.collapsedOld = this.collapsed;
        }
        return changes;
    };

    resolveValue = function (value) {
        value = this.fhml.resolveValueTextOrEmpty(value);
        return value;
    };

    // noinspection JSUnusedGlobalSymbols
    setPresentationStyle(presentationStyle) {
        if (this.parent != null) {
            // @ts-ignore
            this.parent.setPresentationStyle(presentationStyle);
        }
    }

    getAdditionalButtons(): AdditionalButton[] {
        if (this.parent.componentObj.type === 'Accordion') {
            return [
                new AdditionalButton('moveUp', 'arrow-up', 'Move up'),
                new AdditionalButton('moveDown', 'arrow-down', 'Move down')
            ];
        }
    }
}

export {PanelGroup};
