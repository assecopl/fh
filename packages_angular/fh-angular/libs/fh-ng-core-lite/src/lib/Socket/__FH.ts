import {injectable, multiInject, optional} from "inversify";
import getDecorators from "inversify-inject-decorators";
import {ApplicationLock} from "./ApplicationLock";
import {SocketHandler} from "./SocketHandler";
import {FormsManager} from "./FormsManager";
import * as $ from 'jquery';
import {SocketOutputCommands} from "./SocketOutputCommands";

import {FhContainer} from "../FhContainer";
import {ServiceManager} from "../Devices/ServiceManager";

let {lazyInject} = getDecorators(FhContainer);

@injectable()
class __FH {
    @lazyInject('ApplicationLock')
    private applicationLock: ApplicationLock;
    @lazyInject('FormsManager')
    private formsManager: FormsManager;
    @lazyInject('SocketHandler')
    private socketHandler: SocketHandler;
    @multiInject("ServiceManager") @optional()
    protected serviceManagers: ServiceManager[];

    private applicationLocked: boolean;
    private ignoreNextHashChange: boolean;

    constructor() {
        this.applicationLocked = false;
        this.ignoreNextHashChange = false;
    }

    public init() {
        this.socketHandler.selectBestConnector();
        this.socketHandler.activeConnector.connect(function (connectionIdJson) {
            this.socketHandler.connectionId = connectionIdJson.sessionId;

            var requestId = this.socketHandler.activeConnector.getSubsystemMetadata(
                function (requestId, data) {
                    this.formsManager.setInitialized();
                    this.formsManager.handleEvent(requestId, data);

                    for (let deviceManager of this.serviceManagers) {
                        deviceManager.init((config: any) =>
                            this.socketHandler.activeConnector.run(SocketOutputCommands.IN_CLIENT_DATA, config));
                    }
                }.bind(this));
            this.applicationLock.enable(requestId);
        }.bind(this));

        $(function () {
            $(window).on('hashchange', function () {
                if (!this.ignoreNextHashChange) {
                    var json = {'url': location.pathname + this.formsManager.getLocationHash()};
                    var requestId = this.socketHandler.activeConnector.run(SocketOutputCommands.URL_CHANGE, json);
                    this.applicationLock.enable(requestId);
                } else {
                    this.ignoreNextHashChange = false;
                }
            }.bind(this));
        }.bind(this));
    }

    public initExternal(socketUrl: string) {
        this.socketHandler.selectBestConnector();
        this.socketHandler.activeConnector.connectExternal(function (connectionIdJson) {
            this.socketHandler.connectionId = connectionIdJson.sessionId;

            var requestId = this.socketHandler.activeConnector.getSubsystemMetadata(
                function (requestId, data) {
                    this.formsManager.setInitialized();
                    this.formsManager.handleEvent(requestId, data);

                    for (let deviceManager of this.serviceManagers) {
                        deviceManager.init((config: any) =>
                            this.socketHandler.activeConnector.run(SocketOutputCommands.IN_CLIENT_DATA, config));
                    }
                }.bind(this));
            this.applicationLock.enable(requestId);
        }.bind(this), socketUrl);

        $(function () {
            $(window).on('hashchange', function () {
                if (!this.ignoreNextHashChange) {
                    var json = {'url': location.pathname + this.formsManager.getLocationHash()};
                    var requestId = this.socketHandler.activeConnector.run(SocketOutputCommands.URL_CHANGE, json);
                    this.applicationLock.enable(requestId);
                } else {
                    this.ignoreNextHashChange = false;
                }
            }.bind(this));
        }.bind(this));
    }

    public createComponent(componentObj: any, parent: any) {
        var type = componentObj.type;

        var factory = FhContainer.get(type);
        if (!factory) {
            throw new Error('Component "' + type + '" is not available');
        }
        return (<any>factory)(componentObj, parent);
    }

    public changeHash(newHash: string): void {
        if (this.formsManager.getLocationHash() != newHash) {
            this.ignoreNextHashChange = true;
            location.hash = newHash;
        }
    }

    public isIE() {
        let ua = window.navigator.userAgent;

        let msie = ua.indexOf('MSIE ');
        if (msie > 0) {
            // IE 10 or older => return version number
            return parseInt(ua.substring(msie + 5, ua.indexOf('.', msie)), 10);
        }

        let trident = ua.indexOf('Trident/');
        if (trident > 0) {
            // IE 11 => return version number
            let rv = ua.indexOf('rv:');
            return parseInt(ua.substring(rv + 3, ua.indexOf('.', rv)), 10);
        }

        let edge = ua.indexOf('Edge/');
        if (edge > 0) {
            // Edge (IE 12+) => return version number
            return parseInt(ua.substring(edge + 5, ua.indexOf('.', edge)), 10);
        }

        // other browser
        return false;
    }
}

export {__FH};
