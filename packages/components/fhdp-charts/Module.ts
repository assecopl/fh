import {FhContainer, FhModule} from "fh-forms-handler";
import * as pack from './package.json';
import {ChartTime24FhDP} from './source/charts/ChartTime24FhDP';

class FHDPCharts extends FhModule {

    protected registerComponents() {

        FhContainer.bind<(componentObj: any, parent: any) => ChartTime24FhDP>("ChartTime24FhDP")
        .toFactory<ChartTime24FhDP>(() => {
            return (componentObj: any, parent: any) => {
                return new ChartTime24FhDP(componentObj, parent);
            };
        });

        console.log(`FHDP-charts version: ${pack.version}`)
    }
}

export {FHDPCharts, ChartTime24FhDP}
