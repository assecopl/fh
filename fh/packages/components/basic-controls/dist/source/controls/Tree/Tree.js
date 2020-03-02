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
var Tree = /** @class */ (function (_super) {
    __extends(Tree, _super);
    function Tree(componentObj, parent) {
        var _this = _super.call(this, componentObj, parent) || this;
        _this.icons = {
            collapsed: _this.decomposeIcon(_this.componentObj.collapsedNodeIcon),
            notCollapsed: _this.decomposeIcon(_this.componentObj.nodeIcon),
            leaf: _this.decomposeIcon(_this.componentObj.leafIcon)
        };
        return _this;
    }
    Tree.prototype.create = function () {
        var tree = document.createElement('div');
        tree.id = this.id;
        tree.classList.add('fc');
        tree.classList.add('tree');
        tree.classList.add('treeview');
        if (this.componentObj.lines === true) {
            tree.classList.add('treelines');
        }
        var row = document.createElement('div');
        row.classList.add('eq-row');
        var ulElement = document.createElement('ul');
        ulElement.classList.add('list-group');
        ulElement.classList.add('col-12');
        ulElement.classList.add('treeElementList');
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
    };
    ;
    Tree.prototype.decomposeIcon = function (icon) {
        if (icon == null) {
            return [];
        }
        else {
            return icon.split(' ');
        }
    };
    ;
    Tree.prototype.addComponents = function (componentsList) {
        if (!componentsList.length) {
            return;
        }
        componentsList.forEach(function (componentObj) {
            componentObj.level = 1;
            this.addComponent(componentObj);
        }.bind(this));
    };
    ;
    Tree.prototype.collapseAll = function () {
        (this.components || []).forEach(function (node) {
            node.collapse();
        }.bind(this));
    };
    ;
    Tree.prototype.expandAll = function () {
        (this.components || []).forEach(function (node) {
            node.expand();
        }.bind(this));
    };
    ;
    return Tree;
}(fh_forms_handler_1.HTMLFormComponent));
exports.Tree = Tree;
//# sourceMappingURL=Tree.js.map