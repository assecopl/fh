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
require("bootstrap/js/dist/collapse");
var Accordion = /** @class */ (function (_super) {
    __extends(Accordion, _super);
    function Accordion(componentObj, parent) {
        var _this = _super.call(this, componentObj, parent) || this;
        _this.activeGroup = componentObj.activeGroup;
        _this.onGroupChange = _this.componentObj.onGroupChange;
        return _this;
    }
    Accordion.prototype.create = function () {
        var accordion = document.createElement('div');
        accordion.id = this.id;
        ['fc', 'accordion'].forEach(function (cssClass) {
            accordion.classList.add(cssClass);
        });
        this.component = accordion;
        this.hintElement = accordion;
        this.handlemarginAndPAddingStyles();
        this.wrap(true);
        this.display();
        if (this.componentObj.subelements) {
            this.addComponents(this.componentObj.subelements);
        }
    };
    ;
    Accordion.prototype.addComponent = function (componentObj) {
        var component = this.fh.createComponent(componentObj, this);
        this.components.push(component);
        component.create();
        // if (this.designMode) {
        //     component.component.appendChild(component.toolbox);
        //     component.component.addEventListener('mouseenter', function () {
        //         component.showToolbox();
        //     }.bind(this));
        //     component.component.addEventListener('mouseleave', function () {
        //         component.hideToolbox();
        //     }.bind(this));
        // }
        this.contentWrapper.replaceChild(component.component, component.htmlElement);
        component.component.classList.remove('mb-3');
        component.htmlElement = component.component;
        var button = document.createElement('button');
        button.dataset.index = (this.components.length - 1).toString();
        button.dataset.toggle = 'collapse';
        button.classList.add('btn');
        button.attributes['type'] = 'button';
        button.classList.add('btn-block');
        button.classList.add('collapseBtn');
        var active = this.activeGroup === parseInt(button.dataset.index);
        if (!active) {
            button.classList.add('collapsed');
        }
        // link.dataset.parent = '#' + this.id;
        button.setAttribute('aria-expanded', active ? 'true' : 'false');
        var title = component.htmlElement.querySelector('.card-header');
        if (title != null) {
            var titleContent = title.firstChild;
            title.removeChild(titleContent);
            button.appendChild(titleContent);
            title.appendChild(button);
        }
        if (this.onGroupChange) {
            button.addEventListener('click', function (event) {
                if (this.activeGroup == button.dataset.index) {
                    event.preventDefault();
                }
                this.activeGroup = button.dataset.index;
                this.changesQueue.queueValueChange(this.activeGroup);
                this.fireEvent('onGroupChange', this.onGroupChange, event);
            }.bind(this));
        }
        var body = component.htmlElement.querySelector('.card-body');
        var collapseWrapper = document.createElement('div');
        collapseWrapper.dataset.parent = '#' + this.id;
        collapseWrapper.classList.add('collapse');
        if (active || this.designMode) {
            collapseWrapper.classList.add('show');
        }
        var collapsibleBtn = 'collapsible-btn_' + Math.floor((Math.random() * 10000) + 1);
        collapseWrapper.classList.add(collapsibleBtn);
        var checkIfCollapsibleBtnExists = document.querySelectorAll('.' + collapsibleBtn);
        if (checkIfCollapsibleBtnExists.length > 0) {
            collapseWrapper.classList.remove(collapsibleBtn);
            collapsibleBtn = 'collapsible-btn_' + Math.floor((Math.random() * 10000) + 1);
            collapseWrapper.classList.add(collapsibleBtn);
        }
        button.dataset.target = '.' + collapsibleBtn;
        button.setAttribute('aria-controls', collapsibleBtn);
        this.setButtonAccessibility(button, component.accessibility);
        collapseWrapper.appendChild(body);
        component.component.appendChild(collapseWrapper);
    };
    ;
    Accordion.prototype.update = function (change) {
        _super.prototype.update.call(this, change);
        // console.log('%c update ', 'background: #FFF; color: #0F0', change);
        $.each(change.changedAttributes || [], function (name, newValue) {
            switch (name) {
                case 'activeGroup':
                    this.collapse(this.activeGroup);
                    this.activeGroup = newValue;
                    this.show(newValue);
                    break;
            }
        }.bind(this));
    };
    ;
    Accordion.prototype.getAdditionalButtons = function () {
        return [
            new fh_forms_handler_2.AdditionalButton('addDefaultSubcomponent', 'plus', 'Add panel')
        ];
    };
    Accordion.prototype.setButtonAccessibility = function (button, accessibility) {
        if (accessibility !== 'HIDDEN') {
            button.classList.remove('d-none');
            button.classList.remove('invisible');
        }
        switch (accessibility) {
            case 'EDIT':
                button.disabled = false;
                break;
            case 'VIEW':
                button.disabled = true;
                break;
            case 'HIDDEN':
                if (this.invisible) {
                    button.classList.add('invisible');
                }
                else {
                    button.classList.add('d-none');
                }
                break;
            case 'DEFECTED':
                button.disabled = true;
                button.title = 'Broken control';
                break;
        }
    };
    ;
    Accordion.prototype.collapse = function (groupIdx) {
        // @ts-ignore
        var button = this.components[groupIdx].component.querySelector('button');
        button.setAttribute('aria-expanded', 'false');
        button.classList.add("collapsed");
        // @ts-ignore
        var div = this.components[groupIdx].component.querySelector('div.collapse');
        div.classList.remove("show");
    };
    Accordion.prototype.show = function (groupIdx) {
        // @ts-ignore
        var button = this.components[groupIdx].component.querySelector('button');
        button.setAttribute('aria-expanded', 'true');
        button.classList.remove("collapsed");
        // @ts-ignore
        var div = this.components[groupIdx].component.querySelector('div.collapse');
        div.classList.add("show");
    };
    return Accordion;
}(fh_forms_handler_1.HTMLFormComponent));
exports.Accordion = Accordion;
//# sourceMappingURL=Accordion.js.map