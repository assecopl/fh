import { BaseEvent } from "./BaseEvent";
import { Util } from "../Util";
declare class MessageEvent extends BaseEvent {
    protected util: Util;
    fire(data: any): void;
    private escapedMap;
    private escapeHtml;
}
export { MessageEvent };
