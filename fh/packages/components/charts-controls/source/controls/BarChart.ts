import * as Chart from 'chart.js';
import { HTMLFormComponent } from 'fh-forms-handler';

class BarChart extends HTMLFormComponent {
    private title: any;
    private axes: any;
    private series: any;
    private stacked: any;
    private colors: any;
    private canvas: any;
    private chart: any;

    constructor(componentObj: any, parent: HTMLFormComponent) {
        super(componentObj, parent);

        this.title = this.componentObj.barChartModel.title;
        this.axes = this.componentObj.barChartModel.axes;
        this.series = this.componentObj.barChartModel.series || [];
        this.stacked = !!this.componentObj.stacked;
        this.colors = (this.componentObj.colors || '#f00|#0f0|#00f|#ff0|#f0f|#0ff').split('|');
    }

    create() {
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

    update(change) {
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

    displayChart() {
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

    convertData(allSeries, colors) {
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

    poolColors(a) {
        var pool = [];
        for (var i = 0; i < a; i++) {
            pool.push(this.dynamicColors());
        }
        return pool;
    };

    dynamicColors() {
        var r = Math.floor(Math.random() * 255);
        var g = Math.floor(Math.random() * 255);
        var b = Math.floor(Math.random() * 255);

        return "rgb(" + r + "," + g + "," + b + ")";
    };

    destroy(removeFromParent) {
        this.chart.destroy();
        this.chart = null;

        super.destroy(removeFromParent);
    }
}

export {BarChart};