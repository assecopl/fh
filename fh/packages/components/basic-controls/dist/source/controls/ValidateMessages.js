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
var ValidateMessages = /** @class */ (function (_super) {
    __extends(ValidateMessages, _super);
    function ValidateMessages(componentObj, parent) {
        var _this = _super.call(this, componentObj, parent) || this;
        _this.isNavigationEnabled = _this.componentObj.navigation;
        return _this;
    }
    ValidateMessages.prototype.create = function () {
        var message = document.createElement('div');
        message.id = this.id;
        message.classList.add('alert');
        switch (this.componentObj.level) {
            case 'OK':
                message.classList.add('alert-success');
                break;
            case 'INFO':
                message.classList.add('alert-info');
                break;
            case 'WARNING':
                message.classList.add('alert-warning');
                break;
            case 'ERROR':
                message.classList.add('alert-danger');
                break;
            case 'BLOCKER':
                message.classList.add('alert-danger');
                break;
            default:
                message.classList.add('alert-danger');
                break;
        }
        this.component = message;
        this.wrap();
        this.addStyles();
        this.display();
        this.setMessages(this.componentObj.validateMessages);
        console.log("ValidateMessages", this.componentObj.htmlAccessibilityRole, this.componentObj.htmlRole);
    };
    ;
    ValidateMessages.prototype.update = function (change) {
        _super.prototype.update.call(this, change);
        if (change.changedAttributes) {
            $.each(change.changedAttributes, function (name, newValue) {
                switch (name) {
                    case 'validateMessages':
                        this.setMessages(newValue);
                        break;
                }
            }.bind(this));
        }
    };
    ;
    ValidateMessages.prototype.setMessages = function (messages) {
        while (this.component.firstChild) {
            this.component.removeChild(this.component.firstChild);
        }
        if (typeof messages === 'undefined' || !messages.length) {
            this.setAccessibility('HIDDEN');
            return;
        }
        else {
            this.setAccessibility('EDIT');
        }
        messages.forEach(function (message) {
            var paragraph = document.createElement('div');
            if (message.elementLabel != null) {
                var strong = document.createElement('span');
                if (this.fhml.needParse(message.elementLabel)) {
                    strong.innerHTML = this.fhml.parse(message.elementLabel + ': ');
                }
                else {
                    strong.appendChild(document.createTextNode(message.elementLabel + ': '));
                }
                strong.dataset.elementId = message.elementId;
                paragraph.appendChild(strong);
            }
            paragraph.appendChild(document.createTextNode(message.message));
            paragraph.dataset.elementId = message.elementId;
            if (this.isNavigationEnabled && message.elementId != null) {
                paragraph.style.cursor = 'pointer';
                paragraph.addEventListener('click', function (event) {
                    this.formsManager.focusComponent(event.target.dataset.elementId);
                }.bind(this));
            }
            this.component.appendChild(paragraph);
        }.bind(this));
    };
    ;
    return ValidateMessages;
}(fh_forms_handler_1.HTMLFormComponent));
exports.ValidateMessages = ValidateMessages;
//# sourceMappingURL=ValidateMessages.js.map