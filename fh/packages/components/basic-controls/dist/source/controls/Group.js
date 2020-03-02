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
var Group = /** @class */ (function (_super) {
    __extends(Group, _super);
    function Group(componentObj, parent) {
        var _this = _super.call(this, componentObj, parent) || this;
        _this.onClick = _this.componentObj.onClick;
        _this.componentObj.verticalAlign = _this.componentObj.verticalAlign || 'top';
        return _this;
    }
    Group.prototype.create = function () {
        var group = document.createElement('div');
        group.id = this.id;
        ['fc', 'group', 'row', 'eq-row'].forEach(function (cssClass) {
            group.classList.add(cssClass);
        });
        if (this.onClick) {
            group.addEventListener('click', function (event) {
                event.stopPropagation();
                this.fireEventWithLock('onClick', this.onClick, event);
            }.bind(this));
        }
        this.component = group;
        this.hintElement = this.component;
        this.wrap(true);
        this.addStyles();
        /*
         this.htmlElement = this.component;
         this.contentWrapper = this.htmlElement;
         */
        this.display();
        if (this.componentObj.subelements) {
            this.addComponents(this.componentObj.subelements);
        }
    };
    ;
    // noinspection JSUnusedGlobalSymbols
    Group.prototype.setPresentationStyle = function (presentationStyle) {
        if (this.parent != null) {
            // @ts-ignore
            this.parent.setPresentationStyle(presentationStyle);
        }
    };
    Group.prototype.getAdditionalButtons = function () {
        return [
            new fh_forms_handler_2.AdditionalButton('addDefaultSubcomponent', 'plus', 'Add empty row')
        ];
    };
    return Group;
}(fh_forms_handler_1.HTMLFormComponent));
exports.Group = Group;
//# sourceMappingURL=Group.js.map