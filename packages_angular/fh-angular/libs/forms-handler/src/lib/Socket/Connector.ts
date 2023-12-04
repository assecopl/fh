import {SocketOutputCommands} from "./SocketOutputCommands";
import * as Pako from "pako";
import {ApplicationLockService} from "../service/application-lock.service";
import {Injectable, Optional, inject} from "@angular/core";
import {Utils} from "../service/Utils";
import {I18nService} from "../service/i18n.service";
import {FormsManager} from "./FormsManager";

declare const ENV_IS_DEVELOPMENT: boolean;

@Injectable({
    providedIn: 'root',
})
class Connector {
    // @lazyInject('ApplicationLock')
    private applicationLock: ApplicationLockService = inject(ApplicationLockService);
    // @lazyInject('FormsManager')
    private formsManager: FormsManager = inject(FormsManager);
    // @lazyInject('Util')
    private util: Utils = inject(Utils);
    // @lazyInject('I18n')
    private i18n: I18nService = inject(I18nService);


    public target: string;
    public reconnectCallback: any;
    public openCallback: any;

    private runningCommands: { [key: string]: any; } = {};
    private ws: WebSocket = null;
    private reconnecting: boolean = false;
    private doNotReconnect: boolean = false;
    private retryCount: number = 0;
    private reconnectTimeoutMs: number = 3000;
    private reconnectDecisionDialogMs: number = 30000;
    private reconnectStartTime: number = 0;
    private serverAlive: boolean = true;
    private headResponseRetry: number = 0;
    private maxHeadResponseRetry: number = 3;
    private runFunction: any = null;
    private pingInterval: () => void = null;
    private _incomingMessageCallback: (data: string) => void = null;
    private _outcomingMessageCallback: (data: string) => void = null;

    private totalLengthOfAllCompressedMessages: number = 0;
    private totalLengthOfAllUncompressedMessages: number = 0;

    constructor() {
        window.addEventListener('beforeunload', this.close.bind(this));

    }

    public setup(target: string,
                 reconnectCallback?: () => void | null,
                 openCallback?: () => void | null) {
        this.target = target;
        this.reconnectCallback = reconnectCallback;
        this.openCallback = openCallback;
    }

    set incomingMessageCallback(callback: (data: string) => void) {
        this._incomingMessageCallback = callback;
    }

    set outcomingMessageCallback(callback: (data: string) => void) {
        this._outcomingMessageCallback = callback;
    }

    connect(runFunction: any) {
        if (this.ws) {
            this.ws.close();
            this.ws = null;
        }
        if (!this.runFunction && typeof runFunction === 'function') {
            this.runFunction = runFunction;
        }
        // if (ENV_IS_DEVELOPMENT) {
        console.log(this.retryCount, Math.floor(new Date().getTime() / 1000),
            'Trying to connect to "' + this.target + '"');
        // }
        // in Liferay target has full path (socketUrl)
        if (this.target.indexOf('wss://') == 0 || this.target.indexOf('ws://') == 0) {
            this.ws = new WebSocket(this.target);
        } else {
            var protocol = ('https:' === document.location.protocol ? 'wss://' : 'ws://');
            let path = protocol + location.host + this.target;
            if (!this.target.startsWith('/')) {
                path = protocol + location.host + '/' + this.target;
            }
            this.ws = new WebSocket(path);
        }

        this.ws.onopen = this.onOpen.bind(this);
        this.ws.onclose = this.onClose.bind(this);
        this.ws.onmessage = this.onMessage.bind(this);
    }

    connectExternal(runFunction: any, socketUrl: string) {
        if (this.ws) {
            this.ws.close();
            this.ws = null;
        }
        if (!this.runFunction && typeof runFunction === 'function') {
            this.runFunction = runFunction;
        }
        // if (ENV_IS_DEVELOPMENT) {
        console.log(this.retryCount, Math.floor(new Date().getTime() / 1000),
            'Trying to connect to "' + socketUrl + '"');
        // }

        this.ws = new WebSocket(socketUrl);

        this.ws.onopen = this.onOpen.bind(this);
        this.ws.onclose = this.onClose.bind(this);
        this.ws.onmessage = this.onMessage.bind(this);
    }

