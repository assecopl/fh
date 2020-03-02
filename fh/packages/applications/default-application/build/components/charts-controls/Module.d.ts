import './source/Module.css';
import { MeterGaugeChart } from './source/controls/MeterGaugeChart';
import { BarChart } from './source/controls/BarChart';
import { FhModule } from "fh-forms-handler";
declare class ChartsControls extends FhModule {
    protected registerComponents(): void;
}
export { ChartsControls, BarChart, MeterGaugeChart };
