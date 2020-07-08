import { BaseEvent } from "./BaseEvent";
import { FormsManager } from "../Socket/FormsManager";
import { Util } from "../Util";
declare class FileDownloadEvent extends BaseEvent {
    protected formsManager: FormsManager;
    protected util: Util;
    fire(data: any): void;
}
export { FileDownloadEvent };
