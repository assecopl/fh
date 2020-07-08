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
var ElementsHorizontalAlign;
(function (ElementsHorizontalAlign) {
    ElementsHorizontalAlign[ElementsHorizontalAlign["LEFT"] = 0] = "LEFT";
    ElementsHorizontalAlign[ElementsHorizontalAlign["CENTER"] = 1] = "CENTER";
    ElementsHorizontalAlign[ElementsHorizontalAlign["RIGHT"] = 2] = "RIGHT";
    ElementsHorizontalAlign[ElementsHorizontalAlign["BETWEEN"] = 3] = "BETWEEN";
    ElementsHorizontalAlign[ElementsHorizontalAlign["AROUND"] = 4] = "AROUND";
})(ElementsHorizontalAlign || (ElementsHorizontalAlign = {}));
var ElementsVerticalAlign;
(function (ElementsVerticalAlign) {
    ElementsVerticalAlign[ElementsVerticalAlign["TOP"] = 0] = "TOP";
    ElementsVerticalAlign[ElementsVerticalAlign["MIDDLE"] = 1] = "MIDDLE";
    ElementsVerticalAlign[ElementsVerticalAlign["BOTTOM"] = 2] = "BOTTOM";
})(ElementsVerticalAlign || (ElementsVerticalAlign = {}));
var Row = /** @class */ (function (_super) {
    __extends(Row, _super);
    function Row(componentObj, parent) {
        var _this = _super.call(this, componentObj, parent) || this;
        _this.height = componentObj.height;
        if (componentObj.elementsHorizontalAlign) {
            var elementsHorizontalAlignValue = componentObj.elementsHorizontalAlign;
            _this.elementsHorizontalAlign = ElementsHorizontalAlign[elementsHorizontalAlignValue];
        }
        if (componentObj.elementsVerticalAlign) {
            var elementsVerticalAlignValue = componentObj.elementsVerticalAlign;
            _this.elementsVerticalAlign = ElementsVerticalAlign[elementsVerticalAlignValue];
        }
        return _this;
    }
    Row.prototype.create = function () {
        var row = document.createElement('div');
        row.id = this.id;
        row.classList.add('fc');
        row.classList.add('row');
        row.classList.add('sort-row-inner');
        if (this.height) {
            row.style.height = this.height;
        }
        if (this.elementsHorizontalAlign != null) {
            // @ts-ignore
            switch (this.elementsHorizontalAlign) {
                case ElementsHorizontalAlign.LEFT:
                    row.classList.add('justify-content-start');
                    break;
                case ElementsHorizontalAlign.CENTER:
                    row.classList.add('justify-content-center');
                    break;
                case ElementsHorizontalAlign.RIGHT:
                    row.classList.add('justify-content-end');
                    break;
                case ElementsHorizontalAlign.BETWEEN:
                    row.classList.add('justify-content-between');
                    break;
                case ElementsHorizontalAlign.AROUND:
                    row.classList.add('justify-content-around');
                    break;
                default:
                    throw new Error("Unkown elementsHorizontalAlign propety value '" + this.elementsHorizontalAlign + "'!");
            }
        }
        if (this.elementsVerticalAlign != null) {
            switch (this.elementsVerticalAlign) {
                case ElementsVerticalAlign.TOP:
                    row.classList.add('align-items-start');
                    break;
                case ElementsVerticalAlign.MIDDLE:
                    row.classList.add('align-items-center');
                    break;
                case ElementsVerticalAlign.BOTTOM:
                    row.classList.add('align-items-end');
                    break;
                default:
                    throw new Error("Unkown elementsVerticalAlign propety value '" + this.elementsVerticalAlign + "'!");
            }
        }
        this.component = row;
        this.hintElement = this.component;
        this.wrap(true);
        this.addStyles();
        this.display();
        if (this.componentObj.subelements) {
            this.addComponents(this.componentObj.subelements);
        }
    };
    ;
    // noinspection JSUnusedGlobalSymbols
    Row.prototype.setPresentationStyle = function (presentationStyle) {
        if (this.parent != null) {
            // @ts-ignore
            this.parent.setPresentationStyle(presentationStyle);
        }
    };
    return Row;
}(fh_forms_handler_1.HTMLFormComponent));
exports.Row = Row;
//# sourceMappingURL=Row.js.map