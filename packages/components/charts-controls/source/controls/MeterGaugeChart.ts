import * as Chart from 'chart.js';
import {HTMLFormComponent} from 'fh-forms-handler';

/* Chart.js write text plugin */
Chart.pluginService.register({
    afterUpdate: function (a) {
        if (a.config.options.elements.center) {
            var b = Chart.helpers, c = a.config.options.elements.center, d = Chart.defaults.global,
                e = a.chart.ctx, f = b.getValueOrDefault(c.fontStyle, d.defaultFontStyle),
                g = b.getValueOrDefault(c.fontFamily, d.defaultFontFamily);
            if (c.fontSize) {
                var h = c.fontSize;
            } else {
                e.save();
                for (var h = b.getValueOrDefault(c.minFontSize, 1),
                         i = b.getValueOrDefault(c.maxFontSize, 256),
                         j = b.getValueOrDefault(c.maxText, c.text); ;) {
                    e.font = b.fontString(h, f, g);
                    var k = e.measureText(j).width;
                    if (!(k < 2 * a.innerRadius && h < i)) {
                        h -= 1;
                        break
                    }
                    h += 1
                }
                e.restore()
            }
            a.center =
                {
                    font: b.fontString(h, f, g),
                    fillStyle: b.getValueOrDefault(c.fontColor, d.defaultFontColor)
                }
        }
    }, afterDraw: function (a) {
        if (a.center) {
            var b = a.config.options.elements.center, c = a.chart.ctx;
            c.save(), c.font = a.center.font, c.fillStyle = a.center.fillStyle, c.textAlign =
                "center", c.textBaseline = "middle";
            var d = (a.chartArea.left + a.chartArea.right) / 2,
                e = (a.chartArea.top + a.chartArea.bottom) / 2;
            c.fillText(b.text, d, e), c.restore()
        }
    }
});

class MeterGaugeChart extends HTMLFormComponent {
    private title: string;
    private value: number;
    private minValue: number;
    private maxValue: number;
    private unit: any;
    private canvas: any;
    private chart: any;
    private color1: string;
    private color2: string;
    private percentage: number;

    constructor(componentObj: any, parent: HTMLFormComponent) {
        super(componentObj, parent);

        this.title = this.componentObj.title;
        this.value = this.componentObj.value;
        this.maxValue = this.componentObj.maxValue || 100;
        this.minValue = this.componentObj.minValue || 0;
        this.unit = this.componentObj.unit || '';
        this.canvas = null;
        this.chart = null;

        this.color1 = this.componentObj.fillColor || '#f00';
        this.color2 = this.componentObj.color || '#eee';

    }

    calculatePercentage() {
        if (this.value < this.minValue) {
            return 0;
        }

        let denominator = this.maxValue - this.minValue;
        if (denominator == 0) {
            return 100;
        }
        if (this.value > this.maxValue) {
            return 100;
        }
        let fraction = (this.value - this.minValue) / denominator;
        this.percentage = Math.round(fraction * 100);
    }

    create() {
        let container = document.createElement('div');
        container.id = this.id;

        let canvas = document.createElement('canvas');
        this.canvas = canvas;

        container.appendChild(canvas);

        this.component = container;
        this.wrap();
        this.handlemarginAndPAddingStyles();
        this.display();

        this.calculatePercentage();
        this.displayChart();
    };

    update(change) {
        $.each(change.changedAttributes, function (name, newValue) {
            switch (name) {
                case 'title':
                    this.title = newValue;
                    this.chart.options.title.display = !!this.title;
                    this.chart.options.title.text = this.title;
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
                    this.calculatePercentage();
                    this.chart.options.elements.center.text = this.percentage + this.unit;
                    this.chart.data.datasets[0].data = [this.percentage, 100 - this.percentage];
                    break;
                case 'maxValue':
                    this.maxValue = newValue;
                    this.chart.options.elements.center.maxText = this.maxValue + this.unit;

                    this.calculatePercentage();
                    this.chart.options.elements.center.text = this.percentage + this.unit;
                    this.chart.data.datasets[0].data = [this.percentage, 100 - this.percentage];
                    break;
                case 'minValue':
                    this.minValue = newValue;
                    this.calculatePercentage();
                    this.chart.options.elements.center.text = this.percentage + this.unit;
                    this.chart.data.datasets[0].data = [this.percentage, 100 - this.percentage];
                    break;
                case 'unit':
                    this.unit = newValue;
                    this.chart.options.elements.center.maxText = this.maxValue + this.unit;
                    this.chart.options.elements.center.text = this.percentage + this.unit;
                    break;
            }

            this.chart.update();
        }.bind(this));
    };

    displayChart() {
        let ctx = this.canvas;

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
                cutoutPercentage: 90,
                title: {
                    display: this.title && this.title.length > 0,
                    text: this.title
                },
                elements: {
                    center: {
                        maxText: this.maxValue + this.unit,
                        text: this.percentage + this.unit,
                        fontColor: this.color1,
                        fontFamily: "'Helvetica Neue', 'Helvetica', 'Arial', sans-serif",
                        fontStyle: 'normal',
                        minFontSize: 1,
                        maxFontSize: 50
                    }
                },
                tooltips: {
                    enabled: false
                }
            }
        });
    };

    destroy(removeFromParent) {
        this.chart.destroy();
        this.chart = null;

        super.destroy(removeFromParent);
    }
}

export {MeterGaugeChart};
