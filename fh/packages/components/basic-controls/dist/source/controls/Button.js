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
var Button = /** @class */ (function (_super) {
    __extends(Button, _super);
    function Button(componentObj, parent) {
        var _this = _super.call(this, componentObj, parent) || this;
        _this.ButtonPL = {
            "button_icon": "Ikona"
        };
        _this.ButtonEN = {
            "button_icon": "Icon"
        };
        _this.style = _this.componentObj.style;
        _this.onClick = _this.componentObj.onClick;
        _this.i18n.registerStrings('pl', _this.ButtonPL);
        _this.i18n.registerStrings('en', _this.ButtonEN);
        return _this;
    }
    Button.prototype.create = function () {
        var label = this.componentObj.label;
        var button = document.createElement('button');
        button.id = this.id;
        ['fc', 'button', 'btn', 'btn-' + this.style, 'btn-block'].forEach(function (cssClass) {
            button.classList.add(cssClass);
        });
        //Check if there is icon inside
        var needParse = this.fhml.needParse(label);
        label = this.resolveLabelAndIcon(label);
        if (needParse && label) {
            //Get raw text from label, remove all html tags. Basicly remove icon tag.
            var text = this.fhml.removeHtmlTags(label);
            if (text.length == 0) {
                //Fill aria-label when there is no text inside label - only icon.
                //Fill with icon default string.
                button.setAttribute("aria-label", this.i18n.__("button_icon"));
            }
        }
        button.innerHTML = label;
        // + "<div style='width:0px !imporant; height: 0px !imporant;color:transparent;'>h</div>";
        // button.value = "adsa";
        if (this.onClick) {
            button.addEventListener('click', this.onClickEvent.bind(this));
        }
        this.component = button;
        this.hintElement = this.component;
        this.focusableComponent = button;
        this.wrap(true);
        this.addStyles();
        this.display();
        if (this.component.classList.contains('listButton')) {
            this.htmlElement.classList.add('listButtonWrapper');
        }
        else if (this.component.classList.contains('listOpertationButton')) {
            this.htmlElement.classList.add('listOperationButtonWrapper');
        }
        else if (this.component.id === 'designerDeleteFormElement') {
            this.htmlElement.classList.add('designerDeleteFormElementWrapper');
        }
        if (this.designMode && this.accessibility === 'VIEW') {
            this.component.classList.add('disabledElement');
            this.component.disabled = false;
        }
    };
    ;
    Button.prototype.onClickEvent = function (event) {
        event.stopPropagation();
        if (this._formId === 'FormPreview') {
            this.fireEvent('onClick', this.onClick);
        }
        else {
            this.fireEventWithLock('onClick', this.onClick);
        }
        event.target.blur();
    };
    Button.prototype.update = function (change) {
        _super.prototype.update.call(this, change);
        if (change.changedAttributes) {
            $.each(change.changedAttributes, function (name, newValue) {
                switch (name) {
                    case 'label':
                        this.component.innerHTML = this.resolveLabelAndIcon(newValue);
                        break;
                    case 'style':
                        this.component.classList.remove('btn-' + this.style);
                        this.component.classList.add('btn-' + newValue);
                        this.style = newValue;
                }
            }.bind(this));
        }
    };
    ;
    Button.prototype.setAccessibility = function (accessibility) {
        _super.prototype.setAccessibility.call(this, accessibility);
        switch (accessibility) {
            case 'DEFECTED':
                this.component.classList.remove('btn-' + this.style);
                this.component.classList.add('btn-danger');
                break;
            default:
                if (this.style !== 'danger') {
                    this.component.classList.remove('btn-danger');
                    this.component.classList.add('btn-' + this.style);
                }
                break;
        }
    };
    ;
    Button.prototype.resolveLabelPosition = function () {
        if (this.componentObj.leftPadding != undefined) {
            var spacerElement = document.createElement('div');
            this.htmlElement.appendChild(spacerElement);
            this.htmlElement.insertBefore(spacerElement, this.htmlElement.firstChild);
            this.htmlElement.classList.add('positioned');
            this.htmlElement.classList.add('left');
            this.setInputAndLabelPosition(this.componentObj.leftPadding, spacerElement, this.component);
        }
    };
    Button.prototype.wrap = function (skipLabel) {
        _super.prototype.wrap.call(this, skipLabel);
        this.htmlElement.classList.add('form-group');
    };
    ;
    Button.prototype.resolveLabelAndIcon = function (label) {
        var l = this.fhml.resolveValueTextOrEmpty(label);
        return l;
    };
    ;
    Button.prototype.extractChangedAttributes = function () {
        return this.changesQueue.extractChangedAttributes();
    };
    ;
    Button.prototype.getAdditionalButtons = function () {
        if (this.parent.componentObj.type === 'ButtonGroup') {
            return [
                new fh_forms_handler_2.AdditionalButton('moveUp', 'arrow-left', 'Move left'),
                new fh_forms_handler_2.AdditionalButton('moveDown', 'arrow-right', 'Move right')
            ];
        }
        else {
            return [];
        }
    };
    Button.prototype.destroy = function (removeFromParent) {
        this.focusableComponent = undefined;
        if (this.onClick) {
            this.component.removeEventListener('click', this.onClickEvent.bind(this));
        }
        _super.prototype.destroy.call(this, removeFromParent);
    };
    /**
     * @Override
     */
    Button.prototype.getDefaultWidth = function () {
        return null;
    };
    return Button;
}(fh_forms_handler_1.HTMLFormComponent));
exports.Button = Button;
//# sourceMappingURL=Button.js.map