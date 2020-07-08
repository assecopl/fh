import { BaseEvent } from "./BaseEvent";
declare class ChatListEvent extends BaseEvent {
    private formsManager;
    fire(data: {
        show: boolean;
    }): void;
}
export { ChatListEvent };
