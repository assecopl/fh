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
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
};
Object.defineProperty(exports, "__esModule", { value: true });
var fh_forms_handler_1 = require("fh-forms-handler");
var lodash = require("lodash");
var inversify_inject_decorators_1 = require("inversify-inject-decorators");
var fh_forms_handler_2 = require("fh-forms-handler");
var fh_forms_handler_3 = require("fh-forms-handler");
var lazyInject = inversify_inject_decorators_1.default(fh_forms_handler_2.FhContainer).lazyInject;
var OutputLabel = /** @class */ (function (_super) {
    __extends(OutputLabel, _super);
    function OutputLabel(componentObj, parent) {
        var _this = _super.call(this, componentObj, parent) || this;
        _this.onClick = _this.componentObj.onClick;
        _this.icon = _this.componentObj.icon;
        _this.iconAlignment = _this.componentObj.iconAlignment;
        return _this;
    }
    OutputLabel.prototype.create = function () {
        var label = null;
        // @ts-ignore
        if (this.fh.isIE() && this.parent.ieFocusFixEnabled == true) {
            label = document.createElement('span-a');
        }
        else {
            label = document.createElement('span');
        }
        label.id = this.id;
        // output labels for table columns in Designer
        if (this.designMode) {
            var labelId_1 = label.id.split("_");
            var columnLabelMarkers = ['value', 'based', 'label'];
            var columnValueLabel_1 = "";
            columnLabelMarkers.forEach(function (attribute) {
                if (labelId_1.indexOf(attribute) !== -1) {
                    columnValueLabel_1 += attribute;
                }
            });
            if (columnValueLabel_1 === 'valuebasedlabel') {
                label.classList.add('valueBasedLabel');
            }
            var columnId = labelId_1.slice(0, 3).join("_");
            label.dataset.columnId = columnId;
        }
        ['fc', 'outputLabel'].forEach(function (cssClass) {
            label.classList.add(cssClass);
        });
        if (this.onClick) {
            label.addEventListener('click', function (event) {
                event.stopPropagation();
                this.fireEventWithLock('onClick', this.onClick, event);
                event.target.blur();
            }.bind(this));
        }
        this.component = label;
        this.buildInnerHTML();
        this.hintElement = this.component;
        this.wrap();
        this.addStyles();
        this.display();
    };
    ;
    OutputLabel.prototype.update = function (change) {
        _super.prototype.update.call(this, change);
        if (change.changedAttributes) {
            $.each(change.changedAttributes, function (name, newValue) {
                switch (name) {
                    case 'value':
                        this.componentObj.value = newValue;
                        this.buildInnerHTML();
                        break;
                    case 'url':
                        this.componentObj.url = newValue;
                        this.buildInnerHTML();
                        break;
                }
            }.bind(this));
        }
    };
    ;
    OutputLabel.prototype.buildInnerHTML = function () {
        var inner = '';
        if (this.componentObj.value) {
            var value = lodash.escape((this.componentObj.value) ? this.componentObj.value.replace('\\(', '{').replace('\\)', '}') : '');
            value = value.replace(/(?:\r\n|\r|\n)/g, '<br>');
            value = value.replace(/&#39;/g, '\'');
            if (this.fhml.needParse(value)) {
                inner = this.fhml.parse(value, true);
            }
            else {
                inner = value;
            }
        }
        inner = inner.replace('&amp;nbsp;', '&nbsp;');
        if (this.icon) {
            var icon = document.createElement('i');
            var classes = this.componentObj.icon.split(' ');
            switch (classes[0]) {
                case 'fa':
                    icon.classList.add('fa-fw');
                    break;
            }
            icon.classList.add(classes[0]);
            if (classes[1]) {
                icon.classList.add(classes[1]);
            }
            if (this.iconAlignment && this.iconAlignment == 'AFTER') {
                inner = inner + icon.outerHTML;
            }
            else {
                inner = icon.outerHTML + inner;
            }
        }
        this.component.innerHTML = inner;
    };
    ;
    OutputLabel.prototype.wrap = function (skipLabel, isInputElement) {
        if (skipLabel === void 0) { skipLabel = false; }
        if (isInputElement === void 0) { isInputElement = false; }
        var wrappedComponent = this.innerWrap();
        // @ts-ignore
        if (this.fh.isIE() && this.parent.ieFocusFixEnabled == true) {
            var wrapper_1 = document.createElement('div-a');
            ['fc', 'wrapper'].forEach(function (cssClass) {
                wrapper_1.classList.add(cssClass);
            });
            this.wrapInner(wrapper_1, wrappedComponent, skipLabel, isInputElement);
        }
        else {
            _super.prototype.wrap.call(this);
        }
    };
    OutputLabel.prototype.getDefaultWidth = function () {
        return 'md-2';
    };
    __decorate([
        lazyInject("FH"),
        __metadata("design:type", fh_forms_handler_3.FH)
    ], OutputLabel.prototype, "fh", void 0);
    return OutputLabel;
}(fh_forms_handler_1.HTMLFormComponent));
exports.OutputLabel = OutputLabel;
//# sourceMappingURL=OutputLabel.js.map