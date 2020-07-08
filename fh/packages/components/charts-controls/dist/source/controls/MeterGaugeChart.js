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
var Chart = require("chart.js");
var fh_forms_handler_1 = require("fh-forms-handler");
/* Chart.js write text plugin */
Chart.pluginService.register({
    afterUpdate: function (a) {
        if (a.config.options.elements.center) {
            var b = Chart.helpers, c = a.config.options.elements.center, d = Chart.defaults.global, e = a.chart.ctx, f = b.getValueOrDefault(c.fontStyle, d.defaultFontStyle), g = b.getValueOrDefault(c.fontFamily, d.defaultFontFamily);
            if (c.fontSize) {
                var h = c.fontSize;
            }
            else {
                e.save();
                for (var h = b.getValueOrDefault(c.minFontSize, 1), i = b.getValueOrDefault(c.maxFontSize, 256), j = b.getValueOrDefault(c.maxText, c.text);;) {
                    e.font = b.fontString(h, f, g);
                    var k = e.measureText(j).width;
                    if (!(k < 2 * a.innerRadius && h < i)) {
                        h -= 1;
                        break;
                    }
                    h += 1;
                }
                e.restore();
            }
            a.center =
                {
                    font: b.fontString(h, f, g),
                    fillStyle: b.getValueOrDefault(c.fontColor, d.defaultFontColor)
                };
        }
    }, afterDraw: function (a) {
        if (a.center) {
            var b = a.config.options.elements.center, c = a.chart.ctx;
            c.save(), c.font = a.center.font, c.fillStyle = a.center.fillStyle, c.textAlign =
                "center", c.textBaseline = "middle";
            var d = (a.chartArea.left + a.chartArea.right) / 2, e = (a.chartArea.top + a.chartArea.bottom) / 2;
            c.fillText(b.text, d, e), c.restore();
        }
    }
});
var MeterGaugeChart = /** @class */ (function (_super) {
    __extends(MeterGaugeChart, _super);
    function MeterGaugeChart(componentObj, parent) {
        var _this = _super.call(this, componentObj, parent) || this;
        _this.title = _this.componentObj.title;
        _this.percentage = _this.componentObj.percentage;
        _this.value = _this.componentObj.value;
        _this.maxValue = _this.componentObj.maxValue || 100;
        _this.unit = _this.componentObj.unit || '';
        _this.canvas = null;
        _this.chart = null;
        _this.color1 = _this.componentObj.fillColor || '#f00';
        _this.color2 = _this.componentObj.color || '#eee';
        return _this;
    }
    MeterGaugeChart.prototype.create = function () {
        var container = document.createElement('div');
        container.id = this.id;
        var canvas = document.createElement('canvas');
        this.canvas = canvas;
        container.appendChild(canvas);
        this.component = container;
        this.wrap();
        this.handlemarginAndPAddingStyles();
        this.display();
        this.displayChart();
    };
    ;
    MeterGaugeChart.prototype.update = function (change) {
        $.each(change.changedAttributes, function (name, newValue) {
            switch (name) {
                case 'title':
                    this.title = newValue;
                    this.chart.options.title.display = !!this.title;
                    this.chart.options.title.text = this.title;
                    break;
                case 'percentage':
                    this.percentage = newValue;
                    this.chart.data.datasets[0].data = [this.percentage, 100 - this.percentage];
                    break;
                case 'fillColor':
                    this.color1 = newValue;
                    this.chart.data.datasets[0].backgroundColor[0] = this.color1;
                    this.chart.data.datasets[0].hoverBackgroundColor[0] = this.color1;
                    this.chart.options.elements.center.fontColor = this.color1;
                    break;
                case 'color':
                    this.color2 = newValue;
                    this.chart.data.datasets[0].backgroundColor[1] = this.color2;
                    this.chart.data.datasets[0].hoverBackgroundColor[1] = this.color2;
                    break;
                case 'value':
                    this.value = newValue;
                    this.chart.options.elements.center.text = this.value + this.unit;
                    break;
                case 'unit':
                    this.unit = newValue;
                    this.chart.options.elements.center.maxText = this.maxValue + this.unit;
                    this.chart.options.elements.center.text = this.value + this.unit;
                    break;
            }
            this.chart.update();
        }.bind(this));
    };
    ;
    MeterGaugeChart.prototype.displayChart = function () {
        var ctx = this.canvas;
        this.chart = new Chart(ctx, {
            type: 'doughnut',
            data: {
                datasets: [{
                        data: [this.percentage, 100 - this.percentage],
                        backgroundColor: [this.color1, this.color2],
                        hoverBackgroundColor: [this.color1, this.color2],
                        borderWidth: 0
                    }]
            },
            options: {
                cutoutPercentage: 70,
                title: {
                    display: !!this.title,
                    text: this.title
                },
                elements: {
                    center: {
                        maxText: this.maxValue + this.unit,
                        text: this.value + this.unit,
                        fontColor: this.color1,
                        fontFamily: "'Helvetica Neue', 'Helvetica', 'Arial', sans-serif",
                        fontStyle: 'normal',
                        minFontSize: 1,
                        maxFontSize: 256
                    }
                },
                tooltips: {
                    enabled: false
                }
            }
        });
    };
    ;
    MeterGaugeChart.prototype.destroy = function (removeFromParent) {
        this.chart.destroy();
        this.chart = null;
        _super.prototype.destroy.call(this, removeFromParent);
    };
    return MeterGaugeChart;
}(fh_forms_handler_1.HTMLFormComponent));
exports.MeterGaugeChart = MeterGaugeChart;
//# sourceMappingURL=MeterGaugeChart.js.map