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
require("bootstrap/js/dist/dropdown");
var fh_forms_handler_1 = require("fh-forms-handler");
var $ = require("jquery");
var Dropdown = /** @class */ (function (_super) {
    __extends(Dropdown, _super);
    function Dropdown(componentObj, parent) {
        var _this = _super.call(this, componentObj, parent) || this;
        _this.style = _this.componentObj.style;
        _this.onClick = _this.componentObj.onClick;
        _this.button = null;
        _this.menu = null;
        return _this;
    }
    Dropdown.prototype.create = function () {
        //Button.getPrototype().create.call(this);
        var label = this.componentObj.label;
        var button = document.createElement('button');
        button.id = this.id;
        ['fc', 'button', 'btn', 'btn-' + this.style, 'btn-block'].forEach(function (cssClass) {
            button.classList.add(cssClass);
        });
        label = this.resolveLabelAndIcon(label);
        button.innerHTML = label;
        if (this.onClick) {
            button.addEventListener('click', this.onButtonClickEvent.bind(this));
        }
        this.component = button;
        this.hintElement = this.component;
        this.wrap(true);
        this.addStyles();
        this.display();
        button = this.component;
        button.classList.add('dropdown-toggle');
        button.dataset.toggle = 'dropdown';
        this.button = button;
        this.addCaret();
        this.dropdown = document.createElement('div');
        this.dropdown.classList.add('fc');
        this.dropdown.classList.add('dropdown');
        this.dropdown.appendChild(button);
        var menu = document.createElement('div');
        menu.classList.add('dropdown-menu');
        this.menu = menu;
        this.dropdown.appendChild(menu);
        if (this.designMode) {
            this.menu.classList.add('show');
        }
        this.component = this.dropdown;
        this.htmlElement.appendChild(this.component);
        this.contentWrapper = menu;
        this.hintElement = this.component;
        this.menu.addEventListener('click', this.onClickEvent.bind(this));
        if (this.componentObj.subelements) {
            this.addComponents(this.componentObj.subelements);
        }
        if (button.id === 'designerShowPreviewFormButton') {
            menu.classList.add('designerPreviewVariants');
        }
    };
    ;
    Dropdown.prototype.onClickEvent = function (event) {
        event.stopPropagation();
        $(this.dropdown).dropdown('toggle');
    };
    Dropdown.prototype.onButtonClickEvent = function (event) {
        event.stopPropagation();
        this.fireEventWithLock('onClick', this.onClick);
        event.target.blur();
    };
    Dropdown.prototype.update = function (change) {
        _super.prototype.update.call(this, change);
        if (change.changedAttributes) {
            $.each(change.changedAttributes, function (name, newValue) {
                switch (name) {
                    case 'label':
                        this.button.innerHTML = this.resolveLabelAndIcon(newValue);
                        this.addCaret();
                        break;
                    case 'style':
                        this.button.classList.remove('btn-' + this.style);
                        this.button.classList.add('btn-' + newValue);
                        this.style = newValue;
                }
            }.bind(this));
        }
    };
    ;
    Dropdown.prototype.resolveLabelAndIcon = function (label) {
        return this.fhml.resolveValueTextOrEmpty(label);
    };
    ;
    Dropdown.prototype.addCaret = function () {
        var span = document.createElement('span');
        span.classList.add('caret');
        this.button.appendChild(span);
    };
    ;
    Dropdown.prototype.getAdditionalButtons = function () {
        return [
            new fh_forms_handler_1.AdditionalButton('addDefaultSubcomponent', 'plus', 'Add dropdown item')
        ];
    };
    Dropdown.prototype.wrap = function (skipLabel) {
        _super.prototype.wrap.call(this, skipLabel);
        this.htmlElement.classList.add('form-group');
    };
    Dropdown.prototype.setAccessibility = function (accessibility) {
        if (this.button != null) {
            // swap component while changing accessibility
            this.component = this.button;
            _super.prototype.setAccessibility.call(this, accessibility);
            this.component = this.dropdown;
        }
        else {
            _super.prototype.setAccessibility.call(this, accessibility);
            if (this.accessibility === 'VIEW') {
                this.component.disabled = false;
                this.component.classList.add('disabledElement');
            }
        }
    };
    Dropdown.prototype.destroy = function (removeFromParent) {
        if (this.onClick) {
            this.button.removeEventListener('click', this.onButtonClickEvent.bind(this));
        }
        _super.prototype.destroy.call(this, removeFromParent);
    };
    /**
     * @Override
     */
    Dropdown.prototype.getDefaultWidth = function () {
        return "md-2";
    };
    return Dropdown;
}(fh_forms_handler_1.HTMLFormComponent));
exports.Dropdown = Dropdown;
//# sourceMappingURL=Dropdown.js.map