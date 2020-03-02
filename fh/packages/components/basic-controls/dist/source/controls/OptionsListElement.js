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
var OptionsListElement = /** @class */ (function (_super) {
    __extends(OptionsListElement, _super);
    function OptionsListElement(componentObj, parent) {
        var _this = _super.call(this, componentObj, parent) || this;
        _this.canCheck = true;
        _this.onIconClick = componentObj.onIconClick || null;
        _this.onChange = componentObj.onChange || null;
        return _this;
    }
    OptionsListElement.prototype.create = function () {
        var value = this.componentObj.value ?
            (this.componentObj.value.length > 1 ? this.componentObj.value : '&#160;') : '&#160;';
        var listElement = document.createElement("div");
        listElement['role'] = 'button';
        listElement.classList.add("list-group-item");
        listElement.classList.add("list-group-item-action");
        listElement.classList.add("form-check");
        listElement.classList.add("form-check-inline");
        listElement.classList.add("d-flex");
        var label = document.createElement('label');
        label.classList.add('mr-auto');
        label.classList.add('form-check-label');
        label.innerText = value;
        listElement.appendChild(label);
        listElement.id = this.componentObj.id;
        this.isChecked = this.componentObj.checked;
        this.isTitle = !!this.componentObj.title;
        this.canCheck = true;
        if (this.componentObj.icon) {
            var icon = document.createElement('i');
            var classes = this.componentObj.icon.split(' ');
            icon.classList.add('fa-fw');
            icon.classList.add(classes[0]);
            if (classes[1]) {
                icon.classList.add(classes[1]);
            }
            if (this.onIconClick) {
                icon.addEventListener('click', function (event) {
                    event.stopPropagation();
                    this.fireEventWithLock('onIconClick', this.onIconClick, event);
                }.bind(this));
            }
            listElement.appendChild(icon);
        }
        if (this.parent.componentObj.displayCheckbox) {
            var checkBox_1 = document.createElement("input");
            checkBox_1.checked = this.componentObj.checked; // set input checked property
            checkBox_1.type = "checkbox";
            checkBox_1.classList.add("form-check-input");
            listElement.appendChild(checkBox_1);
            this.checkBox = checkBox_1;
            if (this.isTitle) {
                listElement.addEventListener("click", function () {
                    this.toggleCheckAll(this.parent, listElement, checkBox_1);
                }.bind(this));
                listElement.classList.add("active");
            }
            else {
                listElement.addEventListener("click", function (e) {
                    e.stopImmediatePropagation();
                    this.toogleCheckOneCheckBox(this.parent, listElement, checkBox_1);
                }.bind(this));
                checkBox_1.addEventListener("click", function (e) {
                    e.stopImmediatePropagation();
                    this.toogleCheckOneCheckBox(this.parent, listElement, checkBox_1);
                }.bind(this));
                if (this.componentObj.checked != this.checked) {
                    this.checked = this.componentObj.checked;
                    this.toogleElement(listElement);
                }
                // @ts-ignore
                this.parent.elements.forEach(function (element) {
                    if (element.isTitle) {
                        element.canCheck = true; // mozliwosc zaznaczenia belki tytulowej
                        element.readOnly = false;
                        element.checkBox.readOnly = false;
                    }
                }.bind(this));
            }
        }
        else {
            if (!this.isTitle) {
                listElement.addEventListener("click", function () {
                    this.checked = !this.checked;
                    this.toogleElement(listElement);
                }.bind(this));
                if (this.componentObj.checked !== this.checked) {
                    this.checked = this.componentObj.checked;
                    this.toogleElement(listElement);
                }
            }
            else {
                listElement.classList.add("active");
            }
        }
        this.htmlElement = listElement;
        this.component = listElement;
        this.contentWrapper = this.parent.contentWrapper;
        this.parent.elements.push(this);
        this.display();
    };
    ;
    OptionsListElement.prototype.destroy = function (removeFromParent) {
        if (removeFromParent) {
            for (var i = 0; i < this.parent.elements.length; ++i) {
                var element = this.parent.elements[i];
                if (element.id === this.id) {
                    this.parent.elements.splice(i, 1);
                    break;
                }
            }
            // @ts-ignore
            if (this.parent.elements.length == 1) {
                var element = this.parent.elements[0];
                if (element.isTitle) {
                    if (element.checkBox) {
                        element.checkBox.checked = false;
                    }
                    element.checked = false;
                    element.canCheck = false;
                }
            }
        }
        _super.prototype.destroy.call(this, removeFromParent);
    };
    ;
    OptionsListElement.prototype.toogleElement = function (element) {
        if (this.isTitle)
            return;
        if (this.checked) {
            element.classList.add("list-group-item-primary");
        }
        else {
            element.classList.remove("list-group-item-primary");
        }
    };
    ;
    OptionsListElement.prototype.toogleCheckOneCheckBox = function (listComponent, elementHtml, elementCheckboxHtml, forcedValue) {
        if (forcedValue === void 0) { forcedValue = null; }
        if (this.canCheck) {
            var checkedValue = forcedValue != null ? forcedValue : !this.checked;
            elementCheckboxHtml.checked = checkedValue;
            this.checked = checkedValue;
            this.toogleElement(elementHtml);
            if (this.onChange) {
                listComponent.fireEvent('onChange');
            }
        }
        if (forcedValue == null) {
            var allChecked_1 = true;
            var allUnchecked_1 = true;
            // @ts-ignore
            this.parent.elements.forEach(function (element) {
                if (!element.isTitle) {
                    allUnchecked_1 = allUnchecked_1 && !element.htmlElement.querySelector('input').checked;
                    allChecked_1 = allChecked_1 && element.htmlElement.querySelector('input').checked;
                }
            });
            // @ts-ignore
            this.parent.elements.forEach(function (element) {
                if (element.isTitle) {
                    element.htmlElement.querySelector('input').checked = allChecked_1;
                }
            });
            if (allUnchecked_1) {
                // @ts-ignore
                this.parent.elements.forEach(function (element) {
                    if (element.isTitle) {
                        element.htmlElement.querySelector('input').checked = false;
                    }
                });
            }
        }
    };
    ;
    OptionsListElement.prototype.toggleCheckAll = function (listContainer, listElement, titleCheckboxHtml) {
        if (this.canCheck) {
            var checked_1 = titleCheckboxHtml.checked;
            // @ts-ignore
            this.parent.elements.forEach(function (element) {
                var elementCheckboxHtml = element.htmlElement.querySelector('input');
                if (!element.htmlElement.isTitle) {
                    this.toogleCheckOneCheckBox.call(element, this.parent, element.htmlElement, elementCheckboxHtml, checked_1);
                }
            }.bind(this));
            if (this.onChange) {
                listContainer.fireEvent('onChange');
            }
        }
        else {
            listElement.querySelector('input').checked = listElement.checked;
        }
    };
    ;
    return OptionsListElement;
}(fh_forms_handler_1.HTMLFormComponent));
exports.OptionsListElement = OptionsListElement;
//# sourceMappingURL=OptionsListElement.js.map