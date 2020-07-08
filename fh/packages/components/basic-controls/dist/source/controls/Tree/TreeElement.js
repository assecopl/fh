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
var Tree_1 = require("./Tree");
var TreeElement = /** @class */ (function (_super) {
    __extends(TreeElement, _super);
    function TreeElement(componentObj, parent) {
        var _this = _super.call(this, componentObj, parent) || this;
        _this.combinedId = _this.parentId + '[' + _this.id + ']';
        _this.label = _this.componentObj.label;
        _this.spanWithLabel = null;
        _this.subTree = null;
        _this.collapsed = _this.componentObj.collapsed;
        _this.icons = _this.parent.icons;
        _this.icon = _this.componentObj.icon;
        _this.currentTechIconClasses = [];
        _this.techIconElement = null;
        _this.selected = _this.componentObj.selected || false;
        _this.currentIconClasses = [];
        _this.iconElement = null;
        _this.selectable = _this.componentObj.selectable;
        _this.onLabelClick = _this.componentObj.onLabelClick;
        _this.onIconClick = _this.componentObj.onIconClick;
        _this.nextLevelExpandable = _this.componentObj.nextLevelExpandable;
        return _this;
    }
    TreeElement.prototype.create = function () {
        var node = this.createLiElement();
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
        var span = document.createElement('span');
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
        if (this.selected) {
            this.expandAllToSelectedElement();
        }
    };
    ;
    TreeElement.prototype.addNodes = function (nodesList) {
        if (!nodesList.length) {
            return;
        }
        var oldContentWrapper = this.contentWrapper;
        this.contentWrapper = this.ul;
        nodesList.forEach(function (nodeObj) {
            this.addComponent(nodeObj);
        }.bind(this));
        this.contentWrapper = oldContentWrapper;
        this.subTree = this.ul;
        this.component.appendChild(this.ul);
    };
    ;
    TreeElement.prototype.createUlElement = function () {
        var ul = document.createElement('ul');
        this.ul = ul;
        ul.classList.add('list-group');
        this.subTree = ul;
        this.component.appendChild(ul);
    };
    ;
    TreeElement.prototype.createLiElement = function () {
        var node = document.createElement('li');
        node.id = this.id;
        node.dataset.id = this.id;
        // set data attribute to highlight form element's equivalent in designerToolbox
        if (this.formId === 'designerToolbox' && this.label.indexOf('(') !== -1) {
            var sliceStart = this.label.indexOf('(') + 1;
            var sliceEnd = this.label.length - 1;
            node.dataset.designer_element_equivalent = this.label.slice(sliceStart, sliceEnd);
        }
        ['list-group-item-custom', 'node-treeview1', 'treeNode', 'pl-3']
            .forEach(function (cssClass) {
            node.classList.add(cssClass);
        });
        var row = document.createElement('div');
        row.classList.add('row');
        var col = document.createElement('div');
        col.classList.add('col-md-12');
        row.appendChild(col);
        node.appendChild(row);
        this.contentWrapper = col;
        return node;
    };
    ;
    TreeElement.prototype.update = function (change) {
        var beforeComponentCount = this.components.length;
        _super.prototype.update.call(this, change);
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
                    case 'collapsed':
                        this.collapsed = change.changedAttributes.collapsed;
                        this.updateTreeCollapsed();
                        break;
                    case 'selected':
                        if (change.changedAttributes.selected) {
                            this.component.classList.add("selected");
                            this.selected = true;
                        }
                        else {
                            this.component.classList.remove("selected");
                            this.selected = false;
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
    ;
    TreeElement.prototype.lazyLoadIfNeeded = function () {
        if (this.nextLevelExpandable && !this.isContainingNestedNodes()) {
            this.fireEventWithLock('onLazyLoad', 'onLazyLoad');
        }
    };
    ;
    TreeElement.prototype.expandAllToSelectedElement = function () {
        this.collapsed = false;
        this.updateTreeCollapsed();
        this.updateTechIcon();
        if (this.parent instanceof Tree_1.Tree) {
            return;
        }
        if (this.parent instanceof TreeElement) {
            this.parent.expandAllToSelectedElement();
        }
        this.lazyLoadIfNeeded();
    };
    ;
    TreeElement.prototype.collapse = function () {
        this.collapsed = true;
        this.updateTreeCollapsed();
        this.updateTechIcon();
        (this.components || []).forEach(function (node) {
            node.collapse();
        }.bind(this));
    };
    ;
    TreeElement.prototype.expand = function () {
        this.collapsed = false;
        this.updateTreeCollapsed();
        this.updateTechIcon();
        (this.components || []).forEach(function (node) {
            if (node.componentObj.type === 'TreeElement') {
                node.expand();
            }
        }.bind(this));
    };
    ;
    TreeElement.prototype.findTree = function () {
        if (this.parent instanceof TreeElement) {
            return this.parent.findTree();
        }
        if (this.parent instanceof Tree_1.Tree) {
            return this.parent;
        }
        return;
    };
    ;
    TreeElement.prototype.toggleCollaped = function () {
        this.collapsed = !this.collapsed;
        this.updateTreeCollapsed();
        this.updateTechIcon();
        if (!this.collapsed) {
            this.lazyLoadIfNeeded();
        }
    };
    ;
    TreeElement.prototype.updateTreeCollapsed = function () {
        if (!this.collapsed && this.isContainingNestedNodes()) {
            this.subTree.classList.remove('d-none');
        }
        else {
            this.subTree.classList.add('d-none');
        }
    };
    ;
    TreeElement.prototype.updateIcon = function () {
        var newIconClasses;
        if (this.icon != null) {
            newIconClasses = this.icon.split(' ');
        }
        else {
            newIconClasses = ['d-none'];
        }
        this.currentIconClasses = this.updateAnyIcon(this.iconElement, this.currentIconClasses, newIconClasses);
    };
    ;
    TreeElement.prototype.updateTechIcon = function () {
        var newIconClasses;
        if (this.nextLevelExpandable) {
            this.component.classList.remove('isLeaf');
            if (this.collapsed) {
                newIconClasses = this.icons.collapsed;
            }
            else {
                newIconClasses = this.icons.notCollapsed;
            }
        }
        else {
            this.component.classList.add('isLeaf');
            newIconClasses = this.icons.leaf;
        }
        this.currentTechIconClasses = this.updateAnyIcon(this.techIconElement, this.currentTechIconClasses, newIconClasses);
    };
    ;
    TreeElement.prototype.updateAnyIcon = function (icon, currentIconClasses, newIconClasses) {
        currentIconClasses.forEach(function (iconClass) {
            icon.classList.remove(iconClass);
        });
        newIconClasses.forEach(function (iconClass) {
            icon.classList.add(iconClass);
        });
        return newIconClasses;
    };
    ;
    TreeElement.prototype.destroy = function (removeFromParent) {
        _super.prototype.destroy.call(this, removeFromParent);
    };
    ;
    TreeElement.prototype.isContainingNestedNodes = function () {
        return this.components != null && this.components.length;
    };
    ;
    TreeElement.prototype.iconClicked = function (event) {
        event.stopPropagation();
        this.toggleCollaped();
        if (this.nextLevelExpandable) {
            this.changesQueue.queueAttributeChange('collapsed', this.collapsed);
        }
        else {
            this.selected = !this.selected;
            this.changesQueue.queueAttributeChange('selected', this.selected);
        }
        if (this.onIconClick) {
            this.fireEventWithLock('onIconClick', "onIconClick");
        }
        return false;
    };
    ;
    TreeElement.prototype.labelClicked = function (event) {
        event.stopPropagation();
        if (!this.selectable || this.collapsed) {
            this.toggleCollaped();
        }
        // if (this.nextLevelExpandable) {
        this.changesQueue.queueAttributeChange('collapsed', this.collapsed);
        // } else {
        this.selected = !this.selected;
        this.changesQueue.queueAttributeChange('selected', this.selected);
        // }
        if (this.onLabelClick) {
            this.fireEventWithLock('onLabelClick', "onLabelClick");
        }
        return false;
    };
    ;
    return TreeElement;
}(fh_forms_handler_1.HTMLFormComponent));
exports.TreeElement = TreeElement;
//# sourceMappingURL=TreeElement.js.map