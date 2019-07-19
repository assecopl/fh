import './Module.css';
import {MeterGaugeChart} from './source/controls/MeterGaugeChart';
import {BarChart} from './source/controls/BarChart';
import {FhModule, FhContainer} from "fh-forms-handler";

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
    }
}

export {ChartsControls}