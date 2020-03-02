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
var PanelGroup = /** @class */ (function (_super) {
    __extends(PanelGroup, _super);
    function PanelGroup(componentObj, parent) {
        var _this = _super.call(this, componentObj, parent) || this;
        _this.headingTypeValue = null;
        _this.resolveValue = function (value) {
            value = this.fhml.resolveValueTextOrEmpty(value);
            return value;
        };
        _this.componentObj.verticalAlign = _this.componentObj.verticalAlign || 'top';
        _this.isCollapsible = Boolean(_this.componentObj.collapsible);
        _this.onToggle = _this.componentObj.onToggle;
        _this.headingTypeValue = _this.componentObj.headingTypeValue ? _this.componentObj.headingTypeValue : "span";
        _this.collapsed = Boolean(_this.componentObj.collapsed);
        _this.collapseToggler = null;
        _this.collapseChanged = false;
        _this.groupToolbox = null;
        _this.height = _this.componentObj.height || null;
        _this.borderVisible = Boolean(_this.componentObj.borderVisible);
        _this.collapsedOld = _this.collapsed;
        _this.headingElement = null;
        return _this;
    }
    PanelGroup.prototype.create = function () {
        var group = document.createElement('div');
        ['fc', 'group', 'panelGroup', 'mb-3', 'card', 'card-default'].forEach(function (cssClass) {
            group.classList.add(cssClass);
        });
        if (!this.borderVisible) {
            group.classList.add('borderHidden');
        }
        group.id = this.id;
        var heading = document.createElement('div');
        heading.classList.add('card-header');
        heading.classList.add('d-flex');
        var titleElm = document.createElement(this.componentObj.label != null ? this.headingTypeValue : "span"); //Default span
        titleElm.classList.add('mr-auto');
        titleElm.classList.add('card-title');
        titleElm.classList.add('mb-0');
        var titleElmIn = document.createElement('span');
        titleElmIn.id = this.id + '_label';
        titleElm.appendChild(titleElmIn);
        if (this.componentObj.label != null) {
            titleElmIn.innerHTML = this.resolveValue(this.componentObj.label);
        }
        else {
            titleElmIn.innerHTML = '&nbsp;';
        }
        heading.appendChild(titleElm);
        var toolbox = document.createElement('span');
        toolbox.classList.add('toolbox');
        toolbox.classList.add('align-self-center');
        if (this.isCollapsible) {
            var collapseToggle = document.createElement('span');
            collapseToggle.classList.add('collapseToggle');
            var icon = document.createElement('i');
            icon.classList.add('fa');
            // let text = document.createElement('span');
            if (this.collapsed) {
                group.classList.add('collapsed');
                icon.classList.add('fa-arrow-down');
                // text.appendChild(document.createTextNode('rozwiń'));
            }
            else {
                icon.classList.add('fa-arrow-up');
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
        var body = document.createElement('div');
        body.classList.add('card-body');
        var row = document.createElement('div');
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
        var footer = $(body).children('.row').children('.card-footer');
        if (footer.length) {
            if (this.componentObj.height) {
                body.style.height = 'calc(' + this.componentObj.height + ' - 49px - ' + footer[0].clientHeight + 'px)';
                body.classList.add('hasHeight');
            }
            else {
                body.style.height = 'calc(100% - 49px - ' + footer[0].clientHeight + 'px)';
            }
        }
        else {
            if (this.componentObj.height) {
                body.style['overflow-y'] = 'auto';
                body.style.height = this.height;
                body.classList.add('hasHeight');
            }
        }
    };
    ;
    PanelGroup.prototype.update = function (change) {
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
                    this.collapsedOld = this.collapsed;
                    break;
                case 'label':
                    var label = this.component.querySelector('.card-title');
                    if (label != null) {
                        label.removeChild(label.firstChild);
                        var titleElm = document.createElement('span');
                        titleElm.innerHTML = this.resolveValue(newValue);
                        label.appendChild(titleElm);
                    }
                    this.updateHeaderVisibility(newValue);
                    break;
            }
        }.bind(this));
        $(this.component).scrollTop(this.component.clientHeight);
    };
    ;
    PanelGroup.prototype.updateHeaderVisibility = function (newTitle) {
        var hasTitle = this.forceHeader || this.isCollapsible || (newTitle != null && newTitle.trim() != '');
        if (hasTitle) {
            this.headingElement.classList.add('d-flex');
            this.headingElement.classList.remove('d-none');
        }
        else {
            this.headingElement.classList.remove('d-flex');
            this.headingElement.classList.add('d-none');
        }
    };
    ;
    PanelGroup.prototype.toggleCollapse = function () {
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
    PanelGroup.prototype.collapse = function () {
        // let text = this.collapseToggler.firstChild;
        var icon = this.collapseToggler.firstChild; //childNodes[1];
        icon.classList.remove('fa-arrow-up');
        icon.classList.add('fa-arrow-down');
        // text.removeChild(text.firstChild);
        // text.appendChild(document.createTextNode('rozwiń'));
        this.component.classList.add('collapsed');
    };
    ;
    PanelGroup.prototype.uncollapse = function () {
        // let text = this.collapseToggler.firstChild;
        var icon = this.collapseToggler.firstChild; //childNodes[1];
        icon.classList.remove('fa-arrow-down');
        icon.classList.add('fa-arrow-up');
        // text.removeChild(text.firstChild);
        // text.appendChild(document.createTextNode('zwiń'));
        this.component.classList.remove('collapsed');
    };
    ;
    PanelGroup.prototype.extractChangedAttributes = function () {
        var changes = {};
        if (this.collapsedOld != this.collapsed) {
            changes.value = this.collapsed;
            this.collapsedOld = this.collapsed;
        }
        return changes;
    };
    ;
    // noinspection JSUnusedGlobalSymbols
    PanelGroup.prototype.setPresentationStyle = function (presentationStyle) {
        if (this.parent != null) {
            // @ts-ignore
            this.parent.setPresentationStyle(presentationStyle);
        }
    };
    PanelGroup.prototype.getAdditionalButtons = function () {
        if (this.parent.componentObj.type === 'Accordion') {
            return [
                new fh_forms_handler_2.AdditionalButton('moveUp', 'arrow-up', 'Move up'),
                new fh_forms_handler_2.AdditionalButton('moveDown', 'arrow-down', 'Move down')
            ];
        }
    };
    return PanelGroup;
}(fh_forms_handler_1.HTMLFormComponent));
exports.PanelGroup = PanelGroup;
//# sourceMappingURL=PanelGroup.js.map