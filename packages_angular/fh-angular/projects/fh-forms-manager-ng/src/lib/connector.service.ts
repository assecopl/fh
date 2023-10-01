import {Injectable} from '@angular/core';
import {SocketOutputCommands} from '../enum/SocketOutputCommands';
import * as Pako from "pako";

export class ConnectorService {

    private target: string;
    private reconnectCallback: any;
    private openCallback: any;

    private runningCommands: { [key: string]: any; } = {};
    private ws: WebSocket | any;
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
    private pingInterval: () => void = () => {
    };
    private _incomingMessageCallback: (data: string) => void = (data) => {
    };
    private _outcomingMessageCallback: (data: string) => void = (data) => {
    };

    private totalLengthOfAllCompressedMessages: number = 0;
    private totalLengthOfAllUncompressedMessages: number = 0;

    constructor(target?: any,
                reconnectCallback?: () => void,
                openCallback?: () => void) {
        this.target = target;
        this.reconnectCallback = reconnectCallback;
        this.openCallback = openCallback;

        window.addEventListener('beforeunload', this.close.bind(this));

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

        this.ws = new WebSocket(socketUrl);

        this.ws.onopen = this.onOpen.bind(this);
        this.ws.onclose = this.onClose.bind(this);
        this.ws.onmessage = this.onMessage.bind(this);
    }

    run(command: any, jsonData: any, callback = undefined): any {
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

            return requestId;
        } else {
            this.onClose();
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

    reconnectReset() {
        this.reconnecting = false;
        this.retryCount = 0;
        this.headResponseRetry = 0;
    }

    reconnectTry() {
        console.log("Reconnect try")
    }

    onOpen(event: Event) {


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

        // if (!this.reconnecting || !this.formsManager.isInitialized() ) {
        if (!this.reconnecting) {
            this.run(SocketOutputCommands.GET_SESSION_ID, null, this.runFunction);
        }

        this.reconnectReset();
    }

    onClose(event = undefined) {


        this.ws = null;
        if (this.pingInterval) {
            this.pingInterval = () => {
            };
        }
        if (this.reconnecting && this.serverAlive) {
            this.headResponseRetry++;
            if (this.headResponseRetry >= this.maxHeadResponseRetry) {

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

    onMessage(event: any) {
        if (this._incomingMessageCallback) {
            this._incomingMessageCallback.call(this, event);
        }

        var i = event.data.indexOf(':');
        var requestId = event.data.slice(0, i);

        let receivedData = event.data.slice(i + 1);
        //Message decompression (if any compression used)
        this.decompressData(receivedData, (stringData: string) => {
            var data = JSON.parse(stringData);


            var callback = this.runningCommands[requestId];
            this.runningCommands[requestId] = undefined;
            if (callback != null) {
                //console.log('Command execution finished');
                callback(requestId, data);
            } else {
                // this.formsManager.handleEvent(requestId, data);
            }
        });
    }

    /**
     * Decompress gzip data in rawDataString and call calback passing as an argument uncompressed data as string
     * @param rawDataString array of bytes with compressed message or uncompressed message - if messege starts with char code 0 it means it is compressed message
     * @param callback method with one argument for uncompressed data string
     */
    decompressData(rawDataString: string, callback: (data: string) => void) {
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

    getSubsystemMetadata(callback: any) {
        var url = location.pathname + location.search + this.getLocationHash();
        var json = {'url': url};
        return this.run(SocketOutputCommands.INIT, json, callback);
    }

    public getLocationHash() {
        if (location.hash == '#') { // IE workaround
            return '';
        } else {
            return location.hash;
        }
    }


}
