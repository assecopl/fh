import {HTMLFormComponent} from "fh-forms-handler";

class Widget extends HTMLFormComponent {
    private isCollapsible: boolean;
    private onToggle: any;
    private collapsed: boolean;
    private collapseToggler: any;
    private collapseChanged: boolean;
    private groupToolbox: any;
    private height: any;
    private attributes: { posX: any, posY: any, sizeX: any, sizeY: any };

    constructor(componentObj: any, parent: HTMLFormComponent) {
        super(componentObj, parent);

        this.isCollapsible = Boolean(this.componentObj.collapsible);
        this.onToggle = this.componentObj.onToggle;
        this.collapsed = Boolean(this.componentObj.collapsed);
        this.collapseToggler = null;
        this.collapseChanged = false;
        this.groupToolbox = null;
        this.height = this.componentObj.height || null;
        this.attributes = this.componentObj.attributes || null;
    }

    create() {
        var widgetRemoveButton = document.createElement('button');
        ['widgetRemoveButton', 'fa', 'fa-remove'].forEach(function (cssClass) {
            widgetRemoveButton.classList.add(cssClass);
        });
        widgetRemoveButton.id = 'edit-' + this.id;

        var gridStackItem = document.createElement('div');

        ['grid-stack-item'].forEach(function (cssClass) {
            gridStackItem.classList.add(cssClass);
        });

        if (this.attributes) {
            gridStackItem.setAttribute('data-gs-x', this.attributes.posX);
            gridStackItem.setAttribute('data-gs-y', this.attributes.posY);
            gridStackItem.setAttribute('data-gs-width', this.attributes.sizeX);
            gridStackItem.setAttribute('data-gs-height', this.attributes.sizeY);
            gridStackItem.setAttribute('mark-as-deleted', 'false');
        }

        widgetRemoveButton.addEventListener('click', function () {
            gridStackItem.setAttribute('mark-as-deleted', 'false');
            $(this.parent.container).data('gridstack').removeWidget($(gridStackItem), true);
        }.bind(this));

        var gridStackItemContent = document.createElement('div');
        gridStackItemContent.classList.add('grid-stack-item-content');

        var group = document.createElement('div');
        ['fc', 'group', 'Widget'].forEach(function (cssClass) {
            group.classList.add(cssClass);
        });

        group.id = this.id;
        ['card', 'card-default', 'borderless'].forEach(function (cssClass) {
            group.classList.add(cssClass);
        });

        if (this.componentObj.label && this.componentObj.label.trim() != '') {
            var heading = document.createElement('div');
            heading.classList.add('card-header');

            heading.appendChild(document.createTextNode(this.componentObj.label));

            group.appendChild(heading);
        }

        var body = document.createElement('div');
        body.classList.add('card-body');

        var row = document.createElement('div');
        row.classList.add('row');
        row.classList.add('eq-row');

        if (this.height) {
            row.style.height = this.height;
            $(row).height($(row).height() - 70);
        }

        body.appendChild(row);
        group.appendChild(body);
        gridStackItemContent.appendChild(group);
        gridStackItem.appendChild(gridStackItemContent);
        gridStackItem.appendChild(widgetRemoveButton);


        this.component = gridStackItem;
        this.wrap(true);
        this.contentWrapper = this.component.querySelector('div.row');
        this.addAlignStyles();
        this.handlemarginAndPAddingStyles();
        this.display();

        if (this.componentObj.subelements) {
            this.addComponents(this.componentObj.subelements);
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
                    break;
                case 'label':
                    var label = this.component.querySelector('.card-title');
                    label.removeChild(label.firstChild);
                    label.appendChild(document.createTextNode(newValue || ''));
                    break;
            }
        }.bind(this));
    };

    addComponent(componentObj) {
        var oldContentWrapper = this.contentWrapper;
        if (componentObj.type == 'Footer') {
            this.contentWrapper = this.component;
        }
        var component = this.fh.createComponent(componentObj, this);
        if (componentObj.type == 'Footer') {
            this.contentWrapper = oldContentWrapper;
        }

        this.components.push(component);
        component.create();
    };

    toggleCollapse() {
        if (this.collapsed) {
            this.uncollapse();
        } else {
            this.collapse();
        }
        this.collapsed = !this.collapsed;
        this.collapseChanged = true;
    };

    collapse() {
        var text = this.collapseToggler.firstChild;
        var icon = this.collapseToggler.childNodes[1];
        icon.classList.remove('fa-arrow-up');
        icon.classList.add('fa-arrow-down');
        text.removeChild(text.firstChild);
        text.appendChild(document.createTextNode('rozwiń'));
        this.component.classList.add('collapsed');
    };

    uncollapse() {
        var text = this.collapseToggler.firstChild;
        var icon = this.collapseToggler.childNodes[1];
        icon.classList.remove('fa-arrow-down');
        icon.classList.add('fa-arrow-up');
        text.removeChild(text.firstChild);
        text.appendChild(document.createTextNode('zwiń'));
        this.component.classList.remove('collapsed');
    };

    focusCurrentComponent(deferred, options) {
        if (this.isCollapsible && this.collapsed) {
            this.uncollapse();
            this.collapsed = false;
            this.collapseChanged = true;
            if (this.onToggle) {
                this.fireEvent('onToggle', this.onToggle);
            }
            this.collapseChanged = false;
        }
        deferred.resolve(options);

        return deferred.promise();
    };

    extractChangedAttributes() {
        return this.changesQueue.extractChangedAttributes();
    };

    destroy(removeFromParent) {
        super.destroy(removeFromParent);
    };
}

export {Widget};