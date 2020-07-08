import { HTMLFormComponent } from "fh-forms-handler";
declare class ValidateMessages extends HTMLFormComponent {
    private isNavigationEnabled;
    constructor(componentObj: any, parent: HTMLFormComponent);
    create(): void;
    update(change: any): void;
    setMessages(messages: any): void;
}
export { ValidateMessages };
