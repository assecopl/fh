import {Injectable} from '@angular/core';
import {ServiceManager} from "../ServiceManager";
import {SocketHandler} from "./SocketHandler";
import {SocketOutputCommands} from "./SocketOutputCommands";
import {FormsManager} from "./FormsManager";
import {ApplicationLockService} from "../service/application-lock.service";

@Injectable({
    providedIn: 'root',
})
export class FH {

    // @lazyInject('ApplicationLock')
    // private applicationLock: ApplicationLock;
    // @lazyInject('FormsManager')
    // private formsManager: FormsManager;
    // @lazyInject('SocketHandler')

    protected serviceManagers: ServiceManager[] = [];

    private applicationLocked: boolean;
    private ignoreNextHashChange: boolean;

    constructor(private socketHandler: SocketHandler, private formsManager: FormsManager, private applicationLock: ApplicationLockService) {
        this.applicationLocked = false;
        this.ignoreNextHashChange = false;
    }

    // public __init(context: string = 'socketForms') {
    //   this.socketHandler.selectBestConnector();
    //   this.socketHandler.activeConnector.connect((connectionIdJson: any) => {
    //     this.socketHandler.connectionId = connectionIdJson.sessionId;
    //
    //     let requestId = this.socketHandler.activeConnector.getSubsystemMetadata(
    //       (requestId: any, data: any) => {
    //         // this.formsManager.setInitialized();
    //         // this.formsManager.handleEvent(requestId, data);
    //         this.response.next(data);
    //         data.openForm.forEach((form) => {
    //           this.openedForms.push(form);
    //         });
    //
    //         for (let deviceManager of this.serviceManagers) {
    //           deviceManager.init((config: any) =>
    //             this.socketHandler.activeConnector.run(
    //               SocketOutputCommands.IN_CLIENT_DATA,
    //               config
    //             )
    //           );
    //         }
    //       }
    //     );
    //   });


    public init() {
        this.socketHandler.selectBestConnector();
        this.socketHandler.activeConnector.connect((connectionIdJson) => {
            this.socketHandler.connectionId = connectionIdJson.sessionId;

            var requestId = this.socketHandler.activeConnector.getSubsystemMetadata(
                (requestId, data) => {
                    this.formsManager.setInitialized();
                    this.formsManager.handleEvent(requestId, data);

                    for (let deviceManager of this.serviceManagers) {
                        deviceManager.init((config: any) =>
                            this.socketHandler.activeConnector.run(SocketOutputCommands.IN_CLIENT_DATA, config));
                    }
                });
            // this.applicationLock.enable(requestId);
            this.applicationLock.showUC();
        });

        //TODO przeanalizować potem i zaimplementować zmianę Hash-a w URL-u

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

    public initExternal(socketUrl: string) {
        this.socketHandler.selectBestConnector();
        this.socketHandler.activeConnector.connectExternal((connectionIdJson) => {
            this.socketHandler.connectionId = connectionIdJson.sessionId;

            var requestId = this.socketHandler.activeConnector.getSubsystemMetadata(
                (requestId, data) => {
                    this.formsManager.setInitialized();
                    this.formsManager.handleEvent(requestId, data);

                    for (let deviceManager of this.serviceManagers) {
                        deviceManager.init((config: any) =>
                            this.socketHandler.activeConnector.run(SocketOutputCommands.IN_CLIENT_DATA, config));
                    }
                });
            // this.applicationLock.enable(requestId);
            this.applicationLock.showUC();
        }, socketUrl);

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

    //TODO Prawdopodobnie nie będziemy korzystać z tworzenia componentów w ten sposób.
    public createComponent(componentObj: any, parent: any) {
        // var type = componentObj.type;
        //
        // var factory = FhContainer.get(type);
        // if (!factory) {
        //   throw new Error('Component "' + type + '" is not available');
        // }
        // return (<any>factory)(componentObj, parent);
    }

    public changeHash(newHash: string): void {
        if (this.formsManager.getLocationHash() != newHash) {
            this.ignoreNextHashChange = true;
            location.hash = newHash;
        }
    }
}
