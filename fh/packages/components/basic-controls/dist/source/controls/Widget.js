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
var Widget = /** @class */ (function (_super) {
    __extends(Widget, _super);
    function Widget(componentObj, parent) {
        var _this = _super.call(this, componentObj, parent) || this;
        _this.isCollapsible = Boolean(_this.componentObj.collapsible);
        _this.onToggle = _this.componentObj.onToggle;
        _this.collapsed = Boolean(_this.componentObj.collapsed);
        _this.collapseToggler = null;
        _this.collapseChanged = false;
        _this.groupToolbox = null;
        _this.height = _this.componentObj.height || null;
        _this.attributes = _this.componentObj.attributes || null;
        return _this;
    }
    Widget.prototype.create = function () {
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
    ;
    Widget.prototype.update = function (change) {
        _super.prototype.update.call(this, change);
        $.each(change.changedAttributes, function (name, newValue) {
            switch (name) {
                case 'collapsed':
                    if (newValue) {
                        this.collapse();
                        this.collapsed = true;
                    }
                    else {
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
    ;
    Widget.prototype.addComponent = function (componentObj) {
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
    ;
    Widget.prototype.toggleCollapse = function () {
        if (this.collapsed) {
            this.uncollapse();
        }
        else {
            this.collapse();
        }
        this.collapsed = !this.collapsed;
        this.collapseChanged = true;
    };
    ;
    Widget.prototype.collapse = function () {
        var text = this.collapseToggler.firstChild;
        var icon = this.collapseToggler.childNodes[1];
        icon.classList.remove('fa-arrow-up');
        icon.classList.add('fa-arrow-down');
        text.removeChild(text.firstChild);
        text.appendChild(document.createTextNode('rozwiń'));
        this.component.classList.add('collapsed');
    };
    ;
    Widget.prototype.uncollapse = function () {
        var text = this.collapseToggler.firstChild;
        var icon = this.collapseToggler.childNodes[1];
        icon.classList.remove('fa-arrow-down');
        icon.classList.add('fa-arrow-up');
        text.removeChild(text.firstChild);
        text.appendChild(document.createTextNode('zwiń'));
        this.component.classList.remove('collapsed');
    };
    ;
    Widget.prototype.focusCurrentComponent = function (deferred, options) {
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
    ;
    Widget.prototype.extractChangedAttributes = function () {
        return this.changesQueue.extractChangedAttributes();
    };
    ;
    Widget.prototype.destroy = function (removeFromParent) {
        _super.prototype.destroy.call(this, removeFromParent);
    };
    ;
    return Widget;
}(fh_forms_handler_1.HTMLFormComponent));
exports.Widget = Widget;
//# sourceMappingURL=Widget.js.map