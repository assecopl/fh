import { HTMLFormComponent } from "fh-forms-handler";
declare class Spacer extends HTMLFormComponent {
    constructor(componentObj: any, parent: HTMLFormComponent);
    create(): void;
    getDefaultWidth(): string;
    wrap(): void;
}
export { Spacer };
