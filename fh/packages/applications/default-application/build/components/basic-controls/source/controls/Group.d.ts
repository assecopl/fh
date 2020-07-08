import { HTMLFormComponent } from "fh-forms-handler";
declare class Group extends HTMLFormComponent {
    private readonly onClick;
    constructor(componentObj: any, parent: HTMLFormComponent);
    create(): void;
    setPresentationStyle(presentationStyle: any): void;
    getAdditionalButtons(): any[];
    getDefaultWidth(): string;
}
export { Group };
