import { BaseEvent } from "./BaseEvent";
import { Util } from "../Util";
declare class ForcedLogoutEvent extends BaseEvent {
    protected util: Util;
    fire(data: any): void;
}
export { ForcedLogoutEvent };
