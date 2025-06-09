import * as Chartjs4 from 'chart-js-4/dist/chart.js';
import { HTMLFormComponent } from 'fh-forms-handler';


class Chart4 extends HTMLFormComponent {
    protected configuration:Chartjs4.ChartConfiguration;
    private canvas;
    private chart:any;

    constructor(componentObj: any, parent: HTMLFormComponent) {
        super(componentObj, parent);

        this.configuration = this.componentObj.configuration || null;
    }

    create() {
        let container = document.createElement('div');
        container.id = this.id;

        let canvas = document.createElement('canvas');
        this.canvas = canvas;

        container.appendChild(canvas);

        this.component = container;
        this.wrap(true);
        this.handlemarginAndPAddingStyles();
        this.display();


        this.displayChart();
    };

    update(change) {
        super.update(change);
        $.each(change.changedAttributes, function (name, newValue) {
            switch (name) {
                case 'configuration':
                    if(newValue) {
                        this.chart.data = newValue.data;
                        this.chart.options = newValue.options;
                        this.chart.update();
                    }
                    break;

            }

        }.bind(this));
    };

    displayChart() {
        this.chart = new Chartjs4.Chart(this.canvas,this.configuration);
    };



    destroy(removeFromParent) {
        this.chart.destroy();
        this.chart = null;

        super.destroy(removeFromParent);
    }
}

export {Chart4};
