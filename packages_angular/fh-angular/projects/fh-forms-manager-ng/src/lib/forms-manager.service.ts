import {Injectable} from '@angular/core';
import {SocketOutputCommands} from "../enum/SocketOutputCommands";
import {SocketHandlerService} from "./socket-handler.service";
import {ServiceManager} from "./ServiceManager";

@Injectable({
    providedIn: 'root'
})
export class FormsManagerService {

    protected serviceManagers: ServiceManager[] = [];

    constructor(private socketHandler: SocketHandlerService) {
    }


    public init(context: string = 'socketForms') {
        this.socketHandler.selectBestConnector();
        this.socketHandler.activeConnector.connect((connectionIdJson: any) => {
            this.socketHandler.connectionId = connectionIdJson.sessionId;

            let requestId = this.socketHandler.activeConnector.getSubsystemMetadata(
                (requestId: any, data: any) => {
                    // this.formsManager.setInitialized();
                    // this.formsManager.handleEvent(requestId, data);

                    for (let deviceManager of this.serviceManagers) {
                        deviceManager.init((config: any) =>
                            this.socketHandler.activeConnector.run(SocketOutputCommands.IN_CLIENT_DATA, config));
                    }
                });
        });

        // $(function () {
        //   $(window).on('hashchange', function () {
        //     if (!this.ignoreNextHashChange) {
        //       var json = {'url': location.pathname + this.formsManager.getLocationHash()};
        //       var requestId = this.socketHandler.activeConnector.run(SocketOutputCommands.URL_CHANGE, json);
        //       this.applicationLock.enable(requestId);
        //     } else {
        //       this.ignoreNextHashChange = false;
        //     }
        //   }.bind(this));
        // }.bind(this));
    }


}
