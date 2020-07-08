"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
};
Object.defineProperty(exports, "__esModule", { value: true });
var inversify_1 = require("inversify");
var inversify_inject_decorators_1 = require("inversify-inject-decorators");
var SocketOutputCommands_1 = require("./SocketOutputCommands");
var ApplicationLock_1 = require("./ApplicationLock");
var FormsManager_1 = require("./FormsManager");
var Util_1 = require("../Util");
var Connector_pl_1 = require("../I18n/Connector.pl");
var Connector_en_1 = require("../I18n/Connector.en");
var I18n_1 = require("../I18n/I18n");
var FhContainer_1 = require("../FhContainer");
var lazyInject = inversify_inject_decorators_1.default(FhContainer_1.FhContainer).lazyInject;
var Connector = /** @class */ (function () {
    function Connector(target, reconnectCallback, openCallback) {
        this.runningCommands = {};
        this.ws = null;
        this.reconnecting = false;
        this.doNotReconnect = false;
        this.retryCount = 0;
        this.reconnectTimeoutMs = 3000;
        this.reconnectDecisionDialogMs = 30000;
        this.reconnectStartTime = 0;
        this.serverAlive = true;
        this.headResponseRetry = 0;
        this.maxHeadResponseRetry = 3;
        this.runFunction = null;
        this.pingInterval = null;
        this._incomingMessageCallback = null;
        this._outcomingMessageCallback = null;
        this.target = target;
        this.reconnectCallback = reconnectCallback;
        this.openCallback = openCallback;
        window.addEventListener('beforeunload', this.close.bind(this));
        this.i18n.registerStrings('pl', Connector_pl_1.ConnectorPL, true);
        this.i18n.registerStrings('en', Connector_en_1.ConnectorEN, true);
    }
    Object.defineProperty(Connector.prototype, "incomingMessageCallback", {
        set: function (callback) {
            this._incomingMessageCallback = callback;
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(Connector.prototype, "outcomingMessageCallback", {
        set: function (callback) {
            this._outcomingMessageCallback = callback;
        },
        enumerable: true,
        configurable: true
    });
    Connector.prototype.connect = function (runFunction) {
        if (this.ws) {
            this.ws.close();
            this.ws = null;
        }
        if (!this.runFunction && typeof runFunction === 'function') {
            this.runFunction = runFunction;
        }
        if (ENV_IS_DEVELOPMENT) {
            console.log(this.retryCount, Math.floor(new Date().getTime() / 1000), 'Trying to connect to "' + this.target + '"');
        }
        var protocol = ('https:' === document.location.protocol ? 'wss://' : 'ws://');
        this.ws = new WebSocket(protocol + location.host + this.target);
        this.ws.onopen = this.onOpen.bind(this);
        this.ws.onclose = this.onClose.bind(this);
        this.ws.onmessage = this.onMessage.bind(this);
    };
    Connector.prototype.connectExternal = function (runFunction, socketUrl) {
        if (this.ws) {
            this.ws.close();
            this.ws = null;
        }
        if (!this.runFunction && typeof runFunction === 'function') {
            this.runFunction = runFunction;
        }
        if (ENV_IS_DEVELOPMENT) {
            console.log(this.retryCount, Math.floor(new Date().getTime() / 1000), 'Trying to connect to "' + socketUrl + '"');
        }
        this.ws = new WebSocket(socketUrl);
        this.ws.onopen = this.onOpen.bind(this);
        this.ws.onclose = this.onClose.bind(this);
        this.ws.onmessage = this.onMessage.bind(this);
    };
    Connector.prototype.run = function (command, jsonData, callback) {
        if (callback === void 0) { callback = undefined; }
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
            if (ENV_IS_DEVELOPMENT) {
                console.log('Running command: ' + command + ', reqId:' + requestId + ', payload: ', stringData);
            }
            return requestId;
        }
        else {
            this.onClose();
        }
    };
    Connector.prototype.close = function () {
        // todo: move close up yoo remove connector from connectors list.
        if (this.ws) {
            this.doNotReconnect = true;
            this.ws.close();
            this.ws = null;
            window.removeEventListener('beforeunload', this.close.bind(this));
        }
    };
    Connector.prototype.getSubsystemMetadata = function (callback) {
        var url = location.pathname + location.search + this.formsManager.getLocationHash();
        var json = { 'url': url };
        return this.run(SocketOutputCommands_1.SocketOutputCommands.INIT, json, callback);
    };
    Connector.prototype.startUseCase = function (useCaseId, callback, ignored) {
        var json = { 'useCaseQualifiedClassName': useCaseId };
        return this.run(SocketOutputCommands_1.SocketOutputCommands.RUN_USE_CASE, json, callback);
    };
    Connector.prototype.reconnectReset = function () {
        this.reconnecting = false;
        this.retryCount = 0;
        this.headResponseRetry = 0;
    };
    Connector.prototype.reconnectTry = function () {
        var connector = FhContainer_1.FhContainer.get('Connector');
        if (!connector.reconnecting) {
            connector.reconnectStartTime = Date.now();
        }
        if (connector.reconnectStartTime + connector.reconnectDecisionDialogMs < Date.now()) {
            connector.reconnecting = false;
            window["FhContainer"] = FhContainer_1.FhContainer;
            connector.applicationLock.createInfoDialog(this.i18n.__('connection_lost_message'), this.i18n.__('connection_lost_button1'), "window.FhContainer.get('Connector').closeDialog();window.FhContainer.get('Connector').onClose()", null, //this.i18n.__('connection_lost_button2'),
            null, //"window.FhContainer.get('Connector').closeDialog()"
            false);
            return;
        }
        connector.reconnecting = true;
        connector.retryCount += 1;
        var testRequest = new XMLHttpRequest();
        testRequest.open('OPTIONS', connector.util.getPath(''), true);
        testRequest.onload = function () {
            if (testRequest.readyState === 4) {
                if (testRequest.status === 401) { // not authorized, probably http session is lost
                    this.applicationLock.createInfoDialog(this.i18n.__('session_expired_message'), this.i18n.__('session_expired_button'), 'window.location.reload(true)', null, null, false);
                    return;
                }
                connector.serverAlive = testRequest.status === 200;
                connector.connect(undefined);
            }
        }.bind(this);
        testRequest.onerror = function () {
            connector.serverAlive = false;
            if (ENV_IS_DEVELOPMENT) {
                console.log(testRequest.statusText);
            }
            connector.connect(undefined);
        };
        testRequest.setRequestHeader('X-Requested-With', 'XMLHttpRequest');
        testRequest.send(null);
    };
    Connector.prototype.onOpen = function (event) {
        if (ENV_IS_DEVELOPMENT) {
            console.log('Open: ' + event.data);
        }
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
        if (!this.reconnecting) {
            this.run(SocketOutputCommands_1.SocketOutputCommands.GET_SESSION_ID, null, this.runFunction);
        }
        this.reconnectReset();
    };
    Connector.prototype.onClose = function (event) {
        var _this = this;
        if (event === void 0) { event = undefined; }
        if (ENV_IS_DEVELOPMENT) {
            console.log('%c Connection closed - event ', 'background: #F00; color: #FFF', event);
        }
        this.ws = null;
        if (this.pingInterval) {
            this.pingInterval = null;
        }
        if (this.reconnecting && this.serverAlive) {
            this.headResponseRetry++;
            if (this.headResponseRetry >= this.maxHeadResponseRetry) {
                this.applicationLock.createInfoDialog(this.i18n.__('session_expired_message'), this.i18n.__('session_expired_button'), 'window.location.reload(true)');
                return;
            }
        }
        if (!this.doNotReconnect) {
            if (!this.reconnecting && typeof this.reconnectCallback === 'function') {
                this.reconnectCallback();
            }
            setTimeout(function () { return _this.reconnectTry(); }, this.reconnectTimeoutMs);
        }
    };
    Connector.prototype.closeDialog = function () {
        $('.fh-error-dialog').remove();
    };
    Connector.prototype.onMessage = function (event) {
        if (this._incomingMessageCallback) {
            this._incomingMessageCallback.call(this, event);
        }
        var i = event.data.indexOf(':');
        var requestId = event.data.slice(0, i);
        var stringData = event.data.slice(i + 1);
        var data = JSON.parse(stringData);
        if (ENV_IS_DEVELOPMENT) {
            console.log('Received:', data);
        }
        var callback = this.runningCommands[requestId];
        this.runningCommands[requestId] = undefined;
        if (callback != null) {
            //console.log('Command execution finished');
            callback(requestId, data);
        }
        else {
            this.formsManager.handleEvent(requestId, data);
        }
    };
    Connector.prototype.isOpen = function () {
        return this.ws != null;
    };
    __decorate([
        lazyInject('ApplicationLock'),
        __metadata("design:type", ApplicationLock_1.ApplicationLock)
    ], Connector.prototype, "applicationLock", void 0);
    __decorate([
        lazyInject('FormsManager'),
        __metadata("design:type", FormsManager_1.FormsManager)
    ], Connector.prototype, "formsManager", void 0);
    __decorate([
        lazyInject('Util'),
        __metadata("design:type", Util_1.Util)
    ], Connector.prototype, "util", void 0);
    __decorate([
        lazyInject('I18n'),
        __metadata("design:type", I18n_1.I18n)
    ], Connector.prototype, "i18n", void 0);
    Connector = __decorate([
        inversify_1.injectable(),
        __metadata("design:paramtypes", [String, Function, Function])
    ], Connector);
    return Connector;
}());
exports.Connector = Connector;
//# sourceMappingURL=Connector.js.map