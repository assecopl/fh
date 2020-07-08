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
var Dropdown_1 = require("./Dropdown/Dropdown");
var Button_1 = require("./Button");
var fh_forms_handler_2 = require("fh-forms-handler");
var ButtonGroup = /** @class */ (function (_super) {
    __extends(ButtonGroup, _super);
    function ButtonGroup(componentObj, parent) {
        var _this = _super.call(this, componentObj, parent) || this;
        _this.activeButton = _this.componentObj.activeButton;
        _this.onButtonChange = _this.componentObj.onButtonChange;
        _this.margin = _this.componentObj.margin || false;
        return _this;
    }
    ButtonGroup.prototype.create = function () {
        var group = document.createElement('div');
        group.id = this.id;
        group.classList.add('fc');
        group.classList.add('buttonGroup');
        group.classList.add('btn-group');
        group.setAttribute('role', 'group');
        if (this.margin == true) {
            group.classList.add('margin');
        }
        this.component = group;
        this.htmlElement = this.component;
        this.wrap(true);
        this.handlemarginAndPAddingStyles();
        this.display();
        if (this.componentObj.subelements) {
            if (this.componentObj.subelements[0].type === 'Repeater') {
                this.addComponents(this.componentObj.subelements[0].subelements);
            }
            else {
                this.addComponents(this.componentObj.subelements);
            }
        }
        if (this.activeButton > -1) {
            this.components[this.activeButton].component.classList.add('active');
        }
    };
    ;
    ButtonGroup.prototype.update = function (change) {
        _super.prototype.update.call(this, change);
        $.each(change.changedAttributes || [], function (name, newValue) {
            switch (name) {
                case 'activeButton':
                    this.activeButton = newValue;
                    var currentActive = this.component.querySelector('.active');
                    if (currentActive) {
                        currentActive.classList.remove('active');
                    }
                    if (this.activeButton > -1) {
                        this.components[newValue].component.classList.add('active');
                    }
                    break;
            }
        }.bind(this));
    };
    ;
    ButtonGroup.prototype.addComponents = function (componentsList) {
        if (!componentsList.length) {
            return;
        }
        componentsList.forEach(function (componentObj) {
            this.addComponent(componentObj);
            var component = this.components[this.components.length - 1];
            this.contentWrapper.removeChild(component.htmlElement);
            component.htmlElement = component.component;
            component.display();
            component.component.dataset.index = this.components.length - 1;
            component.component.addEventListener('click', function (event) {
                var currentActive = this.component.querySelector('.active');
                if (currentActive) {
                    currentActive.classList.remove('active');
                }
                if (currentActive === event.target) {
                    this.activeButton = -1;
                }
                else {
                    this.activeButton = event.target.dataset.index;
                    event.target.classList.add('active');
                }
                this.changesQueue.queueValueChange(this.activeButton);
                if (!component.onClick) {
                    this.fireEvent('onButtonChange', this.onButtonChange, event);
                }
            }.bind(this));
            if (component instanceof Dropdown_1.Dropdown) {
                component.htmlElement.classList.add('btn-group');
            }
            else if (component instanceof Button_1.Button) {
                component.component.classList.remove('btn-block');
                var btnWrapper = document.createElement('div');
                if (component.toolbox) {
                    btnWrapper.classList.add('wrapper');
                    btnWrapper.appendChild(component.toolbox);
                    component.htmlElement = btnWrapper;
                }
            }
            component.display();
            // this.contentWrapper.appendChild(component.htmlElement);
        }.bind(this));
    };
    ;
    ButtonGroup.prototype.extractChangedAttributes = function () {
        return this.changesQueue.extractChangedAttributes();
    };
    ;
    ButtonGroup.prototype.getAdditionalButtons = function () {
        return [
            new fh_forms_handler_2.AdditionalButton('addDefaultSubcomponent', 'plus', 'Add button'),
        ];
    };
    return ButtonGroup;
}(fh_forms_handler_1.HTMLFormComponent));
exports.ButtonGroup = ButtonGroup;
//# sourceMappingURL=ButtonGroup.js.map