    public run(command, jsonData, callback = undefined) {
        if (!jsonData) {
            jsonData = {};
        }

        var requestId = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
            var r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
            return v.toString(16);
        });

        // set metadata to json
        jsonData.command = command;

        if (callback) {
            this.runningCommands[requestId] = callback;
        }

        if (this.ws) {
            var stringData = JSON.stringify(jsonData);

            this.ws.send(requestId + ':' + stringData);

            if (this._outcomingMessageCallback) {
                this._outcomingMessageCallback.call(this, stringData);
            }

            // if (ENV_IS_DEVELOPMENT) {
            console.log('Running command: ' + command + ', reqId:' + requestId + ', payload: ', stringData);
            // }

            return requestId;
        } else {
            this.onClose();
            return null;
        }
    }

    close() {
        // todo: move close up yoo remove connector from connectors list.

        if (this.ws) {
            this.doNotReconnect = true;
            this.ws.close();
            this.ws = null;

            window.removeEventListener('beforeunload', this.close.bind(this));
        }
    }

    getSubsystemMetadata(callback) {
        var url = location.pathname + location.search + this.formsManager.getLocationHash();
        var json = {'url': url};
        return this.run(SocketOutputCommands.INIT, json, callback);
    }

    startUseCase(useCaseId, callback, ignored) {
        var json = {'useCaseQualifiedClassName': useCaseId};
        return this.run(SocketOutputCommands.RUN_USE_CASE, json, callback)
    }

    reconnectReset() {
        this.reconnecting = false;
        this.retryCount = 0;
        this.headResponseRetry = 0;
    }

    reconnectTry() {
        let connector = this;

        if (!connector.reconnecting) {
            connector.reconnectStartTime = Date.now();
        }

        if (connector.reconnectStartTime + connector.reconnectDecisionDialogMs < Date.now()) {
            connector.reconnecting = false;
            //TODO Zastanowić się co zrobić z createInfoDialog()
            console.log("connector.applicationLock.createInfoDialog")

            // window["FhContainer"] = FhContainer;
            //
            // connector.applicationLock.createInfoDialog(
            //     this.i18n.__('connection_lost_message'),
            //     this.i18n.__('connection_lost_button1'),
            //     "window.FhContainer.get('Connector').closeDialog();window.FhContainer.get('Connector').onClose()",
            //     null,//this.i18n.__('connection_lost_button2'),
            //     null,//"window.FhContainer.get('Connector').closeDialog()"
            //     false
            // );
            return;
        }

        connector.reconnecting = true;
        connector.retryCount += 1;

        fetch(connector.util.getPath(''), {
            method: 'OPTIONS',
            credentials: "include",
            mode: "cors",
            headers: {
                'Origin': connector.util.getPath(''),
                'Content-Type': 'application/x-www-form-urlencoded'
            }
        }).then(result => {
            if (result.status === 401) { // not authorized, probably http session is lost
                console.log("connector.applicationLock.createInfoDialog")
                // this.applicationLock.createInfoDialog(
                //     this.i18n.__('session_expired_message'),
                //     this.i18n.__('session_expired_button'),
                //     'window.location.reload(true)',
                //     null,
                //     null,
                //     false
                // );
                return;
            }
            connector.serverAlive = result.status === 200;

            connector.connect(undefined);
        }).catch(error => {
            connector.serverAlive = false;

            // if (ENV_IS_DEVELOPMENT) {
            console.log(error);
            // }
            connector.connect(undefined);
        });
    }

    onOpen(event) {
        // if (ENV_IS_DEVELOPMENT) {
        console.log('Open: ' + event.data);
        // }

        var that = this;
        this.pingInterval = function () {
            // Send ping 0x09 every 10 seconds
            if (that.pingInterval) {
                var buf = new ArrayBuffer(1);
                var bufView = new Uint8Array(buf);
                bufView[0] = 0x09;
                that.ws.send(buf);

                setTimeout(that.pingInterval, 10000);
            }
        };
        this.pingInterval();

        if (typeof this.openCallback === 'function') {
            this.openCallback();
        }

        if (!this.reconnecting || !this.formsManager.isInitialized()) {
            this.formsManager.clear();
            this.applicationLock.hideUC();
            this.applicationLock.hideForm();

            this.run(SocketOutputCommands.GET_SESSION_ID, null, this.runFunction);
        }

        this.reconnectReset();
    }

    onClose(event = undefined) {
        // if (ENV_IS_DEVELOPMENT) {
        console.log('%c Connection closed - event ', 'background: #F00; color: #FFF', event);
        // }

        this.ws = null;
        if (this.pingInterval) {
            this.pingInterval = null;
        }
        if (this.reconnecting && this.serverAlive) {
            this.headResponseRetry++;
            if (this.headResponseRetry >= this.maxHeadResponseRetry) {
                console.log("applicationLock.createInfoDialog");
                // this.applicationLock.createInfoDialog(
                //     this.i18n.__('session_expired_message'),
                //     this.i18n.__('session_expired_button'),
                //     'window.location.reload(true)'
                // );
                return;
            }
        }
        if (!this.doNotReconnect) {
            if (!this.reconnecting && typeof this.reconnectCallback === 'function') {
                this.reconnectCallback();
            }

            setTimeout(() => this.reconnectTry(), this.reconnectTimeoutMs);
        }
    }

    closeDialog() {
        // $('.fh-error-dialog').remove();
    }

    onMessage(event) {
        if (this._incomingMessageCallback) {
            this._incomingMessageCallback.call(this, event);
        }

        var i = event.data.indexOf(':');
        var requestId = event.data.slice(0, i);

        let receivedData = event.data.slice(i + 1);
        //Message decompression (if any compression used)
        this.decompressData(receivedData, stringData => {
            var data = JSON.parse(stringData);

            // if (ENV_IS_DEVELOPMENT) {
            console.log('Received:', data);
            // }
            var callback = this.runningCommands[requestId];
            this.runningCommands[requestId] = undefined;
            if (callback != null) {
                //console.log('Command execution finished');
                callback(requestId, data);
            } else {
                this.formsManager.handleEvent(requestId, data);
            }
        });
    }

    /**
     * Decompress gzip data in rawDataString and call calback passing as an argument uncompressed data as string
     * @param rawDataString array of bytes with compressed message or uncompressed message - if messege starts with char code 0 it means it is compressed message
     * @param callback method with one argument for uncompressed data string
     */
    decompressData(rawDataString, callback) {
        if (rawDataString.charAt(0) == '\u0000') {
            let rawCompressedData = new Array(rawDataString.length - 1)
            for (let i = 0; i < rawCompressedData.length; i++) {
                rawCompressedData[i] = rawDataString.charCodeAt(i + 1);
            }

            const decompressionString = Pako.ungzip(new Uint8Array(rawCompressedData), {to: 'string'});
            callback(decompressionString);
        } else {
            callback(rawDataString);
        }

    }

    public isOpen(): boolean {
        return this.ws != null;
    }
}

export {Connector};
