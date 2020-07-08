import { BaseEvent } from "./BaseEvent";
declare class FocusEvent extends BaseEvent {
    private formsManager;
    constructor();
    fire(data: any): void;
}
export { FocusEvent };
