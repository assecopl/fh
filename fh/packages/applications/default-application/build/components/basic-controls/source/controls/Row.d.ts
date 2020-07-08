import { HTMLFormComponent } from "fh-forms-handler";
declare class Row extends HTMLFormComponent {
    private readonly elementsHorizontalAlign;
    private readonly elementsVerticalAlign;
    private readonly height;
    constructor(componentObj: any, parent: HTMLFormComponent);
    create(): void;
    setPresentationStyle(presentationStyle: any): void;
}
export { Row };
