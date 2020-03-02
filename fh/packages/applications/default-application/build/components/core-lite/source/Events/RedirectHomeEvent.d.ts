import { BaseEvent } from "./BaseEvent";
import { FH } from "../Socket/FH";
import { Util } from "../Util";
declare class RedirectHomeEvent extends BaseEvent {
    fh: FH;
    private socketHandler;
    protected util: Util;
    fire(): void;
}
export { RedirectHomeEvent };
