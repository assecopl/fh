import './source/Module.css';
import {MeterGaugeChart} from './source/controls/MeterGaugeChart';
import {BarChart} from './source/controls/BarChart';
import {FhModule, FhContainer} from "fh-forms-handler";
import {Chart4} from "./source/controls/Chart4";

class ChartsControls extends FhModule {
    protected registerComponents() {
        FhContainer.bind<(componentObj: any, parent: any) => BarChart>("BarChart")
            .toFactory<BarChart>(() => {
                return (componentObj: any, parent: any) => {
                    return new BarChart(componentObj, parent);
                };
            });
        FhContainer.bind<(componentObj: any, parent: any) => MeterGaugeChart>("MeterGaugeChart")
            .toFactory<MeterGaugeChart>(() => {
                return (componentObj: any, parent: any) => {
                    return new MeterGaugeChart(componentObj, parent);
                };
            });
        FhContainer.bind<(componentObj: any, parent: any) => Chart4>("Chart4")
            .toFactory<Chart4>(() => {
                return (componentObj: any, parent: any) => {
                    return new Chart4(componentObj, parent);
                };
            });
    }
}

export {ChartsControls, BarChart, MeterGaugeChart, Chart4}
