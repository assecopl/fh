import { BaseEvent } from "./BaseEvent";
import { Util } from "../Util";
declare class ScrollEvent extends BaseEvent {
    protected util: Util;
    constructor();
    fire(data: any): void;
}
export { ScrollEvent };
