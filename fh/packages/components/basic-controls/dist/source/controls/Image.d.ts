import { HTMLFormComponent } from "fh-forms-handler";
declare class Image extends HTMLFormComponent {
    private mapElement;
    private mapId;
    private source;
    private onClick;
    private onAreaClick;
    constructor(componentObj: any, parent: HTMLFormComponent);
    create(): void;
    display(): void;
    update(change: any): void;
    wrap(skipLabel: any): void;
    /**
     * @Override
     */
    getDefaultWidth(): string;
}
export { Image };
