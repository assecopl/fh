import { HTMLFormComponent } from "fh-forms-handler";
declare class Link extends HTMLFormComponent {
    private stickedLabel;
    constructor(componentObj: any, parent: HTMLFormComponent);
    create(): void;
    wrap(): void;
    update(change: any): void;
    /**
     * @Override
     */
    getDefaultWidth(): string;
}
export { Link };
