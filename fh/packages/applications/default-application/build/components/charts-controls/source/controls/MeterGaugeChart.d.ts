import { HTMLFormComponent } from 'fh-forms-handler';
declare class MeterGaugeChart extends HTMLFormComponent {
    private title;
    private percentage;
    private value;
    private maxValue;
    private unit;
    private canvas;
    private chart;
    private color1;
    private color2;
    constructor(componentObj: any, parent: HTMLFormComponent);
    create(): void;
    update(change: any): void;
    displayChart(): void;
    destroy(removeFromParent: any): void;
}
export { MeterGaugeChart };
