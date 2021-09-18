import {inject, injectable} from "inversify";
import {FormsManager} from "../Socket/FormsManager";
import {SocketOutputCommands} from "../Socket/SocketOutputCommands";
import {SocketHandler} from "../Socket/SocketHandler";

@injectable()
export class ServiceManagerUtil {
    @inject("FormsManager")
    private formsManager: FormsManager;

    @inject('SocketHandler')
    private socketHandler: SocketHandler;

    public callAction(serviceId: string, actionName: string, params: any[] = undefined, doLock: boolean = true):void {
        let deferedEvent = {
            component: this,
            serviceId: serviceId,
            deferred: $.Deferred()
        };

        this.formsManager.eventQueue.push(deferedEvent);
        if (this.formsManager.eventQueue.length == 1) {
            deferedEvent.deferred.resolve();
        }

        let success = this.formsManager.fireEvent(null, actionName, null, serviceId, deferedEvent, doLock, params);
        if (!success) {
            this.formsManager.eventQueue.pop();
        }
    }

    public cancelServiceEvents(serviceId: string) {
        this.formsManager.eventQueue = this.formsManager.eventQueue.filter(event => event.serviceId !== serviceId);
    }

    public sendData(serviceId: string, data: any) {
        data.serviceId = serviceId;
        this.socketHandler.activeConnector.run(SocketOutputCommands.IN_CLIENT_DATA, {
            "clientMessage" : data
        });
    }
}