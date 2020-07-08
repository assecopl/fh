import { BaseEvent } from "./BaseEvent";
declare class CloseTabEvent extends BaseEvent {
    fire(data: {
        uuid: string;
    }): void;
}
export { CloseTabEvent };
