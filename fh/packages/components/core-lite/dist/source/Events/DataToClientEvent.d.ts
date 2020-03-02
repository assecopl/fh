import { BaseEvent } from "./BaseEvent";
import { FormsManager } from "../Socket/FormsManager";
import { ClientDataHandler } from "./Handlers/ClientDataHandler";
declare class DataToClientEvent extends BaseEvent {
    protected formsManager: FormsManager;
    protected clientDataHandlers: ClientDataHandler[];
    fire(data: any): void;
}
export { DataToClientEvent };
