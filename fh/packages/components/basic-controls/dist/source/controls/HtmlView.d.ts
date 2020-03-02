import { HTMLFormComponent } from "fh-forms-handler";
declare class HtmlView extends HTMLFormComponent {
    protected code: string;
    protected element: HTMLDivElement;
    constructor(componentObj: any, parent: HTMLFormComponent);
    sanitizeCode(code: string): any;
    create(): void;
    update(change: any): void;
}
export { HtmlView };
