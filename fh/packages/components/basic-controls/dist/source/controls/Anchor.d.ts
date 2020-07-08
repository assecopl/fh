import { HTMLFormComponent } from "fh-forms-handler";
declare class Anchor extends HTMLFormComponent {
    private scrollOnStart;
    private scroll;
    private animateDuration;
    constructor(componentObj: any, parent: HTMLFormComponent);
    create(): void;
    display(): void;
    update(change: any): void;
    protected scrollNow(): void;
}
export { Anchor };
