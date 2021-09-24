import {injectable, multiInject, optional} from "inversify";
import {BaseEvent} from "./BaseEvent";
import getDecorators from "inversify-inject-decorators";
import {FormsManager} from "../Socket/FormsManager";

import {FhContainer} from "../FhContainer";
import {ClientDataHandler} from "./Handlers/ClientDataHandler";
let {lazyInject} = getDecorators(FhContainer);

@injectable()
class DataToClientEvent extends BaseEvent{

    @lazyInject("FormsManager")
    protected formsManager: FormsManager;

    @multiInject("ClientDataHandler") @optional()
    protected clientDataHandlers: ClientDataHandler[];

    public fire(data: any) {
        if (this.formsManager.ensureFunctionalityUnavailableDuringShutdown()) {
            for (let handler of this.clientDataHandlers){
                if (data.clientData.serviceId === handler.getServiceId()) {
                    handler.handleData(data);
                }
            }
        }
    }
}

export { DataToClientEvent};