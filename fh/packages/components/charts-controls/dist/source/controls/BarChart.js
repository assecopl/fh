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
var BarChart = /** @class */ (function (_super) {
    __extends(BarChart, _super);
    function BarChart(componentObj, parent) {
        var _this = _super.call(this, componentObj, parent) || this;
        _this.title = _this.componentObj.barChartModel.title;
        _this.axes = _this.componentObj.barChartModel.axes;
        _this.series = _this.componentObj.barChartModel.series || [];
        _this.stacked = !!_this.componentObj.stacked;
        _this.colors = (_this.componentObj.colors || '#f00|#0f0|#00f|#ff0|#f0f|#0ff').split('|');
        return _this;
    }
    BarChart.prototype.create = function () {
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
    BarChart.prototype.update = function (change) {
        var xAxis = this.chart.options.scales.xAxes[0];
        var yAxis = this.chart.options.scales.yAxes[0];
        $.each(change.changedAttributes, function (name, newValue) {
            switch (name) {
                case 'title':
                    this.title = newValue;
                    this.chart.options.title.display = !!this.title;
                    this.chart.options.title.text = this.title;
                    break;
                case 'xAxisLabel':
                    xAxis.scaleLabel.display = !!newValue;
                    xAxis.scaleLabel.labelString = newValue;
                    break;
                case 'yAxisLabel':
                    yAxis.scaleLabel.display = !!newValue;
                    yAxis.scaleLabel.labelString = newValue;
                    break;
                case 'yAxisMin':
                    yAxis.ticks.min = newValue;
                    break;
                case 'yAxisMax':
                    yAxis.ticks.max = newValue;
                    break;
                // TODO: change
                case 'values':
                    if (newValue) {
                        this.series = newValue;
                        var newDatasets = this.convertData(this.series);
                        newDatasets.forEach(function (set, setId) {
                            set.data.forEach(function (item, itemId) {
                                this.chart.data.datasets[setId].data[itemId] = item;
                            }.bind(this));
                            this.chart.data.datasets[setId].label = set.label;
                        }.bind(this));
                    }
                    break;
                case 'colors':
                    var datasets = this.chart.data.datasets;
                    datasets.forEach(function (set, index) {
                        if (newValue[index]) {
                            set.backgroundColor = newValue[index];
                        }
                    });
                    break;
                case 'stacked':
                    this.stacked = newValue;
                    this.chart.options.scales.xAxes[0].stacked = this.stacked;
                    this.chart.options.scales.yAxes[0].stacked = this.stacked;
                    break;
            }
            this.chart.update();
        }.bind(this));
    };
    ;
    BarChart.prototype.displayChart = function () {
        var ctx = this.canvas;
        var datasets = this.convertData(this.series, this.colors);
        var initialSeries = this.series[0];
        var labels;
        if (initialSeries) {
            labels = Object.keys(initialSeries.data);
        }
        this.chart = new Chart(ctx, {
            type: 'bar',
            responsive: true,
            data: {
                labels: labels,
                datasets: datasets
            },
            options: {
                title: {
                    display: !!this.title,
                    text: this.title
                },
                scales: {
                    xAxes: [{
                            stacked: this.stacked,
                            scaleLabel: {
                                display: !!this.axes.X.label,
                                labelString: this.axes.X.label
                            }
                        }],
                    yAxes: [{
                            stacked: this.stacked,
                            scaleLabel: {
                                display: !!this.axes.Y.label,
                                labelString: this.axes.Y.label
                            },
                            ticks: {
                                min: this.axes.Y.min,
                                max: this.axes.Y.max
                            }
                        }]
                }
            }
        });
    };
    ;
    BarChart.prototype.convertData = function (allSeries, colors) {
        var datasets = [];
        colors = colors || [];
        allSeries.forEach(function (series, index) {
            var data = [];
            series.color = colors[index];
            for (var value in series.data) {
                if (series.data.hasOwnProperty(value)) {
                    data.push(series.data[value]);
                }
            }
            var dataset = {
                data: data,
                label: series.label,
                backgroundColor: series.color || this.dynamicColors(),
                borderWidth: 0
            };
            datasets.push(dataset);
        }.bind(this));
        return datasets;
    };
    ;
    BarChart.prototype.poolColors = function (a) {
        var pool = [];
        for (var i = 0; i < a; i++) {
            pool.push(this.dynamicColors());
        }
        return pool;
    };
    ;
    BarChart.prototype.dynamicColors = function () {
        var r = Math.floor(Math.random() * 255);
        var g = Math.floor(Math.random() * 255);
        var b = Math.floor(Math.random() * 255);
        return "rgb(" + r + "," + g + "," + b + ")";
    };
    ;
    BarChart.prototype.destroy = function (removeFromParent) {
        this.chart.destroy();
        this.chart = null;
        _super.prototype.destroy.call(this, removeFromParent);
    };
    return BarChart;
}(fh_forms_handler_1.HTMLFormComponent));
exports.BarChart = BarChart;
//# sourceMappingURL=BarChart.js.map