import {HTMLFormComponent} from "fh-forms-handler";

class Tree extends HTMLFormComponent {
    private icons: { collapsed: any; notCollapsed: any; leaf: any };

    constructor(componentObj: any, parent: HTMLFormComponent) {
        super(componentObj, parent);

        this.icons = {
            collapsed: this.decomposeIcon(this.componentObj.collapsedNodeIcon),
            notCollapsed: this.decomposeIcon(this.componentObj.nodeIcon),
            leaf: this.decomposeIcon(this.componentObj.leafIcon)
        };
    }

    create() {
        let tree = document.createElement('div');
        tree.id = this.id;
        tree.classList.add('fc');
        tree.classList.add('tree');
        tree.classList.add('treeview');
        if (this.componentObj.lines === true) {
            tree.classList.add('treelines');
        }

        let row = document.createElement('div');
        row.classList.add('eq-row');

        let ulElement = document.createElement('ul');
        ulElement.classList.add('list-group');
        ulElement.classList.add('col-12');

        row.appendChild(ulElement);
        tree.appendChild(row);

        this.component = tree;
        this.wrap();
        this.contentWrapper = ulElement;
        this.hintElement = this.component;
        this.addStyles();
        this.display();

        if (this.componentObj.subelements) {
            this.addComponents(this.componentObj.subelements);
        }

        if (this.componentObj.lazy === false && this.componentObj.expanded === true) {
            this.expandAll();
        }

        if (this.formId === 'designerToolbox') {
            this.expandAll();
        }
    };

    decomposeIcon(icon) {
        if (icon == null) {
            return [];
        } else {
            return icon.split(' ');
        }
    };

    addComponents(componentsList) {
        if (!componentsList.length) {
            return;
        }
        componentsList.forEach(function (componentObj) {
            componentObj.level = 1;
            this.addComponent(componentObj);
        }.bind(this));
    };

    collapseAll() {
        (this.components || []).forEach(function (node) {
            node.collapse();
        }.bind(this));
    };

    expandAll() {
        (this.components || []).forEach(function (node) {
            node.expand();
        }.bind(this));
    };
}

export {Tree};