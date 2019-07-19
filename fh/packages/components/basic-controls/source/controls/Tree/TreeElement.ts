import {HTMLFormComponent} from "fh-forms-handler";
import {Tree} from "./Tree";

class TreeElement extends HTMLFormComponent {
    private readonly label: string;
    private readonly onIconClick: any;
    private readonly nextLevelExpandable: any;
    private techIconElement: HTMLSpanElement;
    private iconElement: HTMLSpanElement;
    private collapsed: boolean;
    private subTree: any;
    private readonly onLabelClick: any;
    private currentTechIconClasses: any;
    private currentIconClasses: any;
    private icons: any;
    private readonly selectable: any;
    private readonly icon: any;
    private spanWithLabel: any;
    public parent: TreeElement;
    private ul: any;

    constructor(componentObj: any, parent: HTMLFormComponent) {
        super(componentObj, parent);

        this.combinedId = this.parentId + '[' + this.id + ']';
        this.label = this.componentObj.label;
        this.spanWithLabel = null;
        this.subTree = null;
        this.collapsed = this.componentObj.collapsed || true;
        this.icons = this.parent.icons;
        this.icon = this.componentObj.icon;

        this.currentTechIconClasses = [];
        this.techIconElement = null;

        this.currentIconClasses = [];
        this.iconElement = null;

        this.selectable = this.componentObj.selectable;
        this.onLabelClick = this.componentObj.onLabelClick;
        this.onIconClick = this.componentObj.onIconClick;

        this.nextLevelExpandable = this.componentObj.nextLevelExpandable;
    }

    create() {
        let node = this.createLiElement();

        this.contentWrapper.id = this.id + '_treeNodeBody_';
        this.contentWrapper.classList.add('treeNodeBody');
        this.contentWrapper.addEventListener('click', this.labelClicked.bind(this));

        this.techIconElement = document.createElement('span');
        // this.techIconElement.classList.add('icon');
        // this.techIconElement.classList.add('expand-icon');
        this.techIconElement.classList.add('fa-fw');
        this.techIconElement.addEventListener('click', this.iconClicked.bind(this));
        this.contentWrapper.appendChild(this.techIconElement);

        this.iconElement = document.createElement('span');
        this.iconElement.classList.add('icon');
        this.iconElement.classList.add('element-icon');
        this.iconElement.addEventListener('click', this.iconClicked.bind(this));
        this.contentWrapper.appendChild(this.iconElement);

        let span = document.createElement('span');
        ['fc', 'outputLabel'].forEach(function (cssClass) {
            span.classList.add(cssClass);
        });
        span.innerHTML = this.fhml.resolveValueTextOrEmpty(this.label);
        this.spanWithLabel = span;

        this.contentWrapper.appendChild(span);

        this.component = node;
        this.htmlElement = this.component;

        this.createUlElement();

        // from now on sub tree UL element is the new content wrapper element - new element are being added to this UL element
        this.contentWrapper = this.subTree;

        (this.componentObj.subelements || []).forEach(function (nodeObj) {
            this.addComponent(nodeObj);
        }.bind(this));

        this.spanWithLabel.addEventListener('click', this.labelClicked.bind(this));

        this.updateTechIcon();
        this.updateTreeCollapsed();
        this.updateIcon();

        this.addStyles();
        this.display();
    };

    addNodes(nodesList) {
        if (!nodesList.length) {
            return;
        }

        let oldContentWrapper = this.contentWrapper;
        this.contentWrapper = this.ul;

        nodesList.forEach(function (nodeObj) {
            this.addComponent(nodeObj);
        }.bind(this));

        this.contentWrapper = oldContentWrapper;
        this.subTree = this.ul;
        this.component.appendChild(this.ul);
    };

    createUlElement() {
        let ul = document.createElement('ul');
        this.ul = ul;
        ul.classList.add('list-group');
        this.subTree = ul;
        this.component.appendChild(ul);
    };

    createLiElement() {
        let node = document.createElement('li');
        node.id = this.id;
        node.dataset.id = this.id;

        // set data attribute to highlight form element's equivalent in designerToolbox
        if (this.formId === 'designerToolbox' && this.label.indexOf('(') !== -1) {
            let sliceStart = this.label.indexOf('(') + 1;
            let sliceEnd = this.label.length - 1;
            node.dataset.designer_element_equivalent = this.label.slice(sliceStart, sliceEnd);
        }

        ['list-group-item-custom', 'node-treeview1', 'treeNode', 'pl-3']
            .forEach(function (cssClass) {
                node.classList.add(cssClass);
            });

        let row = document.createElement('div');
        row.classList.add('row');

        let col = document.createElement('div');
        col.classList.add('col-md-12');

        row.appendChild(col);
        node.appendChild(row);
        this.contentWrapper = col;

        return node;
    };

