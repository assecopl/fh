import { FormComponent } from "fh-forms-handler";
declare class Timer extends FormComponent {
    private applicationLock;
    private connector;
    private interval;
    private active;
    private onTimer;
    private timer;
    constructor(componentObj: any, parent: FormComponent);
    create(): void;
    update(change: any): void;
    private setupTimer;
    destroy(removeFromParent: any): void;
    private timeout;
}
export { Timer };
