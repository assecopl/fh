import { BaseEvent } from "./BaseEvent";
import { FH } from "../Socket/FH";
import { Util } from "../Util";
declare class RedirectEvent extends BaseEvent {
    fh: FH;
    private socketHandler;
    protected util: Util;
    fire(data: {
        uuid: string;
        url: string;
        newWindow: boolean;
        closeable: boolean;
    }): void;
}
export { RedirectEvent };