    update(change) {
        let beforeComponentCount = this.components.length;

        super.update(change);

        if (beforeComponentCount != this.components.length) {
            this.updateTechIcon();
            this.updateTreeCollapsed();
        }

        if (change.changedAttributes) {
            $.each(change.changedAttributes, function (name, newValue) {
                switch (name) {
                    case 'label':
                        this.label = newValue;
                        this.spanWithLabel.innerHTML = this.fhml.resolveValueTextOrEmpty(this.label);
                        break;
                    case 'selected':
                        if (change.changedAttributes.selected) {
                            this.component.classList.add("selected");
                            let foundTree = this.findTree();
                            foundTree.collapseAll();
                            this.expandAllToSelectedElement();
                        } else {
                            this.component.classList.remove("selected");
                        }
                        break;
                    case 'icon':
                        this.icon = newValue;
                        this.updateIcon();
                        break;
                    case 'nextLevelExpandable':
                        this.nextLevelExpandable = newValue;
                        this.updateTechIcon();
                        break;
                }
            }.bind(this));
        }
    };

    lazyLoadIfNeeded() {
        if (this.nextLevelExpandable && !this.isContainingNestedNodes()) {
            this.fireEventWithLock('onLazyLoad', 'onLazyLoad');
        }
    };

    expandAllToSelectedElement() {
        this.collapsed = false;
        this.updateTreeCollapsed();
        this.updateTechIcon();

        if (this.parent instanceof Tree) {
            return;
        }

        if (this.parent instanceof TreeElement) {
            this.parent.expandAllToSelectedElement();
        }
        this.lazyLoadIfNeeded();
    };

    collapse() {
        this.collapsed = true;
        this.updateTreeCollapsed();
        this.updateTechIcon();

        (this.components || []).forEach(function (node) {
            node.collapse();
        }.bind(this));
    };

    expand() {
        this.collapsed = false;
        this.updateTreeCollapsed();
        this.updateTechIcon();

        (this.components || []).forEach(function (node) {
            if (node.componentObj.type === 'TreeElement') {
                node.expand();
            }
        }.bind(this));
    };

    findTree() {
        if ((<any>this.parent) instanceof TreeElement) {
            return this.parent.findTree();
        }

        if ((<any>this.parent) instanceof Tree) {
            return this.parent;
        }

        return;
    };

    toggleCollaped() {
        this.collapsed = !this.collapsed;
        this.updateTreeCollapsed();
        this.updateTechIcon();
        if (!this.collapsed) {
            this.lazyLoadIfNeeded();
        }
    };

    updateTreeCollapsed() {
        if (!this.collapsed && this.isContainingNestedNodes()) {
            this.subTree.classList.remove('d-none');
        } else {
            this.subTree.classList.add('d-none');
        }
    };

    updateIcon() {
        let newIconClasses;
        if (this.icon != null) {
            newIconClasses = this.icon.split(' ');
        } else {
            newIconClasses = ['d-none'];
        }
        this.currentIconClasses = this.updateAnyIcon(this.iconElement, this.currentIconClasses, newIconClasses);
    };

    updateTechIcon() {
        let newIconClasses;
        if (this.nextLevelExpandable) {
            this.component.classList.remove('isLeaf');
            if (this.collapsed) {
                newIconClasses = this.icons.collapsed;
            } else {
                newIconClasses = this.icons.notCollapsed;
            }
        } else {
            this.component.classList.add('isLeaf');
            newIconClasses = this.icons.leaf;
        }
        this.currentTechIconClasses = this.updateAnyIcon(this.techIconElement, this.currentTechIconClasses, newIconClasses);
    };

    updateAnyIcon(icon, currentIconClasses, newIconClasses) {
        currentIconClasses.forEach(function (iconClass) {
            icon.classList.remove(iconClass)
        });
        newIconClasses.forEach(function (iconClass) {
            icon.classList.add(iconClass)
        });
        return newIconClasses;
    };

    destroy(removeFromParent) {
        super.destroy(removeFromParent);
    };

    isContainingNestedNodes() {
        return this.components != null && this.components.length;
    };

    iconClicked(event) {
        event.stopPropagation();
        this.toggleCollaped();
        if (this.onIconClick) {
            this.fireEventWithLock('onIconClick', "onIconClick");
        }
        return false;
    };

    labelClicked(event) {
        event.stopPropagation();
        if (!this.selectable || this.collapsed) {
            this.toggleCollaped();
        }
        if (this.onLabelClick) {
            this.fireEventWithLock('onLabelClick', "onLabelClick");
        }
        return false;
    };
}

export {TreeElement};