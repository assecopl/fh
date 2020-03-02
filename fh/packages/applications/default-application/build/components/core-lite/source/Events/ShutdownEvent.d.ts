import { BaseEvent } from "./BaseEvent";
import { Util } from "../Util";
import { Connector } from "../Socket/Connector";
import { FormsManager } from "../Socket/FormsManager";
declare class ShutdownEvent extends BaseEvent {
    protected formsManager: FormsManager;
    protected util: Util;
    protected connector: Connector;
    constructor();
    fire(data: any): void;
}
export { ShutdownEvent };
