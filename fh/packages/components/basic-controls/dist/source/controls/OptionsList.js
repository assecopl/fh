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
var OptionsList = /** @class */ (function (_super) {
    __extends(OptionsList, _super);
    function OptionsList(componentObj, parent) {
        var _this = _super.call(this, componentObj, parent) || this;
        _this.elements = [];
        _this.onIconClick = _this.componentObj.onIconClick || null;
        return _this;
    }
    OptionsList.prototype.create = function () {
        var list = document.createElement("div");
        list.classList.add("list-group");
        list.classList.add('mb-3');
        list.id = this.id;
        this.component = list;
        this.wrap(true);
        this.contentWrapper = list;
        this.addStyles();
        this.display();
        if (this.componentObj.height) {
            list.style.height = "" + this.componentObj.height + 'px';
            list.style.overflowY = "auto";
        }
        // invoke subelements creation
        if (this.componentObj.subelements) {
            this.addComponents(this.componentObj.subelements);
        }
    };
    ;
    OptionsList.prototype.extractChangedAttributes = function () {
        if (this.designMode == true) {
            return this.changesQueue.extractChangedAttributes();
        }
        var attrs = {};
        if (this.elements) {
            /*var allChanges = null;*/
            var elementsChanges = [];
            this.elements.forEach(function (element) {
                if (element.checked != undefined) {
                    if (element.isChecked !== element.checked) {
                        element.isChecked = element.checked;
                        elementsChanges.push({
                            id: element.id, value: element.checked
                        });
                    }
                }
            }.bind(this));
            if (elementsChanges.length > 0) {
                // may be changed to attr per
                attrs[fh_forms_handler_2.FormComponent.VALUE_ATTRIBUTE_NAME] = elementsChanges;
            }
        }
        return attrs;
    };
    ;
    OptionsList.prototype.addComponent = function (componentObj) {
        componentObj.onIconClick = this.onIconClick;
        var component = this.fh.createComponent(componentObj, this);
        this.components.push(component);
        component.create();
    };
    ;
    return OptionsList;
}(fh_forms_handler_1.HTMLFormComponent));
exports.OptionsList = OptionsList;
//# sourceMappingURL=OptionsList.js.map