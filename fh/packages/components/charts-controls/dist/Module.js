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
require("./source/Module.css");
var MeterGaugeChart_1 = require("./source/controls/MeterGaugeChart");
exports.MeterGaugeChart = MeterGaugeChart_1.MeterGaugeChart;
var BarChart_1 = require("./source/controls/BarChart");
exports.BarChart = BarChart_1.BarChart;
var fh_forms_handler_1 = require("fh-forms-handler");
var ChartsControls = /** @class */ (function (_super) {
    __extends(ChartsControls, _super);
    function ChartsControls() {
        return _super !== null && _super.apply(this, arguments) || this;
    }
    ChartsControls.prototype.registerComponents = function () {
        fh_forms_handler_1.FhContainer.bind("BarChart")
            .toFactory(function () {
            return function (componentObj, parent) {
                return new BarChart_1.BarChart(componentObj, parent);
            };
        });
        fh_forms_handler_1.FhContainer.bind("MeterGaugeChart")
            .toFactory(function () {
            return function (componentObj, parent) {
                return new MeterGaugeChart_1.MeterGaugeChart(componentObj, parent);
            };
        });
    };
    return ChartsControls;
}(fh_forms_handler_1.FhModule));
exports.ChartsControls = ChartsControls;
//# sourceMappingURL=Module.js.map