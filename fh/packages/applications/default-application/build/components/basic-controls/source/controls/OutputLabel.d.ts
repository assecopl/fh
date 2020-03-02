import { HTMLFormComponent } from "fh-forms-handler";
import { FH } from "fh-forms-handler";
declare class OutputLabel extends HTMLFormComponent {
    protected fh: FH;
    private icon;
    private iconAlignment;
    protected onClick: any;
    constructor(componentObj: any, parent: HTMLFormComponent);
    create(): void;
    update(change: any): void;
    buildInnerHTML(): void;
    protected wrap(skipLabel?: boolean, isInputElement?: boolean): void;
    getDefaultWidth(): string;
}
export { OutputLabel };
