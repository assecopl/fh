import { HTMLFormComponent } from 'fh-forms-handler';
declare class BarChart extends HTMLFormComponent {
    protected title: any;
    protected axes: any;
    protected series: any;
    protected stacked: any;
    protected colors: any;
    protected canvas: any;
    protected chart: any;
    constructor(componentObj: any, parent: HTMLFormComponent);
    create(): void;
    update(change: any): void;
    displayChart(): void;
    convertData(allSeries: any, colors: any): any[];
    poolColors(a: any): any[];
    dynamicColors(): string;
    destroy(removeFromParent: any): void;
}
export { BarChart };
