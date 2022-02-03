import {FhContainer, FhModule} from "fh-forms-handler";
import * as pack from './package.json';
import {ChartTime24} from './source/charts/ChartTime24';

class FhDPCharts extends FhModule {

    protected registerComponents() {

        FhContainer.bind<(componentObj: any, parent: any) => ChartTime24>("ChartTime24")
        .toFactory<ChartTime24>(() => {
            return (componentObj: any, parent: any) => {
                return new ChartTime24(componentObj, parent);
            };
        });

        console.log(`FhDP-charts version: ${pack.version}`)
    }
}

export {FhDPCharts, ChartTime24}
