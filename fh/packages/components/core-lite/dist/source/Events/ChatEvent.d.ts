import { BaseEvent } from "./BaseEvent";
declare class ChatEvent extends BaseEvent {
    private formsManager;
    fire(data: any): void;
    private fireUpdate;
}
export { ChatEvent };
