import {FhContainer, FhModule} from "fh-forms-handler";
import * as pack from './package.json';
import {ChartTime24FHDP} from './source/charts/ChartTime24FHDP';

class FHDPCharts extends FhModule {

    protected registerComponents() {

        FhContainer.bind<(componentObj: any, parent: any) => ChartTime24FHDP>("ChartTime24FHDP")
        .toFactory<ChartTime24FHDP>(() => {
            return (componentObj: any, parent: any) => {
                return new ChartTime24FHDP(componentObj, parent);
            };
        });

        console.log(`FHDP-charts version: ${pack.version}`)
    }
}

export {FHDPCharts, ChartTime24FHDP}
